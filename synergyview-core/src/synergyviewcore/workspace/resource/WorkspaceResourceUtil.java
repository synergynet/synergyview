package synergyviewcore.workspace.resource;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import synergyviewcore.runtime.RunnableUtil;
import synergyviewcore.runtime.WorkspaceModifyOperation;


public class WorkspaceResourceUtil {
	private static Logger logger = Logger.getLogger(WorkspaceResourceUtil.class);
	
	public static void refreshResource(final IResource resourceToRefersh) throws Exception {
		WorkspaceModifyOperation refreshResourceOperation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				try {
					resourceToRefersh.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				} catch (CoreException e) {
					logger.error("Unable to refresh resource.", e);
					this.error = e;
				}
			}
		};
		RunnableUtil.runWithProgress(refreshResourceOperation);
		if (refreshResourceOperation.getError()!=null)
			throw new Exception("Unable to refresh resource.", refreshResourceOperation.getError());
	}
	
	
}
