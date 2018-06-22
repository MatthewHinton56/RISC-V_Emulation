import java.util.Arrays;


public class Processor {

	public static final RegisterFile registerFile = new RegisterFile();
	public static Instruction currentInstruction;
	public static String status;

	public static void fetch() {
		Word nextInstruction = Memory.loadWord(registerFile.get("pc").calculateValueSigned());
		boolean[] instructionArray = nextInstruction.bitArray;
		currentInstruction = new Instruction(instructionArray);
	}

	public static void decode() {
		currentInstruction.RS1Val = registerFile.get(currentInstruction.Rs1);
		currentInstruction.RS2Val = registerFile.get(currentInstruction.Rs2); 
	}

	public static void execute() {
		switch(currentInstruction.instruction) {
		case "ADD": 
			DoubleWord valE = new DoubleWord(ALU.ADD(currentInstruction.RS1Val.bitArray, currentInstruction.RS2Val.bitArray));
			currentInstruction.EVal = valE; 
			break;
		}


		DoubleWord newPC = new DoubleWord(ALU.ADDFOUR(registerFile.get("pc").bitArray));
		registerFile.set("pc", newPC);
	}

	public static void memory() {
		if(currentInstruction.memory) {
			
		}
	}

	public static void writeBack() {
		if(currentInstruction.memory)
			registerFile.set(currentInstruction.Rd, currentInstruction.MVal);
		else
		registerFile.set(currentInstruction.Rd, currentInstruction.EVal);
	}
	
	//Bare Bones
	public static void setUp(DoubleWord start) {
		registerFile.set("pc", start);
	}

	public static void pc() {}
	



	/*public static void initialize() {
		if(Compiler.compiled) {
			Processor.PC = new DoubleWord(Long.parseLong(Compiler.start_address,16));
			for(long l: Compiler.COMPILED_CONSTANTS.keySet())
				Memory.storeDoubleWord(l, Compiler.COMPILED_CONSTANTS.get(l));
			for(long l: Compiler.COMPILED_INSTRUCTIONS.keySet())
				Memory.storeInstruction(l, Compiler.COMPILED_INSTRUCTIONS.get(l));
			status = "AOK";
			registerFile.reset();
		} else {
			status = "HLT";
		}
	}*/

	public static void step() {
		if(status.equals("AOK")) {
			fetch();
			if(status.equals("AOK")) {
				decode();
				execute();
				memory();
				writeBack();
				pc();
			}
		}
	}

	public static void run() {
		while(status.equals("AOK")) {
			fetch();
			if(status.equals("AOK")) {
				decode();
				execute();
				memory();
				writeBack();
				pc();
				//System.out.println(registerFile.get("%rdx"));
				//System.out.println(registerFile.get("%rsp"));
				//System.out.println(PC.calculateValueSigned());
			}
		}
	}

}
