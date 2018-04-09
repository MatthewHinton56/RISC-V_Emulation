import java.util.Arrays;
import java.util.HashMap;

public class Instruction {
	//OPCODES


	public static final boolean[] OP_IMM = {true, true, false, false, true, false, false};
	public static final boolean[] LUI = {true, true, true, false, true, true, false};
	public static final boolean[] AUIPC = {true, true, true, false, true, false, false};
	public static final boolean[] OP = {true, true, false, false, true, true, false};
	public static final boolean[] JAL = {true, true, true, true, false, true, true};
	public static final boolean[] JALR = {true, true, true, false, false, true, true};
	public static final boolean[] BRANCH = {true, true, false, false, false, true, true};
	public static final boolean[] LOAD = {true, true, false, false, false, false, false};
	public static final boolean[] STORE = {true, true, false, false, false, true, false};
	public static final boolean[] MISC_MEM = {true, true, true, true, false, false, false};
	public static final boolean[] SYSTEM = {true, true, false, false, true, true, true};
	public static final boolean[] OP_IMM_32 = {true, true, false, true, true, false, false};
	public static final boolean[] OP_32 = {true, true, false, true, true, true, false};

	//funct7
	public static void generateFunct3Map() {
		boolean[] BEQ_FUNCT3 = {false, false, false};
		boolean[] BNE_FUNCT3 = {true, false, false};
		boolean[] BLT_FUNCT3 = {false, false, true};
		boolean[] BGE_FUNCT3 = {true, false, true};
		boolean[] BLTU_FUNCT3 = {false, true, true};
		boolean[] BGEU_FUNCT3 = {true, true, true};
		boolean[] LB_FUNCT3 = {false, false, false};
		boolean[] LH_FUNCT3 = {true, false, false};
		boolean[] LW_FUNCT3 = {false, true, false};
		boolean[] LBU_FUNCT3 = {false, false, true};
		boolean[] LHU_FUNCT3 = {true, false, true};
		boolean[] SB_FUNCT3 = {false, false, false};
		boolean[] SH_FUNCT3 = {true, false, false};
		boolean[] SW_FUNCT3 = {false, true, false};
		boolean[] ADDI_FUNCT3 = {false, false, false};
		boolean[] SLTI_FUNCT3 = {false, true, false};
		boolean[] SLTIU_FUNCT3 = {true, true, false};
		boolean[] XORI_FUNCT3 = {false, false, true};
		boolean[] ORI_FUNCT3 = {false, true, true};
		boolean[] ANDI_FUNCT3 = {true, true, true};
		boolean[] SLLI_FUNCT3 = {true, false, false};
		boolean[] SRLI_FUNCT3 = {true, false, true};
		boolean[] SRAI_FUNCT3 = {true, false, true};
		boolean[] ADD_FUNCT3 = {false, false, false};
		boolean[] SUB_FUNCT3 = {false, false, false};
		boolean[] SLL_FUNCT3 = {true, false, false};
		boolean[] SLT_FUNCT3 = {false, true, false};
		boolean[] SLTU_FUNCT3 = {true, true, false};
		boolean[] XOR_FUNCT3 = {false, false, true};
		boolean[] SRL_FUNCT3 = {true, false, true};
		boolean[] SRA_FUNCT3 = {true, false, true};
		boolean[] OR_FUNCT3 = {false, true, true};
		boolean[] AND_FUNCT3 = {true, true, true};
		boolean[] FENCE_FUNCT3 = {false, false, false};
		boolean[] FENCEI_FUNCT3 = {true, false, false};
		boolean[] ECALL_FUNCT3 = {false, false, false};
		boolean[] EBREAK_FUNCT3 = {false, false, false};
		boolean[] CSRRW_FUNCT3 = {true, false, false};
		boolean[] CSRRS_FUNCT3 = {false, true, false};
		boolean[] CSRRC_FUNCT3 = {true, true, false};
		boolean[] CSRRWI_FUNCT3 = {true, false, true};
		boolean[] CSRRSI_FUNCT3 = {false, true, true};
		boolean[] CSRRCI_FUNCT3 = {true, true, true};
		boolean[] LWU_FUNCT3 = {false, true, true};
		boolean[] LD_FUNCT3 = {true, true, false};
		boolean[] SD_FUNCT3 = {true, true, false};
		boolean[] ADDIW_FUNCT3 = {false, false, false};
		boolean[] SLLIW_FUNCT3 = {true, false, false};
		boolean[] SRLIW_FUNCT3 = {true, false, true};
		boolean[] SRAIW_FUNCT3 = {true, false, true};
		boolean[] ADDW_FUNCT3 = {false, false, false};
		boolean[] SUBW_FUNCT3 = {false, false, false};
		boolean[] SLLW_FUNCT3 = {true, false, false};
		boolean[] SRLW_FUNCT3 = {true, false, true};
		boolean[] SRAW_FUNCT3 = {true, false, true};

		funct3.put("BEQ",BEQ_FUNCT3);
		funct3.put("BNE",BNE_FUNCT3);
		funct3.put("BLT",BLT_FUNCT3);
		funct3.put("BGE",BGE_FUNCT3);
		funct3.put("BLTU",BLTU_FUNCT3);
		funct3.put("BGEU",BGEU_FUNCT3);
		funct3.put("LB",LB_FUNCT3);
		funct3.put("LH",LH_FUNCT3);
		funct3.put("LW",LW_FUNCT3);
		funct3.put("LBU",LBU_FUNCT3);
		funct3.put("LHU",LHU_FUNCT3);
		funct3.put("SB",SB_FUNCT3);
		funct3.put("SH",SH_FUNCT3);
		funct3.put("SW",SW_FUNCT3);
		funct3.put("ADDI",ADDI_FUNCT3);
		funct3.put("SLTI",SLTI_FUNCT3);
		funct3.put("SLTIU",SLTIU_FUNCT3);
		funct3.put("XORI",XORI_FUNCT3);
		funct3.put("ORI",ORI_FUNCT3);
		funct3.put("ANDI",ANDI_FUNCT3);
		funct3.put("SLLI",SLLI_FUNCT3);
		funct3.put("SRLI",SRLI_FUNCT3);
		funct3.put("SRAI",SRAI_FUNCT3);
		funct3.put("ADD",ADD_FUNCT3);
		funct3.put("SUB",SUB_FUNCT3);
		funct3.put("SLL",SLL_FUNCT3);
		funct3.put("SLT",SLT_FUNCT3);
		funct3.put("SLTU",SLTU_FUNCT3);
		funct3.put("XOR",XOR_FUNCT3);
		funct3.put("SRL",SRL_FUNCT3);
		funct3.put("SRA",SRA_FUNCT3);
		funct3.put("OR",OR_FUNCT3);
		funct3.put("AND",AND_FUNCT3);
		funct3.put("FENCE",FENCE_FUNCT3);
		funct3.put("FENCE.I",FENCEI_FUNCT3);
		funct3.put("ECALL",ECALL_FUNCT3);
		funct3.put("EBREAK",EBREAK_FUNCT3);
		funct3.put("CSRRW",CSRRW_FUNCT3);
		funct3.put("CSRRS",CSRRS_FUNCT3);
		funct3.put("CSRRC",CSRRC_FUNCT3);
		funct3.put("CSRRWI",CSRRWI_FUNCT3);
		funct3.put("CSRRSI",CSRRSI_FUNCT3);
		funct3.put("CSRRCI",CSRRCI_FUNCT3);
		funct3.put("LWU",LWU_FUNCT3);
		funct3.put("LD",LD_FUNCT3);
		funct3.put("SD",SD_FUNCT3);
		funct3.put("ADDIW",ADDIW_FUNCT3);
		funct3.put("SLLIW",SLLIW_FUNCT3);
		funct3.put("SRLIW",SRLIW_FUNCT3);
		funct3.put("SRAIW",SRAIW_FUNCT3);
		funct3.put("ADDW",ADDW_FUNCT3);
		funct3.put("SUBW",SUBW_FUNCT3);
		funct3.put("SLLW",SLLW_FUNCT3);
		funct3.put("SRLW",SRLW_FUNCT3);
		funct3.put("SRAW",SRAW_FUNCT3);
	}
	public static final HashMap<String, boolean[]> funct3  = new HashMap<String, boolean[]>();;


