package AST;

public class IntegerType extends Type {
	public IntegerType(){ 
		super("Int");
	}
	public String getCname() { return "int"; }
}
