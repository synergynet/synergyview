package synergyviewcore.workspace.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.project.ui.model.StudyNode;
import synergyviewcore.workspace.WorkspaceController;
import synergyviewcore.workspace.WorkspaceException;

public class DeleteStudyHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		List<IProject> studiesToRemove = new ArrayList<IProject>();
		for(Iterator<?> i = structSel.iterator(); i.hasNext();) {
			Object element = i.next();
			if (element instanceof StudyNode) {
				studiesToRemove.add(((StudyNode) element).getResource());
			}
		}
		try {
			WorkspaceController.getInstance().removeStudies(studiesToRemove);
		} catch (WorkspaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
