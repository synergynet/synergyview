package synergyviewcore.projects.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.navigation.model.INode;
import synergyviewcore.projects.model.ProjectNode;

/**
 * The Class DeleteProjectHandler.
 */
public class DeleteProjectHandler extends AbstractHandler implements IHandler {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection structSel = (IStructuredSelection) selection;
		for (Iterator<?> i = structSel.iterator(); i.hasNext();) {
			Object element = i.next();
			if (element instanceof ProjectNode) {
				((ProjectNode) element).getParent().deleteChildren(
						new INode[] { (INode) element });
			}
		}
		return null;
	}
	
}