	//other
	public static final boolean[] ZIMM = {true, true, true, true, true};
	public static final boolean[] CSR = {true, true, true, true, true, true, true, true, true, true, true};

	public static boolean[] register(String register) {
		int reg = Integer.parseInt(register.substring(1));
		boolean[] regArray = new boolean[5];
		for(int pos = 4; pos >= 0; pos--) {
			int pow = ((int)Math.pow(2, pos));
			if(reg >= pow) {
				regArray[pos] = true;
				reg-=pow;
			}
		}
		return regArray;
	}

	public static boolean[] immediate(String immediate, int size) {
		long l = Long.parseLong(immediate);
		String bitString = Long.toBinaryString(l);
		if(bitString.length() > size) {
			bitString = bitString.substring(bitString.length()-size);
		} else {
			int dif = size - bitString.length();
			for(int i = 0; i < dif; i++)
				bitString = "0" +bitString;
		}
		boolean[] immArray = new boolean[size];
		for(int pos = 0; pos < size; pos++) {
			if(bitString.charAt(size-1-pos)=='1')
				immArray[pos] = true;
		}
		return immArray;
	}

	public static boolean[] instructionRType(String instruction, String rd, String rs1, String rs2) {
		boolean[] OpCode = Instruction.getOpCode(instruction);
		boolean[] rdArray = register(rd);
		boolean[] rs1Array = register(rs1);
		boolean[] rs2Array = register(rs2);
		boolean[] funct3Array = funct3.get(instruction);
		boolean[] funct7Array = new boolean[7];
		if(instruction.contains("SUB") || instruction.contains("SRA"))
			funct7Array[5] = true;
		boolean[] instructionArray = new boolean[32];
		System.arraycopy(OpCode, 0, instructionArray, 0, 7);
		System.arraycopy(rdArray, 0, instructionArray, 7, 5);
		System.arraycopy(funct3Array, 0, instructionArray, 12, 3);
		System.arraycopy(rs1Array, 0, instructionArray, 15, 5);
		System.arraycopy(rs2Array, 0, instructionArray, 20, 5);
		System.arraycopy(funct7Array, 0, instructionArray, 25, 7);
		return instructionArray;
	}

