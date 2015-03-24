package synergyviewcore.collections.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.collections.model.CollectionNode;
import synergyviewcore.collections.ui.wizards.ExportCollectionAnnotationWizard;


/**
 * The Class ExportAnnotationForCollectionHandler.
 */
public class ExportAnnotationForCollectionHandler extends AbstractHandler
		implements IHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		Object element = structSel.iterator().next();
		
		if (element instanceof CollectionNode) {
			CollectionNode collectionNode = (CollectionNode) element;
			WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
					new ExportCollectionAnnotationWizard(collectionNode));
			dialog.open();
			return null;
		} else return null;
	}

}
