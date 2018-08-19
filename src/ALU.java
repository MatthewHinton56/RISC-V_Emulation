
public class ALU {
	//only ALU can set these
	private static boolean CF, SF, OF;

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
		SF = isNeg(c);
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
		return c;
	}
	// a - b
	public static boolean[] SUB(boolean[] a, boolean[] b) {
		b = NEG(b);
		boolean[] c = ADD(a,b);
		CF = !CF;
		return c;
	}

	public static boolean[] NEG(boolean[] a) {
		return ADDONE(NOT(a));
	}

	public static boolean[] ABS(boolean[] a) {
		return (isNeg(a)) ? NEG(a) : a;
	}

	public static boolean isNeg(boolean[] a) {
		return a[a.length-1];
	}



	public static boolean[] shiftLeft(boolean[] a, int shamt) {
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
		return (neg) ? NEG(c) : c;
	}
	//arraySize <= 63
	// -2^(arraySize-1) <= l <= 2^(arraySize-1) -1
	public static boolean[] longToBitArrayUnsigned(long l, int arraySize) {
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

	public static boolean[] multiply(boolean[] multiplicand, boolean[] multiplier) {
		boolean[] product = new boolean[multiplicand.length];
		for(int i = 0; i < multiplicand.length; i++) {
			if(multiplicand[i]) {
				product = ALU.ADD(product, multiplier);
			}
			multiplier = ALU.shiftLeft(multiplier, 1);
		}
		return product;
	}

	public static boolean[] upperBits(boolean[] multiplicand, boolean[] multiplier, boolean multiplicandSigned, boolean multiplierSigned) {
		boolean[] multiplicandExtended = signExtension(multiplicand, !multiplicandSigned, multiplicand.length*2 );
		boolean[] multiplierExtended = signExtension(multiplier, !multiplierSigned, multiplier.length*2 );
		boolean result[] = multiply(multiplicandExtended,multiplierExtended);
		boolean[] product = new boolean[multiplicand.length];
		System.arraycopy(result, result.length/2, product, 0, result.length/2);
		return product;
	}
	//if remainder is true, returns the remainder
	public static boolean[] unsignedDivision(boolean[] divisor, boolean[] dividend, boolean remainder) {
		boolean[] quotient = new boolean[divisor.length];
		dividend = cloneArray(dividend);
		divisor = divisorShift(divisor);
		for(int i = rightMostOne(divisor); i >= 0; i--) {
			if(canSubtract(dividend, divisor)) {
				quotient[i] = true;
				dividend = divSubtract(dividend, divisor);
			}
			divisor = ALU.shiftRight(divisor, 1, true);
		}
		return (remainder) ? dividend : quotient;
	}

	public static boolean[] signedDivision(boolean[] divisor, boolean[] dividend, boolean remainder) {
		boolean[] divisorABS = ABS(divisor);
		boolean[] dividendABS = ABS(dividend);
		boolean[] ret = unsignedDivision(divisorABS, dividendABS, remainder);
		if(remainder) {
			return (isNeg(dividend)) ? NEG(ret) : ret;
		} else {
			return (isNeg(dividend) ^ isNeg(divisor)) ? NEG(ret) : ret;
		}
	}



	private static boolean[] divSubtract(boolean[] dividend, boolean[] divisor) {
		boolean[] ret = new boolean[dividend.length];
		for(int i = 0; i < ret.length; i++) {
			if(!divisor[i] && dividend[i])
				ret[i] = true; 
		}
		return ret;
	}

	private static boolean[] divisorShift(boolean[] divisor) {
		int leftMostOne = -1;
		int pos = divisor.length-1;
		while(leftMostOne == -1 && pos >= 0) {
			if(divisor[pos])
				leftMostOne = pos;
			pos--;
		}
		int shamt = (divisor.length-1) - leftMostOne;
		return ALU.shiftLeft(divisor, shamt);
	}

	public static int rightMostOne(boolean[] bitArray) {
		int rightMostOne = -1;
		int pos = 0;
		while(rightMostOne == -1 && pos < bitArray.length) {
			if(bitArray[pos])
				rightMostOne = pos;
			pos++;
		}
		return rightMostOne;
	}


	public static boolean canSubtract(boolean[] minuend, boolean[] subtrahend) {
		for(int i = 0; i < minuend.length; i++) {
			if(!minuend[i] && subtrahend[i])
				return false;
		}
		return true;
	}

	public static long calculateValueSigned(boolean[] bitArray) {
		if(bitArray == null)
			return 0;
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

	public static boolean[] cloneArray(boolean[] bitArray) {
		boolean[] c = new boolean[bitArray.length];
		for(int i = 0; i < bitArray.length; i++) {
			c[i] = bitArray[i];
		}
		return bitArray;
	}

}
