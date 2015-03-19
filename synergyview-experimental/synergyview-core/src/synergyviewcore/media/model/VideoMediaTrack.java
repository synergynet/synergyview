package synergyviewcore.media.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class VideoMediaTrack extends MediaTrack {

	private String videoCodec;
	

	public void setVideoCodec(String videoCodec) {
		this.videoCodec = videoCodec;
	}
	public String getVideoCodec() {
		return videoCodec;
	}
	
	@OneToOne
	@Override
	public MediaInfo getMediaInfo() {
		return super.getMediaInfo();
	}
}
