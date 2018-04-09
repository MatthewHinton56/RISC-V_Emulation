import java.util.Arrays;

public class HalfWord implements LittleEndian{

	protected BYTE[] halfWordArray;

	public String calculateValueSigned() {
		boolean[] bitArray = generateBitArray();
		long val = (bitArray[15]) ? ((int)Math.pow(-2, 15)) : 0;
		for(int pos = 0; pos < 15; pos++) {
			if(bitArray[pos]) {
				val+= ((int)Math.pow(2, pos));
			}
		}
		return ""+val;
	}

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

	public String generateBitString() {
		return halfWordArray[1].generateBitString() + halfWordArray[0].generateBitString();
	}

	public boolean[] generateBitArray() {
		boolean[] bitArray = new boolean[16];
		System.arraycopy(halfWordArray[0].byteArray, 0, bitArray, 0, 8);
		System.arraycopy(halfWordArray[1].byteArray, 0, bitArray, 8, 8);
		return bitArray;
	}

	public static void main(String[] args) {
		HalfWord hw = new HalfWord("FFFF", false);
		System.out.println(hw.calculateValueSigned());
	}
	
	public HalfWord() {
		halfWordArray = new BYTE[2];
		halfWordArray[0] = new BYTE();
		halfWordArray[1] = new BYTE();
	}
	
	public HalfWord(BYTE bLower, BYTE bUpper) {
		halfWordArray = new BYTE[2];
		halfWordArray[0] = bLower;
		halfWordArray[1] = bUpper;
	}
	
	public HalfWord(String hex, boolean LE) {
		if(LE)
			hex = LittleEndian.LEHexFixer(hex, 4);
		else
			hex = LittleEndian.hexFixer(hex,4);
		String byteUpper = "";
		String byteLower = "";
		if(LE) {
			byteUpper = hex.substring(2,4);
			byteLower = hex.substring(0,2);
		} else {
			byteUpper = hex.substring(0,2);
			byteLower = hex.substring(2, 4);
		}
		boolean[] byteUpperArray = BYTE.getByte(byteUpper);
		boolean[] byteLowerArray = BYTE.getByte(byteLower);
		BYTE bUpper = new BYTE(byteUpperArray);
		BYTE bLower = new BYTE(byteLowerArray);
		halfWordArray = new BYTE[2];
		halfWordArray[0] = bLower;
		halfWordArray[1] = bUpper;
	}
	
	public HalfWord(boolean bitArray[], boolean LE) {
		boolean[] lowerByte = new boolean[8];
		boolean[] upperByte = new boolean[8];
		if(LE){
			System.arraycopy(bitArray, 0, upperByte, 0, 8);
			System.arraycopy(bitArray, 8, lowerByte, 0, 8);
		} else {
			System.arraycopy(bitArray, 0, lowerByte, 0, 8);
			System.arraycopy(bitArray, 8, upperByte, 0, 8);
		}
		BYTE bUpper = new BYTE(upperByte);
		BYTE bLower = new BYTE(lowerByte);
		halfWordArray = new BYTE[2];
		halfWordArray[0] = bLower;
		halfWordArray[1] = bUpper;
	}
	

	public String generateHexLE() {
		return halfWordArray[0].generateHex() + halfWordArray[1].generateHex();
	}

	public String generateHex() {
		return halfWordArray[1].generateHex() + halfWordArray[0].generateHex();
		
	}
	
	public String generateBitStringLE() {
		return halfWordArray[0].generateBitString() + halfWordArray[1].generateBitString();
	}
	
	public String toString() {
		return this.generateHex();
	}
	
}
