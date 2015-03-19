package synergyviewcore.collection.gstreamer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeUnit;

import org.gstreamer.Element;
import org.gstreamer.elements.PlayBin2;

import synergyviewcore.collection.model.MediaCollectionEntry;
import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.media.MediaUnavailableException;

public class GstreamerMediaCollectionEntry extends GstreamerPlayableMediaPlayer {
	
	private MediaCollectionEntry mediaCollectionEntry;
	private PropertyChangeListener offSetChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			updatePlayBin(((MediaCollectionEntry)arg0.getSource()).getOffset());
		}
	};
	
	public GstreamerMediaCollectionEntry(MediaCollectionEntry mediaCollectionEntry) throws MediaUnavailableException, ObjectNotfoundException {
		super(mediaCollectionEntry.getMediaItem().getId());
		this.mediaCollectionEntry = mediaCollectionEntry;
		init();
	}
	
	private void updatePlayBin(long offset) {
		
	}

	private void init() {
		playBin.seek(mediaCollectionEntry.getOffset(), TimeUnit.MILLISECONDS);
		mediaCollectionEntry.addPropertyChangeListener(MediaCollectionEntry.PROP_OFFSET, offSetChangeListener);
	}

	public PlayBin2 getPlayBin() {
		return playBin;
	}
	
	public void setVideoSink(Element element) {
		playBin.setVideoSink(element);
	}
}
