
public interface LittleEndian {

	public String calculateValueSigned();
	public String calculateValueUnSigned();
	public String generateBitString();
	public boolean[] generateBitArray();
	public String generateHex();
	public String generateHexLE();
	public String generateBitStringLE();
	
	public static String LEHexFixer(String hex, int requiredSize) {
		int pad = requiredSize - hex.length();
		if(pad %2 == 1) {
			hex = hex.substring(0, hex.length()-1) + "0" + hex.substring(hex.length()-1);
			pad--;
		}
		for(int i = 0; i < pad; i++) {
			hex = hex + "0";
		}
		return hex;
	}
	
	public static String hexFixer(String hex, int requiredSize) {
		int pad = requiredSize - hex.length();
		for(int i = 0; i < pad; i++)
				hex = "0"+hex;
		return hex;
	}
	
}
