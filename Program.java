package AST;

import java.util.*;

public class Program {      
    public Program( ArrayList<Variable> arrayVariable, StatementList statementList ) {
        this.arrayVariable = arrayVariable;
        this.statementList = statementList;
    }
    
    public void genC() {
        System.out.println("#include <stdio.h>\n");
        System.out.println("void main() {");
        if ( arrayVariable != null ) {
            // generate code for the declaration of variables
            for ( Variable v : arrayVariable )
                v.genC();
        }
        statementList.genC();
        System.out.println("}");
    }

    private ArrayList<Variable> arrayVariable;
    private StatementList statementList;
}