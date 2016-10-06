/*
 *  File: LineChartHeaderRenderer.java 
 *  Copyright (c) 2004-2008  Peter Kliem (Peter.Kliem@jaret.de)
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;
import de.jaret.util.ui.timebars.swt.renderer.HeaderRenderer;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;

/**
 * Simple header renderer for the linechart example. Draws the lbels for the value markers.
 * 
 * @author Peter Kliem
 * @version $Id: LineChartHeaderRenderer.java 801 2008-12-27 22:44:54Z kliem $
 */
public class LineChartHeaderRenderer extends RendererBase implements HeaderRenderer {
    /** line width when printing. */
    private static final int PRINTING_LINEWIDTH = 3;

    /**
     * Constructor for printing use.
     * 
     * @param printer printing device
     */
    public LineChartHeaderRenderer(Printer printer) {
        super(printer);
    }

    /**
     * Constructor for screen use.
     * 
     */
    public LineChartHeaderRenderer() {
        super(null);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, TimeBarRowHeader header,
            boolean selected, boolean printing) {
        Color bg = gc.getBackground();

        gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(gc.getClipping());

        // draw lines for 10, 50, 90
        Color fg = gc.getForeground();
        gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
        gc.setLineStyle(SWT.LINE_DASH);
        // 10
        int ly = LineChartRenderer.yForValue(drawingArea, 10);
        gc.drawLine(0, ly, gc.getClipping().x + gc.getClipping().width, ly);
        gc.drawString("10", 0, ly);
        // 50
        ly = LineChartRenderer.yForValue(drawingArea, 50);
        gc.drawLine(0, ly, gc.getClipping().x + gc.getClipping().width, ly);
        gc.drawString("50", 0, ly);
        // 90
        ly = LineChartRenderer.yForValue(drawingArea, 90);
        gc.drawLine(0, ly, gc.getClipping().x + gc.getClipping().width, ly);
        gc.drawString("90", 0, ly);

        gc.setLineStyle(SWT.LINE_SOLID);
        gc.setForeground(fg);

        gc.setBackground(bg);
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(TimeBarRow row, Rectangle drawingArea, int x, int y) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Rectangle drawingArea, int x, int y) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        // nothing to dispose
    }

    /**
     * {@inheritDoc}
     */
    public HeaderRenderer createPrintRenderer(Printer printer) {
        return new LineChartHeaderRenderer(printer);
    }
}
