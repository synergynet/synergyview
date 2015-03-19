/*
 *  File: HeaderRenderer.java 
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
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;

/**
 * This interface describes a renderer used to render the header of a row.
 * 
 * @author Peter Kliem
 * @version $Id: HeaderRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface HeaderRenderer {
    /**
     * Draw the header.
     * 
     * @param gc GC to use (clipped to drawing area)
     * @param drawingArea Rectangle to paint within
     * @param delegate TimeBarViewerDelegate for retrieving some support
     * @param header the header to paint
     * @param selected true if the row is selected
     * @param printing flag indicating the current drawing is done for a printer
     */
    void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, TimeBarRowHeader header, boolean selected,
            boolean printing);

    /**
     * Retrieve the tooltip for a position inside the header.
     * 
     * @param row the header is for
     * @param drawingArea area in which the header has been drawn
     * @param x x of the position in question
     * @param y of the position in question
     * @return the tooltip for the position or <code>null</code>
     */
    String getToolTipText(TimeBarRow row, Rectangle drawingArea, int x, int y);

    /**
     * Check whether a position in the header should be active for selecting.
     * 
     * @param drawingArea area in which the header has been drawn
     * @param x x of the position in question
     * @param y of the position in question
     * @return true if this position hits an active part leeding to selection of the row.
     */
    boolean contains(Rectangle drawingArea, int x, int y);

    /**
     * Dispose the renderer. Should free up any resources locked.
     * 
     */
    void dispose();

    /**
     * Create a similar renderer for printing. The creatin should copy settings made to the producing renderer.
     * 
     * @param printer Printer device
     * @return a configured renderer for printing.
     */
    HeaderRenderer createPrintRenderer(Printer printer);
}
