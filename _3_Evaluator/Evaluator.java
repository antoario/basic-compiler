/**
 * Simile al parser per espressioni aritmetiche ma returna il valore dell'espressione stessa
 */
package _3_Evaluator;
import _1_Lexer.*;
import java.io.*;

public class Evaluator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Evaluator(Lexer l, BufferedReader br) {
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

    // GUIDA: { (, Tag.NUM }
    public void start() {
	    int expr_val;
        switch(look.tag) {
            case '(':
            case Tag.NUM:
                expr_val = expr();
                match(Tag.EOF);
                System.out.println("Value of expression: " + expr_val);
                break;
            default:
                error("in start.");
        }
    }

    // GUIDA: { (, Tag.NUM }
    private int expr() {
        int term_val, exprp_val;
        switch(look.tag) {
            case '(':
            case Tag.NUM:
                term_val = term();
                exprp_val = exprp(term_val);
                return exprp_val;
            default:
                error("in expr.");
                return 0;
        }
    }

    // GUIDA: { +, -, ), Tag.EOF }
    private int exprp(int exprp_i) {
        int term_val, exprp_val;
        switch (look.tag) {
        case '+':
            match('+');
            term_val = term();
            exprp_val = exprp(exprp_i + term_val);
            return exprp_val;
        case '-':
            match('-');
            term_val = term();
            exprp_val = exprp(exprp_i - term_val);
            return exprp_val;
        case ')':
        case Tag.EOF:
            return exprp_i;
        default:
            error("in exprp.");
            return 0;
        }
    }

    // GUIDA: { 8, Tag.NUM }
    private int term() {
        int fact_val, termp_val;
	    switch(look.tag) {
            case '(':
            case Tag.NUM:
                fact_val = fact();
                termp_val = termp(fact_val);
                return termp_val;
            default:
                error("in term.");
                return 0;
        }
    }

    // GUIDA: { *, /, +, -, ), Tag.EOF }
    private int termp(int termp_i) {
        int termp_val, fact_val;
	    switch(look.tag) {
            case '*':
                match('*');
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                return termp_val;
            case '/':
                match('/');
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                return termp_val;
            case '+':
            case '-':
            case ')':
            case Tag.EOF:
                return termp_i;
            default:
                error("in termp.");
                return 0;
        }
    }

    // GUIDA: { (, Tag.NUM   }
    private int fact() {
        int fact_val, num_val;
	    switch(look.tag) {
            case '(':
                match('(');
                fact_val = expr();
                match(')');
                return fact_val;
            case Tag.NUM:
                num_val = ((NumberTok)look).getValue();
                match(Tag.NUM);
                return num_val;
            default:
                error("in fact.");
                return 0;
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "esame.lft";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Evaluator evaluator = new Evaluator(lex, br);
            evaluator.start();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}