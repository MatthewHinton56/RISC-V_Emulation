import java.util.HashMap;
import java.util.TreeMap;


public class Processor {

	public static final RegisterFile registerFile = new RegisterFile();
	public static final DoubleWord[] pcAddresses = new DoubleWord[5];
	public static final Instruction[] instructionStages = new Instruction[4];
	public static TreeMap<String, DoubleWord> initialRegisterFile, stepBeforeReg, stepAfterReg, finalRegisterFile;
	public static HashMap<Long, BYTE> initialMemory, stepBeforeMem, stepAfterMem, finalMemory;
	public static String status = "HLT";
	public static boolean JALRStall, stopFetching;
	public static int stopCount;
	private static boolean JALRTempStall;
	public static boolean validStop;
	public static Instruction completedInstruction;
	public static boolean initialized;

	//returns true if a instruction went through writeback
	private static boolean pipeLineIncrement() {
		boolean ret = writeBackControl();
		memoryControl();
		executeControl();
		boolean decodeStall = decodeControl();
		fetchControl(decodeStall);
		if(stopFetching)
			pcAddresses[FETCH_ADDRESS_POSITION] = null;
		if(JALRTempStall)
			JALRTempStall = false;
		if(stopFetching && stopCount == 0)
			status = "HLT";
		stopCount--;
		setPC();
		return ret;
	}

	private static void fetchControl(boolean decodeStall) {
		if((JALRStall || JALRTempStall) && !decodeStall) {
			//places bubble in the fetch stage
			instructionStages[DECODE_INSTRUCTION_POSITION] = new Instruction(DECODE);
		}
		if(stopFetching) 
			instructionStages[DECODE_INSTRUCTION_POSITION] = null;

		if(!decodeStall && !stopFetching && !JALRStall && !JALRTempStall) {
			try {
				instructionStages[DECODE_INSTRUCTION_POSITION] = fetch();
			} catch(MemoryException e) {
				status = "HLT";
				throw new ProcessorException(e.getMessage(), "The instruction the processor attempted to access generated the following error:");
			}
			if(instructionStages[DECODE_INSTRUCTION_POSITION] != null)
				instructionStages[DECODE_INSTRUCTION_POSITION].stage = DECODE;
			pcAddresses[DECODE_ADDRESS_POSITION] = pcAddresses[FETCH_ADDRESS_POSITION];
			if(instructionStages[DECODE_INSTRUCTION_POSITION] != null)
				pcAddresses[FETCH_ADDRESS_POSITION] = instructionStages[DECODE_INSTRUCTION_POSITION].valP;
		}
	}

	private static boolean decodeControl() {
		boolean decodeStall = false;
		if(instructionStages[DECODE_INSTRUCTION_POSITION] != null && !instructionStages[DECODE_INSTRUCTION_POSITION].bubble) {
			decodeStall = decode();
		}
		if(!decodeStall) {
			pcAddresses[EXECUTE_ADDRESS_POSITION] = pcAddresses[DECODE_ADDRESS_POSITION];
			pcAddresses[DECODE_ADDRESS_POSITION] = null;
			instructionStages[EXECUTE_INSTRUCTION_POSITION] = instructionStages[DECODE_INSTRUCTION_POSITION];
			instructionStages[DECODE_INSTRUCTION_POSITION] = null;
			if(instructionStages[EXECUTE_INSTRUCTION_POSITION] != null) 
				instructionStages[EXECUTE_INSTRUCTION_POSITION].stage = EXECUTE;
		} else {
			//puts a bubble
			pcAddresses[EXECUTE_ADDRESS_POSITION] = null;
			instructionStages[EXECUTE_INSTRUCTION_POSITION] = new Instruction(EXECUTE);
		}
		return decodeStall;
	}

