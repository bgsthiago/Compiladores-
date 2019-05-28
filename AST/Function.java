package AST;

import java.util.ArrayList;

public class Function {
	public Function( String id, Type type ) {
        this.id = id;
        this.type = type; 
    }

    public String getId() { return id; }

    public void genC() {
        System.out.println( "" );
    }
    
    private String id;
    private Type type; //futuramente criar uma classe typer
    private ArrayList<ParamDec> ParamList;
    private ArrayList<Stat> StatList;
    
}
