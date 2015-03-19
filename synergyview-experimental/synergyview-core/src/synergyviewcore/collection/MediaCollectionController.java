package synergyviewcore.collection;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import synergyviewcore.collection.model.MediaCollection;
import synergyviewcore.collection.model.MediaCollectionEntry;
import synergyviewcore.controller.AbstractController;
import synergyviewcore.controller.ModelPersistenceException;
import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.media.model.PlayableMedia;
import synergyviewcore.project.OpenedProjectController;

public class MediaCollectionController extends AbstractController<MediaCollection> {
	private static Logger logger = Logger.getLogger(MediaCollectionController.class);
	private MediaCollectionEntryController mediaCollectionEntryController = new MediaCollectionEntryController();
	
	public MediaCollectionController() {
		super(OpenedProjectController.getInstance().getEntityManagerFactory(), MediaCollection.class);
	}
	
	public void dispose() {
		//
	}


	public MediaCollectionEntryController getMediaCollectionEntryController() {
		return mediaCollectionEntryController;
	}

	public List<String> getMediaCollectionNamesList() {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			TypedQuery<String> query = entityManager.createNamedQuery("getAllMediaCollectionNames", String.class);
			return query.getResultList();
		} finally {
			if (entityManager!=null && entityManager.isOpen())
				entityManager.close();
		}
	}

	public List<MediaCollection> getMediaCollectionList() {
		return this.getAll("getAllMediaCollection", MediaCollection.class);
	}

	public MediaCollection findMediaCollectionById(String mediaCollectionId) throws ObjectNotfoundException {
		return this.find(mediaCollectionId);
	}

	public void createMediaCollection(MediaCollection mediaCollection) throws ModelPersistenceException {
		this.create(mediaCollection);
	}

	public void updateMediaCollection(MediaCollection mediaCollection) throws ModelPersistenceException {
		this.update(mediaCollection);
	}

	public void deleteMediaCollection(
			List<MediaCollection> mediaDataItemListToRemove) throws ModelPersistenceException {
		this.delete(mediaDataItemListToRemove);
		
	}

	public void removeMediaCollectionEntryById(String id) throws ModelPersistenceException {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			MediaCollectionEntry modelObject = entityManager.find(MediaCollectionEntry.class, id);
			entityManager.remove(modelObject);
			MediaCollection collection = entityManager.find(MediaCollection.class, modelObject.getMediaCollection().getId());
			collection.getMediaCollectionEntryList().remove(modelObject);
			entityManager.merge(collection);
			entityManager.getTransaction().commit();
			logger.debug("Model objects deleted.");
		} catch (PersistenceException e) {
			logger.error("Unable to delete object.", e);
			throw new ModelPersistenceException("Unable to delete object.", e);
		} finally {
			if (entityManager!=null && entityManager.isOpen())
				entityManager.close();
		}
	}
	
	public void addMediaCollectionEntry(MediaCollectionEntry mediaCollectionEntry, String mediaCollectionId) throws ModelPersistenceException, ObjectNotfoundException {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(mediaCollectionEntry);
			MediaCollection parentMediaCollection = entityManager.find(MediaCollection.class, mediaCollectionId);
			if (parentMediaCollection == null)
				throw new ObjectNotfoundException("Unable to find Media Collection Id");
			parentMediaCollection.getMediaCollectionEntryList().add(mediaCollectionEntry);
			entityManager.merge(parentMediaCollection);
			entityManager.getTransaction().commit();
			logger.debug("Media Collection Entry added.");
		} catch (PersistenceException e) {
			logger.error("Unable to add  Media Collection Entry.", e);
			throw new ModelPersistenceException("Unable to add  Media Collection Entry.", e);
		} finally {
			if (entityManager!=null && entityManager.isOpen())
				entityManager.close();
		}
	}

	public void createMediaCollectionEntryFromMediaList(List<PlayableMedia> mediaList, String mediaCollectionId) throws ModelPersistenceException, ObjectNotfoundException {
		MediaCollection mediaCollection = OpenedProjectController.getInstance().getMediaCollectionController().findMediaCollectionById(mediaCollectionId);
		for (PlayableMedia playableMedia : mediaList) {
			MediaCollectionEntry mediaCollectionEntry = mediaCollectionEntryController.createNew();
			mediaCollectionEntry.setMediaItem(playableMedia);
			mediaCollectionEntry.setMediaCollection(mediaCollection);
			mediaCollectionEntry.setOffset(0L);
			addMediaCollectionEntry(mediaCollectionEntry, mediaCollection.getId());
		}
	}

	public void notifyListChangeListeners(MediaCollection media, boolean isAddition) {
		List<MediaCollection> changedMediaList = new ArrayList<MediaCollection>();
		changedMediaList.add(media);
		this.notifyCollectionChangeListeners(changedMediaList, isAddition);
	}
	
	
}
