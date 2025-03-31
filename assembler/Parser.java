package assembler;

public class Parser {
	
	private int linenumber = 0;
	private int symboladdress = 16;
	private Code code = new Code();
	private SymbolTable symboltable = new SymbolTable();
	
	public String analyzeLine(String line) {
		String sub = line.replaceAll("\\s*", "");
		if ( sub.length()==0 ) { // Empty line
			return null;
		}
		if ( sub.charAt(0)=='/' && sub.charAt(1)=='/' ) { // Comment
			return null;
		}
		if ( sub.charAt(0)=='(' ) { // Label
			return null;
		}
		if ( sub.charAt(0)=='@' ) { // A-Command
			String asub = sub.substring(1);
			Integer i = 0;
			if ( asub.charAt(0)>='0' && asub.charAt(0)<='9' ) { // no symbol
				i = Integer.valueOf(asub);
			}
			else if ( symboltable.contains(asub) ) { // label
				i = symboltable.getAddress(asub);
			}
			else { // variable
				i = symboladdress;
				symboltable.addSymbol(asub, i);
				symboladdress++;
			}
			StringBuffer sbnum = new StringBuffer(Integer.toBinaryString(i));
			while ( sbnum.length()<16 ) {
				sbnum.insert(0, '0');
			}
			return sbnum.toString();
		}
		else { // C-Command
			String[] spt = sub.split("=|;");
			String[] sptr = new String[3];
			if ( spt.length==3 ) { // dest=comp;jump
				sptr[0] = spt[1];
				sptr[1] = spt[0];
				sptr[2] = spt[2];
			}
			else if ( sub.indexOf("=")==-1 ) { // comp;jump
				sptr[0] = spt[0];
				sptr[1] = null;
				sptr[2] = spt[1];
			}
			else { // dest=comp
				sptr[0] = spt[1];
				sptr[1] = spt[0];
				sptr[2] = null;
			}
			StringBuffer sb = new StringBuffer("111");
			sb.append(code.comp(sptr[0]));
			sb.append(code.dest(sptr[1]));
			sb.append(code.jump(sptr[2]));
			return sb.toString();
		}
	}
	
	public void analyzeSymbol(String line) {
		String sub = line.replaceAll("\\s*", "");
		if ( sub.length()==0 ) { // Empty line
			return;
		}
		if ( sub.charAt(0)=='/' && sub.charAt(1)=='/' ) { // Comment
			return;
		}
		if ( sub.charAt(0)=='(' ) { // Label
			symboltable.addSymbol(sub.substring(1, sub.indexOf(")")), linenumber);
		}
		else { // Command
			linenumber++;
		}
	}
	
}
