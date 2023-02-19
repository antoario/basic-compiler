/**
 * Analizzatore sintattico per grammatica data
 */
package _2_Parser;
import _1_Lexer.*;
import java.io.*;

public class Parser 
{
	private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
		throw new Error("near line " + Lexer.line + ": " + s);
    }

    void match(int t) {
		if (look.tag == t) {
		    if (look.tag != Tag.EOF) move();
		} 
		else error("syntax error");
    }

	// GUIDA: { Tag.ASSIGN, Tag.PRINT, Tag.READ, Tag.COND, Tag.WHILE, { }
    public void prog() {
		switch(look.tag) {
			case Tag.ASSIGN:
			case Tag.PRINT:
			case Tag.READ:
			case Tag.COND:
			case Tag.WHILE:
			case '{':
				statlist();
				match(Tag.EOF);
				break;
			default:
				error("in prog.");
		}
    }

	// GUIDA: { Tag.ASSIGN, Tag.PRINT, Tag.READ, Tag.COND, Tag.WHILE, { }
    private void statlist() {
    	switch(look.tag) {
			case Tag.ASSIGN:
			case Tag.PRINT:
			case Tag.READ:
			case Tag.COND:
			case Tag.WHILE:
			case '{':
				stat();
				statlistp();
				break;
			default:
				error("in statlist.");
		}
    }

	// GUIDA: { ;, }, Tag.EOF }
    private void statlistp() {
    	switch(look.tag) {
    		case ';':
    			match(';');
    			stat();
    			statlistp();
    			break;
    		case '}':
    		case Tag.EOF:
    			break;
    		default:
    			error("in statlistp.");
    	}
    }

	// GUIDA: { Tag.ASSIGN, Tag.PRINT, Tag.READ, Tag.COND, Tag.WHILE, { }
    private void stat() {
    	switch(look.tag) {
    		case Tag.ASSIGN:
    			match(Tag.ASSIGN);
    			expr();
    			match(Tag.TO);
    			idlist();
    			break;
    		case Tag.PRINT:
    			match(Tag.PRINT);
    			match('[');
    			exprlist();
    			match(']');
    			break;
    		case Tag.READ:
    			match(Tag.READ);
    			match('[');
    			idlist();
    			match(']');
    			break;
    		case Tag.COND:
    			match(Tag.COND);
    			match('[');
    			optlist();
    			match(']');
    			condp();
    			match(Tag.END);
    			break;
			case Tag.WHILE:
				match(Tag.WHILE);
				match('(');
				bexpr();
				match(')');
				stat();
				break;
    		case '{':
    			match('{');
    			statlist();
    			match('}');
    			break;
    		default:
    			error("in stat.");
    	}
    }

	// GUIDA: { Tag.ELSE, Tag.END }
    private void condp() {
    	switch(look.tag) {
    		case Tag.ELSE:
    			match(Tag.ELSE);
    			stat();
    			break;
    		case Tag.END:
    			break;
    		default:
    			error("in condp.");
    	}
    }

	// GUIDA: { Tag.ID }
    private void idlist() {
    	switch(look.tag) {
    		case Tag.ID:
    			match(Tag.ID);
        		idlistp();
        		break;
        	default:
        		error("in idlist.");
    	}
    }

	// GUIDA: { ,, ;, ], }, Tag.OPTION, Tag.END, Tag.EOF }
	// }
    private void idlistp() {
    	switch(look.tag) {
    		case ',':
    			match(',');
    			match(Tag.ID);
    			idlistp();
    			break;
    		case ';':
    		case ']':
    		case '}':
    		case Tag.OPTION:
    		case Tag.END:
    		case Tag.EOF:
    			break;
    		default:
    			error("in idlistp.");
    	}
    }

    // GUIDA: { Tag.OPTION }
    private void optlist() {
    	switch(look.tag) {
    		case Tag.OPTION:
    			optitem();
        		optlistp();
        		break;
        	default:
        		error("in optlist.");
    	}
    }

	// GUIDA: { Tag.OPTION, ] }
    private void optlistp() {
    	switch(look.tag) {
    		case Tag.OPTION:
    			optitem();
    			optlistp();
    			break;
    		case ']':
    			break;
    		default:
    			error("in optlistp.");
    	}
    }

	// GUIDA: { Tag.OPTION }
    private void optitem() {
    	switch(look.tag) {
    		case Tag.OPTION:
    			match(Tag.OPTION);
    			match('(');
    			bexpr();
    			match(')');
    			match(Tag.DO);
    			stat();
    			break;
    		default:
    			error("in optitem.");
    	}
    }

	// GUIDA: { Tag.RELOP }
    private void bexpr() {
    	switch(look.tag) {
    		case Tag.RELOP:
    			match(Tag.RELOP);
    			expr();
    			expr();
    			break;
    		default:
    			error("in bexpr");
    	}
    }

	// GUIDA: { +, -, *, /, Tag.NUM, Tag.ID }
    private void expr() {
    	switch(look.tag) {
    		case '+':
    			match('+');
    			match('(');
    			exprlist();
    			match(')');
    			break;
    		case '-':
    			match('-');
    			expr();
    			expr();
    			break;
    		case '*':
    			match('*');
    			match('(');
    			exprlist();
    			match(')');
    			break;
    		case '/':
    			match('/');
    			expr();
    			expr();
    			break;
    		case Tag.NUM:
    			match(Tag.NUM);
    			break;
    		case Tag.ID:
    			match(Tag.ID);
    			break;
    		default:
    			error("in expr.");
    	}
    }

	// GUIDA: { +, -, *, /, Tag.NUM, Tag.ID }
    private void exprlist() {
    	switch(look.tag) {
    		case '+':
    		case '-':
    		case '*':
    		case '/':
    		case Tag.NUM:
    		case Tag.ID:
    			expr();
    			exprlistp();
    			break;
    		default:
    			error("in exprlist.");
    	}
    }

	// GUIDA: { ,, ), ] }
    private void exprlistp() {
    	switch(look.tag) {
    		case ',':
    			match(',');
    			expr();
    			exprlistp();
    			break;
    		case ')':
    		case ']':
    			break;
    		default:
    			error("in exprlistp.");
    	}
    }
    
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "esempio_semplice.lft";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
}