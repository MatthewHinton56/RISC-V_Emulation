import java.util.HashSet;

public class Memory {
	public static final BYTE[] memory = new BYTE[8192];
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
	}
	
}
