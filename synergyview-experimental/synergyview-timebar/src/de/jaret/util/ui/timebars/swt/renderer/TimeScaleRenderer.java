/*
 *  File: TimeScaleRenderer.java 
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

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * Interface for a renderer rending the timescale (x axis). The renderer can (should) support printing.
 * 
 * @author Peter Kliem
 * @version $Id: TimeScaleRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeScaleRenderer {
    /**
     * Draw the Timescale.
     * 
     * @param gc GraphicsContext to paint with.
     * @param drawingArea Rectangel denoting the area to draw in. The gc is clipped to this area.
     * @param delegate TimeBarViewerDelegate exposed for callbacks
     * @param top if true the tmiescale is in position top, bottom otherwise
     * @param printing flag to indicate printing.
     */
    void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, boolean top, boolean printing);

    /**
     * Retrieve the tooltip text for a given locatin in the timescale.
     * 
     * @param tbv TimeBarViewer
     * @param drawingArea area in that the timescale has been painted
     * @param x x coordinate
     * @param y y coordinate
     * @return tooltip text or <code>null</code>
     */
    String getToolTipText(TimeBarViewer tbv, Rectangle drawingArea, int x, int y);

    /**
     * Returns the prferred height for rendering.
     * 
     * @return preferred height
     */
    int getHeight();

    /**
     * Dispose resources.
     * 
     */
    void dispose();

    /**
     * Create a timescale renderer setup for printing.
     * 
     * @param printer Printer device taht will be used.
     * @return timescale renderer setup for printing.
     */
    TimeScaleRenderer createPrintRenderer(Printer printer);
}
