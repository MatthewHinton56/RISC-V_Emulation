import java.math.BigInteger;
import java.util.Arrays;

public class DoubleWord implements LittleEndian{
	protected Word[] doubleWordArrayWord;

	@Override
	public String calculateValueSigned() {
		boolean[] bitArray = generateBitArray();
		long val = (bitArray[63]) ? ((long)Math.pow(-2, 63)) : 0;
		for(int pos = 0; pos < 63; pos++) {
			if(bitArray[pos]) {
				val+= ((long)Math.pow(2, pos));
			}
		}
		return ""+val;
		
	}

	@Override
	public String calculateValueUnSigned() {
		boolean[] bitArray = generateBitArray();
		long val = (bitArray[63]) ? ((int)Math.pow(-2, 63)) : 0;
		for(int pos = 0; pos < 63; pos++) {
			if(bitArray[pos]) {
				val+= ((int)Math.pow(2, pos));
			}
		}
		return Long.toUnsignedString(val);
	}

	@Override
	public String generateBitString() {
		return doubleWordArrayWord[1].generateBitString() + doubleWordArrayWord[0].generateBitString();
	}

	@Override
	public boolean[] generateBitArray() {
		boolean[] bitArray = new boolean[64];
		boolean[] lowerBitArray = doubleWordArrayWord[0].generateBitArray();
		boolean[] upperBitArray = doubleWordArrayWord[1].generateBitArray();
		System.arraycopy(lowerBitArray, 0, bitArray, 0, 32);
		System.arraycopy(upperBitArray, 0, bitArray, 32, 32);
		return bitArray;
	}

	@Override
	public String generateHex() {
		return doubleWordArrayWord[1].generateHex() + doubleWordArrayWord[0].generateHex();
	}

	@Override
	public String generateHexLE() {
		return doubleWordArrayWord[0].generateHexLE() + doubleWordArrayWord[1].generateHexLE();
	}

	@Override
	public String generateBitStringLE() {
		return doubleWordArrayWord[0].generateBitStringLE() + doubleWordArrayWord[1].generateBitStringLE();
	}
	
	public DoubleWord() {
		doubleWordArrayWord = new Word[2];
		doubleWordArrayWord[0] = new Word();
		doubleWordArrayWord[1] = new Word();
		
	}

	public DoubleWord(Word upper, Word lower) {
		doubleWordArrayWord = new Word[2];
		doubleWordArrayWord[0] = lower;
		doubleWordArrayWord[1] = upper;
	}
	
	public DoubleWord(String hex, boolean LE) {
		if(LE)
			hex = LittleEndian.LEHexFixer(hex, 16);
		else
			hex = LittleEndian.hexFixer(hex,16);
		String WordUpper = "";
		String WordLower = "";
		if(LE) {
			WordUpper = hex.substring(8,16);
			WordLower = hex.substring(0,8);
		} else {
			WordUpper = hex.substring(0,8);
			WordLower = hex.substring(8, 16);
		}
		Word upper = new Word(WordUpper, LE);
		Word lower = new Word(WordLower, LE);
		doubleWordArrayWord = new Word[2];
		doubleWordArrayWord[0] = lower;
		doubleWordArrayWord[1] = upper;
	}
	
	public DoubleWord(boolean bitArray[], boolean LE) {
		boolean[] lowerWord = new boolean[32];
		boolean[] upperWord = new boolean[32];
		if(LE){
			System.arraycopy(bitArray, 0, upperWord, 0, 32);
			System.arraycopy(bitArray, 32, lowerWord, 0, 32);
		} else {
			System.arraycopy(bitArray, 0, lowerWord, 0, 32);
			System.arraycopy(bitArray, 32, upperWord, 0, 32);
		}
		Word Upper = new Word(upperWord, LE);
		Word Lower = new Word(lowerWord, LE);
		doubleWordArrayWord = new Word[2];
		doubleWordArrayWord[0] = Lower;
		doubleWordArrayWord[1] = Upper;
	}
	public static void main(String[] args) {
		String f = "FFFFFFFFFFFFFFFF";
		DoubleWord dw = new DoubleWord(f,false);
		System.out.println(dw.calculateValueSigned());
	}
}
