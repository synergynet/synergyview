/*
 *  File: DetailEventRenderer.java 
 *  Copyright (c) 2004-2007  Peter Kliem (Peter.Kliem@jaret.de)
 *  A commercial license is available, see http://www.jaret.de.
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
package de.jaret.examples.timebars.timeline.swt.renderer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.examples.timebars.timeline.model.TimelineEvent;
import de.jaret.util.date.Interval;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2;

/**
 * Renderer rendering a point in time as a simple diamod and a label (using extended painting area).
 * 
 * @author Peter Kliem
 * @version $Id: FancyEventRenderer.java 565 2007-09-16 13:25:48Z olk $
 */
public class DetailEventRenderer extends RendererBase implements TimeBarRenderer, TimeBarRenderer2 {
    /** size of the drawn element. */
    private static final int SIZE = 5;
    /** extend for the label. */
    private static final int MAXLABELWIDTH = 200;
    /** pixeloffset for the label drawing. */
    private static final int LABELOFFSET = 3;

    /** corrected size (for printing). */
    private int _size = SIZE;

    /** cache for the delegate supplying the orientation information. */
    protected TimeBarViewerDelegate _delegate;

    /**
     * Create renderer for printing.
     * 
     * @param printer printer device
     */
    public DetailEventRenderer(Printer printer) {
        super(printer);
        _size = scaleX(SIZE);
    }

    /**
     * Construct renderer for screen use.
     * 
     */
    public DetailEventRenderer() {
        super(null);
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getPreferredDrawingBounds(Rectangle intervalDrawingArea, TimeBarViewerDelegate delegate,
            Interval interval, boolean selected, boolean printing, boolean overlap) {

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        if (horizontal) {
            return new Rectangle(intervalDrawingArea.x - _size, intervalDrawingArea.y, intervalDrawingArea.width + 2
                    * _size + scaleX(MAXLABELWIDTH), intervalDrawingArea.height);
        } else {
            return new Rectangle(intervalDrawingArea.x, intervalDrawingArea.y - _size, intervalDrawingArea.width,
                    intervalDrawingArea.height + 2 * _size + scaleY(MAXLABELWIDTH));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected,
            boolean printing, boolean overlap) {
        _delegate = delegate;
        defaultDraw(gc, drawingArea, delegate, interval, selected, printing, overlap);
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        return getToolTipText(_delegate, interval, drawingArea, x, y, overlapping);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        return contains(_delegate, interval, drawingArea, x, y, overlapping);
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getContainingRectangle(Interval interval, Rectangle drawingArea, boolean overlapping) {
        return getContainingRectangle(_delegate, interval, drawingArea, overlapping);
    }

    /**
     * {@inheritDoc}. Will create print renderes for all registered renderers.
     */
    public TimeBarRenderer createPrintrenderer(Printer printer) {
        DetailEventRenderer renderer = new DetailEventRenderer(printer);
        return renderer;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
    }

    /**
     * Drawing method for default rendering.
     * 
     * @param gc GC
     * @param drawingArea drawingArea
     * @param delegate delegate
     * @param interval interval to draw
     * @param selected true for selected drawing
     * @param printing true for printing
     * @param overlap true if the interval overlaps with other intervals
     */
    private void defaultDraw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval,
            boolean selected, boolean printing, boolean overlap) {

        TimelineEvent event = (TimelineEvent) interval;

        
        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle da = getDrawingRect(drawingArea, horizontal);

        // draw focus
        drawFocus(gc, da, delegate, interval, selected, printing, overlap);

        Color bg = gc.getBackground();

        // draw the duration if the event has one
        if (event.getSeconds()>0) {
            gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_CYAN));
            gc.fillRectangle(drawingArea);
        }
        
        
        // draw the diamond
        gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
        if (event.getColor() != null && event.getColor().equals("red")) {
            gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_RED));
        }
        
        
        int[] points = new int[] {da.x, da.y + da.height / 2, da.x + da.width / 2, da.y, da.x + da.width,
                da.y + da.height / 2, da.x + da.width / 2, da.y + da.height};

        gc.fillPolygon(points);
        gc.drawPolygon(points);

        if (selected) {
            gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
            gc.setAlpha(60);
            gc.fillPolygon(points);
            gc.setAlpha(255);
        }
        gc.setBackground(bg);


        // draw the label
        if (horizontal) {
            SwtGraphicsHelper.drawStringVCentered(gc, event.getTitle(), da.x + da.width + scaleX(LABELOFFSET), da.y, da.y
                    + da.height);
        } else {
            SwtGraphicsHelper.drawStringCentered(gc, event.getTitle(), da.x + da.width / 2, da.y + da.height
                    + scaleY(LABELOFFSET) + gc.textExtent(event.getTitle()).y);
        }

    }

    /**
     * Calculate the drawing area for the marking symbol.
     * 
     * @param drawingArea drawing area as given for the time
     * @return Rectangle for drawing the main symbol
     */
    private Rectangle getDrawingRect(Rectangle drawingArea, boolean horizontal) {
        if (horizontal) {
            int y = drawingArea.y + (drawingArea.height - 2 * _size) / 2;
            return new Rectangle(drawingArea.x - _size, y, 2 * _size, 2 * _size);
        } else {
            int x = drawingArea.x + (drawingArea.width - 2 * _size) / 2;
            return new Rectangle(x, drawingArea.y - _size, 2 * _size, 2 * _size);
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
        Rectangle da = getDrawingRect(drawingArea, horizontal);
        return da.contains(drawingArea.x + x, drawingArea.y + y);
    }

    public Rectangle getContainingRectangle(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea,
            boolean overlapping) {
        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle da = getDrawingRect(drawingArea, horizontal);
        return da;
    }

}
