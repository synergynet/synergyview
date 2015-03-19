/*
 *  File: MilliGrid.java 
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
package de.jaret.examples.timebars.millis.swt.renderer;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.strategy.ITickProvider;
import de.jaret.util.ui.timebars.swt.renderer.AbstractGridRenderer;
import de.jaret.util.ui.timebars.swt.renderer.GridRenderer;

/**
 * Simple grid renderer for the milli example.
 * 
 * @author Peter Kliem
 * @version $Id: MilliGrid.java 856 2009-04-02 18:54:40Z kliem $
 */
public class MilliGrid extends AbstractGridRenderer implements GridRenderer {
    /** color of the major grid. */
    private Color _colorMajorGrid;

    /** color of the minor grid. */
    private Color _colorMinorGrid;

    public MilliGrid(Printer printer) {
        super(printer);
        _colorMajorGrid = new Color(printer, 200, 200, 200);
        _colorMinorGrid = new Color(printer, 230, 230, 230);

    }

    public MilliGrid() {
        super(null);
        _colorMajorGrid = new Color(Display.getCurrent(), 200, 200, 200);
        _colorMinorGrid = new Color(Display.getCurrent(), 230, 230, 230);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, TimeBarViewerDelegate delegate, Rectangle drawingArea, boolean printing) {
        int ox = drawingArea.x;
        int oy = drawingArea.y;
        int width = drawingArea.width;
        int height = drawingArea.height;

        Color fg = gc.getForeground();

        // first date
        JaretDate date = delegate.getStartDate().copy();

        while (date.diffMilliSeconds(delegate.getMinDate()) % MilliScale.MAJOR != 0) {
            date.advanceMillis(-1);
        }

        JaretDate save = date.copy();

        date = save.copy();
        // draw the minor grid
        if (printing) {
            gc.setLineWidth(3);
        }
        gc.setForeground(_colorMinorGrid);
        while (delegate.xForDate(date) < ox + width) {
            JaretDate eosc = MilliScale.endOfSpecialScaling(delegate, date);
            if (eosc == null) {
                int x = delegate.xForDate(date);
                gc.drawLine(x, oy, x, oy + height);

                date.advanceMillis(MilliScale.MINOR);
            } else {
                long diff = eosc.diffMilliSeconds(date);
                date.advanceMillis(diff+MilliScale.MINOR);
            }
        }

        date = save.copy();
        // draw the major grid
        gc.setForeground(_colorMajorGrid);
        while (delegate.xForDate(date) < ox + width) {
            JaretDate eosc = MilliScale.endOfSpecialScaling(delegate, date);
            if (eosc == null) {
                int x = delegate.xForDate(date);
                gc.drawLine(x, oy, x, oy + height);
                date.advanceMillis(MilliScale.MAJOR);
            } else {
                long diff = eosc.diffMilliSeconds(date);
                date.advanceMillis(diff+MilliScale.MAJOR);
            }
        }
        gc.setLineWidth(1);
        gc.setForeground(fg);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        _colorMajorGrid.dispose();
        _colorMinorGrid.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public GridRenderer createPrintRenderer(Printer printer) {
        MilliGrid renderer = new MilliGrid(printer);
        return renderer;
    }

    public void setTickProvider(ITickProvider tickProvider) {
        // TODO Auto-generated method stub
        
    }
}
