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
 * Customised Interval Renderer that allows developers to define gradient-enabled background colour.
 */

public class TimeBarIntervalRenderer extends RendererBase implements TimeBarRenderer, TimeBarRenderer2 {

    /** The _rounding. */
    private static int _rounding = 2;

    /** The Constant BORDERFACTOR. */
    protected static final double BORDERFACTOR = 0.2;

    /** The Constant PREFWIDTH. */
    protected static final int PREFWIDTH = 5;

    /**
     * Gets the rounding.
     * 
     * @return the rounding
     */
    public static int getRounding() {
	return _rounding;
    }

    /**
     * Sets the rounding.
     * 
     * @param rounding
     *            the new rounding
     */
    public static void setRounding(int rounding) {
	_rounding = rounding;
    }

    /** The _delegate. */
    protected TimeBarViewerDelegate _delegate;

    /** The _image registry. */
    protected ImageRegistry _imageRegistry;

    /** The gradient end color. */
    protected int gradientEndColor = SWT.COLOR_BLUE;

    /** The gradient start color. */
    protected int gradientStartColor = SWT.COLOR_WHITE;

    /** The mute image. */
    protected Image muteImage;

    /**
     * Instantiates a new time bar interval renderer.
     */
    public TimeBarIntervalRenderer() {
	super(null);

    }

