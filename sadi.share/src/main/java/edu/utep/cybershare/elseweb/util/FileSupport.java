package edu.utep.cybershare.elseweb.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

public class FileSupport{
		
	public static void writeTextFile(String contents, File file){
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(contents);
			out.close();
		}
		catch (Exception e){e.printStackTrace();}
	}
	
	public static void writeBinaryFile(byte[] contents, File file){
		try{
			FileOutputStream out = new FileOutputStream(file);
			out.write(contents);
			out.close();
		}
		catch (Exception e){e.printStackTrace();}
	}

	public static byte[] readBinaryFile(File file){
		byte[] contents = new byte[0];
		try{
			FileInputStream in = new FileInputStream(file);
			contents = new byte[(int) file.length()];
			in.read(contents);
			in.close();
		}
		catch (Exception e){e.printStackTrace();}
		return contents;
	}

	public static String readTextFile(File file){
		String contents = null;
		
		try{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;

			while ((line = in.readLine()) != null){
				if (contents == null)
					contents = line + "\n";
				else
					contents = contents + line + "\n";
			}
			in.close();
		}
		catch (Exception e){e.printStackTrace();}
		return contents;
	}
}