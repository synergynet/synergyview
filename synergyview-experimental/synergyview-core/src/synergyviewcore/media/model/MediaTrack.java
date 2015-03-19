package synergyviewcore.media.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

import uk.ac.durham.tel.commons.persistence.IdBasedObject;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class MediaTrack extends IdBasedObject {
	private MediaInfo mediaInfo;
	
	@Id
	@Override
	public String getId() {
		return super.getId();
	}

	public void setMediaInfo(MediaInfo mediaInfo) {
		this.mediaInfo = mediaInfo;
	}

	@ManyToOne
	public MediaInfo getMediaInfo() {
		return mediaInfo;
	}
	
	
}
