package synergyviewmedia.model;

public class AudioMediaTrack extends MediaTrack {
	private static final long serialVersionUID = 1L;
	
	private String audioCodec;

	public void setAudioCodec(String audioCodec) {
		this.audioCodec = audioCodec;
	}

	public String getAudioCodec() {
		return audioCodec;
	}
}
