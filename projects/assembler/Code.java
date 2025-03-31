package assembler;

public class Code {
	
	public StringBuffer dest(String s) {
		StringBuffer sb = new StringBuffer();
		if ( s==null ) {
			sb.append("000");
			return sb;
		}
		switch (s) {
		case "M":
			sb.append("001");
			break;
		case "D":
			sb.append("010");
			break;
		case "MD":
			sb.append("011");
			break;
		case "A":
			sb.append("100");
			break;
		case "AM":
			sb.append("101");
			break;
		case "AD":
			sb.append("110");
			break;
		case "AMD":
			sb.append("111");
			break;
		}
		return sb;
	}
	
	public StringBuffer comp(String s) {
		StringBuffer sb = new StringBuffer();
		switch (s) {
		case "0":
			sb.append("0101010");
			break;
		case "1":
			sb.append("0111111");
			break;
		case "-1":
			sb.append("0111010");
			break;
		case "D":
			sb.append("0001100");
			break;
		case "A":
			sb.append("0110000");
			break;
		case "M":
			sb.append("1110000");
			break;
		case "!D":
			sb.append("0001101");
			break;
		case "!A":
			sb.append("0110001");
			break;
		case "!M":
			sb.append("1110001");
			break;
		case "-D":
			sb.append("0001111");
			break;
		case "-A":
			sb.append("0110011");
			break;
		case "-M":
			sb.append("1110011");
			break;
		case "D+1":
			sb.append("0011111");
			break;
		case "A+1":
			sb.append("0110111");
			break;
		case "M+1":
			sb.append("1110111");
			break;
		case "D-1":
			sb.append("0001110");
			break;
		case "A-1":
			sb.append("0110010");
			break;
		case "M-1":
			sb.append("1110010");
			break;
		case "D+A":
			sb.append("0000010");
			break;
		case "D+M":
			sb.append("1000010");
			break;
		case "D-A":
			sb.append("0010011");
			break;
		case "D-M":
			sb.append("1010011");
			break;
		case "A-D":
			sb.append("0000111");
			break;
		case "M-D":
			sb.append("1000111");
			break;
		case "D&A":
			sb.append("0000000");
			break;
		case "D&M":
			sb.append("1000000");
			break;
		case "D|A":
			sb.append("0010101");
			break;
		case "D|M":
			sb.append("1010101");
			break;
		}
		return sb;
	}
	
	public StringBuffer jump(String s) {
		StringBuffer sb = new StringBuffer();
		if ( s==null ) {
			sb.append("000");
			return sb;
		}
		switch (s) {
		case "JGT":
			sb.append("001");
			break;
		case "JEQ":
			sb.append("010");
			break;
		case "JGE":
			sb.append("011");
			break;
		case "JLT":
			sb.append("100");
			break;
		case "JNE":
			sb.append("101");
			break;
		case "JLE":
			sb.append("110");
			break;
		case "JMP":
			sb.append("111");
			break;
		}
		return sb;
	}
	
}
