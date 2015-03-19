package synergyviewcore.timebar.render;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import synergyviewcore.annotations.ui.MediaClipIntervalImpl;
import synergyviewcore.timebar.model.MediaIntervalImpl;

import de.jaret.util.date.Interval;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2;

/**
 * 
 * Customised Interval Renderer that allows developers to define gradient-enabled background colour
 *
 */

public class TimeBarIntervalRenderer extends RendererBase implements TimeBarRenderer, TimeBarRenderer2 {
	
	protected static final int PREFWIDTH = 5;	  
    protected static final double BORDERFACTOR = 0.2;
    private static int _rounding = 2;

    protected TimeBarViewerDelegate _delegate;
    protected ImageRegistry _imageRegistry;
    
    protected int gradientStartColor = SWT.COLOR_WHITE;
    protected int gradientEndColor = SWT.COLOR_BLUE;
    
    protected Image muteImage;

    public TimeBarIntervalRenderer(Printer printer) {
        super(printer);
    }


    public TimeBarIntervalRenderer() {
        super(null);
        
    }
    
	public Rectangle getPreferredDrawingBounds(Rectangle intervalDrawingArea,
			TimeBarViewerDelegate delegate, Interval interval,
			boolean selected, boolean printing, boolean overlap) {

		if (intervalDrawingArea.width<PREFWIDTH) {
			int diff = (PREFWIDTH-intervalDrawingArea.width/2);
			return new Rectangle(intervalDrawingArea.x-diff, intervalDrawingArea.y, intervalDrawingArea.width+2*diff, intervalDrawingArea.height);
		} else {
			return intervalDrawingArea;
		}
	
	}

    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected,
            boolean printing, boolean overlap) {
        _delegate = delegate;
        
        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;

        drawFocus(gc, drawingArea, delegate, interval, selected, printing, overlap);

        Rectangle iRect = getIRect(horizontal, drawingArea, overlap);

        Color bg = gc.getBackground();
        String str = interval.toString();

        gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));

        Pattern pattern = new Pattern(gc.getDevice(), 0, iRect.y, 0, iRect.y + iRect.height * 2, gc.getDevice()
                .getSystemColor(this.gradientStartColor), gc.getDevice().getSystemColor(this.gradientEndColor));

        gc.setBackgroundPattern(pattern);
        
        if (_rounding == 0) {
            gc.fillRectangle(iRect);
            gc.drawRectangle(iRect);
        } else {
            gc.fillRoundRectangle(iRect.x, iRect.y, iRect.width, iRect.height, _rounding, _rounding);
            gc.drawRoundRectangle(iRect.x, iRect.y, iRect.width, iRect.height, _rounding, _rounding);
        }
        if (horizontal) {
            SwtGraphicsHelper.drawStringCentered(gc, str, iRect);
        } else {
            SwtGraphicsHelper.drawStringVertical(gc, str, iRect.x + 2, iRect.y + 2);
        }
        gc.setBackgroundPattern(null);
        pattern.dispose();

       
        if (selected && !printing) {
            gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
            gc.setAlpha(60);
            if (_rounding == 0) {
                gc.fillRectangle(iRect);
            } else {
                gc.fillRoundRectangle(iRect.x, iRect.y, iRect.width, iRect.height, _rounding, _rounding);
            }
            gc.setAlpha(255);
        }

        gc.setBackground(bg);
        
        if (muteImage==null)
        	muteImage = new Image(gc.getDevice(), TimeBarIntervalRenderer.class.getResourceAsStream("sound_mute.png"));
        
        if (interval instanceof MediaIntervalImpl) {
        	MediaIntervalImpl mediaInterval = (MediaIntervalImpl) interval;
        	if (!mediaInterval.isMediaMute()) return; 
        	gc.drawImage(muteImage, iRect.x+2, iRect.y+2);
        }
        
        if (interval instanceof MediaClipIntervalImpl) {
        	MediaClipIntervalImpl mediaInterval = (MediaClipIntervalImpl) interval;
        	if (!mediaInterval.isMute()) return; 
        	gc.drawImage(muteImage, iRect.x+2, iRect.y+2);
        }
           
    }


    public String getToolTipText(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        return getToolTipText(_delegate, interval, drawingArea, x, y, overlapping);
    }

 
    public boolean contains(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        return contains(_delegate, interval, drawingArea, x, y, overlapping);
    }


    public Rectangle getContainingRectangle(Interval interval, Rectangle drawingArea, boolean overlapping) {
        return getContainingRectangle(_delegate, interval, drawingArea, overlapping);
    }

  
    public TimeBarRenderer createPrintrenderer(Printer printer) {
        TimeBarIntervalRenderer renderer = new TimeBarIntervalRenderer(printer);
        return renderer;
    }

    public void dispose() {
        if (_imageRegistry != null) {
            _imageRegistry.dispose();
        }
        
        if (muteImage!=null){
        	muteImage.dispose();
        	muteImage = null;
        }
    }

    public String getToolTipText(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x,
            int y, boolean overlapping) {
        if (contains(delegate, interval, drawingArea, x, y, overlapping)) {
            return interval.toString();
        }
        return null;
    }

    public boolean contains(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x, int y,
            boolean overlapping) {

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle iRect = getIRect(horizontal, drawingArea, overlapping);
        return iRect.contains(drawingArea.x + x, drawingArea.y + y);
    }

    public Rectangle getContainingRectangle(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea,
            boolean overlapping) {

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle iRect = getIRect(horizontal, drawingArea, overlapping);
        return iRect;
    }

    protected Rectangle getIRect(boolean horizontal, Rectangle drawingArea, boolean overlap) {
        if (horizontal) {
            int borderHeight = (int) (drawingArea.height * BORDERFACTOR / 2);
            int height = drawingArea.height - (overlap ? 0 : 2 * borderHeight);
            int y = drawingArea.y + (overlap ? 0 : borderHeight);
            return new Rectangle(drawingArea.x, y, drawingArea.width - 1, height - 1);
        } else {
            int borderWidth = (int) (drawingArea.width * BORDERFACTOR / 2);
            int width = drawingArea.width - (overlap ? 0 : 2 * borderWidth);
            int x = drawingArea.x + (overlap ? 0 : borderWidth);
            return new Rectangle(x, drawingArea.y, width - 1, drawingArea.height - 1);
        }
    }

  
    protected ImageRegistry getImageRegistry() {    
        return _imageRegistry;
    }

    public static int getRounding() {
        return _rounding;
    }

    public static void setRounding(int rounding) {
        _rounding = rounding;
    }


	public int getGradientStartColor() {
		return gradientStartColor;
	}


	public void setGradientStartColor(int gradientStartColor) {
		this.gradientStartColor = gradientStartColor;
	}


	public int getGradientEndColor() {
		return gradientEndColor;
	}


	public void setGradientEndColor(int gradientEndColor) {
		this.gradientEndColor = gradientEndColor;
	}


	/**
	 * 
	 */
	public void disposeResource() {
		if (muteImage!=null && muteImage.isDisposed()) {
			muteImage.dispose();
			muteImage = null;
		}
			
	}

}
