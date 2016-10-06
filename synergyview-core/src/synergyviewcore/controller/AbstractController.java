package synergyviewcore.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import synergyviewcore.project.OpenedProjectController;
import uk.ac.durham.tel.commons.collections.CollectionChangeEvent;
import uk.ac.durham.tel.commons.collections.CollectionChangeListener;
import uk.ac.durham.tel.commons.collections.CollectionDiff;
import uk.ac.durham.tel.commons.collections.CollectionDiffEntry;
import uk.ac.durham.tel.commons.collections.CollectionDiffEntryImpl;
import uk.ac.durham.tel.commons.collections.CollectionDiffImpl;
import uk.ac.durham.tel.commons.collections.ICollectionObservable;
import uk.ac.durham.tel.commons.persistence.IdBasedObject;

public abstract class AbstractController<ModelObject extends IdBasedObject> implements ICollectionObservable {
	private List<CollectionChangeListener> collectionListeners = new CopyOnWriteArrayList<CollectionChangeListener>();
	private Map<String, List<PropertyChangeListener>> propertyListeners = new HashMap<String, List<PropertyChangeListener>>();
	protected EntityManagerFactory eManagerFactory;
	private static Logger logger = Logger.getLogger(AbstractController.class);
	private Class<ModelObject> modelObjectClass;
	
	public AbstractController(EntityManagerFactory eManagerFactory, Class<ModelObject> modelObjectClass) {
		this.eManagerFactory = eManagerFactory;
		this.modelObjectClass = modelObjectClass;
	}
	
	public void addPropertyChangeListener(String id,
			PropertyChangeListener listener) {
		List<PropertyChangeListener> propertyListener = propertyListeners.get(id);
		if (propertyListener == null) {
			propertyListener = new CopyOnWriteArrayList<PropertyChangeListener>();
			propertyListeners.put(id, propertyListener);
		} 
		logger.debug("Adding property listener.");
		propertyListener.add(listener);
	}

	public void removePropertyChangeListener(String id,
			PropertyChangeListener listener) {
		List<PropertyChangeListener> propertyListener = propertyListeners.get(id);
		if (propertyListener != null) {
			logger.debug("Removing property listener.");
			propertyListener.remove(listener);
		} 
	}
	
	@Override
	public void addChangeListener(CollectionChangeListener listener) {
		logger.debug("Adding change listener.");
		collectionListeners.add(listener);
	}

	@Override
	public void removeChangeListener(CollectionChangeListener listener) {
		logger.debug("Removing change listener.");
		collectionListeners.remove(listener);
	}
	
