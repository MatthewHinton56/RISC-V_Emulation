import java.math.BigInteger;
import java.util.Arrays;

public class DoubleWord extends LittleEndian{
	
	public static final int DOUBLEWORDSIZE = 64;
	public DoubleWord() {
		super(DOUBLEWORDSIZE);
	}
	
	public DoubleWord(String hex, boolean LE) {
		super(DOUBLEWORDSIZE);
		if(LE)
			hex = LEHexFixer(hex,DOUBLEWORDSIZE);
		else
			hex = hexFixer(hex,DOUBLEWORDSIZE);
		for(int pos = 0; pos < hex.length(); pos += 2) {
			boolean[] hexBYTE = getByte(hex.substring(pos, pos+2));
			if(LE) {
				System.arraycopy(hexBYTE, 0, bitArray, pos*BYTE.BYTESIZE, BYTE.BYTESIZE);
			} else {
				System.arraycopy(hexBYTE, 0, bitArray, BYTE.BYTESIZE - pos*BYTE.BYTESIZE, BYTE.BYTESIZE);
			}
			
		}
	}
	
	public DoubleWord(boolean bitArray[]) {
		super(bitArray);
	}

	public BYTE getBYTE(int pos) {
		pos = pos*BYTE.BYTESIZE;
		boolean[] bitArray = new boolean[BYTE.BYTESIZE];
		System.arraycopy(this.bitArray, pos, bitArray, 0, BYTE.BYTESIZE);
		return new BYTE(bitArray);
	}
	
	public HalfWord getHalfWord(int pos) {
		pos = pos*HalfWord.HALFWORDSIZE;
		boolean[] bitArray = new boolean[HalfWord.HALFWORDSIZE];
		System.arraycopy(this.bitArray, pos, bitArray, 0, HalfWord.HALFWORDSIZE);
		return new HalfWord(bitArray);
	}
	
	public Word getWord(int pos) {
		pos = pos*Word.WORDSIZE;
		boolean[] bitArray = new boolean[Word.WORDSIZE];
		System.arraycopy(this.bitArray, pos, bitArray, 0, Word.WORDSIZE);
		return new Word(bitArray);
	}
}
