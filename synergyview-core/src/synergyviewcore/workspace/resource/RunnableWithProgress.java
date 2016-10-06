package synergyviewcore.workspace.resource;

import org.eclipse.jface.operation.IRunnableWithProgress;

public abstract class RunnableWithProgress implements IRunnableWithProgress {
	protected Exception error;
	public Exception getError() {
		return error;
	}
}
