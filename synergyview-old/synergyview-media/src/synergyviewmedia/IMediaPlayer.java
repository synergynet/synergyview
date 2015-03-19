package synergyviewmedia;

import synergyviewmedia.gsteamer.IVideoFrameListener;

public interface IMediaPlayer {
	public enum PlayRate { STOP, x1 };
	public static final String PROP_PLAYRATE = "playRate";
	public void setPlayRate(IMediaPlayer.PlayRate playRate);
	public IMediaPlayer.PlayRate getPlayRate();
	public static final String PROP_POSITION = "position";
	public void setPosition(long milliSec);
	public long getPosition();
	public void setVideoFrameListener(IVideoFrameListener videoFrameListener);
	public void dispose();
}
