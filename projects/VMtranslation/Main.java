package VMtranslation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		try {
			String addr = "C:/nand/nand2tetris/projects/9/Tetris/";
			File[] list = new File(addr).listFiles();
			PrintWriter out = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(addr + "Tetris.asm"))));
			String line;
			Parser initp = new Parser(null);
			for ( String s : initp.init() ) {
				out.println(s);
				System.out.println(s);
			}
			for ( File file : list ) {
				if ( file.getName().endsWith(".vm") ) {
					BufferedReader in = new BufferedReader(
							new InputStreamReader(
									new FileInputStream(file)));
					Parser p = new Parser(file.getName().substring(0, file.getName().length()-3));
					ArrayList<String> as;
					while ( (line=in.readLine()) != null ) {
						if ( (as=p.analyzeLine(line)) != null ) {
							for ( String s : as ) {
								out.println(s);
								System.out.println(s);
							}
						}
					}
					in.close();
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
