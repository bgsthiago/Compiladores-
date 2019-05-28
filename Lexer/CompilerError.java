package Lexer;

import java.io.*;

public class CompilerError {
	
	private Lexer lexer;
	PrintWriter out;
	
	public CompilerError(PrintWriter out) {
		this.out = out;
	}
	
	public void setLexer(Lexer lexer) {
		this.lexer = lexer;
	}
	
	public void signal (String strMessage) {
		out.println("Error at line " + lexer.getLineNumber() + ": ");
		out.println(lexer.getCurrentLine());
		out.println(strMessage);
		if(out.checkError())
			System.out.println("Error in signaling an error");
		throw new RuntimeException(strMessage);
	}
	
	
}
