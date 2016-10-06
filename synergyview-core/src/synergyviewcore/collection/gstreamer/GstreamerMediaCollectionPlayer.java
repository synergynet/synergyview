package synergyviewcore.collection.gstreamer;

import java.util.HashMap;
import java.util.Map;

import synergyviewcore.collection.model.MediaCollection;
import synergyviewcore.collection.model.MediaCollectionEntry;
import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.media.IMediaPlayer;
import synergyviewcore.media.MediaUnavailableException;
import synergyviewcore.media.model.PlayableMedia;
import synergyviewcore.media.ui.awt.GstreamerVideoContainer;
import synergyviewcore.media.ui.awt.VideoWindow;
import synergyviewcore.project.OpenedProjectController;

public class GstreamerMediaCollectionPlayer implements IMediaPlayer {
	private Map<MediaCollectionEntry, GstreamerMediaCollectionEntry> playBinMap = new HashMap<MediaCollectionEntry, GstreamerMediaCollectionEntry>();
	private MediaCollection mediaCollection;
	private GstreamerVideoContainer videoContainer;
	
	public GstreamerMediaCollectionPlayer(String mediaCollectionId, GstreamerVideoContainer videoContainer) throws MediaUnavailableException, ObjectNotfoundException {
		this.videoContainer = videoContainer;
		mediaCollection = OpenedProjectController.getInstance().getMediaCollectionController().findMediaCollectionById(mediaCollectionId);
		PlayableMedia media = (PlayableMedia) OpenedProjectController.getInstance().getMediaController().find(mediaCollection.getMediaItem().getId());
		if (!isMediaResourceAvailable(media))
			throw new MediaUnavailableException("Media resource unavailable for " + mediaCollection.getMediaItem().getId());
		setupMediaCollection();
		setupMediaCollectionEntries();
	}
	


	private void setupMediaCollection() throws MediaUnavailableException, ObjectNotfoundException {
		GstreamerPlayableMediaPlayer gstreamerPlayableMediaPlayer = new GstreamerPlayableMediaPlayer(mediaCollection.getMediaItem().getId());
		VideoWindow videoWindow = videoContainer.createVideoWindow();
		gstreamerPlayableMediaPlayer.setVideoSink(videoWindow.getElement());
	}

	private boolean isMediaResourceAvailable(PlayableMedia media) {
		return (media.getMediaFileResource() != null);
	}

	private void setupMediaCollectionEntries() throws MediaUnavailableException, ObjectNotfoundException {
		for (MediaCollectionEntry mediaCollectionEntry : mediaCollection.getMediaCollectionEntryList()) {
			GstreamerMediaCollectionEntry gstreamerMediaCollectionEntry = new GstreamerMediaCollectionEntry(mediaCollectionEntry);
			VideoWindow videoWindow = videoContainer.createVideoWindow();
			gstreamerMediaCollectionEntry.setVideoSink(videoWindow.getElement());
			playBinMap.put(mediaCollectionEntry, gstreamerMediaCollectionEntry);
		}
	}

	@Override
	public void setPlayRate(PlayRate playRate) {
		//
	}

	@Override
	public PlayRate getPlayRate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPosition(long milliSec) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
