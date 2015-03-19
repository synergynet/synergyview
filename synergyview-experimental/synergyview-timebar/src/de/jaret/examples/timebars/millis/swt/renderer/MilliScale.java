/*
 *  File: MilliScale.java 
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

import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;
import de.jaret.util.ui.timebars.swt.renderer.TimeScaleRenderer;

/**
 * Scale for the milli second accuracy example. Regions with special scale will not painted.
 * 
 * @author Peter Kliem
 * @version $Id: MilliScale.java 826 2009-02-07 12:49:00Z kliem $
 */
public class MilliScale extends RendererBase implements TimeScaleRenderer {
    public static final boolean SUPPRESS_WHEN_SCALED = true;
    public static final long MAJOR = 10;
    public static final long MINOR = 1;

    private long _major = MAJOR;
    private long _minor = MINOR;

    public MilliScale(Printer printer) {
        super(printer);
    }

    public MilliScale() {
        super(null);
    }

    public MilliScale(long minor, long major) {
        super(null);
        _major = major;
        _minor = minor;
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, boolean top, boolean printing) {
        int ox = drawingArea.x;

        int basey;
        int minorOff;
        int majorOff;
        int majorLabelOff;

        if (!top) {
            basey = drawingArea.y;
            minorOff = scaleY(5);
            majorOff = scaleY(10);
            majorLabelOff = scaleY(22);
        } else {
            basey = drawingArea.y + drawingArea.height - 1;
            minorOff = scaleY(-5);
            majorOff = scaleY(-10);
            majorLabelOff = scaleY(-10);
        }
        int oy = basey;

        int width = drawingArea.width;
        JaretDate date = delegate.getStartDate().copy();

        while (date.diffMilliSeconds(delegate.getMinDate()) % _major != 0) {
            date.advanceMillis(-1);
        }

        JaretDate save = date.copy();

        if (printing) {
            gc.setLineWidth(1);
        }
        // draw top/bottom line
        gc.drawLine(ox, oy, ox+width, oy);
        
        // draw the minor ticks
        while (delegate.xForDate(date) < ox+width) {
            JaretDate eosc = endOfSpecialScaling(delegate, date);
            if (eosc == null || !SUPPRESS_WHEN_SCALED) {
                int x = delegate.xForDate(date);
                gc.drawLine(x, oy, x, oy + minorOff);
                date.advanceMillis(_minor);
            } else {
                long diff = eosc.diffMilliSeconds(date);
                date.advanceMillis(diff+_minor);
            }
        }

        date = save.copy();
        // draw the major ticks
        while (delegate.xForDate(date) < ox+width) {
            JaretDate eosc = endOfSpecialScaling(delegate, date);
            if (eosc == null || !SUPPRESS_WHEN_SCALED) {
                int x = delegate.xForDate(date);
                gc.drawLine(x, oy, x, oy + majorOff);
                date.advanceMillis(_major);
            } else {
                long diff = eosc.diffMilliSeconds(date);
                date.advanceMillis(diff+_major);
            }
        }

        gc.setLineWidth(1);

        // labels: draw every two major ticks
        date = save.copy();
        // Labels are drawn beyond the width. Otherwise when the beginning of
        // the labels
        // would not be drawn when the tick itself is out of sight
        while (delegate.xForDate(date) < width + 100) {
            JaretDate eosc = endOfSpecialScaling(delegate, date);
            if (eosc == null  || !SUPPRESS_WHEN_SCALED) {
                int x = delegate.xForDate(date);
                if (date.diffMilliSeconds(delegate.getMinDate()) % _major == 0) {
                    // Second line
                    String str = date.getDate().getTime()+"";//date.diffMilliSeconds(delegate.getMinDate()) + "";

                    // draw
                    if (x > SwtGraphicsHelper.getStringDrawingWidth(gc, str) / 2) {
                        SwtGraphicsHelper.drawStringCentered(gc, str, x, oy + majorLabelOff);
                    }
                }
                date.advanceMillis(_major);
            } else {
                long diff = eosc.diffMilliSeconds(date);
                date.advanceMillis(diff+_major+1);
                while (date.diffMilliSeconds(delegate.getMinDate()) % _major != 0) {
                    date.advanceMillis(-1);
                }

            }
        }

    }

    /**
     * If the given date hits a scaled area, return the end date of that area.
     * 
     * @param delegate
     * @return end of special scaled area or null
     */
    public static JaretDate endOfSpecialScaling(TimeBarViewerDelegate delegate, JaretDate date) {
        if (!delegate.hasVariableXScale()) {
            return null;
        } else {
            List<Interval> l = delegate.getPpsRow().getIntervals(date);
            if (l == null || l.size() == 0) {
                return null;
            } else {
                return l.get(0).getEnd();
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(TimeBarViewer tbv, Rectangle drawingArea, int x, int y) {
        String str = null;
        JaretDate date = tbv.dateForX(x);
        long millis = date.diffMilliSeconds(tbv.getStartDate());
        str = "" + millis;

        return str;
    }

    /**
     * {@inheritDoc}
     */
    public int getHeight() {
        if (_printer == null) {
            return 50;
        } else {
            return scaleY(50);
        }
    }

    public void dispose() {
        // nothing to dispose
    }

    public void print(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, boolean top) {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    public TimeScaleRenderer createPrintRenderer(Printer printer) {
        return new MilliScale(printer);
    }

}
