import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Compiler {

	public static final HashMap<Long, String[]> COMPILED_INSTRUCTIONS =  new HashMap<Long, String[]>(); 
	public static final HashMap<Long, LittleEndian> COMPILED_CONSTANTS = new HashMap<Long, LittleEndian>(); 
	public static String start_address;
	public static boolean compiled;

	public static String compile(String input) {
		preprocessor(input);
		compiled = true;
		COMPILED_INSTRUCTIONS.clear();
		COMPILED_CONSTANTS.clear();
		start_address = inputLines.get(0).address;
		start_address = inputLines.get(0).address;
		String output = "";
		for(Line l: inputLines) {
			System.out.println(l);
			output += "0x" + l.address +": ";
			String firstWord = l.splitLine[0];
			if(firstWord.startsWith("."))
				output += assemblerDirectiveProcessing(l,firstWord);
			else if(firstWord.contains(":")) 
			{
				
			}
			else if(!l.line.equals("")){ 
				String opCode = InstructionBuilder.INSTRUCTION_TO_OPCODE.get(firstWord.toUpperCase());
				if(Instruction.contains(Instruction.RTYPE, opCode))
					output += rType(l,firstWord.toUpperCase());
				else if(opCode.equals(Instruction.OP_32_IMM) || opCode.equals(Instruction.OP_IMM))
					output += iTypeType1(l,firstWord.toUpperCase());
				else if(Instruction.contains(Instruction.UTYPE, opCode) || Instruction.contains(Instruction.UJTYPE, opCode))	
					output += uAndUJType(l,firstWord.toUpperCase());
				else if(opCode.equals(Instruction.JALR) || opCode.equals(Instruction.LOAD) || opCode.equals(Instruction.STORE))
					output += memType(l, firstWord.toUpperCase());
				else if(opCode.equals(Instruction.BRANCH))
					output += branchType(l, firstWord.toUpperCase());
				else if(opCode.equals(Instruction.STOP))
					output += haltType(l, firstWord.toUpperCase());
			}
			output+= " "+l.line+"\n";
		}
		return output;
	}

	private static String haltType(Line l, String instruction) {
		boolean[] instructionArray = InstructionBuilder.generateInstruction(instruction, null, null, null, null);
		Word w = new Word(instructionArray);
		COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), w);
		return w.generateHex();
	}

	private static String branchType(Line l, String instruction) {
		String rs1 = l.splitLine[1].substring(1);
		String rs2 = l.splitLine[2].substring(1);
		String val;
		if(TAG_TO_ADDRESS.containsKey(l.splitLine[3])) {
			System.out.println(TAG_TO_ADDRESS.get(l.splitLine[3]));
			System.out.println(l.address);
			val = (Long.parseLong(TAG_TO_ADDRESS.get(l.splitLine[3]),16) - Long.parseLong(l.address,16)) + "";
		} else if(l.splitLine[3].contains("0x")) {
			val = ""+Long.parseLong(l.splitLine[3].substring(2),16);
		} else {
			val = l.splitLine[3];
		}

		String imm = Long.parseLong(val)%((long)(Math.pow(2, 12))) +"";
		boolean[] rs1Array = ALU.longToBitArrayUnsigned(Long.parseLong(rs1), 5);
		boolean[] rs2Array = ALU.longToBitArrayUnsigned(Long.parseLong(rs2), 5);
		boolean[] immArray = ALU.longToBitArray(Long.parseLong(imm), 13);
		immArray[0] = false;
		if(instruction.toUpperCase().startsWith("SRAI"))
			immArray[10] = true;
		boolean[] instructionArray = InstructionBuilder.generateInstruction(instruction, null, rs1Array, rs2Array, immArray);
		System.out.println(instructionArray);
		Word w = new Word(instructionArray);
		COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), w);
		return w.generateHex();
	}

	private static String memType(Line l, String instruction) {
		String rDRs2 = l.splitLine[1].substring(1);
		String rs1 = l.splitLine[2].substring(1);
		String val = l.splitLine[3];
		if(TAG_TO_ADDRESS.containsKey(val)) {
			val = (Long.parseLong(TAG_TO_ADDRESS.get(val),16) - Long.parseLong(l.address,16)) + "";
		} else if(val.contains("0x")) {
			val = ""+Long.parseLong(val.substring(2),16);
		} else if(val.length() == 0) {
			val = "0";
		}
		boolean[] rDRS2Array = ALU.longToBitArrayUnsigned(Long.parseLong(rDRs2), 5);
		boolean[] rs1Array = ALU.longToBitArrayUnsigned(Long.parseLong(rs1), 5);
		String imm = Long.parseLong(val)%((long)(Math.pow(2, 11)))  +"";
		boolean[] immArray = ALU.longToBitArray(Long.parseLong(imm), 12);
		System.out.println(instruction+" "+ALU.calculateValueSigned(immArray));
		boolean[] instructionArray;
		switch(InstructionBuilder.INSTRUCTION_TO_OPCODE.get(instruction)) {
		case Instruction.LOAD:
		case Instruction.JALR:
			 instructionArray = InstructionBuilder.generateInstruction(instruction, rDRS2Array, rs1Array, null, immArray);
			break;
		default:
			instructionArray = InstructionBuilder.generateInstruction(instruction, null, rs1Array, rDRS2Array, immArray);
			break;
		}
		Word w = new Word(instructionArray);
		COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), w);
		return w.generateHex();
	}

	static String uAndUJType(Line l, String instruction) {
		String rD = l.splitLine[1].substring(1);
		String val;
		if(TAG_TO_ADDRESS.containsKey(l.splitLine[2])) {
			val = (Long.parseLong(TAG_TO_ADDRESS.get(l.splitLine[2])) - Long.parseLong(l.address,16)) + "";
		} else if(l.splitLine[2].contains("0x")) {
			val = ""+Long.parseLong(l.splitLine[2].substring(2),16);
		} else {
			val = l.splitLine[2];
		}

		boolean[] rDArray = ALU.longToBitArrayUnsigned(Long.parseLong(rD), 5);
		boolean[] immArray;
		String imm ;
		switch(InstructionBuilder.INSTRUCTION_TO_OPCODE.get(instruction)) {

		case Instruction.AUIPC:
		case Instruction.LUI:
			imm = Long.parseLong(val)%((long)(Math.pow(2, 19)))  +"";
			immArray = ALU.longToBitArray(Long.parseLong(imm), 20);
			break;
		default:
			imm = Long.parseLong(val)%((long)(Math.pow(2, 20)))  +"";
			immArray = ALU.longToBitArray(Long.parseLong(imm), 21);
			immArray[0] = false;
			break;
		}
		boolean[] instructionArray = InstructionBuilder.generateInstruction(instruction, rDArray, null, null, immArray);
		Word w = new Word(instructionArray);
		COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), w);
		return w.generateHex();
	}






	private static String iTypeType1(Line l, String instruction) {
		String rD = l.splitLine[1].substring(1);
		String rs1 = l.splitLine[2].substring(1);
		String val;
		if(TAG_TO_ADDRESS.containsKey(l.splitLine[3])) {
			val = (Long.parseLong(TAG_TO_ADDRESS.get(l.splitLine[3])) - Long.parseLong(l.address,16)) + "";
		} else if(l.splitLine[3].contains("0x")) {
			val = ""+Long.parseLong(l.splitLine[3].substring(2),16);
		} else {
			val = l.splitLine[3];
		}

		String imm = Long.parseLong(val)%((long)(Math.pow(2, 11))) +"";
		boolean[] rDArray = ALU.longToBitArrayUnsigned(Long.parseLong(rD), 5);
		boolean[] rs1Array = ALU.longToBitArrayUnsigned(Long.parseLong(rs1), 5);
		boolean[] immArray = ALU.longToBitArray(Long.parseLong(imm), 12);
		if(instruction.toUpperCase().startsWith("SRAI"))
			immArray[10] = true;
		boolean[] instructionArray = InstructionBuilder.generateInstruction(instruction, rDArray, rs1Array, null, immArray);
		System.out.println(instructionArray);
		Word w = new Word(instructionArray);
		COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), w);
		return w.generateHex();
	}






	private static String rType(Line l, String instruction) {
		String rD = l.splitLine[1].substring(1);
		String rs1 = l.splitLine[2].substring(1);
		String rs2 = l.splitLine[3].substring(1);
		boolean[] rDArray = ALU.longToBitArrayUnsigned(Long.parseLong(rD), 5);
		boolean[] rs1Array = ALU.longToBitArrayUnsigned(Long.parseLong(rs1), 5);
		boolean[] rs2Array = ALU.longToBitArrayUnsigned(Long.parseLong(rs2), 5);
		boolean[] instructionArray = InstructionBuilder.generateInstruction(instruction, rDArray, rs1Array, rs2Array, null);
		Word w = new Word(instructionArray);
		COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), w);
		return w.generateHex();
	}






	private static String assemblerDirectiveProcessing(Line l, String directive) {
		String output = "";
		String val;
		if(TAG_TO_ADDRESS.containsKey(l.splitLine[1])) {
			val = (Long.parseLong(TAG_TO_ADDRESS.get(l.splitLine[1])) - Long.parseLong(l.address,16)) + "";
		} else if(l.splitLine[1].contains("0x")) {
			val = l.splitLine[1].substring(2);
		} else {
			val = l.splitLine[1];
		}


		switch(directive) {
		case BYTE:
			BYTE b = new BYTE(val);
			output += b.generateHexLE();
			COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), b);
			break;
		case TWOBYTE:
		case SHORT:
		case HALF:
			HalfWord hw = new HalfWord(val,false);
			output += hw.generateHexLE();
			COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), hw);
			break;
		case FOURBYTE:
		case LONG:
		case WORD:
			Word w = new Word(val,false);
			output += w.generateHexLE();
			COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), w);
			break;
		default:
			DoubleWord dw = new DoubleWord(val,false);
			output += dw.generateHexLE();
			COMPILED_CONSTANTS.put(Long.parseLong(l.address, 16), dw);
			break;		
		}
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
			line = lineCorrection(line);
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

	private static String lineCorrection(String line) {
		if(line.contains("(")) {
			String[] splitLine = split(line);
			String instruction = splitLine[0];
			String rDRs2 = splitLine[1];
			String rs1 = splitLine[2].substring(splitLine[2].indexOf("(")+1, splitLine[2].indexOf(")"));
			String imm = splitLine[2].substring(0, splitLine[2].indexOf("("));
			return instruction + " " + rDRs2 + ", " + rs1 +", " + imm;
		}
		return line;
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
		line = line.replace(",", " ");
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
