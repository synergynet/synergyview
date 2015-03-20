package synergyviewcore.media.model;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import synergyviewcore.Activator;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class VLCMedia extends AbstractMedia {
	
	private static final int OFFSET = 16;	
	
	private Dimension movieDimension;
	private int savedVolume = 0;
	private PlayRate _currentPlayRate = PlayRate.X1;
	private final ILog logger;
	
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private UpdateStatusThread updateThread;
	
	private int width = 400;
	private int height = 320;
	
	public VLCMedia(URI mediaUrl, String name) {
		super(mediaUrl, name);
		logger = Activator.getDefault().getLog();
		
		movieDimension = new Dimension(width, height);
		
		try {
			mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
			mediaPlayerComponent.getMediaPlayer().mute(true);
			mediaPlayerComponent.getMediaPlayer().setRepeat(true);
			mediaPlayerComponent.getMediaPlayer().prepareMedia(new File(mediaUrl).toString(), "");
			this.name = name;
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}
	
	public void prepareMedia(){
		int initialVolume = mediaPlayerComponent.getMediaPlayer().getVolume();
		mediaPlayerComponent.getMediaPlayer().play();	
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mediaPlayerComponent.getMediaPlayer().pause();
		mediaPlayerComponent.getMediaPlayer().setVolume(initialVolume);
		
		updateThread = new UpdateStatusThread();
		updateThread.start();
	}

	public void dispose() {		
		mediaPlayerComponent.release();
		updateThread.shutdown();		
	}

	public void setMute(boolean mute) {
		try {
			if (mute) {
				if (mediaPlayerComponent.getMediaPlayer().getVolume() != 0) {
					savedVolume = mediaPlayerComponent.getMediaPlayer().getVolume();
					mediaPlayerComponent.getMediaPlayer().setVolume(0);
				}
			} else {
				if (mediaPlayerComponent.getMediaPlayer().getVolume() == 0) {
					mediaPlayerComponent.getMediaPlayer().setVolume(savedVolume);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getTime() {
		return (int)mediaPlayerComponent.getMediaPlayer().getTime();
	}

	public Dimension getSize() {
		return movieDimension;
	}

	public void setTime(int time) {
		try {
			int previousTime = getTime();
			this.firePropertyChange(IMedia.PROP_TIME, previousTime, time);
			mediaPlayerComponent.getMediaPlayer().setTime(time);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}

	public String getFormattedTime() {
		return getStringTimeFormat(getTime());
	}

	private String getStringTimeFormat(int time) {
		try {
			if (time == 0)
				return "00:00:00,000";
			Calendar currentTime = Calendar.getInstance();
			currentTime.setTimeInMillis(time);
			NumberFormat formatter = new DecimalFormat("00");
			NumberFormat miFormatter = new DecimalFormat("000");
			return String.format("%s:%s:%s,%s", formatter.format(currentTime
					.get(Calendar.HOUR_OF_DAY) - 1), formatter
					.format(currentTime.get(Calendar.MINUTE)), formatter
					.format(currentTime.get(Calendar.SECOND)), miFormatter
					.format(currentTime.get(Calendar.MILLISECOND)));
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.WARNING,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			return "00:00:00,000";
		}
	}

	public int getDuration() {
		try {
			return (int)mediaPlayerComponent.getMediaPlayer().getLength();
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			return 0;
		}
	}

	public void setPlaying(boolean playingValue) {
		try {
			mediaPlayerComponent.getMediaPlayer().setPause(!playingValue);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}



	public Component getUIComponent() {
		try {
			return mediaPlayerComponent;
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			return null;
		}
	}
	
	public String getFormattedDuration() {
		return getStringTimeFormat(this.getDuration());
	}

	public boolean isAudioAvailable() {
		try {
			return mediaPlayerComponent.getMediaPlayer().getAudioTrackCount() > 0;
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			return false;
		}

	}

	public boolean isMute() {
		try {
			return (mediaPlayerComponent.getMediaPlayer().getVolume() == 0) ? true : false;
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
		return false;
	}

	public boolean isPlaying() {
		return mediaPlayerComponent.getMediaPlayer().isPlaying();
	}

	public void stepFF() {
		try {			
			long time = mediaPlayerComponent.getMediaPlayer().getTime();
			long newTime = time + OFFSET;
			if (newTime > mediaPlayerComponent.getMediaPlayer().getLength())newTime = mediaPlayerComponent.getMediaPlayer().getLength();
			mediaPlayerComponent.getMediaPlayer().setTime(time);
			firePropertyChange(IMedia.PROP_TIME, time, newTime);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}

	}

	public void stepRE() {
		try {
			long time = mediaPlayerComponent.getMediaPlayer().getTime();
			long newTime = time - OFFSET;
			if (newTime < 0)newTime = 0;
			mediaPlayerComponent.getMediaPlayer().setTime(time);
			firePropertyChange(IMedia.PROP_TIME, time, newTime);

		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}

	}

	public void setRate(PlayRate rate) {

		if (isPlaying()) {
			updateMoviePlayRate();
		}
		firePropertyChange(IMedia.PROP_RATE, _currentPlayRate, _currentPlayRate = rate);
	}

	private void updateMoviePlayRate() {
		try {
			switch (_currentPlayRate) {
			case HALF:
				mediaPlayerComponent.getMediaPlayer().setRate(0.5f);
				break;
			case X1:
				mediaPlayerComponent.getMediaPlayer().setRate(1f);
				break;
			case X2:
				mediaPlayerComponent.getMediaPlayer().setRate(2f);
				break;
			case X3:
				mediaPlayerComponent.getMediaPlayer().setRate(3f);
				break;
			case X4:
				mediaPlayerComponent.getMediaPlayer().setRate(4f);
				break;
			}
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}

	public PlayRate getRate() {
		return _currentPlayRate;
	}
	
	class UpdateStatusThread extends Thread {
		
		private boolean active = true;
		private int previousTime = -1;
		
		public UpdateStatusThread () {
			super();
		}
		
		public void start () {
			super.start();
		}
		
		public void run () {
			while(active){
				if (isPlaying()){
					try{
						int currentTime = (int)mediaPlayerComponent.getMediaPlayer().getTime();
						VLCMedia.this.firePropertyChange(IMedia.PROP_TIME, previousTime, currentTime);
					}catch(Exception ex){}
				}
				
				try {
					Thread.sleep(OFFSET);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void shutdown(){
			active = false;
		}
		
	}

}
