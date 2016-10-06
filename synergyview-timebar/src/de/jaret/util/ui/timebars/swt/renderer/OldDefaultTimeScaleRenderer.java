/*
 *  File: OldDefaultTimeScaleRenderer.java 
 *  Copyright (c) 2004-2009  Peter Kliem (Peter.Kliem@jaret.de)
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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.date.DateUtils;
import de.jaret.util.date.JaretDate;
import de.jaret.util.date.holidayenumerator.HolidayEnumerator;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TickScaler;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.TickScaler.Range;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * A default timescale renderer using the TickScaler to render a readable scale for use cases taht display something in
 * the range of hours and more in the timebar viewer. A holiday enumerator is used to display special and holidays in
 * the tooltip. Suports horizontal and ertical orientation.
 * 
 * @author Peter Kliem
 * @version $Id: OldDefaultTimeScaleRenderer.java 836 2009-02-14 21:24:39Z kliem $
 */
public class OldDefaultTimeScaleRenderer extends RendererBase implements TimeScaleRenderer {

    /** preferred height. */
    protected static final int PREFERREDHEIGHT = 50;

    /** aproximate number of seconds in one month. */
    private static final long APPROXSECONDSINMONTH = (24 * 60 * 7 * 4);

    /** length of minor ticks. */
    protected static final int MINORLENGTH = 5;
    /** length of major ticks. */
    protected static final int MAJORLENGTH = 10;

    /** remember tha last range for tooltip display. */
    private Range _lastRange = Range.DAY;

    /** holiday enumeratotr for tooltips. */
    protected HolidayEnumerator _holidayEnumerator;

    /**
     * Construct the renderer for a prinetr device.
     * 
     * @param printer printer device
     */
    public OldDefaultTimeScaleRenderer(Printer printer) {
        super(printer);
    }

    /**
     * Default constructor.
     */
    public OldDefaultTimeScaleRenderer() {
        super(null);
    }

    /**
     * Set a holidayenumerator for diasplaying special days as tooltip.
     * 
     * @param he holidayenumerator to use
     */
    public void setHolidayEnumerator(HolidayEnumerator he) {
        _holidayEnumerator = he;
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, boolean top, boolean printing) {
        if (delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL) {
            drawHorizontal(gc, drawingArea, delegate, top, printing);
        } else {
            drawVertical(gc, drawingArea, delegate, top, printing);
        }
    }

