package AST;

import java.io.*;
import java.util.*;

public class ParamDec {

	//atributos:
	private String id;
	private Type type;


	//metodos:
	public ParamDec(String name) {
		this.id = name;;
	}

	public void setType(Type t){
		this.type = t;
	}

	public Type getType(){
		return this.type;
	}

	public String getId(){
		return this.id;
	}


}
