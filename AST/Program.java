package AST;

import java.util.*;
import java.io.*;

public class Program{

		//atributos:

		//guarda todas as funcoes que existem no programa
		//vai ser util na hora de checar pra ver se existe public static void main(String[] args) {
		// ou pra checar se to declarando funcao com nome  repetido
		private ArrayList<Function> arrayFunction;

		//construtor:
		Program( 	ArrayList<Function> arrayFunction ){

			this.arrayFunction = arrayFunction;

		}

		public void	genC(){
			//ainda sei la o que isso aqui faz
		}



}
