package VMtranslation;

import java.util.ArrayList;

public class Parser {
	
	private Codewriter cw;
	
	public Parser(String filename) {
		cw = new Codewriter(filename);
	}
	
	public ArrayList<String> init() {
		return cw.writeInit();
	}
	
	public ArrayList<String> analyzeLine(String s) {
		if ( s.replaceAll("\\s*", "").length()==0 ) { // empty line
			return null;
		}
		String[] subarray = s.trim().split(" |\t");
		if ( subarray[0].charAt(0)=='/' && subarray[0].charAt(1)=='/' ) { // comment
			return null;
		}
		else if ( subarray[0].equals("push") ) { // push command
			return cw.writePush(subarray[1], subarray[2]);
		}
		else if ( subarray[0].equals("pop") ) { // pop command
			return cw.writePop(subarray[1], subarray[2]);
		}
		else if ( subarray[0].equals("label") ) { // label command
			return cw.writeLable(subarray[1]);
		}
		else if ( subarray[0].equals("goto") ) { // goto command
			return cw.writeGoto(subarray[1]);
		}
		else if ( subarray[0].equals("if-goto") ) { // if-goto command
			return cw.writeIf(subarray[1]);
		}
		else if ( subarray[0].equals("function") ) { // function command
			return cw.writeFunction(subarray[1], subarray[2]);
		}
		else if ( subarray[0].equals("call") ) { // call command
			return cw.writeCall(subarray[1], subarray[2]);
		}
		else if ( subarray[0].equals("return") ) { // return command
			return cw.writeReturn();
		}
		else { // arithmetic command
			return cw.writeArithmatic(subarray[0]);
		}
	}

}
