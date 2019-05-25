package AST;

import Lexer.*;

public class CompositeExpr extends Expr {
    public CompositeExpr( Expr pleft, Symbol poper, Expr pright ) {
        left = pleft;
        oper = poper;
        right = pright;
    }

    public void genC() {
        System.out.print("(");
        left.genC();
        System.out.print(" " + oper.toString() + " ");
        right.genC();
        System.out.print(")");
    }
    
    private Expr left, right;
    private Symbol oper;
}