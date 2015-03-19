/*
 *  File: CalendarGridRenderer.java 
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
package de.jaret.examples.timebars.calendar.swt.renderer;

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
 * Grid renderer for the calendar example, dividing the day in work and non work parts..
 * 
 * @author Peter Kliem
 * @version $Id: CalendarGridRenderer.java 856 2009-04-02 18:54:40Z kliem $
 */
public class CalendarGridRenderer extends AbstractGridRenderer implements GridRenderer {
    /** default color for the major grid (r,g,b). */
    private static final RGB MAJORGRID_COLOR = new RGB(200, 200, 200);
    /** default color for the minor grid (r,g,b). */
    private static final RGB MINORGRID_COLOR = new RGB(230, 230, 230);
    /** default color for saturdays (r,g,b). */
    private static final RGB WORK_COLOR = new RGB(246, 249, 169);
    /** default color for sundays (r,g,b). */
    private static final RGB NONWORK_COLOR = new RGB(214, 218, 104);

    /** linewidth when printing. */
    private static final int PRINTING_LINEWIDTH = 3;

    /** color of the major grid. */
    private Color _colorMajorGrid;

    /** color of the minor grid. */
    private Color _colorMinorGrid;

    /** color for saturdays. */
    private Color _colorWork;

    /** color for sundays. */
    private Color _colorNonWork;

    private int _beginWorkHour = 8;
    private int _endWorkHour = 18;

    /**
     * Create a DefaultGridRenderer for a printer.
     * 
     * @param printer printer device
     */
    public CalendarGridRenderer(Printer printer) {
        super(printer);
        _colorMajorGrid = new Color(printer, MAJORGRID_COLOR);
        _colorMinorGrid = new Color(printer, MINORGRID_COLOR);
        _colorWork = new Color(printer, WORK_COLOR);
        _colorNonWork = new Color(printer, NONWORK_COLOR);
    }

    /**
     * Create a DefaultGridRenderer for the screen.
     */
    public CalendarGridRenderer() {
        super(null);
        _colorMajorGrid = new Color(Display.getCurrent(), MAJORGRID_COLOR);
        _colorMinorGrid = new Color(Display.getCurrent(), MINORGRID_COLOR);
        _colorWork = new Color(Display.getCurrent(), WORK_COLOR);
        _colorNonWork = new Color(Display.getCurrent(), NONWORK_COLOR);
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

        int idx = TickScaler.getTickIdx(delegate.getPixelPerSecond() / getScaleX());
        int majTick = TickScaler.getMajorTickMinutes(idx);
        int minTick = TickScaler.getMinorTickMinutes(idx);
        Range range = TickScaler.getRange(idx);

        // first date
        JaretDate date = delegate.getStartDate().copy();
        // TODO
        if (date.getHours() == 23 && date.getMinutes() == 59) {
            date.advanceSeconds(1);
        }

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
        int max = horizontal ? (ox + width) : (max = oy + height);

        date = save.copy();
        Color bg = gc.getBackground();
        int end = delegate.xForDate(date.setTime(_beginWorkHour, 0, 0));
        int end2 = delegate.xForDate(date.setTime(_endWorkHour, 0, 0));
        if (horizontal) {
            gc.setBackground(_colorNonWork);
            gc.fillRectangle(drawingArea.x, drawingArea.y, end - drawingArea.x, drawingArea.height);
            gc.setBackground(_colorWork);
            gc.fillRectangle(end, drawingArea.y, end2 - end, drawingArea.height);
            gc.setBackground(_colorNonWork);
            gc.fillRectangle(end2, drawingArea.y, drawingArea.width, drawingArea.height);
        } else {
            gc.setBackground(_colorNonWork);
            gc.fillRectangle(drawingArea.x, drawingArea.y, drawingArea.width, end - drawingArea.y);
            gc.setBackground(_colorWork);
            gc.fillRectangle(drawingArea.x, end, drawingArea.width, end2 - end);
            gc.setBackground(_colorNonWork);
            gc.fillRectangle(drawingArea.x, end2, drawingArea.width, drawingArea.height);
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
                gc.drawLine(ox, x, ox + width, x);
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
                gc.drawLine(ox, x, ox + width, x);
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
        _colorWork.dispose();
        _colorNonWork.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public GridRenderer createPrintRenderer(Printer printer) {
        CalendarGridRenderer renderer = new CalendarGridRenderer(printer);
        // TODO
        return renderer;
    }

    public int getBeginWorkHour() {
        return _beginWorkHour;
    }

    public void setBeginWorkHour(int beginWorkHour) {
        _beginWorkHour = beginWorkHour;
    }

    public int getEndWorkHour() {
        return _endWorkHour;
    }

    public void setEndWorkHour(int endWorkHour) {
        _endWorkHour = endWorkHour;
    }

    public void setTickProvider(ITickProvider tickProvider) {
        // TODO Auto-generated method stub
        
    }
}
