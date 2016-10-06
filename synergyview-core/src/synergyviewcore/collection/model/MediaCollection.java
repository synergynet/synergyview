package synergyviewcore.collection.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;

import synergyviewcore.collection.model.listeners.MediaCollectionListener;
import synergyviewcore.media.model.PlayableMedia;
import uk.ac.durham.tel.commons.persistence.IdBasedObject;

@Entity
@EntityListeners({MediaCollectionListener.class})
@NamedQueries({
@NamedQuery(name="getAllMediaCollection", query="SELECT M FROM MediaCollection M"),
@NamedQuery(name="getAllMediaCollectionNames", query="SELECT M.name FROM MediaCollection M")
})
public class MediaCollection extends IdBasedObject {
	
	public static final String PROP_NAME = "name";
	private String name;

	public static final String PROP_MEDIA_COLLECTION_ENTRY_LIST = "mediaCollectionEntryList";
	private List<MediaCollectionEntry> mediaCollectionEntryList;
	
	public static final String PROP_MEDIAITEM = "mediaItem";
	private PlayableMedia mediaItem;
	
	public static final String PROP_ISMUATED = "isMuted";
	private boolean isMuted;
	
	public static final String PROP_DESCRIPTION = "description";
	private String description;
	
	@Id
	public String getId() {
		return super.getId();
	}

	public void setName(String name) {
		this.firePropertyChange(PROP_NAME, this.name , this.name = name);
	}

	@Column(nullable=false)
	public String getName() {
		return name;
	}

	public void setMediaCollectionEntryList(List<MediaCollectionEntry> mediaCollectionEntryList) {
		this.firePropertyChange(PROP_MEDIA_COLLECTION_ENTRY_LIST, mediaCollectionEntryList , this.mediaCollectionEntryList = mediaCollectionEntryList);
	}
	
	@Transient
	public long getDuration() {
		return mediaItem.getMediaInfo().getLengthInMilliSeconds();
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="mediaCollection")
	@OrderColumn(name="EntryOrder")
	public List<MediaCollectionEntry> getMediaCollectionEntryList() {
		return mediaCollectionEntryList;
	}

	public void setMediaItem(PlayableMedia mediaItem) {
		this.firePropertyChange(PROP_MEDIAITEM, this.mediaItem, this.mediaItem = mediaItem);
	}

	@OneToOne
	public PlayableMedia getMediaItem() {
		return mediaItem;
	}

	public void setMuted(boolean isMuted) {
		this.firePropertyChange(PROP_ISMUATED, this.isMuted, this.isMuted = isMuted);
	}

	public boolean isMuted() {
		return isMuted;
	}

	public void setDescription(String description) {
		this.firePropertyChange(PROP_DESCRIPTION, this.description, this.description = description);
	}

	public String getDescription() {
		return description;
	}

}
