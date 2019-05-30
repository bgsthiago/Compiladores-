package AST;

import java.util.*;
import java.io.*;


public class ParamList{

    //atributos:
    private ArrayList<ParamDec> arrayParamDec;

    //métodos:
    ParamList(){
      arrayParamDec = new ArrayList<ParamDec>
    }

    public void addElement(ParamDec param){
      arrayParamDec.add(param);

    }

    public ArrayList<ParamDec> getParamList(){
      return arrayParamDec;
    }


}
