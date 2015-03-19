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

import synergyviewcore.annotations.model.Annotation;
import synergyviewcore.annotations.ui.AnnotationIntervalImpl;

public class DeleteAnnoationHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection structSel = (IStructuredSelection) selection;

		@SuppressWarnings("rawtypes")
		Iterator iteratorCaptionInterval = structSel.iterator();
		List<AnnotationIntervalImpl> annotationImplsToBeRemoved = new ArrayList<AnnotationIntervalImpl>();
		while (iteratorCaptionInterval.hasNext()) {
			Object element = iteratorCaptionInterval.next();
			if (element instanceof AnnotationIntervalImpl) {
				AnnotationIntervalImpl annotationInterval = (AnnotationIntervalImpl) element;
				annotationImplsToBeRemoved.add(annotationInterval);
			}
		} 
		if (annotationImplsToBeRemoved.size()>0) {
			boolean answer = MessageDialog.openConfirm(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Delete Annotation confirmation", String.format("Are you sure you want to delete %d Annotation(s)?", annotationImplsToBeRemoved.size()));
			if (answer == true) {
				
				for (AnnotationIntervalImpl annotationInterval : annotationImplsToBeRemoved) {
					List<Annotation> annotationToBeRemoved = new ArrayList<Annotation>();
					annotationToBeRemoved.add(annotationInterval.getAnnotation());
					try {
						annotationInterval.getOwner().getAnnotationSetNode().removeAnnotations(annotationToBeRemoved, annotationInterval.getOwner().getSubject());
					} catch (Exception e) {
						MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", String.format("Unable to delete %s.", annotationInterval.getAnnotation().getText()));
					}
				}
			}
			
		}
		return null;
	}
}
