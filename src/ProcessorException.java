
public class ProcessorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProcessorException(String message, Instruction instruction) {
		super("The instruction: "+ instruction.buildDisplayInstruction() + " at address: 0x" + instruction.address.displayToString() +
				" generated the error:\n"+ message);
	}

	public ProcessorException(String message, String processorMessage) {
		super(processorMessage +"\n" + message);
	}

}
