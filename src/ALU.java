import java.util.Arrays;
import java.util.Scanner;



public class ALU {
	//only ALU can set these
	private static boolean CF, ZF, SF, OF;

	//a.length ==  b.length
	public static boolean[] AND(boolean[] a, boolean[] b) {
		boolean[] c = new boolean[a.length];
		for(int pos = 0; pos < a.length; pos++)
			c[pos] = a[pos] && b[pos];
		return c;
	}

	public static boolean[] OR(boolean[] a, boolean[] b) {
		boolean[] c = new boolean[a.length];
		for(int pos = 0; pos < a.length; pos++)
			c[pos] = a[pos] || b[pos];
		return c;
	}

	public static boolean[] XOR(boolean[] a, boolean[] b) {
		boolean[] c = new boolean[a.length];
		for(int pos = 0; pos < a.length; pos++)
			c[pos] = a[pos] ^ b[pos];
		return c;
	}

	public static boolean[] NOT(boolean[] a) {
		boolean[] c = new boolean[a.length];
		for(int pos = 0; pos < a.length; pos++)
			c[pos] = !a[pos];
		return c;
	}

	public static boolean Equal(boolean[] a, boolean[] b) {
		for(int pos = 0; pos < a.length; pos++)
			if(a[pos] != b[pos])
				return false;
		return true;
	}

	public static boolean[] ADD(boolean[] a, boolean[] b) {
		boolean[] c = new boolean[a.length];
		boolean carry = false;
		for(int pos = 0; pos < a.length; pos++) {
			c[pos] = a[pos] ^ b[pos] ^ carry;
			boolean carryTemp = (a[pos] && b[pos]) || (a[pos] && carry) || (b[pos] && carry);
			if(pos == c.length-1) {
				OF = carry ^ carryTemp;
			}
			carry = carryTemp;
		}
		CF = carry;
		ZF = ALU.Equal(c, new boolean[a.length]);
		SF = c[c.length-1];
		return c;
	}
	
	public static boolean[] IADD(boolean[] a, boolean[] b) {
		boolean[] c = new boolean[a.length];
		boolean carry = false;
		for(int pos = 0; pos < a.length; pos++) {
			c[pos] = a[pos] ^ b[pos] ^ carry;
			boolean carryTemp = (a[pos] && b[pos]) || (a[pos] && carry) || (b[pos] && carry);
			if(pos == c.length-1) {
			}
			carry = carryTemp;
		}
		System.out.println("ALU output: "+ALU.calculateValueSigned(c));
		return c;
	}
	// a - b
	public static boolean[] SUB(boolean[] a, boolean[] b) {
		System.out.println("SUB " +(new DoubleWord(a)).calculateValueSigned()+" "+ (new DoubleWord(b)).calculateValueSigned());
		b = NOT(b);
		b = ADDONE(b);
		System.out.println("SUB " +(new DoubleWord(a)).calculateValueSigned()+" "+ (new DoubleWord(b)).calculateValueSigned());
		boolean[] c = ADD(a,b);
		CF = !CF;
		System.out.println("SUB " +(new DoubleWord(c)).calculateValueSigned());
		return c;
	}


	public static boolean[] shiftLeft(boolean[] a, int shamt) {
		System.out.println("here " +shamt);
		boolean[] c = new boolean[a.length];
		for(int pos = c.length-1; pos >= 0; pos--) {
			if(pos + shamt < c.length)
				c[pos+shamt] = a[pos];
		}
		return c;
	}

	public static boolean[] shiftRight(boolean[] a, int shamt, boolean logic) {
		boolean[] c = new boolean[a.length];
		boolean sign = a[a.length-1];
		for(int pos = 0; pos < c.length; pos++) {
			if(pos - shamt >= 0)
				c[pos - shamt] = a[pos];
		}
		if(!logic) {
			for(int pos = 0; pos < shamt; pos++)
				c[c.length-pos-1] = sign;
		}
		return c;
	}

	public static boolean LessThan(boolean[] a, boolean[] b, boolean U) {
		if(U && ALU.calculateValueSigned(b) == 0)
			return false;
		if(!U && ALU.calculateValueSigned(a) == ALU.T_MIN_Calculator(a) && ALU.calculateValueSigned(b) != ALU.T_MIN_Calculator(b))
			return true;
		if(!U && ALU.calculateValueSigned(a) != ALU.T_MIN_Calculator(a) && ALU.calculateValueSigned(b) == ALU.T_MIN_Calculator(b))
			return false;
		SUB(a,b);
		System.out.println("Unsigned: "+ U +" CF: "+ CF);
		if(U)
			return CF;
		return SF ^ OF;
	}
	
