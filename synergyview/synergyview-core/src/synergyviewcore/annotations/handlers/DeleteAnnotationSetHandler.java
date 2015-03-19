package synergyviewcore.annotations.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.collections.model.CollectionMediaClipNode;
import synergyviewcore.model.ModelPersistenceException;

public class DeleteAnnotationSetHandler extends AbstractHandler implements
		IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection structSel = (IStructuredSelection) selection;
		@SuppressWarnings("rawtypes")
		Iterator iteratorCaptionInterval = structSel.iterator();
		boolean answer = MessageDialog.openConfirm(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Delete Annotation Set confirmation", "Are you sure you want to delete Annotation Set?");
		if (answer) {
			while (iteratorCaptionInterval.hasNext()) {
				Object object = iteratorCaptionInterval.next();
				if (object instanceof AnnotationSetNode) {
					AnnotationSetNode node = (AnnotationSetNode) object;
					try {
						((CollectionMediaClipNode) node.getParent()).removeAnnotationSetNode(node);
					} catch (ModelPersistenceException e) {
						MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Delete Error", "Unable to delete " + node.getLabel() + "!");
					}
				}
			}
		}
		return null;
	}

}