	private static void executeControl() {
		if(instructionStages[EXECUTE_INSTRUCTION_POSITION] != null && !instructionStages[EXECUTE_INSTRUCTION_POSITION].bubble) {
			instructionStages[EXECUTE_INSTRUCTION_POSITION].exeFinished = true;
			DoubleWord newPredictedValP = null;
			try {
				newPredictedValP = execute();
			} catch(ArithmeticException e) {
				status = "HLT";
				throw new ProcessorException(e.getMessage(), instructionStages[EXECUTE_INSTRUCTION_POSITION]);
			}
			//misprediction
			if(!instructionStages[EXECUTE_INSTRUCTION_POSITION].instruction.equals("JALR") && !pcAddresses[DECODE_ADDRESS_POSITION].equals(newPredictedValP)) {
				pcAddresses[DECODE_ADDRESS_POSITION] = null;
				pcAddresses[FETCH_ADDRESS_POSITION] = newPredictedValP;
				instructionStages[DECODE_INSTRUCTION_POSITION] = new Instruction(DECODE);
				stopFetching = false;
				stopCount = -1;
			}
		}
		pcAddresses[MEMORY_ADDRESS_POSITION] = pcAddresses[EXECUTE_ADDRESS_POSITION];
		pcAddresses[EXECUTE_ADDRESS_POSITION] = null;
		instructionStages[MEMORY_INSTRUCTION_POSITION] = instructionStages[EXECUTE_INSTRUCTION_POSITION];
		instructionStages[EXECUTE_INSTRUCTION_POSITION] = null;
		if(instructionStages[MEMORY_INSTRUCTION_POSITION] != null) 
			instructionStages[MEMORY_INSTRUCTION_POSITION].stage = MEMORY;
	}

	private static void memoryControl() {
		if(instructionStages[MEMORY_INSTRUCTION_POSITION] != null && !instructionStages[MEMORY_INSTRUCTION_POSITION].bubble) {
			if(instructionStages[MEMORY_INSTRUCTION_POSITION].memory)
				instructionStages[MEMORY_INSTRUCTION_POSITION].memLoaded = true;
			try {
				memory();
			} catch(MemoryException e) {
				status = "HLT";
				throw new ProcessorException(e.getMessage(), instructionStages[MEMORY_INSTRUCTION_POSITION]);
			}
		}
		pcAddresses[WRITE_BACK_ADDRESS_POSITION] = pcAddresses[MEMORY_ADDRESS_POSITION];
		pcAddresses[MEMORY_ADDRESS_POSITION] = null;
		instructionStages[WRITE_BACK_INSTRUCTION_POSITION] = instructionStages[MEMORY_INSTRUCTION_POSITION];
		instructionStages[MEMORY_INSTRUCTION_POSITION] = null;
		if(instructionStages[WRITE_BACK_INSTRUCTION_POSITION] != null)  
			instructionStages[WRITE_BACK_INSTRUCTION_POSITION].stage = WRITE_BACK;
	}
	
	private static boolean writeBackControl() {
		boolean ret = false;
		if(instructionStages[WRITE_BACK_INSTRUCTION_POSITION] != null && !instructionStages[WRITE_BACK_INSTRUCTION_POSITION].bubble) {
			ret = true;
			writeBack();
		}
		if(instructionStages[WRITE_BACK_INSTRUCTION_POSITION] != null) 
			instructionStages[WRITE_BACK_INSTRUCTION_POSITION].stage = FINISHED;
		completedInstruction = instructionStages[WRITE_BACK_INSTRUCTION_POSITION];
		pcAddresses[WRITE_BACK_ADDRESS_POSITION] = null;
		instructionStages[WRITE_BACK_INSTRUCTION_POSITION] = null;
		return ret;
	}
	

	private static void setPC() {
		for(int i = WRITE_BACK_ADDRESS_POSITION; i >= 0; i--)
			if(pcAddresses[i] != null) {
				registerFile.set("pc", pcAddresses[i]);
				i= (-1);
			}
	}

