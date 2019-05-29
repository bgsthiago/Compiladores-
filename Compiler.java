/*
comp8

Variables now can have any number of characters and numbers any number
of digits. There are new keywords and new non-terminals. The operator
set includes the comparison operators. There are a few statements.
Anything after // till the end of the line is a comment.
Note that VarDecList was modified.

The input is now taken from a file.

Method error now prints the line in which the error occurred.

Grammar:

Program ::= [ "var" VarDecList ";" ] CompositeStatement
CompositeStatement ::= "begin" StatementList "end"
StatementList ::= | Statement ";" StatementList
Statement ::= AssignmentStatement | IfStatement | ReadStatement |
WriteStatement
AssignmentStatement ::= Variable "=" Expr
IfStatement ::= "if" Expr "then" StatementList [ "else" StatementList ] "endif"
ReadStatement ::= "read" "(" Variable ")"
WriteStatement ::= "write" "(" Expr ")"
Variable ::= Letter { Letter }
VarDecList ::= Variable | Variable "," VarDecList
Expr::= ’(’ oper Expr Expr ’)’ | Number | Variable
Oper ::= ’+’ | ’-’ | ’*’ | ’/’ | ’<’ | ’<=’ | ’>’ | ’>=’| ’==’ | ’<>’
Number ::= Digit { Digit }
Digit ::= ’0’| ’1’ | ... | ’9’
Letter ::= ’A’ | ’B’| ... | ’Z’| ’a’| ’b’ | ... | ’z’/*

Anything between [] is optional. Anything between { e } can be
repeated zero or more times.
*/

import AST.*;
import java.util.*;
import Lexer.*;
import java.lang.Character;
import java.io.*;

public class Compiler {
	
	private Hashtable<String, Variable> symbolTable; 
	private Lexer lexer; 
	private CompilerError error;

    // compile must receive an input with an character less than
    // p_input.lenght
    
    public Program compile( char []input, PrintWriter outError ) {
        
        symbolTable = new Hashtable<String, Variable>(); 
        
        error = new CompilerError( outError ); 
        lexer = new Lexer(input, error); 
        error.setLexer(lexer);
        lexer.nextToken(); 
        return program();
    }

    private Program program() {
    	
        // Program ::= Func {Func}
    	ArrayList<Function> arrayFunction = new ArrayList<Function>();

        while ( lexer.token == Symbol.FUNCTION ) {
            lexer.nextToken();
            arrayFunction.add(func())
        }

        if ( lexer.token != Symbol.EOF )
        	error.signal("EOF expected");

        Program program = new Program(arrayFunction); 
        return program; //ta errado
    }
    
    private Function func() {
    	//Func ::= "function" Id [ "(" ParamList ")" ] ["->" Type ] StatList 
    	
    	Boolean isIdent = true;
    	String id = "";
    	Type type = null;
    	
    	//
    	for (Symbol c : Symbol.values()) {
            if (c.name().equals(lexer.token)) {
                isIdent = false;
            }
        }

    	if(isIdent) {
    		id = lexer.token.getStringValue();
            Function func = new Function(id);
    		lexer.nextToken();

    		
            if(lexer.token == Symbol.LEFTPAR) {
    			lexer.nextToken();
    			func.setparamList(paramList());

    			if(lexer.token == Symbol.RIGHTPAR) {
    				lexer.nextToken();
    			} else {
    				error.signal("right parenthesis missing");
    			}
    		} else {
                error.signal("left parenthesis missing")
            }
    	} else {
            error.signal("Identifier expected")
        }

    	if(lexer.token == Symbol.ARROW) {
    		lexer.nextToken();
            type = type();
            func.setReturnType(type); 	
    	}

        // Check and consume '{'
        //lexer.nextToken();
        /*if (lexer.token != Symbol.OPENBRACE) {
            error.signal("{ expected")
        } else {
            lexer.nextToken();
        }*/
        func.setStatList(statList()); 

    	return func;
    	
    }

    private ParamList paramList() {
        // ParamList ::= ParamDec {”, ”ParamDec}
        
        ParamList paramlist = null;

        if (lexer.token == Symbol.IDENT) {
            paramlist = new ParamList();
            paramDec(paramlist);

            while (lexer.token == Symbol.COMMA) {
                lexer.nextToken();
                paramDec(paramlist);
            }
        } else {
            error.signal("identifier expected");
        }

        return paramlist;   
    }

    private void paramDec(ParamList paramList) {
        // ParamDec ::= Id ":" Type

        if (lexer.token != Symbol.IDENT) {
            error.signal("Identifier expected");
        }

        String name = (String) lexer.getStringValue();
        lexer.nextToken();

        if (lexer.token != Symbol.COLON) {
            error.signal(": expected");
        } else {
            lexer.nextToken();
        }

        Parameter param = new Parameter(name);
        param.setType(type());
        paramList.addElement(param);
    }

