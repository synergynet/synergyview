package synergyviewcore.workspace.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.project.ui.model.StudyNode;
import synergyviewcore.workspace.WorkspaceController;
import synergyviewcore.workspace.WorkspaceException;

public class RenameStudyHandler extends AbstractHandler implements IHandler {


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		Object element = structSel.iterator().next();		
		if (element instanceof StudyNode) {
			IProject studyResource = ((StudyNode) element).getResource();
			InputDialog studyNameDialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Rename Study", "Enter new name",  "", null);
			int result = studyNameDialog.open();
			if (InputDialog.OK == result) {
				try {
					WorkspaceController.getInstance().renameStudy(studyResource, studyNameDialog.getValue());
				} catch (WorkspaceException e) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", e.getMessage());
				}
			}
		}
		return null;
	}
}
