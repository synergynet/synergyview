package synergyviewmvc.annotations.handlers;

import java.util.Iterator;

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

import synergyviewmvc.annotations.ui.AnnotationIntervalImpl;
import synergyviewmvc.annotations.ui.SubjectRowModel;

public class CaptionEditHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection structSel = (IStructuredSelection) selection;
		
		@SuppressWarnings("rawtypes")
		Iterator iteratorCaptionInterval = structSel.iterator();
		while (iteratorCaptionInterval.hasNext()) {
			Object element = iteratorCaptionInterval.next();
			if (element instanceof AnnotationIntervalImpl) {
				AnnotationIntervalImpl cElement = (AnnotationIntervalImpl) element;
				IInputValidator validator = new IInputValidator() {
					public String isValid(String newText) {
						if(!newText.equalsIgnoreCase(""))
							return null;
						else
							return "Name empty!";
					}
				};
				InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Edit", "Enter caption:", cElement.getLabel(), validator);
				if(dialog.open() == Window.OK) {
					cElement.setLabel(dialog.getValue());
				}
				return null;
			}
		}
		Object element = structSel.iterator().next();
		if (element instanceof SubjectRowModel) {
			SubjectRowModel rowToDelete = (SubjectRowModel) element;
			rowToDelete.getOwner().remRow(rowToDelete);
		} 
		return null;

	}

}
