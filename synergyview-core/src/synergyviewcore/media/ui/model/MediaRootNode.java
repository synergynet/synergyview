package synergyviewcore.media.ui.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import synergyviewcore.media.MediaController;
import synergyviewcore.project.OpenedProjectController;
import uk.ac.durham.tel.commons.collections.CollectionChangeEvent;
import uk.ac.durham.tel.commons.collections.CollectionChangeListener;
import uk.ac.durham.tel.commons.collections.CollectionDiffEntry;
import uk.ac.durham.tel.commons.jface.node.AbstractBaseParent;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.INode;
import uk.ac.durham.tel.commons.jface.node.NodeRemoveException;

public class MediaRootNode extends AbstractBaseParent<MediaController> {
	private Map<String, MediaNode> childrenNodesMap = new HashMap<String, MediaNode>(); 
	private static Logger logger = Logger.getLogger(MediaRootNode.class);
	private CollectionChangeListener mediaListChangeListener = new CollectionChangeListener() {
		@Override
		public void listChanged(final CollectionChangeEvent event) {
			Display.getCurrent().asyncExec(new Runnable() {

				@Override
				public void run() {
					for (CollectionDiffEntry<?> diffEntry : event.getListDiff().getDifferences()) {
						if (diffEntry.isAddition())
							addMediaNode((String) diffEntry.getElement());
						else removeMediaNode((String) diffEntry.getElement());
					}
				}
				
			});
			
		}

	};
	
	
	public MediaRootNode(MediaController resource) {
		super(resource, null);
		addMediaInfoListChangeListener();
		loadMediaInfoList();
	}
	

	private void removeMediaNode(String mediaId) {
		try {
			this.deleteChildren(new INode[] {childrenNodesMap.get(mediaId)});
		} catch (NodeRemoveException e) {
			
			logger.error("Unable to remove Media node.", e);
		}
	}

	private void addMediaNode(String mediaId) {
		MediaNode mediaNode = new MediaNode(mediaId, this);
		childrenNodesMap.put(mediaId, mediaNode);
		addChildren(mediaNode);
	}

	private void addMediaInfoListChangeListener() {
		OpenedProjectController.getInstance().getMediaController().addChangeListener(mediaListChangeListener);
	}
	
	private void removeMediaInfoListChangeListener() {
		OpenedProjectController.getInstance().getMediaController().removeChangeListener(mediaListChangeListener);
	}

	private void loadMediaInfoList() {
		try {
			for (String mediaId : resource.getMediaIdList()) {
				addMediaNode(mediaId);
			}
		} catch (Exception e) {
			logger.error("Unable to load initial media information", e);
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", "Unable to load initial media information");
		}		
	}
	
	@Override
	public String getLabel() {
		return "Root Media Node";
	}

	@Override
	public List<String> getChildrenNames() {
		return null;
	}

	@Override
	public void dispose() throws DisposeException {
		removeMediaInfoListChangeListener();
		try {
			clearChildren();
		} catch (NodeRemoveException e) {
			logger.error("Unable to dispose children.", e);
		}
	}

	@Override
	public ImageDescriptor getIcon() {
		return null;
	}


	public MediaController getMediaController() {
		return resource;
	}

}
