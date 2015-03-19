package synergyviewmvc.collections.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewmvc.collections.ui.editors.CollectionEditor;

public class AddCollectionMediaClipHandler extends AbstractHandler implements
		IHandler {

	@Override
	public boolean isEnabled() {
		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part instanceof CollectionEditor) {
			return ((CollectionEditor) part).isMediaAdded();
		}
		return false;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart window = HandlerUtil.getActiveEditor(event);
		if (window instanceof CollectionEditor) {
			((CollectionEditor) window).addCollectionMediaClip();
		}
		return null;
	}

}
