package synergyviewcore.media.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@Entity
@NamedQuery(name="getAllPlayableMedia", query="select M from PlayableMedia M")
public class PlayableMedia extends Media implements Serializable {

	private static final long serialVersionUID = -4131030909768758728L;
	
	private MediaInfo mediaInfo;

	public void setMediaInfo(MediaInfo mediaInfo) {
		this.mediaInfo = mediaInfo;
	}

	@OneToOne(cascade=CascadeType.ALL)
	public MediaInfo getMediaInfo() {
		return mediaInfo;
	}
	
}
