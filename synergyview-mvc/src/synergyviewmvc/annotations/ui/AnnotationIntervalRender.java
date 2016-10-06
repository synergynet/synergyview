/**
 *  File: CaptionIntervalRender.java
 *  Copyright (c) 2010
 *  phyokyaw
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package synergyviewmvc.annotations.ui;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.date.Interval;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2;

/**
 * @author phyokyaw
 *
 */
public class AnnotationIntervalRender extends RendererBase implements TimeBarRenderer, TimeBarRenderer2 {
	
	protected static final int PREFWIDTH = 10;	  
    protected static final double BORDERFACTOR = 0.2;
    private static int _rounding = 3;
    protected TimeBarViewerDelegate _delegate;
    protected ImageRegistry _imageRegistry;
    
    protected int gradientStartColor = SWT.COLOR_WHITE;
    protected int gradientEndColor = SWT.COLOR_GREEN;
    protected int gradientInrangeEndColor = SWT.COLOR_YELLOW;

    public AnnotationIntervalRender(Printer printer) {
        super(printer);
    }

    public AnnotationIntervalRender() {
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
            gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
            gc.setAlpha(90);
            if (_rounding == 0) {
                gc.fillRectangle(iRect);
            } else {
                gc.fillRoundRectangle(iRect.x, iRect.y, iRect.width, iRect.height, _rounding, _rounding);
            }
            gc.setAlpha(255);
        }

        gc.setBackground(bg);
           
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
    	AnnotationIntervalRender renderer = new AnnotationIntervalRender(printer);
        return renderer;
    }

    public void dispose() {
        if (_imageRegistry != null) {
            _imageRegistry.dispose();
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

}

