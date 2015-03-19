/*
 *  File: DefaultRenderer.java 
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
package de.jaret.util.ui.timebars.swt.renderer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.util.date.Interval;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;

/**
 * DefaultRenderer for the TimeBarViewer widget. It renders intervals as grey/blue bars.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultRenderer.java 802 2008-12-28 12:30:41Z kliem $
 */
public class DefaultRenderer extends AbstractTimeBarRenderer {
    /** width or height times this factor = perentage used as the non painted border. */
    protected static final double BORDERFACTOR = 0.2;

    /** cache for the delegate supplying the orientation information. */
    protected TimeBarViewerDelegate _delegate;

    /**
     * Create renderer for printing.
     * 
     * @param printer printer device
     */
    public DefaultRenderer(Printer printer) {
        super(printer);
    }

    /**
     * Construct renderer for screen use.
     * 
     */
    public DefaultRenderer() {
        super(null);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected,
            boolean printing, boolean overlap) {
        _delegate = delegate;
        if (!printing) {
            defaultDraw(gc, drawingArea, delegate, interval, selected, printing, overlap);
        } else {
            print(gc, drawingArea, delegate, interval, overlap);
        }
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
        DefaultRenderer renderer = new DefaultRenderer(printer);
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
        // draw focus
        drawFocus(gc, drawingArea, delegate, interval, selected, printing, overlap);

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle iRect = getIRect(horizontal, drawingArea, overlap);

        Color bg = gc.getBackground();
        String str = interval.toString();

        if (!selected) {
            gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
        } else {
            gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        }
        gc.fillRectangle(iRect);
        gc.drawRectangle(iRect);
        SwtGraphicsHelper.drawStringCentered(gc, str, iRect);

        gc.setBackground(bg);
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x,
            int y, boolean overlapping) {
        if (contains(delegate, interval, drawingArea, x, y, overlapping)) {
            return interval.toString();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x, int y,
            boolean overlapping) {

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle iRect = getIRect(horizontal, drawingArea, overlapping);
        return iRect.contains(drawingArea.x + x, drawingArea.y + y);
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getContainingRectangle(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea,
            boolean overlapping) {

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle iRect = getIRect(horizontal, drawingArea, overlapping);
        return iRect;
    }

    /**
     * Print the rendered interval.
     * @param gc GC
     * @param drawingArea area of the interval
     * @param delegate the delegate
     * @param interval the interval
     * @param overlap true if overlapping
     */
    private void print(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean overlap) {
        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle iRect = getIRect(horizontal, drawingArea, overlap);

        Color bg = gc.getBackground();
        gc.setLineWidth(getDefaultLineWidth());
        gc.setBackground(_printer.getSystemColor(SWT.COLOR_GRAY));
        gc.fillRectangle(iRect);
        gc.drawRectangle(iRect);
        String str = interval.toString();
        gc.setLineWidth(1);
        SwtGraphicsHelper.drawStringCentered(gc, str, iRect);

        gc.setBackground(bg);
    }

    /**
     * Calculate the actual drawing rectangle for the interval usig the BORDERFACTOR to determine the border.
     * 
     * @param horizontal true for horizontal false for vertical
     * @param drawingArea drawingArea
     * @param overlap true if it is an overlapping interval
     * @return the actual drawing rectangle
     */
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

}
