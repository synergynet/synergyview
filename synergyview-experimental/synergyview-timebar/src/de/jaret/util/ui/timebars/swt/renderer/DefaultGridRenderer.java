/*
 *  File: DefaultGridRenderer.java 
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

import java.util.Calendar;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.date.DateUtils;
import de.jaret.util.date.JaretDate;
import de.jaret.util.date.holidayenumerator.HolidayEnumerator;
import de.jaret.util.ui.timebars.TickScaler;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.TickScaler.Range;
import de.jaret.util.ui.timebars.strategy.ITickProvider;

/**
 * Simple default grid renderer for the timebar viewer. Uses a holiday enumerator (jaretutils) to color the days.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultGridRenderer.java 837 2009-02-14 21:44:39Z kliem $
 */
public class DefaultGridRenderer extends AbstractGridRenderer {
    /** default value (pps) for coloring days. */
    private static final double DEFAULT_UPPERPPSMARKLIMIT = 0.01;

    /** default color for the major grid (r,g,b). */
    private static final RGB MAJORGRID_COLOR = new RGB(200, 200, 200);
    /** default color for the minor grid (r,g,b). */
    private static final RGB MINORGRID_COLOR = new RGB(230, 230, 230);
    /** default color for saturdays (r,g,b). */
    private static final RGB SATURDAY_COLOR = new RGB(255, 230, 230);
    /** default color for sundays (r,g,b). */
    private static final RGB SUNDAY_COLOR = new RGB(255, 200, 200);
    /** default color for special days (r,g,b). */
    private static final RGB SPECIALDAY_COLOR = new RGB(255, 255, 207);

    /** color of the major grid. */
    private Color _colorMajorGrid;

    /** color of the minor grid. */
    private Color _colorMinorGrid;

    /** color for saturdays. */
    private Color _colorSaturday;

    /** color for sundays. */
    private Color _colorSunday;

    /** color for special days. */
    private Color _colorSpecialDay;

    /** holiday enumerator for coloring days. */
    private HolidayEnumerator _holidayEnumerator;

    /** if true weekend days are colored. */
    private boolean _markWeekends = true;

    /** if true, special day according to the holiday enumerator are marked. */
    private boolean _markSpecialdays = true;

    /** if true, holidays according to the holiday enumerator are marked. */
    private boolean _markHolidays = true;

    /** limit for coloring days. */
    private double _upperPPSMarkLimit = DEFAULT_UPPERPPSMARKLIMIT;

    /** external tick provider. */
    protected ITickProvider _tickProvider = null;

    /**
     * Create a DefaultGridRenderer for a printer.
     * 
     * @param printer printer device
     */
    public DefaultGridRenderer(Printer printer) {
        super(printer);
    }

    /**
     * Create a DefaultGridRenderer for the screen.
     */
    public DefaultGridRenderer() {
        super(null);
    }

    /**
     * Set a tick provider to determine the ticks to be drawn. If no tick provider is set the defaulst by the tick
     * scaler will be used.
     * 
     * @param tickProvider tick provider to be used or <code>null</code>.
     */
    public void setTickProvider(ITickProvider tickProvider) {
        _tickProvider = tickProvider;
    }

    /**
     * Initialize the colors using the device in the gc.
     * 
     * @param gc GC carrying the device
     */
    private void initializeColors(GC gc) {
        _colorMajorGrid = new Color(gc.getDevice(), MAJORGRID_COLOR);
        _colorMinorGrid = new Color(gc.getDevice(), MINORGRID_COLOR);
        _colorSaturday = new Color(gc.getDevice(), SATURDAY_COLOR);
        _colorSunday = new Color(gc.getDevice(), SUNDAY_COLOR);
        _colorSpecialDay = new Color(gc.getDevice(), SPECIALDAY_COLOR);
    }

    /**
     * Set the holiday enumerator to be used for coloring days.
     * 
     * @param he HolidayEnumerator to be used
     */
    public void setHolidayEnumerator(HolidayEnumerator he) {
        _holidayEnumerator = he;
    }

