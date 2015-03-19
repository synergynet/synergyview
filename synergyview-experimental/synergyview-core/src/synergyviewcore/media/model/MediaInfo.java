package synergyviewcore.media.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.gstreamer.ClockTime;

import uk.ac.durham.tel.commons.persistence.IdBasedObject;

@Entity
public class MediaInfo extends IdBasedObject {
	
	private List<MediaTrack> addtionalTracks = new ArrayList<MediaTrack>();
	private AudioMediaTrack audioTrack;
	private VideoMediaTrack videoTrack;
	private String containerFormat;
	private long lengthInMilliSeconds;
	
	@Id
	@Override
	public String getId() {
		return super.getId();
	}

	public void setLengthInMilliSeconds(long lengthInMilliSeconds) {
		this.lengthInMilliSeconds = lengthInMilliSeconds;
	}

	public long getLengthInMilliSeconds() {
		return lengthInMilliSeconds;
	}
		
	public String getFormattedLength() {
		ClockTime clockTime = ClockTime.fromMillis(lengthInMilliSeconds);
		return String.format("%02d:%02d:%02d", clockTime.getHours(), clockTime.getMinutes(), clockTime.getSeconds());
	}
	
	public void setAdditionalTracks(List<MediaTrack> addtionalTracks) {
		this.addtionalTracks = addtionalTracks;
	}
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="mediaInfo")
	public List<MediaTrack> getAdditionalTracks() {
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
	
	@OneToOne(cascade=CascadeType.ALL, mappedBy="mediaInfo")
	public AudioMediaTrack getAudioTrack() {
		return audioTrack;
	}
	
	public void setVideoTrack(VideoMediaTrack videoTrack) {
		this.videoTrack = videoTrack;
	}
	
	@OneToOne(cascade=CascadeType.ALL, mappedBy="mediaInfo")
	public VideoMediaTrack getVideoTrack() {
		return videoTrack;
	}

}