	//returns predicted PC
	public static Instruction fetch() {
		Word nextInstruction = Memory.loadWord(pcAddresses[0].calculateValueSigned());
		boolean[] instructionArray = nextInstruction.bitArray;
		Instruction currentInstruction = new Instruction(instructionArray,pcAddresses[0]);
		if(currentInstruction.stop) { 
			stopFetching = true;
			stopCount = 4;
			return null;
		}
		validStop = (currentInstruction.instruction.equals("HALT"));
		currentInstruction.valP = predictPC(currentInstruction);
		if(currentInstruction.instruction.equals("JALR")) {
			JALRStall = true;
		}
		return currentInstruction;
	}

	private static DoubleWord predictPC(Instruction currentInstruction) {
		if(currentInstruction.instruction.equals("JAL")) {
			boolean[] constant = new boolean[21];
			System.arraycopy(currentInstruction.immediate, 1, constant, 1, 20);
			constant = ALU.signExtension(constant, false, 64);
			DoubleWord c = new DoubleWord(constant);
			return pcAddresses[FETCH_ADDRESS_POSITION].add(c);
		}
		return pcAddresses[0].addFour();
	}

	//returns true if stall is required
	public static boolean decode() {
		Instruction currentInstruction = Processor.instructionStages[0];
		DoubleWord RS1ValTemp = forward(currentInstruction.Rs1);
		DoubleWord RS2ValTemp = forward(currentInstruction.Rs2);
		if(RS1ValTemp == null || RS2ValTemp == null)
			return true;
		currentInstruction.RS1Val = RS1ValTemp;
		currentInstruction.RS2Val = RS2ValTemp;
		return false;
	}


	public static DoubleWord forward(String register) {
		if(register.equals("x0"))
			return registerFile.get(register);
		for(int i = EXECUTE_INSTRUCTION_POSITION; i <= WRITE_BACK_INSTRUCTION_POSITION; i++) {
			if(instructionStages[i] != null && !instructionStages[i].bubble && instructionStages[i].Rd.equals(register)) {
				if(instructionStages[i].memLoaded) {
					return (instructionStages[i].memory) ? instructionStages[i].MVal : instructionStages[i].EVal;
				} else {
					return null;
				}
			}
		}
		return registerFile.get(register);
	}


