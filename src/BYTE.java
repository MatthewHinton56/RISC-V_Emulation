import java.util.Arrays;

public class BYTE implements LittleEndian {

	protected boolean[] byteArray;

	public String calculateValueSigned() {
		int val = (byteArray[7]) ? ((int)Math.pow(-2, 7)) : 0;
		for(int pos = 0; pos < 7; pos++) {
			if(byteArray[pos]) {
				val+= ((int)Math.pow(2, pos));
			}
		}
		return ""+val;
	}

	public String calculateValueUnSigned() {
		int val = 0;
		for(int pos = 0; pos <= 7; pos++) {
			if(byteArray[pos]) {
				val+= ((int)Math.pow(2, pos));
			}
		}
		return ""+val;
	}

	public String generateBitString() {
		String s = "";
		for(int pos = 7; pos >=0; pos--)
			s+= (byteArray[pos]) ? "1" : "0";
		return s;
	}

	public boolean[] generateBitArray() {
		return byteArray;
	}
	
	public BYTE(boolean[] array) {
		byteArray = array.clone();
	}
	
	public BYTE() {
		byteArray = new boolean[8];
	}
	
	public static void main(String[] args) {
		boolean[] array = new boolean[] {true,true,false,false,false,true,false,true};
		System.out.println((new BYTE(array).generateHex()));
	}

	@Override
	public String generateHex()
	{
		int valLowerFour = 0;
		int valUpperFour = 0;
		for(int pos = 0; pos < 4; pos++) {
			if(byteArray[pos]) {
				valLowerFour+= ((int)Math.pow(2, pos));
			}
			if(byteArray[pos+4]) {
				valUpperFour+= ((int)Math.pow(2, pos));
			}
		}
		String lowerFour = (valLowerFour > 9) ? ((char)(55+valLowerFour))+"" : ""+ valLowerFour;
		String upperFour = (valUpperFour > 9) ? ((char)(55+valUpperFour))+"" : ""+ valUpperFour;
		
		return upperFour+lowerFour;
	}

	@Override
	public String generateHexLE() {
		// TODO Auto-generated method stub
		return generateHexLE();
	}

	@Override
	public String generateBitStringLE() {
		// TODO Auto-generated method stub
		return generateBitString();
	}
	
	public BYTE(String hex) {
		this(getByte(hex));
	}
	
	public static boolean[] getNibble(char hex) {
		boolean[] nibble = new boolean[4];
		int val = 0;
		if(Character.isLowerCase(hex)) {
			 val = hex - 'a'+10;
		} else if(Character.isUpperCase(hex)) {
			val = hex - 'A' +10;
		} else {
			val = hex - '0';
		}
		for(int pos = 3; pos >= 0; pos--) {
			int pow = ((int)Math.pow(2, pos));
			if(val >= pow) {
				nibble[pos] = true;
				val-=pow;
			}
		}
		return nibble;
		
	}
	
	public static boolean[] getByte(String hex) {
		hex = LittleEndian.hexFixer(hex, 2);
		boolean[] byteArray = new boolean[8];
		char[] nibbles = hex.toCharArray();
		System.arraycopy(getNibble(nibbles[1]), 0, byteArray, 0, 4);
		System.arraycopy(getNibble(nibbles[0]), 0, byteArray, 4, 4);
		return byteArray;
	}
	
	public String toString() {
		return this.generateHex();
	}
	
	
}