    /**
     * Instantiates a new time bar interval renderer.
     * 
     * @param printer
     *            the printer
     */
    public TimeBarIntervalRenderer(Printer printer) {
	super(printer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer#contains(de.jaret .util.date.Interval, org.eclipse.swt.graphics.Rectangle, int, int, boolean)
     */
    public boolean contains(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
	return contains(_delegate, interval, drawingArea, x, y, overlapping);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2#contains(de.jaret .util.ui.timebars.TimeBarViewerDelegate, de.jaret.util.date.Interval, org.eclipse.swt.graphics.Rectangle, int, int, boolean)
     */
    public boolean contains(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {

	boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
	Rectangle iRect = getIRect(horizontal, drawingArea, overlapping);
	return iRect.contains(drawingArea.x + x, drawingArea.y + y);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer#createPrintrenderer (org.eclipse.swt.printing.Printer)
     */
    public TimeBarRenderer createPrintrenderer(Printer printer) {
	TimeBarIntervalRenderer renderer = new TimeBarIntervalRenderer(printer);
	return renderer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer#dispose()
     */
    public void dispose() {
	if (_imageRegistry != null) {
	    _imageRegistry.dispose();
	}

	if (muteImage != null) {
	    muteImage.dispose();
	    muteImage = null;
	}
    }

    /**
     * Dispose resource.
     */
    public void disposeResource() {
	if ((muteImage != null) && muteImage.isDisposed()) {
	    muteImage.dispose();
	    muteImage = null;
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer#draw(org.eclipse .swt.graphics.GC, org.eclipse.swt.graphics.Rectangle, de.jaret.util.ui.timebars.TimeBarViewerDelegate, de.jaret.util.date.Interval, boolean, boolean, boolean)
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected, boolean printing, boolean overlap) {
	_delegate = delegate;

	boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;

	drawFocus(gc, drawingArea, delegate, interval, selected, printing, overlap);

	Rectangle iRect = getIRect(horizontal, drawingArea, overlap);

	Color bg = gc.getBackground();
	String str = interval.toString();

	gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));

	Pattern pattern = new Pattern(gc.getDevice(), 0, iRect.y, 0, iRect.y + (iRect.height * 2), gc.getDevice().getSystemColor(this.gradientStartColor), gc.getDevice().getSystemColor(this.gradientEndColor));

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

	if (muteImage == null) {
	    muteImage = new Image(gc.getDevice(), TimeBarIntervalRenderer.class.getResourceAsStream("sound_mute.png"));
	}

	if (interval instanceof MediaIntervalImpl) {
	    MediaIntervalImpl mediaInterval = (MediaIntervalImpl) interval;
	    if (!mediaInterval.isMediaMute()) {
		return;
	    }
	    gc.drawImage(muteImage, iRect.x + 2, iRect.y + 2);
	}

	if (interval instanceof MediaClipIntervalImpl) {
	    MediaClipIntervalImpl mediaInterval = (MediaClipIntervalImpl) interval;
	    if (!mediaInterval.isMute()) {
		return;
	    }
	    gc.drawImage(muteImage, iRect.x + 2, iRect.y + 2);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer#getContainingRectangle (de.jaret.util.date.Interval, org.eclipse.swt.graphics.Rectangle, boolean)
     */
    public Rectangle getContainingRectangle(Interval interval, Rectangle drawingArea, boolean overlapping) {
	return getContainingRectangle(_delegate, interval, drawingArea, overlapping);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2# getContainingRectangle(de.jaret.util.ui.timebars.TimeBarViewerDelegate, de.jaret.util.date.Interval, org.eclipse.swt.graphics.Rectangle, boolean)
     */
    public Rectangle getContainingRectangle(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, boolean overlapping) {

	boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
	Rectangle iRect = getIRect(horizontal, drawingArea, overlapping);
	return iRect;
    }

    /**
     * Gets the gradient end color.
     * 
     * @return the gradient end color
     */
    public int getGradientEndColor() {
	return gradientEndColor;
    }

    /**
     * Gets the gradient start color.
     * 
     * @return the gradient start color
     */
    public int getGradientStartColor() {
	return gradientStartColor;
    }

    /**
     * Gets the image registry.
     * 
     * @return the image registry
     */
    protected ImageRegistry getImageRegistry() {
	return _imageRegistry;
    }

    /**
     * Gets the i rect.
     * 
     * @param horizontal
     *            the horizontal
     * @param drawingArea
     *            the drawing area
     * @param overlap
     *            the overlap
     * @return the i rect
     */
    protected Rectangle getIRect(boolean horizontal, Rectangle drawingArea, boolean overlap) {
	if (horizontal) {
	    int borderHeight = (int) ((drawingArea.height * BORDERFACTOR) / 2);
	    int height = drawingArea.height - (overlap ? 0 : 2 * borderHeight);
	    int y = drawingArea.y + (overlap ? 0 : borderHeight);
	    return new Rectangle(drawingArea.x, y, drawingArea.width - 1, height - 1);
	} else {
	    int borderWidth = (int) ((drawingArea.width * BORDERFACTOR) / 2);
	    int width = drawingArea.width - (overlap ? 0 : 2 * borderWidth);
	    int x = drawingArea.x + (overlap ? 0 : borderWidth);
	    return new Rectangle(x, drawingArea.y, width - 1, drawingArea.height - 1);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2# getPreferredDrawingBounds(org.eclipse.swt.graphics.Rectangle, de.jaret.util.ui.timebars.TimeBarViewerDelegate, de.jaret.util.date.Interval, boolean, boolean, boolean)
     */
    public Rectangle getPreferredDrawingBounds(Rectangle intervalDrawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected, boolean printing, boolean overlap) {

	if (intervalDrawingArea.width < PREFWIDTH) {
	    int diff = (PREFWIDTH - (intervalDrawingArea.width / 2));
	    return new Rectangle(intervalDrawingArea.x - diff, intervalDrawingArea.y, intervalDrawingArea.width + (2 * diff), intervalDrawingArea.height);
	} else {
	    return intervalDrawingArea;
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer#getToolTipText (de.jaret.util.date.Interval, org.eclipse.swt.graphics.Rectangle, int, int, boolean)
     */
    public String getToolTipText(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
	return getToolTipText(_delegate, interval, drawingArea, x, y, overlapping);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2#getToolTipText (de.jaret.util.ui.timebars.TimeBarViewerDelegate, de.jaret.util.date.Interval, org.eclipse.swt.graphics.Rectangle, int, int, boolean)
     */
    public String getToolTipText(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
	if (contains(delegate, interval, drawingArea, x, y, overlapping)) {
	    return interval.toString();
	}
	return null;
    }

    /**
     * Sets the gradient end color.
     * 
     * @param gradientEndColor
     *            the new gradient end color
     */
    public void setGradientEndColor(int gradientEndColor) {
	this.gradientEndColor = gradientEndColor;
    }

    /**
     * Sets the gradient start color.
     * 
     * @param gradientStartColor
     *            the new gradient start color
     */
    public void setGradientStartColor(int gradientStartColor) {
	this.gradientStartColor = gradientStartColor;
    }

}
