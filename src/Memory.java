
public class Memory {
	public static final BYTE[] memory = new BYTE[8192];
	static {
		for(int pos = 0; pos < memory.length;pos++)
			memory[pos] = new BYTE();
	}
	
}
