/**
 * Top-down parser for regular expressions written in infixed form.
 * Grammar: ({⟨start⟩,⟨expr⟩,⟨exprp⟩,⟨term⟩,⟨termp⟩,⟨fact⟩}, {+,-,*,/,(,),NUM,EOF}, P, ⟨start⟩)
 * Where P(productions) are:
 * - ⟨start⟩  -->  ⟨expr⟩ EOF
 * - ⟨expr⟩   -->  ⟨term⟩ ⟨exprp⟩
 * - ⟨exprp⟩  -->  + ⟨term⟩ ⟨exprp⟩ | - ⟨term⟩ ⟨exprp⟩ | ε
 * - ⟨term⟩   -->  ⟨fact⟩ ⟨termp⟩
 * - ⟨termp⟩  -->  * ⟨fact⟩ ⟨termp⟩ | / ⟨fact⟩ ⟨termp⟩ | ε
 * - ⟨fact⟩   -->  ( ⟨expr⟩ ) | NUM
 */
package _2_Parser;
import _1_Lexer.*;
import java.io.*;

public class ParserArithmeticExpressions {
	private Lexer lex;
	private BufferedReader pbr;
    private Token look;

    public ParserArithmeticExpressions(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

	/**
	 * Method that scrolls the string and prints the recognized token on the screen.
	 */
	void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

	/**
	 * Method to manage error.
	 * @param s string specifying in which method the error occurred.
	 */
	void error(String s) {
		throw new Error("near line " + Lexer.line + ": " + s);
    }

	/**
	 * 
	 * @param t
	 */
	void match(int t) {
		if (look.tag == t) {
		    if (look.tag != Tag.EOF) move();
		} 
		else error("syntax error");
    }

	// GUIDA: { (, Tag.NUM }
    public void start() {
    	switch(look.tag) {
    		case '(':
    		case Tag.NUM:
    			expr();
        		match(Tag.EOF);
        		break;
        	default:
        		error("in start.");
    	}
    }

	// GUIDA: { (, Tag.NUM }
    private void expr() {
    	switch(look.tag) {
    		case '(':
    		case Tag.NUM:
    			term();
    			exprp();
        		break;
        	default:
        		error("in expr.");
    	}
    }

	// GUIDA: { +, -, ), Tag.EOF }
    private void exprp() {
		switch (look.tag) {
			case '+':
				match('+');
				term();
				exprp();
				break;
			
			case '-':
				match('-');
				term();
				exprp();
				break;
				
			case ')':
			case Tag.EOF:
				break;
				
			default:
				error("in exprp.");
		}
    }

	// GUIDA: { (, Tag.NUM }
    private void term() {
    	switch(look.tag) {
    		case '(':
    		case Tag.NUM:
    			fact();
    			termp();
    			break;
    		default:
    			error("in term.");
    	}
    }

	// GUIDA: { *, / , +, -, ), Tag.EOF }
    private void termp() {
        switch(look.tag) {
        	case '*':
        		match('*');
        		fact();
        		termp();
        		break;
        		
        	case '/':
        		match('/');
        		fact();
        		termp();
        		break;
        		
        	case '+':
        	case '-':
        	case ')':
        	case Tag.EOF:
        		break;
        		
        	default:
        		error("in termp.");
        }
    }

	// GUIDA: { ( , Tag.NUM }
    private void fact() {
       switch(look.tag) {
       		case '(':
       			match('(');
       			expr();
       			match(')');
       			break;
       			
       		case Tag.NUM:
       			match(Tag.NUM);
       			break;
       			
       		default:
       			error("in fact.");
       }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "esame.lft";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            ParserArithmeticExpressions parser = new ParserArithmeticExpressions(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
}