
public class Processor {

	public static final RegisterFile registerFile = new RegisterFile();
	public static Instruction currentInstruction;
	public static DoubleWord PC;
	public static String status = "AOK";

	public void fetch() {
		int pcInt = ((int)PC.calculateValueSigned());
		BYTE[] instructionArray = Memory.getInstruction(pcInt);
		currentInstruction = new Instruction(instructionArray);
		currentInstruction.valP = new DoubleWord(ALU.IADD(PC.bitArray, currentInstruction.standardValPIncrement.bitArray));
	}

	public void decode() {
		currentInstruction.valA = registerFile.get(currentInstruction.rA);
		currentInstruction.valB = registerFile.get(currentInstruction.rB);
		String instruction = currentInstruction.instruction;
		if(instruction.equals("ret") || instruction.equals("pushq") || instruction.equals("popq") || instruction.equals("call")) {
			currentInstruction.valB = registerFile.get("%rsp");
		}
		if(instruction.equals("ret") || instruction.equals("popq"))
			currentInstruction.valA = registerFile.get("%rsp");

	}

	public void execute() {
		switch(currentInstruction.instruction) {
		case "halt":
			status = "HLT";
			break;
		case "mrmovq":	
		case "rmmovq":
			currentInstruction.valE = new DoubleWord(ALU.IADD(currentInstruction.immediate.bitArray, currentInstruction.valB.bitArray));
			break;
		case "addq":
			currentInstruction.valE = new DoubleWord(ALU.ADD(currentInstruction.valA.bitArray, currentInstruction.valB.bitArray));
			break;
		case "subq":
			currentInstruction.valE = new DoubleWord(ALU.SUB(currentInstruction.valA.bitArray, currentInstruction.valB.bitArray));
			break;
		case "andq":
			currentInstruction.valE = new DoubleWord(ALU.AND(currentInstruction.valA.bitArray, currentInstruction.valB.bitArray));
			break;
		case "xorq":
			currentInstruction.valE = new DoubleWord(ALU.XOR(currentInstruction.valA.bitArray, currentInstruction.valB.bitArray));
			break;
		case "pushq":	
		case "call":
			currentInstruction.valE = new DoubleWord(ALU.DECREMENTEIGHT(currentInstruction.valB.bitArray));
			break;
		case "ret":	
		case "popq":
			currentInstruction.valE = new DoubleWord(ALU.ADDEIGHT(currentInstruction.valB.bitArray));
			break;
			
		}
	}

}
