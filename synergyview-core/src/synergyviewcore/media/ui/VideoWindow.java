package synergyviewcore.media.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import synergyviewcore.media.VideoFrameData;
import synergyviewcore.media.gstreamer.IVideoFrameListener;

public class VideoWindow extends Canvas implements IVideoFrameListener {
	private ImageData imageData;
	private boolean isKeepAspectRatio = true;
	private PaintListener videoImagePaintListener = new PaintListener() {
		@Override
		public void paintControl(PaintEvent e) {
			if (imageData != null)
			{
				drawVideoFrame(e.gc);
			} else {
				drawBlank(e.gc);
			}
		}
	};
	
	public VideoWindow(final Composite parent, int style)  {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		this.addPaintListener(videoImagePaintListener);
	}


	@Override
	public void updateFrame(VideoFrameData videoFrameData) {
		if (isDisposed())
			return;
		synchronized(videoFrameData) {
			if (imageData == null) {
				imageData =  new ImageData(videoFrameData.getWidth(), videoFrameData.getHeight(), 24,
						new PaletteData(0xFF0000, 0x00FF00, 0x0000FF));
			}
			imageData.setPixels(0, 0, videoFrameData.getHeight() * videoFrameData.getWidth(), videoFrameData.getPixels(), 0);
		}
		if (!isDisposed())
			getDisplay().asyncExec(update);
	}
	
	public void setKeepAspectRatio(boolean isKeepAspectRatio) {
		this.isKeepAspectRatio = isKeepAspectRatio;
	}

	public boolean isKeepAspectRatio() {
		return isKeepAspectRatio;
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}

	private final Runnable update = new Runnable() {
		public void run() {
			if (!isDisposed()) 
				redraw();
		}
	};

	private void drawVideoFrame(GC graphicsContext) {

		Point cSize = getSize();
		int newX = 0, newY = 0;
		synchronized (imageData) {
			int sizeX = cSize.x, sizeY = cSize.y;
			if ((imageData.width != cSize.x) || (imageData.height != cSize.y)) {
				graphicsContext.setInterpolation(SWT.HIGH);
				graphicsContext.setAdvanced(false);
				if (isKeepAspectRatio()) {
					
					if (((float) imageData.width / (float) cSize.x)
						> ((float) imageData.height / (float) cSize.y)) {
						sizeY = cSize.x * imageData.height / imageData.width;
						newY = (cSize.y - sizeY) / 2;
					} else {
						sizeX = cSize.y * imageData.width / imageData.height;
						newX = (cSize.x - sizeX) / 2;
					}
				}
			}
			Image image = new Image(getDisplay(), imageData);
			//TODO Draw black around the video frame
			graphicsContext.drawImage(image, 0, 0, imageData.width, imageData.height, newX, newY, sizeX, sizeY);
			image.dispose();
			graphicsContext.dispose();
		}
	}
	
	private void drawBlank(GC graphicsContext) {
		graphicsContext.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK)); 
		graphicsContext.fillRectangle(0, 0, getSize().x, getSize().y);
		graphicsContext.dispose();
	}
}
