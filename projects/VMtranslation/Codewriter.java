package VMtranslation;

import java.util.ArrayList;

public class Codewriter {
	
	private String filename;
	private int labeltimes = 0;
	private static int linenumber = 0;
	private ArrayList<String> push = new ArrayList<String>();
	private ArrayList<String> pop = new ArrayList<String>();
	
	public Codewriter(String filename) {
		this.filename = filename;
		push.add("@SP");
		push.add("A=M");
		push.add("M=D");
		push.add("@SP");
		push.add("M=M+1");
		pop.add("@SP");
		pop.add("M=M-1");
		pop.add("A=M");
		pop.add("D=M");
	}
	
	public ArrayList<String> writeInit() {
		ArrayList<String> command = new ArrayList<String>();
		command.add("// bootstrap");
		command.add("@256");
		command.add("D=A");
		command.add("@SP");
		command.add("M=D");
		linenumber += 4;
		command.addAll(writeCall("Sys.init", "0"));
		return command;
	}
	
	public ArrayList<String> writePush(String s1, String s2) {
		ArrayList<String> command = new ArrayList<String>();
		String s3 = "@" + s2;
		String s4 = "@" + filename + "." + s2;
		command.add("// push " + s1 + " " + s2);
		switch(s1) {
		case "local":
			command.add(s3);
			command.add("D=A");
			command.add("@LCL");
			command.add("A=D+M");
			command.add("D=M");
			command.addAll(push);
			linenumber += 10;
			return command;
		case "argument":
			command.add(s3);
			command.add("D=A");
			command.add("@ARG");
			command.add("A=D+M");
			command.add("D=M");
			command.addAll(push);
			linenumber += 10;
			return command;
		case "this":
			command.add(s3);
			command.add("D=A");
			command.add("@THIS");
			command.add("A=D+M");
			command.add("D=M");
			command.addAll(push);
			linenumber += 10;
			return command;
		case "that":
			command.add(s3);
			command.add("D=A");
			command.add("@THAT");
			command.add("A=D+M");
			command.add("D=M");
			command.addAll(push);
			linenumber += 10;
			return command;
		case "constant":
			command.add(s3);
			command.add("D=A");
			command.addAll(push);
			linenumber += 7;
			return command;
		case "static":
			command.add(s4);
			command.add("D=M");
			command.addAll(push);
			linenumber += 7;
			return command;
		case "temp":
			command.add(s3);
			command.add("D=A");
			command.add("@5");
			command.add("A=D+A");
			command.add("D=M");
			command.addAll(push);
			linenumber += 10;
			return command;
		case "pointer":
			if ( s2.equals("0") ) {
				command.add("@THIS");
			}
			else {
				command.add("@THAT");
			}
			command.add("D=M");
			command.addAll(push);
			linenumber += 7;
			return command;
		default:
			return null;
		}
	}
	
	public ArrayList<String> writePop(String s1, String s2) {
		ArrayList<String> command = new ArrayList<String>();
		String s3 = "@" + s2;
		String s4 = "@" + filename + "." + s2;
		command.add("// pop " + s1 + " " + s2);
		switch(s1) {
		case "local":
			command.add(s3);
			command.add("D=A");
			command.add("@LCL");
			command.add("D=D+M");
			command.add("@addr");
			command.add("M=D");
			command.addAll(pop);
			command.add("@addr");
			command.add("A=M");
			command.add("M=D");
			linenumber += 13;
			return command;
		case "argument":
			command.add(s3);
			command.add("D=A");
			command.add("@ARG");
			command.add("D=D+M");
			command.add("@addr");
			command.add("M=D");
			command.addAll(pop);
			command.add("@addr");
			command.add("A=M");
			command.add("M=D");
			linenumber += 13;
			return command;
		case "this":
			command.add(s3);
			command.add("D=A");
			command.add("@THIS");
			command.add("D=D+M");
			command.add("@addr");
			command.add("M=D");
			command.addAll(pop);
			command.add("@addr");
			command.add("A=M");
			command.add("M=D");
			linenumber += 13;
			return command;
		case "that":
			command.add(s3);
			command.add("D=A");
			command.add("@THAT");
			command.add("D=D+M");
			command.add("@addr");
			command.add("M=D");
			command.addAll(pop);
			command.add("@addr");
			command.add("A=M");
			command.add("M=D");
			linenumber += 13;
			return command;
		case "static":
			command.addAll(pop);
			command.add(s4);
			command.add("M=D");
			linenumber += 6;
			return command;
		case "temp":
			command.add(s3);
			command.add("D=A");
			command.add("@5");
			command.add("D=D+A");
			command.add("@addr");
			command.add("M=D");
			command.addAll(pop);
			command.add("@addr");
			command.add("A=M");
			command.add("M=D");
			linenumber += 13;
			return command;
		case "pointer":
			command.addAll(pop);
			if ( s2.equals("0") ) {
				command.add("@THIS");
			}
			else {
				command.add("@THAT");
			}
			command.add("M=D");
			linenumber += 6;
			return command;
		default:
			return null;
		}
	}
	
