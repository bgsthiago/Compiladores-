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
    			nextToken();
    			func.setparamList();

    			if(lexer.token == Symbol.RIGHTPAR) {
    				nextToken();
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
        lexer.nextToken();
        if (lexer.token != OPENBRACE) {
            error.signal("{ expected")
        } else {
            lexer.nextToken();
        }

        func.setCompositeStatement(compositeStatement());

    	return func;
    	
    }

    private StatementList compositeStatement() {
        // CompositeStatement ::= "begin" StatementList "end"
        // StatementList ::= | Statement ";" StatementList
        if ( token != Symbol.BEGIN ){
            error("BEGIN expected");
            nextToken();
        }
        StatementList sl = statementList();
    
        if ( token != Symbol.END ){
            error("\"end\" expected");
        }
        nextToken();
        return sl;
    }

    private StatementList statementList() {
        ArrayList<Statement> v = new ArrayList<Statement>();
        // statements always begin with an identifier, if, read or write
        while ( token == Symbol.IDENT ||token == Symbol.IF || token == Symbol.READ ||token == Symbol.WRITE ) {
            v.add( statement() );
            if ( token != Symbol.SEMICOLON )
            error("; expected");
        }
        nextToken();
        
    return new StatementList(v);
    }

    private Statement statement() {
        /* Statement ::= AssignmentStatement | IfStatement | ReadStatement |
        WriteStatement
        */
        switch (token) {
            case IDENT :
                return assignmentStatement();
            case IF :
                return ifStatement();
            case READ :
                return readStatement();
            case WRITE :
                 return writeStatement();
            default :
                // will never be executed
                error("Statement expected");
        }
        return null;
    }

    private AssignmentStatement assignmentStatement() {
    
        // the current token is Symbol.IDENT and stringValue
        // contains the identifier
        String name = stringValue;
    
        // is the variable in the symbol table ? Variables are inserted in the
        // symbol table when they are declared. It the variable is not there, it has
        // not been declared.
        Variable v = (Variable ) symbolTable.get(name);
    
        // was it in the symbol table ?
        if ( v == null ){
            error("Variable " + name + " was not declared");
            // eat token Symbol.IDENT
        }
        nextToken();
        if ( token != Symbol.ASSIGN ){
            error("= expected");
        }
        nextToken();
        return new AssignmentStatement( v, expr() );
    }


    private IfStatement ifStatement() {
        nextToken();
        Expr e = expr();
        if ( token != Symbol.THEN ){
           error("then expected");
        }
        nextToken();

        StatementList thenPart = statementList();
        StatementList elsePart = null;
        
        if ( token == Symbol.ELSE ) {
            nextToken();
            elsePart = statementList();
        }

        if ( token != Symbol.ENDIF ){
            error("\"endif\" expected");
        }
        nextToken();
        return new IfStatement( e, thenPart, elsePart );
    }

    private ReadStatement readStatement() {
        nextToken();
        if ( token != Symbol.LEFTPAR )
            error("( expected");
        nextToken();
        
        if ( token != Symbol.IDENT ){
            error("Identifier expected");
            // check if the variable was declared
        }

        String name = stringValue;
        Variable v = (Variable ) symbolTable.get(name);
        
        if ( v == null ){
            error("Variable " + name + " was not declared");
        }
        nextToken();
        if ( token != Symbol.RIGHTPAR ){
            error(") expected");
        }

        nextToken();
        return new ReadStatement( v );
    }

    private WriteStatement writeStatement() {
    
        nextToken();
    
        if ( token != Symbol.LEFTPAR ){
            error("( expected");
        }
        nextToken();
        
        Expr e = expr();
        
        if ( token != Symbol.RIGHTPAR ){
            error(") expected");
        }
        nextToken();
        return new WriteStatement( e );
    }

    private ArrayList<Variable> varDecList() {
        //VarDecList ::= Variable | Variable "," VarDecList ";"
        ArrayList<Variable> v = new ArrayList<Variable>();
        Variable variable = varDec();
       
        v.add(variable);
       
        while ( token == Symbol.COMMA ) {
         nextToken();
          v.add( varDec() );
        }
        return v;
    }

    private Variable varDec() {
        
        Variable v;
        
        if ( token != Symbol.IDENT ){
            error("Identifier expected");
            // name of the identifier
        }

        String name = stringValue;
        nextToken();
            // semantic analysis
            // if the name is in the symbol table, the variable has been declared twice.
        
        if ( symbolTable.get(name) != null ){
            error("Variable " + name + " has already been declared");
            // inserts the variable in the symbol table. The name is the key and an
            // object of class Variable is the value. Hash tables store a pair (key, value)
            // retrieved by the key.
        }

        symbolTable.put( name, v = new Variable(name) );
        return v;
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


        
    














