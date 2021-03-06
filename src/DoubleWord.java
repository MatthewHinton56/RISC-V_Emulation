

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
				System.arraycopy(hexBYTE, 0, bitArray, pos*BYTE.BYTESIZE/2, BYTE.BYTESIZE);
			} else {
				System.arraycopy(hexBYTE, 0, bitArray, DoubleWord.DOUBLEWORDSIZE - (pos/2+1)*BYTE.BYTESIZE, BYTE.BYTESIZE);
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
	
	public DoubleWord(long l) {
		super(ALU.longToBitArray(l, DOUBLEWORDSIZE));
	}
	
	@Override
	public DoubleWord add(LittleEndian addends) {
		boolean[] out = ALU.IADD(this.bitArray, addends.bitArray);
		return new DoubleWord(out);
	}
	
	public DoubleWord subtract(DoubleWord minuend) {
		boolean[] out = ALU.SUB(this.bitArray, minuend.bitArray);
		return new DoubleWord(out);
	}

	public DoubleWord addFour() {
		return add(FOUR);
	}
	public static final DoubleWord FOUR = new DoubleWord(4);	
	
	public DoubleWord(LittleEndian input, boolean signed) {
		super(DOUBLEWORDSIZE);
		boolean[] tempBitArray = ALU.signExtension(input.bitArray, !signed, DOUBLEWORDSIZE);
		System.arraycopy(tempBitArray, 0, bitArray, 0, 64);
	}
	
	public DoubleWord and(DoubleWord andend) {
		boolean[] out = ALU.AND(this.bitArray, andend.bitArray);
		return new DoubleWord(out);
	}
	
	public DoubleWord or(DoubleWord andend) {
		boolean[] out = ALU.OR(this.bitArray, andend.bitArray);
		return new DoubleWord(out);
	}
	
	public DoubleWord xor(DoubleWord andend) {
		boolean[] out = ALU.XOR(this.bitArray, andend.bitArray);
		return new DoubleWord(out);
	}

	public boolean lessThan(DoubleWord b, boolean unsigned) {
		// TODO Auto-generated method stub
		return ALU.LessThan(this.bitArray, b.bitArray, unsigned);
	}
	
	public DoubleWord shiftLeft(DoubleWord shamt) {
		int shamtI = (int)(Math.abs(shamt.calculateValueSigned()%64));
		boolean[] c = ALU.shiftLeft(this.bitArray, shamtI);
		return new DoubleWord(c);
	}
	
	public DoubleWord shiftRight(DoubleWord shamt, boolean logical) {
		int shamtI = (int)(Math.abs(shamt.calculateValueSigned()%64));
		boolean[] c = ALU.shiftRight(this.bitArray, shamtI, logical);
		return new DoubleWord(c);
	}
	
	public DoubleWord shiftLeft(boolean[] shamt) {
		int shamtI = ALU.bitArrayToInt(shamt);
		boolean[] c = ALU.shiftLeft(this.bitArray, shamtI);
		return new DoubleWord(c);
	}
	
	public DoubleWord shiftRight(boolean[] shamt, boolean logical) {
		int shamtI = ALU.bitArrayToInt(shamt);
		boolean[] c = ALU.shiftRight(this.bitArray, shamtI, logical);
		return new DoubleWord(c);
	}

	public DoubleWord mul(DoubleWord rS2Val) {
		boolean[] out = ALU.multiply(this.bitArray, rS2Val.bitArray);
		return new DoubleWord(out);
	}

	public DoubleWord upper(DoubleWord rS2Val, boolean multiplicandSigned, boolean multiplierSigned) {
		boolean[] out = ALU.upperBits(this.bitArray, rS2Val.bitArray, multiplicandSigned, multiplierSigned);
		return new DoubleWord(out);
	}

	
	public DoubleWord div(DoubleWord rS2Val, boolean signed, boolean remainder) {
		if(rS2Val.isZero())
			throw new ArithmeticException("Attempting to divide by 0");
		boolean[] out;
		if(signed) {
			out = ALU.signedDivision(rS2Val.bitArray, this.bitArray, remainder);
		} else {
			out = ALU.unsignedDivision(rS2Val.bitArray, this.bitArray, remainder);
		}
		return new DoubleWord(out);
	}
	
}
