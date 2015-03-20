package synergyviewcore.help;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class VisitUpdateWebpage extends AbstractHandler {

	private final static String helpURL = "https://github.com/synergynet/synergyview/releases";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(helpURL));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
