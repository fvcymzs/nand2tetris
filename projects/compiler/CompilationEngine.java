package compiler;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class CompilationEngine {
	private BufferedReader in;
	private PrintWriter out;
	private SymbolTable st;
	private JackTokenizer jack;
	private VMWriter wt;
	// variable to store define message
	private String className;
	private String namebuf;
	private String typebuf;
	private String kindbuf;
	// variable about current compiled subroutine
	private String subKind;
	private String subName;
	private boolean isVoid;
	// variable to store expression message
	private String varName;
	private String funcName;
	private boolean isArray;
	private int expLNum;
	// variable for if/while statements
	int ifLabel;
	int whileLabel;
	
	public CompilationEngine(BufferedReader in, PrintWriter out) {
		this.in = in;
		this.out = out;
		st = new SymbolTable();
		jack = new JackTokenizer(in);
		jack.advance();
		wt = new VMWriter(out);
		ifLabel = 0;
		whileLabel = 0;
	}
	
	public void compileClass() {
		// class
		jack.advance();
		// className
		jack.advance();
		className = jack.token();
		st.define("this", className, "argument");
		// {
		jack.advance();
		// classVarDec*
		while (jack.tokenAhead().equals("static") || jack.tokenAhead().equals("field")) {
			compileClassVarDec();
		}
		// subroutine*
		while (jack.tokenAhead().equals("constructor") || jack.tokenAhead().equals("function") ||
			   jack.tokenAhead().equals("method")) {
			compileSubroutineDec();
		}
		// }
		jack.advance();
	}
	
	public void compileClassVarDec() {
		// static | field
		jack.advance();
		kindbuf = jack.token();
		// type
		jack.advance();
		typebuf = jack.token();
		// varName
		jack.advance();
		namebuf = jack.token();
		st.define(namebuf, typebuf, kindbuf);
		// ,varName*
		while (jack.tokenAhead().equals(",")) {
			// ,
			jack.advance();
			// varName
			jack.advance();
			namebuf = jack.token();
			st.define(namebuf, typebuf, kindbuf);
		}
		// ;
		jack.advance();
	}
	
	public void compileSubroutineDec() {
		isVoid = false;
		st.startSubroutine();
		// constructor | function | method
		jack.advance();
		subKind = jack.token();
		if (subKind.equals("method")) {
			st.define("this", className, "argument");
		}
		// void | type
		jack.advance();
		if (jack.token().equals("void")) {
			isVoid = true;
		}
		// subroutineName
		jack.advance();
		subName = jack.token();
		// (
		jack.advance();
		// parameterList
		compileParameterList();
		// )
		jack.advance();
		// subroutineBody
		compileSubroutineBody();
	}
	
	public void compileParameterList() {
		kindbuf = "argument";
		if (!jack.tokenAheadType().equals("symbol")) {
			// type
			jack.advance();
			typebuf = jack.token();
			// varName
			jack.advance();
			namebuf = jack.token();
			st.define(namebuf, typebuf, kindbuf);
			// (,type varName)*
			while(jack.tokenAhead().equals(",")) {
				// ,
				jack.advance();
				// type
				jack.advance();
				typebuf = jack.token();
				// varName
				jack.advance();
				namebuf = jack.token();
				st.define(namebuf, typebuf, kindbuf);
			}
		}
	}
	
	public void compileSubroutineBody() {
		// {
		jack.advance();
		// varDec*
		while (jack.tokenAhead().equals("var")) {
			compileVarDec();
		}
		wt.writeFunction(className + "." + subName, st.varCount("var"));
		switch (subKind) {
		case "constructor":
			wt.writePush("constant", st.classSize());
			wt.writeCall("Memory.alloc", 1);
			wt.writePop("pointer", 0);
			break;
		case "method":
			wt.writePush("argument", 0);
			wt.writePop("pointer", 0);
			break;
		default:
			break;
		}
		// statements
		compileStatements();
		// }
		jack.advance();
	}
	
	public void compileVarDec() {
		kindbuf = "var";
		// var
		jack.advance();
		// type
		jack.advance();
		typebuf = jack.token();
		// varName
		jack.advance();
		namebuf = jack.token();
		st.define(namebuf, typebuf, kindbuf);
		// ,varName*
		while (jack.tokenAhead().equals(",")) {
			// ,
			jack.advance();
			// varName
			jack.advance();
			namebuf = jack.token();
			st.define(namebuf, typebuf, kindbuf);
		}
		// ;
		jack.advance();
	}
	
	public void compileStatements() {
		while (jack.tokenAhead().equals("let") || jack.tokenAhead().equals("if") ||
			   jack.tokenAhead().equals("while") || jack.tokenAhead().equals("do") ||
			   jack.tokenAhead().equals("return")) {
			switch (jack.tokenAhead()) {
			case "let":
				compileLet();
				break;
			case "if":
				compileIf();
				break;
			case "while":
				compileWhile();
				break;
			case "do":
				compileDo();
				break;
			case "return":
				compileReturn();
				break;
			}
		}
	}
	
	public void compileLet() {
		isArray = false;
		// let
		jack.advance();
		// varName
		jack.advance();
		varName = jack.token();
		// [expression]?
		if (jack.tokenAhead().equals("[")) {
			isArray = true;
			wt.writePush(st.kindOf(varName), st.indexOf(varName));
			// [
			jack.advance();
			// expression
			compileExpression();
			wt.writeArithmetic("add");
			// ]
			jack.advance();
		}
		// =
		jack.advance();
		// expression
		compileExpression();
		if (isArray) {
			wt.writePop("temp", 0);
			wt.writePop("pointer", 1);
			wt.writePush("temp", 0);
			wt.writePop("that", 0);
		}
		else {
			wt.writePop(st.kindOf(varName), st.indexOf(varName));
		}
		// ;
		jack.advance();
	}
	
	public void compileIf() {
		int thisIfLabel = ifLabel++;
		// if
		jack.advance();
		// (
		jack.advance();
		// expression
		compileExpression();
		wt.writeArithmetic("not");
		wt.writeIf("IF_FALSE" + thisIfLabel);
		// )
		jack.advance();
		// {
		jack.advance();
		// statements
		compileStatements();
		wt.writeGoto("IF_TRUE" + thisIfLabel);
		wt.writeLabel("IF_FALSE" + thisIfLabel);
		// }
		jack.advance();
		// else{statements}*
		if (jack.tokenAhead().equals("else")) {
			// else
			jack.advance();
			// {
			jack.advance();
			// statements
			compileStatements();
			// }
			jack.advance();
		}
		wt.writeLabel("IF_TRUE" + thisIfLabel);
	}
	
	public void compileWhile() {
		//WHILE_EXP0
		//WHILE_END0
		int thisWhileLabel = whileLabel++;
		// while
		jack.advance();
		wt.writeLabel("WHILE_EXP" + thisWhileLabel);
		// (
		jack.advance();
		// expression
		compileExpression();
		wt.writeArithmetic("not");
		wt.writeIf("WHILE_END" + thisWhileLabel);
		// )
		jack.advance();
		// {
		jack.advance();
		// statements
		compileStatements();
		wt.writeGoto("WHILE_EXP" + thisWhileLabel);
		wt.writeLabel("WHILE_END" + thisWhileLabel);
		// }
		jack.advance();
	}
	
	public void compileDo() {
		// do
		jack.advance();
		// variousName
		jack.advance();
		if (jack.tokenAhead().equals("(")) { // i(expL)
			funcName = className + "." + jack.token();
			// (
			jack.advance();
			// expression
			expLNum = 1; // need to push self first
			wt.writePush("pointer", 0);
			compileExpressionList();
			wt.writeCall(funcName, expLNum);
			// )
			jack.advance();
		}
		else { // i.i(expL)
			funcName = jack.token();
			if (st.contains(jack.token())) {
				wt.writePush(st.kindOf(jack.token()), st.indexOf(jack.token()));
				expLNum = 1;
			}
			else {
				expLNum = 0;
			}
			// .
			jack.advance();
			// identifier
			jack.advance();
			if (expLNum == 1) {
				funcName = st.typeOf(funcName) + "." + jack.token();
			}
			else {
				funcName = funcName + "." + jack.token();
			}
			// (
			jack.advance();
			// expression
			compileExpressionList();
			wt.writeCall(funcName, expLNum);
			// )
			jack.advance();
		}
		wt.writePop("temp", 0); // ignore return value
		// ;
		jack.advance();
	}
	
	public void compileReturn() {
		// return
		jack.advance();
		// expression?
		if (!jack.tokenAhead().equals(";")) {
			compileExpression();
		}
		if (isVoid) {
			wt.writePush("constant", 0);
		}
		wt.writeReturn();
		// ;
		jack.advance();
	}
	
	public void compileExpression() {
		int opType = 0;
		// term
		compileTerm();
		// (op term)*
		while (jack.tokenAhead().equals("+") || jack.tokenAhead().equals("-") ||
			   jack.tokenAhead().equals("*") || jack.tokenAhead().equals("/") ||
			   jack.tokenAhead().equals("&") || jack.tokenAhead().equals("|") ||
			   jack.tokenAhead().equals("<") || jack.tokenAhead().equals(">") ||
			   jack.tokenAhead().equals("=")) {
			// op
			jack.advance();
			switch(jack.token()) {
			case "+":
				opType = 1;
				break;
			case "-":
				opType = 2;
				break;
			case "*":
				opType = 3;
				break;
			case "/":
				opType = 4;
				break;
			case "&":
				opType = 5;
				break;
			case "|":
				opType = 6;
				break;
			case "<":
				opType = 7;
				break;
			case ">":
				opType = 8;
				break;
			case "=":
				opType = 9;
				break;
			}
			// term
			compileTerm();
			switch (opType) {
			case 1:
				wt.writeArithmetic("add");
				break;
			case 2:
				wt.writeArithmetic("sub");
				break;
			case 3:
				wt.writeCall("Math.multiply", 2);
				break;
			case 4:
				wt.writeCall("Math.divide", 2);
				break;
			case 5:
				wt.writeArithmetic("and");
				break;
			case 6:
				wt.writeArithmetic("or");
				break;
			case 7:
				wt.writeArithmetic("lt");
				break;
			case 8:
				wt.writeArithmetic("gt");
				break;
			case 9:
				wt.writeArithmetic("eq");
				break;
			}
		}
	}
	
	public void compileExpressionList() {
		if (!jack.tokenAhead().equals(")")) {
			// expression
			compileExpression();
			expLNum++;
			// ,expression*
			while (jack.tokenAhead().equals(",")) {
				// ,
				jack.advance();
				// expression
				compileExpression();
				expLNum++;
			}
		}
	}
	
	public void compileTerm() {
		if (jack.tokenAhead().equals("-") | jack.tokenAhead().equals("~")) { // -|~ term
			// -|~
			jack.advance();
			boolean isNeg = false;
			if (jack.token().equals("-")) {
				isNeg = true;
			}
			// term
			compileTerm();
			if (isNeg) {
				wt.writeArithmetic("neg");
			}
			else {
				wt.writeArithmetic("not");
			}
		}
		else if (jack.tokenAhead().equals("(")) { // (expression)
			// (
			jack.advance();
			// expression
			compileExpression();
			// )
			jack.advance();
		}
		else if (jack.tokenAheadType().equals("identifier")) { // i | i[exp] | i(expL) | i.i(expL)
			// identifier
			jack.advance();
			if (jack.tokenAhead().equals("[")) { // i[exp]
				wt.writePush(st.kindOf(jack.token()), st.indexOf(jack.token()));
				// [
				jack.advance();
				// expression
				compileExpression();
				wt.writeArithmetic("add");
				wt.writePop("pointer", 1);
				wt.writePush("that", 0);
				// ]
				jack.advance();
			}
			else if (jack.tokenAhead().equals("(")) { // i(expL)
				funcName = className + "." + jack.token();
				// (
				jack.advance();
				// expression
				expLNum = 1; // need to push self first
				wt.writePush("pointer", 0);
				compileExpressionList();
				wt.writeCall(funcName, expLNum);
				// )
				jack.advance();
			}
			else if (jack.tokenAhead().equals(".")) { // i.i(expL)
				funcName = jack.token();
				if (st.contains(jack.token())) {
					wt.writePush(st.kindOf(jack.token()), st.indexOf(jack.token()));
					expLNum = 1;
				}
				else {
					expLNum = 0;
				}
				// .
				jack.advance();
				// identifier
				jack.advance();
				if (expLNum == 1) {
					funcName = st.typeOf(funcName) + "." + jack.token();
				}
				else {
					funcName = funcName + "." + jack.token();
				}
				// (
				jack.advance();
				// expression
				compileExpressionList();
				wt.writeCall(funcName, expLNum);
				// )
				jack.advance();
			}
			else { // i
				wt.writePush(st.kindOf(jack.token()), st.indexOf(jack.token()));
			}
		}
		else { // constant
			jack.advance();
			if (jack.tokenType().equals("integerConstant")) { // integer constant
				wt.writePush("constant", Integer.parseInt(jack.token()));
			}
			else if (jack.tokenType().equals("keyword")) { // keyword constant
				switch (jack.token()) {
				case "true":
					wt.writePush("constant", 0);
					wt.writeArithmetic("not");
					break;
				case "false":
					wt.writePush("constant", 0);
					break;
				case "null":
					wt.writePush("constant", 0);
					break;
				case "this":
					wt.writePush("pointer", 0);
					break;
				}
			}
			else { // string constant
				wt.writePush("constant", jack.token().length());
				wt.writeCall("String.new", 1);
				for (int i = 0; i < jack.token().length(); i++) {
					wt.writePush("constant", (int)jack.token().charAt(i));
					wt.writeCall("String.appendChar", 2);
				}
			}
		}
	}
}