    /**
     * Draw the horizontal scale.
     * 
     * @param gc GC
     * @param drawingArea drawing area
     * @param delegate the timebar viewer delegate
     * @param top true for top position
     * @param printing true if printing
     */
    public void drawHorizontal(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, boolean top,
            boolean printing) {
        int ox = drawingArea.x;

        int basey;
        int minorOff;
        int majorOff;
        int majorLabelOff;
        int dayOff;

        if (!top) {
            basey = drawingArea.y;
            minorOff = scaleY(MINORLENGTH);
            majorOff = scaleY(MAJORLENGTH);
            majorLabelOff = scaleY(22);
            dayOff = scaleY(34);
        } else {
            basey = drawingArea.y + drawingArea.height - 1;
            minorOff = scaleY(-MINORLENGTH);
            majorOff = scaleY(-MAJORLENGTH);
            majorLabelOff = scaleY(-10);
            dayOff = scaleY(-22);
        }
        int oy = basey;

        int width = drawingArea.width;
        JaretDate date = delegate.getStartDate().copy();

        int idx;
        if (!printing) {
            idx = TickScaler.getTickIdx(delegate.getPixelPerSecond() / getScaleX());
        } else {
            idx = TickScaler.getTickIdx(delegate.getPixelPerSecond() / getScaleX());
        }
        int majTick = TickScaler.getMajorTickMinutes(idx);
        int minTick = TickScaler.getMinorTickMinutes(idx);
        TickScaler.Range range = TickScaler.getRange(idx);
        _lastRange = range;

        // clean starting date on a major tick minute position (starting with a
        // day)
        date.setMinutes(0);
        date.setHours(0);
        date.setSeconds(0);

        // if range is week take a week starting point
        if (range == Range.WEEK) {
            while (date.getDayOfWeek() != DateUtils.getFirstDayOfWeek()) {
                date.backDays(1.0);
            }
        } else if (range == Range.MONTH) {
            // month -> month starting point
            date.setDay(1);
        }
        JaretDate save = date.copy();

        if (printing) {
            gc.setLineWidth(1);
        }
        // draw top/bottom line
        gc.drawLine(ox, oy, ox + width, oy);

        // draw the minor ticks
        while (delegate.xForDate(date) < ox + width) {
            int x = delegate.xForDate(date);
            gc.drawLine(x, oy, x, oy + minorOff);
            if (range == Range.MONTH) {
                int adv = Math.round(minTick / APPROXSECONDSINMONTH);
                if (adv == 0) {
                    adv = 1;
                }
                date.advanceMonths(adv);
            } else {
                date.advanceMinutes(minTick);
            }
        }

        date = save.copy();
        // draw the major ticks
        while (delegate.xForDate(date) < ox + width) {
            int x = delegate.xForDate(date);
            gc.drawLine(x, oy, x, oy + majorOff);
            if (range == Range.MONTH) {
                int adv = Math.round(majTick / APPROXSECONDSINMONTH);
                if (adv == 0) {
                    adv = 1;
                }
                date.advanceMonths(adv);
            } else {
                date.advanceMinutes(majTick);
            }
        }

        gc.setLineWidth(1);

        // labels: draw every two major ticks
        date = save.copy();
        int lastDay = date.getDay();
        boolean second = true;
        int count = 0;
        boolean third = true;
        // Labels are drawn beyond the width. Otherwise when the beginning of
        // the labels
        // would not be drawn when the tick itself is out of sight
        while (delegate.xForDate(date) < drawingArea.x + drawingArea.width) {
            int x = delegate.xForDate(date);
            if (date.getMinutes() % (majTick * 2) == 0) {
                // Second line
                String str = null;
                if (range == Range.HOUR) {
                    // time
                    str = date.toDisplayStringTime();
                } else if (range == Range.DAY) {
                    // day
                    str = date.getShortDayOfWeekString();
                } else if (range == Range.WEEK) {
                    // week
                    str = "KW" + date.getWeekOfYear();
                } else if (range == Range.MONTH) {
                    // month
                    str = Integer.toString(date.getYear());
                }
                // draw
                if (x > SwtGraphicsHelper.getStringDrawingWidth(gc, str) / 2) {
                    SwtGraphicsHelper.drawStringCentered(gc, str, x, oy + majorLabelOff);
                }
                // first line
                if (range == Range.HOUR) {
                    if (date.getDay() != lastDay) {
                        str = date.getDay() + ". (" + date.getDayOfWeekString() + ")";
                    } else {
                        str = "";
                    }
                    lastDay = date.getDay();
                } else if (range == Range.DAY || range == Range.WEEK) {
                    str = date.getDay() + "." + (third ? date.getShortMonthString() : "");
                } else if (range == Range.MONTH) {
                    str = date.getMonthString();
                }
                second = !second;
                third = count++ % 3 == 0;
                SwtGraphicsHelper.drawStringCentered(gc, str, x, oy + dayOff);
            }
            if (range == Range.MONTH) {
                int adv = Math.round(majTick / APPROXSECONDSINMONTH);
                if (adv == 0) {
                    adv = 1;
                }
                date.advanceMonths(adv);
            } else {
                date.advanceMinutes(majTick);
            }
        }
    }

