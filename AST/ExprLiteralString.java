package AST;

import Lexer.*;

public class ExprLiteralString extends Expr {

  //atributos:
  private String value;

  public ExprLiteralString(String par){
    this.value = par;
  }

  public String getStringValue(){
    return this.value;
  }

  public genC(){
    System.out.print(getStringValue());
  }

}