	public ArrayList<String> writeArithmatic(String s) {
		ArrayList<String> command = new ArrayList<String>();
		String s1 = "(" + filename +".TRUE_" + labeltimes +")";
		String s2 = "@" + filename + ".TRUE_" + labeltimes;
		String s3 = "(" + filename +".END_" + labeltimes +")";
		String s4 = "@" + filename + ".END_" + labeltimes;
		command.add("// " + s);
		switch (s) {
		case "add":
			command.addAll(pop);
			command.add("@tmp");
			command.add("M=D");
			command.addAll(pop);
			command.add("@tmp");
			command.add("D=D+M");
			command.addAll(push);
			linenumber += 17;
			return command;
		case "sub":
			command.addAll(pop);
			command.add("@tmp");
			command.add("M=D");
			command.addAll(pop);
			command.add("@tmp");
			command.add("D=D-M");
			command.addAll(push);
			linenumber += 17;
			return command;
		case "neg":
			command.addAll(pop);
			command.add("D=-D");
			command.addAll(push);
			linenumber += 10;
			return command;
		case "eq":
			command.addAll(pop);
			command.add("@tmp");
			command.add("M=D");
			command.addAll(pop);
			command.add("@tmp");
			command.add("D=D-M");
			command.add(s2);
			command.add("D;JEQ");
			command.add("D=0");
			command.add(s4);
			command.add("0;JMP");
			command.add(s1);
			command.add("D=-1");
			command.add(s3);
			command.addAll(push);
			labeltimes++;
			linenumber += 23;
			return command;
		case "gt":
			command.addAll(pop);
			command.add("@tmp");
			command.add("M=D");
			command.addAll(pop);
			command.add("@tmp");
			command.add("D=D-M");
			command.add(s2);
			command.add("D;JGT");
			command.add("D=0");
			command.add(s4);
			command.add("0;JMP");
			command.add(s1);
			command.add("D=-1");
			command.add(s3);
			command.addAll(push);
			labeltimes++;
			linenumber += 23;
			return command;
		case "lt":
			command.addAll(pop);
			command.add("@tmp");
			command.add("M=D");
			command.addAll(pop);
			command.add("@tmp");
			command.add("D=D-M");
			command.add(s2);
			command.add("D;JLT");
			command.add("D=0");
			command.add(s4);
			command.add("0;JMP");
			command.add(s1);
			command.add("D=-1");
			command.add(s3);
			command.addAll(push);
			labeltimes++;
			linenumber += 23;
			return command;
		case "and":
			command.addAll(pop);
			command.add("@tmp");
			command.add("M=D");
			command.addAll(pop);
			command.add("@tmp");
			command.add("D=D&M");
			command.addAll(push);
			linenumber += 17;
			return command;
		case "or":
			command.addAll(pop);
			command.add("@tmp");
			command.add("M=D");
			command.addAll(pop);
			command.add("@tmp");
			command.add("D=D|M");
			command.addAll(push);
			linenumber += 17;
			return command;
		case "not":
			command.addAll(pop);
			command.add("D=!D");
			command.addAll(push);
			linenumber += 10;
			return command;
		default:
			return null;
		}
	}
	
