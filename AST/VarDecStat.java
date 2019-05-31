package AST;

import java.io.*;
import Lexer.*;

public class VarDecStat extends Statement{

    //atributos:
    private Variable v;

    //metodos:
    public VarDecStat(Variable v){
        this.v = v;
    }

    //genc
    public void genC(){
        this.v.genC();
    }
}