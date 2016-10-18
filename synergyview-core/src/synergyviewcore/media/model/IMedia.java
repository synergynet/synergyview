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
	X4
    };

    /** The Constant PROP_MUTE. */
    public static final String PROP_MUTE = "mute";

    /**
     * Play and stop the media "playing" boolean perperty changed event is fired when the media starts playing.
     */
    public static final String PROP_PLAYING = "playing";

    /**
     * Change media play rate. The only apply to when playing the media
     */
    public static final String PROP_RATE = "rate";

    /**
     * Change the current time position of the media.
     */
    public static final String PROP_TIME = "time";

    /**
     * Dispose.
     */
    void dispose();

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
     * Gets the name.
     * 
     * @return the name
     */
    String getName();

    /**
     * Gets the rate.
     * 
     * @return the rate
     */
    PlayRate getRate();

    /**
     * Gets the size.
     * 
     * @return the size
     */
    Dimension getSize();

    /**
     * Gets the time.
     * 
     * @return the time
     */
    int getTime();

    /**
     * Gets the UI component.
     * 
     * @return the UI component
     */
    Component getUIComponent();

    /**
     * Checks if is audio available.
     * 
     * @return true, if is audio available
     */
    boolean isAudioAvailable();

    /**
     * Checks if is mute.
     * 
     * @return true, if is mute
     */
    boolean isMute();

    /**
     * Checks if is playing.
     * 
     * @return true, if is playing
     */
    boolean isPlaying();

    /**
     * Prepare media.
     */
    public void prepareMedia();

    /**
     * Sets the mute.
     * 
     * @param muteValue
     *            the new mute
     */
    void setMute(boolean muteValue);

    /**
     * Sets the playing.
     * 
     * @param playValue
     *            the new playing
     */
    void setPlaying(boolean playValue);

    /**
     * Sets the rate.
     * 
     * @param rate
     *            the new rate
     */
    void setRate(PlayRate rate);

    /**
     * Sets the time.
     * 
     * @param time
     *            the new time
     */
    void setTime(int time);

    /**
     * Step one frame or sample forward.
     */
    void stepFF();

    /**
     * Step one frame or sample backward.
     */
    void stepRE();
}