    private Type type() {
        Type result;

        switch(lexer.token) {
            case Symbol.INTEGER :
                result = Type.IntegerType;
                break;
            case Symbol.BOOLEAN :
                result = Type.BooleanType;
                break;
            case Symbol.STRING :
                result = Type.CharType;
                break;
            default:
                error.signal("Type expected");
                result = Type.IntegerType;
        }

        lexer.nextToken();
        return result;
    }

    private StatementList statList() {
        // StatList ::= "{" {Stat} "}"
        
        Symbol tkn;
        ArrayList<Statement> v = new ArrayList<Statement>();

        if (lexer.token != Symbol.OPENBRACE) {
            error.signal("{ expected")
        } else {
            lexer.nextToken();
        }

        while ((tkn = lexer.token) != Symbol.CLOSEBRACE && tkn != Symbol.EOF) {
            v.add(stat());
        }

        if (tkn != Symbol.CLOSEBRACE) {
            error.signal("} expected");
        } else {
            lexer.nextToken();
        }

        return new StatementList(v);
    }

    private Statement stat() {
        // Stat ::= AssignExprStat| ReturnStat | VarDecStat | IfStat | WhileStat

        switch (lexer.token) {
            case Symbol.IDENT :
                return assignExprStat();
                break;
            case Symbol.TRUE:
                return assignExprStat();
                break;
            case Symbol.FALSE:
                return assignExprStat();
                break;
            case Symbol.LITERALINT:
                return assignExprStat();
                break;
            case Symbol.LITERALSTRING:
                return assignExprStat();
                break;
            case Symbol.RETURN :
                return returnStat();
                break;
            case Symbol.VAR :
                return varDecStat();
                break;
            case Symbol.IF :
                return IfStat();
                break;
            case Symbol.WHILE:
                return whileStat();
                break;
            default :
                // will never be executed
                error.signal("Statement expected");
        }
        return null;
    }

    private AssignmentStatement assignExprStat() {
        // AssignExprStat ::= Expr [ "=" Expr] ";"

        Expr left = expr();
        Expr right = null;

        if (lexer.token == Symbol.ATRIB) {
            lexer.nextToken()
            right = expr();
        }

        if (lexer.token != Symbol.SEMICOLON) {
            error.signal("; expected");
        }

    
        // #Sera implementado na segunda fase (Analisador Semantico)
        // is the variable in the symbol table ? Variables are inserted in the
        // symbol table when they are declared. It the variable is not there, it has
        // not been declared.
        // # Implementar analise semantica

        return new AssignExprStat(left, right);
    }


    private IfStatement IfStat() {
        // IfStat ::= "if" Expr StatList

        lexer.nextToken();
        Expr e = expr();
        
        StatementList thenPart = statList();
        
        StatementList elsePart = null;
        if (lexer.token == Symbol.ELSE) {
            lexer.nextToken();
            elsePart = statList();
        }

        return new IfStatement( e, thenPart, elsePart );
    }

    private VarDecStat varDecStat() {
        // VarDecStat ::= "var" Id ":" Type ";"
        Variable v = new Variable();
        String id;
        Type type;

        lexer.nextToken();
        if (lexer.token != Symbol.IDENT) {
            error.signal("Identifier expected");
        } else {
            id = lexer.stringValue();
            lexer.nextToken();
        }
        
        if (lexer.token != Symbol.COLON) {
            error.signal(": expected")
        } else {
            lexer.nextToken();
        }

        type = type();

        return new VarDecStat(v);
    }

    private Expr expr() {
        if ( token == Symbol.LEFTPAR ) {
            nextToken();
            Symbol op = token;
            if ( op == Symbol.EQ || op == Symbol.NEQ || op == Symbol.LE || op == Symbol.LT ||
            op == Symbol.GE || op == Symbol.GT || op == Symbol.PLUS ||
            op == Symbol.MINUS || op == Symbol.MULT || op == Symbol.DIV ){
                nextToken();
            }else{
                error("operator expected");
                Expr e1 = expr();
                Expr e2 = expr();
                CompositeExpr ce = new CompositeExpr(e1, op, e2);
                if ( token == Symbol.RIGHTPAR )
                    nextToken();
                else
                    error(") expected");
                return ce;

            }
        }else{

                // note we test the token to decide which production to use
            if ( token == Symbol.NUMBER )
                return number();
            else {
                if ( token != Symbol.IDENT )
                    error("Identifier expected");
            String name = stringValue;
            nextToken();
            Variable v = (Variable ) symbolTable.get( name );
                // semantic analysis
                // was the variable declared ?
            if ( v == null )
                error("Variable " + name + " was not declared");
            return new VariableExpr(v);
            }

        }
    }

    private NumberExpr number() {
        NumberExpr e = null;
        if ( token != Symbol.NUMBER )
            error("Number expected"); // in the current version, never occurs
        // the number value is stored in token.value as an object of Integer.
        // Method intValue returns that value as an value of type int.
        int value = numberValue;
        nextToken();
        return new NumberExpr(value);
    }

