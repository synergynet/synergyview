/*
 *  File: LowerGridRenderer.java 
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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.util.date.DateUtils;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TickScaler;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.TickScaler.Range;
import de.jaret.util.ui.timebars.strategy.ITickProvider;
import de.jaret.util.ui.timebars.swt.renderer.AbstractGridRenderer;
import de.jaret.util.ui.timebars.swt.renderer.GridRenderer;

/**
 * Grid renderer for the lower viewer.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultGridRenderer.java 556 2007-09-04 22:07:59Z olk $
 */
public class LowerGridRenderer extends AbstractGridRenderer implements GridRenderer {
    /** default color for the major grid (r,g,b). */
    private static final RGB MAJORGRID_COLOR = new RGB(200, 200, 200);
    /** default color for the minor grid (r,g,b). */
    private static final RGB MINORGRID_COLOR = new RGB(230, 230, 230);
    private static final RGB MARK_COLOR = new RGB(255, 200, 200);

    /** linewidth when printing. */
    private static final int PRINTING_LINEWIDTH = 3;
    
    
    /** color of the major grid. */
    private Color _colorMajorGrid;

    /** color of the minor grid. */
    private Color _colorMinorGrid;

    private Color _colorMark;

    private JaretDate _startMark;
    private JaretDate _endMark;
    

    /**
     * Create a DefaultGridRenderer for a printer.
     * 
     * @param printer printer device
     */
    public LowerGridRenderer(Printer printer) {
        super(printer);
        _colorMajorGrid = new Color(printer, MAJORGRID_COLOR);
        _colorMinorGrid = new Color(printer,  MINORGRID_COLOR);
        _colorMark = new Color(printer, MARK_COLOR);
    }

    /**
     * Create a DefaultGridRenderer for the screen.
     */
    public LowerGridRenderer() {
        super(null);
        _colorMajorGrid = new Color(Display.getCurrent(), MAJORGRID_COLOR);
        _colorMinorGrid = new Color(Display.getCurrent(),  MINORGRID_COLOR);
        _colorMark = new Color(Display.getCurrent(), MARK_COLOR);
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

        int idx;
        if (!printing) {
            idx = TickScaler.getTickIdx(delegate.getPixelPerSecond() / getScaleX());
        } else {
            idx = TickScaler.getTickIdx(delegate.getPixelPerSecond() / getScaleX());
        }
        int majTick = TickScaler.getMajorTickMinutes(idx);
        int minTick = TickScaler.getMinorTickMinutes(idx);
        Range range = TickScaler.getRange(idx);

        // first date
        JaretDate date = delegate.getStartDate().copy();

        // clean starting date on a major tick minute position (starting with a
        // day)
        date.setMinutes(0);
        date.setHours(0);
        date.setSeconds(0);
        // if range is week take a week starting point
        if (range == Range.WEEK) {
            while (date.getDayOfWeek() != DateUtils.getFirstDayOfWeek()) {
                date.backDays(1);
            }
        } else if (range == Range.MONTH) {
            // month -> month starting point
            date.setDay(1);
        }
        JaretDate save = date.copy();

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        int max = horizontal ? (ox + width):(oy + height);
        
        Color bg = gc.getBackground();
        
        if (_startMark != null && _endMark != null) {
            int startX = delegate.xForDate(_startMark);
            int endX = delegate.xForDate(_endMark);
            gc.setBackground(_colorMark);
            gc.fillRectangle(startX, oy, endX-startX, height);
        }
        

            gc.setBackground(bg);

        date = save.copy();
        // draw the minor grid
        if (printing) {
            gc.setLineWidth(PRINTING_LINEWIDTH);
        }
        gc.setForeground(_colorMinorGrid);
        while (delegate.xForDate(date) < max) {
            int x = delegate.xForDate(date);
            if (horizontal) {
                gc.drawLine(x, oy, x, oy + height);
            } else {
                gc.drawLine(ox, x, ox+width, x);
            }

            if (range == Range.MONTH) {
                int adv = Math.round(minTick / (24 * 60 * 7 * 4));
                if (adv == 0) {
                    adv = 1;
                }
                date.advanceMonths(adv);
            } else {
                date.advanceMinutes(minTick);
            }
        }

        date = save.copy();
        // draw the major grid
        gc.setForeground(_colorMajorGrid);
        while (delegate.xForDate(date) < ox + width) {
            int x = delegate.xForDate(date);
            if (horizontal) {
                gc.drawLine(x, oy, x, oy + height);
            } else {
                gc.drawLine(ox, x, ox+width, x);
            }
            if (range == Range.MONTH) {
                int adv = Math.round(majTick / (24 * 60 * 7 * 4));
                if (adv == 0) {
                    adv = 1;
                }
                date.advanceMonths(adv);
            } else {
                date.advanceMinutes(majTick);
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
        _colorMark.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public GridRenderer createPrintRenderer(Printer printer) {
        LowerGridRenderer renderer = new LowerGridRenderer(printer);
        return renderer;
    }

    public JaretDate getStartMark() {
        return _startMark;
    }

    public void setStartMark(JaretDate startMark) {
        _startMark = startMark;
    }

    public JaretDate getEndMark() {
        return _endMark;
    }

    public void setEndMark(JaretDate endMark) {
        _endMark = endMark;
    }

    public void setTickProvider(ITickProvider tickProvider) {
        // TODO Auto-generated method stub
        
    }
}
