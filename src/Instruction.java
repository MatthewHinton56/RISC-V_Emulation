import java.util.HashMap;

public class Instruction {
	static {
		generateMaps();
		generateExtensionMap();
		generateMExtensionMap();
		generateInstructionArchetypes();
	}
	public static HashMap<String, String> U_OPCODE_TO_INSTRUCTION, UJ_OPCODE_TO_INSTRUCTION;
	public static HashMap<String, String> BRANCH_FUNCT3_TO_FUNCTION, STORE_FUNCT3_TO_FUNCTION,
	OP_FUNCT3_TO_FUNCTION, OP_32_FUNCT3_TO_FUNCTION, OP_32_IMM_FUNCT3_TO_FUNCTION, LOAD_FUNCT3_TO_FUNCTION, OP_IMM_FUNCT3_TO_FUNCTION,
	MISC_MEM_FUNCT3_TO_FUNCTION, SYSTEM_FUNCT3_TO_FUNCTION, JALR_FUNCT3_TO_FUNCTION, MOP_32_FUNCT3_TO_FUNCTION, MOP_FUNCT3_TO_FUNCTION;
	public static HashMap<String, String> INSTRUCTION_TO_EXTENSION;
	public static HashMap<String, String> INSTRUCTION_TO_ARCHETYPE, INSTRUCTION_TO_ARCHETYPE_M;

	boolean[] immediate;
	String Rd, Rs1, Rs2;
	String stage;
	DoubleWord RS1Val, RS2Val, EVal, MVal, valP;
	boolean conditionMet;
	//0 - RS1Val, 1 - RS2Val, 2 - EVal, 3 - MVal
	String type, instruction;
	boolean memory;
	boolean memLoaded, branch, exeFinished, bubble;
	public boolean stop;
	public Instruction(boolean[] instructionBitEncoding) {
		String opCode = bitToString(0,6, instructionBitEncoding);
		if(!contains(VALID_OPCODES, opCode)) {
			stop = true;
		}
		else if(contains(UTYPE, opCode))
			generateUType(instructionBitEncoding, opCode);
		else if(contains(UJTYPE,opCode)) 
			generateUJType(instructionBitEncoding, opCode);
		else if(contains(SBTYPE,opCode)) 
			generateSBType(instructionBitEncoding, opCode);
		else if(contains(STYPE,opCode))
			generateSType(instructionBitEncoding, opCode);
		else if(contains(RTYPE,opCode))
			generateRType(instructionBitEncoding, opCode);
		else
			generateIType(instructionBitEncoding, opCode);
		if(instruction == null) {
			stop = true;
		}
		memory = (opCode.equals(LOAD));
		memLoaded = !memory;
		bubble = false;
		conditionMet = true;
		stage = Processor.FETCH;
	}
	@Override
	public String toString() {
		if(bubble)
			return "BUBBLE";
		return "Instruction [instruction=" + instruction + "]";
	}
	public Instruction(String stage) {
		this.stage = stage;
		bubble = true;
		instruction = "BUBBLE";
	}
	
	private void generateIType(boolean[] instructionBitEncoding, String opCode) {
		immediate = new boolean[12];
		System.arraycopy(instructionBitEncoding, 20, immediate, 0, 12);
		type = "I";
		String funct3 = bitToString(12,14,instructionBitEncoding);
		switch(opCode) {
		case OP_32_IMM:
			instruction = OP_32_IMM_FUNCT3_TO_FUNCTION.get(funct3);
			break;
		case LOAD:
			instruction = LOAD_FUNCT3_TO_FUNCTION.get(funct3);
			break;
		case OP_IMM:
			instruction = OP_IMM_FUNCT3_TO_FUNCTION.get(funct3);
			break;
		case MISC_MEM:
			instruction = MISC_MEM_FUNCT3_TO_FUNCTION.get(funct3);
			break;
		case SYSTEM:
			instruction = SYSTEM_FUNCT3_TO_FUNCTION.get(funct3);
			break;
		case JALR:
			instruction = JALR_FUNCT3_TO_FUNCTION.get(funct3);
			break;
		}
		if(instruction.contains("|"))
			instruction = (!instructionBitEncoding[30]) ? instruction.substring(0, instruction.indexOf("|")) : instruction.substring(instruction.indexOf("|")+1);
		String Rd = getRegister(7,11,instructionBitEncoding); 
		System.out.println(Rd);
		String Rs1 = getRegister(15,19,instructionBitEncoding);
		this.Rd = Rd;
		this.Rs1 = Rs1;
		this.Rs2 = "x0";
	}

