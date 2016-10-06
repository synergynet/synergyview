/*
 *  File: IMiscRenderer.java 
 *  Copyright (c) 2004-2008  Peter Kliem (Peter.Kliem@jaret.de)
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

import java.awt.Rectangle;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * Interface describing several rendering routines for the TimeBarViewer.
 * 
 * @author kliem
 * @version $Id: IMiscRenderer.java 797 2008-12-27 14:21:37Z kliem $
 */
public interface IMiscRenderer {

    /**
     * Render the region selection.
     * 
     * @param gc GC to use
     * @param tbv viewer
     * @param delegate delegate
     */
    void renderRegionSelection(GC gc, TimeBarViewer tbv, TimeBarViewerDelegate delegate);

    /**
     * Render the selection rectangle.
     * 
     * @param gc to paint with
     * @param rect retangle
     */
    void renderSelectionRect(GC gc, Rectangle rect);

    /**
     * Create a renderre suitable for printing.
     * 
     * @param printer the printer device to use
     * @return a configured misc renderer ready for printing
     */
    IMiscRenderer createPrintRenderer(Printer printer);

    /**
     * Dispose whatever ressources have been allocated.
     */
    void dispose();
}
