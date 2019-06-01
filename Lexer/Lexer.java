/*Bruno Veiga - 743514
Lucas Costa - 743563
Luiz Felipe Guimarães - 743570
Thiago Borges - 613770*/

package Lexer;

import java.util.*;

public class Lexer {
    
    /*construtor:*/
    public Lexer( char []input, CompilerError error ) {
    
        this.input = input;
        
        input[input.length - 1] = '\0';
        
        lineNumber = 1;
        tokenPos = 0;
        this.error = error;
    }

    /* contém as palavras chave*/
    static private Hashtable<String, Symbol> keywordsTable;

    /*executado uma vez por execução do programa*/
    static {
        keywordsTable = new Hashtable<String, Symbol>();
        
        keywordsTable.put( "var", Symbol.VAR );
        keywordsTable.put( "Int", Symbol.INTEGER );
        keywordsTable.put( "Boolean", Symbol.BOOLEAN );
        keywordsTable.put( "String", Symbol.STRING );
        keywordsTable.put( "true", Symbol.TRUE );
        keywordsTable.put( "false", Symbol.FALSE );
        keywordsTable.put( "function", Symbol.FUNCTION);
        keywordsTable.put( "Id", Symbol.IDENT );
        keywordsTable.put( "if", Symbol.IF );
        keywordsTable.put( "else", Symbol.ELSE );
        keywordsTable.put( "while", Symbol.WHILE );
        keywordsTable.put( "or", Symbol.OR );
        keywordsTable.put( "and", Symbol.AND );
        keywordsTable.put( "return", Symbol.RETURN);
    }

    
    public void nextToken(){

        char ch;

        //checa por espaços em brancos, tabs, etc...
        while((ch = input[tokenPos]) == ' ' || ch == '\r' || ch == '\t' || ch == '\n'){
            //conto numero de linhas
            if(ch == '\n'){
                lineNumber++;
            }
            tokenPos++;
        }

        
        //se for fim de input:
        if(ch == '\0'){
            token = Symbol.EOF;
        }else{

            //cuidando de comentarios agora:
            if(input[tokenPos] == '/' && input[tokenPos + 1] == '/'){
               while(input[tokenPos] != '\0' && input[tokenPos] != '\n'){
                   tokenPos++;
               }
               nextToken();
            } else {
                int contadorDeAspas = 0;

                if (ch == '\"') {
                    StringBuffer stringLiteral = new StringBuffer();
                    contadorDeAspas++;
                    tokenPos++;

                    while (input[tokenPos] != '\"') {
                        if (input[tokenPos] == '\0') {
                            error.signal("\" expected");
                        }
                        
                        stringLiteral.append(input[tokenPos]);
                        tokenPos++;
                    }
                    tokenPos++;
                    
                    stringValue = stringLiteral.toString();
                    token = Symbol.LITERALSTRING;
                    
                }else{
                
                    //cuidando de identificadores agora
                    if(Character.isLetter(ch)){

                        StringBuffer identificador =  new StringBuffer();

                        //vou colocando char por char dentro da string do identificador
                        while(Character.isLetter(input[tokenPos])){
                            identificador.append(input[tokenPos]);
                            tokenPos++;
                        }

                        stringValue = identificador.toString();

                        //se a string formada pelo identificador estiver na lista de palavras reservadas:
                        Symbol value = keywordsTable.get(stringValue);

                        if(value == null){
                            //entao nao eh palavra reservada:
                            token = Symbol.IDENT;
                        }else{
                            //nesse caso, eh palavra reservada: cuido de boolean, int, etc.. aqui.
                            token = value;
                        }
                        if(Character.isDigit(input[tokenPos])){
                            //nesse caso, o identificador termina com um numero, ilegal:
                            error.signal("Identificador seguido de numero");
                        }
                    }else if(Character.isDigit(ch)){
                        
                        StringBuffer number = new StringBuffer();

                        while(Character.isDigit(ch)){
                            number.append(input[tokenPos]);
                            tokenPos++;
                            ch = input[tokenPos];
                        }
                        
                        token = Symbol.LITERALINT;

                        try{
                            numberValue = Integer.valueOf(number.toString()).intValue();    
                        }catch( NumberFormatException e){
                            error.signal("Número fora dos limites");
                        }
                        if( numberValue > MaxValueInteger || numberValue < MinValueInteger){
                            error.signal("Numero fora dos limites");
                        }
                    }else{

                        tokenPos++;
                        switch(ch){

                            case '+':
                                token = Symbol.PLUS;
                                break;

                            case '-':
                                if( input[tokenPos] == '>'){
                                    tokenPos++;
                                    token = Symbol.ARROW;
                                    break;
                                }else{
                                    token = Symbol.MINUS;
                                    break;
                                }
                                

                            case '*':
                                token= Symbol.MULT;
                                break;
                            
                            case '/':
                                token = Symbol.DIV;
                                break;

                            case'(':
                                token = Symbol.LEFTPAR;
                                break;

                            case')':
                                token= Symbol.RIGHTPAR;
                                break;

                            case '{':
                                token = Symbol.OPENBRACE;
                                break;
                            
                            case '}':
                                token = Symbol.CLOSEBRACE;
                                break;
                            
                            case ';':
                                token = Symbol.SEMICOLON;
                                break;
                            
                            case ':':
                                token = Symbol.COLON;
                                break;
                            
                            case ',':
                                token = Symbol.COMMA;
                                break;
                            
                            case '[':
                                token = Symbol.LEFTBRACK;
                                break;
                            
                            case ']':
                                token = Symbol.RIGHTBRACK;
                                break;

                            
                            case '<' :
                                if ( input[tokenPos] == '=' ) {
                                    tokenPos++;
                                    token = Symbol.LE;
                                    } else{
                                        token = Symbol.LT;
                                    }
                                break;

                            case '>' :
                                if ( input[tokenPos] == '=' ) {
                                    tokenPos++;
                                    token = Symbol.GE;
                                }
                                else
                                    token = Symbol.GT;
                                break;
                            
                            case '=' :
                                if ( input[tokenPos] == '=' ) {
                                    tokenPos++;
                                    token = Symbol.EQ;
                                }else{
                                    token = Symbol.ATRIB;
                                }
                                break;
                            
                            case '!':
                                if(input[tokenPos] == '='){
                                    tokenPos++;
                                    token = Symbol.NEQ;
                                }
                                break;
                            
                            default :
                                error.signal("Invalid Character: '" + ch + "'");
                                
                            
                            
                            
                            

                        }
                    }
                }    

            }

        }

        lastTokenPos = tokenPos - 1;
        // Descomente a linha abaixo para exibir os tokens
        System.out.println(token.toString());
    }


    public String getCurrentLine() {
        int i = lastTokenPos;
        if ( i == 0 )
            i = 1;
        else
            if ( i >= input.length )
                i = input.length;
       
         StringBuffer line = new StringBuffer();
        while ( i >= 1 && input[i] != '\n' )
            i--;
        if ( input[i] == '\n' )
            i++;
        while ( input[i] != '\0' && input[i] != '\n' && input[i] != '\r' ) {
            line.append( input[i] );
            i++;
        }
        return line.toString();
    }
    
    public String getStringValue() {
        return stringValue;
    }

    //retorna o numero da linha do ultimo token pego 
    public int getLineNumber() {
        return lineNumber;
    }

    public int getNumberValue() {
        return numberValue;
    }

    public char getCharValue() {
        return charValue;
    }

    //atributos:

    //token atual
    public Symbol token;
    private String stringValue;
    private int numberValue;
    private char charValue;
    private int tokenPos;
    private int lastTokenPos;
    private char []input;
    private int lineNumber;
    private CompilerError error;
    private static final int MaxValueInteger = 2147483647;
    private static final int MinValueInteger = 0;

}