import java.util.Arrays;


public class Processor {

	public static final RegisterFile registerFile = new RegisterFile();
	public static Instruction currentInstruction;
	public static String status;

	public static void fetch() {
		Word nextInstruction = Memory.loadWord(registerFile.get("pc").calculateValueSigned());
		boolean[] instructionArray = nextInstruction.bitArray;
		currentInstruction = new Instruction(instructionArray);
		currentInstruction.valP = registerFile.get("pc").addFour();
	}

	public static void decode() {
		currentInstruction.RS1Val = registerFile.get(currentInstruction.Rs1);
		currentInstruction.RS2Val = registerFile.get(currentInstruction.Rs2); 
	}

	public static void execute() {
		DoubleWord valE = null;
		boolean[] constant = null;
		switch(currentInstruction.instruction) {
		case "ADD": 
			valE = currentInstruction.RS1Val.add(currentInstruction.RS2Val);
			currentInstruction.EVal = valE; 
			break;
		case "SUB":
			valE = currentInstruction.RS1Val.subtract(currentInstruction.RS2Val);
			currentInstruction.EVal = valE; 
			break;
		case "AND":
			valE = currentInstruction.RS1Val.and(currentInstruction.RS2Val);
			break;
		case "OR":
			valE = currentInstruction.RS1Val.or(currentInstruction.RS2Val);
			break;
		case "XOR":
			valE = currentInstruction.RS1Val.xor(currentInstruction.RS2Val);
			break;	
		case "XORI":
			constant = new boolean[32];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
			valE = new DoubleWord(ALU.XOR(currentInstruction.RS1Val.bitArray,constant));
			currentInstruction.EVal = valE;
			break;
		case "ORI":
			constant = new boolean[32];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
			valE = new DoubleWord(ALU.OR(currentInstruction.RS1Val.bitArray,constant));
			currentInstruction.EVal = valE; 
			break;
		case "ANDI":
			constant = new boolean[32];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
			valE = new DoubleWord(ALU.AND(currentInstruction.RS1Val.bitArray,constant));
			currentInstruction.EVal = valE; 
			break;
		default:
			constant = new boolean[32];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
			valE = new DoubleWord(ALU.IADD(currentInstruction.RS1Val.bitArray,constant));
			currentInstruction.EVal = valE; 
			break;		
			
		}
	}

	public static void memory() {
		switch(currentInstruction.instruction) {
		case "LD":
			currentInstruction.MVal = Memory.loadDoubleWord(currentInstruction.EVal.calculateValueSigned());
			break;
		case "SD": 
			Memory.storeDoubleWord(currentInstruction.EVal.calculateValueSigned(), currentInstruction.RS2Val);
			break;
		case "LW":
			currentInstruction.MVal = new  DoubleWord(Memory.loadWord(currentInstruction.EVal.calculateValueSigned()), true);
			break;
		case "LWU":
			currentInstruction.MVal = new  DoubleWord(Memory.loadWord(currentInstruction.EVal.calculateValueSigned()), false);
			break;
		case "SW": 
			Memory.storeWord(currentInstruction.EVal.calculateValueSigned(), currentInstruction.RS2Val.getWord(0));
			break;
		case "LH":
			currentInstruction.MVal = new  DoubleWord(Memory.loadHalfWord(currentInstruction.EVal.calculateValueSigned()), true);
			break;
		case "LHU":
			currentInstruction.MVal = new  DoubleWord(Memory.loadHalfWord(currentInstruction.EVal.calculateValueSigned()), false);
			break;
		case "SH": 
			Memory.storeHalfWord(currentInstruction.EVal.calculateValueSigned(), currentInstruction.RS2Val.getHalfWord(0));
			break;	
		case "LB":
			currentInstruction.MVal = new  DoubleWord(Memory.loadBYTE(currentInstruction.EVal.calculateValueSigned()), true);
			break;
		case "LBU":
			currentInstruction.MVal = new  DoubleWord(Memory.loadBYTE(currentInstruction.EVal.calculateValueSigned()), false);
			break;
		case "SB": 
			Memory.storeBYTE(currentInstruction.EVal.calculateValueSigned(), currentInstruction.RS2Val.getBYTE(0));
			break;		

		}
	}

	public static void writeBack() {
		switch(currentInstruction.instruction) {
		case "LD":
		case "LW":
		case "LWU":
		case "LH":
		case "LHU":
		case "LB":
		case "LBU":	
			registerFile.set(currentInstruction.Rd, currentInstruction.MVal);
			break;
		case "ADDI":
		case "SUB":
		case "ADD":
		case "AND":
		case "XOR":
		case "OR":	
			registerFile.set(currentInstruction.Rd, currentInstruction.EVal);
			break;
		}
	}

	//Bare Bones
	public static void setUp(DoubleWord start) {
		registerFile.set("pc", start);
	}

	public static void pc() {
		switch(currentInstruction.instruction) {

		default:
			registerFile.set("pc", currentInstruction.valP);
		}
	}




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
