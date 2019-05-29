package AST;

import Lexer.Symbol;

abstract public class Type {
	private String nome = "";
	
	public Type(String name) {
		this.nome = name;
	}
	
	public static Type booleanType = new BooleanType();
	public static Type integerType = new IntegerType(); 
	public static Type stringType = new StringType(); 
	
	public String getName() { return this.nome; }

}
