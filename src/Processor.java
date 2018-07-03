import java.util.Arrays;


public class Processor {

	public static final RegisterFile registerFile = new RegisterFile();
	public static Instruction currentInstruction;
	public static String status;

	public static void fetch() {
		Word nextInstruction = Memory.loadWord(registerFile.get("pc").calculateValueSigned());
		boolean[] instructionArray = nextInstruction.bitArray;
		currentInstruction = new Instruction(instructionArray);
		System.out.println(currentInstruction.instruction);
		currentInstruction.valP = registerFile.get("pc").addFour();
	}

	public static void decode() {
		currentInstruction.RS1Val = registerFile.get(currentInstruction.Rs1);
		currentInstruction.RS2Val = registerFile.get(currentInstruction.Rs2); 
	}

	public static void execute() {
		DoubleWord valE = null;
		boolean[] constant = null;
		DoubleWord c = null;
		Word w = null;
		switch(currentInstruction.instruction) {
		case "ADD": 
			System.out.println("ADDADDADDADDD");
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
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
			constant = ALU.signExtension(constant, false, 64);
			valE = new DoubleWord(ALU.XOR(currentInstruction.RS1Val.bitArray,constant));
			currentInstruction.EVal = valE;
			break;
		case "ORI":
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
			constant = ALU.signExtension(constant, false, 64);
			valE = new DoubleWord(ALU.OR(currentInstruction.RS1Val.bitArray,constant));
			currentInstruction.EVal = valE; 
			break;
		case "ANDI":
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
			constant = ALU.signExtension(constant, false, 64);
			valE = new DoubleWord(ALU.AND(currentInstruction.RS1Val.bitArray,constant));
			currentInstruction.EVal = valE; 
			break;
		case "BEQ":
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 1, constant, 1, 12);
			constant = ALU.signExtension(constant, false, 64);
			c = new DoubleWord(constant);
			currentInstruction.valP = (currentInstruction.RS1Val.equals(currentInstruction.RS2Val)) ? registerFile.get("pc").add(c) : currentInstruction.valP;
			break;
		case "BNE":
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 1, constant, 1, 12);
			constant = ALU.signExtension(constant, false, 64);
			c = new DoubleWord(constant);
			currentInstruction.valP = (!currentInstruction.RS1Val.equals(currentInstruction.RS2Val)) ? registerFile.get("pc").add(c) : currentInstruction.valP;
			break;	
		case "BLT":
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 1, constant, 1, 12);
			constant = ALU.signExtension(constant, false, 64);
			c = new DoubleWord(constant);
			currentInstruction.valP = (currentInstruction.RS1Val.lessThan(currentInstruction.RS2Val,true)) ? registerFile.get("pc").add(c) : currentInstruction.valP;
			break;
		case "BLTU":
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 1, constant, 1, 12);
			constant = ALU.signExtension(constant, false, 64);
			c = new DoubleWord(constant);
			currentInstruction.valP = (currentInstruction.RS1Val.lessThan(currentInstruction.RS2Val,false)) ? registerFile.get("pc").add(c) : currentInstruction.valP;
			break;	
		case "BGE":
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 1, constant, 1, 12);
			constant = ALU.signExtension(constant, false, 64);
			c = new DoubleWord(constant);
			currentInstruction.valP = (!currentInstruction.RS1Val.lessThan(currentInstruction.RS2Val,true)) ? registerFile.get("pc").add(c) : currentInstruction.valP;
			break;
		case "BGEU":
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 1, constant, 1, 12);
			constant = ALU.signExtension(constant, false, 64);
			c = new DoubleWord(constant);
			currentInstruction.valP = (!currentInstruction.RS1Val.lessThan(currentInstruction.RS2Val,false)) ? registerFile.get("pc").add(c) : currentInstruction.valP;
			break;	
		case "JAL":
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 1, constant, 1, 12);
			constant = ALU.signExtension(constant, false, 64);
			c = new DoubleWord(constant);
			currentInstruction.EVal = registerFile.get("pc").add(c);
			break;
		case "LUI":
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 12, constant, 12, 20);
			constant = ALU.signExtension(constant, false, 64);
			c = new DoubleWord(constant);
			currentInstruction.EVal = c;
		case "AUIPC":
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 12, constant, 12, 20);
			constant = ALU.signExtension(constant, false, 64);
			c = new DoubleWord(constant);
			currentInstruction.EVal = registerFile.get("pc").add(c);
		case "SLT":
			currentInstruction.EVal = (currentInstruction.RS1Val.lessThan(currentInstruction.RS2Val,true)) ? new DoubleWord(1) : new DoubleWord();
			break;
		case "SLTU":
			currentInstruction.EVal = (currentInstruction.RS1Val.lessThan(currentInstruction.RS2Val,false)) ? new DoubleWord(1) : new DoubleWord();
			break;
		case "SLTI":
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
			constant = ALU.signExtension(constant, false, 64);
			c = new DoubleWord(constant);
			currentInstruction.EVal = (currentInstruction.RS1Val.lessThan(c,true)) ? new DoubleWord(1) : new DoubleWord();
			break;
		case "SLTIU":
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
			constant = ALU.signExtension(constant, false, 64);
			c = new DoubleWord(constant);
			currentInstruction.EVal = (currentInstruction.RS1Val.lessThan(c,false)) ? new DoubleWord(1) : new DoubleWord();
			break;
		case "ADDW":
			w = currentInstruction.RS1Val.getWord(0).add(currentInstruction.RS2Val.getWord(0));
			currentInstruction.EVal = new DoubleWord(w,true);
			break;
		case "SUBW":
			w = currentInstruction.RS1Val.getWord(0).subtract(currentInstruction.RS2Val.getWord(0));
			currentInstruction.EVal = new DoubleWord(w,true);
			break;	
			
		case "SLL":
			valE = currentInstruction.RS1Val.shiftLeft(currentInstruction.RS2Val);
			currentInstruction.EVal = valE; 
			System.out.println(valE);
			break;
		case "SRL":
			valE = currentInstruction.RS1Val.shiftRight(currentInstruction.RS2Val,true);
			currentInstruction.EVal = valE; 
			break;
		case "SRA":	
			valE = currentInstruction.RS1Val.shiftRight(currentInstruction.RS2Val,false);
			currentInstruction.EVal = valE; 
			break;
		case "SLLW":
			w = currentInstruction.RS1Val.getWord(0).shiftLeft(currentInstruction.RS2Val.getWord(0));
			currentInstruction.EVal = new DoubleWord(w,true);
			break;
		case "SRLW":
			w = currentInstruction.RS1Val.getWord(0).shiftRight(currentInstruction.RS2Val.getWord(0),true);
			currentInstruction.EVal = new DoubleWord(w,true); 
			break;
		case "SRAW":		
			w = currentInstruction.RS1Val.getWord(0).shiftRight(currentInstruction.RS2Val.getWord(0),false);
			currentInstruction.EVal = new DoubleWord(w,true); 
			break;
			
		case "SLLI":
			constant = new boolean[6];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 6);
			valE = currentInstruction.RS1Val.shiftLeft(constant);
			currentInstruction.EVal = valE; 
			break;
		case "SRLI":
			constant = new boolean[6];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 6);
			valE = currentInstruction.RS1Val.shiftRight(constant,true);
			currentInstruction.EVal = valE; 
			break;
		case "SRAI":
			constant = new boolean[6];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 6);
			valE = currentInstruction.RS1Val.shiftRight(constant,false);
			currentInstruction.EVal = valE; 
			break;
		case "SLLIW":
			constant = new boolean[5];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 5);
			w = currentInstruction.RS1Val.getWord(0).shiftLeft(constant);
			currentInstruction.EVal = new DoubleWord(w,true);
			break;
		case "SRLIW":
			constant = new boolean[5];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 5);
			w = currentInstruction.RS1Val.getWord(0).shiftRight(constant,true);
			currentInstruction.EVal = new DoubleWord(w,true); 
			break;
		case "SRAIW":
			constant = new boolean[5];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 5);
			w = currentInstruction.RS1Val.getWord(0).shiftRight(constant,false);
			currentInstruction.EVal = new DoubleWord(w,true); 
			break;	
		case "ADDIW":	
			constant = new boolean[32];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
			constant = ALU.signExtension(constant, false, 64);
			w = new Word(constant);
			valE = new DoubleWord(currentInstruction.RS1Val.getWord(0).add(w),true);
			
		default:
			constant = new boolean[64];
			System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
			constant = ALU.signExtension(constant, false, 64);
			valE = new DoubleWord(ALU.IADD(currentInstruction.RS1Val.bitArray,constant));
			currentInstruction.EVal = valE; 
			System.out.println(valE);
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
		case "JALR":	
		case "JAL":
			registerFile.set(currentInstruction.Rd, currentInstruction.valP);
			break;
		default:	
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
		case "JALR":
		case "JAL":
			registerFile.set("pc", currentInstruction.EVal);
			break;
		default:
			registerFile.set("pc", currentInstruction.valP);
		}
	}




	public static void initialize() {
		if(Compiler.compiled) {
			Processor.registerFile.set("pc",new DoubleWord(Long.parseLong(Compiler.start_address,16)));
			System.out.println(registerFile.get("pc").calculateValueSigned());
			for(long l: Compiler.COMPILED_CONSTANTS.keySet()) {
				LittleEndian le = Compiler.COMPILED_CONSTANTS.get(l);
				if(le instanceof BYTE)
					Memory.storeBYTE(l, (BYTE)le);
				if(le instanceof HalfWord)
					Memory.storeHalfWord(l, (HalfWord)le);
				if(le instanceof Word)
					Memory.storeWord(l, (Word)le);
				if(le instanceof DoubleWord)
					Memory.storeDoubleWord(l, (DoubleWord)le);
				System.out.println(Memory.loadWord(l) + " "+ l);
			}
			status = "AOK";
			registerFile.reset();
		} else {
			status = "HLT";
		}
		System.out.println("finished");
	}

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
