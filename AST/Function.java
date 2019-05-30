package AST;

import java.util.ArrayList;
import java.io.*;

public class Function {

	//atributos:
	private String id;
	private Type type;
	private ParamList paramList;
	private StatementList statList;


	//metodos:
	public Function( String Id ) {
      this.id = Id;
			this.type = new Type;
			//this.statList = new ArrayList<Stat>;

  }

	public void setReturnType(Type x){
		this.type  = x;
	}

  public String getId() {
		return this.id;
	 }

	public void getType(){
		return this.type;
	}

	public void setParamList(ParamList paramlist){
		this.paramList = paramlist;
	}

	public void setStatList(StatementList statlist){
		this.statList = statlist;
	}


  public void genC() {
      System.out.println( "" );
  }


}
