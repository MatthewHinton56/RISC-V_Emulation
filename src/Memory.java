import java.util.HashMap;
import java.util.HashSet;

public class Memory {
	public static final HashMap<Long,BYTE> memory = new HashMap<Long,BYTE>();

	//public static BYTE[] getInstruction(long position) {
	//	BYTE instructionArray[] = new BYTE[10];
	//	for(Long i = position; i < position + 10; i++) {
	//		BYTE instruct = memory.get(i);
	//		if(instruct == null)
	//			memory.put(i, BYTE.randomBYTE());
	//		instructionArray[(int) (i-position)] =  memory.get(i);
	//	}
	//	return instructionArray;
	//}

	public static DoubleWord loadDoubleWord(long position) {
		String immediate = "";
		if(position % 8 != 0) {
			Processor.status = "HLT";
			return null;
		}
		
		for(long i = position; i < position + 8; i++) {
			if(memory.get(i) == null) {
				immediate += "00";
			} else {
				immediate += memory.get(i).generateHex();
			}
		}

		return new DoubleWord(immediate, true);
	}

	public static void storeDoubleWord(long position, DoubleWord val) {
		for(long i = position; i < position + 8; i++)
			memory.put(i, new BYTE(val.getBYTE((int) (i-position)).generateHex()));
	}

	public static Word loadWord(long position) {
		String immediate = "";
		if(position % 4 != 0) {
			Processor.status = "HLT";
			return null;
		}
		for(long i = position; i < position + 4; i++) {
			if(memory.get(i) == null) {
				immediate += "00";
			} else {
				immediate += memory.get(i).generateHex();
			}
		}

		return new Word(immediate, true);
	}

	public static HalfWord loadHalfWord(long position) {
		if(position % 2 != 0) {
			Processor.status = "HLT";
			return null;
		}
		String immediate = "";
		for(long i = position; i < position + 2; i++) {
			if(memory.get(i) == null) {
				immediate += "00";
			} else {
				immediate += memory.get(i).generateHex();
			}
		}

		return new HalfWord(immediate, true);
	}

	public static BYTE loadBYTE(long position) {
		String immediate = "";
		if(memory.get(position) == null) {
			immediate += "00";
		} else {
			immediate += memory.get(position).generateHex();
		}

		return new BYTE(immediate);
	}

	public static void storeWord(long position, Word val) {
		for(long i = position; i < position + 4; i++)
			memory.put(i, new BYTE(val.getBYTE((int) (i-position)).generateHex()));
	}

	public static void storeHalfWord(long position, HalfWord val) {
		for(long i = position; i < position + 2; i++)
			memory.put(i, new BYTE(val.getBYTE((int) (i-position)).generateHex()));
	}

	public static void storeBYTE(long position, BYTE val) {
		memory.put(position, val);
	}

	public static LittleEndian load(long address, int size) {
		switch(size) {
		case 1:
			return Memory.loadBYTE(address);
		case 2: 
			return Memory.loadHalfWord(address);
		case 4:
			return Memory.loadWord(address);
		case 8:
			return Memory.loadDoubleWord(address);
		default:
			return null;
		}
	}

	//public static void storeInstruction(long position, String[] instruction) {
	//	for(long i = position; i < position + instruction.length; i++)
	//		memory.put(i, new BYTE(instruction[((int) (i-position))]));
	//}
	/*public static final BYTE[] memory = new BYTE[8192];
	public static final HashSet<Integer> memoryInUse = new HashSet<Integer>();
	static {
		for(int pos = 0; pos < memory.length;pos++)
			memory[pos] = new BYTE();
	}

	public static BYTE[] getInstruction(int position) {
		BYTE instructionArray[] = new BYTE[10];
		for(int i = position; i < position + 10; i++)
			instructionArray[i-position] = memory[i];
		return instructionArray;
	}


	public static DoubleWord loadDoubleWord(int position) {
		String immediate = "";
		for(int i = position; i < position + 8; i++)
			immediate += memory[i].generateHex();
		return new DoubleWord(immediate, true);
	}

	public static void storeDoubleWord(int position, DoubleWord val) {
		for(int i = position; i < position + 8; i++)
			memory[i] = new BYTE(val.getBYTE(i-position).generateHex());
	}*/

}



