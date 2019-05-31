package AST;

import Lexer.*;

public class ExprLiteralInt extends Expr {

  //atributos:
  private String value;

  public ExprLiteralInt(int value){
    this.value = Integer.toString(value);
  }

  public String getIntValue(){
    return this.value;
  }

  public void genC(){
    System.out.print(getIntValue());
  }

}