	protected void create(List<ModelObject> modelObjects) throws ModelPersistenceException {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			for (ModelObject modelObject : modelObjects)
				entityManager.persist(modelObject);
			entityManager.getTransaction().commit();
//			notifyCollectionChangeListeners(modelObjects, true);
			logger.debug("Model objects created.");
		} catch (PersistenceException e) {
			logger.error("Unable to create object.", e);
			throw new ModelPersistenceException("Unable to create objects.", e);
		} finally {
			if (entityManager!=null && entityManager.isOpen())
				entityManager.close();
		}
	}
	
	protected void create(ModelObject modelObject) throws ModelPersistenceException {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(modelObject);
			entityManager.getTransaction().commit();
//			List<ModelObject> changedModelObjects = new ArrayList<ModelObject>();
//			changedModelObjects.add(modelObject);
//			notifyCollectionChangeListeners(changedModelObjects, true);
			logger.debug("Model object created for " + modelObject.getClass().getName());
		} catch (PersistenceException e) {
			logger.error("Unable to create object.", e);
			throw new ModelPersistenceException("Unable to create object.", e);
		} finally {
			if (entityManager!=null && entityManager.isOpen())
				entityManager.close();
		}
	}
	
	protected ModelObject find(String id) throws ObjectNotfoundException {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			ModelObject object = entityManager.find(modelObjectClass, id);
			if (object == null)
				throw new ObjectNotfoundException("Unable to find Object with id: " + id);
			else return object;
		} finally {
			if (entityManager!=null && entityManager.isOpen())
				entityManager.close();
		}
	}
	
	protected <T extends ModelObject> List<T> getAll(String queryString, Class<T> className) {
		EntityManager entityManager = null;
		try {
			EntityManagerFactory eManagerFactory = OpenedProjectController.getInstance().getEntityManagerFactory();
			entityManager = eManagerFactory.createEntityManager();
			TypedQuery<T> mediaItemQuery = entityManager.createNamedQuery(queryString, className);
			return mediaItemQuery.getResultList();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
	
	protected void update(ModelObject modelObject) throws ModelPersistenceException {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			ModelObject currentModelObject = entityManager.find(modelObjectClass, modelObject.getId());
			List<PropertyChangeEvent> propertyChangeEvents = copyProperties(currentModelObject, modelObject);
			entityManager.merge(currentModelObject);
			entityManager.getTransaction().commit();
			notifyPropertyListeners(modelObject.getId(), propertyChangeEvents);
			logger.debug("Model object updated.");
		} catch (PersistenceException e) {
			logger.error("Unable to update object.", e);
			throw new ModelPersistenceException("Unable to update object.", e);
		} finally {
			if (entityManager!=null && entityManager.isOpen())
				entityManager.close();
		}
	}
	
	private List<PropertyChangeEvent> copyProperties (
			ModelObject currentModelObject, ModelObject modelObject) throws ModelPersistenceException {
		final List<PropertyChangeEvent> propertyChangeEvents = new ArrayList<PropertyChangeEvent>();
		try {
			currentModelObject.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(((IdBasedObject) arg0.getSource()).getId(),arg0.getPropertyName(), arg0.getOldValue(), arg0.getNewValue()); 
					propertyChangeEvents.add(propertyChangeEvent);
				}
			});
			BeanUtils.copyProperties(currentModelObject, modelObject);
		} catch (IllegalAccessException e) {
			logger.error("Unable to copy updated data.", e);
			throw new ModelPersistenceException("Unable to copy updated data.", e);
		} catch (InvocationTargetException e) {
			logger.error("Unable to copy updated data.", e);
			throw new ModelPersistenceException("Unable to copy updated data.", e);
		}
		return propertyChangeEvents;
	}


	protected void notifyPropertyListeners(String id, List<PropertyChangeEvent> propertyChangeEvents) {
		for (PropertyChangeEvent propertyChangeEvent : propertyChangeEvents){
			notifyPropertyListeners(id, propertyChangeEvent);
		}
	}
	
	protected void notifyPropertyListeners(String id, PropertyChangeEvent propertyChangeEvent) {
		List<PropertyChangeListener> listeners = propertyListeners.get(id);
		if (listeners == null) 
			return;
		logger.debug("notifying property listeners for " + id);
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(propertyChangeEvent);
		}
	}

	protected void delete(List<ModelObject> modelObjects) throws ModelPersistenceException {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			for (ModelObject modelObject : modelObjects) {
				modelObject = entityManager.merge(modelObject);
				entityManager.remove(modelObject);
			}
			entityManager.getTransaction().commit();
//			notifyCollectionChangeListeners(modelObjects, false);
			logger.debug("Model objects deleted.");
		} catch (PersistenceException e) {
			logger.error("Unable to delete object.", e);
			throw new ModelPersistenceException("Unable to delete object.", e);
		} finally {
			if (entityManager!=null && entityManager.isOpen())
				entityManager.close();
		}
	}
	
	protected void delete(String modelId)  throws ModelPersistenceException {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			ModelObject modelObject = entityManager.find(modelObjectClass, modelId);
			entityManager.remove(modelObject);
			entityManager.getTransaction().commit();
			List<ModelObject> removedItems = new ArrayList<ModelObject>();
			removedItems.add(modelObject);
//			notifyCollectionChangeListeners(removedItems, false);
			logger.debug("Model objects deleted.");
		} catch (PersistenceException e) {
			logger.error("Unable to delete object.", e);
			throw new ModelPersistenceException("Unable to delete object.", e);
		} finally {
			if (entityManager!=null && entityManager.isOpen())
				entityManager.close();
		}
	}
	
	protected void notifyCollectionChangeListeners(Collection<ModelObject> changedCollection, boolean isAddition) {
		CollectionDiffEntry<String>[] changedItems = createCollectionDiffEntryArray(changedCollection, isAddition);
		CollectionDiff<String> diff = new CollectionDiffImpl<String>(changedItems);
		CollectionChangeEvent event = new CollectionChangeEvent(this, diff);
		for (CollectionChangeListener listener : collectionListeners) {
			listener.listChanged(event);
		}
	}
	
	private CollectionDiffEntry<String>[] createCollectionDiffEntryArray(Collection<ModelObject> collection, boolean isAddition) {
		@SuppressWarnings("unchecked")
		CollectionDiffEntry<String>[] entries = (CollectionDiffEntry<String>[]) Array.newInstance(CollectionDiffEntry.class, collection.size());
		Iterator<ModelObject> iterator = collection.iterator();
		for(int i = 0 ; i < collection.size(); i++) {
			ModelObject item = iterator.next();
			entries[i] = new CollectionDiffEntryImpl<String>(item.getId(), isAddition, i);
		}
		return entries;
	}
	
	public ModelObject createNew() {
		return IdBasedObject.create(modelObjectClass);
	}
}
