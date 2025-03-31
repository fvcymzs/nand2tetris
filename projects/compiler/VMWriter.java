package compiler;

import java.io.PrintWriter;

public class VMWriter {
	private PrintWriter out;
	
	public VMWriter(PrintWriter out) {
		this.out = out;
	}
	
	public void writePush(String kind, int index) {
		out.println("push " + kind + " " + index);
	}
	
	public void writePop(String kind, int index) {
		out.println("pop " + kind + " " + index);
	}
	
	public void writeArithmetic(String command) {
		out.println(command);
	}
	
	public void writeLabel(String label) {
		out.println("label " + label);
	}
	
	public void writeGoto(String label) {
		out.println("goto " + label);
	}
	
	public void writeIf(String label) {
		out.println("if-goto " + label);
	}
	
	public void writeCall(String name, int arg) {
		out.println("call " + name + " " + arg);
	}
	
	public void writeFunction(String name, int arg) {
		out.println("function " + name + " " + arg);
	}
	
	public void writeReturn() {
		out.println("return");
	}
}
