package synergyviewcore.collection.ui.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import synergyviewcore.collection.MediaCollectionController;
import synergyviewcore.collection.model.MediaCollection;
import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.project.OpenedProjectController;
import uk.ac.durham.tel.commons.collections.CollectionChangeEvent;
import uk.ac.durham.tel.commons.collections.CollectionChangeListener;
import uk.ac.durham.tel.commons.collections.CollectionDiffEntry;
import uk.ac.durham.tel.commons.jface.node.AbstractBaseParent;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.INode;
import uk.ac.durham.tel.commons.jface.node.NodeRemoveException;

public class MediaCollectionRootNode extends AbstractBaseParent<MediaCollectionController> {
	private Map<String, MediaCollectionNode> childrenNodesMap = new HashMap<String, MediaCollectionNode>();
	private static Logger logger = Logger.getLogger(MediaCollectionRootNode.class);
	private CollectionChangeListener mediaCollectionListChangeListener = new CollectionChangeListener() {
		@Override
		public void listChanged(final CollectionChangeEvent event) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					for (CollectionDiffEntry<?> diffEntry : event.getListDiff().getDifferences()) {
						if (diffEntry.isAddition())
							try {
								addMediaCollectionNode((String) diffEntry.getElement());
							} catch (final ObjectNotfoundException e) {
								MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Unable to add media collection node. " + e.getMessage());								
							}
						else removeCollectionMediaNode((String) diffEntry.getElement());
					}
				}
			});
		}
	};

	public MediaCollectionRootNode(MediaCollectionController mediaClipController) {
		super(mediaClipController, null);
		addMediaCollectionChangeListener();
		loadMediaCollectionList();
	}

	protected void removeCollectionMediaNode(String id) {
		try {
			this.deleteChildren(new INode[] {childrenNodesMap.get(id)});
		} catch (NodeRemoveException e) {
			
			logger.error("Unable to remove Media node.", e);
		}
	}

	protected void addMediaCollectionNode(String id) throws ObjectNotfoundException {
		MediaCollectionNode mediaCollectionNode = new MediaCollectionNode(id, this);
		childrenNodesMap.put(id, mediaCollectionNode);
		addChildren(mediaCollectionNode);
	}

	@Override
	public List<String> getChildrenNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() throws DisposeException {
		removeMediaCollectionChangeListener();
	}

	private void removeMediaCollectionChangeListener() {
		OpenedProjectController.getInstance().getMediaCollectionController().removeChangeListener(mediaCollectionListChangeListener);
	}
	
	private void addMediaCollectionChangeListener() {
		OpenedProjectController.getInstance().getMediaCollectionController().addChangeListener(mediaCollectionListChangeListener);
	}

	@Override
	public ImageDescriptor getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void loadMediaCollectionList() {
		for (MediaCollection mediaCollection : resource.getMediaCollectionList()) {
			try {
				addMediaCollectionNode(mediaCollection.getId());
			} catch (ObjectNotfoundException e) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Unable to add media collection node. " + e.getMessage());	
			}
		}
	}

}