	public static boolean GreaterThanOrEqual(boolean[] a, boolean[] b, boolean U) {
		if(U && ALU.calculateValueSigned(b) == 0)
			return true;
		if(!U && ALU.calculateValueSigned(a) == ALU.T_MIN_Calculator(a) && ALU.calculateValueSigned(b) != ALU.T_MIN_Calculator(b))
			return false;
		if(!U && ALU.calculateValueSigned(a) != ALU.T_MIN_Calculator(a) && ALU.calculateValueSigned(b) == ALU.T_MIN_Calculator(b))
			return true;
		if(ALU.calculateValueSigned(a) == ALU.T_MIN_Calculator(a) && ALU.calculateValueSigned(b) == ALU.T_MIN_Calculator(b))
			return true;
		SUB(a,b);
		if(U)
			return !CF;
		return !(SF ^ OF);
	}


	public static boolean[] ADDFOUR(boolean[] a) {
		boolean[] b = new boolean[a.length];
		b[2] = true;
		return IADD(a,b);
	}

	public static boolean[] INCREMENTEIGHT(boolean[] a) {
		boolean[] b = new boolean[a.length];
		b[3] = true;
		return IADD(a,b);
	}

	public static boolean[] DECREMENTEIGHT(boolean[] a) {
		boolean[] b = NEGATIVE_EIGHT;
		return IADD(a,b);
	}


	public static boolean[] ADDONE(boolean[] a) {
		boolean[] b = new boolean[a.length];
		b[0] = true;
		return IADD(a,b);
	}

	public static boolean[] signExtension(boolean[] a, boolean U, int targetSize) {
		boolean[] c = new boolean[targetSize];
		boolean sign = a[a.length-1];
		System.arraycopy(a, 0, c, 0, a.length);
		if(U || !sign) {
			return c;
		}
		for(int pos = a.length; pos < targetSize; pos++)
			c[pos] = true;
		return c;
	}

	// Test Bed

	//arraySize <= 64
	// -2^(arraySize-1) <= l <= 2^(arraySize-1) -1
	public static boolean[] longToBitArray(long l, int arraySize) {
		long T_MIN = (long) (-1 * Math.pow(2, arraySize-1));
		long T_MAX =  (long) (-1 + Math.pow(2, arraySize-1));
		boolean[] c = new boolean[arraySize];
		if(l == T_MIN) {
			c[c.length-1] = true;
			return c;
		}
		boolean neg = l < 0;
		l = Math.abs(l);
		for(int pos = arraySize-2; pos >= 0; pos--) {
			long val = ((long)Math.pow(2, pos));
			if(val <= l) {
				c[pos] = true;
				l-=val;
			}
		}
		if(neg)
			c = ADDONE(NOT(c));
		return c;
	}
	//arraySize <= 63
	// -2^(arraySize-1) <= l <= 2^(arraySize-1) -1
	public static boolean[] longToBitArrayUnsigned(long l, int arraySize) {
		long T_MAX =  (long) (-1 + Math.pow(2, arraySize));
		boolean[] c = new boolean[arraySize];
		for(int pos = arraySize-1; pos >= 0; pos--) {
			long val = ((long)Math.pow(2, pos));
			if(val <= l) {
				c[pos] = true;
				l-=val;
			}
		}
		return c;
	}

	public static int bitArrayToInt(boolean[] bitArray) {
		int val = 0;
		for(int i = 0; i < bitArray.length; i++) {
			val += (bitArray[i]) ? (int)Math.pow(2, i) : 0;
		}
		return val;
	}
	

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		while(true) {
			System.out.print("valA: ");
			int valA = scan.nextInt();
			System.out.print("valB: ");
			int valB = scan.nextInt();
			boolean[] a = longToBitArray(valA,8);
			System.out.println(Arrays.toString(a));
			boolean[] b = longToBitArray(valB,8);
			System.out.println(Arrays.toString(b));
			boolean c = LessThan(a,b, false);
			/*BYTE byt = new BYTE(c);
			System.out.println(Arrays.toString(c));
			System.out.println(byt.generateHex());
			System.out.println(byt.generateBitString());
			System.out.println(byt.calculateValueUnSigned());
			System.out.println("SF:" + SF);
			System.out.println("OF:" + OF);
			System.out.println("ZF:" + ZF);*/
			System.out.println(c);
			System.out.println(SF ^ OF);
		}
	}
	
	public static long calculateValueSigned(boolean[] bitArray) {
		long val = (bitArray[bitArray.length-1]) ? ((long)Math.pow(-2, bitArray.length-1)) : 0;
		for(int pos = 0; pos < bitArray.length-1; pos++) {
			if(bitArray[pos]) {
				val+= ((long)Math.pow(2, pos));
			}
		}
		return val;
	}

	public static boolean[] NEGATIVE_EIGHT = longToBitArray(-8, 64);

	public static String bitString(boolean[] bitArray) {
		String s = "";
		for(int i = 0; i < bitArray.length; i++) {
			s = ((bitArray[i]) ? "1" : "0") + s;
		}
		return s;
	}
	
	public static long T_MIN_Calculator(boolean[] bitArray) {
		return (long)(Math.pow(2, bitArray.length-1)*-1);
	}
	
	

}
