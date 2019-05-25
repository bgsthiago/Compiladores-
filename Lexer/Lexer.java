package Lexer;

import java.util.*; 
import CompilerError;

public class Lexer {
    
    /*construtor:*/
    public Lexer( char []input, CompilerError error ) {
    
        this.input = input;
        
        // add an end-of-file label to make it easy to do the lexer
        input[input.length - 1] = '\0';
        
        // number of the current line
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
        keywordsTable.put( "int", Symbol.INTEGER );
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
        keywordsTable.put( "writeln", Symbol.WRITELN );
        keywordsTable.put( "write", Symbol.WRITE );
    }

    
    public nextToken(){

        char ch;

        //checa por espaços em brancos, tabs, etc...
        while( ch = input[tokenPos] == ' ' || ch == '\r' || ch == '\t' || ch == '\n'){
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
            }else{
                
                //cuidando de identificadores aogra
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
                }else if(Chacaracter.isDigit){

                StringBuffer number = new StringBuffer();
                    while(Character.isDigit(ch)){

                        number.append(input[tokenPos]);
                        tokenPos++;
                    }

                    token = Symbol.INTEGER;

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
                                token = Symbol.ASSIGN;
                            }
                            break;
                        
                        case '!':
                            if(input[tokenPos] == '='){
                                tokenPos++;
                                token = Symbol.NEQ;
                            }
                            break;
                        
                        default :
                            error("Invalid Character: '" + ch + "'");
                            
                        
                        
                        
                        

                    }
                }

            }

        }

        lastTokenPos = tokenPos - 1;

    }

    //metodos just in case:

    public String getCurrentLine() {
        int i = lastTokenPos;
        if ( i == 0 )
            i = 1;
        else
            if ( i >= input.length )
                i = input.length;
       
         StringBuffer line = new StringBuffer();
        // go to the beginning of the line
        while ( i >= 1 && input[i] != '\n' )
            i--;
        if ( input[i] == '\n' )
            i++;
        // go to the end of the line putting it in variable line
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
    
    private String stingValue;

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














