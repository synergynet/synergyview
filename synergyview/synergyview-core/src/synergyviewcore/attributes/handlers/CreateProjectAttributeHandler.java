package synergyviewcore.attributes.handlers;

import java.util.ArrayList;
import java.util.List;

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

import synergyviewcore.attributes.ui.views.CodingExplorerViewPart;
import synergyviewcore.attributes.ui.wizards.NewAttributeWizard;
import synergyviewcore.navigation.model.INode;

/**
 * The Class CreateProjectAttributeHandler.
 */
public class CreateProjectAttributeHandler extends AbstractHandler implements
		IHandler {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		if (window.getPartService().getActivePart() instanceof CodingExplorerViewPart) {
			CodingExplorerViewPart cPart = (CodingExplorerViewPart) window
					.getPartService().getActivePart();
			if (cPart.getRootNode() != null) {
				List<INode> selectionList = new ArrayList<INode>();
				selectionList.add(cPart.getRootNode());
				if (!(selection instanceof IStructuredSelection)
						|| ((IStructuredSelection) selection).isEmpty()) {
					selection = new StructuredSelection(selectionList);
				}
				NewAttributeWizard wizard = new NewAttributeWizard();
				wizard.init(window.getWorkbench(),
						(IStructuredSelection) selection);
				WizardDialog dialog = new WizardDialog(window.getShell(),
						wizard);
				dialog.open();
			}
		}
		
		return null;
	}
	
}
