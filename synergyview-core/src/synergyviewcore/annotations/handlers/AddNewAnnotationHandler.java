package synergyviewcore.annotations.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.collections.model.CollectionMediaClipNode;

/**
 * The Class AddNewAnnotationHandler.
 */
public class AddNewAnnotationHandler extends AbstractHandler implements IHandler {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands .ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
	ISelection selection = HandlerUtil.getCurrentSelection(event);
	if (!(selection instanceof IStructuredSelection)) {
	    return null;
	}
	IStructuredSelection structSel = (IStructuredSelection) selection;
	Object element = structSel.iterator().next();

	if (element instanceof CollectionMediaClipNode) {
	    IInputValidator validator = new IInputValidator() {
		public String isValid(String newText) {
		    if (!newText.equalsIgnoreCase("")) {
			return null;
		    } else {
			return "Name empty!";
		    }
		}
	    };
	    InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "New Annoatation Set", "Enter Annotation Set Name:", "", validator);
	    if (dialog.open() == Window.OK) {
		((CollectionMediaClipNode) element).addAnnotationSet(dialog.getValue());
	    } else {
		return null;
	    }
	}
	return null;
    }

}