	public static boolean[] instructionIType(String instruction, String immediate, String rs1, String rd) {
		boolean[] OpCode = Instruction.getOpCode(instruction);
		boolean[] funct3Array = funct3.get(instruction);
		boolean[] rdArray = register(rd);
		boolean[] rs1Array = register(rs1);
		boolean[] immediateArray = immediate(immediate, 12);
		boolean[] instructionArray = new boolean[32];
		System.arraycopy(OpCode, 0, instructionArray, 0, 7);
		System.arraycopy(rdArray, 0, instructionArray, 7, 5);
		System.arraycopy(funct3Array, 0, instructionArray, 12, 3);
		System.arraycopy(rs1Array, 0, instructionArray, 15, 5);
		System.arraycopy(immediateArray, 0, instructionArray, 20, 12);
		return instructionArray;
	}

	public static boolean[] instructionSType(String instruction, String immediate, String rs1, String rs2) {
		boolean[] OpCode = Instruction.getOpCode(instruction);
		boolean[] funct3Array = funct3.get(instruction);
		boolean[] rs2Array = register(rs2);
		boolean[] rs1Array = register(rs1);
		boolean[] immediateArray = immediate(immediate, 12);
		boolean[] instructionArray = new boolean[32];
		System.arraycopy(OpCode, 0, instructionArray, 0, 7);
		System.arraycopy(immediateArray, 0, instructionArray, 7, 5);
		System.arraycopy(funct3Array, 0, instructionArray, 12, 3);
		System.arraycopy(rs1Array, 0, instructionArray, 15, 5);
		System.arraycopy(rs2Array, 0, instructionArray, 20, 5);
		System.arraycopy(immediateArray, 5, instructionArray, 25, 7);
		return instructionArray;
	}

