/*
 *  File: TimeBarGapRenderer.java 
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
import de.jaret.util.ui.timebars.model.TimeBarRow;

/**
 * Interface describing a renderer for rendering in beetween two intervals.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarGapRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeBarGapRenderer {
    /**
     * Draw the gap decorator.
     * 
     * @param gc GC to use
     * @param delegate TimeBarViewerDelaget
     * @param row row in which the intervals are located
     * @param interval1 left interval (may be null)
     * @param interval2 right interval (may be null)
     * @param drawingArea drawing area in beetween the intervals
     * @param printing flag indicating that the drawing is don efor a printer
     */
    void draw(GC gc, TimeBarViewerDelegate delegate, TimeBarRow row, Interval interval1, Interval interval2,
            Rectangle drawingArea, boolean printing);

    /**
     * Dispose any resources.
     * 
     */
    void dispose();

    /**
     * Create a similar renderer for printing. The creation should copy settings made to the producing renderer.
     * 
     * @param printer Printer device
     * @return a configured renderer for printing.
     */
    TimeBarGapRenderer createPrintRenderer(Printer printer);

}
