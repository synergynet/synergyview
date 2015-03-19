/*
 *  File: BoxTimeScaleRenderer.java 
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
import java.util.List;

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
import de.jaret.util.ui.timebars.model.PPSInterval;
import de.jaret.util.ui.timebars.strategy.ITickProvider;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * Timescalerenderer rendering strips of boxes for different time intervals using DateIterator instances. A holiday
 * enumerator can be used to display special and holidays in the tooltip. Supports horizontal and vertical operation
 * (vertical might be imperformant since vertical text is used).
 * 
 * @author Peter Kliem
 * @version $Id: BoxTimeScaleRenderer.java 883 2009-10-07 21:03:00Z kliem $
 */
public class BoxTimeScaleRenderer extends RendererBase implements TimeScaleRenderer, ITickProvider {
    /** default number of strips to render. */
    private static final int DEFAULT_NUMBER_OF_STRIPS = 3;

    /** height of a box. */
    protected static final int BOXHEIGHT = 20;

    /** additional gap between end of label and next line (approx). */
    protected static final int ADDITIONALGAP = 7;

    /** gap between line and label. */
    protected static final int GAP = 3;

    /** Bonus rewarded when an Iterator is already enabled for a format. */
    protected static final int SETBONUS = 5;

    /** local cache for textextent height. */
    protected int _textHeight = -1;

    /** actual number of strips to render. */
    protected int _numberOfStrips = DEFAULT_NUMBER_OF_STRIPS;

    /** remember the last seen pps value. */
    private double _lastPPS = -1;

    /** holiday enumerator for tooltips. */
    protected HolidayEnumerator _holidayEnumerator;

    /** List of dateiterators for the strips. */
    protected List<DateIterator> _iterators;

    /** format of the corresponding iterator label. */
    protected List<DateIterator.Format> _formats;

    /** enabled state of the corresponding strip. */
    protected List<Boolean> _enable;

    /** major ticks of the last rendering. */
    protected List<JaretDate> _majorTicks;
    /** minor ticks of the last rendering. */
    protected List<JaretDate> _minorTicks;

    /**
     * Construct the renderer for a printer device.
     * 
     * @param printer printer device
     */
    public BoxTimeScaleRenderer(Printer printer) {
        super(printer);
        initIterators();
    }

    /**
     * Initialize all possible iterators (strips).
     */
    private void initIterators() {
        _iterators = new ArrayList<DateIterator>();
        _formats = new ArrayList<DateIterator.Format>();
        _enable = new ArrayList<Boolean>();

        _iterators.add(new MillisecondIterator(100));
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);

