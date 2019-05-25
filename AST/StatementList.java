package AST;

import java.util.*;

public class StatementList {
    public StatementList(ArrayList<Statement> v) {
        this.v = v;
    }

    public void genC() {
        if ( v != null ) {
            for ( Statement s : v )
                s.genC();
        }
    }
    
    private ArrayList<Statement> v;
}