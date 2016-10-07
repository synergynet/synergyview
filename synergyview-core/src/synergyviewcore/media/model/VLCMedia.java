package synergyviewcore.media.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import javax.swing.JPanel;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import synergyviewcore.Activator;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

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
						
						float pos = mediaPlayerComponent.getPosition();						
						if (pos > 0 && pos < 1){						
							int currentTime = (int) mediaPlayerComponent.getTime();
							VLCMedia.this.firePropertyChange(IMedia.PROP_TIME, previousTime, currentTime);
						}
						
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

    /** The image to render the video playback to. */
    private final BufferedImage image;
    
	/** The panel to add the image to. */
	private JPanel jPanel;
	
	/** The logger. */
	private final ILog logger;
	
	/** The media player component. */
	private DirectMediaPlayer mediaPlayerComponent;
	
	/** The string representing the location of the media file to be played. */
	private String mediaLoc = "";
	
	/** The movie dimension. */
	private Dimension movieDimension;
	
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
		
		jPanel = new JPanel(new GridLayout()) {
			private static final long serialVersionUID = -575622818908986903L;

			@Override
            protected void paintComponent(Graphics g) {				
				super.paintComponents(g);			
				
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
				
				float pos = mediaPlayerComponent.getPosition();
				
				if (pos > 0.0001 && pos < 1) {
					
					// Get actual dimensions to draw to.
					int videoWidth = mediaPlayerComponent.getVideoDimension().width;
					int videoHeight = mediaPlayerComponent.getVideoDimension().height;
					
					float ratio = 1;
					if (videoWidth > videoHeight) {
						ratio = (float)getWidth()/videoWidth;
					} else{
						ratio =  (float)getHeight()/videoHeight;
					}
					
					int scaledWidth = Math.round(ratio * videoWidth); 
					int scaledHeight = Math.round(ratio * videoHeight); 
					
					int xOffset = (getWidth() - scaledWidth)/2;
					int yOffset = (getHeight() - scaledHeight)/2;			

	                g.drawImage(image, xOffset, yOffset, scaledWidth, scaledHeight, null);
				}
            }
        };
        jPanel.setOpaque(true);

        image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
		
		try {			

			mediaLoc = new File(mediaUrl).toString();
			
			MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory("--no-video-title-show", "--quiet");
			
	        BufferFormatCallback bufferFormatCallback = new BufferFormatCallback() {

	            public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
	                return new RV32BufferFormat(width, height);
	            }
	        };
			
			mediaPlayerComponent = mediaPlayerFactory.newDirectMediaPlayer(bufferFormatCallback, new JPanelRenderCallbackAdapter());
			
//			mediaPlayerComponent.setRepeat(true); 
			mediaPlayerComponent.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			    @Override
			    public void finished(MediaPlayer mediaPlayer) {
			    	mediaPlayerComponent.prepareMedia(mediaLoc, "");	
			    	mediaPlayer.play();
			    	mediaPlayer.setPosition(1);
			    	mediaPlayer.setPause(true);
			    }
			});

			mediaPlayerComponent.prepareMedia(mediaLoc, "");			
			
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
			return (int) mediaPlayerComponent.getLength();
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
		return (int) mediaPlayerComponent.getTime();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#getUIComponent()
	 */
	public Component getUIComponent() {
		try {
		
			return jPanel;
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
			return mediaPlayerComponent.getAudioTrackCount() > 0;
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
			return mediaPlayerComponent.isMute();
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
		return mediaPlayerComponent.isPlaying();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#prepareMedia()
	 */
	public void prepareMedia() {
		
		mediaPlayerComponent.mute(true);
		mediaPlayerComponent.play();
		try {
			Thread.sleep(1000);
			mediaPlayerComponent.pause();
			mediaPlayerComponent.setPosition(0);
			mediaPlayerComponent.mute(false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
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
			mediaPlayerComponent.mute(mute);
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
			mediaPlayerComponent.setPause(!playingValue);
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
			mediaPlayerComponent.setTime(time);
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
			long time = mediaPlayerComponent.getTime();
			long newTime = time + OFFSET;
			if (newTime > mediaPlayerComponent.getLength()) {
				newTime = mediaPlayerComponent.getLength();
			}
			mediaPlayerComponent.setTime(time);
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
			long time = mediaPlayerComponent.getTime();
			long newTime = time - OFFSET;
			if (newTime < 0) {
				newTime = 0;
			}
			mediaPlayerComponent.setTime(time);
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
					mediaPlayerComponent.setRate(0.5f);
					break;
				case X1:
					mediaPlayerComponent.setRate(1f);
					break;
				case X2:
					mediaPlayerComponent.setRate(2f);
					break;
				case X3:
					mediaPlayerComponent.setRate(3f);
					break;
				case X4:
					mediaPlayerComponent.setRate(4f);
					break;
			}
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
		}
	}
	
	/**
	 * Handles rendering of videos to the image on a jPanel.
	 */
    private class JPanelRenderCallbackAdapter extends RenderCallbackAdapter {

    	/**
    	 * Set up Renderer.
    	 */
        private JPanelRenderCallbackAdapter() {
            super(new int[width * height]);
        }

        /**
         * Called on render, outputs video frame to image object.
         * 
         * @param mediaPlayer The VLC Media player object.
         * @param rgbBuffer The buffer holding the current video frame.
         * 
         */
        @Override
        protected void onDisplay(DirectMediaPlayer mediaPlayer, int[] rgbBuffer) {
            image.setRGB(0, 0, width, height, rgbBuffer, 0, width);            
            jPanel.repaint();
        }
    }
	
}
