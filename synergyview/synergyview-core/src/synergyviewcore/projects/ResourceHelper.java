package synergyviewcore.projects;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import synergyviewcore.Activator;

public class ResourceHelper {

	public static void deleteResources(final IResource[] resources) {
		final ILog logger = Activator.getDefault().getLog();
		try {
			
			PlatformUI.getWorkbench().getProgressService().run(true, false, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

						@Override
						protected void execute(IProgressMonitor monitor)
								throws CoreException,
								InvocationTargetException, InterruptedException {
							try {
								monitor.beginTask("Deleting resources...", resources.length);
								for(int i = 0; i < resources.length; i++) {
									resources[i].delete(true, new SubProgressMonitor(monitor,1));
								}
							} catch (Exception ex) {
								IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
								logger.log(status);
							
							} finally {
								monitor.done();
							}
							
						}
						
					};
					
					op.run(monitor);
					
				}
				
			});
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}
	
}
