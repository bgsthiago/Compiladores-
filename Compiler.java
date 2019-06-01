/*Bruno Veiga - 743514
Lucas Costa - 743563
Luiz Felipe Guimarães - 743570
Thiago Borges - 613770*/

import AST.*;
import java.util.*;
import Lexer.*;
import java.lang.Character;
import java.io.*;

public class Compiler {

	private Hashtable<String, Variable> symbolTable;
	private Lexer lexer;
	private CompilerError error;

    public Program compile( char []input) {
        symbolTable = new Hashtable<String, Variable>();

        error = new CompilerError();
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
            arrayFunction.add(func());
        }

        if ( lexer.token != Symbol.EOF )
        	error.signal("EOF expected");

        Program program = new Program(arrayFunction);
        return program;
    }

    private Function func() {
    	//Func ::= "function" Id [ "(" ParamList ")" ] ["->" Type ] StatList
    	Boolean isIdent = true;
    	String id = "";
    	Type type = null;
        Function f = null;

    	for (Symbol c : Symbol.values()) {
            if (c.name().equals(lexer.token)) {
                isIdent = false;
            }
        }

    	if(isIdent) {
    		id = lexer.getStringValue();
            f = new Function(id);
    		lexer.nextToken();


            if(lexer.token == Symbol.LEFTPAR) {
    			lexer.nextToken();
    			f.setParamList(paramList());

    			if(lexer.token == Symbol.RIGHTPAR) {
    				lexer.nextToken();
    			} else {
    				error.signal("right parenthesis missing");
    			}
    		} 
    	} else {
            error.signal("Identifier expected");
        }

    	if(lexer.token == Symbol.ARROW) {
    		lexer.nextToken();
            type = type();
            f.setReturnType(type);
    	}

        f.setStatList(statList());

    	return f;

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
            case INTEGER :
                result = new IntegerType();
                break;
            case BOOLEAN :
                result = new BooleanType();
                break;
            case STRING :
                result = new StringType();
                break;
            default:
                error.signal("Type expected");
                result = null;
        }

        lexer.nextToken();
        return result;
    }

    private StatementList statList() {
        // StatList ::= "{" {Stat} "}"

        Symbol tkn;
        ArrayList<Statement> v = new ArrayList<Statement>();

        if (lexer.token != Symbol.OPENBRACE) {
            error.signal("{ expected");
        } else {
            lexer.nextToken();
        }

        int rolaCount = 1;
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
            case IDENT :
                return assignExprStat();
            case TRUE:
                return assignExprStat();
            case FALSE:
                return assignExprStat();
            case LITERALINT:
                return assignExprStat();
            case LITERALSTRING:
                return assignExprStat();
            case RETURN :
                return returnStat();
            case VAR :
                return varDecStat();
            case IF :
                return IfStat();
            case WHILE:
                return whileStat();
            default :
                error.signal("Statement expected while");
        }
        return null;
    }

    private AssignExprStatement assignExprStat() {
        // AssignExprStat ::= Expr [ "=" Expr] ";"
        Expr left = expr();
        Expr right = null;

        if (lexer.token == Symbol.ATRIB) {
            lexer.nextToken();
            right = expr();
        }

        if (lexer.token != Symbol.SEMICOLON) {
            error.signal("; expected");
        }
        lexer.nextToken();


        // #Sera implementado na segunda fase (Analisador Semantico)

        return new AssignExprStatement(left, right);
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
        Variable v = null;
        String id = null;
        Type type = null;

        lexer.nextToken();
        if (lexer.token != Symbol.IDENT) {
            error.signal("Identifier expected");
        } else {
            id = lexer.getStringValue();
            lexer.nextToken();
        }

        if (lexer.token != Symbol.COLON) {
            error.signal(": expected");
        } else {
            lexer.nextToken();
        }

        v = new Variable(id);
        type = type();
        v.setType(type);

        if (lexer.token != Symbol.SEMICOLON) {
            error.signal("; expected");
        }
        lexer.nextToken();

        return new VarDecStat(v);
    }

    private WhileStatement whileStat() {
        // WhileStat ::= "while" Expr StatList
        lexer.nextToken();

        Expr e = expr();
        StatementList stmt = statList();

        return new WhileStatement(e, stmt);
    }

    private ReturnStatement returnStat() {
        //ReturnStat ::= "return" Expr ";"
        lexer.nextToken();
        Expr e = expr();

        if (lexer.token != Symbol.SEMICOLON) {
            error.signal("; expected");
        }
        lexer.nextToken();

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

    private Expr exprAnd() {
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

    private Expr exprRel() {
        // ExprRel ::= ExprAdd [ RelOp ExprAdd ]
        Expr left, right;
        left = exprAdd();
        Symbol op = lexer.token;

        if (op == Symbol.LT || op == Symbol.LE || op == Symbol.GT || op == Symbol.GE || op == Symbol.NEQ || op == Symbol.EQ) {
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

        while (lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS) {
            op = lexer.token;
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

        while (lexer.token == Symbol.MULT || lexer.token == Symbol.DIV) {
            op = lexer.token;
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
            case LITERALINT:
                return exprLiteralInt();
            case LITERALSTRING:
                return exprLiteralString();
            case TRUE:
                return exprLiteralBoolean();
            case FALSE:
                return exprLiteralBoolean();
            case IDENT: // Sera uma variavel simples ou uma chamada de funcao
                return exprId();

                
            default:
                error.signal("Statement expected se pa");
                return null;
        }
    }
    
    private ExprLiteralInt exprLiteralInt() {
        if (lexer.token != Symbol.LITERALINT) {
            error.signal("Int expected");
        }

        int value = lexer.getNumberValue();
        lexer.nextToken();

        return new ExprLiteralInt(value);
    }

    private ExprLiteralString exprLiteralString() {
        if (lexer.token != Symbol.LITERALSTRING) {
            error.signal("String expected");
        }

        String value = lexer.getStringValue();
        lexer.nextToken();

        return new ExprLiteralString(value);
    }

    private ExprLiteralBoolean exprLiteralBoolean() {
        if (lexer.token != Symbol.FALSE && lexer.token != Symbol.TRUE) {
            error.signal("Boolean expected");
        }

        boolean value = false; // #IMPLEMENTAR LEXER
        lexer.nextToken();

        return new ExprLiteralBoolean(value);
    }

    private FuncCall funcCall() {
        // FuncCall ::= Id "(" [ Expr {”, ”Expr} ] ")"
        ArrayList<Expr> exprList = new ArrayList<Expr>();
        Expr e = null;
        String name = lexer.getStringValue();

        if (lexer.token != Symbol.LEFTPAR) {
            error.signal("( expected"); 
        }
        lexer.nextToken();

        if (lexer.token != Symbol.RIGHTPAR) {
            // # Implementar analise semantica
            
            // processa todas expressoes
            while (true) {
                e = expr();
                exprList.add(e);

                if (lexer.token == Symbol.COMMA) {
                    lexer.nextToken();
                } else {
                    break;
                }
            }

            if (lexer.token != Symbol.RIGHTPAR) {
                error.signal(") expected");
            }
            lexer.nextToken();

        }

        return new FuncCall(name, exprList);
    }

    private Expr exprId() {
        String name = lexer.getStringValue();
        
        lexer.nextToken();

        if (lexer.token == Symbol.LEFTPAR) {
            return funcCall();
        }

        return new ExprIdentifier(name);
    }
    
}
