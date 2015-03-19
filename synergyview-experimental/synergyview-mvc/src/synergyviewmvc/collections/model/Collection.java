package synergyviewmvc.collections.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import synergyviewmvc.model.PersistenceModelObject;

@Entity
public class Collection extends PersistenceModelObject {

	private String fileName;
	public static final String PROP_FILENAME = "fileName";
	
	private String name;
	public static final String PROP_NAME = "name";

	private String details;
	public static final String PROP_DETAILS = "details";
	
	@OneToMany(mappedBy="collection", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private List<CollectionMediaClip> collectionMediaClipList = new ArrayList<CollectionMediaClip>();
	public static final String PROP_COLLECTION_MEDIA_CLIP_LIST = "collectionMediaClipList";

	
	@OneToMany(mappedBy="collection", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private List<CollectionMedia> collectionMediaList = new ArrayList<CollectionMedia>();
	public static final String PROP_COLLECTION_MEDIA_LIST = "collectionMediaList";

	
	public String getFileName() {
		return fileName;
	}
		
	public void setFileName(String fileName) {
		this.firePropertyChange(PROP_FILENAME, this.fileName, this.fileName = fileName);
	}
	
	public void setName(String name) {
		this.firePropertyChange(PROP_NAME, this.name, this.name = name);
	}

	public String getName() {
		return name;
	}

	public void setDetails(String details) {
		this.firePropertyChange(PROP_DETAILS, this.details, this.details = details);
	}

	public String getDetails() {
		return details;
	}
	
	public void setCollectionMediaList(List<CollectionMedia> collectionMediaList) {
		this.firePropertyChange(PROP_COLLECTION_MEDIA_LIST, null, Collections.unmodifiableCollection(this.collectionMediaList = collectionMediaList));
	}

	public List<CollectionMedia> getCollectionMediaList() {
		return collectionMediaList;
	}
	

	public void setCollectionMediaClipList(List<CollectionMediaClip> collectionMediaClip) {
		this.firePropertyChange(PROP_COLLECTION_MEDIA_LIST, null, Collections.unmodifiableCollection(this.collectionMediaClipList = collectionMediaClip));
	}

	public List<CollectionMediaClip> getCollectionMediaClipList() {
		return collectionMediaClipList;
	}

}