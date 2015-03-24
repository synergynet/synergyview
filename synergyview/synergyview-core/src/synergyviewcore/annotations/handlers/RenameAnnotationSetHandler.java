package synergyviewcore.annotations.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.Activator;
import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.model.ModelPersistenceException;


/**
 * The Class RenameAnnotationSetHandler.
 */
public class RenameAnnotationSetHandler extends AbstractHandler implements
		IHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ILog logger = Activator.getDefault().getLog();
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection structSel = (IStructuredSelection) selection;
		Object object = structSel.getFirstElement();
		if (object instanceof AnnotationSetNode) {
			AnnotationSetNode node = (AnnotationSetNode) object;
			IInputValidator validator = new IInputValidator() {
				public String isValid(String newText) {
					if(!newText.equalsIgnoreCase(""))
						return null;
					else
						return "Name empty!";
				}
			};
			InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Rename Annoatation Set", "Enter Annotation Set Name:", node.getResource().getName(), validator);
			if(dialog.open() == Window.OK) {
				try {
					node.renameAnnotationSet(dialog.getValue());
				} catch (ModelPersistenceException e) {
					IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,"Unable to rename the Annotation Set", e);
	        		logger.log(status);
					MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Save Error", "Unable to rename the Annotation Set!");
				}
			} else return null;
		}
		return null;
	}

}
