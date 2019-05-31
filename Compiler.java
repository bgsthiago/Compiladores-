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
            arrayFunction.add(func());
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
    		id = lexer.getStringValue();
            Function f = new Function(id);
    		lexer.nextToken();

    		
            if(lexer.token == Symbol.LEFTPAR) {
    			lexer.nextToken();
    			f.setparamList(paramList());

    			if(lexer.token == Symbol.RIGHTPAR) {
    				lexer.nextToken();
    			} else {
    				error.signal("right parenthesis missing");
    			}
    		} else {
                error.signal("left parenthesis missing");
            }
    	} else {
            error.signal("Identifier expected");
        }

    	if(lexer.token == Symbol.ARROW) {
    		lexer.nextToken();
            type = type();
            f.setReturnType(type); 	
    	}

        // Check and consume '{'
        //lexer.nextToken();
        /*if (lexer.token != Symbol.OPENBRACE) {
            error.signal("{ expected")
        } else {
            lexer.nextToken();
        }*/
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
                result = Type.IntegerType;
                break;
            case BOOLEAN :
                result = Type.BooleanType;
                break;
            case STRING :
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
            error.signal("{ expected");
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
            case DENT :
                return assignExprStat();
                break;
            case TRUE:
                return assignExprStat();
                break;
            case FALSE:
                return assignExprStat();
                break;
            case LITERALINT:
                return assignExprStat();
                break;
            case LITERALSTRING:
                return assignExprStat();
                break;
            case RETURN :
                return returnStat();
                break;
            case VAR :
                return varDecStat();
                break;
            case IF :
                return IfStat();
                break;
            case WHILE:
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
            lexer.nextToken();
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
            id = lexer.getStringValue();
            lexer.nextToken();
        }
        
        if (lexer.token != Symbol.COLON) {
            error.signal(": expected");
        } else {
            lexer.nextToken();
        }

        type = type();

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
        Statement stmt = statement();

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
            case LITERALINT:
                return exprLiteralInt();
                break;
            case LITERALSTRING:
                return exprLiteralString();
                break;
            case TRUE:
                return exprLiteralBoolean();
                break;
            case FALSE:
                return exprLiteralBoolean();
                break;
            case IDENT: // Sera uma variavel simples ou uma chamada de funcao
                lexer.nextToken();
                
                if (lexer.token == Symbol.LEFTPAR) {
                    return funcCall();
                } else {
                    return exprId();
                }

                break;
            default:
                error.signal("Statement expected");

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

        boolean value = lexer.getBooleanValue();
        lexer.nextToken();

        return new ExprLiteralBoolean(value);
    }

    private FunctionCall funcCall() {
        // FuncCall ::= Id "(" [ Expr {”, ”Expr} ] ")"
        ArrayList<Expr> exprList = new ArrayList<Expr>();
        Expr e = null;
        String name = lexer.getStringValue();

        lexer.nextToken();

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

        return new FunctionCall(name, exprList);
    }

    private ExprIdentifier exprId() {
        String name = lexer.getStringValue();
        
        lexer.nextToken();

        return new ExprIdentifier(name);
    }
    
}
