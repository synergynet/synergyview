package synergyviewcore.collection.ui.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcore.collection.model.MediaCollection;
import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.project.OpenedProjectController;
import synergyviewcore.resource.ResourceLoader;
import uk.ac.durham.tel.commons.jface.node.AbstractBaseParent;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.IParentNode;

public class MediaCollectionNode extends AbstractBaseParent<String> implements PropertyChangeListener {
	private static Logger logger = Logger.getLogger(MediaCollectionNode.class);
	
	public MediaCollectionNode(String resourceValue,
			IParentNode parentValue) throws ObjectNotfoundException {
		super(resourceValue, parentValue);
		MediaCollection collection = OpenedProjectController.getInstance().getMediaCollectionController().findMediaCollectionById(resourceValue);
		this.setLabel(collection.getName());
		addMediaCollectionChangeListener();
	}
	
	private void removeMediaCollectionChangeListener() {
		OpenedProjectController.getInstance().getMediaCollectionController().removePropertyChangeListener(resource, this);
	}
	
	private void addMediaCollectionChangeListener() {
		OpenedProjectController.getInstance().getMediaCollectionController().addPropertyChangeListener(resource, this);
	}

	@Override
	public List<String> getChildrenNames() {
		return null;
	}

	@Override
	public void dispose() throws DisposeException {
		removeMediaCollectionChangeListener();
	}

	@Override
	public ImageDescriptor getIcon() {
		return ResourceLoader.getDefaultIconDescriptor();
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		logger.debug("Property changed " + arg0.getSource() + " and the value is " + arg0.getNewValue());
		this.setLabel(((MediaCollection) arg0.getSource()).getName());
	}

}
