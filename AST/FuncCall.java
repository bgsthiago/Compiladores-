package AST;

import Lexer.*;
import Java.io.*;

public class FuncCall extends Expr {

  //atributos:
  private ArrayList<Expr> exprList;
  private String name;

  //metodos:
  public FuncCall(String nome, ArrayList<Expr> lista){
    this.ArrayList<Expr> exprList = lista;
    this.name = nome;
  }

  public ArrayList<Expr> getExprList(){
    return this.exprList;
  }

  public String getName(){
    return this.name;
  }


}
