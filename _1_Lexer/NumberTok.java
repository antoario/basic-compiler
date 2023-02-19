package _1_Lexer;

public class NumberTok extends Token {
	private int val;
	
	public NumberTok(int tag, int n) {
		super(tag);
		val = n;
	}

	public String toString() {
		return "<" + tag + ", " + val + ">";
	}
	public int getValue() {
		return val;
	}
}
