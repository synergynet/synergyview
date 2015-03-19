/*
 *  File: TitleRenderer.java 
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

/**
 * Interface for a renderer simply rendering the title area of the time bar viewer.
 * 
 * @author Peter Kliem
 * @version $Id: TitleRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TitleRenderer {

    /**
     * Daw the title.
     * 
     * @param gc GC
     * @param drawingArea area to draw in
     * @param delegate asking delegate
     * @param title title to draw
     * @param printing true if this is a printing operation
     */
    void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, String title, boolean printing);

    /**
     * Create a similar renderer for printing. The creatin should copy settings made to the producing renderer.
     * 
     * @param printer Printer device
     * @return a configured renderer for printing.
     */
    TitleRenderer createPrintRenderer(Printer printer);

    /**
     * Dispose whatever there is.
     * 
     */
    void dispose();
}
