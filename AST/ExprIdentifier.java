package AST;

import Lexer.*;
import java.io.*;

public class ExprIdentifier extends Expr {

  //atributos:
  private String name;

  //metodos:
  public ExprIdentifier(String nome){
    this.name = nome;
  }

  public String getName(){
    return this.name;
  }
  
  public void genC(){}
}
