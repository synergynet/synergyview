package synergyviewcore.sharing;

public class SharingException extends Exception {
	private static final long serialVersionUID = 6616840260755427861L;
	public SharingException(String message, Throwable throwable) {
		super(message, throwable);
	}
	public SharingException(String message) {
		super(message);
	}
}
