package synergyviewcore.media.model;

import java.awt.Component;
import java.awt.Dimension;


/**
 * The Interface IMedia.
 */
public interface IMedia {
	
	/**
	 * The Enum PlayRate.
	 */
	public enum PlayRate { 
 /** The half. */
 HALF, 
 /** The X1. */
 X1, 
 /** The X2. */
 X2, 
 /** The X3. */
 X3, 
 /** The X4. */
 X4 };
	
	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	Dimension getSize();
	
	/**
	 * Gets the duration.
	 *
	 * @return the duration
	 */
	int getDuration();
	
	/**
	 * Gets the formatted duration.
	 *
	 * @return String formatted in HH:MM:SS,SSS of the media guration
	 */
	String getFormattedDuration();
	
	/**
	 * Gets the formatted time.
	 *
	 * @return String formatted in HH:MM:SS,SSS of the media current time
	 */
	String getFormattedTime();
	
	/**
	 * Change the current time position of the media.
	 *
	 */
	public static final String PROP_TIME = "time";
	
	/**
	 * Sets the time.
	 *
	 * @param time the new time
	 */
	void setTime(int time);
	
	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	int getTime();
	
	/** The Constant PROP_MUTE. */
	public static final String PROP_MUTE = "mute";
	
	/**
	 * Checks if is mute.
	 *
	 * @return true, if is mute
	 */
	boolean isMute();
	
	/**
	 * Sets the mute.
	 *
	 * @param muteValue the new mute
	 */
	void setMute(boolean muteValue);
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();
	
	/**
	 * Gets the UI component.
	 *
	 * @return the UI component
	 */
	Component getUIComponent();
	
	/**
	 * Play and stop the media
	 * "playing" boolean perperty changed event is fired when the media starts playing.
	 *
	 */
	public static final String PROP_PLAYING = "playing";
	
	/**
	 * Sets the playing.
	 *
	 * @param playValue the new playing
	 */
	void setPlaying(boolean playValue);
	
	/**
	 * Checks if is playing.
	 *
	 * @return true, if is playing
	 */
	boolean isPlaying();

	/**
	 * Dispose.
	 */
	void dispose();
	
	/**
	 * Step one frame or sample forward.
	 */
	void stepFF();
	
	/**
	 * Step one frame or sample backward.
	 */
	void stepRE();
	
	/**
	 * Change media play rate. The only apply to when playing the media
	 *
	 */
	public static final String PROP_RATE = "rate";
	
	/**
	 * Sets the rate.
	 *
	 * @param rate the new rate
	 */
	void setRate(PlayRate rate);
	
	/**
	 * Gets the rate.
	 *
	 * @return the rate
	 */
	PlayRate getRate();
	
	/**
	 * Checks if is audio available.
	 *
	 * @return true, if is audio available
	 */
	boolean isAudioAvailable();
	
	/**
	 * Prepare media.
	 */
	public void prepareMedia();
}