	public static boolean[] instructionSBType(String instruction, String immediate, String rs1, String rs2) {
		boolean[] OpCode = Instruction.getOpCode(instruction);
		boolean[] funct3Array = funct3.get(instruction);
		boolean[] rs2Array = register(rs2);
		boolean[] rs1Array = register(rs1);
		boolean[] immediateArray = immediate(immediate, 13);
		boolean[] instructionArray = new boolean[32];
		System.arraycopy(OpCode, 0, instructionArray, 0, 7);
		System.arraycopy(immediateArray, 11, instructionArray, 7, 1);
		System.arraycopy(immediateArray, 0, instructionArray, 8, 4);
		System.arraycopy(funct3Array, 0, instructionArray, 12, 3);
		System.arraycopy(rs1Array, 0, instructionArray, 15, 5);
		System.arraycopy(rs2Array, 0, instructionArray, 20, 5);
		System.arraycopy(immediateArray, 5, instructionArray, 25, 6);
		System.arraycopy(immediateArray, 12, instructionArray, 31, 1);
		return instructionArray;
	}

	public static boolean[] instructionUType(String instruction, String immediate, String rd) {
		boolean[] OpCode = Instruction.getOpCode(instruction);
		boolean[] rdArray = register(rd);
		boolean[] immediateArray = immediate(immediate, 32);
		boolean[] instructionArray = new boolean[32];
		System.arraycopy(OpCode, 0, instructionArray, 0, 7);
		System.arraycopy(rdArray, 0, instructionArray, 7, 5);
		System.arraycopy(immediateArray, 12, instructionArray, 12, 20);
		return instructionArray;
	}

	public static boolean[] instructionUJType(String instruction, String immediate, String rd) {
		boolean[] OpCode = Instruction.getOpCode(instruction);
		boolean[] rdArray = register(rd);
		boolean[] immediateArray = immediate(immediate, 32);
		boolean[] instructionArray = new boolean[32];
		System.arraycopy(OpCode, 0, instructionArray, 0, 7);
		System.arraycopy(rdArray, 0, instructionArray, 7, 5);
		System.arraycopy(immediateArray, 12, instructionArray, 12, 8);
		System.arraycopy(immediateArray, 11, instructionArray, 20, 1);
		System.arraycopy(immediateArray, 1, instructionArray, 21, 10);
		System.arraycopy(immediateArray, 20, instructionArray, 31, 1);
		return instructionArray;
	}

	public static boolean[] instructionShiftIType(String instruction, String shamt, String rs1, String rd) {
		boolean[] OpCode = Instruction.getOpCode(instruction);
		boolean[] funct3Array = funct3.get(instruction);
		boolean[] rdArray = register(rd);
		boolean[] rs1Array = register(rs1);
		boolean[] shamtArray;
		boolean[] funct7Array;
		int shamtLength = 0;
		if(instruction.contains("W")) {
			shamtArray = immediate(shamt, 5);
			funct7Array = new boolean[7];
			shamtLength = 5;
		} else {
			shamtArray = immediate(shamt, 6);
			funct7Array = new boolean[6];
			shamtLength = 6;
		}
		if(instruction.contains("SRAI"))
			shamtArray[shamtArray.length-2] = true;
		boolean[] instructionArray = new boolean[32];
		System.arraycopy(OpCode, 0, instructionArray, 0, 7);
		System.arraycopy(rdArray, 0, instructionArray, 7, 5);
		System.arraycopy(funct3Array, 0, instructionArray, 12, 3);
		System.arraycopy(rs1Array, 0, instructionArray, 15, 5);
		System.arraycopy(shamtArray, 0, instructionArray, 20, shamtLength);
		System.arraycopy(funct7Array, 0, instructionArray, 20+shamtLength, 12 - shamtLength);
		return instructionArray;
	}


	public static void main(String[] args) {
		generateFunct3Map();
		boolean[] i = instructionSType("SB", "1", "x2", "x3");
		Word word = new Word(i,false);
		System.out.println(word.generateBitString());
		System.out.println(word.generateBitStringLE());
	}

