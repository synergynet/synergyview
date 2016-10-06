package synergyviewcore.media.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;

import synergyviewcore.media.MediaException;
import synergyviewcore.media.ui.model.MediaRootNode;

public class MediaFileTreeDropAdapter extends ViewerDropAdapter {

	public MediaFileTreeDropAdapter(TreeViewer mediaInfoTreeViewer) {
		super(mediaInfoTreeViewer);
	}

	@Override
	public boolean performDrop(Object data) {
		Object target = this.getCurrentTarget();
		if (target==null) {
			target = this.getViewer().getInput();
		}
		if (!(target instanceof MediaRootNode))
			return false;
		MediaRootNode mediaRootNode = (MediaRootNode) target;
		String[] sourceFileNames = (String[]) data;
		if (!validate(sourceFileNames))
			return false;
		try {
			mediaRootNode.getMediaController().importMediaResourceFiles(sourceFileNames);
			return true;
		} catch (MediaException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to import media item.", e.getMessage());
			return false;
		}
	}
	
	@Override
	public void dragOver(DropTargetEvent event) {
		event.detail = DND.DROP_COPY;
		super.dragOver(event);
	}

	private boolean validate(String[] sourceFileNames) {
		//TODO Fix this!
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return FileTransfer.getInstance().isSupportedType(transferType);
	}

}
