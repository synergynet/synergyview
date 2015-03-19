package synergyviewcore.collection.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import synergyviewcore.collection.model.listeners.MediaCollectionEntryListener;
import synergyviewcore.media.model.PlayableMedia;
import uk.ac.durham.tel.commons.persistence.IdBasedObject;

@Entity
@EntityListeners({MediaCollectionEntryListener.class})
public class MediaCollectionEntry extends IdBasedObject {
	
	public static final String PROP_OFFSET = "offset";
	private long offset;
	
	private PlayableMedia mediaItem;
	private boolean isMuted;
	private MediaCollection mediaCollection;
	
	@Id
	public String getId() {
		return super.getId();
	}

	public void setOffset(long offset) {
		this.firePropertyChange(PROP_OFFSET, this.offset, this.offset = offset);
	}

	public long getOffset() {
		return offset;
	}


	public void setMediaItem(PlayableMedia mediaItem) {
		this.mediaItem = mediaItem;
	}

	@OneToOne(fetch=FetchType.LAZY)
	public PlayableMedia getMediaItem() {
		return mediaItem;
	}

	public void setMuted(boolean isMuted) {
		this.isMuted = isMuted;
	}

	public boolean isMuted() {
		return isMuted;
	}

	public void setMediaCollection(MediaCollection mediaCollection) {
		this.mediaCollection = mediaCollection;
	}

	public MediaCollection getMediaCollection() {
		return mediaCollection;
	}
}
