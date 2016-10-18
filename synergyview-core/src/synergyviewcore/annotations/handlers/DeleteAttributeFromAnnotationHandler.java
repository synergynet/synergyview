package synergyviewcore.annotations.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.annotations.ui.views.AnnotationPropertyViewPart;

/**
 * The Class DeleteAttributeFromAnnotationHandler.
 */
public class DeleteAttributeFromAnnotationHandler extends AbstractHandler implements IHandler {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands .ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
	IWorkbenchPart activePart = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getActivePart();
	if (activePart instanceof AnnotationPropertyViewPart) {
	    AnnotationPropertyViewPart part = (AnnotationPropertyViewPart) activePart;
	    part.removeSelectedAttributes();
	}
	return null;
    }

}
