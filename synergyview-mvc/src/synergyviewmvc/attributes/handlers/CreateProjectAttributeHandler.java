package synergyviewmvc.attributes.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewmvc.attributes.model.IAttributeNode;
import synergyviewmvc.attributes.ui.wizards.NewAttributeWizard;

public class CreateProjectAttributeHandler extends AbstractHandler
implements IHandler {



	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		if (structSel.size()>0) {
			Object element = structSel.iterator().next();

			NewAttributeWizard wizard = new NewAttributeWizard((IAttributeNode) element);
			wizard.init(window.getWorkbench(), selection instanceof IStructuredSelection ? (IStructuredSelection) selection : StructuredSelection.EMPTY);
			WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
			dialog.open();
		} 

		//		IInputValidator validator = new IInputValidator() {
		//			public String isValid(String newText) {
		//				if(!newText.equalsIgnoreCase(""))
		//					return null;
		//				else
		//					return "Name empty!";
		//			}
		//		};
		//		InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Edit", "Enter caption:", "", validator);
		//		String attributeName = null;
		//		if(dialog.open() == Window.OK) {
		//			attributeName = dialog.getValue();
		//		} else return null;
		//

		return null;
	}

}

