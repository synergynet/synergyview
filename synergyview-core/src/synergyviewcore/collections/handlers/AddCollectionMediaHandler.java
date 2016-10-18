package synergyviewcore.collections.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.collections.ui.editors.CollectionEditor;

/**
 * The Class AddCollectionMediaHandler.
 */
public class AddCollectionMediaHandler extends AbstractHandler implements IHandler {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands .ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
	IEditorPart window = HandlerUtil.getActiveEditor(event);

	if (window instanceof CollectionEditor) {
	    ((CollectionEditor) window).addCollectionMedia();
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
     */
    @Override
    public boolean isEnabled() {
	return true;
    }

}
