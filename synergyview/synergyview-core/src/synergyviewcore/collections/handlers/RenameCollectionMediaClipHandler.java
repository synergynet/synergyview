package synergyviewcore.collections.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.collections.model.CollectionMediaClip;
import synergyviewcore.collections.model.CollectionMediaClipNode;
import synergyviewcore.timebar.model.MediaSegmentIntervalImpl;


/**
 * The Class RenameCollectionMediaClipHandler.
 */
public class RenameCollectionMediaClipHandler extends AbstractHandler implements
		IHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		if (structSel.isEmpty())
			return null;
		Object element = structSel.iterator().next();
		if (element instanceof MediaSegmentIntervalImpl) {
			CollectionMediaClip clip = ((MediaSegmentIntervalImpl) element).getCollectionMediaClip();
			CollectionMediaClipNode clipNode = ((MediaSegmentIntervalImpl) element).getCollectionNode().findCollectionMediaClipNode(clip);
			IInputValidator validator = new IInputValidator() {
				public String isValid(String newText) {
					if(!newText.equalsIgnoreCase(""))
						return null;
					else
						return "Name empty!";
				}
			};
			InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Rename Media Clip", "Enter Media Clip Name:", clip.getClipName(), validator);
			if(dialog.open() == Window.OK) {
				try {
					clipNode.renameClip(dialog.getValue());
				} catch (Exception ex) {
					MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", String.format("Unable to rename %s.", clip.getClipName()));
				}
			}
		}
		
		return null;
	}

}