	//returns the new predicted valP, if it is different than the previous, the previous two values are bubbled out.
	public static DoubleWord execute() {
		Instruction currentInstruction = Processor.instructionStages[1];
		if(Instruction.isMType(currentInstruction.instruction))
			executeMExtension(currentInstruction);
		else {
			DoubleWord valE = null;
			boolean[] constant = null;
			DoubleWord c = null;
			Word w = null;
			switch(currentInstruction.instruction) {
			case "ADD": 
				valE = currentInstruction.RS1Val.add(currentInstruction.RS2Val);
				currentInstruction.EVal = valE; 
				break;
			case "SUB":
				valE = currentInstruction.RS1Val.subtract(currentInstruction.RS2Val);
				currentInstruction.EVal = valE; 
				currentInstruction.EVal = valE; 
				break;
			case "AND":
				valE = currentInstruction.RS1Val.and(currentInstruction.RS2Val);
				currentInstruction.EVal = valE; 
				break;
			case "OR":
				valE = currentInstruction.RS1Val.or(currentInstruction.RS2Val);
				currentInstruction.EVal = valE; 
				break;
			case "XOR":
				valE = currentInstruction.RS1Val.xor(currentInstruction.RS2Val);
				currentInstruction.EVal = valE; 
				break;	
			case "XORI":
				constant = new boolean[13];
				System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
				constant = ALU.signExtension(constant, false, 64);
				valE = new DoubleWord(ALU.XOR(currentInstruction.RS1Val.bitArray,constant));
				currentInstruction.EVal = valE;
				break;
			case "ORI":
				constant = new boolean[13];
				System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
				constant = ALU.signExtension(constant, false, 64);
				valE = new DoubleWord(ALU.OR(currentInstruction.RS1Val.bitArray,constant));
				currentInstruction.EVal = valE; 
				break;
			case "ANDI":
				constant = new boolean[13];
				System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
				constant = ALU.signExtension(constant, false, 64);
				valE = new DoubleWord(ALU.AND(currentInstruction.RS1Val.bitArray,constant));
				currentInstruction.EVal = valE; 
				break;
			case "BEQ":
				constant = new boolean[13];
				System.arraycopy(currentInstruction.immediate, 1, constant, 1, 12);
				constant = ALU.signExtension(constant, false, 64);
				c = new DoubleWord(constant);
				currentInstruction.valP = (currentInstruction.RS1Val.equals(currentInstruction.RS2Val)) ? pcAddresses[EXECUTE_ADDRESS_POSITION].add(c) : currentInstruction.valP;
				break;
			case "BNE":
				constant = new boolean[13];
				System.arraycopy(currentInstruction.immediate, 1, constant, 1, 12);
				constant = ALU.signExtension(constant, false, 64);
				c = new DoubleWord(constant);
				currentInstruction.valP = (!currentInstruction.RS1Val.equals(currentInstruction.RS2Val)) ? pcAddresses[EXECUTE_ADDRESS_POSITION].add(c) : currentInstruction.valP;
				break;	
			case "BLT":
				constant = new boolean[13];
				System.arraycopy(currentInstruction.immediate, 1, constant, 1, 12);
				constant = ALU.signExtension(constant, false, 64);
				c = new DoubleWord(constant);
				currentInstruction.valP = (currentInstruction.RS1Val.lessThan(currentInstruction.RS2Val,false)) ? pcAddresses[EXECUTE_ADDRESS_POSITION].add(c) : currentInstruction.valP;
				break;
			case "BLTU":
				constant = new boolean[13];
				System.arraycopy(currentInstruction.immediate, 1, constant, 1, 12);
				constant = ALU.signExtension(constant, false, 64);
				c = new DoubleWord(constant);
				currentInstruction.valP = (currentInstruction.RS1Val.lessThan(currentInstruction.RS2Val,true)) ? pcAddresses[EXECUTE_ADDRESS_POSITION].add(c) : currentInstruction.valP;
				break;	
			case "BGE":
				constant = new boolean[13];
				System.arraycopy(currentInstruction.immediate, 1, constant, 1, 12);
				constant = ALU.signExtension(constant, false, 64);
				c = new DoubleWord(constant);
				currentInstruction.valP = (!currentInstruction.RS1Val.lessThan(currentInstruction.RS2Val,false)) ? pcAddresses[EXECUTE_ADDRESS_POSITION].add(c) : currentInstruction.valP;
				break;
			case "BGEU":
				constant = new boolean[13];
				System.arraycopy(currentInstruction.immediate, 1, constant, 1, 12);
				constant = ALU.signExtension(constant, false, 64);
				c = new DoubleWord(constant);
				currentInstruction.valP = (!currentInstruction.RS1Val.lessThan(currentInstruction.RS2Val,true)) ? pcAddresses[EXECUTE_ADDRESS_POSITION].add(c) : currentInstruction.valP;
				break;	
			case "JAL":
				constant = new boolean[21];
				System.arraycopy(currentInstruction.immediate, 1, constant, 1, 20);
				constant = ALU.signExtension(constant, false, 64);
				c = new DoubleWord(constant);
				currentInstruction.valP = pcAddresses[EXECUTE_ADDRESS_POSITION].add(c);
				currentInstruction.EVal = pcAddresses[EXECUTE_ADDRESS_POSITION].addFour();
				break;
			case "LUI":
				constant = new boolean[32];
				System.arraycopy(currentInstruction.immediate, 12, constant, 12, 20);
				constant = ALU.signExtension(constant, false, 64);
				c = new DoubleWord(constant);
				currentInstruction.EVal = c;
				break;
			case "AUIPC":
				constant = new boolean[32];
				System.arraycopy(currentInstruction.immediate, 12, constant, 12, 20);
				constant = ALU.signExtension(constant, false, 64);
				c = new DoubleWord(constant);
				currentInstruction.EVal = pcAddresses[EXECUTE_ADDRESS_POSITION].add(c);
				break;
			case "SLT":
				currentInstruction.EVal = (currentInstruction.RS1Val.lessThan(currentInstruction.RS2Val,false)) ? new DoubleWord(1) : new DoubleWord();
				break;
			case "SLTU":
				currentInstruction.EVal = (currentInstruction.RS1Val.lessThan(currentInstruction.RS2Val,true)) ? new DoubleWord(1) : new DoubleWord();
				break;
			case "SLTI":
				constant = new boolean[12];
				System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
				constant = ALU.signExtension(constant, false, 64);
				c = new DoubleWord(constant);
				currentInstruction.EVal = (currentInstruction.RS1Val.lessThan(c,false)) ? new DoubleWord(1) : new DoubleWord();
				break;
			case "SLTIU":
				constant = new boolean[12];
				System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
				constant = ALU.signExtension(constant, false, 64);
				c = new DoubleWord(constant);
				currentInstruction.EVal = (currentInstruction.RS1Val.lessThan(c,true)) ? new DoubleWord(1) : new DoubleWord();
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
				constant = new boolean[12];
				System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
				constant = ALU.signExtension(constant, false, 32);
				w = new Word(constant);
				valE = new DoubleWord(currentInstruction.RS1Val.getWord(0).add(w),true);
				break;
			default:
				constant = new boolean[12];
				System.arraycopy(currentInstruction.immediate, 0, constant, 0, 12);
				constant = ALU.signExtension(constant, false, 64);
				valE = new DoubleWord(ALU.IADD(currentInstruction.RS1Val.bitArray,constant));
				currentInstruction.EVal = valE; 
				break;			
			}
		}
		if(currentInstruction.instruction.equals("JALR")) {
			Processor.pcAddresses[0] = currentInstruction.EVal;
			currentInstruction.valP = currentInstruction.EVal;
			currentInstruction.EVal = pcAddresses[EXECUTE_ADDRESS_POSITION].addFour();
			JALRStall = false;
			JALRTempStall = true;
			stopFetching = false;
		}
		return currentInstruction.valP;
	}

