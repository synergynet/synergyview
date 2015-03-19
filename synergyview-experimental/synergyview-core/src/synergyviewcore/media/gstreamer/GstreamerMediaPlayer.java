package synergyviewcore.media.gstreamer;

import java.io.File;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Display;
import org.gstreamer.Bus;
import org.gstreamer.Element;
import org.gstreamer.Format;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.State;
import org.gstreamer.elements.PlayBin2;

import synergyviewcore.media.IMediaPlayer;
import uk.ac.durham.tel.commons.model.PropertySupportObject;

public class GstreamerMediaPlayer extends PropertySupportObject implements IMediaPlayer {
	private PlayBin2 mediaPlayBin;
    private static final int UPDATE_INTERVAL = 100; //This is to cover 10 fps 
    private static final TimeUnit scaleUnit = TimeUnit.MILLISECONDS;
    private volatile ScheduledFuture<?> updateTask = null;
    private long position;
    
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
			
			if (current == State.PLAYING) 
				startPoll();
			else 
				stopPoll();
		}		
	};
	
    private synchronized void startPoll() {
        Runnable task = new Runnable() {
            public void run() {
                updatePosition(mediaPlayBin.queryPosition(scaleUnit));
            }
        };
        if (updateTask == null) 
        	updateTask = Gst.getScheduledExecutorService().scheduleAtFixedRate(task, UPDATE_INTERVAL, UPDATE_INTERVAL, scaleUnit);
    }
    
    private void updatePosition(final long currentPostion) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				GstreamerMediaPlayer.this.firePropertyChange(PROP_POSITION, position, position = currentPostion);
			}
		});
	}

	private synchronized void stopPoll() {
        if (updateTask != null) {
            updateTask.cancel(true);
            updateTask = null;
        }
    }
	
	public GstreamerMediaPlayer(File mediaFile) {
		mediaPlayBin = createMediaPlayBin(mediaFile);
	}
	
	private PlayBin2 createMediaPlayBin(File mediaFile) {
		final PlayBin2 playbin = new PlayBin2("Media Graph");
		playbin.setInputFile(mediaFile);
		playbin.getBus().connect(stateChangeEventListener);
		playbin.getBus().connect(segmentChangeEventListener);
		playbin.getBus().connect(segmentStartEventListener);
		return playbin;
	}
	
	public void setVideoSink(Element element) {
		mediaPlayBin.setVideoSink(element);
	}

	@Override
	public void setPlayRate(PlayRate playRate) {
		if (playRate == PlayRate.X1)
			mediaPlayBin.setState(State.PLAYING);
		else if (playRate == PlayRate.STOP)
			mediaPlayBin.setState(State.NULL);
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
		return position;
	}

	@Override
	public void dispose() {
		if (mediaPlayBin!=null)
			mediaPlayBin.setState(State.NULL);
	}
}
