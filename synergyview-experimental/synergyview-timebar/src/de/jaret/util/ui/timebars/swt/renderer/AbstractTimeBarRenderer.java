/*
 *  File: AbstractTimeBarRenderer.java 
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

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;

/**
 * Abstract base class for TimeBarRenderers.
 * 
 * @author kliem
 * @version $Id: AbstractTimeBarRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public abstract class AbstractTimeBarRenderer extends RendererBase implements TimeBarRenderer, TimeBarRenderer2 {

    /**
     * Construct the base for a printer.
     * 
     * @param printer printer
     */
    public AbstractTimeBarRenderer(Printer printer) {
        super(printer);
    }

    /**
     * {@inheritDoc} Default implementation simply returns the intervalDrawingArea.
     */
    public Rectangle getPreferredDrawingBounds(Rectangle intervalDrawingArea, TimeBarViewerDelegate delegate,
            Interval interval, boolean selected, boolean printing, boolean overlap) {
        return intervalDrawingArea;
    }

}
