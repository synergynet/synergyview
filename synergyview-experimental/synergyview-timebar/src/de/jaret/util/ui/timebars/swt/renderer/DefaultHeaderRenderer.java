/*
 *  File: DefaultHeaderRenderer.java 
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;

/**
 * Simple default header renderer.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultHeaderRenderer.java 781 2008-09-20 20:10:18Z kliem $
 */
public class DefaultHeaderRenderer extends RendererBase implements HeaderRenderer {

    /**
     * Constructor for printing use.
     * 
     * @param printer printing device
     */
    public DefaultHeaderRenderer(Printer printer) {
        super(printer);
    }

    /**
     * Constructor for screen use.
     * 
     */
    public DefaultHeaderRenderer() {
        super(null);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, TimeBarRowHeader header,
            boolean selected, boolean printing) {

        String str = header.toString();
        Color bg = gc.getBackground();
        Color fg = gc.getForeground();
        if (selected && !printing) {
            gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
            gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        }
        gc.fillRectangle(drawingArea);
        SwtGraphicsHelper.drawStringCenteredVCenter(gc, str, drawingArea.x, drawingArea.x + drawingArea.width,
                drawingArea.y + drawingArea.height / 2);

        gc.setBackground(bg);
        gc.setForeground(fg);
        if (delegate.getDrawRowGrid()) {
            if (printing) {
                gc.setLineWidth(getDefaultLineWidth());
            }
            if (delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL) {
                gc.drawLine(drawingArea.x, drawingArea.y + drawingArea.height - 1, drawingArea.x + drawingArea.width,
                        drawingArea.y + drawingArea.height - 1);
            } else {
                gc.drawLine(drawingArea.x + drawingArea.width - 1, drawingArea.y,
                        drawingArea.x + drawingArea.width - 1, drawingArea.y + drawingArea.height - 1);
            }
            gc.setLineWidth(1);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(TimeBarRow row, Rectangle drawingArea, int x, int y) {
        if (row == null) {
            return null;
        }
        return row.getRowHeader().getLabel();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Rectangle drawingArea, int x, int y) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        // nothing to dispose
    }

    /**
     * {@inheritDoc}
     */
    public HeaderRenderer createPrintRenderer(Printer printer) {
        return new DefaultHeaderRenderer(printer);
    }
}