    /**
     * If mark weekends is set to true, weekend days will be colored.
     * 
     * @param mark true for week end coloring
     */
    public void setMarkWeekends(boolean mark) {
        _markWeekends = mark;
    }

    /**
     * If mark special days is set to true, days that are special days according to the holiday enumerator are colored.
     * 
     * @param mark true for coloring spcial days
     */
    public void setMarkSpecialDays(boolean mark) {
        _markSpecialdays = mark;
    }

    /**
     * If mark holidaysdays is set to true, days that are holidays days according to the holiday enumerator are colored.
     * 
     * @param mark true for coloring holidays
     */
    public void setMarkHolidays(boolean mark) {
        _markHolidays = mark;
    }

    /**
     * Set the limit for the pixPerSecond (scale) for marking days.
     * 
     * @param limit pixPerSecond limit (upper bound)
     */
    public void setUpperPPSMarkLimit(double limit) {
        _upperPPSMarkLimit = limit;
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, TimeBarViewerDelegate delegate, Rectangle drawingArea, boolean printing) {
        // lazy initialize the used colors
        if (_colorMajorGrid == null) {
            initializeColors(gc);
        }
        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;

        if (_tickProvider == null) {
            drawIntern(gc, delegate, drawingArea, printing, horizontal);
        } else {
            drawIntern(gc, delegate, drawingArea, printing, horizontal, _tickProvider);
        }

    }

    public void drawIntern(GC gc, TimeBarViewerDelegate delegate, Rectangle drawingArea, boolean printing,
            boolean horizontal, ITickProvider tickProvider) {
        int ox = drawingArea.x;
        int oy = drawingArea.y;
        int width = drawingArea.width;
        int height = drawingArea.height;

        Color fg = gc.getForeground();

        if (printing) {
            _upperPPSMarkLimit = _upperPPSMarkLimit * getScaleX();
        }

        // first date
        JaretDate date = delegate.getStartDate().copy();
        date.setTime(0, 0, 0);

        int max = horizontal ? (ox + width) : (oy + height);

        // draw day backgroud if configured
        if (_markHolidays || _markSpecialdays || _markWeekends) {
            Color bg = gc.getBackground();
            if (delegate.getPixelPerSecond() < _upperPPSMarkLimit) {
                // only valid if no variable xaxis is used, see below
                int daywidth = (int) (delegate.getPixelPerSecond() * 24.0 * 60.0 * 60.0);
                while (delegate.xForDate(date) < max) {
                    int x = delegate.xForDate(date);
                    Color mark = null;
                    if (_holidayEnumerator != null) {
                        if (_markHolidays && _holidayEnumerator.isHoliday(date.getDate())) {
                            mark = _colorSunday;
                        } else if (_markSpecialdays && _holidayEnumerator.isSpecialDay(date.getDate())) {
                            mark = _colorSpecialDay;
                        }
                    }
                    if (mark == null && _markWeekends) {
                        if (date.getDayOfWeek() == Calendar.SATURDAY) {
                            mark = _colorSaturday;
                        } else if (date.getDayOfWeek() == Calendar.SUNDAY) {
                            mark = _colorSunday;
                        }
                    }
                    if (mark != null) {
                        gc.setBackground(mark);
                        // if the viewer uses a variable x scale, calculate the width of the day dynamically
                        if (delegate.hasVariableXScale()) {
                            int x2 = delegate.xForDate(date.copy().advanceDays(1));
                            daywidth = x2 - x;
                        }
                        if (horizontal) {
                            gc.fillRectangle(x, oy, daywidth, height);
                        } else {
                            // TODO CHECK
                            gc.fillRectangle(ox, x, width, daywidth);
                        }
                    }
                    date.advanceDays(1);
                }

            }

            gc.setBackground(bg);
        }

        // correct line width for printing
        if (printing) {
            gc.setLineWidth(getDefaultLineWidth());
        }
        // draw the minor grid
        gc.setForeground(_colorMinorGrid);
        for (JaretDate d : tickProvider.getMinorTicks(delegate)) {
            int x = delegate.xForDate(d);
            if (horizontal) {
                gc.drawLine(x, oy, x, oy + height);
            } else {
                gc.drawLine(ox, x, ox + width, x);
            }
        }

        // draw the major grid
        gc.setForeground(_colorMajorGrid);
        for (JaretDate d : tickProvider.getMajorTicks(delegate)) {
            int x = delegate.xForDate(d);
            if (horizontal) {
                gc.drawLine(x, oy, x, oy + height);
            } else {
                gc.drawLine(ox, x, ox + width, x);
            }
        }
        gc.setLineWidth(1);
        gc.setForeground(fg);
    }