	public static boolean[] getOpCode(String instruction) {
		if(instruction.equals("LUI"))
			return Instruction.LUI;
		if(instruction.equals("AUIPC"))
			return Instruction.AUIPC;
		if(instruction.equals("JAL"))
			return Instruction.JAL;
		if(instruction.equals("JALR"))
			return Instruction.JALR;
		if(instruction.startsWith("S") && instruction.length() == 2)
			return Instruction.STORE;
		if(instruction.startsWith("L") && instruction.length() == 2)
			return Instruction.LOAD;
		if(instruction.contains("CSRR") || instruction.equals("ECALL") || instruction.equals("EBREAK") )
			return Instruction.SYSTEM;
		if(instruction.contains("FENCE"))
			return Instruction.MISC_MEM;
		if(instruction.contains("I") && instruction.contains("W"))
			return Instruction.OP_IMM_32;
		if(instruction.contains("W"))
			return Instruction.OP_32;
		if(instruction.contains("I"))
			return Instruction.OP_IMM;
		if(instruction.equals("BEQ") ||instruction.equals("BNE") ||instruction.equals("BLT") 
				||instruction.equals("BGE") ||instruction.equals("BLTU") ||instruction.equals("BGEU"))
			return Instruction.BRANCH;
		return Instruction.OP;


	}

	public boolean[] generateInstruction(String line) {
		line = line.replace(',', ' ');
		String[] instruction = line.split("\\s+");
		String instruct = instruction[0];
		if(contains(ShiftIType,instruct)) {
			String rd = instruction[1];
			String rs1 = instruction[2];
			String shamt = instruction[3];
			return instructionShiftIType(instruct, shamt, rs1, rd);
		}

		if(instruct.equals("JAL")) {
			String rd = instruction[1];
			String imm = instruction[2];
			return instructionUJType(instruct, rd, imm);
		}

		if(instruct.equals("LUI") || instruct.equals("AUIPC")) {
			String rd = instruction[1];
			String imm = instruction[2];
			return instructionUType(instruct, rd, imm);
		}

		if(opCodeEqual(getOpCode(instruct),BRANCH)) {
			String rs1 = instruction[1];
			String rs2 = instruction[2];
			String imm = instruction[3];
			return instructionSBType(instruct, imm, rs1, rs2);
		}

		if(opCodeEqual(getOpCode(instruct),STORE)) {
			String rs1 = instruction[1];
			String rs2 = instruction[2];
			String imm = instruction[3];
			return instructionSType(instruct, imm, rs1, rs2);
		}
		if(opCodeEqual(getOpCode(instruct),OP) || opCodeEqual(getOpCode(instruct),OP_32)) {
			String rd = instruction[1];
			String rs1 = instruction[2];
			String rs2 = instruction[3];
			return instructionRType(instruct, rd, rs1, rs2);
		}
		String rd = instruction[1];
		String rs1 = instruction[2];
		String imm = instruction[3];
		if(instruct.contains("FENCE")) {
			return instructionIType(instruct, "0", "x0", "x0");
		}
		if(instruct.equals("ECALL")) {
			return instructionIType(instruct, "0", "x0", "x0");
		}
		if(instruct.equals("EBREAK")) {
			return instructionIType(instruct, "1", "x0", "x0");
		}
		if(instruct.contains("CSRR")) {
			if(instruct.contains("I")) {
				return instructionIType(instruct, imm, "0", rd);
			} else {
				return instructionIType(instruct, imm, rs1, rd);
			}
		}
		
		return instructionIType(instruct, imm, rs1, rd);

	}

	public static boolean contains(String[] array, String s) {
		for(String c: array)
			if(s.equals(c))
				return true;
		return false;
	}

	public static boolean opCodeEqual(boolean[] op1, boolean[] op2) {
		for(int pos = 0; pos < op1.length; pos++) {
			if(op1[pos] != op2[pos]) {
				return false;
			}
		}
		return true;
	}

	public static final String[] ShiftIType = {"SLLI", "SRLI", "SRAI", "SLLIW", "SRLIW", "SRAIW"};
	//UJ Type identified by the JAL opcode
	//U Type identified by the LUI and AUIPC opcodes
	//SB TYPE identified by the opcode BRANCH
	//S Type identified by the Store opcode
	//R-TYPE identified by the OP and OP-32 opcodes
	//I is everything else

}
