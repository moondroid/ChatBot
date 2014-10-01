package it.moondroid.chatbot.alice.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.charset.Charset;

//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;


public class IOUtils {


	BufferedReader reader;
	BufferedWriter writer;

	public IOUtils(String filePath, String mode) {
		try {
			if (mode.equals("read")) {
				reader = new BufferedReader(new FileReader(filePath));
			} else if (mode.equals("write")) {
				(new File(filePath)).delete();
				writer = new BufferedWriter(new FileWriter(filePath, true));
			}
		} catch (IOException e) {
			System.err.println("error: " + e);
		}
	}

	
	public String readLine() {
		String result = null;
		try {
			result = reader.readLine();
		} catch (IOException e) {
			System.err.println("error: " + e);
		}
		return result;
	}


	public void writeLine(String line) {
		try {
			writer.write(line);
			writer.newLine();
		} catch (IOException e) {
			System.err.println("error: " + e);
		}
	}


	public void close() {
		try {
			if (reader != null) reader.close();
			if (writer != null) writer.close();
		} catch (IOException e) {
			System.err.println("error: " + e);
		}

	}


	public static void writeOutputTextLine(String prompt, String text) {
		System.out.println(prompt + ": " + text);
	}


	public static String readInputTextLine() {
		return readInputTextLine(null);
	}


	public static String readInputTextLine(String prompt) {
		if (prompt != null) {
			System.out.print(prompt + ": ");
		}
        BufferedReader lineOfText = new BufferedReader(new InputStreamReader(System.in));
		String textLine = null;
		try {
			textLine = lineOfText.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return textLine;
	}


	public static File[] listFiles(File dir) {
		return dir.listFiles();
	}


	public static String system(String evaluatedContents, String failedString) {
		Runtime rt = Runtime.getRuntime();
        //System.out.println("System "+evaluatedContents);
        try {
            Process p = rt.exec(evaluatedContents);
            InputStream istrm = p.getInputStream();
            InputStreamReader istrmrdr = new InputStreamReader(istrm);
            BufferedReader buffrdr = new BufferedReader(istrmrdr);
            String result = "";
            String data = "";
            while ((data = buffrdr.readLine()) != null) {
                result += data+"\n";
            }
            //System.out.println("Result = "+result);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedString;

        }
	}

	
	public static String evalScript(String engineName, String script) throws Exception {
//        //System.out.println("evaluating "+script);
//        ScriptEngineManager mgr = new ScriptEngineManager();
//        ScriptEngine engine = mgr.getEngineByName("JavaScript");
//		String result = ""+engine.eval(script);
//		return result;
        return ""; //TODO
	}

}

