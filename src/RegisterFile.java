import java.util.Comparator;
import java.util.TreeMap;


public class RegisterFile extends TreeMap<String, DoubleWord>{

	private static final long serialVersionUID = 1L;



	public RegisterFile() {
		
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
	
	
}

