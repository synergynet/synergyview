package synergyviewcore.media.gstreamer;

import synergyviewcore.media.VideoFrameData;

public interface IVideoFrameListener {
	public void updateFrame(VideoFrameData videoFrameData);
}
