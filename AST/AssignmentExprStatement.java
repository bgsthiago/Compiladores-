package AST;

public class AssignmentExprStatement extends Statement {

    //atributos:
    private Expr right;
    private Expr left;

    public AssignmentStatement( Expr left, Expr right ) {
        this.left = left;
        this.right = right;
    }

    public void genC() {
      //  System.out.print( v.getName() + " = " );
      //  expr.genC();
      //  System.out.println(";");
    }
  }
