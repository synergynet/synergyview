package synergyviewcore.collection.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import uk.ac.durham.tel.commons.persistence.IdBasedObject;
@Entity
public class Clip extends IdBasedObject {

	private String name;
	public static final String PROP_NAME = "name";

	private int startOffset;
	public static final String PROP_START_OFFSET = "startOffset";

	private int duration;
	public static final String PROP_DURATION = "duration";
	
	private MediaCollection mediaCollection;
	public static final String PROP_MEDIA_COLLECTION = "mediaCollection";

	public void setStartOffset(int startOffset) {
		this.firePropertyChange(PROP_START_OFFSET, this.startOffset, this.startOffset = startOffset);
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setDuration(int duration) {
		this.firePropertyChange(PROP_DURATION, this.duration, this.duration = duration);
	}

	public int getDuration() {
		return duration;
	}

	public void setName(String name) {
		this.firePropertyChange(PROP_NAME, this.name , this.name = name);
	}

	public String getName() {
		return name;
	}

	public void setMediaCollection(MediaCollection mediaCollection) {
		this.firePropertyChange(PROP_MEDIA_COLLECTION, this.mediaCollection , this.mediaCollection = mediaCollection);
	}

	@OneToOne
	public MediaCollection getMediaCollection() {
		return mediaCollection;
	}
}
