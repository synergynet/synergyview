package synergyviewmvc.projects;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.eclipse.swt.dnd.FileTransfer;

import synergyviewmvc.media.model.MediaRootNode;

public class ProjectsDropAdapterAssistant extends CommonDropAdapterAssistant {

	public ProjectsDropAdapterAssistant() {
		//
	}
	
	@Override
	public boolean isSupportedType(TransferData aTransferType) {
		//return super.isSupportedType(aTransferType) || FileTransfer.getInstance().isSupportedType(aTransferType) || TextTransfer.getInstance().isSupportedType(aTransferType);
		return true;
	}	

	@Override
	public IStatus handleDrop(CommonDropAdapter aDropAdapter,
			DropTargetEvent aDropTargetEvent, Object aTarget) {
		if (aDropAdapter.getCurrentTarget() == null
				|| aDropTargetEvent.data == null) {
			return Status.CANCEL_STATUS;
		}
		TransferData currentTransfer = aDropAdapter.getCurrentTransfer();
		
		return Status.OK_STATUS;

	}

	@Override
	public IStatus validateDrop(Object target, int operation,
			TransferData transferType) {
		if (!(target instanceof MediaRootNode)) {
			return Status.CANCEL_STATUS;
		}
		else return Status.OK_STATUS;
	}


}
