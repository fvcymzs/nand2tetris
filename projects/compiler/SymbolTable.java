package compiler;

import java.util.HashMap;

public class SymbolTable {
	private HashMap<String, Object[]> classTable;
	private HashMap<String, Object[]> methodTable;
	private int staticIndex;
	private int fieldIndex;
	private int argIndex;
	private int varIndex;
	// class : static | field
	// method : argument | var
	
	public SymbolTable() {
		classTable = new HashMap<String, Object[]>();
		methodTable = new HashMap<String, Object[]>();
		staticIndex = 0;
		fieldIndex = 0;
		argIndex = 0;
		varIndex = 0;
	}
	
	public void startSubroutine() {
		HashMap<String, Object[]> bufTable = new HashMap<String, Object[]>();
		methodTable = bufTable;
		argIndex = 0;
		varIndex = 0;
	}
	
	public void define(String name, String type, String kind) {
		switch (kind) {
		case "static":
			classTable.put(name, new Object[] {type, kind, staticIndex++});
			break;
		case "field":
			classTable.put(name, new Object[] {type, kind, fieldIndex++});
			break;
		case "argument":
			methodTable.put(name, new Object[] {type, kind, argIndex++});
			break;
		case "var":
			methodTable.put(name, new Object[] {type, kind, varIndex++});
			break;
		default:
			break;
		}
	}
	
	public int varCount(String kind) {
		switch (kind) {
		case "static":
			return staticIndex;
		case "field":
			return fieldIndex;
		case "argument":
			return argIndex;
		case "var":
			return varIndex;
		default:
			return -1;
		}
	}
	
	public String typeOf(String name) {
		if (methodTable.containsKey(name)) {
			return (String)methodTable.get(name)[0];
		}
		else if (classTable.containsKey(name)) {
			return (String)classTable.get(name)[0];
		}
		else {
			return null;
		}
	}
	
	public String kindOf(String name) {
		if (methodTable.containsKey(name)) {
			String s = (String)methodTable.get(name)[1];
			if (s.equals("field")) {
				return "this";
			}
			else if (s.equals("var")) {
				return "local";
			}
			else {
				return s;
			}
		}
		else if (classTable.containsKey(name)) {
			String s = (String)classTable.get(name)[1];
			if (s.equals("field")) {
				return "this";
			}
			else if (s.equals("var")) {
				return "local";
			}
			else {
				return s;
			}
		}
		else {
			return null;
		}
	}
	
	public int indexOf(String name) {
		if (methodTable.containsKey(name)) {
			return (int)methodTable.get(name)[2];
		}
		else if (classTable.containsKey(name)) {
			return (int)classTable.get(name)[2];
		}
		else {
			return -1;
		}
	}
	
	public boolean contains(String name) {
		if (methodTable.containsKey(name)) {
			return true;
		}
		else if (classTable.containsKey(name)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public int classSize() {
		return classTable.size();
	}
}