    public void drawIntern(GC gc, TimeBarViewerDelegate delegate, Rectangle drawingArea, boolean printing,
            boolean horizontal) {

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
            _upperPPSMarkLimit = _upperPPSMarkLimit * getScaleX();
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

        int max = horizontal ? (ox + width) : (oy + height);

        // draw day backgroud if configured
        if (_markHolidays || _markSpecialdays || _markWeekends) {
            Color bg = gc.getBackground();
            if (range == Range.DAY || range == Range.WEEK
                    || (range == Range.HOUR && delegate.getPixelPerSecond() < _upperPPSMarkLimit)) {
                // only valid if no variable xaxis is used, see below
                int daywidth = (int) (delegate.getPixelPerSecond() * 24.0 * 60.0 * 60.0);
                while (delegate.xForDate(date) < max) {
                    int x = delegate.xForDate(date);
                    Color mark = null;
                    if (_holidayEnumerator != null) {
                        if (_markHolidays && _holidayEnumerator.isHoliday(date.getDate())) {
                            mark = _colorSunday;
                        } else if (_markSpecialdays && _holidayEnumerator.isSpecialDay(date.getDate())) {
                            mark = _colorSpecialDay;
                        }
                    }
                    if (mark == null && _markWeekends) {
                        if (date.getDayOfWeek() == Calendar.SATURDAY) {
                            mark = _colorSaturday;
                        } else if (date.getDayOfWeek() == Calendar.SUNDAY) {
                            mark = _colorSunday;
                        }
                    }
                    if (mark != null) {
                        gc.setBackground(mark);
                        // if the viewer uses a variable x scale, calculate the width of the day dynamically
                        if (delegate.hasVariableXScale()) {
                            int x2 = delegate.xForDate(date.copy().advanceDays(1));
                            daywidth = x2 - x;
                        }
                        if (horizontal) {
                            gc.fillRectangle(x, oy, daywidth, height);
                        } else {
                            // TODO CHECK
                            gc.fillRectangle(ox, x, width, daywidth);
                        }
                    }
                    date.advanceDays(1);
                }

            }

            gc.setBackground(bg);
        }

        date = save.copy();
        // draw the minor grid
        if (printing) {
            gc.setLineWidth(getDefaultLineWidth());
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
                int adv = Math.round((float) minTick / (float) (24 * 60 * 7 * 4));
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
                int adv = Math.round((float) majTick / (float) (24 * 60 * 7 * 4));
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
        super.dispose();
        if (_colorMajorGrid != null) {
            _colorMajorGrid.dispose();
            _colorMinorGrid.dispose();
            _colorSaturday.dispose();
            _colorSpecialDay.dispose();
            _colorSunday.dispose();
        }
    }

    /**
     * {@inheritDoc}
     */
    public GridRenderer createPrintRenderer(Printer printer) {
        DefaultGridRenderer renderer = new DefaultGridRenderer(printer);
        renderer.setHolidayEnumerator(_holidayEnumerator);
        renderer.setMarkHolidays(_markHolidays);
        renderer.setMarkSpecialDays(_markSpecialdays);
        renderer.setMarkWeekends(_markWeekends);
        renderer.setTickProvider(_tickProvider);
        return renderer;
    }

}