	private void generateRType(boolean[] instructionBitEncoding, String opCode) {
		type = "R";
		String funct3 = bitToString(12,14,instructionBitEncoding);
		boolean mType = instructionBitEncoding[25];
		switch(opCode) {
		case OP:
			instruction = (mType) ? MOP_FUNCT3_TO_FUNCTION.get(funct3) : OP_FUNCT3_TO_FUNCTION.get(funct3);
			break;
		case OP_32:
			instruction = (mType) ? MOP_32_FUNCT3_TO_FUNCTION.get(funct3) : OP_32_FUNCT3_TO_FUNCTION.get(funct3);
		}
		if(instruction.contains("|"))
			instruction = (!instructionBitEncoding[30]) ? instruction.substring(0, instruction.indexOf("|")) : instruction.substring(instruction.indexOf("|")+1);
		String Rd = getRegister(7,11,instructionBitEncoding); 
		String Rs1 = getRegister(15,19,instructionBitEncoding);
		String Rs2 = getRegister(20,24,instructionBitEncoding);
		this.Rd = Rd;
		this.Rs1 = Rs1;
		this.Rs2 = Rs2;
	}

	private void generateSType(boolean[] instructionBitEncoding, String opCode) {
		immediate = new boolean[12];
		System.arraycopy(instructionBitEncoding, 7, immediate, 0, 5);
		System.arraycopy(instructionBitEncoding, 25, immediate, 5, 7);
		type = "S";
		String funct3 = bitToString(12,14,instructionBitEncoding);
		switch(opCode) {
		case STORE:
			instruction = STORE_FUNCT3_TO_FUNCTION.get(funct3);
			break;
		}
		String Rs1 = getRegister(15,19,instructionBitEncoding);
		String Rs2 = getRegister(20,24,instructionBitEncoding);
		this.Rd = "x0";
		this.Rs1 = Rs1;
		this.Rs2 = Rs2;
	}

	private void generateSBType(boolean[] instructionBitEncoding, String opCode) {
		immediate = new boolean[13];
		System.arraycopy(instructionBitEncoding, 7, immediate, 11, 1);
		System.arraycopy(instructionBitEncoding, 8, immediate, 1, 4);
		System.arraycopy(instructionBitEncoding, 25, immediate, 5, 6);
		System.arraycopy(instructionBitEncoding, 31, immediate, 12, 1);
		String funct3 = bitToString(12,14,instructionBitEncoding);
		type = "SB";
		switch(opCode) {
		case BRANCH:
			instruction = BRANCH_FUNCT3_TO_FUNCTION.get(funct3);
			break;
		}
		branch = true;
		String Rs1 = getRegister(15,19,instructionBitEncoding);
		String Rs2 = getRegister(20,24,instructionBitEncoding);
		instruction = correctInstruction(instruction, instructionBitEncoding);
		this.Rd = "x0";
		this.Rs1 = Rs1;
		this.Rs2 = Rs2;
	}

	private void generateUJType(boolean[] instructionBitEncoding, String opCode) {
		immediate = new boolean[21];
		System.arraycopy(instructionBitEncoding, 12, immediate, 12, 8);
		System.arraycopy(instructionBitEncoding, 20, immediate, 11, 1);
		System.arraycopy(instructionBitEncoding, 21, immediate, 1, 10);
		System.arraycopy(instructionBitEncoding, 31, immediate, 20, 1);
		type = "UJ";
		instruction = UJ_OPCODE_TO_INSTRUCTION.get(opCode);
		String Rd = getRegister(7,11,instructionBitEncoding);
		this.Rd = Rd;
		this.Rs1 = "x0";
		this.Rs2 = "x0";
	}

