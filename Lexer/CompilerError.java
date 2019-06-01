/*Bruno Veiga - 743514
Lucas Costa - 743563
Luiz Felipe Guimarães - 743570
Thiago Borges - 613770*/

package Lexer;

import java.io.*;

public class CompilerError {
	
	private Lexer lexer;
	
	public void setLexer(Lexer lexer) {
		this.lexer = lexer;
	}
	
	public void signal (String strMessage) {
		System.out.println("Error at line " + lexer.getLineNumber() + ": ");
		System.out.println(lexer.getCurrentLine());
		System.out.println(strMessage);
		throw new RuntimeException(strMessage);
	}
	
	
}
