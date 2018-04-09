
public class Word implements LittleEndian{

	protected HalfWord[] wordArrayHalfWord;
	@Override
	public String calculateValueSigned() {
		boolean[] bitArray = generateBitArray();
		long val = (bitArray[31]) ? ((int)Math.pow(-2, 31)) : 0;
		for(int pos = 0; pos < 31; pos++) {
			if(bitArray[pos]) {
				val+= ((int)Math.pow(2, pos));
			}
		}
		return ""+val;
	}
	@Override
	public String calculateValueUnSigned() {
		boolean[] bitArray = generateBitArray();
		long val = 0;
		for(int pos = 0; pos <= 15; pos++) {
			if(bitArray[pos]) {
				val+= ((int)Math.pow(2, pos));
			}
		}
		return ""+val;
	}
	@Override
	public String generateBitString() {
		return wordArrayHalfWord[1].generateBitString() + wordArrayHalfWord[0].generateBitString();
	}
	@Override
	public boolean[] generateBitArray() {
		boolean[] bitArray = new boolean[32];
		boolean[] lowerBitArray = wordArrayHalfWord[0].generateBitArray();
		boolean[] upperBitArray = wordArrayHalfWord[1].generateBitArray();
		System.arraycopy(lowerBitArray, 0, bitArray, 0, 16);
		System.arraycopy(upperBitArray, 0, bitArray, 16, 16);
		return bitArray;
	}
	@Override
	public String generateHex() {
		return wordArrayHalfWord[1].generateHex() + wordArrayHalfWord[0].generateHex();
	}
	@Override
	public String generateHexLE() {
		return wordArrayHalfWord[0].generateHexLE() + wordArrayHalfWord[1].generateHexLE();
	}
	@Override
	public String generateBitStringLE() {
		return wordArrayHalfWord[0].generateBitStringLE() + wordArrayHalfWord[1].generateBitStringLE();
	}
	
	public Word() {
		wordArrayHalfWord = new HalfWord[2];
		wordArrayHalfWord[0] = new HalfWord();
		wordArrayHalfWord[1] = new HalfWord();
		
	}
	
	public Word(HalfWord upper, HalfWord lower) {
		wordArrayHalfWord = new HalfWord[2];
		wordArrayHalfWord[0] = lower;
		wordArrayHalfWord[1] = upper;
	}
	
	public Word(String hex, boolean LE) {
		if(LE)
			hex = LittleEndian.LEHexFixer(hex, 8);
		else
			hex = LittleEndian.hexFixer(hex,8);
		String halfWordUpper = "";
		String halfWordLower = "";
		if(LE) {
			halfWordUpper = hex.substring(4,8);
			halfWordLower = hex.substring(0,4);
		} else {
			halfWordUpper = hex.substring(0,4);
			halfWordLower = hex.substring(4, 8);
		}
		HalfWord upper = new HalfWord(halfWordUpper, LE);
		HalfWord lower = new HalfWord(halfWordLower, LE);
		wordArrayHalfWord = new HalfWord[2];
		wordArrayHalfWord[0] = lower;
		wordArrayHalfWord[1] = upper;
	}
	
	public Word(boolean bitArray[], boolean LE) {
		boolean[] lowerHalfWord = new boolean[16];
		boolean[] upperHalfWord = new boolean[16];
		if(LE){
			System.arraycopy(bitArray, 0, upperHalfWord, 0, 16);
			System.arraycopy(bitArray, 16, lowerHalfWord, 0, 16);
		} else {
			System.arraycopy(bitArray, 0, lowerHalfWord, 0, 16);
			System.arraycopy(bitArray, 16, upperHalfWord, 0, 16);
		}
		HalfWord Upper = new HalfWord(upperHalfWord, LE);
		HalfWord Lower = new HalfWord(lowerHalfWord, LE);
		wordArrayHalfWord = new HalfWord[2];
		wordArrayHalfWord[0] = Lower;
		wordArrayHalfWord[1] = Upper;
	}
	
	public static void main(String[] args) {
		Word word = new Word("FFFFFFFF",true);
		System.out.println(word.generateHex());
		System.out.println(word.generateHexLE());
		System.out.println(word.calculateValueSigned());
	}
	
	public String toString() {
		return this.generateHex();
	}
}
