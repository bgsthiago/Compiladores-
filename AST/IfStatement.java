package AST;

public class IfStatement extends Statement {

    public IfStatement( Expr expr, StatementList thenPart, StatementList elsePart ) {
        this.expr = expr;
        this.thenPart = thenPart;
        this.elsePart = elsePart;
    }
    
    public void genC() {
        System.out.print("if ( ");
        expr.genC();
        System.out.println(" ) { ");

        if ( thenPart != null )
            thenPart.genC();
        System.out.println("}");

        if ( elsePart != null ) {
            System.out.println("else {");
            elsePart.genC();
            System.out.println("}");
        }
    }

    private Expr expr;
    private StatementList thenPart, elsePart;
}