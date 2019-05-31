package AST;

import java.util.*;
import java.io.*;


public class ParamList{

    //atributos:
    private ArrayList<ParamDec> arrayParamDec;

    //métodos:
    ParamList(){
      this.arrayParamDec = new ArrayList<ParamDec>();
    }

    public void addElement(ParamDec param){
      arrayParamDec.add(param);

    }

    public int size(){
      return arrayParamDec.size();
    }

    public ArrayList<ParamDec> getParamList(){
      return arrayParamDec;
    }

    public ParamDec access( int i){
      return this.arrayParamDec.get(i);
    }

}