	private void generateUType(boolean[] instructionBitEncoding, String opCode) {
		immediate = new boolean[32];
		System.arraycopy(instructionBitEncoding, 12, immediate, 12, 20);
		type = "U";
		instruction = U_OPCODE_TO_INSTRUCTION.get(opCode);
		String Rd = getRegister(7,11,instructionBitEncoding); 
		this.Rd = Rd;
		this.Rs1 = "x0";
		this.Rs2 = "x0";
	}

	//Creates bit string from start <= end, 0 <= start <= end, 0 <= end < bitArray.length
	public static String bitToString(int start, int end,boolean[] bitArray) {
		String bitString = "";
		for(; start <= end; start++) {
			if(bitArray[start])
				bitString+="1";
			else
				bitString+="0";
		}
		return bitString;	
	}

	public static boolean contains(String[] array, String s) {
		for(String c: array)
			if(s.equals(c))
				return true;
		return false;
	}

	public static String correctInstruction(String instruction, boolean[] instructionBitEncoding) {
		int barPoint = instruction.indexOf("|");
		if(barPoint > 0)
			instruction = (!instructionBitEncoding[30]) ? instruction.substring(0,barPoint) : instruction.substring(barPoint+1);
			int starPoint = instruction.indexOf("*");
			if(starPoint > 0)
				instruction = (!instructionBitEncoding[25]) ? instruction.substring(0,starPoint) : instruction.substring(starPoint+1);
				return instruction;	
	}

	public static final String STOP = "0000000";
	public static final String LUI = "1110110";
	public static final String AUIPC = "1110100";
	public static final String JAL = "1111011";
	public static final String BRANCH = "1100011";
	public static final String STORE = "1100010";
	public static final String OP = "1100110";
	public static final String OP_32 = "1101110";
	public static final String OP_32_IMM = "1101100";
	public static final String LOAD = "1100000";
	public static final String OP_IMM = "1100100";
	public static final String MISC_MEM = "1111000";
	public static final String SYSTEM = "1100111";
	public static final String JALR = "1110011";
	public static final String[] VALID_OPCODES = {LUI, AUIPC, JAL, BRANCH, STORE, OP, OP_32, OP_32_IMM, LOAD, OP_IMM, MISC_MEM, SYSTEM, JALR};
	public static final String[] UTYPE = {LUI, AUIPC};
	public static final String[] UJTYPE = {JAL};
	public static final String[] SBTYPE = {BRANCH};
	public static final String[] STYPE = {STORE};
	public static final String[] RTYPE = {OP, OP_32 };
	public static final String[] ITYPE = { OP_32_IMM, LOAD, OP_IMM, MISC_MEM, SYSTEM, JALR};

	//Values are flipped as the zero index comes first
	public static final String ZERO = "000";
	public static final String ONE = "100";
	public static final String TWO = "010";
	public static final String THREE = "110";
	public static final String FOUR = "001";
	public static final String FIVE = "101";
	public static final String SIX = "011";
	public static final String SEVEN = "111";
	
	public static final String[] NON_TRADITIONAL_PC = {JALR, JAL, BRANCH};
	public static final String[] DATA_STALL = {LOAD};


