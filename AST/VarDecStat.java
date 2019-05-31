

package AST;

import java.io.*;
import Lexer.*;

public class Variable{

    //atributos:
    private VarDecStat varDecStat;

    //metodos:
    VarDecStat(Variable v){
        this.varDecStat = v;
    }

    //genc
    public genC(){
        this.v.genC();
    }
}