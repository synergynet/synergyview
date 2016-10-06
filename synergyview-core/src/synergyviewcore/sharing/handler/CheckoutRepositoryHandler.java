package synergyviewcore.sharing.handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import synergyviewcore.navigation.NavigatorLabelProvider;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.sharing.SharingController;
import synergyviewcore.sharing.SharingException;
import synergyviewcore.sharing.model.SharingInfo;
import uk.ac.durham.tel.commons.jface.node.AbstractBaseNode;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.INode;

public class CheckoutRepositoryHandler extends AbstractHandler implements
		IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<SharingInfo> remoteSharingInfoList;
		try {
			remoteSharingInfoList = SharingController.getInstance().browseRemoteStudies();
			List<SharingInfo> selectedRemoteSharingInfoList = selectRemoteSharingProjects(remoteSharingInfoList);
			if (selectedRemoteSharingInfoList.isEmpty())
				return null;
			SharingController.getInstance().downloadStudies(remoteSharingInfoList);
		} catch (SharingException e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", e.getMessage());
		}
		return null;
	}
	
	private List<SharingInfo> selectRemoteSharingProjects(final List<SharingInfo> remoteSharingInfoList) {
		List<SharingInfo> selectedRemoteSharingInfoList = new ArrayList<SharingInfo>();
		List<RemoteSharingInfoNode> remoteSharingInfoNodeList = new ArrayList<RemoteSharingInfoNode>();
		for (SharingInfo remoteSharingInfo : remoteSharingInfoList) {
			remoteSharingInfoNodeList.add(new RemoteSharingInfoNode(remoteSharingInfo));
		}
		ElementListSelectionDialog dialog = new  ElementListSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new NavigatorLabelProvider());
		dialog.setTitle("Select remote Studies to download");
		dialog.setMultipleSelection(true);
		dialog.setElements(remoteSharingInfoNodeList.toArray(new INode[]{}));
		dialog.open();
		if (dialog.getResult()!=null) {
			for (Object result : dialog.getResult()) {
				RemoteSharingInfoNode node = (RemoteSharingInfoNode) result;
				selectedRemoteSharingInfoList.add(node.getResource());
			}
		}
		return selectedRemoteSharingInfoList;
	}
	
	private static class RemoteSharingInfoNode extends AbstractBaseNode<SharingInfo> {
		public static final String PROJECT_REMOTE_ICON = "study-remote.png";
		
		@Override
		public String getLabel() {
			SimpleDateFormat format = new SimpleDateFormat("(dd.MM.yyyy 'at' HH:mm:ss z)");
			return String.format("%s by %s %s",resource.getProjectName(), resource.getOwnerName(), format.format(resource.getCommitDate())) ;
		}
		
		public RemoteSharingInfoNode(SharingInfo remoteSharingInfo) {
			super(remoteSharingInfo, null);
		}
		
		@Override
		public void dispose() throws DisposeException {}

		@Override
		public ImageDescriptor getIcon() {
			return ResourceLoader.getIconDescriptor(PROJECT_REMOTE_ICON);
		}
	}

}
