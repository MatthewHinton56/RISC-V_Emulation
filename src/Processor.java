
public class Processor {

	public static final RegisterFile registerFile = new RegisterFile();
	public static Instruction currentInstruction;

	public static void fetch() {
		Word nextInstruction = Memory.loadWord((int) registerFile.get("pc").calculateValueSigned());
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

	public static void next() {
		fetch();
		decode();
		execute();
		memory();
		writeBack();
	}

	public static void run() {
		while(currentInstruction == null || !currentInstruction.stop)
			next();
	}
	//Bare Bones
	public static void setUp(DoubleWord start) {
		registerFile.set("pc", start);
	}

	
	
}
