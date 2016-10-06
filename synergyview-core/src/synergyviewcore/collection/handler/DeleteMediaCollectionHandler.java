package synergyviewcore.collection.handler;

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

import synergyviewcore.collection.model.MediaCollection;
import synergyviewcore.collection.ui.model.MediaCollectionNode;
import synergyviewcore.controller.ModelPersistenceException;
import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.project.OpenedProjectController;

public class DeleteMediaCollectionHandler extends AbstractHandler implements
		IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		List<MediaCollection> mediaDataItemListToRemove = new ArrayList<MediaCollection>();
		for (Iterator<?> i = structSel.iterator(); i.hasNext();) {
			Object element = i.next();
			if (element instanceof MediaCollectionNode) {
				try {
					MediaCollection media = OpenedProjectController.getInstance().getMediaCollectionController().findMediaCollectionById(((MediaCollectionNode) element).getResource());
					mediaDataItemListToRemove.add(media);
				} catch (ObjectNotfoundException e) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", e.getMessage());
				}
			}
		}
		try {
			OpenedProjectController.getInstance().getMediaCollectionController().deleteMediaCollection(mediaDataItemListToRemove);
		} catch (ModelPersistenceException e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", e.getMessage());
		}
		return null;
	}

}
