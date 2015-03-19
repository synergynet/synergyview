package synergyviewcore.media.gstreamer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.gstreamer.Buffer;
import org.gstreamer.Bus;
import org.gstreamer.ElementFactory;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.State;
import org.gstreamer.TagList;
import org.gstreamer.elements.BaseSink;
import org.gstreamer.elements.FakeSink;
import org.gstreamer.elements.PlayBin2;

import synergyviewcore.media.IllegalMediaFormatException;
import synergyviewcore.media.model.AudioMediaTrack;
import synergyviewcore.media.model.MediaInfo;
import synergyviewcore.media.model.VideoMediaTrack;


public class GstreamerMediaUtil {
	private static Logger logger = Logger.getLogger(GstreamerMediaUtil.class);
	private static final String VIDEO_CODEC_KEY = "video-codec";
	private static final String AUDIO_CODEC_KEY = "audio-codec";
	private static final String CONTAINER_KEY = "container-format";
	private static final String DURATION_KEY = "duration";
	private static final long TIMEOUT_SEC = 5L;
	
	public static MediaInfo getMediaInfo(File mediaFile, final IProgressMonitor monitor) throws IllegalMediaFormatException{
		final CountDownLatch done = new CountDownLatch(1);
		final Map<String, String> tags = new HashMap<String, String>();
		
		// Creates a temp playbin to get media info
		final PlayBin2 playbin = new PlayBin2(String.format("Play bin media graph for %s file.", mediaFile.getName()));
		monitor.beginTask(String.format("Retrieving media properties for %s file.", mediaFile.getName()), 4);
		playbin.setInputFile(mediaFile);
		FakeSink video = (FakeSink) ElementFactory.make(FakeSink.GST_NAME, "video-sink");
		playbin.setVideoSink(video);
		FakeSink audio = (FakeSink) ElementFactory.make(FakeSink.GST_NAME, "audio-sink");
		playbin.setAudioSink(audio);
		monitor.worked(1);
		playbin.getBus().connect(new Bus.ERROR() {
			@Override
			public void errorMessage(GstObject source, int code, String message) {
				playbin.setState(State.NULL);
				monitor.worked(2);
				done.countDown(); 
			}
		});
		
		playbin.getBus().connect(new Bus.TAG() {
			@Override
			public void tagsFound(GstObject source, TagList tagList) {
				for (String tag : tagList.getTagNames()) {
					if (!tags.containsKey(tag))
						tags.put(tag, tagList.getString(tag, 0));
                }
				monitor.worked(1);
			}
        });
		
		//
        // In theory, an ASYNC_DONE from the pipeline corresponds with the demux
        // completing parsing the media file
        //
		playbin.getBus().connect(new Bus.ASYNC_DONE() {
            public void asyncDone(GstObject source) {
            	//
            }
        });
		
        audio.set("signal-handoffs", true);
        video.set("signal-handoffs", true);
        
        //
        // As soon as data starts to flow, it means all tags have been found
        //
        BaseSink.HANDOFF handoff = new BaseSink.HANDOFF() {
            public void handoff(BaseSink sink, Buffer buffer, Pad pad) {
            	long duration = playbin.queryDuration(TimeUnit.MILLISECONDS);
            	if (duration > 0 && !tags.containsKey(DURATION_KEY)) {
        	    	tags.put(DURATION_KEY, Long.toString(duration));
            		done.countDown();
            	}
            }
        };
        audio.connect(handoff);
        video.connect(handoff);

		playbin.setState(State.PLAYING);
		
		try {
			done.await(TIMEOUT_SEC,  TimeUnit.SECONDS);
	    } catch (InterruptedException ex) {
	    	//
	    }
    	
		playbin.setState(State.NULL);
		monitor.worked(1);
		if (tags.isEmpty())
			throw new IllegalMediaFormatException("Unable to find media information in the file!");
		
		if (!tags.containsKey(VIDEO_CODEC_KEY) && !tags.containsKey(AUDIO_CODEC_KEY))
			throw new IllegalMediaFormatException("No audio or video tracks found!");
		MediaInfo mediaInfo = collectMediaInformation(tags);
		monitor.worked(1);
		return mediaInfo;
	}
	
	private static MediaInfo collectMediaInformation(Map<String, String> tagsMap) {
		
		final MediaInfo mediaInfo = new MediaInfo();
		mediaInfo.setId(UUID.randomUUID().toString());
		String videoTag = tagsMap.get(VIDEO_CODEC_KEY);
		if (videoTag != null) {
			VideoMediaTrack videoTrack = new VideoMediaTrack();
			videoTrack.setId(UUID.randomUUID().toString());
			videoTrack.setVideoCodec(videoTag);
			mediaInfo.setVideoTrack(videoTrack);
		}
		String audioTag = tagsMap.get(AUDIO_CODEC_KEY);
		if (audioTag != null) {
			AudioMediaTrack audioTrack = new AudioMediaTrack();
			audioTrack.setAudioCodec(audioTag);
			audioTrack.setId(UUID.randomUUID().toString());
			mediaInfo.setAudioTrack(audioTrack);
		}
		String mediaContainerTag = tagsMap.get(CONTAINER_KEY);
		if (mediaContainerTag != null) {
			mediaInfo.setContainerFormat(mediaContainerTag);
		}
		String durationTag = tagsMap.get(DURATION_KEY);
		if (durationTag != null) {
			mediaInfo.setLengthInMilliSeconds(Long.parseLong(durationTag));
		}
		logger.debug("Media info object with media tags created.");
		return mediaInfo;
		
	}
}
