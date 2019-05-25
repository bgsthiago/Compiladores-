package AST;

    public class NumberExpr extends Expr {
        public NumberExpr( int value ) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }

        public void genC() {
            System.out.print(value);
        }
        
        private int value;
}