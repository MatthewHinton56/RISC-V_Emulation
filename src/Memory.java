import java.util.ArrayList;
import java.util.HashMap;

public class Memory {
	public static final HashMap<Long,BYTE> memory = new HashMap<Long,BYTE>();

	public static DoubleWord loadDoubleWord(long position) {
		String immediate = "";
		if(position % 8 != 0) {
			throw new MemoryException(position, "DoubleWord");
		}
		
		for(long i = position; i < position + 8; i++) {
			if(memory.get(i) == null) {
				immediate += "00";
			} else {
				immediate += memory.get(i).generateHex();
			}
		}

		return new DoubleWord(immediate, true);
	}

	public static boolean storeDoubleWord(long position, DoubleWord val) {
		if(position % 8 != 0) {
			throw new MemoryException(position, "DoubleWord");
		}
		
		for(long i = position; i < position + 8; i++)
			memory.put(i, new BYTE(val.getBYTE((int) (i-position)).generateHex()));
		
		return true;
	}

	public static Word loadWord(long position) {
		String immediate = "";
		if(position % 4 != 0) {
			throw new MemoryException(position, "Word");
		}
		
		for(long i = position; i < position + 4; i++) {
			if(memory.get(i) == null) {
				immediate += "00";
			} else {
				immediate += memory.get(i).generateHex();
			}
		}

		return new Word(immediate, true);
	}

	public static HalfWord loadHalfWord(long position) {
		if(position % 2 != 0) {
			throw new MemoryException(position, "HalfWord");
		}
		String immediate = "";
		for(long i = position; i < position + 2; i++) {
			if(memory.get(i) == null) {
				immediate += "00";
			} else {
				immediate += memory.get(i).generateHex();
			}
		}

		return new HalfWord(immediate, true);
	}

	public static BYTE loadBYTE(long position) {
		String immediate = "";
		if(memory.get(position) == null) {
			immediate += "00";
		} else {
			immediate += memory.get(position).generateHex();
		}

		return new BYTE(immediate);
	}

	public static boolean storeWord(long position, Word val) {
		if(position % 4 != 0) {
			throw new MemoryException(position, "Word");
		}
		
		for(long i = position; i < position + 4; i++)
			memory.put(i, new BYTE(val.getBYTE((int) (i-position)).generateHex()));
		return true;
	}

	public static boolean storeHalfWord(long position, HalfWord val) {
		if(position % 2 != 0) {
			throw new MemoryException(position, "HalfWord");
		}
		
		for(long i = position; i < position + 2; i++)
			memory.put(i, new BYTE(val.getBYTE((int) (i-position)).generateHex()));
		return true;
	}

	public static void storeBYTE(long position, BYTE val) {
		memory.put(position, val);
	}

	public static LittleEndian load(long address, int size) {
		switch(size) {
		case 1:
			return Memory.loadBYTE(address);
		case 2: 
			return Memory.loadHalfWord(address);
		case 4:
			return Memory.loadWord(address);
		case 8:
			return Memory.loadDoubleWord(address);
		default:
			return null;
		}
	}
	
	public static HashMap<Long, BYTE> createImage() {
		HashMap<Long, BYTE> image = new HashMap<Long, BYTE>();
		for(long reg: memory.keySet()) {
			image.put(reg, memory.get(reg));
		}
		return image;
	}
	
	public static ArrayList<Long> getDif(HashMap<Long, BYTE> memoryBefore, HashMap<Long, BYTE> memoryAfter) {
		ArrayList<Long> dif = new ArrayList<Long>();
		for(Long reg: memoryAfter.keySet()) {
			if(!memoryBefore.containsKey(reg) || !memoryAfter.get(reg).equals(memoryBefore.get(reg)))
				dif.add(reg);
		}
		return dif;
	}
}