package synergyviewcore.media;


public interface IMediaPlayer {
	public enum PlayRate { STOP, PULSE, xHalf, X1, X2, X3 };
	public static final String PROP_PLAYRATE = "playRate";
	public void setPlayRate(IMediaPlayer.PlayRate playRate);
	public IMediaPlayer.PlayRate getPlayRate();
	public static final String PROP_POSITION = "position";
	public void setPosition(long milliSec);
	public long getPosition();
	public void dispose();
}
