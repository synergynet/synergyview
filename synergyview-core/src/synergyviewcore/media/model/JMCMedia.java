package synergyviewcore.media.model;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import javax.swing.JPanel;

import com.sun.media.jmc.MediaProvider;
import com.sun.media.jmc.control.VideoRenderControl;
import com.sun.media.jmc.event.VideoRendererEvent;
import com.sun.media.jmc.event.VideoRendererListener;

/**
 * The Class JMCMedia.
 */
public class JMCMedia extends AbstractMedia {

    /** The mp. */
    private MediaProvider mp;

    /** The vrc. */
    private VideoRenderControl vrc;

    /**
     * Instantiates a new JMC media.
     * 
     * @param mediaUrl
     *            the media url
     * @param name
     *            the name
     */
    public JMCMedia(URI mediaUrl, String name) {
	super(mediaUrl, name);
	mp = new MediaProvider(mediaUrl);
	vrc = mp.getControl(VideoRenderControl.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#dispose()
     */
    public void dispose() {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#getDuration()
     */
    public int getDuration() {
	return (int) (mp.getDuration() * 1000d);
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#getFormattedDuration()
     */
    public String getFormattedDuration() {
	return getStringTimeFormat(mp.getDuration());
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#getFormattedTime()
     */
    public String getFormattedTime() {
	return getStringTimeFormat(mp.getMediaTime());
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#getRate()
     */
    public PlayRate getRate() {
	// TODO Auto-generated method stub
	return null;
    }

    // public PlayRate getRate() {
    // TODO Auto-generated method stub
    // return null;
    // }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#getSize()
     */
    public Dimension getSize() {
	return new Dimension((int) vrc.getFrameSize().getWidth(), (int) vrc.getFrameSize().getHeight());
    }

    /**
     * Gets the string time format.
     * 
     * @param time
     *            the time
     * @return the string time format
     */
    private String getStringTimeFormat(double time) {
	try {
	    if (time == 0) {
		return "00:00:00,000";
	    }
	    Calendar currentTime = Calendar.getInstance();
	    currentTime.setTimeInMillis((long) (time * 1000f));
	    NumberFormat formatter = new DecimalFormat("00");
	    NumberFormat miFormatter = new DecimalFormat("000");
	    return String.format("%s:%s:%s,%s", formatter.format(currentTime.get(Calendar.HOUR_OF_DAY) - 1), formatter.format(currentTime.get(Calendar.MINUTE)), formatter.format(currentTime.get(Calendar.SECOND)), miFormatter.format(currentTime.get(Calendar.MILLISECOND)));
	} catch (Exception e) {
	    return "00:00:00,000";
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#getTime()
     */
    public int getTime() {
	return (int) (mp.getMediaTime() * 1000d);
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#getUIComponent()
     */
    public Component getUIComponent() {
	return new MediaPanel(vrc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#isAudioAvailable()
     */
    public boolean isAudioAvailable() {
	// TODO Auto-generated method stub
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#isDone()
     */
    /**
     * Checks if is done.
     * 
     * @return true, if is done
     */
    public boolean isDone() {
	// TODO Auto-generated method stub
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#isMute()
     */
    public boolean isMute() {
	// TODO Auto-generated method stub
	return false;
    }

    // public void setRate(PlayRate rate) {
    // TODO Auto-generated method stub

    // }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#isPlaying()
     */
    public boolean isPlaying() {
	// TODO Auto-generated method stub
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#prepareMedia()
     */
    public void prepareMedia() {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#setMute(boolean)
     */
    public void setMute(boolean muteValue) {
	//

    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#setPlaying(boolean)
     */
    public void setPlaying(boolean playValue) {
	if (playValue) {
	    mp.play();
	} else {
	    mp.pause();
	    mp.setMediaTime(0.0);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#setRate(synergyviewcore.media.model .IMedia.PlayRate)
     */
    public void setRate(PlayRate rate) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#setTime(int)
     */
    public void setTime(int time) {
	mp.setMediaTime(time / 1000d);
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#stepFF()
     */
    public void stepFF() {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.media.model.IMedia#stepRE()
     */
    public void stepRE() {
	// TODO Auto-generated method stub
    }

}

@SuppressWarnings("serial")
class MediaPanel extends JPanel {
    private VideoRenderControl vrc;

    MediaPanel(VideoRenderControl vrc) {
	this.vrc = vrc;
	VideoRendererListener vrl;
	vrl = new VideoRendererListener() {

	    public void videoFrameUpdated(VideoRendererEvent vre) {
		repaint();
	    }
	};

	vrc.addVideoRendererListener(vrl);
	setPreferredSize(new Dimension((int) vrc.getFrameSize().getWidth(), (int) vrc.getFrameSize().getHeight()));
    }

    protected void paintComponent(Graphics g) {

	Graphics2D g2d = (Graphics2D) g;
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	vrc.paintVideoFrame(g2d, new Rectangle(0, 0, getWidth(), getHeight()));
	// vrc.paintVideo(g2d,
	// new Rectangle(0, 0, 640, 480),
	// new Rectangle(0, 0, getWidth(),getHeight()));

    }
}
