import java.util.HashMap;

public class InstructionBuilder {
	public static final HashMap<String, String> INSTRUCTION_TO_OPCODE = new HashMap<String, String>();
	public static final HashMap<String, String> INSTRUCTION_TO_FUNCT7 = new HashMap<String, String>();
	static
	{
		generateMaps();
	}
	private static void generateMaps() {
		INSTRUCTION_TO_OPCODE.put("LUI", Instruction.LUI);

		INSTRUCTION_TO_OPCODE.put("AUIPC", Instruction.AUIPC);

		INSTRUCTION_TO_OPCODE.put("JAL", Instruction.JAL);

		INSTRUCTION_TO_OPCODE.put("BEQ", Instruction.BRANCH);
		INSTRUCTION_TO_OPCODE.put("BNE", Instruction.BRANCH);
		INSTRUCTION_TO_OPCODE.put("BLT", Instruction.BRANCH);
		INSTRUCTION_TO_OPCODE.put("BGE", Instruction.BRANCH);
		INSTRUCTION_TO_OPCODE.put("BLTU", Instruction.BRANCH);
		INSTRUCTION_TO_OPCODE.put("BGEU", Instruction.BRANCH);

		INSTRUCTION_TO_OPCODE.put("SB", Instruction.STORE);
		INSTRUCTION_TO_OPCODE.put("SH", Instruction.STORE);
		INSTRUCTION_TO_OPCODE.put("SW", Instruction.STORE);
		INSTRUCTION_TO_OPCODE.put("SD", Instruction.STORE);

		INSTRUCTION_TO_OPCODE.put("ADDI", Instruction.OP_IMM);
		INSTRUCTION_TO_OPCODE.put("SLTI", Instruction.OP_IMM);
		INSTRUCTION_TO_OPCODE.put("SLTIU", Instruction.OP_IMM);
		INSTRUCTION_TO_OPCODE.put("XORI", Instruction.OP_IMM);
		INSTRUCTION_TO_OPCODE.put("ORI", Instruction.OP_IMM);
		INSTRUCTION_TO_OPCODE.put("ANDI", Instruction.OP_IMM);
		INSTRUCTION_TO_OPCODE.put("SLLI", Instruction.OP_IMM);
		INSTRUCTION_TO_OPCODE.put("SRLI", Instruction.OP_IMM);
		INSTRUCTION_TO_OPCODE.put("SRAI", Instruction.OP_IMM);

		INSTRUCTION_TO_OPCODE.put("ADD", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("SUB", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("SLL", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("SLT", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("SLTU", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("XOR", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("SRL", Instruction.OP);	
		INSTRUCTION_TO_OPCODE.put("SRA", Instruction.OP);	
		INSTRUCTION_TO_OPCODE.put("OR", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("AND", Instruction.OP);

		INSTRUCTION_TO_OPCODE.put("ADDW", Instruction.OP_32);
		INSTRUCTION_TO_OPCODE.put("SUBW", Instruction.OP_32);
		INSTRUCTION_TO_OPCODE.put("SLLW", Instruction.OP_32);
		INSTRUCTION_TO_OPCODE.put("SRAW", Instruction.OP_32);
		INSTRUCTION_TO_OPCODE.put("SRLW", Instruction.OP_32);
		
		INSTRUCTION_TO_OPCODE.put("SLLIW", Instruction.OP_32_IMM);
		INSTRUCTION_TO_OPCODE.put("ADDIW", Instruction.OP_32_IMM);
		INSTRUCTION_TO_OPCODE.put("SRAIW", Instruction.OP_32_IMM);
		INSTRUCTION_TO_OPCODE.put("SRLIW", Instruction.OP_32_IMM);

		INSTRUCTION_TO_OPCODE.put("LB", Instruction.LOAD);
		INSTRUCTION_TO_OPCODE.put("LH", Instruction.LOAD);
		INSTRUCTION_TO_OPCODE.put("LW", Instruction.LOAD);
		INSTRUCTION_TO_OPCODE.put("LD", Instruction.LOAD);
		INSTRUCTION_TO_OPCODE.put("LBU", Instruction.LOAD);
		INSTRUCTION_TO_OPCODE.put("LHU", Instruction.LOAD);	
		INSTRUCTION_TO_OPCODE.put("LWU", Instruction.LOAD);

		INSTRUCTION_TO_OPCODE.put("FENCE", Instruction.MISC_MEM);
		INSTRUCTION_TO_OPCODE.put("FENCE.I", Instruction.MISC_MEM);

		INSTRUCTION_TO_OPCODE.put("ECALL", Instruction.SYSTEM);
		INSTRUCTION_TO_OPCODE.put("EBREAK", Instruction.SYSTEM);
		INSTRUCTION_TO_OPCODE.put("CSRRW", Instruction.SYSTEM);
		INSTRUCTION_TO_OPCODE.put("CSRRS", Instruction.SYSTEM);
		INSTRUCTION_TO_OPCODE.put("CSRRC", Instruction.SYSTEM);
		INSTRUCTION_TO_OPCODE.put("CSRRWI", Instruction.SYSTEM);
		INSTRUCTION_TO_OPCODE.put("CSRRSI", Instruction.SYSTEM);	
		INSTRUCTION_TO_OPCODE.put("CSRRCI", Instruction.SYSTEM);

		INSTRUCTION_TO_OPCODE.put("JALR", Instruction.JALR);

		INSTRUCTION_TO_OPCODE.put("HALT", Instruction.STOP);
		
		INSTRUCTION_TO_FUNCT7.put("SUB", "0000010");
		INSTRUCTION_TO_FUNCT7.put("SRA", "0000010");
		INSTRUCTION_TO_FUNCT7.put("SUBW", "0000010");
		INSTRUCTION_TO_FUNCT7.put("SRAW", "0000010");
		
		//M Extension
		
		INSTRUCTION_TO_OPCODE.put("MUL", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("MULH", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("MULHSU", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("MULHU", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("DIV", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("DIVU", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("REM", Instruction.OP);
		INSTRUCTION_TO_OPCODE.put("REMU", Instruction.OP);
		
		INSTRUCTION_TO_OPCODE.put("MULW", Instruction.OP_32);
		INSTRUCTION_TO_OPCODE.put("DIVW", Instruction.OP_32);
		INSTRUCTION_TO_OPCODE.put("DIVUW", Instruction.OP_32);
		INSTRUCTION_TO_OPCODE.put("REMW", Instruction.OP_32);
		INSTRUCTION_TO_OPCODE.put("REMUW", Instruction.OP_32);
		
		INSTRUCTION_TO_FUNCT7.put("MUL", "1000000");
		INSTRUCTION_TO_FUNCT7.put("MULH", "1000000");
		INSTRUCTION_TO_FUNCT7.put("MULHSU", "1000000");
		INSTRUCTION_TO_FUNCT7.put("MULHU", "1000000");
		INSTRUCTION_TO_FUNCT7.put("DIV", "1000000");
		INSTRUCTION_TO_FUNCT7.put("DIVU", "1000000");
		INSTRUCTION_TO_FUNCT7.put("REM", "1000000");
		INSTRUCTION_TO_FUNCT7.put("REMU", "1000000");
		
		INSTRUCTION_TO_FUNCT7.put("MULW", "1000000");
		INSTRUCTION_TO_FUNCT7.put("DIVW", "1000000");
		INSTRUCTION_TO_FUNCT7.put("DIVUW", "1000000");
		INSTRUCTION_TO_FUNCT7.put("REMW", "1000000");
		INSTRUCTION_TO_FUNCT7.put("REMUW", "1000000");
	}

	public static String getFunct3(String function, HashMap<String, String> map) {
		for(String funct3: map.keySet()) {
			String instruct = map.get(funct3);
			System.out.println(function+" "+instruct);
			if(instruct.contains("|"))
			{
				String upper = instruct.substring(instruct.indexOf("|")+1);
				String lower = instruct.substring(0, instruct.indexOf("|"));
				if(function.equals(upper) || function.equals(lower))
					return funct3;
			} else if(instruct.contains("*")) {
				String upper = instruct.substring(instruct.indexOf("*")+1);
				String lower = instruct.substring(0, instruct.indexOf("*"));
				if(function.equals(upper) || function.equals(lower))
					return funct3;
			} else {
				
				if(instruct.equals(function)) {
					System.out.println(instruct);
					return funct3;
				}
			}
		}
		return null;
	}


	//Precondition: Rd, Rs1, Rs2, and imm already converted to bitStrings
	public static boolean[] generateInstruction(String function, boolean[] Rd, boolean[] Rs1, boolean[] Rs2, boolean[] imm) {
		String opCode = INSTRUCTION_TO_OPCODE.get(function);
		if(opCode.equals(Instruction.STOP)) {
			return new boolean[32];
		}
		if(Instruction.contains(Instruction.UTYPE, opCode))
			return generateUType(opCode, Rd, imm);

		else if(Instruction.contains(Instruction.UJTYPE,opCode)) 
			return generateUJType(opCode, Rd, imm);

		else if(Instruction.contains(Instruction.SBTYPE,opCode)) { 
			String funct3 = "";
			switch(opCode) {
			case Instruction.BRANCH:
				funct3 = getFunct3(function,Instruction.BRANCH_FUNCT3_TO_FUNCTION);
				break;
			}
			return generateSBType(opCode, funct3, Rs1, Rs2,imm);

		} else if(Instruction.contains(Instruction.STYPE,opCode)) {
			String funct3 = "";
			switch(opCode) {
			case Instruction.STORE:
				funct3 = getFunct3(function,Instruction.STORE_FUNCT3_TO_FUNCTION);
				break;
			}
			return generateSType(opCode, funct3, Rs1, Rs2,imm);

		} else if(Instruction.contains(Instruction.RTYPE,opCode)) {
			String funct3 = "";
			String funct7 = "";
			switch(opCode) {
			case Instruction.OP:
				funct3 = getFunct3(function,Instruction.getOpMap(function));
				funct7 = INSTRUCTION_TO_FUNCT7.getOrDefault(function, InstructionBuilder.DEFAULTRTYPEFUNCT7);
				break;
			case Instruction.OP_32:
				funct3 = getFunct3(function,Instruction.getOpMap(function));
				funct7 = INSTRUCTION_TO_FUNCT7.getOrDefault(function, InstructionBuilder.DEFAULTRTYPEFUNCT7);
				break;
			}
			return generateRType(opCode, funct3,funct7, Rd, Rs1, Rs2);
		}
		else {
			String funct3 = "";
			switch(opCode) {
			case Instruction.OP_32_IMM:
				funct3 = getFunct3(function,Instruction.OP_32_IMM_FUNCT3_TO_FUNCTION);
				break;
			case Instruction.LOAD:
				funct3 = getFunct3(function,Instruction.LOAD_FUNCT3_TO_FUNCTION);
				break;
			case Instruction.OP_IMM:
				funct3 = getFunct3(function,Instruction.OP_IMM_FUNCT3_TO_FUNCTION);
				break;
			case Instruction.MISC_MEM:
				funct3 = getFunct3(function,Instruction.MISC_MEM_FUNCT3_TO_FUNCTION);
				break;
			case Instruction.SYSTEM:
				funct3 = getFunct3(function,Instruction.SYSTEM_FUNCT3_TO_FUNCTION);
				break;
			case Instruction.JALR:
				funct3 = Instruction.ZERO;
				break;
			}
			return generateIType(opCode, funct3, Rd, Rs1, Rs2, imm);
		}

	}

	private static boolean[] generateIType(String opCode, String funct3, boolean[] Rd, boolean[] Rs1, boolean[] Rs2,
			boolean[] imm) {
		boolean[] opCodeBit = stringToBit(opCode);
		System.out.println(opCode);
		boolean[] funct3Bit = stringToBit(funct3);
		boolean[] instruct = new boolean[32];
		System.arraycopy(opCodeBit, 0, instruct, 0, 7);
		System.arraycopy(Rd, 0, instruct, 7, 5);
		System.arraycopy(funct3Bit, 0, instruct, 12, 3);
		System.arraycopy(Rs1, 0, instruct, 15, 5);
		System.arraycopy(imm, 0, instruct, 20, 12);
		return instruct;
	}

	private static boolean[] generateRType(String opCode, String funct3, String funct7, boolean[] Rd, boolean[] Rs1,
			boolean[] Rs2) {
		boolean[] opCodeBit = stringToBit(opCode);
		boolean[] funct3Bit = stringToBit(funct3);
		boolean[] funct7Bit = stringToBit(funct7);
		boolean[] instruct = new boolean[32];
		System.arraycopy(opCodeBit, 0, instruct, 0, 7);
		System.arraycopy(Rd, 0, instruct, 7, 5);
		System.arraycopy(funct3Bit, 0, instruct, 12, 3);
		System.arraycopy(Rs1, 0, instruct, 15, 5);
		System.arraycopy(Rs2, 0, instruct, 20, 5);
		System.arraycopy(funct7Bit, 0, instruct, 25, 7);
		return instruct;
	}

	private static boolean[] generateSType(String opCode, String funct3, boolean[] Rs1, boolean[] Rs2, boolean[] imm) {
		boolean[] opCodeBit = stringToBit(opCode);
		boolean[] funct3Bit = stringToBit(funct3);
		boolean[] instruct = new boolean[32];
		System.arraycopy(opCodeBit, 0, instruct, 0, 7);
		System.arraycopy(imm, 0, instruct, 7, 5);
		System.arraycopy(funct3Bit, 0, instruct, 12, 3);
		System.arraycopy(Rs1, 0, instruct, 15, 5);
		System.arraycopy(Rs2, 0, instruct, 20, 5);
		System.arraycopy(imm, 5, instruct, 25, 7);
		return instruct;
	}

	private static boolean[] generateSBType(String opCode, String funct3, boolean[] Rs1, boolean[] Rs2, boolean[] imm) {
		boolean[] opCodeBit = stringToBit(opCode);
		boolean[] funct3Bit = stringToBit(funct3);
		boolean[] instruct = new boolean[32];
		System.arraycopy(opCodeBit, 0, instruct, 0, 7);
		System.arraycopy(imm, 11, instruct, 7, 1);
		System.arraycopy(imm, 1, instruct, 8, 4);
		System.arraycopy(funct3Bit, 0, instruct, 12, 3);
		System.arraycopy(Rs1, 0, instruct, 15, 5);
		System.arraycopy(Rs2, 0, instruct, 20, 5);
		System.arraycopy(imm, 5, instruct, 25, 6);
		System.arraycopy(imm, 12, instruct, 31, 1);
		return instruct;
	}
	
	private static boolean[] generateUJType(String opCode, boolean[] Rd, boolean[] imm) {
		boolean[] opCodeBit = stringToBit(opCode);
		boolean[] instruct = new boolean[32];
		System.arraycopy(opCodeBit, 0, instruct, 0, 7);
		System.arraycopy(Rd, 0, instruct, 7, 5);
		System.arraycopy(imm, 12, instruct, 12, 8);
		System.arraycopy(imm, 11, instruct, 20, 1);
		System.arraycopy(imm, 1, instruct, 21, 10);
		System.arraycopy(imm, 20, instruct, 31, 1);
		return instruct;
	}

	private static boolean[] generateUType(String opCode, boolean[] Rd, boolean[] imm) {
		boolean[] opCodeBit = stringToBit(opCode);
		boolean[] instruct = new boolean[32];
		System.arraycopy(opCodeBit, 0, instruct, 0, 7);
		System.arraycopy(Rd, 0, instruct, 7, 5);
		System.arraycopy(imm, 0, instruct, 12, 20);
		return instruct;

	}
	public static boolean[] stringToBit(String s) {
		boolean[] array = new boolean[s.length()];
		for(int i = 0; i < s.length(); i++) {
			if(s.charAt(i) == '1')
				array[i] = true;
			else
				array[i] = false;
		}
		return array;
	}

	public static final String DEFAULTRTYPEFUNCT7  = "0000000";
}