    private void nextToken() {
        
        char ch;
        while ((ch = input[tokenPos]) == ’ ’ || ch == ’\r’ ||ch == ’\t’ || ch == ’\n’) {
        // count the number of lines
            if ( ch == ’\n’)
                lineNumber++;
            tokenPos++;
        }

        if ( ch == ’\0’){
            token = Symbol.EOF;
        }else{
            // skip comments
            if ( input[tokenPos] == ’/’ && input[tokenPos + 1] == ’/’ ) {
            // comment found
                while ( input[tokenPos] != ’\0’&& input[tokenPos] != ’\n’ )
                    tokenPos++;
            nextToken();
            }else {

                if ( Character.isLetter( ch ) ) {
                    
                    // get an identifier or keyword
                    // StringBuffer represents a string that can grow
                    StringBuffer ident = new StringBuffer();
                    
                    // is input[tokenPos] a letter ?
                    // isLetter is a static method of class Character
                    while ( Character.isLetter( input[tokenPos] ) ) {
                    
                        // add a character to ident
                        ident.append(input[tokenPos]);
                        tokenPos++;
                    }
                    // convert a StringBuffer object into a String object
                    stringValue = ident.toString();
                    // if identStr is in the list of keywords, it is a keyword !
                    Symbol value = keywordsTable.get(stringValue);
                    if(value == null){
                        token = Symbol.IDENT;
                    }else{
                        token = value;
                    }
                    if ( Character.isDigit(input[tokenPos]) )
                        error("Word followed by a number");
                }else if(Character.isDigit(ch)){
                    
                    // get a number
                    StringBuffer number = new StringBuffer();
                    
                    while ( Character.isDigit( input[tokenPos] ) ) {
                        number.append(input[tokenPos]);
                        tokenPos++;
                    }
                    
                    token = Symbol.NUMBER;
                    try {
                        /*
                        number.toString() converts a StringBuffer
                        into a String object.
                        valueOf converts a String object into an
                        Integer object. intValue gets the int
                        inside the Integer object.
                        */
                        numberValue = Integer.valueOf(number.toString()).intValue();
                    } catch ( NumberFormatException e ) {
                        error("Number out of limits");
                    }
                    if ( numberValue >= MaxValueInteger )
                        error("Number out of limits");
                    if ( Character.isLetter(input[tokenPos]) )
                        error("Number followed by a letter");
                }else{

                    tokenPos++;
                    switch ( ch ) {
                        case ’+’ :
                            token = Symbol.PLUS;
                            break;
                        case ’-’ :
                            token = Symbol.MINUS;
                            break;
                        case ’*’ :
                            token = Symbol.MULT;
                            break;
                        case ’/’ :
                            token = Symbol.DIV;
                            break;
                    
                        case ’<’ :
                            if ( input[tokenPos] == '=' ) {
                                tokenPos++;
                                token = Symbol.LE;
                                } else if ( input[tokenPos] == '>' ) {
                                    tokenPos++;
                                    token = Symbol.NEQ;
                                }else{
                                    token = Symbol.LT;
                                }
                            break;

                        case ’>’ :
                            if ( input[tokenPos] == '=' ) {
                                tokenPos++;
                                token = Symbol.GE;
                            }
                            else
                                token = Symbol.GT;
                            break;
                        
                        case ’=’ :
                            if ( input[tokenPos] == '=' ) {
                                tokenPos++;
                                token = Symbol.EQ;
                            }else{
                                token = Symbol.ASSIGN;
                            }
                            break;
                        
                        case '(' :
                            token = Symbol.LEFTPAR;
                        break;
                       
                        case ')' :
                            token = Symbol.RIGHTPAR;
                        break;
                        
                        case ',' :
                            token = Symbol.COMMA;
                        break;
                        
                        case ';' :
                            token = Symbol.SEMICOLON;
                        break;
                        
                        default :
                        error("Invalid Character: ’" + ch + "’");


                    }                  
                
                }
            }
        }
    }

    private void error( String strMessage ) {
        if ( tokenPos == 0 )
            tokenPos = 1;
        else
            if ( tokenPos >= input.length )
                tokenPos = input.length;
        
        StringBuffer line = new StringBuffer();
        // go to the beginning of the line
        int i = tokenPos;
        while ( i >= 1 && input[i] != '\n' )
            i--;
        if ( input[i] == ’\n’ )
            i++;
            // go to the end of the line putting it in variable line
        
        while ( input[i] != '\0' && input[i] != '\n' && input[i] != '\r' ) {
            line.append( input[i] );
            i++;
        }

        System.out.println("Error at line " + lineNumber + ": ");
        System.out.println(line);
        System.out.println( strMessage );
        throw new RuntimeException(strMessage);
    }
}


        
    














