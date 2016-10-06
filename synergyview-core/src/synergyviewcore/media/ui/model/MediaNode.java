package synergyviewcore.media.ui.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

import synergyviewcore.media.MediaController;
import synergyviewcore.media.model.Media;
import synergyviewcore.project.OpenedProjectController;
import synergyviewcore.resource.ResourceLoader;
import uk.ac.durham.tel.commons.jface.node.AbstractBaseNode;
import uk.ac.durham.tel.commons.jface.node.DisposeException;

public class MediaNode extends AbstractBaseNode<String> {
	private static Logger logger = Logger.getLogger(MediaNode.class);
	private MediaController mediaController;
	private PropertyChangeListener mediaResoureFileAvailableListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (resource.equals(evt.getSource())) //TODO Refactor
				updateIconText(evt.getNewValue());
		}
	};
	
	public MediaNode(String mediaId, MediaRootNode mediaRootNode) {
		super(mediaId, mediaRootNode);
		mediaController = OpenedProjectController.getInstance().getMediaController();
		addMediaResourceFileAvailableListener();
		try {
			Media media = OpenedProjectController.getInstance().getMediaController().find(resource);
			setLabel(media.getId());
			updateIconText(media.getMediaFileResource());
		} catch (Exception e) {
			logger.error("Unable to load media information", e);
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", "Unable to load media information");
		}
		
	}

	private void addMediaResourceFileAvailableListener() {
		mediaController.addPropertyChangeListener(resource, mediaResoureFileAvailableListener);
	}

	private void updateIconText(Object object) {
		if (object==null)
			this.setLabelDecorator("Unavailable");
		else this.setLabelDecorator("");
	}
	
	@Override
	public void dispose() throws DisposeException {
		removeMediaResourceFileAvailableListener();
	}

	private void removeMediaResourceFileAvailableListener() {
		mediaController.removePropertyChangeListener(resource, mediaResoureFileAvailableListener);
	}
	

	@Override
	public ImageDescriptor getIcon() {
		ImageDescriptor imageDescriptor  = null;
		try {
			String fileExtension = resource.substring(resource.lastIndexOf('.') + 1, resource.length()); 
			if (!fileExtension.isEmpty()) {
				imageDescriptor = ResourceLoader.getIconFromProgram(fileExtension);
			}
		} catch (Exception ex) {
			imageDescriptor = ResourceLoader.getDefaultIconDescriptor();
			logger.info("Unable to find file extension and set image descriptor.", ex);
		}
		return imageDescriptor;
	}
}
