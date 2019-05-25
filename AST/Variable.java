package AST;

public class Variable {
    public Variable( String name ) {
        this.name = name;
    }

    public String getName() { return name; }

    public void genC() {
        System.out.println( "int " + name + ";" );
    }
    
    private String name;
}