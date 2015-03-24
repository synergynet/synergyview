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

/**
 * The Class VLCMedia.
 */
public class VLCMedia extends AbstractMedia {
	
	/**
	 * The Class UpdateStatusThread.
	 */
	class UpdateStatusThread extends Thread {
		
		/** The active. */
		private boolean active = true;
		
		/** The previous time. */
		private int previousTime = -1;
		
		/**
		 * Instantiates a new update status thread.
		 */
		public UpdateStatusThread() {
			super();
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			while (active) {
				if (isPlaying()) {
					try {
						int currentTime = (int) mediaPlayerComponent
								.getMediaPlayer().getTime();
						VLCMedia.this.firePropertyChange(IMedia.PROP_TIME,
								previousTime, currentTime);
					} catch (Exception ex) {
					}
				}
				
				try {
					Thread.sleep(OFFSET);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * Shutdown.
		 */
		public void shutdown() {
			active = false;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#start()
		 */
		public void start() {
			super.start();
		}
		
	}
	
	/** The Constant OFFSET. */
	private static final int OFFSET = 16;
	
	/** The _current play rate. */
	private PlayRate _currentPlayRate = PlayRate.X1;
	
	/** The height. */
	private int height = 320;
	
	/** The logger. */
	private final ILog logger;
	
	/** The media player component. */
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	
	/** The movie dimension. */
	private Dimension movieDimension;
	
	/** The saved volume. */
	private int savedVolume = 0;
	
	/** The update thread. */
	private UpdateStatusThread updateThread;
	
	/** The width. */
	private int width = 400;
	
	/**
	 * Instantiates a new VLC media.
	 * 
	 * @param mediaUrl
	 *            the media url
	 * @param name
	 *            the name
	 */
	public VLCMedia(URI mediaUrl, String name) {
		super(mediaUrl, name);
		logger = Activator.getDefault().getLog();
		
		movieDimension = new Dimension(width, height);
		
		try {
			mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
			mediaPlayerComponent.getMediaPlayer().mute(true);
			mediaPlayerComponent.getMediaPlayer().setRepeat(true);
			mediaPlayerComponent.getMediaPlayer().prepareMedia(
					new File(mediaUrl).toString(), "");
			this.name = name;
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#dispose()
	 */
	public void dispose() {
		mediaPlayerComponent.release();
		updateThread.shutdown();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#getDuration()
	 */
	public int getDuration() {
		try {
			return (int) mediaPlayerComponent.getMediaPlayer().getLength();
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
			return 0;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#getFormattedDuration()
	 */
	public String getFormattedDuration() {
		return getStringTimeFormat(this.getDuration());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#getFormattedTime()
	 */
	public String getFormattedTime() {
		return getStringTimeFormat(getTime());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#getRate()
	 */
	public PlayRate getRate() {
		return _currentPlayRate;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#getSize()
	 */
	public Dimension getSize() {
		return movieDimension;
	}
	
	/**
	 * Gets the string time format.
	 * 
	 * @param time
	 *            the time
	 * @return the string time format
	 */
	private String getStringTimeFormat(int time) {
		try {
			if (time == 0) {
				return "00:00:00,000";
			}
			Calendar currentTime = Calendar.getInstance();
			currentTime.setTimeInMillis(time);
			NumberFormat formatter = new DecimalFormat("00");
			NumberFormat miFormatter = new DecimalFormat("000");
			return String
					.format("%s:%s:%s,%s", formatter.format(currentTime
							.get(Calendar.HOUR_OF_DAY) - 1), formatter
							.format(currentTime.get(Calendar.MINUTE)),
							formatter.format(currentTime.get(Calendar.SECOND)),
							miFormatter.format(currentTime
									.get(Calendar.MILLISECOND)));
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
			return "00:00:00,000";
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#getTime()
	 */
	public int getTime() {
		return (int) mediaPlayerComponent.getMediaPlayer().getTime();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#getUIComponent()
	 */
	public Component getUIComponent() {
		try {
			return mediaPlayerComponent;
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#isAudioAvailable()
	 */
	public boolean isAudioAvailable() {
		try {
			return mediaPlayerComponent.getMediaPlayer().getAudioTrackCount() > 0;
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
			return false;
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#isMute()
	 */
	public boolean isMute() {
		try {
			return (mediaPlayerComponent.getMediaPlayer().getVolume() == 0) ? true
					: false;
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#isPlaying()
	 */
	public boolean isPlaying() {
		return mediaPlayerComponent.getMediaPlayer().isPlaying();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#prepareMedia()
	 */
	public void prepareMedia() {
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#setMute(boolean)
	 */
	public void setMute(boolean mute) {
		try {
			if (mute) {
				if (mediaPlayerComponent.getMediaPlayer().getVolume() != 0) {
					savedVolume = mediaPlayerComponent.getMediaPlayer()
							.getVolume();
					mediaPlayerComponent.getMediaPlayer().setVolume(0);
				}
			} else {
				if (mediaPlayerComponent.getMediaPlayer().getVolume() == 0) {
					mediaPlayerComponent.getMediaPlayer()
							.setVolume(savedVolume);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#setPlaying(boolean)
	 */
	public void setPlaying(boolean playingValue) {
		try {
			mediaPlayerComponent.getMediaPlayer().setPause(!playingValue);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * synergyviewcore.media.model.IMedia#setRate(synergyviewcore.media.model
	 * .IMedia.PlayRate)
	 */
	public void setRate(PlayRate rate) {
		
		if (isPlaying()) {
			updateMoviePlayRate();
		}
		firePropertyChange(IMedia.PROP_RATE, _currentPlayRate,
				_currentPlayRate = rate);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#setTime(int)
	 */
	public void setTime(int time) {
		try {
			int previousTime = getTime();
			this.firePropertyChange(IMedia.PROP_TIME, previousTime, time);
			mediaPlayerComponent.getMediaPlayer().setTime(time);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#stepFF()
	 */
	public void stepFF() {
		try {
			long time = mediaPlayerComponent.getMediaPlayer().getTime();
			long newTime = time + OFFSET;
			if (newTime > mediaPlayerComponent.getMediaPlayer().getLength()) {
				newTime = mediaPlayerComponent.getMediaPlayer().getLength();
			}
			mediaPlayerComponent.getMediaPlayer().setTime(time);
			firePropertyChange(IMedia.PROP_TIME, time, newTime);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#stepRE()
	 */
	public void stepRE() {
		try {
			long time = mediaPlayerComponent.getMediaPlayer().getTime();
			long newTime = time - OFFSET;
			if (newTime < 0) {
				newTime = 0;
			}
			mediaPlayerComponent.getMediaPlayer().setTime(time);
			firePropertyChange(IMedia.PROP_TIME, time, newTime);
			
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
		}
		
	}
	
	/**
	 * Update movie play rate.
	 */
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
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
		}
	}
	
}
