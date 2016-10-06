package synergyviewmvc.annotations.handlers;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewmvc.annotations.ui.AnnotationIntervalImpl;
import synergyviewmvc.annotations.ui.SubjectRowModel;


public class AnnotationRemoveHandler extends AbstractHandler implements IHandler {

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
				cElement.getOwner().remInterval(cElement);
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
