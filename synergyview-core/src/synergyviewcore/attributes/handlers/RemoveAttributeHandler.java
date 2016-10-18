/**
 * File: RemoveAttributeHandler.java Copyright (c) 2010 phyokyaw This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcore.attributes.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.attributes.model.Attribute;
import synergyviewcore.attributes.model.AttributeNode;
import synergyviewcore.attributes.model.ProjectAttributeRootNode;

/**
 * The Class RemoveAttributeHandler.
 * 
 * @author phyokyaw
 */
public class RemoveAttributeHandler extends AbstractHandler implements IHandler {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands. ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
	ISelection selection = HandlerUtil.getCurrentSelection(event);
	if (!(selection instanceof IStructuredSelection)) {
	    return null;
	}
	IStructuredSelection structSel = (IStructuredSelection) selection;
	for (Iterator<?> i = structSel.iterator(); i.hasNext();) {
	    Object element = i.next();
	    if (element instanceof AttributeNode) {
		AttributeNode nodeToBeDeleted = (AttributeNode) element;
		try {
		    if (nodeToBeDeleted.getParent() instanceof ProjectAttributeRootNode) {
			nodeToBeDeleted.getProjectAttributeRootNode().removeAttribute(nodeToBeDeleted.getResource(), null);
		    } else {
			nodeToBeDeleted.getProjectAttributeRootNode().removeAttribute(nodeToBeDeleted.getResource(), (Attribute) nodeToBeDeleted.getParent().getResource());
		    }
		} catch (Exception ex) {

		    MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.ICON_ERROR);
		    messageBox.setText("Unable to remove!");
		    messageBox.setMessage(ex.getMessage());
		    messageBox.open();
		}
	    }
	}
	return null;
    }

}
