import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class TextInterface {
	public static final String HEX = "H";
	public static final String UNSIGNED = "U";
	public static final String SIGNED = "S";
	public static final String HEXLE = "HL";
	public static final String BYTE = "B";
	public static final String HALFWORD = "HW";
	public static final String WORD = "W";
	public static final String DOUBLEWORD = "DW";


	public static final String[] VALID_TYPES = {HEX, UNSIGNED, SIGNED, HEXLE};
	public static final String[] VALID_SIZES = {BYTE, HALFWORD, WORD, DOUBLEWORD};
	public static String defaultDisplayType = HEX;
	public static String defaultDisplaySize = DOUBLEWORD;
	public static void main(String[] args) {
		runInterface();
	}

	private static void runInterface() {
		boolean running = true;
		String fileText = "";
		boolean fileLoad = false;
		boolean programCompiled = false;
		boolean programInitialized = false;
		String compiledText = "";
		Scanner inputScanner = new Scanner(System.in); 
		while(running) {
			System.out.print("(RISCV): ");
			String input = inputScanner.nextLine();
			String[] parsedInput = input.split("\\s+");
			if(parsedInput.length > 0) {
				switch(parsedInput[0].toLowerCase()) {
				case "q":
				case "quit":
					System.out.println("Program Exitting");
					running = false;
					break;
				case "load":
				case "l":
					if(parsedInput.length >= 2) {
						fileText = loadFile(parsedInput[1]);
						fileLoad = (fileText != null);
						compiledText = "";
						programCompiled = false; 
						programInitialized = false;
					} else {
						System.out.println("No file given");
					}
					break;
				case "compile":
					compiledText = compile(fileLoad, fileText);
					programCompiled = (compiledText != null);
					programInitialized = false;
					break;
				case "initialize":
				case "i":
					if(programCompiled) {
						processFlags(parsedInput);
						initialize();
						programInitialized = true;
					} else {
						System.out.println("No Program is currently compiled");
					}
					break;
				case "disas":
					if(programCompiled) {
						System.out.println(disas(compiledText));
					} else {
						System.out.println("No code currently compiled");
					}
					break;
				case "step":
				case "s":
				case "next":
					if(programInitialized) {
						processFlags(parsedInput);
						step();
					} else {
						System.out.println("Program is not initialized");
					}
					break;
				case "r":
				case "run":
					if(programInitialized) {
						processFlags(parsedInput);
						run();
						} else {
							System.out.println("Program is not initialized");
						}
					break;
				case "clockpulse":
				case "cp":
				case "c":
					if(programInitialized) {
						processFlags(parsedInput);
						clockPulse();
					} else {
						System.out.println("Program is not initialized");
					}
					break;
				case "reg":
				case "register":
					if(programInitialized) {
						processFlags(parsedInput);
						register(); 
					} else {
						System.out.println("Program is not initialized");
					}
					break;
				case "mem":
				case "memory":
					if(programInitialized) {
						processFlags(parsedInput);
						memory(); 
					} else {
						System.out.println("Program is not initialized");
					}
					break;
				case "pipeline":
				case "p":
					if(programInitialized)
						pipeline(compiledText);
					break;
				default: 
					if(input.length() > 0) {
						System.out.println("Invalid command: " + input);
					}
				}
			}
		}
		inputScanner.close();
	}

	private static void memory() {
		if(Processor.initialized) {
			System.out.println(memoryDisplay());
		} else {
			System.out.println("Processor is not initialized");
		}
	}

	private static void register() {
		if(Processor.initialized) {
			System.out.println(registerDisplay());
		} else {
			System.out.println("Processor is not initialized");
		}
	}

	private static void pipeline(String compiledText) {
		System.out.println("Pipeline info:");
		System.out.println("Status: " + Processor.status);
		DoubleWord fetchAddress = Processor.pcAddresses[0];
		System.out.print("Fetch: ");
		if(fetchAddress == null || !validAddress(fetchAddress, compiledText)) {
			System.out.println("BUBBLE");
		} else {
			System.out.println("0x"+fetchAddress.displayToString());
		}

		DoubleWord decodeAddress = Processor.pcAddresses[1];
		System.out.print("DECODE: ");
		if(decodeAddress == null) {
			System.out.println("BUBBLE");
		} else {
			System.out.println("0x"+decodeAddress.displayToString());
		}

		DoubleWord executeAddress = Processor.pcAddresses[2];
		System.out.print("EXECUTE: ");
		if(executeAddress == null) {
			System.out.println("BUBBLE");
		} else {
			System.out.println("0x"+executeAddress.displayToString());
		}

		DoubleWord memoryAddress = Processor.pcAddresses[3];
		System.out.print("MEMORY: ");
		if(memoryAddress == null) {
			System.out.println("BUBBLE");
		} else {
			System.out.println("0x"+memoryAddress.displayToString());
		}

		DoubleWord writeBackAddress = Processor.pcAddresses[4];
		System.out.print("WRITE BACK: ");
		if(writeBackAddress == null) {
			System.out.println("BUBBLE");
		} else {
			System.out.println("0x"+writeBackAddress.displayToString());
		}
	}

	private static void clockPulse() {
		Processor.clockPulse();
		if(Processor.initialized) {
			System.out.println("Processor clock pulsed.");
		} else {
			System.out.println("Processor is not initialized");
		}
	}

	private static void run() {
		Processor.run();
		if(Processor.initialized) {
			System.out.println("RUN:\n");
			System.out.println("PC: " + displayText(Processor.registerFile.get("pc")) + "\n");
			if(Processor.exceptionGenerated)
				System.out.println("The processor exited with: " + Processor.exception+"\n");
			System.out.println(registerDisplay());
			System.out.println(memoryDisplay());
			System.out.println(registerDifference(Processor.initialRegisterFile, Processor.finalRegisterFile, "FINAL"));
			System.out.println(memoryDifference(Processor.initialMemory, Processor.finalMemory, "FINAL"));
		} else {
			System.out.println("Processor is not initialized");
		}
	}

	private static void step() {
		Processor.step();
		if(Processor.initialized) {
			System.out.println("STEP:");
			if(Processor.status.equals("HLT")) {
				if(Processor.exceptionGenerated)
					System.out.println("The processor exited with:\n" + Processor.exception);
				else {
					System.out.println("The program has completed its execution:");
					System.out.println("PC: " + displayText(Processor.registerFile.get("pc")));
					System.out.println("Completed Instruction: " + Processor.completedInstruction.buildDisplayInstruction());
					System.out.println(registerDisplay());
					System.out.println(memoryDisplay());
					System.out.println(registerDifference(Processor.initialRegisterFile, Processor.finalRegisterFile, "FINAL"));
					System.out.println(memoryDifference(Processor.initialMemory, Processor.finalMemory, "FINAL"));
				}
			} else {
				System.out.println("PC: " + displayText(Processor.completedInstruction.address));
				System.out.println("Completed Instruction: " + Processor.completedInstruction.buildDisplayInstruction());
				System.out.println(registerDifference(Processor.stepBeforeReg, Processor.stepAfterReg, "STEP"));
				System.out.println(memoryDifference(Processor.stepBeforeMem, Processor.stepAfterMem, "STEP"));
			}
		} else {
			System.out.println("Processor is not initialized");
		}
	}

	private static void processFlags(String[] parsedInput) {
		if(parsedInput.length > 1) {
			if(Instruction.contains(VALID_SIZES, parsedInput[1])) {
				defaultDisplaySize = parsedInput[1];
			} else if(Instruction.contains(VALID_TYPES, parsedInput[1])) {
				defaultDisplayType = parsedInput[1];
			} else {
				System.out.println("Invalid modifier: "+ parsedInput[1] + ". Default setting used");
			}
		}
		if(parsedInput.length == 3) {
			if(Instruction.contains(VALID_SIZES, parsedInput[2])) {
				defaultDisplaySize = parsedInput[2];
			} else if(Instruction.contains(VALID_TYPES, parsedInput[2])) {
				defaultDisplayType = parsedInput[2];
			} else {
				System.out.println("Invalid modifier: "+ parsedInput[2] + ". Default setting used");
			}
		}

	}

	private static String disas(String compiledText) {
		String output = "";
		Scanner scan = new Scanner(compiledText);
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			String addressString = line.substring(line.indexOf("x")+1, line.indexOf(":"));
			DoubleWord address = new DoubleWord(Long.parseLong(addressString, 16));
			String restOfLine = line.substring(line.indexOf(":")+1);
			if(!restOfLine.contains(":") && !restOfLine.contains(".")) {
				if(Processor.pcAddresses[Processor.FETCH_ADDRESS_POSITION] != null && Processor.pcAddresses[Processor.FETCH_ADDRESS_POSITION].equals(address))
					output += "F";
				else 
					output += "_";
				if(Processor.pcAddresses[Processor.DECODE_ADDRESS_POSITION] != null && Processor.pcAddresses[Processor.DECODE_ADDRESS_POSITION].equals(address))
					output += "D";
				else 
					output += "_";
				if(Processor.pcAddresses[Processor.EXECUTE_ADDRESS_POSITION] != null && Processor.pcAddresses[Processor.EXECUTE_ADDRESS_POSITION].equals(address))
					output += "E";
				else 
					output += "_";
				if(Processor.pcAddresses[Processor.MEMORY_ADDRESS_POSITION] != null && Processor.pcAddresses[Processor.MEMORY_ADDRESS_POSITION].equals(address))
					output += "M";
				else 
					output += "_";
				if(Processor.pcAddresses[Processor.WRITE_BACK_ADDRESS_POSITION] != null && Processor.pcAddresses[Processor.WRITE_BACK_ADDRESS_POSITION].equals(address))
					output+= "W";
				else 
					output += "_";
			} else {
				output += "_____";
			}

			output+=" "+line+"\n";
		}
		scan.close();
		return output;
	}

	private static void initialize() {
		Processor.initialize();
		System.out.println("Processor output:\n\nInitialize:\n");
		if(Processor.status.equals("HLT")) {
			System.out.println("Program failed to initialize, check that all memory locations are valid");
		} else {
			System.out.println("PC: " + displayText(Processor.registerFile.get("pc")) + "\n");
			System.out.println(registerDisplay());
			System.out.println(memoryDisplay());
		}
	}

	private static String compile(boolean fileLoad, String fileText) {
		if(!fileLoad) {
			System.out.println("No File is currently loaded");
			return null;
		}
		System.out.println();
		String output;
		try {
			output = Compiler.compile(fileText);
			System.out.println("Assembly compiled and ready for emulation");
			System.out.println(output);
			Processor.clear();
		}
		catch(IllegalArgumentException e) {
			System.out.println("Compiler Output:\n" + e.getMessage());
			return null;
		}
		return output;
	}

	private static String loadFile(String filePath) {
		String fileText = "";
		// Traditional way to get the response value.
		try {
			Scanner scan = new Scanner(new File(filePath));
			while(scan.hasNextLine()) {
				fileText+=scan.nextLine()+"\n";
			}
			scan.close();
		} catch (FileNotFoundException e1) {
			System.out.println("Not a valid File Path. No File loaded");
			return null;
		}
		System.out.println("File: testFile was successfully loaded");
		return fileText;
	}

	private static String registerDisplay() {
		String output = "Register File:\n";
		for(String reg: Processor.registerFile.keySet()) {
			output += String.format("%3s", reg) + " = " + displayText(Processor.registerFile.get(reg))+ "\n";
		}
		return output;
	}

	private static String registerDifference(TreeMap<String, DoubleWord> before, TreeMap<String, DoubleWord> after, String text) {
		String output = "Register File Differences: " + text + ":\n";
		ArrayList<String> dif = RegisterFile.getDif(before, after);
		for(String s: dif) {
			output += String.format("%-3s", s) +  ": " + displayText(before.get(s)) + "====>" + displayText(after.get(s)) +"\n";
		}
		return output;
	}

	private static String memoryDisplay() {
		int offset = 0;
		switch(defaultDisplaySize) {
		case "B":
			offset = 1;
			break;
		case "HW":
			offset = 2;
			break;
		case "W":
			offset = 4;
			break;
		case "DW":
			offset = 8;
			break;
		}
		String output = "Memory:\n";
		Set<Long> usedAddresses = new HashSet<Long>();
		for(Long address: Memory.memory.keySet()) {
			long modifiedAddress = address - address%offset;
			if(!usedAddresses.contains(modifiedAddress)) {
				usedAddresses.add(modifiedAddress);	
				output +=  "0x" + Long.toString(modifiedAddress, 16)+ " = " + displayText(Memory.load(modifiedAddress, offset))+ "\n";
			}
		}
		return output;
	}

	private static String memoryDifference(HashMap<Long, BYTE> before, HashMap<Long, BYTE> after, String text) {
		String output = "Memory Differences: " + text + ":\n";
		ArrayList<Long> dif = Memory.getDif(before, after);
		for(Long l: dif) {
			if(before.containsKey(l))
				output += "0x" +Long.toString(l, 16) + ": " + displayText(before.get(l)) + "====>" + displayText(after.get(l)) +"\n";
			else 
				output += "0x" +Long.toString(l, 16) + ": " + displayText(new BYTE()) + "====>" + displayText(after.get(l)) +"\n";
		}
		return output;
	}

	private static String displayText(LittleEndian val) {
		switch(defaultDisplayType) {
		case SIGNED:
			return (val.calculateValueSigned()+" ");
		case UNSIGNED:
			return val.calculateValueUnSigned();
		case HEXLE:
			return val.generateHexLE();
		default:
			return "0x"+val.displayToString();
		}
	}

	private static boolean validAddress(DoubleWord address, String compiledText) {
		Scanner scan = new Scanner(compiledText);
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			String addressString = line.substring(line.indexOf("x")+1, line.indexOf(":"));
			String restOfLine = line.substring(line.indexOf(":")+1);
			DoubleWord addressLine = new DoubleWord(Long.parseLong(addressString, 16));
			if(addressLine.equals(address) && !restOfLine.contains(":") && !restOfLine.contains(".")) {
				scan.close();
				return true;
			}
		}
		scan.close();
		return false;
	}
}
