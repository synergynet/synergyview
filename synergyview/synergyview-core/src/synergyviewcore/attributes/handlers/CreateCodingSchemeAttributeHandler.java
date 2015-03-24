package synergyviewcore.attributes.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;


/**
 * The Class CreateCodingSchemeAttributeHandler.
 */
public class CreateCodingSchemeAttributeHandler extends AbstractHandler
implements IHandler {



	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
//		ISelection selection = HandlerUtil.getCurrentSelection(event);
//		if (!(selection instanceof IStructuredSelection))
//			return null;
//		IStructuredSelection structSel = (IStructuredSelection) selection;
//		IAttributeNode node;
//		if (structSel.size()>0) {
//			Object element = structSel.iterator().next();
//			node = (IAttributeNode) element;
//		} else node = (IAttributeNode) CodingRoot.getInstance();
//			
//		NewAttributeWizard wizard = new NewAttributeWizard(node);
//		wizard.init(window.getWorkbench(), selection instanceof IStructuredSelection ? (IStructuredSelection) selection : StructuredSelection.EMPTY);
//		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
//		dialog.open();

		return null;
	}

}
