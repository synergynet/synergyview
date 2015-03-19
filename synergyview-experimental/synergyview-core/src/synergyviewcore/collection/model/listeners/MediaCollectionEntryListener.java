package synergyviewcore.collection.model.listeners;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;

import synergyviewcore.collection.model.MediaCollectionEntry;
import synergyviewcore.project.OpenedProjectController;

public class MediaCollectionEntryListener {
	@PostPersist
	public void inserted(MediaCollectionEntry mediaCollectionEntry) {
		if (!OpenedProjectController.getInstance().isMediaCollectionControllerAvailable())
			return;
		OpenedProjectController.getInstance().getMediaCollectionController().getMediaCollectionEntryController().notifyListChangeListeners(mediaCollectionEntry, true);
	}
	
	@PostRemove
	public void removed(MediaCollectionEntry mediaCollectionEntry) {
		if (!OpenedProjectController.getInstance().isMediaCollectionControllerAvailable())
			return;
		OpenedProjectController.getInstance().getMediaCollectionController().getMediaCollectionEntryController().notifyListChangeListeners(mediaCollectionEntry, false);
	}
}
