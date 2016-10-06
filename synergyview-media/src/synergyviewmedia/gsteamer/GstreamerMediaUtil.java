package synergyviewmedia.gsteamer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

import synergyviewmedia.IllegalMediaFormatException;
import synergyviewmedia.model.AudioMediaTrack;
import synergyviewmedia.model.MediaInfo;
import synergyviewmedia.model.VideoMediaTrack;


public class GstreamerMediaUtil {
	private static final String VIDEO_CODEC_KEY = "video-codec";
	private static final String AUDIO_CODEC_KEY = "audio-codec";
	private static final String CONTAINER_KEY = "container-format";
	private static final String DURATION_KEY = "duration";
	
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
				done.countDown();
			}
		});
		
		playbin.getBus().connect(new Bus.TAG() {
			@Override
			public void tagsFound(GstObject source, TagList tagList) {
				for (String tag : tagList.getTagNames()) {
					System.out.println(tag);
					System.out.println(tagList.getString(tag));
					if (!tags.containsKey(tag))
						tags.put(tag, tagList.getString(tag, 0));
                }
				monitor.worked(2);
			}
        });
		
		//
        // In theory, an ASYNC_DONE from the pipeline corresponds with the demux
        // completing parsing the media file
        //
		playbin.getBus().connect(new Bus.ASYNC_DONE() {
            public void asyncDone(GstObject source) {
            	playbin.setState(State.NULL);
                done.countDown();
                monitor.worked(3);
            }
        });
		
        audio.set("signal-handoffs", true);
        video.set("signal-handoffs", true);
        
        //
        // As soon as data starts to flow, it means all tags have been found
        //
        BaseSink.HANDOFF handoff = new BaseSink.HANDOFF() {
            public void handoff(BaseSink sink, Buffer buffer, Pad pad) {
                done.countDown();
            }
        };
        audio.connect(handoff);
        video.connect(handoff);

		playbin.setState(State.PLAYING);
		
		try {
			done.await();
	    } catch (InterruptedException ex) {
	    	//
	    }
	    if (playbin.isPlaying()) {
	    	long duration = playbin.queryDuration(TimeUnit.MILLISECONDS);
	    	tags.put(DURATION_KEY, Long.toString(duration));
	    }
    	
		playbin.setState(State.NULL);
		monitor.worked(4);
		if (tags.isEmpty())
			throw new IllegalMediaFormatException("Unable to find media information in the file!");
		
		if (!tags.containsKey(VIDEO_CODEC_KEY) && !tags.containsKey(AUDIO_CODEC_KEY))
			throw new IllegalMediaFormatException("No audio or video tracks found!");
		
		return collectMediaInformation(tags);
	}
	
	private static MediaInfo collectMediaInformation(Map<String, String> tagsMap) {
		final MediaInfo mediaInfo = new MediaInfo();
		String videoTag = tagsMap.get(VIDEO_CODEC_KEY);
		if (videoTag != null) {
			VideoMediaTrack videoTrack = new VideoMediaTrack();
			videoTrack.setVideoCodec(videoTag);
			mediaInfo.setVideoTrack(videoTrack);
		}
		String audioTag = tagsMap.get(AUDIO_CODEC_KEY);
		if (audioTag != null) {
			AudioMediaTrack audioTrack = new AudioMediaTrack();
			audioTrack.setAudioCodec(audioTag);
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
		
		return mediaInfo;
	}
}
