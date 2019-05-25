package Lexer;
public enum Symbol {
    EOF("eof"),
    IDENT("Identifier"),
    NUMBER("Number"),
    PLUS("+"),
    MINUS("-"),
    MULT("*"),
    DIV("/"),
    LT("<"),
    LE("<="),
    GT(">"),
    GE(">="),
    NEQ("!="),
    EQ("=="),
    ASSIGN("="),
    LEFTPAR("("),
    RIGHTPAR(")"),
    SEMICOLON(";"),
    VAR("var"),
    BEGIN("begin"),
    END("end"),
    IF("if"),
    THEN("then"),
    ELSE("else"),
    ENDIF("endif"),
    COMMA(","),
    READ("read"),
    WRITE("write");
}

Symbol(String name) {
    this.name = name;
}
    public String toString() { return name; }
    public String name;
}