package synergyviewmvc.annotations.handlers;

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

import synergyviewmvc.collections.model.CollectionMediaClipNode;

public class AddNewAnnotationHandler extends AbstractHandler implements
IHandler {


	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		Object element = structSel.iterator().next();

		if (element instanceof CollectionMediaClipNode) {
			IInputValidator validator = new IInputValidator() {
				public String isValid(String newText) {
					if(!newText.equalsIgnoreCase(""))
						return null;
					else
						return "Name empty!";
				}
			};
			InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "New Annoatation", "Enter annotation name:", "", validator);
			if(dialog.open() == Window.OK) {
				((CollectionMediaClipNode) element).addAnnotation(dialog.getValue());
			} else return null;
		}
		return null;
	}

}