	private static void executeMExtension(Instruction currentInstruction) {
		DoubleWord valE = null;
		Word w = null;
		switch(currentInstruction.instruction) {
		case "MUL":
			valE = currentInstruction.RS1Val.mul(currentInstruction.RS2Val);
			currentInstruction.EVal = valE; 
			break;
		case "MULH": 
			valE = currentInstruction.RS1Val.upper(currentInstruction.RS2Val, true, true);
			currentInstruction.EVal = valE; 
			break;	
		case "MULHSU": 
			valE = currentInstruction.RS1Val.upper(currentInstruction.RS2Val, true, false);
			currentInstruction.EVal = valE; 
			break;	
		case "MULHU": 
			valE = currentInstruction.RS1Val.upper(currentInstruction.RS2Val, false, false);
			currentInstruction.EVal = valE; 
			break;
		case "DIV": 
			valE = currentInstruction.RS1Val.div(currentInstruction.RS2Val, true, false);
			currentInstruction.EVal = valE; 
			break;
		case "DIVU": 
			valE = currentInstruction.RS1Val.div(currentInstruction.RS2Val, false, false);
			currentInstruction.EVal = valE; 
			break;	
		case "REM": 
			valE = currentInstruction.RS1Val.div(currentInstruction.RS2Val, true, true);
			currentInstruction.EVal = valE; 
			break;
		case "REMU": 
			valE = currentInstruction.RS1Val.div(currentInstruction.RS2Val, false, true);
			currentInstruction.EVal = valE; 
			break;		
		case "MULW":
			w = currentInstruction.RS1Val.getWord(0).mul(currentInstruction.RS2Val.getWord(0));
			currentInstruction.EVal = new DoubleWord(w,true);
			break;	
		case "DIVW":
			w = currentInstruction.RS1Val.getWord(0).div(currentInstruction.RS2Val.getWord(0), true, false);
			currentInstruction.EVal = new DoubleWord(w,true);
			break;
		case "DIVUW":
			w = currentInstruction.RS1Val.getWord(0).div(currentInstruction.RS2Val.getWord(0), false, false);
			currentInstruction.EVal = new DoubleWord(w,true);
			break;
		case "REMW":
			w = currentInstruction.RS1Val.getWord(0).div(currentInstruction.RS2Val.getWord(0), true, true);
			currentInstruction.EVal = new DoubleWord(w,true);
			break;
		case "REMUW":
			w = currentInstruction.RS1Val.getWord(0).div(currentInstruction.RS2Val.getWord(0), false, true);
			currentInstruction.EVal = new DoubleWord(w,true);
			break;	
		}
	}

