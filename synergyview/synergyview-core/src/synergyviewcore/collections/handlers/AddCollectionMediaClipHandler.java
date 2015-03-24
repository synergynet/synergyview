package synergyviewcore.collections.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.collections.ui.editors.CollectionEditor;

/**
 * The Class AddCollectionMediaClipHandler.
 */
public class AddCollectionMediaClipHandler extends AbstractHandler implements
		IHandler {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart window = HandlerUtil.getActiveEditor(event);
		if (window instanceof CollectionEditor) {
			if (((CollectionEditor) window).isMediaAdded()) {
				((CollectionEditor) window).addCollectionMediaClip();
			} else {
				MessageDialog.openWarning(window.getSite().getShell(),
						"No Media found!",
						"Please add the collection media first.");
			}
		}
		return null;
	}
	
}
