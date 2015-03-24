package synergyviewcore.collections.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import synergyviewcore.model.PersistenceModelObject;


/**
 * The Class Collection.
 */
@Entity
public class Collection extends PersistenceModelObject {

	/** The file name. */
	private String fileName;
	
	/** The Constant PROP_FILENAME. */
	public static final String PROP_FILENAME = "fileName";
	
	/** The name. */
	private String name;
	
	/** The Constant PROP_NAME. */
	public static final String PROP_NAME = "name";

	/** The details. */
	private String details;
	
	/** The Constant PROP_DETAILS. */
	public static final String PROP_DETAILS = "details";
	
	/** The collection media clip list. */
	@OneToMany(mappedBy="collection", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private List<CollectionMediaClip> collectionMediaClipList = new ArrayList<CollectionMediaClip>();
	
	/** The Constant PROP_COLLECTION_MEDIA_CLIP_LIST. */
	public static final String PROP_COLLECTION_MEDIA_CLIP_LIST = "collectionMediaClipList";

	
	/** The collection media list. */
	@OneToMany(mappedBy="collection", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private List<CollectionMedia> collectionMediaList = new ArrayList<CollectionMedia>();
	
	/** The Constant PROP_COLLECTION_MEDIA_LIST. */
	public static final String PROP_COLLECTION_MEDIA_LIST = "collectionMediaList";

	
	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}
		
	/**
	 * Sets the file name.
	 *
	 * @param fileName the new file name
	 */
	public void setFileName(String fileName) {
		this.firePropertyChange(PROP_FILENAME, this.fileName, this.fileName = fileName);
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.firePropertyChange(PROP_NAME, this.name, this.name = name);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the details.
	 *
	 * @param details the new details
	 */
	public void setDetails(String details) {
		this.firePropertyChange(PROP_DETAILS, this.details, this.details = details);
	}

	/**
	 * Gets the details.
	 *
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}
	
	/**
	 * Sets the collection media list.
	 *
	 * @param collectionMediaList the new collection media list
	 */
	public void setCollectionMediaList(List<CollectionMedia> collectionMediaList) {
		this.firePropertyChange(PROP_COLLECTION_MEDIA_LIST, null, Collections.unmodifiableCollection(this.collectionMediaList = collectionMediaList));
	}

	/**
	 * Gets the collection media list.
	 *
	 * @return the collection media list
	 */
	public List<CollectionMedia> getCollectionMediaList() {
		return collectionMediaList;
	}
	

	/**
	 * Sets the collection media clip list.
	 *
	 * @param collectionMediaClip the new collection media clip list
	 */
	public void setCollectionMediaClipList(List<CollectionMediaClip> collectionMediaClip) {
		this.firePropertyChange(PROP_COLLECTION_MEDIA_LIST, null, Collections.unmodifiableCollection(this.collectionMediaClipList = collectionMediaClip));
	}

	/**
	 * Gets the collection media clip list.
	 *
	 * @return the collection media clip list
	 */
	public List<CollectionMediaClip> getCollectionMediaClipList() {
		return collectionMediaClipList;
	}

}