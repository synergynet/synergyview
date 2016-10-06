package synergyviewcore.collection.model.listeners;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;

import synergyviewcore.collection.model.MediaCollection;
import synergyviewcore.project.OpenedProjectController;

public class MediaCollectionListener {
	@PostPersist
	public void inserted(MediaCollection mediaCollection) {
		if (!OpenedProjectController.getInstance().isMediaCollectionControllerAvailable())
			return;
		OpenedProjectController.getInstance().getMediaCollectionController().notifyListChangeListeners(mediaCollection, true);
	}
	
	@PostRemove
	public void removed(MediaCollection mediaCollection) {
		if (!OpenedProjectController.getInstance().isMediaCollectionControllerAvailable())
			return;
		OpenedProjectController.getInstance().getMediaCollectionController().notifyListChangeListeners(mediaCollection, false);
	}
}
