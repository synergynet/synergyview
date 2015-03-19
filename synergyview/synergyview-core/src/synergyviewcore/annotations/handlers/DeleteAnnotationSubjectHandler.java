package synergyviewcore.annotations.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.annotations.ui.SubjectRowModel;
import synergyviewcore.subjects.model.Subject;

public class DeleteAnnotationSubjectHandler extends AbstractHandler implements
		IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection structSel = (IStructuredSelection) selection;

		@SuppressWarnings("rawtypes")
		Iterator iteratorCaptionInterval = structSel.iterator();
		List<SubjectRowModel> subjectRowsToBeRemoved = new ArrayList<SubjectRowModel>();
		while (iteratorCaptionInterval.hasNext()) {
			Object element = iteratorCaptionInterval.next();
			if (element instanceof SubjectRowModel) {
				SubjectRowModel cElement = (SubjectRowModel) element;
				subjectRowsToBeRemoved.add(cElement);
			}
		} 
		if (subjectRowsToBeRemoved.size()>0) {
			boolean answer = MessageDialog.openConfirm(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Delete Confirmation", String.format("Are you sure you want to delete %d Subject(s)?", subjectRowsToBeRemoved.size()));
			if (answer == true) {
				
				for (SubjectRowModel subjectRow : subjectRowsToBeRemoved) {
					List<Subject> subjectToBeRemoved = new ArrayList<Subject>();
					subjectToBeRemoved.add(subjectRow.getSubject());
					try {
						subjectRow.getAnnotationSetNode().removeSubjects(subjectToBeRemoved);
					} catch (Exception e) {
						MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", String.format("Unable to delete %s.",subjectRow.getSubject().getName()));
					}
				}
			}
			
		}
		return null;
	}
}
