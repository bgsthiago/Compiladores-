package Lexer;

public enum Symbol {
    EOF("eof"),
    IDENT("Id"),
    INTEGER("int"),
    BOOLEAN("Boolean"),
    TRUE("true"),
    FALSE("false"),
    FUNCTION("function"),
    STRING("String"),
    ARROW("->"),
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
    ATRIB("="),
    LEFTPAR("("),
    RIGHTPAR(")"),
    OPENBRACE("{"),
    CLOSEBRACE("}"),
    SEMICOLON(";"),
    COLON(":"),
    VAR("var"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    COMMA(","),
    OR("or"),
    AND("and"),
    LEFTBRACK("["),
    RIGHTBRACK("]"),
    WRITELN("writeln"),
    WRITE("write");

    Symbol(String name) {
        this.name = name;
    }
        public String toString() { return name; }
        public String name;
}