	public static void generateMaps() {
		U_OPCODE_TO_INSTRUCTION = new HashMap<String, String> ();
		U_OPCODE_TO_INSTRUCTION.put(LUI, "LUI");
		U_OPCODE_TO_INSTRUCTION.put(AUIPC, "AUIPC");

		UJ_OPCODE_TO_INSTRUCTION = new HashMap<String, String> ();
		UJ_OPCODE_TO_INSTRUCTION.put(JAL, "JAL");

		BRANCH_FUNCT3_TO_FUNCTION = new HashMap<String, String> ();
		BRANCH_FUNCT3_TO_FUNCTION.put(ZERO, "BEQ");
		BRANCH_FUNCT3_TO_FUNCTION.put(ONE, "BNE");
		BRANCH_FUNCT3_TO_FUNCTION.put(FOUR, "BLT");
		BRANCH_FUNCT3_TO_FUNCTION.put(FIVE, "BGE");
		BRANCH_FUNCT3_TO_FUNCTION.put(SIX, "BLTU");
		BRANCH_FUNCT3_TO_FUNCTION.put(SEVEN, "BGEU");

		STORE_FUNCT3_TO_FUNCTION = new HashMap<String, String> ();
		STORE_FUNCT3_TO_FUNCTION.put(ZERO, "SB");
		STORE_FUNCT3_TO_FUNCTION.put(ONE, "SH");
		STORE_FUNCT3_TO_FUNCTION.put(TWO, "SW");
		STORE_FUNCT3_TO_FUNCTION.put(THREE, "SD");

		OP_IMM_FUNCT3_TO_FUNCTION = new HashMap<String, String> ();
		OP_IMM_FUNCT3_TO_FUNCTION.put(ZERO, "ADDI");
		OP_IMM_FUNCT3_TO_FUNCTION.put(TWO, "SLTI");
		OP_IMM_FUNCT3_TO_FUNCTION.put(THREE, "SLTIU");
		OP_IMM_FUNCT3_TO_FUNCTION.put(FOUR, "XORI");
		OP_IMM_FUNCT3_TO_FUNCTION.put(SIX, "ORI");
		OP_IMM_FUNCT3_TO_FUNCTION.put(SEVEN, "ANDI");
		OP_IMM_FUNCT3_TO_FUNCTION.put(ONE, "SLLI");
		OP_IMM_FUNCT3_TO_FUNCTION.put(FIVE, "SRLI|SRAI");//Based on value in imm

		OP_FUNCT3_TO_FUNCTION = new HashMap<String, String> ();
		OP_FUNCT3_TO_FUNCTION.put(ZERO, "ADD|SUB");
		OP_FUNCT3_TO_FUNCTION.put(ONE, "SLL");
		OP_FUNCT3_TO_FUNCTION.put(TWO, "SLT");
		OP_FUNCT3_TO_FUNCTION.put(THREE, "SLTU");
		OP_FUNCT3_TO_FUNCTION.put(FOUR, "XOR");
		OP_FUNCT3_TO_FUNCTION.put(FIVE, "SRL|SRA");	
		OP_FUNCT3_TO_FUNCTION.put(SIX, "OR");
		OP_FUNCT3_TO_FUNCTION.put(SEVEN, "AND");

		OP_32_FUNCT3_TO_FUNCTION = new HashMap<String, String> ();
		OP_32_FUNCT3_TO_FUNCTION.put(ZERO, "ADDW|SUBW");
		OP_32_FUNCT3_TO_FUNCTION.put(ONE, "SLLW");
		OP_32_FUNCT3_TO_FUNCTION.put(FIVE, "SRLW|SRAW");

		OP_32_IMM_FUNCT3_TO_FUNCTION = new HashMap<String, String> ();
		OP_32_IMM_FUNCT3_TO_FUNCTION.put(ZERO, "ADDIW");
		OP_32_IMM_FUNCT3_TO_FUNCTION.put(ONE, "SLLIW");
		OP_32_IMM_FUNCT3_TO_FUNCTION.put(FIVE, "SRLIW|SRAIW");

		LOAD_FUNCT3_TO_FUNCTION = new HashMap<String, String> ();
		LOAD_FUNCT3_TO_FUNCTION.put(ZERO, "LB");
		LOAD_FUNCT3_TO_FUNCTION.put(ONE, "LH");
		LOAD_FUNCT3_TO_FUNCTION.put(TWO, "LW");
		LOAD_FUNCT3_TO_FUNCTION.put(THREE, "LD");
		LOAD_FUNCT3_TO_FUNCTION.put(FOUR, "LBU");
		LOAD_FUNCT3_TO_FUNCTION.put(FIVE, "LHU");	
		LOAD_FUNCT3_TO_FUNCTION.put(SIX, "LWU");

		MISC_MEM_FUNCT3_TO_FUNCTION = new HashMap<String, String> ();
		MISC_MEM_FUNCT3_TO_FUNCTION.put(ZERO, "FENCE");
		MISC_MEM_FUNCT3_TO_FUNCTION.put(ONE, "FENCE.I");

		SYSTEM_FUNCT3_TO_FUNCTION = new HashMap<String, String> ();
		SYSTEM_FUNCT3_TO_FUNCTION.put(ZERO, "ECALL*EBREAK");
		SYSTEM_FUNCT3_TO_FUNCTION.put(ONE, "CSRRW");
		SYSTEM_FUNCT3_TO_FUNCTION.put(TWO, "CSRRS");
		SYSTEM_FUNCT3_TO_FUNCTION.put(THREE, "CSRRC");
		SYSTEM_FUNCT3_TO_FUNCTION.put(FIVE, "CSRRWI");
		SYSTEM_FUNCT3_TO_FUNCTION.put(SIX, "CSRRSI");	
		SYSTEM_FUNCT3_TO_FUNCTION.put(SEVEN, "CSRRCI");

		JALR_FUNCT3_TO_FUNCTION = new HashMap<String, String> ();
		JALR_FUNCT3_TO_FUNCTION.put(ZERO, "JALR");
	}
	
