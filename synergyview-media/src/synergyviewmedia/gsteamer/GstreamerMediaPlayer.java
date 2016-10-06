package synergyviewmedia.gsteamer;

import java.io.File;
import java.nio.IntBuffer;
import java.util.concurrent.TimeUnit;

import org.gstreamer.Bus;
import org.gstreamer.Format;
import org.gstreamer.GstObject;
import org.gstreamer.State;
import org.gstreamer.elements.PlayBin2;
import org.gstreamer.elements.RGBDataSink;
import org.gstreamer.elements.RGBDataSink.Listener;

import synergyviewcommons.model.PropertySupportObject;
import synergyviewmedia.IMediaPlayer;
import synergyviewmedia.VideoFrameData;

public class GstreamerMediaPlayer extends PropertySupportObject implements IMediaPlayer {
	private PlayBin2 mediaPlayBin;
	private RGBDataSink videosink;
	private IVideoFrameListener videoFrameListener;
	private Listener rgbDataSinkListener = new Listener() {
		@Override
		public void rgbFrame(boolean isPrerollFrame, int width, int height,
				IntBuffer rgb) {
			dispatchVideoFrameData(width, height, rgb);
		}
	};
	
	private Bus.SEGMENT_DONE segmentChangeEventListener = new Bus.SEGMENT_DONE() {
		@Override
		public void segmentDone(GstObject source, Format format, long position) {
			System.out.println(position);
			GstreamerMediaPlayer.this.firePropertyChange(PROP_POSITION, null, position);
		}
	};
	
	private Bus.SEGMENT_START segmentStartEventListener = new Bus.SEGMENT_START() {

		@Override
		public void segmentStart(GstObject source, Format format, long position) {
			System.out.println("start"+position);
		}


	};
	
	private Bus.STATE_CHANGED stateChangeEventListener = new Bus.STATE_CHANGED() {
		@Override
		public void stateChanged(GstObject source, State old, State current,
				State pending) {
			if (current == State.PLAYING) {
				//
			}
			else if (current == State.PAUSED) {
				//
			}
		}		
	};
	
	VideoFrameData videoFrameData;
	int[] pixels;
	public GstreamerMediaPlayer(File mediaFile) {
		mediaPlayBin = createMediaPlayBin(mediaFile);
	}
	
	private PlayBin2 createMediaPlayBin(File mediaFile) {
		final PlayBin2 playbin = new PlayBin2("Media Graph");
		playbin.setInputFile(mediaFile);
		playbin.getBus().connect(stateChangeEventListener);
		playbin.getBus().connect(segmentChangeEventListener);
		playbin.getBus().connect(segmentStartEventListener);
		videosink = new RGBDataSink("GstVideoComponent", rgbDataSinkListener);
		videosink.setPassDirectBuffer(true);
		playbin.setVideoSink(videosink);
		return playbin;
	}
	
	public void setVideoFrameListener(IVideoFrameListener videoFrameListener) {
		this.videoFrameListener = videoFrameListener;
	}

	@Override
	public void setPlayRate(PlayRate playRate) {
		if (playRate == PlayRate.x1)
			mediaPlayBin.setState(State.PLAYING);
		else if (playRate == PlayRate.STOP)
			mediaPlayBin.setState(State.PAUSED);
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
		return mediaPlayBin.queryPosition(TimeUnit.MILLISECONDS);
	}

	@Override
	public void dispose() {
		if (mediaPlayBin!=null)
			mediaPlayBin.setState(State.NULL);
	}

	
	private void dispatchVideoFrameData(int width, int height, IntBuffer rgb) {
		if (videoFrameListener == null) 
			return;
		if (videoFrameData == null) { 
			videoFrameData = new VideoFrameData();
			videoFrameData.setPixels(new int[rgb.limit()]);
			videoFrameData.setHeight(height);
			videoFrameData.setWidth(width);
		}
		rgb.get(videoFrameData.getPixels());
		videoFrameListener.updateFrame(videoFrameData);
	}

}
