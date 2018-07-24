import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;


public class RegisterFile extends TreeMap<String, DoubleWord>{

	private static final long serialVersionUID = 1L;



	public RegisterFile() {
		super(new RegisterComparator());
		for(int r = 0; r < 32; r++) {
			super.put("x"+r, new DoubleWord());
		}
		this.put("pc", new DoubleWord());
	}

	public void set(String key, DoubleWord value) {
		// TODO Auto-generated method stub
			if(this.keySet().contains(key) && !key.equals("x0"))
				this.put(key, value);
		
	}
	
	public static void main(String[] args) {
		RegisterFile file = new RegisterFile();
		System.out.println(file);
	}
	
	public String toString() {
		String ret = "{";
		for(String key: this.keySet()) {
			ret+= key +" = 0x"+this.get(key)+", ";
		}
		ret = ret.substring(0,ret.length()-2) + "}";
		return ret;
	}

	public void reset() {
		for(String key: this.keySet()) {
			this.set(key, new DoubleWord());
		}
		
	}
	
	public TreeMap<String, DoubleWord> createImage() {
		TreeMap<String, DoubleWord> image = new TreeMap<String, DoubleWord>(new RegisterComparator());
		for(String reg: this.keySet()) {
			image.put(reg, this.get(reg));
		}
		return image;
	}
	
	
	public static ArrayList<String> getDif(TreeMap<String, DoubleWord> registerFileBefore, TreeMap<String, DoubleWord> registerFileAfter) {
		ArrayList<String> dif = new ArrayList<String>();
		for(String reg: registerFileBefore.keySet()) {
			if(!registerFileBefore.get(reg).equals(registerFileAfter.get(reg)))
				dif.add(reg);
		}
		return dif;
	}
	
}

class RegisterComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		if(o1.compareTo(o2) == 0)
			return 0;
		int letter = o1.charAt(0) - o2.charAt(0);
		if(letter != 0)
			return (letter < 0) ? -1 : 1;
		int i1 = Integer.parseInt(o1.substring(1));
		int i2 = Integer.parseInt(o2.substring(1));
		return ((i1-i2) < 0) ? -1 : 1;
	}
	
}

