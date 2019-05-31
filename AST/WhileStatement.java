package AST;

import java.io.*;

public class WhileStatement extends Statement{

  //atributos:
  private Expr whileExpr;
  private Statement whileStmt;

  //metodos:
  public WhileStatement( Expr expr, Statement stmt){
    this.whileStmt = stmt;
    this.whileExpr = expr;

  }

  public Expr getWhileExpr(){
    return this.whileExpr;
  }

  public Statement getThenPart(){
    return this.whileStmt;
  }
}
