package synergyviewcore.annotations.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.annotations.ui.views.AnnotationTableViewPart;


/**
 * The Class PinAnnotationTableViewHandler.
 */
public class PinAnnotationTableViewHandler extends AbstractHandler implements
		IHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Object obj = event.getTrigger();
		Event eobj = (Event) obj;
		if (eobj.widget instanceof ToolItem) {
			ToolItem b = (ToolItem) eobj.widget;

			if (window.getPartService().getActivePart() instanceof AnnotationTableViewPart) {
				AnnotationTableViewPart cPart = (AnnotationTableViewPart) window
						.getPartService().getActivePart();
				cPart.setSticky(b.getSelection());
			}
		}
		return null;
	}

}
