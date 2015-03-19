package synergyviewcore.workspace;

public class WorkspaceException extends Exception {
	private static final long serialVersionUID = 5149543428168498207L;
	public WorkspaceException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public WorkspaceException(String message) {
		super(message);
	}
}
