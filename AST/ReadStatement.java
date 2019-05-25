package AST;

public class ReadStatement extends Statement {
    public ReadStatement( Variable v ) {
        this.v = v;
    }

    public void genC() {
        System.out.println("{ char s[256]; gets(s); sscanf(s, \"%d\", &" + v.getName() + "); }");
    }
    
    private Variable v;
}