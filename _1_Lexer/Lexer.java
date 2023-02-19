/**
 * Implementation of a lexical analyzer for a simple programming language given in class.
 * The lexical analyzer must recognize:
 * - NUM(numerical value)   -> 256
 * - ID(identifier)         -> 257
 * - RELOP(<,<=,>,>=,<>,==) -> 258
 * - ASSIGN(assignment)     -> 259
 * - TO                     -> 260
 * - COND(conditional)      -> 261
 * - OPTION(conditions)     -> 262
 * - DO                     -> 263
 * - ELSE                   -> 264
 * - WHILE                  -> 265
 * - BEGIN                  -> 266
 * - END                    -> 267
 * - PRINT(in console)      -> 268
 * - READ(from keyborad)    -> 269
 * - OR operator(||)        -> 270
 * - AND operator(&&)       -> 271
 * - ! operator             -> 33
 * - (                      -> 40
 * - )                      -> 41
 * - [                      -> 91
 * - ]                      -> 93
 * - {                      -> 123
 * - }                      -> 125
 * - SUM operator(+)        -> 43
 * - SUB operator(-)        -> 45
 * - MUL operator(*)        -> 42
 * - DIV operator(/)        -> 47
 * - ;                      -> 59
 * - ,                      -> 44
 * - EOF(End Of File)       -> -1
 */
package _1_Lexer;
import java.io.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';

    /**
     * Allows scrolling of input file.
     * @param br buffer reader(to allow scrolling of the input file).
     */
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1;
        }
    }

    /**
     * Method that handles the recognition of identifiers and keywords.
     * @param br buffer reader(to allow scrolling of the input file).
     * @return Token(Word type) corresponding to the word found.
     */
    private Token getWord(BufferedReader br) {
        String s = "";
        for(; ((Character.isLetter(peek)) || Character.isDigit(peek) || peek == '_'); readch(br)) s+=peek;
        switch(s) {
            case "assign":
                return Word.assign;
            case "to":
                return Word.to;
            case "conditional":
                return Word.conditional;
            case "option":
                return Word.option;
            case "do":
                return Word.dotok;
            case "else":
                return Word.elsetok;
            case "while":
                return Word.whiletok;
            case "begin":
                return Word.begin;
            case "end":
                return Word.end;
            case "print":
                return Word.print;
            case "read":
                return Word.read;
        }
        return(new Word(Tag.ID, s));
    }

    /**
     * Method that handles the recognition of numbers.
     * @param br buffer reader(to allow scrolling of the input file).
     * @return Token(NumberTok type) corresponding to the number found.
     */
    private Token getNumber(BufferedReader br) {
        int n = peek - '0';
        readch(br);
	    for(; Character.isDigit(peek); readch(br)) {
	        n = n * 10 + peek - '0';
	    }
	    return new NumberTok(Tag.NUM, n);
    }

    /**
     * Method that parses the input file and recognizes symbols.
     * @param br buffer reader(to allow scrolling of the input file).
     * @return Token corresponding to the symbol found.
     */
    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;
            // Case management: ( ) [ ] { } + - * / ; , &
            case '(':
                peek = ' ';
                return Token.lpt;
            case ')':
                peek = ' ';
                return Token.rpt;  
            case '[':
                peek = ' ';
                return Token.lpq;
            case ']':
                peek = ' ';
                return Token.rpq;
            case '{':
                peek = ' ';
                return Token.lpg;
            case '}':
                peek = ' ';
                return Token.rpg;
            case '+':
                peek = ' ';
                return Token.plus;
            case '-':
                peek = ' ';
                return Token.minus;
            case '*':
                peek = ' ';
                return Token.mult;
            // Comments management
            case '/':   
                readch(br);
                if(peek == '*') {
                    char prec = ' ';
                    while(!(prec == '*' && peek == '/')) {
                        prec = peek;
                        readch(br);
                        if(peek == (char) Tag.EOF) {
                            System.err.println("Error in comment.");
                            return null;
                        }
                    }
                    readch(br);
                    return lexical_scan(br);
                }
                else if(peek == '/') {
                    while(true) {
                        readch(br);
                        if(peek == '\n') {
                            readch(br);
                            return lexical_scan(br);
                        }
                        else if(peek == (char) Tag.EOF) {
                            System.err.println("Error.");
                            return null;
                        }
                    }
                }
                else {
                    return Token.div;
                }
            case ';':
                peek = ' ';
                return Token.semicolon;
            case ',':
                peek = ' ';
                return Token.comma;
            
            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character" + " after & : "  + peek);
                    return null;
                }

	        // Case management: || < > <= >= == <> default
            case '|':
                readch(br);
                if(peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else { 
                    System.err.println("Erroneous character" + " after | : "  + peek);
                    return null;
                }

            case '<': 
                readch(br);
                if(peek == '=') {
                    peek = ' ';
                    return Word.le;
                } 
                else if(peek == '>') {
                    peek = ' ';
                    return Word.ne;
                }
                else if(peek == ' ') {
                    return Word.lt;
                }
                else {
                    System.err.println("Erroneous character" + " after < : "  + peek);
                    return null;
                }

            case '>':
                readch(br);
                if(peek == '=') {
                    peek = ' ';
                    return Word.ge;
                }
                else if(peek == ' ') {
                    return Word.gt;
                }
                else {
                    System.err.println("Erroneous character" + " after > : "  + peek);
                    return null;
                }

            case '=':
                readch(br);
                if(peek == '=') {
                    peek = ' ';
                    return Word.eq;
                }
                else {
                    System.err.println("Error: only one =");
                    return null;
                }
            case (char) Tag.EOF:
                return new Token(Tag.EOF);

            default:
                if(Character.isLetter(peek)) return getWord(br);
                else if(Character.isDigit(peek)) return getNumber(br);
                else {
                    System.err.println("Erroneous character: " + peek);
                    return null;
                }
        }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Input.lft";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }    
    }
}