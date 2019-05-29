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

    private AssignExprStatement assignExprStat() {
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

    private WhileStatement whileStat() {
        // WhileStat ::= "while" Expr StatList
        lexer.nextToken();

        Expr e = expr();
        Statement stmt = statement();

        return new WhileStatement(expr, stmt);
    }

    private ReturnStatement returnStat() {
        lexer.nextToken();
        Expr e = expr();

        // # Implementar analise semantica (verificar se esta dentro de funcao)
        return new ReturnStatement(e);
    }

    private Expr expr() {
        // Expr ::= ExprAnd {”or”ExprAnd}
        Expr left, right;
        left = exprAnd();

        if (lexer.token == Symbol.OR) {
            lexer.nextToken();
            right = exprAnd();
            left = new CompositeExpr(left, Symbol.OR, right);
        }

        return left;
    }

    private Expr andExpr() {
        // ExprAnd ::= ExprRel {”and”ExprRel}
        Expr left, right;
        left = exprRel();

        if (lexer.token == Symbol.AND) {
            lexer.nextToken();
            right = exprRel();
            left = new CompositeExpr(left, Symbol.AND, right);
        }

        return left;   
    }

    private Expr relExpr() {
        // ExprRel ::= ExprAdd [ RelOp ExprAdd ]
        Expr left, right;
        left = exprAdd();
        Symbol op = lexer.token;

        if (op == Symbol.LT || op == Symbol.LT || op == Symbol.GT || op == Symbol.GE || op == Symbol.NEQ || op == Symbol.EQ) {
            lexer.nextToken();
            right = exprAdd();
            left = new CompositeExpr(left, op, right);
        }
        
        return left;
    }

    private Expr exprAdd() {
        // ExprAdd ::= ExprMult {(” + ” | ” − ”)ExprMult}
        Expr left, right;
        left = exprMult();
        Symbol op = lexer.token;

        while (op == Symbol.PLUS || op == Symbol.MINUS) {
            lexer.nextToken();
            right = exprMult();
            left = new CompositeExpr(left, op, right);
        }

        return left;
    }

    private Expr exprMult() {
        // ExprMult ::= ExprUnary {(” ∗ ” | ”/”)ExprUnary}
        Expr left, right;
        left = exprUnary();
        Symbol op = lexer.token;

        while (op == Symbol.MULT || op == Symbol.DIV) {
            lexer.nextToken();
            right = exprUnary();
            left = new CompositeExpr(left, op, right);
        }
        
        return left;
    }

    private Expr exprUnary() {
        // ExprUnary ::= [ ( "+" | "-" ) ] ExprPrimary
        Symbol op = null;

        if (lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS) {
            op = lexer.token;
            lexer.nextToken();
        }

        Expr e = exprPrimary();

        return new ExprUnary(op, e);
    }

    private Expr exprPrimary() {
        // ExprPrimary ::= Id | FuncCall | ExprLiteral
        Expr e;

        switch (lexer.token) {
            case Symbol.LITERALINT:
                return exprLiteralInt();
                break;
            case Symbol.LITERALSTRING:
                return exprLiteralString();
                break;
            case Symbol.TRUE:
                return exprLiteralBoolean();
                break;
            case Symbol.FALSE:
                return exprLiteralBoolean();
                break;
            case Symbol.IDENT: // Sera uma variavel simples ou uma chamada de funcao
                lexer.nextToken();
                
                if (lexer.token == Symbol.LEFTPAR) {
                    return funcCall();
                } else {
                    return idExpr();
                }

                break;
            default:
                error.signal("Statement expected");

        }

        private ExprLiteralInt exprLiteralInt() {
            
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

    
}


        
    














