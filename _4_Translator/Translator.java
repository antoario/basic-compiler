package _4_Translator;
import _1_Lexer.*;
import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();

    public Translator(Lexer l, BufferedReader br) {
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
        int label_next_prog;
        switch(look.tag) {
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                label_next_prog = code.newLabel();
                statlist();
                code.emitLabel(label_next_prog);
                match(Tag.EOF);
                try {
                    code.toJasmin();
                } catch (Exception e) {
                    System.out.println("IO error\n");
                } finally {
                    break;
                }
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

    // GUIDA: { Tag.ASSIGN, Tag.PRINT, Tag.READ, Tag.COND, Tag.WHILE, {, Tag.EOF }
    public void stat() {
        int label_start, label_continue, label_end;
        switch(look.tag) {
            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist(OpCode.dup, -1, false);
                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                match('[');
                exprlist(OpCode.invokestatic, 1, true);
                match(']');
                break;
            case Tag.READ:
                match(Tag.READ);
                match('[');
                idlist(OpCode.invokestatic, 0, true);
                match(']');
                break;
            case Tag.COND:
                label_end = code.newLabel();
                match(Tag.COND);
                match('[');
                optlist(label_end);
                match(']');
                condp();
                code.emitLabel(label_end);
                match(Tag.END);
                break;
            case Tag.WHILE:
                label_start = code.newLabel();
                label_continue = code.newLabel();
                label_end = code.newLabel();
                match(Tag.WHILE);
                match('(');
                code.emitLabel(label_start);
                bexpr(label_continue, label_end);
                code.emitLabel(label_continue);
                match(')');
                stat();
                code.emit(OpCode.GOto, label_start);
                code.emitLabel(label_end);
                break;
            case '{':
                match('{');
                statlist();
                match('}');
                break;
            case Tag.EOF:
            default:
                error("in stat.");
        }
    }

    // GUIDA: { Tag.ELSE,  Tag.END }
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
    private void idlist(OpCode opcode, int p, boolean chiamato) {
        int to_add;
        switch(look.tag) {
            case Tag.ID:
                to_add = st.insertIf(((Word)look).lexeme);
                match(Tag.ID);
                if(OpCode.dup == opcode);
                else if(chiamato || look.tag == ',') code.emit(opcode, p);
                code.emit(OpCode.istore, to_add);
                idlistp(opcode, p, chiamato);
                break;
            default:
                error("in idlist.");
        }
    }

    // GUIDA: { ,, ;, ], },Tag.OPTION, Tag.END, Tag.EOF }
    private void idlistp(OpCode opcode, int p, boolean chiamato) {
        int to_add;
        switch(look.tag) {
            case ',':
                match(',');
                to_add = st.insertIf(((Word)look).lexeme);
                match(Tag.ID);
                if(chiamato || look.tag == ',') code.emit(opcode, p);
                code.emit(OpCode.istore, to_add);
                idlistp(opcode, p, chiamato);
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
    private void optlist(int label_end) {
        int label_next_optlist;
        switch(look.tag) {
            case Tag.OPTION:
                label_next_optlist = code.newLabel();
                optitem(label_next_optlist, label_end);
                code.emitLabel(label_next_optlist);
                optlistp(label_end);
                break;
            default:
                error("in optlist.");
        }
    }

    // GUIDA: { Tag.OPTION, ] }
    private void optlistp(int label_end) {
        int label_next_optlist;
        switch(look.tag) {
            case Tag.OPTION:
                label_next_optlist = code.newLabel();
                optitem(label_next_optlist, label_end);
                code.emitLabel(label_next_optlist);
                optlistp(label_end);
                break;
            case ']':
                break;
            default:
                error("in optlistp.");
        }
    }

    // GUIDA: { Tag.OPTION }
    private void optitem(int label_next_optlist, int label_end) {
        int label_continue;
        switch(look.tag) {
            case Tag.OPTION:
                match(Tag.OPTION);
                match('(');
                label_continue = code.newLabel();
                bexpr(label_continue, label_next_optlist);
                code.emitLabel(label_continue);
                match(')');
                match(Tag.DO);
                stat();
                code.emit(OpCode.GOto, label_end);
                break;
            default:
                error("in optitem.");
        }
    }

    // GUIDA: { Tag.RELOP }
    private void bexpr(int label_continue, int label_next_optlist) {
        switch(look.tag) {
            case Tag.RELOP:
                String type = ((Word)look).lexeme;
                match(Tag.RELOP);
                expr();
                expr();
                OpCode op = switch (type) {
                    case "<" -> OpCode.if_icmplt;
                    case "<=" -> OpCode.if_icmple;
                    case "==" -> OpCode.if_icmpeq;
                    case "<>" -> OpCode.if_icmpne;
                    case ">=" -> OpCode.if_icmpge;
                    case ">" -> OpCode.if_icmpgt;
                    default -> null;
                };
                code.emit(op, label_continue);
                code.emit(OpCode.GOto, label_next_optlist);
                break;
            default:
                error("in bexpr.");
        }
    }

    // GUIDA: { +, -, *, /, Tag.NUM, Tag.ID }
    private void expr() {
        switch(look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist(OpCode.iadd, -1, false);
                match(')');
                break;
            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
            case '*':
                match('*');
                match('(');
                exprlist(OpCode.imul, -1, false);
                match(')');
                break;
            case '/':
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;
            case Tag.NUM:
                code.emit(OpCode.ldc, ((NumberTok)look).getValue());
                match(Tag.NUM);
                break;
            case Tag.ID:
                code.emit(OpCode.iload, st.lookupAddress(((Word)look).lexeme));
                match(Tag.ID);
                break;
            default:
                error("in expr.");
        }
    }

    // GUIDA: { +, -, *, /, Tag.NUM, Tag.ID }
    private void exprlist(OpCode opcode, int p, boolean chiamato) {
        switch(look.tag) {
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                if(chiamato) code.emit(opcode, p);
                exprlistp(opcode, p, chiamato);
                break;
            default:
                error("in exprlist.");
        }
    }

    // GUIDA{ ,, ,),] }
    private void exprlistp(OpCode opcode, int p, boolean chiamato) {
        switch(look.tag) {
            case ',':
                match(',');
                expr();
                code.emit(opcode, p);
                exprlistp(opcode, p, chiamato);
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
        String path = "esame.lft";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator traduttore = new Translator(lex, br);
            traduttore.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
