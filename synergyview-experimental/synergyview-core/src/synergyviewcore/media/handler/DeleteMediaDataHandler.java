package synergyviewcore.media.handler;

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

import synergyviewcore.media.MediaException;
import synergyviewcore.media.model.Media;
import synergyviewcore.media.ui.model.MediaNode;
import synergyviewcore.project.OpenedProjectController;

public class DeleteMediaDataHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		List<Media> mediaDataItemListToRemove = new ArrayList<Media>();
		for (Iterator<?> i = structSel.iterator(); i.hasNext();) {
			Object element = i.next();
			if (element instanceof MediaNode) {
				try {
					Media media = OpenedProjectController.getInstance().getMediaController().find(((MediaNode) element).getResource());
					mediaDataItemListToRemove.add(media);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			OpenedProjectController.getInstance().getMediaController().deleteMedia(mediaDataItemListToRemove);
		} catch (MediaException e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", e.getMessage());
		}
		return null;
	}
}
