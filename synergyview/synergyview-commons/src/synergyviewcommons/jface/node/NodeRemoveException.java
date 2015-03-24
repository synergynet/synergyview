package synergyviewcommons.jface.node;

/**
 * The Class NodeRemoveException.
 */
public class NodeRemoveException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new node remove exception.
	 * 
	 * @param string
	 *            the string
	 * @param e
	 *            the e
	 */
	public NodeRemoveException(String string, DisposeException e) {
		super(string, e);
	}
}