	public ArrayList<String> writeLable(String s) {
		ArrayList<String> command = new ArrayList<String>();
		command.add("// label " + s);
		command.add("(" + filename + "." + s + ")");
		return command;
	}
	
	public ArrayList<String> writeGoto(String s) {
		ArrayList<String> command = new ArrayList<String>();
		command.add("// goto " + s);
		command.add("@" + filename + "." + s);
		command.add("0;JMP");
		linenumber += 2;
		return command;
	}
	
	public ArrayList<String> writeIf(String s) {
		ArrayList<String> command = new ArrayList<String>();
		command.add("// if-goto " + s);
		command.addAll(pop);
		command.add("@" + filename + "." + s);
		command.add("D;JNE");
		linenumber += 6;
		return command;
	}
	
	public ArrayList<String> writeFunction(String s1, String s2) {
		ArrayList<String> command = new ArrayList<String>();
		command.add("// function " + s1 + " " + s2);
		command.add("("+ s1 + ")");
		command.add("@tmp");
		command.add("M=0");
		command.add("(" + filename + "." + "LOOP_" + labeltimes + ")");
		command.add("@tmp");
		command.add("D=M");
		command.add("@" + s2);
		command.add("D=D-A");
		command.add("@" + filename + "." + "END_" + labeltimes);
		command.add("D;JEQ");
		command.add("D=0");
		command.addAll(push);
		command.add("@tmp");
		command.add("M=M+1");
		command.add("@" + filename + "." + "LOOP_" + labeltimes);
		command.add("0;JMP");
		command.add("(" + filename + "." + "END_" + labeltimes + ")");
		labeltimes++;
		linenumber += 18;
		return command;
	}
	
	public ArrayList<String> writeCall(String s1, String s2) {
		ArrayList<String> command = new ArrayList<String>();
		linenumber += 49;
		command.add("// call " + s1 + " " + s2);
		command.add("@" + linenumber);
		command.add("D=A");
		command.addAll(push);
		command.add("@LCL");
		command.add("D=M");
		command.addAll(push);
		command.add("@ARG");
		command.add("D=M");
		command.addAll(push);
		command.add("@THIS");
		command.add("D=M");
		command.addAll(push);
		command.add("@THAT");
		command.add("D=M");
		command.addAll(push);
		command.add("@SP");
		command.add("D=M");
		command.add("@" + s2);
		command.add("D=D-A");
		command.add("@5");
		command.add("D=D-A");
		command.add("@ARG");
		command.add("M=D");
		command.add("@SP");
		command.add("D=M");
		command.add("@LCL");
		command.add("M=D");
		command.add("@"+ s1);
		command.add("0;JMP");
		return command;
	}
	
	public ArrayList<String> writeReturn() {
		ArrayList<String> command = new ArrayList<String>();
		command.add("// return");
		command.add("@LCL");
		command.add("D=M");
		command.add("@tmp");
		command.add("M=D");
		command.add("@5");
		command.add("A=D-A");
		command.add("D=M");
		command.add("@ret");
		command.add("M=D");
		command.addAll(pop);
		command.add("@ARG");
		command.add("A=M");
		command.add("M=D");
		command.add("@ARG");
		command.add("D=M");
		command.add("@SP");
		command.add("M=D+1");
		command.add("@tmp");
		command.add("D=M");
		command.add("@1");
		command.add("A=D-A");
		command.add("D=M");
		command.add("@THAT");
		command.add("M=D");
		command.add("@tmp");
		command.add("D=M");
		command.add("@2");
		command.add("A=D-A");
		command.add("D=M");
		command.add("@THIS");
		command.add("M=D");
		command.add("@tmp");
		command.add("D=M");
		command.add("@3");
		command.add("A=D-A");
		command.add("D=M");
		command.add("@ARG");
		command.add("M=D");
		command.add("@tmp");
		command.add("D=M");
		command.add("@4");
		command.add("A=D-A");
		command.add("D=M");
		command.add("@LCL");
		command.add("M=D");
		command.add("@ret");
		command.add("A=M");
		command.add("0;JMP");
		linenumber += 51;
		return command;
	}

}
