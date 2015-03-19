/*
 *  File: DefaultTimeScaleRenderer.java 
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.date.holidayenumerator.HolidayEnumerator;
import de.jaret.util.date.iterator.DateIterator;
import de.jaret.util.date.iterator.DayIterator;
import de.jaret.util.date.iterator.HourIterator;
import de.jaret.util.date.iterator.MillisecondIterator;
import de.jaret.util.date.iterator.MinuteIterator;
import de.jaret.util.date.iterator.MonthIterator;
import de.jaret.util.date.iterator.SecondIterator;
import de.jaret.util.date.iterator.WeekIterator;
import de.jaret.util.date.iterator.YearIterator;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.TickScaler.Range;
import de.jaret.util.ui.timebars.model.PPSInterval;
import de.jaret.util.ui.timebars.strategy.ITickProvider;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * A default timescale renderer rendering a readable scale for ranges from years to milliseconds. A holiday enumerator
 * is used to display special and holidays in the tooltip. Suports horizontal and vertical orientation.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultTimeScaleRenderer.java 883 2009-10-07 21:03:00Z kliem $
 */
public class DefaultTimeScaleRenderer extends RendererBase implements TimeScaleRenderer, ITickProvider {

    /** preferred height. */
    protected static final int PREFERREDHEIGHT = 50;

    /** length of minor ticks. */
    protected static final int MINORLENGTH = 5;
    /** length of major ticks. */
    protected static final int MAJORLENGTH = 10;

    /** additional gap between end of label and next line (approx). */
    protected static final int ADDITIONALGAP = 7;

    /** gap between line and label. */
    protected static final int GAP = 3;

    /** Bonus rewarded when an Iterator is already enabled for a format. */
    protected static final int SETBONUS = 5;

    /** remember the last seen pps value. */
    private double _lastPPS = -1;

    /** major ticks of the last rendering. */
    protected List<JaretDate> _majorTicks;
    /** minor ticks of the last rendering. */
    protected List<JaretDate> _minorTicks;

    /** List of date iterators for the strips. */
    protected List<DateIterator> _iterators;

    /** format of the corresponding iterator label. */
    protected List<DateIterator.Format> _formats;

    /** iterator for the middle main strip. */
    protected DateIterator _midStrip;
    /** iterator for the upmost strip. */
    protected DateIterator _upperStrip;
    /** iterator for the minor ticks. */
    protected DateIterator _lowerStrip;
    /** map defining upper iterators for middle iterators. */
    protected Map<DateIterator, DateIterator> _upperMap = new HashMap<DateIterator, DateIterator>();

    /** holiday enumerator for tooltips. */
    protected HolidayEnumerator _holidayEnumerator;

    /**
     * Construct the renderer for a prinetr device.
     * 
     * @param printer printer device
     */
    public DefaultTimeScaleRenderer(Printer printer) {
        super(printer);
        initIterators();
    }

    /**
     * Default constructor.
     */
    public DefaultTimeScaleRenderer() {
        super(null);
        initIterators();
    }

    /**
     * Initialize all possible iterators (strips).
     */
    protected void initIterators() {
        _iterators = new ArrayList<DateIterator>();
        _formats = new ArrayList<DateIterator.Format>();

        DateIterator iterator = new MillisecondIterator(1);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new MillisecondIterator(10);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new MillisecondIterator(100);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new MillisecondIterator(500);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new SecondIterator(1);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new SecondIterator(5);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new SecondIterator(30);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new MinuteIterator(1);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new MinuteIterator(10);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        _upperMap.put(iterator, new DayIterator(1));

        iterator = new MinuteIterator(30);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        _upperMap.put(iterator, new DayIterator(1));

        iterator = new HourIterator(3);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        _upperMap.put(iterator, new DayIterator(1));

        iterator = new HourIterator(12);
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        _upperMap.put(iterator, new DayIterator(1));

        iterator = new DayIterator();
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);

        iterator = new WeekIterator();
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        _upperMap.put(iterator, new MonthIterator());

        iterator = new MonthIterator();
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
        _upperMap.put(iterator, new YearIterator());

        iterator = new YearIterator();
        _iterators.add(iterator);
        _formats.add(DateIterator.Format.LONG);
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
        // each drawing operation produces new tick dates
        _majorTicks = new ArrayList<JaretDate>();
        _minorTicks = new ArrayList<JaretDate>();

