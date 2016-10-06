package synergyviewcore.media;


public class VideoFrameData {
	private int[] pixels;
	private String caption;
	private int width;
	private int height;
	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}
	public int[] getPixels() {
		return pixels;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getCaption() {
		return caption;
	}
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
}
