import java.util.HashMap;

public class Instruction {
	
	DoubleWord immediate;
	String Rs1, Rs2;
	DoubleWord RS1Val, RS2Val, EVal, MVal;
	//0 - RS1Val, 1 - RS2Val, 2 - EVal, 3 - MVal
	String type, instruction;
	boolean memory;
	public boolean stop;
}