        _iterators.add(new MillisecondIterator(500));
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);

        _iterators.add(new SecondIterator(1));
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);

        _iterators.add(new SecondIterator(5));
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);

        _iterators.add(new SecondIterator(30));
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);

        _iterators.add(new MinuteIterator(1));
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);

        _iterators.add(new MinuteIterator(10));
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);

        _iterators.add(new MinuteIterator(30));
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);

        _iterators.add(new HourIterator(3));
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);

        _iterators.add(new HourIterator(12));
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);

        _iterators.add(new DayIterator());
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);

        _iterators.add(new WeekIterator());
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);

        _iterators.add(new MonthIterator());
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);

        _iterators.add(new YearIterator());
        _formats.add(DateIterator.Format.LONG);
        _enable.add(true);
    }

    /**
     * Default constructor.
     */
    public BoxTimeScaleRenderer() {
        super(null);
        initIterators();
    }

    /**
     * Set a holidayenumerator for displaying special days as tooltip.
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
        // if pps changed check which stripes to draw
        if (_lastPPS != delegate.getPixelPerSecond()) {
            checkStrips(gc, delegate, delegate.getStartDate());
            _lastPPS = delegate.getPixelPerSecond();
        }
        if (_textHeight == -1) {
            Point p = gc.textExtent("Q");
            _textHeight = p.y;
        }

        int lineWidth = gc.getLineWidth();
        if (printing) {
            gc.setLineWidth(getDefaultLineWidth());
        }
        // each drawing operation produces new tick dates
        _majorTicks = new ArrayList<JaretDate>();
        _minorTicks = new ArrayList<JaretDate>();

        if (!delegate.hasVariableXScale()) {
            // plain scale
            drawStrips(gc, drawingArea, delegate, top, printing, delegate.getStartDate().copy(), delegate
                    .getStartDate().copy().advanceSeconds(delegate.getSecondsDisplayed() + 1));
        } else {
            // check strips for every part with different scale
            JaretDate startDate = delegate.getStartDate().copy();
            JaretDate endDate = delegate.getStartDate().copy().advanceSeconds(delegate.getSecondsDisplayed() + 1);
            List<Interval> ppsIntervals = delegate.getPpsRow().getIntervals(startDate, endDate);
            // shortcut if no ppsintervals are in the area just draw straight
            if (ppsIntervals.size() == 0) {
                drawStrips(gc, drawingArea, delegate, top, printing, delegate.getStartDate().copy(), delegate
                        .getStartDate().copy().advanceSeconds(delegate.getSecondsDisplayed() + 1));
            } else {
                JaretDate d = startDate.copy();
                while (d.compareTo(endDate) < 0) {
                    // calculate the strips for the current date in question
                    checkStrips(gc, delegate, d);
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
                    drawStrips(gc, drawingArea, delegate, top, printing, d, e);
                    d = e;
                }
                _lastPPS = -1; // force check next paint
            }
        }
        gc.setLineWidth(lineWidth); // restore linewidth
    }

    /**
     * Draw the configured strips in the area indicated.
     * 
     * @param gc GC
     * @param drawingArea complete drawing area for the scale
     * @param delegate delegate
     * @param top boolean for top position
     * @param printing true for printing
     * @param startDate start date for this part of the strip
     * @param endDate end date for this part of the strip
     */
    private void drawStrips(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, boolean top,
            boolean printing, JaretDate startDate, JaretDate endDate) {
        boolean horizontal = delegate.getOrientation().equals(TimeBarViewerInterface.Orientation.HORIZONTAL);

        Rectangle clipSave = gc.getClipping();

        if (horizontal) {
            // clip to start/end date for ensuring nothing is painted beyond the limits
            int sx = delegate.xForDate(startDate);
            Rectangle destRect = new Rectangle(sx, drawingArea.y, delegate.xForDate(endDate) - sx, drawingArea.height);
            gc.setClipping(destRect.intersection(clipSave));

            int ox = drawingArea.x;
            int basey = top ? drawingArea.y + drawingArea.height : drawingArea.y + scaleY(BOXHEIGHT);

            int drawn = 0;
            // now draw the stripes
            for (int i = 0; i < _iterators.size() && drawn < _numberOfStrips; i++) {
                DateIterator it = _iterators.get(i);
                if (_enable.get(i)) {
                    drawn++;
                    it.reInitialize(startDate, endDate.copy().advanceMillis(it.getApproxStepMilliSeconds()));
                    while (it.hasNextDate()) {
                        JaretDate d = it.getNextDate();
                        String label = it.getLabel(d, _formats.get(i));

                        // save the tic dates for the two first strips as the minor and major ticks
                        if (drawn==1) {
                            _minorTicks.add(d);
                        } else if(drawn==2) {
                            _majorTicks.add(d);
                        }

                        int x = delegate.xForDate(d);
                        gc.drawLine(x, basey, x, basey - scaleY(BOXHEIGHT));
                        int yy = (scaleY(BOXHEIGHT) - _textHeight) / 2;
                        gc.drawString(label, x + scaleX(GAP), basey - yy - _textHeight);
                    }
                    if (top) {
                        gc.drawLine(ox, basey - scaleY(BOXHEIGHT), ox + drawingArea.width, basey - scaleY(BOXHEIGHT));
                    } else {
                        gc.drawLine(ox, basey, ox + drawingArea.width, basey);
                    }
                    basey = basey + (top ? -scaleY(BOXHEIGHT) : scaleY(BOXHEIGHT));
                }
            }
        } else {
            // vertical strip
            // clip to start/end date for ensuring nothing is painted beyond the limits
            int sy = delegate.xForDate(startDate);
            Rectangle destRect = new Rectangle(drawingArea.x, sy, drawingArea.width, delegate.xForDate(endDate) - sy);
            gc.setClipping(destRect.intersection(clipSave));

            int oy = drawingArea.y;
            int basex = top ? drawingArea.x + drawingArea.width : drawingArea.x + scaleX(BOXHEIGHT);

            int drawn = 0;
            // now draw the stripes
            for (int i = 0; i < _iterators.size() && drawn < _numberOfStrips; i++) {
                DateIterator it = _iterators.get(i);
                if (_enable.get(i)) {
                    drawn++;
                    it.reInitialize(startDate, endDate.copy().advanceMillis(it.getApproxStepMilliSeconds()));
                    while (it.hasNextDate()) {
                        JaretDate d = it.getNextDate();
                        String label = it.getLabel(d, _formats.get(i));

                        // save the tic dates for the two first strips as the minor and major ticks
                        if (drawn==1) {
                            _minorTicks.add(d);
                        } else if(drawn==2) {
                            _majorTicks.add(d);
                        }

                        int y = delegate.xForDate(d);
                        gc.drawLine(basex, y, basex - scaleX(BOXHEIGHT), y);
                        int xx = (scaleX(BOXHEIGHT) - _textHeight) / 2;
                        SwtGraphicsHelper.drawStringVertical(gc, label, basex - xx - _textHeight, y + scaleY(GAP));
                    }
                    if (top) {
                        gc.drawLine(basex - scaleX(BOXHEIGHT), oy, basex - scaleX(BOXHEIGHT), oy + drawingArea.height);
                    } else {
                        gc.drawLine(basex, oy, basex, oy + drawingArea.height);
                    }
                    basex = basex + (top ? -scaleX(BOXHEIGHT) : scaleX(BOXHEIGHT));
                }
            }
        }
        gc.setClipping(clipSave);
    }

    /**
     * Check which strips should be drawn.
     * 
     * @param gc GC
     * @param delegate delegate
     * @param startDate date to use for starting check
     */
    private void checkStrips(GC gc, TimeBarViewerDelegate delegate, JaretDate startDate) {
        for (int i = 0; i < _iterators.size(); i++) {
            DateIterator it = _iterators.get(i);
            it.reInitialize(startDate, null);
            if (it.previewNextDate() != null) {
                JaretDate current = it.getNextDate();
                JaretDate next = it.getNextDate();
                int width = delegate.xForDate(next) - delegate.xForDate(current);
                String label = it.getLabel(current, DateIterator.Format.LONG);
                Point p = gc.textExtent(label);
                int bonus = _enable.get(i) && _formats.get(i).equals(DateIterator.Format.LONG) ? SETBONUS : 0;
                if (width > p.x + GAP + ADDITIONALGAP - bonus) {
                    _enable.set(i, true);
                    _formats.set(i, DateIterator.Format.LONG);
                } else {
                    label = it.getLabel(current, DateIterator.Format.MEDIUM);
                    p = gc.textExtent(label);
                    bonus = _enable.get(i) && _formats.get(i).equals(DateIterator.Format.MEDIUM) ? SETBONUS : 0;
                    if (width > p.x + GAP + ADDITIONALGAP - bonus) {
                        _enable.set(i, true);
                        _formats.set(i, DateIterator.Format.MEDIUM);
                    } else {
                        label = it.getLabel(current, DateIterator.Format.SHORT);
                        p = gc.textExtent(label);
                        bonus = _enable.get(i) && _formats.get(i).equals(DateIterator.Format.SHORT) ? SETBONUS : 0;
                        if (width > p.x + GAP + ADDITIONALGAP - bonus) {
                            _enable.set(i, true);
                            _formats.set(i, DateIterator.Format.SHORT);
                        } else {
                            _enable.set(i, false);
                        }
                    }
                }
            } else {
                _enable.set(i, false);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(TimeBarViewer tbv, Rectangle drawingArea, int x, int y) {
        String str = null;
        JaretDate date = tbv.dateForX(x);
        str = date.toDisplayString();
        if (_holidayEnumerator != null) {
            if (_holidayEnumerator.isHoliday(date.getDate()) || _holidayEnumerator.isSpecialDay(date.getDate())) {
                str += "\n" + _holidayEnumerator.getDayName(date.getDate());
            }
        }
        return str;
    }

    /**
     * {@inheritDoc}
     */
    public int getHeight() {
        if (_printer == null) {
            return _numberOfStrips * BOXHEIGHT;
        } else {
            return scaleY(_numberOfStrips * BOXHEIGHT);
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
        return new BoxTimeScaleRenderer(printer);
    }

    /**
     * retrieve the number of strips to render.
     * 
     * @return number of strips
     */
    public int getNumberOfStrips() {
        return _numberOfStrips;
    }

    /**
     * Set the maximum number of strips to render.
     * 
     * @param numberOfStrips maximum number of strips
     */
    public void setNumberOfStrips(int numberOfStrips) {
        _numberOfStrips = numberOfStrips;
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
