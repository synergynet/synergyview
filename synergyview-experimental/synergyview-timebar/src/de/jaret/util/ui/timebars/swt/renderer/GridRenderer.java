/*
 *  File: GridRenderer.java 
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
import de.jaret.util.ui.timebars.strategy.ITickProvider;

/**
 * Renderer for drawing the background (grid) of the TimeBarViewer. The grid renderer cares for the vertical structure
 * (draw) and has two additional methods for row based rendering that includes drawing a row highlight and the row
 * selection and allow for simple row based effect rendernigs as well.
 * 
 * 
 * @author Peter Kliem
 * @version $Id: GridRenderer.java 836 2009-02-14 21:24:39Z kliem $
 */
public interface GridRenderer {
    /**
     * Draw the grid.
     * 
     * @param gc GC to paint on.
     * @param delegate TimeBarViewerDelegate supplying information.
     * @param drawingArea background area.
     * @param printing flag indicating that the paint operation is for a printer.
     */
    void draw(GC gc, TimeBarViewerDelegate delegate, Rectangle drawingArea, boolean printing);

    /**
     * Do row painting BEFORE intervals are drawn. This method (or the "after" method) is responsible for drawing
     * highlighted rows and row selection.
     * 
     * @param gc GC
     * @param delegate time bar viewer delegate
     * @param drawingArea area the row occupies on the screen
     * @param row the row
     * @param selected <code>true</code> if the row is selected
     * @param printing <code>true</code> if printing
     */
    void drawRowBeforeIntervals(GC gc, TimeBarViewerDelegate delegate, Rectangle drawingArea, TimeBarRow row,
            boolean selected, boolean printing);

    /**
     * Do row painting AFTER intervals are drawn. This method (or the "before" method) is responsible for drawing
     * highlighted rows and row selection.
     * 
     * @param gc GC
     * @param delegate time bar viewer delegate
     * @param drawingArea area the row occupies on the screen
     * @param row the row
     * @param selected <code>true</code> if the row is selected
     * @param printing <code>true</code> if printing
     */
    void drawRowAfterIntervals(GC gc, TimeBarViewerDelegate delegate, Rectangle drawingArea, TimeBarRow row,
            boolean selected, boolean printing);

    
    /**
     * Set a tick provider the grid renderer may use.
     * @param tickProvider the tick provider
     */
    void setTickProvider(ITickProvider tickProvider);
    
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
    GridRenderer createPrintRenderer(Printer printer);
}