	private static void generateMExtensionMap() {
		MOP_FUNCT3_TO_FUNCTION = new HashMap<String, String> ();
		MOP_FUNCT3_TO_FUNCTION.put(ZERO, "MUL");
		MOP_FUNCT3_TO_FUNCTION.put(ONE, "MULH");
		MOP_FUNCT3_TO_FUNCTION.put(TWO, "MULHSU");
		MOP_FUNCT3_TO_FUNCTION.put(THREE, "MULHU");
		MOP_FUNCT3_TO_FUNCTION.put(FOUR, "DIV");
		MOP_FUNCT3_TO_FUNCTION.put(FIVE, "DIVU");	
		MOP_FUNCT3_TO_FUNCTION.put(SIX, "REM");
		MOP_FUNCT3_TO_FUNCTION.put(SEVEN, "REMU");
		
		MOP_32_FUNCT3_TO_FUNCTION = new HashMap<String, String> ();
		MOP_32_FUNCT3_TO_FUNCTION.put(ZERO, "MULW");
		MOP_32_FUNCT3_TO_FUNCTION.put(FOUR, "DIVW");
		MOP_32_FUNCT3_TO_FUNCTION.put(FIVE, "DIVW");	
		MOP_32_FUNCT3_TO_FUNCTION.put(SIX, "REMW");
		MOP_32_FUNCT3_TO_FUNCTION.put(SEVEN, "REMUW");
	}
	
	private static void generateExtensionMap() {
		INSTRUCTION_TO_EXTENSION = new HashMap<String, String> ();
		INSTRUCTION_TO_EXTENSION.put("MUL", "M");
		INSTRUCTION_TO_EXTENSION.put("MULH", "M");
		INSTRUCTION_TO_EXTENSION.put("MULHSU", "M");
		INSTRUCTION_TO_EXTENSION.put("MULHU", "M");
		INSTRUCTION_TO_EXTENSION.put("DIV", "M");
		INSTRUCTION_TO_EXTENSION.put("DIVU", "M");
		INSTRUCTION_TO_EXTENSION.put("REM", "M");
		INSTRUCTION_TO_EXTENSION.put("REMU", "M");
		
		INSTRUCTION_TO_EXTENSION.put("MUL", "M");
		INSTRUCTION_TO_EXTENSION.put("DIV", "M");
		INSTRUCTION_TO_EXTENSION.put("DIVU", "M");
		INSTRUCTION_TO_EXTENSION.put("REM", "M");
		INSTRUCTION_TO_EXTENSION.put("REMU", "M");
	}
	
	public static String getExtension(String instruction) {
		return INSTRUCTION_TO_EXTENSION.getOrDefault(instruction, "I");
	}
	
	
	public static HashMap<String, String> getOpMap(String instruction) {
		String opCode = InstructionBuilder.INSTRUCTION_TO_OPCODE.get(instruction);
		System.out.println(instruction+" "+ opCode);
		if(opCode.equals(OP)) {
			switch(getExtension(instruction)) {
			case "M":
				return Instruction.MOP_FUNCT3_TO_FUNCTION;
			default:
				return Instruction.OP_FUNCT3_TO_FUNCTION;
			}
		}
		switch(getExtension(instruction)) {
		case "M":
			return Instruction.MOP_32_FUNCT3_TO_FUNCTION;
		default:
			return Instruction.OP_32_FUNCT3_TO_FUNCTION;
		}
		
		
		
	}
	
