
public class Memory {
	public static final BYTE[] memory = new BYTE[8192];
	static {
		for(int pos = 0; pos < memory.length;pos++)
			memory[pos] = new BYTE();
	}
	
	//Store B, HW, W, DW
	//Load B, HW, W, DW
	public static BYTE loadByte(int pos) {
		return memory[pos];
	}
	
	public static BYTE storeByte(BYTE b, int pos) {
		BYTE bOld = loadByte(pos);
		memory[pos] = b;
		return bOld;
	}
	
	public static HalfWord loadHalfWord(int pos) {
		String hex = "";
		for(int i = pos; i < pos + 2; i++)
			hex += memory[i].generateHex();
		return new HalfWord(hex, true);
	}
	
	public static HalfWord storeHalfWord(HalfWord hw, int pos) {
		HalfWord hWOld = loadHalfWord(pos);
		for(int i = pos; i < pos + 2; i++)
			memory[i] = hw.getBYTE(i-pos);
		return hWOld;
	}
	
	public static Word loadWord(int pos) {
		String hex = "";
		for(int i = pos; i < pos + 4; i++)
			hex += memory[i].generateHex();
		return new Word(hex, true);
	}
	
	public static Word storeWord(HalfWord hw, int pos) {
		Word wOld = loadWord(pos);
		for(int i = pos; i < pos + 4; i++)
			memory[i] = hw.getBYTE(i-pos);
		return wOld;
	}
	
	public static DoubleWord loadDoubleWord(int pos) {
		String hex = "";
		for(int i = pos; i < pos + 8; i++)
			hex += memory[i].generateHex();
		return new DoubleWord(hex, true);
	}
	
	public static DoubleWord storeDoubleWord(DoubleWord hw, int pos) {
		DoubleWord dWOld = loadDoubleWord(pos);
		for(int i = pos; i < pos + 8; i++)
			memory[i] = hw.getBYTE(i-pos);
		return dWOld;
	}
	
}
