package synergyviewmedia;

public class IllegalMediaFormatException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public IllegalMediaFormatException(String string) {
		super(string);
	}
	
	public IllegalMediaFormatException(String string, Throwable t) {
		super(string, t);
	}
	
}
