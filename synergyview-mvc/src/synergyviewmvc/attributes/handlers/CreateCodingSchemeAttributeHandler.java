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

import synergyviewmvc.attributes.model.CodingRoot;
import synergyviewmvc.attributes.model.IAttributeNode;
import synergyviewmvc.attributes.ui.wizards.NewAttributeWizard;

public class CreateCodingSchemeAttributeHandler extends AbstractHandler
implements IHandler {



	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		IAttributeNode node;
		if (structSel.size()>0) {
			Object element = structSel.iterator().next();
			node = (IAttributeNode) element;
		} else node = (IAttributeNode) CodingRoot.getInstance();
			
		NewAttributeWizard wizard = new NewAttributeWizard(node);
		wizard.init(window.getWorkbench(), selection instanceof IStructuredSelection ? (IStructuredSelection) selection : StructuredSelection.EMPTY);
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
//		String attributeName = null;
//		if(dialog.open() == Window.OK) {
//			attributeName = dialog.getValue();
//		} else return null;

//
//		IStructuredSelection structSel = (IStructuredSelection) selection;
//		if (structSel.size()>0) {
//			Object element = structSel.iterator().next();
//			if (element instanceof AttributeNode) {
//				AttributeNode node = (AttributeNode) element;
//				Attribute attribute = new Attribute();
//				attribute.setId(UUID.randomUUID().toString());
//				attribute.setName(attributeName);
//				attribute.setParent(node.getAttribute());
//				node.getAttribute().getChildren().add(attribute);
//				node.addAttribute(attribute);
//			} 
//		} else {
//			CodingRoot root = CodingRoot.getInstance();
//			Attribute attribute = new Attribute();
//			attribute.setId(UUID.randomUUID().toString());
//			attribute.setName(attributeName);
//			root.addAttribute(attribute);
//		}
		return null;
	}

}
