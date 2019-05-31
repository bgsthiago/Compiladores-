package AST;

import java.io.*;

public class ReturnStatement extends Statement{

  //atributos:
  private Expr returnExpr;

  //metodos:
  public ReturnStatement( Expr expr){
    this.returnExpr = expr;
  }

  public Expr getReturnExpr(){
    return this.returnExpr;
  }

}
