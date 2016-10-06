/*
 *  File: DefaultMiscRenderer.java 
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * Default implementation of the IMiscRenderer for SWT.
 * 
 * @author kliem
 * @version $Id: DefaultMiscRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public class DefaultMiscRenderer extends RendererBase implements IMiscRenderer {

    /**
     * Default constructor.
     */
    public DefaultMiscRenderer() {
        super(null);
    }

    /**
     * Construct the renderer for printing.
     * 
     * @param printer the printer device.
     */
    public DefaultMiscRenderer(Printer printer) {
        super(printer);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
    }

    /**
     * {@inheritDoc}
     */
    public void renderRegionSelection(GC gc, TimeBarViewer tbv, TimeBarViewerDelegate delegate) {
        if (delegate.getRegionRect() != null) {
            Rectangle rect = delegate.calcRect(delegate.getRegionRect());

            // save
            Color bg = gc.getBackground();
            int alpha = gc.getAlpha();

            gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
            gc.setAlpha(100);
            gc.fillRectangle(rect.x, rect.y, rect.width, rect.height);
            gc.setAlpha(255);
            gc.drawRectangle(rect.x, rect.y, rect.width, rect.height);

            // restore
            gc.setAlpha(alpha);
            gc.setBackground(bg);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void renderSelectionRect(GC gc, Rectangle selRect) {
        Color fg = gc.getForeground();
        gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_DARK_MAGENTA));
        gc.drawRectangle(selRect.x, selRect.y, selRect.width, selRect.height);
        gc.setForeground(fg);
    }

    /**
     * {@inheritDoc}
     */
    public IMiscRenderer createPrintRenderer(Printer printer) {
        return new DefaultMiscRenderer(printer);
    }

}
