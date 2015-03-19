/*
 *  File: TimeBarRenderer.java 
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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;

/**
 * Renderer for rendering intervals in the time bar viewer.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeBarRenderer {
    /**
     * Render the given interval. Flags show if the rendering is done for printing and whether the interval is alone in
     * its loation (overlap = false).
     * 
     * @param gc GC
     * @param drawingArea the rectangle to render the interval in.
     * @param delegate the viewer delegate
     * @param interval the interval to be rendered
     * @param selected true if the interval is selected
     * @param printing true if rendering is done for a prinetr
     * @param overlap true if the interval is drawn as one of several intervals that overlap while beeing drawn.
     */
    void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected,
            boolean printing, boolean overlap);

    /**
     * Retrieve the tooltip text for the interval. Coordniates and drwaing area are given, so it is possible to return
     * different tooltips for different locations on the rendered interval.
     * 
     * @param interval the interval
     * @param drawingArea area the interval has been randered in
     * @param x x coordinate in the drawing area (relative)
     * @param y y coordniate in the drawing area (relative)
     * @param overlapping true if the interval is not alone at this location
     * @return tooltip text or <code>null</code> indicating no tooltip should be displayed
     */
    String getToolTipText(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping);

    /**
     * Check whether a given coordinate is contained in the rendered interval. This is used for exact selection.
     * 
     * @param interval the interval
     * @param drawingArea area the interval has been randered in
     * @param x x coordinate in the drawing area
     * @param y y coordniate in the drawing area
     * @param overlapping true if overlapping occurred
     * @return true if the coordinate belongs to the interval representation
     */
    boolean contains(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping);

    /**
     * Retrieve the bounding rectangle of the interval rendering.
     * 
     * @param interval the interval
     * @param drawingArea area the interval has been randered in
     * @param overlapping true if overlapping occurred
     * @return containing rectangle of the interval's representation
     */
    Rectangle getContainingRectangle(Interval interval, Rectangle drawingArea, boolean overlapping);

    /**
     * Dispose the renderer. Should free up any resources locked.
     * 
     */
    void dispose();

    /**
     * Create a similar renderer for printing. The creation should copy settings made to the producing renderer.
     * 
     * @param printer Printer device
     * @return a configured renderer for printing.
     */
    TimeBarRenderer createPrintrenderer(Printer printer);
}
