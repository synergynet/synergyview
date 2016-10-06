/*
 *  File: LineChartRenderer.java 
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
package de.jaret.examples.timebars.linechart.swt.renderer;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.examples.timebars.linechart.model.DataPoint;
import de.jaret.examples.timebars.linechart.model.LineChartInterval;
import de.jaret.examples.timebars.linechart.model.ModelCreator;
import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;

/**
 * renderer rendering a line chart in a LineChartInterval.
 * 
 * @author Peter Kliem
 * @version $Id: LineChartRenderer.java 766 2008-05-28 21:36:48Z kliem $
 */
public class LineChartRenderer extends RendererBase implements TimeBarRenderer {

    /**
     * Create renderer for printing.
     * 
     * @param printer printer device
     */
    public LineChartRenderer(Printer printer) {
        super(printer);
    }

    /**
     * Construct renderer for screen use.
     * 
     */
    public LineChartRenderer() {
        super(null);
    }

    /**
     * Calculate y value for a given value in the line chart example.
     * 
     * @param drawingArea drawing area (of which the height is needed)
     * @param value value to project
     * @return projected y coordinate
     */
    public static int yForValue(Rectangle drawingArea, int value) {
        double vForPix = (double) drawingArea.height / ModelCreator.MAX;
        return drawingArea.y + drawingArea.height - (int) (vForPix * value);
    }

    /**
     * Calculate the value represented by an y coordinate in the line chart example.
     * 
     * @param drawingArea drawing area as the base for the projection
     * @param y y coordinate
     * @return the value
     */
    public static int valueForY(Rectangle drawingArea, int y) {
        double vForPix = (double) drawingArea.height / ModelCreator.MAX;
        int off = drawingArea.height - y;
        return (int) ((double) off / vForPix);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected,
            boolean printing, boolean overlap) {

        Color bg = gc.getBackground();

        gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(gc.getClipping());

        // draw lines for 10, 50, 90
        Color fg = gc.getForeground();
        gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
        gc.setLineStyle(SWT.LINE_DASH);
        // 10
        int ly = yForValue(drawingArea, 10);
        gc.drawLine(0, ly, gc.getClipping().x + gc.getClipping().width, ly);
        // 50
        ly = yForValue(drawingArea, 50);
        gc.drawLine(0, ly, gc.getClipping().x + gc.getClipping().width, ly);
        // 90
        ly = yForValue(drawingArea, 90);
        gc.drawLine(0, ly, gc.getClipping().x + gc.getClipping().width, ly);

        gc.setLineStyle(SWT.LINE_SOLID);
        gc.setForeground(fg);

        // get all points to be drawn
        LineChartInterval lci = (LineChartInterval) interval;

        // get the data points to draw
        // since the drawing of the connecting lines will fail due to the scroll optimization take some more points
        // on each side
        List<DataPoint> points = lci.getDataPoints(delegate.getStartDate().copy().backHours(3), delegate.getEndDate()
                .copy().advanceHours(3));

        Point last = null;

        for (DataPoint dataPoint : points) {
            int x = delegate.xForDate(dataPoint.getTime());
            int y = yForValue(drawingArea, dataPoint.getValue());

            if (last != null) {
                gc.drawLine(last.x, last.y, x, y);
            }
            last = new Point(x, y);
            drawPoint(gc, x, y);
        }
        gc.setBackground(bg);
    }

    private void drawPoint(GC gc, int x, int y) {
        int size = 3;
        Color fg = gc.getForeground();
        gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_MAGENTA));

        gc.drawLine(x - size, y - size, x + size, y + size);
        gc.drawLine(x - size, y + size, x + size, y - size);

        gc.setForeground(fg);
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        // return the value
        return "" + valueForY(drawingArea, y);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getContainingRectangle(Interval interval, Rectangle drawingArea, boolean overlapping) {
        return drawingArea;
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarRenderer createPrintrenderer(Printer printer) {
        return new LineChartRenderer(printer);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
    }

}
