package compiler;

import java.io.BufferedReader;
import java.io.IOException;

public class JackTokenizer {
	private BufferedReader in;
	private int chnumber;
	private String line;
	private String token;
	private String tokenAhead;
	private String tokenType;
	private String tokenAheadType;
	
	public JackTokenizer(BufferedReader in) {
		this.in = in;
		line = "";
		tokenAhead = null;
		tokenAheadType = null;
	}
	
	public boolean checkNewLine() { // advance to a non empty line
		boolean advance = true;
		try {
			if (chnumber >= line.length()) { // check if need to read new line
				if ((line = in.readLine()) == null) {
					token = tokenAhead;
					tokenType = tokenAheadType;
					advance = false;
				}
				else {
					chnumber = 0;
					while (line.isBlank()) {
						if ((line = in.readLine()) == null) {
							token = tokenAhead;
							tokenType = tokenAheadType;
							advance = false;
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return advance;
	}
	
	public boolean advance() {
		boolean advance = true;
		advance = checkNewLine();
		if (advance) { // ignore whitespace
			while (line.charAt(chnumber)==' ' || line.charAt(chnumber)=='\t') {
				chnumber++;
				advance = checkNewLine();
				if (!advance) {
					break;
				}
			}
		}
		if (advance) { // check comment
			checkcomment:
			while (line.charAt(chnumber)=='/') {
				if (line.charAt(chnumber+1)=='/') { // "//..." comment
					chnumber = line.length();
					advance = checkNewLine();
					if (!advance) {
						break;
					}
					while (line.charAt(chnumber)==' ' || line.charAt(chnumber)=='\t') { // ignore whitespace
						chnumber++;
						advance = checkNewLine();
						if (!advance) {
							break;
						}
					}
				}
				else if (line.charAt(chnumber+1)=='*') { // "/*...*/" comment
					while (chnumber < line.length()) {
						if ((chnumber>0) && (line.charAt(chnumber)=='/') && (line.charAt(chnumber-1)=='*')) {
							chnumber++;
							advance = checkNewLine();
							while (line.charAt(chnumber)==' ' || line.charAt(chnumber)=='\t') {
								chnumber++;
								advance = checkNewLine();
								if (!advance) {
									break;
								}
							}
							break;
						}
						else {
							chnumber++;
							advance = checkNewLine();
							if (!advance) {
								break checkcomment;
							}
							while (line.charAt(chnumber)==' ' || line.charAt(chnumber)=='\t') {
								chnumber++;
								advance = checkNewLine();
								if (!advance) {
									break;
								}
							}
						}
					}
				}
				else { // no comment
					break;
				}
			}
		}
		if (advance) { // read token
			char c = line.charAt(chnumber);
			StringBuffer sb = new StringBuffer();
			if (c == '"') { // stringConstant
				while (true) {
					chnumber++;
					c = line.charAt(chnumber);
					if (c != '"') {
						sb.append(c);
					}
					else {
						break;
					}
				}
				chnumber++;
				token = tokenAhead;
				tokenType = tokenAheadType;
				tokenAhead = sb.toString();
				tokenAheadType = "stringConstant";
			}
			else if (c >= '0' && c <= '9') { // integerConstant
				do {
					sb.append(c);
					chnumber++;
					c = line.charAt(chnumber);
				} while (c >= '0' && c <= '9');
				token = tokenAhead;
				tokenType = tokenAheadType;
				tokenAhead = sb.toString();
				tokenAheadType = "integerConstant";
			}
			else if (c=='{' | c=='}' | c=='[' | c==']' | c=='(' | c==')' | c=='.' |
					 c==',' | c==';' | c=='+' | c=='-' | c=='*' | c=='/' | c=='&' |
					 c=='|' | c=='<' | c=='>' | c=='=' | c=='~') { // symbol
				sb.append(c);
				chnumber++;
				token = tokenAhead;
				tokenType = tokenAheadType;
				tokenAhead = sb.toString();
				tokenAheadType = "symbol";
			}
			else { // identifier or keyword
				do {
					sb.append(c);
					chnumber++;
					c = line.charAt(chnumber);
				} while ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_');
				token = tokenAhead;
				tokenType = tokenAheadType;
				tokenAhead = sb.toString();
				switch(tokenAhead) {
				case "class": case "constructor": case "function": case "method":
				case "field": case "static": case "var": case "int": case "char":
				case "boolean": case "void": case "true": case "false":
				case "null": case "this": case "let": case "do": case "if":
				case "else": case "while": case "return":
					tokenAheadType = "keyword";
					break;
				default:
					tokenAheadType = "identifier";
				}
			}
		}
		return advance;
	}
	
	public String token() {
		return token;
	}
	
	public String tokenAhead() {
		return tokenAhead;
	}
	
	public String tokenType() {
		return tokenType;
	}
	
	public String tokenAheadType() {
		return tokenAheadType;
	}
}
