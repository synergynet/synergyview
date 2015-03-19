/*
 *  File: GlobalAssistantRenderer.java 
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

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;

/**
 * Interface describing a renderer that is called to do global rendering on the timebarviewer.
 * 
 * @author Peter Kliem
 * @version $Id: GlobalAssistantRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface GlobalAssistantRenderer {
    /**
     * Will be called before the interval rendering starts.
     * 
     * @param delegate delegate that calls
     * @param gc GC
     * @param printing true for printing
     */
    void doRenderingBeforeIntervals(TimeBarViewerDelegate delegate, GC gc, boolean printing);

    /**
     * Will be called after the intervals have been rendered.
     * 
     * @param delegate delegate that calls
     * @param gc GC
     * @param printing true for printing
     */
    void doRenderingLast(TimeBarViewerDelegate delegate, GC gc, boolean printing);

    /**
     * Dispose whatever has been allocated.
     * 
     */
    void dispose();

    /**
     * Produce a renderer for printing.
     * 
     * @param printer printer to use
     * @return configured renderer for printing
     */
    GlobalAssistantRenderer createPrintRenderer(Printer printer);

}
