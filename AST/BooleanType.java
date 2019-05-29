package AST;
import java.io.*;

public class BooleanType extends Type {
	public BooleanType(){ 
		super("Boolean");
	}
	public String getCname() { return "int"; }

}
