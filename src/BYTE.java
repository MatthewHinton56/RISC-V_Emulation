
public class BYTE extends LittleEndian {

	public static final int BYTESIZE = 8;
	public static final BYTE ZERO_BYTE = new BYTE("00");
	
	public BYTE(boolean[] bitArray) {
		super(bitArray);
	}
	
	public BYTE() {
		super(BYTESIZE);
	}
	
	public BYTE(String hex) {
		super(getByte(hex));
	}

	public static BYTE randomBYTE() {
		BYTE b = new BYTE();
		for(int pos = 0; pos < BYTE.BYTESIZE; pos++)
			if(Math.random() >= .5)
				b.bitArray[pos] = true;
		return b;
	}

	@Override
	public BYTE add(LittleEndian addends) {
		boolean[] out = ALU.IADD(this.bitArray, addends.bitArray);
		return new BYTE(out);
	}
	
}
