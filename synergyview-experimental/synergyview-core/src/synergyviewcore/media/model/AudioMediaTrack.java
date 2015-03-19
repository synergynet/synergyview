package synergyviewcore.media.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class AudioMediaTrack extends MediaTrack {
	
	private String audioCodec;

	public void setAudioCodec(String audioCodec) {
		this.audioCodec = audioCodec;
	}

	public String getAudioCodec() {
		return audioCodec;
	}
	
	@OneToOne
	@Override
	public MediaInfo getMediaInfo() {
		return super.getMediaInfo();
	}
}
