package synergyviewcore.collection;

import java.util.ArrayList;
import java.util.List;

import synergyviewcore.collection.model.MediaCollectionEntry;
import synergyviewcore.controller.AbstractController;
import synergyviewcore.controller.ModelPersistenceException;
import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.project.OpenedProjectController;

public class MediaCollectionEntryController extends AbstractController<MediaCollectionEntry> {
//	private static Logger logger = Logger.getLogger(MediaCollectionEntryController.class);


	public MediaCollectionEntryController() {
		super(OpenedProjectController.getInstance().getEntityManagerFactory(), MediaCollectionEntry.class);
	}

	public MediaCollectionEntry findMediaCollectionEntryById(String id) throws ObjectNotfoundException {
		return this.find(id);
	}

	public void updateMediaCollectionEntry(MediaCollectionEntry mediaCollectionEntry) throws ModelPersistenceException {
		this.update(mediaCollectionEntry);
	}

	public void notifyListChangeListeners(
			MediaCollectionEntry mediaCollectionEntry, boolean isAddition) {
		List<MediaCollectionEntry> changedMediaList = new ArrayList<MediaCollectionEntry>();
		changedMediaList.add(mediaCollectionEntry);
		this.notifyCollectionChangeListeners(changedMediaList, isAddition);
	}

}
