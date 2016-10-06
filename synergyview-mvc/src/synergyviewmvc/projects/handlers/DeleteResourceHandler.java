package synergyviewmvc.projects.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import uk.ac.durham.tel.commons.jface.node.INode;
import uk.ac.durham.tel.commons.jface.node.NodeRemoveException;

public class DeleteResourceHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		for(Iterator<?> i = structSel.iterator(); i.hasNext();) {
			Object element = i.next();
			if (element instanceof INode) {
				INode node = (INode) element;
				try {
					((INode) element).getParent().deleteChildren(new INode[]{node});
				} catch (NodeRemoveException e) {
					e.printStackTrace();
					MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(),SWT.ICON_ERROR);
					messageBox.setText("Unable to remove!");
					messageBox.setMessage(e.getMessage());
					messageBox.open();
				}
			}
		}
		return null;
	}

}
