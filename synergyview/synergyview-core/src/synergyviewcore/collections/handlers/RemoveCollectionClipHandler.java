package synergyviewcore.collections.handlers;

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

import synergyviewcore.collections.model.CollectionMediaClip;
import synergyviewcore.collections.model.CollectionMediaClipNode;
import synergyviewcore.collections.model.CollectionNode;


/**
 * The Class RemoveCollectionClipHandler.
 */
public class RemoveCollectionClipHandler extends AbstractHandler implements
		IHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		for(Iterator<?> i = structSel.iterator(); i.hasNext();) {
			Object element = i.next();
			if (element instanceof CollectionMediaClipNode) {
				CollectionMediaClip clip = ((CollectionMediaClipNode) element).getResource();
				List<CollectionMediaClip> clipsToRemove = new ArrayList<CollectionMediaClip>();
				clipsToRemove.add(clip);
				try {
					((CollectionNode)((CollectionMediaClipNode) element).getParent()).removeClip(clipsToRemove);
				} catch (Exception ex) {
					MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", String.format("Unable to delete %s.", clip.getClipName()));
				}
			}
		}
		return null;
	}

}
