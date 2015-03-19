/*
 *  File: RendererBase.java 
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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;

/**
 * Base implementation for renderer that support both screen and printer rendering. It's main purpose is scaling
 * beetween screen and printer coordinates (based on a 96dpi for the screen)
 * 
 * @author Peter Kliem
 * @version $Id: RendererBase.java 781 2008-09-20 20:10:18Z kliem $
 */
public class RendererBase {
    /** assumed dpi value for displays. */
    private static final double ASSUMED_DISPLAY_DPI = 96.0;

    /** printer device if constructed for a printer. */
    protected Printer _printer;

    /** scale factor for valus on the x axis. */
    protected double _scaleX = 1.0;

    /** scale factor for values on the y axis. */
    protected double _scaleY = 1.0;

    /**
     * May be constructed without printer (supplying null).
     * 
     * @param printer or <code>null</code>
     */
    public RendererBase(Printer printer) {
        _printer = printer;
        if (_printer != null) {
            Point dpi = _printer.getDPI();
            _scaleX = (double) dpi.x / ASSUMED_DISPLAY_DPI;
            _scaleY = (double) dpi.y / ASSUMED_DISPLAY_DPI;
        }
    }

    /**
     * Scale an x value.
     * 
     * @param in x val to scale
     * @return scaled value
     */
    public int scaleX(int in) {
        return (int) Math.round(_scaleX * (double) in);
    }

    /**
     * Retrieve the x scale factor.
     * 
     * @return scale factor x
     */
    public double getScaleX() {
        return _scaleX;
    }

    /**
     * Scale an y value.
     * 
     * @param in y val to scale
     * @return scaled value
     */
    public int scaleY(int in) {
        return (int) Math.round(_scaleY * (double) in);
    }

    /**
     * Retruve the y scale value.
     * 
     * @return y scale factor
     */
    public double getScaleY() {
        return _scaleY;
    }

    /**
     * Retrieve the printer device if set.
     * 
     * @return printer device or <code>null</code>
     */
    public Printer getPrinter() {
        return _printer;
    }

    /**
     * Draw focus marking when not printing and focussed.
     * 
     * @param gc GC
     * @param drawingArea rectangular drawing area
     * @param delegate tbv delegate
     * @param interval interval that maybe in focus
     * @param selected true if the interval is selecetd
     * @param printing true if operating for a printer gc
     * @param overlap true if interval overlaps with otzher intervals
     */
    public void drawFocus(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval,
            boolean selected, boolean printing, boolean overlap) {
        // no selection when printing
        if (printing) {
            return;
        }
        if (delegate.isFocussed(interval)) {
            gc.drawFocus(drawingArea.x, drawingArea.y, drawingArea.width, drawingArea.height);
        }
    }

    /**
     * Retrieve a correctd linewidth taking printer resolution in account when printing.
     * 
     * @return correctd linewidth
     */
    public int getDefaultLineWidth() {
        if (_printer == null) {
            return 1;
        } else {
            int width = scaleX(1);
            if (width <= 0) {
                width = 1;
            }
            return width;
        }
    }

}
