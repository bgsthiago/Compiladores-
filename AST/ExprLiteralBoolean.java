package AST;

import Lexer.*;

public class ExprLiteralBoolean extends Expr {

  //atributos:
  private String value;

  public ExprLiteralBoolean(boolean par){
    if( par == true){
      this.value = "1";
    }else{
      this.value = "0";
    }
  }

  public String getBooleanValue(){
    return this.value;
  }

  public genC(){
    System.out.print(getBooleanValue());
  }

}
