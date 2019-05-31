package AST;

public class AssignExprStatement extends Statement {

    //atributos:
    private Expr right;
    private Expr left;

    public  AssignExprStatement( Expr left, Expr right ) {
        this.left = left;
        this.right = right;
    }

    public void genC() {
      //  System.out.print( v.getName() + " = " );
      //  expr.genC();
      //  System.out.println(";");
    }
  }
