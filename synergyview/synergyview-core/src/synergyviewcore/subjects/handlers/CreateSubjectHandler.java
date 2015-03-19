/**
 *  File: NewSubjectHandler.java
 *  Copyright (c) 2010
 *  phyo
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package synergyviewcore.subjects.handlers;

import java.util.UUID;

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

import synergyviewcore.subjects.model.Subject;
import synergyviewcore.subjects.model.SubjectRootNode;

/**
 * @author phyo
 *
 */
public class CreateSubjectHandler extends AbstractHandler implements IHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		Object element = structSel.iterator().next();
		
		if (element instanceof SubjectRootNode) {
			final SubjectRootNode sf = (SubjectRootNode) element;
			IInputValidator validator = new IInputValidator() {
				public String isValid(String newText) {
					if (!newText.equalsIgnoreCase("")) {
						if (sf.getChildrenNames().contains(newText))
							return "Already exist!";
						else return null;
					}
					else
						return "Name empty!";
				}
			};
			InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "New Subject", "Enter subject name:", "", validator);
			if(dialog.open() == Window.OK) {
				Subject subject = new Subject();
				subject.setId(UUID.randomUUID().toString());
				subject.setName(dialog.getValue());
				sf.addChildCollection(subject);
			} 
			return null;
		} else return null;
	}


}