	//end - start + 1 == 5
	public static String getRegister(int start, int end, boolean[] bitArray) {
		int val = 0;
		for(int pos = start; pos <= end; pos++) {
			if(bitArray[pos])
				val += ((long)Math.pow(2, pos-start));
		}
		return "x" + val;
	}

	
	public static String[] M_EXTENSION_INSTRUCTION = {"MUL", "MULH", "MULHSU", "MULHU", "DIV", "DIVU", "REM", "REMU", "MULW", "DIVW", "DIVUW", "REMW", "REMUW"};
	
	public static boolean isMType(String instruction) {
		return Instruction.contains(M_EXTENSION_INSTRUCTION, instruction);
	}
	
	private static void generateInstructionArchetypes() {
		INSTRUCTION_TO_ARCHETYPE = new HashMap<String, String> ();
		INSTRUCTION_TO_ARCHETYPE.put("ADD", "add rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("ADDW", "addw rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("ADDI", "addi rd, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("ADDIW", "addiw rd, rs1, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("AND", "and rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("ANDI", "andi rd, rs1, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("AUIPC", "auipc rd, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("BEQ", "beq rs1, rs2, imm");
		INSTRUCTION_TO_ARCHETYPE.put("BNE", "bne rs1, rs2, imm");
		INSTRUCTION_TO_ARCHETYPE.put("BGE", "bge rs1, rs2, imm");
		INSTRUCTION_TO_ARCHETYPE.put("BGEU", "bgeu rs1, rs2, imm");
		INSTRUCTION_TO_ARCHETYPE.put("BLT", "blt rs1, rs2, imm");
		INSTRUCTION_TO_ARCHETYPE.put("BLTU", "bltu rs1, rs2, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("JAL", "jal rd, imm");
		INSTRUCTION_TO_ARCHETYPE.put("JALR", "jalr rd, rs1, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("LB", "lb rd, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("LBU", "lbu rd, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("LH", "lh rd, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("LHU", "lhu rd, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("LW", "lw rd, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("LWU", "lwu rd, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("LD", "ld rd, rs1, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("SB", "sb rs2, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("SH", "sh rs2, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("SW", "sw rs2, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("SD", "sd rs2, rs1, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("LUI", "lui rd, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("OR", "or rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("ORI", "ori rd, rs1, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("XOR", "xor rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("XORI", "xori rd, rs1, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("SLL", "sll rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("SLLW", "sllw rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("SLLI", "slli rd, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("SLLIW", "slliw rd, rs1, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("SRA", "sra rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("SRAW", "sraw rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("SRAI", "srai rd, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("SRAIW", "sraiw rd, rs1, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("SRL", "sra rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("SRLW", "sraw rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("SRLI", "srai rd, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("SRLIW", "sraiw rd, rs1, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("SLT", "slt rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("SLTW", "sltw rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("SLTI", "slti rd, rs1, imm");
		INSTRUCTION_TO_ARCHETYPE.put("SLTIW", "sltiw rd, rs1, imm");
		
		INSTRUCTION_TO_ARCHETYPE.put("SUB", "sub rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE.put("SUBW", "subw rd, rs1, rs2");
		
		INSTRUCTION_TO_ARCHETYPE_M = new HashMap<String, String> ();
		
		INSTRUCTION_TO_ARCHETYPE_M.put("MUL", "mul rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE_M.put("MULH", "mulh rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE_M.put("MULHSU", "mulhsu rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE_M.put("MULHU", "mulhu rd, rs1, rs2");
		
		INSTRUCTION_TO_ARCHETYPE_M.put("DIV", "div rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE_M.put("DIVU", "divu rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE_M.put("DIVUW", "divuw rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE_M.put("DIVW", "divw rd, rs1, rs2");
		
		INSTRUCTION_TO_ARCHETYPE_M.put("REM", "rem rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE_M.put("REMU", "remu rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE_M.put("REMUW", "remuw rd, rs1, rs2");
		INSTRUCTION_TO_ARCHETYPE_M.put("REMW", "remw rd, rs1, rs2");
	}
	
	
}