        if (!delegate.hasVariableXScale()) {
            // if pps changed check which stripes to draw
            if (_lastPPS != delegate.getPixelPerSecond()) {
                checkStrips(gc, delegate, delegate.getStartDate(), null);
                _lastPPS = delegate.getPixelPerSecond();
            }
            // plain scale
            // +1 second for millisecond scales since the getSecondsDisplayed method rounds to nearest lower second
            // count
            drawStrips(gc, delegate, drawingArea, top, delegate.getStartDate().copy(), delegate.getStartDate().copy()
                    .advanceSeconds(delegate.getSecondsDisplayed() + 1), printing);
        } else {
            // check strips for every part with different scale
            JaretDate startDate = delegate.getStartDate().copy();
            JaretDate endDate = delegate.getStartDate().copy().advanceSeconds(delegate.getSecondsDisplayed());
            List<Interval> ppsIntervals = delegate.getPpsRow().getIntervals(startDate, endDate);
            // shortcut if no ppsintervals are in the area just draw straight
            if (ppsIntervals.size() == 0) {
                // if pps changed check which stripes to draw
                if (_lastPPS != delegate.getPixelPerSecond()) {
                    checkStrips(gc, delegate, delegate.getStartDate(), null);
                    _lastPPS = delegate.getPixelPerSecond();
                }
                drawStrips(gc, delegate, drawingArea, top, delegate.getStartDate().copy(), delegate.getStartDate()
                        .copy().advanceSeconds(delegate.getSecondsDisplayed() + 1), printing); // +1 -> see above
            } else {
                JaretDate d = startDate.copy();
                while (d.compareTo(endDate) < 0) {
                    PPSInterval ppsInterval = delegate.getPPSInterval(d);
                    JaretDate e;
                    if (ppsInterval != null) {
                        e = ppsInterval.getEnd();
                    } else {
                        PPSInterval nextInterval = delegate.nextPPSInterval(d);
                        if (nextInterval != null) {
                            e = nextInterval.getBegin();
                        } else {
                            e = endDate;
                        }
                    }
                    // calculate the strips for the current date in question
                    checkStrips(gc, delegate, d, e);
                    // only draw if the interval is not a break
                    if (ppsInterval == null || !ppsInterval.isBreak()) {
                        drawStrips(gc, delegate, drawingArea, top, d, e, printing);
                    } else {
                        // TODO draw break
                    }
                    d = e;
                }
                _lastPPS = -1; // force check next paint
            }
        }
    }

    /**
     * Draw the configured strips in the area indicated.
     * 
     * @param gc Graphics to draw with
     * @param delegate delegate
     * @param top boolean for top position
     * @param startDate start date for this part of the strip
     * @param endDate end date for this part of the strip
     * @param printing true if used to print
     */
    private void drawStrips(GC gc, TimeBarViewerDelegate delegate, Rectangle drawingArea, boolean top,
            JaretDate startDate, JaretDate endDate, boolean printing) {
        boolean horizontal = delegate.getOrientation().equals(TimeBarViewerInterface.Orientation.HORIZONTAL);

        Rectangle clipSave = gc.getClipping();

        if (horizontal) {
            // clip to start/end date for ensuring nothing is painted beyond the limits
            int sx = delegate.xForDate(startDate);
            Rectangle destRect = new Rectangle(sx, drawingArea.y, delegate.xForDate(endDate) - sx, drawingArea.height);
            // gc.setClip(destRect.intersection(clipSave));

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

            // draw top line
            gc.drawLine(drawingArea.x, oy, drawingArea.x + drawingArea.width, oy);

            if (_lowerStrip != null) {
                DateIterator it = _lowerStrip;
                it.reInitialize(startDate, endDate.copy().advanceMillis(it.getApproxStepMilliSeconds()));
                while (it.hasNextDate()) {
                    JaretDate d = it.getNextDate();
                    _minorTicks.add(d);
                    int x = delegate.xForDate(d);
                    gc.drawLine(x, oy, x, oy + minorOff);
                }
            }
            if (_midStrip != null) {
                DateIterator it = _midStrip;
                it.reInitialize(startDate, endDate.copy().advanceMillis(it.getApproxStepMilliSeconds()));
                while (it.hasNextDate()) {
                    JaretDate d = it.getNextDate();

                    _majorTicks.add(d);

                    int x = delegate.xForDate(d);
                    gc.drawLine(x, oy, x, oy + majorOff);
                    // label every two major ticks
                    String label = it.getLabel(d, DateIterator.Format.LONG);
                    SwtGraphicsHelper.drawStringCentered(gc, label, x, oy + majorLabelOff);
                }
            }

            // draw upper part
            if (_upperStrip != null) {
                DateIterator it = _upperStrip;
                it.reInitialize(startDate, endDate.copy().advanceMillis(it.getApproxStepMilliSeconds()));
                while (it.hasNextDate()) {
                    JaretDate d = it.getNextDate();
                    int x = delegate.xForDate(d);

                    gc.drawLine(x, oy, x, oy + majorOff);
                    String label = it.getLabel(d, DateIterator.Format.LONG);
                    SwtGraphicsHelper.drawStringCentered(gc, label, x, oy + dayOff);
                }
            }

        } else {
            // vertical strip
            // clip to start/end date for ensuring nothing is painted beyond the limits
            int sy = delegate.xForDate(startDate);
            Rectangle destRect = new Rectangle(drawingArea.x, sy, drawingArea.width, delegate.xForDate(endDate) - sy);
            gc.setClipping(destRect.intersection(clipSave));

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

            // draw left/right line
            if (top) {
                gc.drawLine(drawingArea.x + drawingArea.width - 1, 0, drawingArea.x + drawingArea.width - 1,
                        getHeight());
            } else {
                gc.drawLine(0, 0, 0, getHeight());
            }

            if (_lowerStrip != null) {
                DateIterator it = _lowerStrip;
                it.reInitialize(startDate, endDate.copy().advanceMillis(it.getApproxStepMilliSeconds()));
                while (it.hasNextDate()) {
                    JaretDate d = it.getNextDate();

                    _minorTicks.add(d);

                    int y = delegate.xForDate(d);
                    gc.drawLine(ox, y, ox + minorOff, y);
                }
            }
            if (_midStrip != null) {
                DateIterator it = _midStrip;
                it.reInitialize(startDate, endDate.copy().advanceMillis(it.getApproxStepMilliSeconds()));
                while (it.hasNextDate()) {
                    JaretDate d = it.getNextDate();

                    _majorTicks.add(d);

                    int y = delegate.xForDate(d);

                    gc.drawLine(ox, y, ox + majorOff, y);
                    String label = it.getLabel(d, DateIterator.Format.LONG);
                    if (top) {
                        SwtGraphicsHelper.drawStringRightAlignedVCenter(gc, label, ox + majorOff - 1, y);
                    } else {
                        SwtGraphicsHelper.drawStringLeftAlignedVCenter(gc, label, ox + majorOff + 1, y);
                    }
                }
            }

            // draw upper part
            if (_upperStrip != null) {
                DateIterator it = _upperStrip;
                it.reInitialize(startDate, endDate.copy().advanceMillis(it.getApproxStepMilliSeconds()));
                while (it.hasNextDate()) {
                    JaretDate d = it.getNextDate();
                    int x = delegate.xForDate(d);

                    // String label = it.getLabel(DateIterator.Format.LONG);
                    // if (_top) {
                    // GraphicsHelper.drawStringRightAlignedVCenter(gc, label, ox + dayOff - 1, y);
                    // } else {
                    // GraphicsHelper.drawStringLeftAlignedVCenter(gc, label, ox + majorOff + 1, y);
                    // }
                }
            }

        }
        gc.setClipping(clipSave);
    }

    /**
     * Check which strips should be drawn.
     * 
     * @param gc Graphics
     * @param delegate delegate
     * @param startDate date to use for starting check
     */
    private void checkStrips(GC gc, TimeBarViewerDelegate delegate, JaretDate startDate, JaretDate endDate) {
        // System.out.println("checkstrips "+startDate.getDate().getTime()+" -- "+endDate.getDate().getTime());
        for (int i = 0; i < _iterators.size(); i++) {
            DateIterator it = _iterators.get(i);
            it.reInitialize(startDate, endDate);
            if (it.previewNextDate() != null) {
                JaretDate current = it.getNextDate();
                if (!it.hasNextDate()) {
                    continue;
                }
                JaretDate next = it.getNextDate();
                int width = delegate.xForDate(next) - delegate.xForDate(current);
                String label = it.getLabel(current, DateIterator.Format.LONG);
                System.out.println("Label " + label);
                Point p = gc.textExtent(label);
                int bonus = _midStrip == it && _formats.get(i).equals(DateIterator.Format.LONG) ? SETBONUS : 0;
                if (width > p.x + GAP + ADDITIONALGAP - bonus) {
                    _midStrip = it;
                    System.out.println(it);
                    if (it instanceof MillisecondIterator) {
                        MillisecondIterator mit = (MillisecondIterator) it;
                        System.out.println(mit.getApproxStepMilliSeconds());
                    }
                    _upperStrip = _upperMap.get(_midStrip);
                    if (i > 0) {
                        _lowerStrip = _iterators.get(i - 1);
                    }
                    break;
                }
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

        // TODO
        // String str = getToolTipTextForDate(date, _lastRange);
        // if (_holidayEnumerator != null) {
        // if (_holidayEnumerator.isHoliday(date.getDate()) || _holidayEnumerator.isSpecialDay(date.getDate())) {
        // str += "\n" + _holidayEnumerator.getDayName(date.getDate());
        // }
        // }
        // return str;
        return date.toDisplayString();
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
        DefaultTimeScaleRenderer dtsr = new DefaultTimeScaleRenderer(printer);
        dtsr.setHolidayEnumerator(_holidayEnumerator);
        return dtsr;
    }

    /**
     * {@inheritDoc}
     */
    public List<JaretDate> getMajorTicks(TimeBarViewerDelegate delegate) {
        return _majorTicks;
    }

    /**
     * {@inheritDoc}
     */
    public List<JaretDate> getMinorTicks(TimeBarViewerDelegate delegate) {
        return _minorTicks;
    }

    /**
     * Setup the iterators to do a DST correction.
     * 
     * @param correctDST true if a correction should be done.
     */
    public void setCorrectDST(boolean correctDST) {
        for (DateIterator iterator : _iterators) {
            iterator.setCorrectDST(correctDST);
        }
    }

}
