package AST;

public class AssignmentStatement extends Statement {
    public AssignmentStatement( Variable v, Expr expr ) {
        this.v = v;
        this.expr = expr;
    }

    public void genC() {
        System.out.print( v.getName() + " = " );
        expr.genC();
        System.out.println(";");
    }
    
    private Variable v;
    private Expr expr;
}