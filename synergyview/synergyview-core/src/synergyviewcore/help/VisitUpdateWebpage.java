package synergyviewcore.help;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * The Class VisitUpdateWebpage.
 */
public class VisitUpdateWebpage extends AbstractHandler {
	
	/** The Constant helpURL. */
	private final static String helpURL = "https://github.com/synergynet/synergyview/releases";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(helpURL));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