    /**
     * Draw the vertical scale.
     * 
     * @param gc GC
     * @param drawingArea drawing area
     * @param delegate the timebar viewer delegate
     * @param top true for left position
     * @param printing true if printing
     */
    private void drawVertical(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, boolean top,
            boolean printing) {
        int oy = drawingArea.y;

        int basex;
        int minorOff;
        int majorOff;
        int majorLabelOff;
        int dayOff;

        if (!top) {
            basex = drawingArea.x;
            minorOff = scaleX(MINORLENGTH);
            majorOff = scaleX(MAJORLENGTH);
            majorLabelOff = scaleX(22);
            dayOff = scaleX(34);
        } else {
            basex = drawingArea.x + drawingArea.width - 1;
            minorOff = scaleX(-MINORLENGTH);
            majorOff = scaleX(-MAJORLENGTH);
            majorLabelOff = scaleX(-10);
            dayOff = scaleX(-22);
        }
        int ox = basex;

        int height = drawingArea.height;
        JaretDate date = delegate.getStartDate().copy();

        int idx = TickScaler.getTickIdx(delegate.getPixelPerSecond() / getScaleY());

        int majTick = TickScaler.getMajorTickMinutes(idx);
        int minTick = TickScaler.getMinorTickMinutes(idx);
        TickScaler.Range range = TickScaler.getRange(idx);
        _lastRange = range;

        // clean starting date on a major tick minute position (starting with a
        // day)
        date.setMinutes(0);
        date.setHours(0);
        date.setSeconds(0);

        // if range is week take a week starting point
        if (range == Range.WEEK) {
            while (date.getDayOfWeek() != DateUtils.getFirstDayOfWeek()) {
                date.backDays(1.0);
            }
        } else if (range == Range.MONTH) {
            // month -> month starting point
            date.setDay(1);
        }
        JaretDate save = date.copy();

        if (printing) {
            gc.setLineWidth(1);
        }
        // draw top/bottom line
        gc.drawLine(ox, oy, ox, oy + height);

        // draw the minor ticks
        while (delegate.xForDate(date) < oy + height) {
            int y = delegate.xForDate(date);
            gc.drawLine(ox, y, ox + minorOff, y);
            if (range == Range.MONTH) {
                int adv = Math.round(minTick / APPROXSECONDSINMONTH);
                if (adv == 0) {
                    adv = 1;
                }
                date.advanceMonths(adv);
            } else {
                date.advanceMinutes(minTick);
            }
        }

        date = save.copy();
        // draw the major ticks
        while (delegate.xForDate(date) < oy + height) {
            int y = delegate.xForDate(date);
            gc.drawLine(ox, y, ox + majorOff, y);
            if (range == Range.MONTH) {
                int adv = Math.round(majTick / APPROXSECONDSINMONTH);
                if (adv == 0) {
                    adv = 1;
                }
                date.advanceMonths(adv);
            } else {
                date.advanceMinutes(majTick);
            }
        }

        gc.setLineWidth(1);

        // labels: draw every two major ticks
        date = save.copy();
        // Labels are drawn beyond the width. Otherwise when the beginning of
        // the labels
        // would not be drawn when the tick itself is out of sight
        while (delegate.xForDate(date) < drawingArea.y + drawingArea.height + 50) {
            int y = delegate.xForDate(date);
            if (date.getMinutes() % (majTick * 2) == 0) {
                // Second line
                String str = null;
                if (range == Range.HOUR) {
                    // time
                    str = date.toDisplayStringTime();
                } else if (range == Range.DAY) {
                    // day
                    str = date.getShortDayOfWeekString();
                } else if (range == Range.WEEK) {
                    // week
                    str = "KW" + date.getWeekOfYear();
                } else if (range == Range.MONTH) {
                    // month
                    str = Integer.toString(date.getYear());
                }
                // draw
                if (top) {
                    SwtGraphicsHelper.drawStringRightAlignedVCenter(gc, str, drawingArea.x + drawingArea.width
                            + majorOff, y);
                } else {
                    SwtGraphicsHelper.drawStringLeftAlignedVCenter(gc, str, drawingArea.x + majorOff, y);
                }
                // // first line
                // if (range == Range.HOUR) {
                // if (date.getDay() != lastDay) {
                // str = date.getDay() + ". (" + date.getDayOfWeekString() + ")";
                // } else {
                // str = "";
                // }
                // lastDay = date.getDay();
                // } else if (range == Range.DAY || range == Range.WEEK) {
                // str = date.getDay() + "." + (third ? date.getShortMonthString() : "");
                // } else if (range == Range.MONTH) {
                // str = date.getMonthString();
                // }
                // second = !second;
                // third = count++ % 3 == 0;
                // SwtGraphicsHelper.drawStringCentered(gc, str, x, oy + dayOff);
            }
            if (range == Range.MONTH) {
                int adv = Math.round(majTick / APPROXSECONDSINMONTH);
                if (adv == 0) {
                    adv = 1;
                }
                date.advanceMonths(adv);
            } else {
                date.advanceMinutes(majTick);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(TimeBarViewer tbv, Rectangle drawingArea, int x, int y) {

        TimeBarViewerDelegate delegate = (TimeBarViewerDelegate) tbv.getData("delegate");
        JaretDate date = null;
        if (delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL) {
            date = tbv.dateForX(x);
        } else {
            date = tbv.dateForX(y);
        }

        String str = getToolTipTextForDate(date, _lastRange);
        if (_holidayEnumerator != null) {
            if (_holidayEnumerator.isHoliday(date.getDate()) || _holidayEnumerator.isSpecialDay(date.getDate())) {
                str += "\n" + _holidayEnumerator.getDayName(date.getDate());
            }
        }
        return str;
    }

    /**
     * Convert date to string for tooltip display.
     * 
     * @param date date
     * @param range last range
     * @return string for displaying as tooltip
     */
    protected String getToolTipTextForDate(JaretDate date, Range range) {
        String str;
        if (range == Range.HOUR) {
            str = date.toDisplayString();
        } else {
            str = date.toDisplayStringDate();
        }
        return str;
    }

    /**
     * {@inheritDoc}
     */
    public int getHeight() {
        if (_printer == null) {
            return PREFERREDHEIGHT;
        } else {
            return scaleY(PREFERREDHEIGHT);
        }
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
    public TimeScaleRenderer createPrintRenderer(Printer printer) {
        OldDefaultTimeScaleRenderer dtsr = new OldDefaultTimeScaleRenderer(printer);
        dtsr.setHolidayEnumerator(_holidayEnumerator);
        return dtsr;
    }

}
