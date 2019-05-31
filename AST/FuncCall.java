package AST;

import Lexer.*;
import java.io.*;
import java.util.*;

public class FuncCall extends Expr {

  //atributos:
  private ArrayList<Expr> exprList;
  private String name;

  //metodos:
  public FuncCall(String nome, ArrayList<Expr> lista){
    this.exprList = lista;
    this.name = nome;
  }

  public ArrayList<Expr> getExprList(){
    return this.exprList;
  }

  public String getName(){
    return this.name;
  }

  public void genC() {}


}
