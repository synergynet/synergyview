package synergyviewcore.collections.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.timebar.model.MediaIntervalImpl;
import synergyviewcore.timebar.model.MediaSegmentIntervalImpl;

/**
 * The Class RemoveCollectionMediaHandler.
 */
public class RemoveCollectionMediaHandler extends AbstractHandler implements IHandler {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands .ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
	ISelection selection = HandlerUtil.getCurrentSelection(event);
	if (!(selection instanceof IStructuredSelection)) {
	    return null;
	}
	IStructuredSelection structSel = (IStructuredSelection) selection;
	Object element = structSel.iterator().next();

	if (element instanceof MediaIntervalImpl) {
	    MediaIntervalImpl mediaIntervalImpl = (MediaIntervalImpl) element;
	    mediaIntervalImpl.getOwner().remInterval(mediaIntervalImpl);
	    return null;
	} else if (element instanceof MediaSegmentIntervalImpl) {
	    MediaSegmentIntervalImpl mediaIntervalImpl = (MediaSegmentIntervalImpl) element;
	    mediaIntervalImpl.dispose();
	    mediaIntervalImpl.getOwner().remInterval(mediaIntervalImpl);
	    return null;
	} else {
	    return null;
	}
    }

}
