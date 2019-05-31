package AST;

import Lexer.*;
import Java.io.*;

public class ExprIdentifier extends Expr {

  //atributos:
  private String name;

  //metodos:
  public FuncCall(String nome){
    this.name = nome;
  }


  public String getName(){
    return this.name;
  }


}
