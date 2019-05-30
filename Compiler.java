/*
comp8

Variables now can have any number of characters and numbers any number
of digits. There are new keywords and new non-terminals. The operator
set includes the comparison operators. There are a few statements.
Anything after // till the end of the line is a comment.
Note that VarDecList was modified.

The input is now taken from a file.

Method error now prints the line in which the error occurred.

Grammar:

Program ::= [ "var" VarDecList ";" ] CompositeStatement
CompositeStatement ::= "begin" StatementList "end"
StatementList ::= | Statement ";" StatementList
Statement ::= AssignmentStatement | IfStatement | ReadStatement |
WriteStatement
AssignmentStatement ::= Variable "=" Expr
IfStatement ::= "if" Expr "then" StatementList [ "else" StatementList ] "endif"
ReadStatement ::= "read" "(" Variable ")"
WriteStatement ::= "write" "(" Expr ")"
Variable ::= Letter { Letter }
VarDecList ::= Variable | Variable "," VarDecList
Expr::= ’(’ oper Expr Expr ’)’ | Number | Variable
Oper ::= ’+’ | ’-’ | ’*’ | ’/’ | ’<’ | ’<=’ | ’>’ | ’>=’| ’==’ | ’<>’
Number ::= Digit { Digit }
Digit ::= ’0’| ’1’ | ... | ’9’
Letter ::= ’A’ | ’B’| ... | ’Z’| ’a’| ’b’ | ... | ’z’/*

Anything between [] is optional. Anything between { e } can be
repeated zero or more times.
*/

import AST.*;
import java.util.*;
import Lexer.*;
import java.lang.Character;
import java.io.*;

public class Compiler {

 private Hashtable < String, Variable > symbolTable;
 private Lexer lexer;
 private CompilerError error;

 // compile must receive an input with an character less than
 // p_input.lenght

 public Program compile(char[] input, PrintWriter outError) {

  symbolTable = new Hashtable < String, Variable > ();

  error = new CompilerError(outError);
  lexer = new Lexer(input, error);
  error.setLexer(lexer);
  lexer.nextToken();
  return program();
 }

 private Program program() {

  // Program ::= Func {Func}
  ArrayList < Function > arrayFunction = new ArrayList < Function > ();

  while (lexer.token == Symbol.FUNCTION) {
   lexer.nextToken();
   arrayFunction.add(func())
  }

  Program program = new Program(arrayFunction);

  if (lexer.token != Symbol.EOF)
   error.signal("EOF expected");

  return program;

 }

 private Function func() {
  //Func ::= "function" Id [ "(" ParamList ")" ] ["->" Type ] StatList

  Boolean isIdent = true;
  String id = "";
  Type type = null;

  for (Symbol c: Symbol.values()) {
   if (c.name().equals(lexer.token)) {
    isIdent = false;
   }
  }

  if (isIdent) {
   id = lexer.token.getStringValue();
   Function func = new Function(id);
   lexer.nextToken();


   if (lexer.token == Symbol.LEFTPAR) {
    lexer.nextToken();

    func.setParamList(paramList());

    if (lexer.token == Symbol.RIGHTPAR) {
     lexer.nextToken();
    } else {
     error.signal("right parenthesis missing");
    }
   } else {
    error.signal("left parenthesis missing")
   }
  } else {
   error.signal("Identifier expected")
  }

  if (lexer.token == Symbol.ARROW) {
   lexer.nextToken();
   type = type();
   func.setReturnType(type);
  }

  // Check and consume '{'
  //lexer.nextToken();
  /*if (lexer.token != Symbol.OPENBRACE) {
      error.signal("{ expected")
  } else {
      lexer.nextToken();
  }*/
  func.setStatList(statList());

  return func;

 }


 private paramList() {
  // ParamList ::= ParamDec {”, ”ParamDec}
  ParamList paramlist = null;

  if (lexer.token == Symbol.IDENT) {
   paramlist = new ParamList();
   paramDec(paramlist);


   while (lexer.token == Symbol.COMMA) {
    lexer.nextToken();
    paramDec(paramlist);
   }
  } else {
   error.signal("identifier expected");
  }

  return paramlist;

 }

 private Type type() {
  Type result;

  switch (lexer.token) {
   case Symbol.INTEGER:
    result = Type.integerType;
    break;
   case Symbol.BOOLEAN:
    result = Type.booleanType;
    break;
   case Symbol.STRING:
    result = Type.stringType;
    break;
   default:
    error.signal("Type expected");
    result = Type.integerType;
  }

  lexer.nextToken();
  return result;
 }



 private StatementList statList() {
  // StatList ::= "{" {Stat} "}"

  Symbol tkn;
  ArrayList < Statement > v = new ArrayList < Statement > ();

  if (lexer.token != Symbol.OPENBRACE) {
   error.signal("{ expected")
  } else {
   lexer.nextToken();
  }

  while ((tkn = lexer.token) != Symbol.CLOSEBRACE && tkn != Symbol.EOF) {
   v.add(stat());
  }

  if (tkn != Symbol.CLOSEBRACE) {
   error.signal("} expected");
  } else {
   lexer.nextToken();
  }

  return new StatementList(v);
 }


 private Statement stat() {
  // Stat ::= AssignExprStat| ReturnStat | VarDecStat | IfStat | WhileStat

  switch (lexer.token) {
   case Symbol.IDENT: // Ta errado, precisa revisar a regra da gramatica
    return assignExprStat();
    break;
   case Symbol.TRUE:
    return assignExprStat();
    break;
   case Symbol.FALSE:
    return assignExprStat();
    break;
   case Symbol.LITERALINT:
    return assignExprStat();
    break;
   case Symbol.LITERALSTRING:
    return assignExprStat();
    break;
   case Symbol.PLUS:
    return assignExpStat();
    break;
   case Symbol.MINUS:
    return assignExpStat();
    break;
   case Symbol.RETURN:
    return returnStat();
    break;
   case Symbol.VAR:
    return varDecStat();
    break;
   case Symbol.IF:
    return IfStat();
    break;
   case Symbol.WHILE:
    return whileStat();
    break;
   default:
    // will never be executed
    error.signal("Statement expected");
  }
  return null;
 }


 private AssignExprStatement assignExprStat() {
  // AssignExprStat ::= Expr [ "=" Expr] ";"

  Expr left = expr();
  Expr right = null;

  if (lexer.token == Symbol.ATRIB) {
   lexer.nextToken()
   right = expr();
  }

  if (lexer.token != Symbol.SEMICOLON) {
   error.signal("; expected");
  }


  // #Sera implementado na segunda fase (Analisador Semantico)
  // is the variable in the symbol table ? Variables are inserted in the
  // symbol table when they are declared. It the variable is not there, it has
  // not been declared.
  // # Implementar analise semantica

  return new AssignExprStat(left, right);
 }







}
