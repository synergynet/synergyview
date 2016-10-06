package synergyviewmedia.model;

public class VideoMediaTrack extends MediaTrack {
	private static final long serialVersionUID = 1L;
	
	private int width;
	private int height;
	private String videoCodec;
	public void setWidth(int width) {
		this.width = width;
	}
	public int getWidth() {
		return width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getHeight() {
		return height;
	}
	public void setVideoCodec(String videoCodec) {
		this.videoCodec = videoCodec;
	}
	public String getVideoCodec() {
		return videoCodec;
	}
}
