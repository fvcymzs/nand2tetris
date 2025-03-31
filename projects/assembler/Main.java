package assembler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Main {

	public static void main(String[] args) {
		try {
			String addr = "C:/nand/nand2tetris/projects/9/Tetris/Tetris";
			BufferedReader symbolin = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(addr + ".asm")));
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(addr + ".asm")));
			PrintWriter out = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(addr + ".hack"))));
			String line;
			String analyzedline;
			Parser parser = new Parser();
			while ( (line=symbolin.readLine()) != null ) {
				parser.analyzeSymbol(line);
			}
			symbolin.close();
			while ( (line=in.readLine()) != null ) {
				analyzedline = parser.analyzeLine(line);
				if ( analyzedline!=null ) {
					out.println(analyzedline);
					System.out.println(line);
					System.out.println(analyzedline);
				}
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
