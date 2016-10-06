package synergyviewcore.collection.gstreamer;

import org.gstreamer.Element;
import org.gstreamer.elements.FakeSink;
import org.gstreamer.elements.PlayBin2;

import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.media.MediaUnavailableException;
import synergyviewcore.media.model.PlayableMedia;
import synergyviewcore.project.OpenedProjectController;
import uk.ac.durham.tel.commons.model.PropertySupportObject;

public class GstreamerPlayableMediaPlayer extends PropertySupportObject {
	protected PlayBin2 playBin;
	private PlayableMedia media;
	public GstreamerPlayableMediaPlayer(String mediaId) throws MediaUnavailableException, ObjectNotfoundException {
		this.media = (PlayableMedia) OpenedProjectController.getInstance().getMediaController().find(mediaId);
		init();
	}
	private void init() throws MediaUnavailableException {
		if (media.getMediaFileResource()==null)
			throw new MediaUnavailableException("File resource for " + media.getId() + "was not found.");
		playBin = new PlayBin2("Media Graph");
		playBin.setInputFile(media.getMediaFileResource().getFullPath().toFile());
		playBin.setVideoSink(new FakeSink("Fake Sync"));
	}

	public PlayBin2 getPlayBin() {
		return playBin;
	}
	
	public void setVideoSink(Element element) {
		playBin.setVideoSink(element);
	}
}
