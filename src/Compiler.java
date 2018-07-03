import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Compiler {

	public static final HashMap<Long, String[]> COMPILED_INSTRUCTIONS =  new HashMap<Long, String[]>(); 
	public static final HashMap<Long, DoubleWord> COMPILED_CONSTANTS = new HashMap<Long, DoubleWord>(); 
	public static String start_address;
	public static boolean compiled;

	public static String compile(String input) {
		preprocessor(input);
		compiled = true;
		COMPILED_INSTRUCTIONS.clear();
		COMPILED_CONSTANTS.clear();
		start_address = inputLines.get(0).address;
		String output = "";



		return output;
	}


	//creates a map of tags assigned with an address
	public static void preprocessor(String input) {
		long address = 0;
		TAG_TO_ADDRESS.clear();
		inputLines.clear();
		Scanner scan = new Scanner(input);
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] lineSplit = split(line);
			String firstWord = lineSplit[0];
			if(firstWord.startsWith("."))
				address = assemblerDirective(firstWord, lineSplit, address, line);
			else if(firstWord.contains(":"))
				address = tag(firstWord, lineSplit, address, line);
			else 
				address = instruction(firstWord, lineSplit, address, line);
		}
	}

	private static long tag(String tag, String[] lineSplit, long address, String line) {
		TAG_TO_ADDRESS.put(lineSplit[0].substring(0, lineSplit[0].length()-1), Long.toHexString(address));
		inputLines.add(new Line(Long.toHexString(address),lineSplit,line));
		return address;
	}


	private static long instruction(String instruction, String[] lineSplit, long address, String line) {
		inputLines.add(new Line(Long.toHexString(address),lineSplit,line));
		return address + 4;
	}


	private static long assemblerDirective(String directive, String[] lineSplit, long address, String line) {
		if(directive.equals(ALIGN)) {
			address = address + address%Integer.parseInt(lineSplit[1]);
			inputLines.add(new Line(Long.toHexString(address),lineSplit,line));
			return address;
		}
		long increment = 0;
		switch(directive) {
		case BYTE:
			increment = 1;
			break;
		case TWOBYTE:
		case SHORT:
		case HALF:
			increment = 2;
			break;
		case FOURBYTE:
		case LONG:
		case WORD:
			increment = 4;
			break;
		default:
			increment = 8;
			break;		
		}
		for(int i = 1; i < lineSplit.length; i++) {
			String[] newLineArray = new String[2];
			newLineArray[0] = directive;
			newLineArray[1] = lineSplit[i];
			String newLine = directive + " " + lineSplit[i];
			inputLines.add(new Line(Long.toHexString(address),newLineArray,newLine));
			address+=increment;
		}
		return address;
	}


	private static String[] split(String line) {
		line = line.replace(",", "");
		int nonWhiteSpace = -1;
		int index = 0;
		while(nonWhiteSpace ==-1 && index < line.length()) {
			if(line.charAt(index) > 32)
				nonWhiteSpace = index;
			index++;
		}
		line = (nonWhiteSpace == -1 || line.contains("#")) ? "" : line.substring(nonWhiteSpace) ;
		return line.split("\\s+");
	}

	private static HashMap<String, String> TAG_TO_ADDRESS = new HashMap<String, String>(); 
	private static ArrayList<Line> inputLines = new ArrayList<Line>();



	public static void main(String[] args) throws FileNotFoundException {
		//String input = ".pos 0\nirmovq stack, %rsp\nrrmovq %rsp, %rbp\nirmovq src, %rdi\nirmovq dest, %rsi\nirmovq $3, %rdx\ncall copy_block\nhalt";
		Scanner scan = new Scanner(new File("copy.ys"));
		String input = "";
		while(scan.hasNextLine()) {
			input+=scan.nextLine()+"\n";
		}
		System.out.println(compile(input));
		//System.out.println(Compiler.start_address);
		//System.out.println(Compiler.COMPILED_CONSTANTS);
		//System.out.println(Compiler.COMPILED_INSTRUCTIONS);
		//Processor.initialize();
		Processor.run();
		System.out.println(Processor.registerFile.get("%rax"));
		//System.out.println(Processor.PC.calculateValueSigned());
	}

	private static class Line {
		String address;
		String[] splitLine;
		String line;
		public Line(String address, String[] splitLine, String line) {
			this.address = address;
			this.splitLine = splitLine;
			this.line = line;
		}

		public String toString() {
			return address + " " + Arrays.toString(splitLine);
		}
	}

	public static String convertArrayToString(String[] array) {
		String output = "";
		for(String s: array)
			output += s;
		return output;
	}
	//valid assembler tags
	public static final String ALIGN = ".align";
	public static final String BYTE = ".byte";
	
	public static final String TWOBYTE = ".2byte";
	public static final String HALF = ".half";
	public static final String SHORT = ".short";
	
	public static final String FOURBYTE = ".4byte";
	public static final String WORD = ".word";
	public static final String LONG = ".long";
	
	public static final String EIGHTBYTE = ".8byte";
	public static final String DWORD = ".dword";
	public static final String QUAD = ".quad";
}