	public static void memory() {
		Instruction currentInstruction = Processor.instructionStages[2];
		switch(currentInstruction.instruction) {
		case "LD":
			DoubleWord memDW =  Memory.loadDoubleWord(currentInstruction.EVal.calculateValueSigned());
			currentInstruction.MVal = (memDW == null) ? null : memDW;
			break;
		case "SD": 
			Memory.storeDoubleWord(currentInstruction.EVal.calculateValueSigned(), currentInstruction.RS2Val);
			break;
		case "LW":
			Word memW =  Memory.loadWord(currentInstruction.EVal.calculateValueSigned());
			currentInstruction.MVal = (memW == null) ? null : new  DoubleWord(memW, true);
			break;
		case "LWU":
			Word memWU =  Memory.loadWord(currentInstruction.EVal.calculateValueSigned());
			currentInstruction.MVal = (memWU == null) ? null : new  DoubleWord(memWU, false);
			break;
		case "SW": 
			Memory.storeWord(currentInstruction.EVal.calculateValueSigned(), currentInstruction.RS2Val.getWord(0));
			break;
		case "LH":
			HalfWord memH =  Memory.loadHalfWord(currentInstruction.EVal.calculateValueSigned());
			currentInstruction.MVal = (memH == null) ? null : new  DoubleWord(memH, true);
			break;
		case "LHU":
			HalfWord memHU =  Memory.loadHalfWord(currentInstruction.EVal.calculateValueSigned());
			currentInstruction.MVal = (memHU == null) ? null : new  DoubleWord(memHU, false);
			break;
		case "SH": 
			Memory.storeHalfWord(currentInstruction.EVal.calculateValueSigned(), currentInstruction.RS2Val.getHalfWord(0));
			break;	
		case "LB":
			BYTE memB =  Memory.loadBYTE(currentInstruction.EVal.calculateValueSigned());
			currentInstruction.MVal = (memB == null) ? null : new  DoubleWord(memB, true);
			break;
		case "LBU":
			BYTE memBU =  Memory.loadBYTE(currentInstruction.EVal.calculateValueSigned());
			currentInstruction.MVal = (memBU == null) ? null : new  DoubleWord(memBU, false);
			break;
		case "SB": 
			Memory.storeBYTE(currentInstruction.EVal.calculateValueSigned(), currentInstruction.RS2Val.getBYTE(0));
			break;		

		}
	}

