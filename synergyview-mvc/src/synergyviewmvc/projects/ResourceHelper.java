package synergyviewmvc.projects;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class ResourceHelper {

	public static void deleteResources(final IResource[] resources) {
		try {
			IRunnableContext context = PlatformUI.getWorkbench().getProgressService();
			context.run(true, false, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

						@Override
						protected void execute(IProgressMonitor monitor)
								throws CoreException,
								InvocationTargetException, InterruptedException {
							try {
								monitor.beginTask("Deleting resources...", resources.length);
								for(IResource resource : resources) {
									resource.delete(true, new SubProgressMonitor(monitor,1));
								}
							} finally {
								monitor.done();
							}
							
						}
						
					};
					
					op.run(monitor);
					
				}
				
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
