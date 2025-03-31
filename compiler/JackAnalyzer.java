package compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class JackAnalyzer {

	public static void main(String[] args) {
		try {
			String addr = "C:/nand/nand2tetris/projects/9/Tetris/";
			File[] list = new File(addr).listFiles();
			for (File file : list) {
				if (file.getName().endsWith(".jack")) {
					String name = file.getName().substring(0, file.getName().indexOf("."));
					BufferedReader in = new BufferedReader(
							new InputStreamReader(
									new FileInputStream(file)));
					PrintWriter out = new PrintWriter(
							new BufferedWriter(
									new OutputStreamWriter(
											new FileOutputStream(addr + name + ".vm"))));
					CompilationEngine ce = new CompilationEngine(in, out);
					ce.compileClass();
					in.close();
					out.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
