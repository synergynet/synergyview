package synergyviewcore.annotations.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.Activator;
import synergyviewcore.annotations.ui.AnnotationIntervalImpl;
import synergyviewcore.model.ModelPersistenceException;

/**
 * The Class EditAnnotationTextHandler.
 */
public class EditAnnotationTextHandler extends AbstractHandler implements
		IHandler {
	
	/** The logger. */
	private ILog logger;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		logger = Activator.getDefault().getLog();
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
				AnnotationIntervalImpl annotationInterval = (AnnotationIntervalImpl) element;
				IInputValidator validator = new IInputValidator() {
					public String isValid(String newText) {
						if (!newText.equalsIgnoreCase("")) {
							return null;
						} else {
							return "Name empty!";
						}
					}
				};
				InputDialog dialog = new InputDialog(PlatformUI.getWorkbench()
						.getDisplay().getActiveShell(), "Edit",
						"Enter caption:", annotationInterval.getLabel(),
						validator);
				if (dialog.open() == Window.OK) {
					annotationInterval.getAnnotation().setText(
							dialog.getValue());
					try {
						annotationInterval
								.getOwner()
								.getAnnotationSetNode()
								.updateAnnotation(
										annotationInterval.getAnnotation());
					} catch (ModelPersistenceException e) {
						IStatus status = new Status(IStatus.WARNING,
								Activator.PLUGIN_ID, e.getMessage(), e);
						logger.log(status);
						MessageDialog.openError(PlatformUI.getWorkbench()
								.getDisplay().getActiveShell(),
								"Error updating Annotation", e.getMessage());
					}
				}
				return null;
			}
		}
		
		return null;
		
	}
	
}
