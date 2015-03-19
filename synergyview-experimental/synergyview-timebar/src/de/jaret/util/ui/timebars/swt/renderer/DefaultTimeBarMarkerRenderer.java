/*
 *  File: DefaultTimeBarMarkerRenderer.java 
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
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;

/**
 * Simple default renderer for time bar markers. Renders them as a simple vertical line.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultTimeBarMarkerRenderer.java 821 2009-02-04 21:12:16Z kliem $
 */
public class DefaultTimeBarMarkerRenderer extends RendererBase implements TimeBarMarkerRenderer {
    /** color when dragging. */
    private static final Color MARKER_ACTIVE_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);

    /** normal color. */
    private static final Color MARKER_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_RED);

    /**
     * Cunstructor for printer use.
     * 
     * @param printer printer device
     */
    public DefaultTimeBarMarkerRenderer(Printer printer) {
        super(printer);
    }

    /**
     * Constructor for screen use.
     * 
     */
    public DefaultTimeBarMarkerRenderer() {
        super(null);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, TimeBarViewerDelegate tbv, TimeBarMarker marker, boolean isDragged, boolean printing) {
        boolean horizontal = tbv.getOrientation().equals(TimeBarViewerInterface.Orientation.HORIZONTAL);
        Color oldCol = gc.getForeground();

        if (!printing) {
            if (isDragged) {
                gc.setForeground(MARKER_ACTIVE_COLOR);
            } else {
                gc.setForeground(MARKER_COLOR);
            }
        } else {
            gc.setForeground(_printer.getSystemColor(SWT.COLOR_DARK_MAGENTA));
            gc.setLineWidth(getDefaultLineWidth());
        }

        if (horizontal) {
            int startY = Math.min(tbv.getXAxisRect().y, tbv.getDiagramRect().y);
            int x = tbv.xForDate(marker.getDate());
            int height = tbv.getXAxisRect().height + tbv.getDiagramRect().height;
            gc.drawLine(x, startY, x, startY + height);
        } else {
            int startX = Math.min(tbv.getXAxisRect().x, tbv.getDiagramRect().x);
            int y = tbv.xForDate(marker.getDate());
            int width = tbv.getXAxisRect().width + tbv.getDiagramRect().width;
            gc.drawLine(startX, y, startX + width, y);
        }
        gc.setLineWidth(1);
        gc.setForeground(oldCol);

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
    public int getWidth(TimeBarMarker marker) {
        return scaleX(4);
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarMarkerRenderer createPrintRenderer(Printer printer) {
        return new DefaultTimeBarMarkerRenderer(printer);
    }

}
