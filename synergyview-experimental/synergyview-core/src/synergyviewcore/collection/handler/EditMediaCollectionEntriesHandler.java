package synergyviewcore.collection.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.collection.ui.dialog.MediaCollectionEntriesEditorDialog;
import synergyviewcore.collection.ui.model.MediaCollectionNode;

public class EditMediaCollectionEntriesHandler extends AbstractHandler
		implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		Object element = structSel.getFirstElement(); 
		if (element instanceof MediaCollectionNode) {
			MediaCollectionEntriesEditorDialog dlg = new MediaCollectionEntriesEditorDialog(Display.getDefault().getActiveShell(), ((MediaCollectionNode) element).getResource());
	        dlg.open();
		}
		return null;
	}

}
