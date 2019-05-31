package AST;

import java.util.*;
import java.io.*;


public class ParamList{

    //atributos:
    private ArrayList<Parameter> arrayParameter;

    //métodos:
    public ParamList(){
      this.arrayParameter = new ArrayList<Parameter>();
    }

    public void addElement(Parameter param){
      arrayParameter.add(param);

    }

    public int size(){
      return arrayParameter.size();
    }

    public ArrayList<Parameter> getParamList(){
      return arrayParameter;
    }

    public Parameter access( int i){
      return this.arrayParameter.get(i);
    }

}
