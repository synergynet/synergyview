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

public class JMCMedia extends AbstractMedia {

	private MediaProvider mp;
	private VideoRenderControl vrc;
	
	public JMCMedia(URI mediaUrl, String name) {
		super(mediaUrl, name);
		mp = new MediaProvider(mediaUrl);
		vrc = mp.getControl(VideoRenderControl.class);
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public int getDuration() {
		return  (int) (mp.getDuration() * 1000d);
	}

	public String getFormattedDuration() {
		return getStringTimeFormat(mp.getDuration());
	}
	

	private String getStringTimeFormat(double time) {
		try {
			if (time == 0)
				return "00:00:00,000";
			Calendar currentTime = Calendar.getInstance();
	    	currentTime.setTimeInMillis((long)(time * 1000f));
	    	NumberFormat formatter = new DecimalFormat("00");
			NumberFormat miFormatter = new DecimalFormat("000");
			return String.format("%s:%s:%s,%s", formatter.format(currentTime.get(Calendar.HOUR_OF_DAY)-1),formatter.format(currentTime.get(Calendar.MINUTE)),formatter.format(currentTime.get(Calendar.SECOND)),miFormatter.format(currentTime.get(Calendar.MILLISECOND)));
    	}
	    catch(Exception e) {
	    	return "00:00:00,000";
	    }
	}

	public String getFormattedTime() {
		return getStringTimeFormat(mp.getMediaTime());
	}

	//public PlayRate getRate() {
		// TODO Auto-generated method stub
		//return null;
	//}

	public Dimension getSize() {
		return new Dimension((int) vrc.getFrameSize().getWidth(),(int)vrc.getFrameSize().getHeight());
	}

	public int getTime() {
		return (int) (mp.getMediaTime() * 1000d);
	}

	public Component getUIComponent() {
		return new MediaPanel(vrc);
	}

	public boolean isAudioAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isMute() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setPlaying(boolean playValue) {
		if (playValue)
			mp.play();
		else {
			mp.pause();
			mp.setMediaTime(0.0);
		}

	}

	public void setMute(boolean muteValue) {
		//

	}

	//public void setRate(PlayRate rate) {
		// TODO Auto-generated method stub

	//}

	public void setTime(int time) {
		mp.setMediaTime(time / 1000d);
	}

	public void stepFF() {
		// TODO Auto-generated method stub

	}

	public void stepRE() {
		// TODO Auto-generated method stub
	}

	public PlayRate getRate() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRate(PlayRate rate) {
		// TODO Auto-generated method stub
		
	}

	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.media.model.IMedia#isDone()
	 */
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	public void prepareMedia() {
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
        
        vrc.paintVideoFrame(g2d, new Rectangle(0, 0, getWidth(),getHeight()));
        //vrc.paintVideo(g2d,
                //new Rectangle(0, 0, 640, 480),
                //new Rectangle(0, 0, getWidth(),getHeight()));
        
    }
}
