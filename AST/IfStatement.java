package AST;

import Java.io.*;

public class IfStatement extends Statement{

  //atributos:
  private Expr expr;
  private StatementList thenPart;
  private StatementList elsePart;

  //metodos:
  public IfStatement( Expr expr, StatementList then, StatementList elsePart){
    this.thenPart = then;
    this.elsePart = elsePart;
    this.expr = expr;
  }

  public Expr getIfExpr(){
    return this.expr;
  }

  public StatementList getThenPart(){
    return this.thenPart;
  }

  public StatementList getElsePart(){
    return this.elsePart;
  }
}