	public static void writeBack() {
		Instruction currentInstruction = Processor.instructionStages[3];
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
		default:	
			registerFile.set(currentInstruction.Rd, currentInstruction.EVal);
			break;	
		}
	}
	
	public static void initialize() {
		if(Compiler.compiled) {
			Memory.memory.clear();
			Processor.registerFile.set("pc",new DoubleWord(Long.parseLong(Compiler.start_address,16)));
			clearArrays();
			pcAddresses[0] = new DoubleWord(Long.parseLong(Compiler.start_address,16));
			status = "AOK";
			for(long l: Compiler.COMPILED_CONSTANTS.keySet()) {
				try {
					LittleEndian le = Compiler.COMPILED_CONSTANTS.get(l);
					if(le instanceof BYTE)
						Memory.storeBYTE(l, (BYTE)le);
					if(le instanceof HalfWord)
						Memory.storeHalfWord(l, (HalfWord)le);
					if(le instanceof Word)
						Memory.storeWord(l, (Word)le);
					if(le instanceof DoubleWord)
						Memory.storeDoubleWord(l, (DoubleWord)le);
				} catch(MemoryException e) {
					status = "HLT";
					exception = e.getMessage();
				}
			}
			stopCount = -1;
			registerFile.reset();
			JALRStall = false;
			stopFetching = false;
			JALRTempStall = false;
			exception = null;
		} else {
			status = "HLT";
		}
		Processor.initialMemory = Memory.createImage();
		Processor.initialRegisterFile = Processor.registerFile.createImage();
		finalMemory = stepBeforeMem = stepAfterMem = null;
		finalRegisterFile = stepBeforeReg = stepAfterReg = null;
		stopCount = -1;
		registerFile.reset();
		JALRStall = false;
		stopFetching = false;
		JALRTempStall = false;
		initialized = true;
		exceptionGenerated = false;
	}

	public static void clear() {
		Memory.memory.clear();
		clearArrays();
	}


	private static void clearArrays() {
		for(int i = 0; i < pcAddresses.length; i++)
			pcAddresses[i] = null;
		for(int i = 0; i < instructionStages.length; i++)
			instructionStages[i] = null;
	}

	public static void step() {
		if(status.equals("AOK")) {
			Processor.stepBeforeMem = Memory.createImage();
			Processor.stepBeforeReg = Processor.registerFile.createImage();
			try {
				while(status.equals("AOK") && Processor.pipeLineIncrement() == false) {
					if(status.equals("AOK"))
						status = (validInstruction()) ? "AOK" : "HLT";
				}
			} catch(ProcessorException e) {
				exception = e.getMessage();
				exceptionGenerated = true;
			} 
			if(status.equals("AOK"))
				status = (validInstruction()) ? "AOK" : "HLT";
			if(status.equals("AOK")) {
				Processor.stepAfterMem = Memory.createImage();
				Processor.stepAfterReg = Processor.registerFile.createImage();
			} else {
				Processor.stepBeforeMem = Processor.finalMemory = Memory.createImage();
				Processor.stepBeforeReg = Processor.finalRegisterFile = Processor.registerFile.createImage();
			}


		}

	}

	private static boolean validInstruction() {
		for(int i = 0; i < instructionStages.length; i++) {
			if(instructionStages[i] != null && !instructionStages[i].bubble)
				return true;
		}
		return false;
	}

	public static void run() {
		while(status.equals("AOK")) {
			step();
		}
		Processor.finalMemory = Memory.createImage();
		Processor.finalRegisterFile = Processor.registerFile.createImage();
	}
	public static String exception;
	public static boolean exceptionGenerated;
	public static void clockPulse() {
		if(status.equals("AOK")) {
			try {
				Processor.pipeLineIncrement();
			} catch(ProcessorException e) {
				exception = e.getMessage();
				exceptionGenerated = true;
			} 
		}
	}

	public static final String FETCH = "Fetch";
	public static final String DECODE = "Decode";
	public static final String EXECUTE = "Execute";
	public static final String MEMORY = "Memory";
	public static final String WRITE_BACK = "Write Back";
	public static final String FINISHED = "Finished";

	public static final int WRITE_BACK_INSTRUCTION_POSITION = 3;
	public static final int MEMORY_INSTRUCTION_POSITION = 2;
	public static final int EXECUTE_INSTRUCTION_POSITION = 1;
	public static final int DECODE_INSTRUCTION_POSITION = 0;

	public static final int WRITE_BACK_ADDRESS_POSITION = 4;
	public static final int MEMORY_ADDRESS_POSITION = 3;
	public static final int EXECUTE_ADDRESS_POSITION = 2;
	public static final int DECODE_ADDRESS_POSITION = 1;
	public static final int FETCH_ADDRESS_POSITION = 0;

}
