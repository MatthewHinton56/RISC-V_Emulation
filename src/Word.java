
public class Word extends LittleEndian{
	
	public static final int WORDSIZE = 32;
	
	public Word() {
		super(WORDSIZE);
	}
	
	public Word(String hex, boolean LE) {
		super(WORDSIZE);
		if(LE)
			hex = LEHexFixer(hex, WORDSIZE);
		else
			hex = hexFixer(hex, WORDSIZE);
		for(int pos = 0; pos < hex.length(); pos += 2) {
			boolean[] hexBYTE = getByte(hex.substring(pos, pos+2));
			if(LE) {
				System.arraycopy(hexBYTE, 0, bitArray, pos*BYTE.BYTESIZE/2, BYTE.BYTESIZE);
			} else {
				System.arraycopy(hexBYTE, 0, bitArray, BYTE.BYTESIZE - (pos/2+1)*BYTE.BYTESIZE, BYTE.BYTESIZE);
			}
			
		}
	}
	
	public Word(boolean bitArray[]) {
		super(bitArray);
	}
	
	public DoubleWord extendToDoubleWord(boolean signed) {
		String hex = this.generateHex();
		 if(signed) {
			 boolean neg = this.getSign();
			 hex = (neg) ? "FFFFFFFF" + hex : "00000000" +hex;
			 return new DoubleWord(hex, false);
		 } 
		 return new DoubleWord("00000000" + hex, false);
	}
	// 0 <= pos < 2
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
	
	@Override
	public Word add(LittleEndian addends) {
		boolean[] out = ALU.IADD(this.bitArray, addends.bitArray);
		return new Word(out);
	}

	public Word subtract(Word minuend) {
		boolean[] out = ALU.SUB(this.bitArray, minuend.bitArray);
		return new Word(out);
	}
	
	public Word shiftLeft(Word shamt) {
		int shamtI = (int)(Math.abs(shamt.calculateValueSigned()%32));
		boolean[] c = ALU.shiftLeft(this.bitArray, shamtI);
		return new Word(c);
	}
	
	public Word shiftRight(Word shamt, boolean logical) {
		int shamtI = (int)(Math.abs(shamt.calculateValueSigned()%32));
		boolean[] c = ALU.shiftRight(this.bitArray, shamtI, logical);
		return new Word(c);
	}
	
	public Word shiftLeft(boolean[] shamt) {
		int shamtI = ALU.bitArrayToInt(shamt);
		boolean[] c = ALU.shiftLeft(this.bitArray, shamtI);
		return new Word(c);
	}
	
	public Word shiftRight(boolean[] shamt, boolean logical) {
		int shamtI = ALU.bitArrayToInt(shamt);
		boolean[] c = ALU.shiftRight(this.bitArray, shamtI, logical);
		return new Word(c);
	}

	public Word mul(Word rS2Val) {
		boolean[] out = ALU.multiply(this.bitArray, rS2Val.bitArray);
		return new Word(out);
	}

	public Word div(Word rS2Val, boolean signed, boolean remainder) {
		boolean[] out;
		if(signed) {
			out = ALU.signedDivision(rS2Val.bitArray, this.bitArray, remainder);
		} else {
			out = ALU.unsignedDivision(rS2Val.bitArray, this.bitArray, remainder);
		}
		return new Word(out);
	}

}
