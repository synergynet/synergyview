/*
 *  File: TimeBarMarkerRenderer.java 
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
import org.eclipse.swt.printing.Printer;

import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;

/**
 * Interface for drawing markers in a timebar viewer.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarMarkerRenderer.java 821 2009-02-04 21:12:16Z kliem $
 */
public interface TimeBarMarkerRenderer {
    /**
     * Darw a marker.
     * 
     * @param gc GC to use
     * @param delegate TimeBarViewerDelegate for retrieving information.
     * @param marker marker to be drawn
     * @param isDragged true if the marker is currently dragged
     * @param printing flag indicating that the draw operation is for a printer
     */
    void draw(GC gc, TimeBarViewerDelegate delegate, TimeBarMarker marker, boolean isDragged, boolean printing);

    /**
     * Get the width for the marker rendering.
     * 
     * @param marker the marker to get the width for
     * @return width for rendering
     */
    int getWidth(TimeBarMarker marker);

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
    TimeBarMarkerRenderer createPrintRenderer(Printer printer);

}
