package AST;

import Lexer.*;

public class ExprUnary extends Expr {

  //atrubutos:
  private Symbol op;
  private Expr e;

  // metodos:
  public ExprUnary(Symbol op, Expr e){
    this.op = op;
    this.e = e;
  }

  public void genC(){
    System.out.print(this.op);
    e.genC();
  }
}
