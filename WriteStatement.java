package AST;

    public class WriteStatement extends Statement {
        public WriteStatement( Expr expr ) {
            this.expr = expr;
        }

        public void genC() {
            System.out.print("printf(\"%d\", ");
            expr.genC();
            System.out.println(" );");
        }
        
        private Expr expr;
}