package synergyviewmedia.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MediaInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<MediaTrack> addtionalTracks = new ArrayList<MediaTrack>();
	private VideoMediaTrack videoTrack;
	private AudioMediaTrack audioTrack;
	private String containerFormat;
	private long lengthInMilliSeconds;

	public void setLengthInMilliSeconds(long lengthInMilliSeconds) {
		this.lengthInMilliSeconds = lengthInMilliSeconds;
	}

	public long getLengthInMilliSeconds() {
		return lengthInMilliSeconds;
	}	
	
	public void setTracks(List<MediaTrack> tracks) {
		this.addtionalTracks = tracks;
	}
	public List<MediaTrack> getTracks() {
		return addtionalTracks;
	}
	public void setContainerFormat(String containerFormat) {
		this.containerFormat = containerFormat;
	}
	public String getContainerFormat() {
		return containerFormat;
	}
	public void setAudioTrack(AudioMediaTrack audioTrack) {
		this.audioTrack = audioTrack;
	}
	public AudioMediaTrack getAudioTrack() {
		return audioTrack;
	}
	public void setVideoTrack(VideoMediaTrack videoTrack) {
		this.videoTrack = videoTrack;
	}
	public VideoMediaTrack getVideoTrack() {
		return videoTrack;
	}
}
