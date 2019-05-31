

package AST;

import java.io.*;
import Lexer.*;

public class Variable{

    //atributos:
    private String id;
    private Type type;

    Variable(String id){
        this.id = id;
    }

    public String getId(){ 
        return this.id;
    }

    public void setType(Type type){
        this.type = type;
    }

    public Type getType(){
        return this.type;
    }

    //genc
    public genC(){
        System.out.print(this.type.getTypeName() + this.id);
    }
}