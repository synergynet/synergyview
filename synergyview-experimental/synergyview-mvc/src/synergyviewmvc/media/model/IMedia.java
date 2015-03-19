package synergyviewmvc.media.model;

import java.awt.Component;
import java.awt.Dimension;

public interface IMedia {
	public enum PlayRate { HALF, X1, X2, X3, X4 };
	Dimension getSize();
	int getDuration();
	
	/**
	 * @return String formatted in HH:MM:SS,SSS of the media guration
	 */
	String getFormattedDuration();
	
	/**
	 * 
	 * @return String formatted in HH:MM:SS,SSS of the media current time
	 */
	String getFormattedTime();
	
	/**
	 * Change the current time position of the media
	 * 
	 * @param time
	 */
	public static final String PROP_TIME = "time";
	void setTime(int time);
	int getTime();
	
	public static final String PROP_MUTE = "mute";
	boolean isMute();
	void setMute(boolean muteValue);
	
	String getName();
	Component getUIComponent();
	
	/**
	 * Play and stop the media
	 * "playing" boolean perperty changed event is fired when the media starts playing
	 * 
	 * @param playValue true to play and false to stop  
	 */
	public static final String PROP_PLAYING = "playing";
	
	void setPlaying(boolean playValue);
	boolean isPlaying();

	void dispose();
	
	/**
	 * Step one frame or sample forward
	 * 
	 */
	void stepFF();
	
	/**
	 * Step one frame or sample backward
	 * 
	 */
	void stepRE();
	
	/**
	 * Change media play rate. The only apply to when playing the media
	 * 
	 * @param rate
	 */
	public static final String PROP_RATE = "rate";
	void setRate(PlayRate rate);
	PlayRate getRate();
	
	boolean isAudioAvailable();
}
