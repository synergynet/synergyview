/*
 *  File: GlobalBreakRenderer.java 
 *  Copyright (c) 2004-2009  Peter Kliem (Peter.Kliem@jaret.de)
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
package de.jaret.examples.timebars.millis.swt.renderer;

import java.awt.Rectangle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.model.PPSInterval;
import de.jaret.util.ui.timebars.swt.renderer.GlobalAssistantRenderer;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;

/**
 * GlobalAssistantRenderer that renders breaks in the time line.
 * 
 * @author kliem
 * @version $Id: GlobalBreakRenderer.java 836 2009-02-14 21:24:39Z kliem $
 */
public class GlobalBreakRenderer extends RendererBase implements GlobalAssistantRenderer {
    /** Color used to draw the gaps. */
    protected Color _gapColor;

    /**
     * Construct the renderer for a printer.
     * 
     * @param printer printer device
     */
    public GlobalBreakRenderer(Printer printer) {
        super(printer);
    }

    /**
     * Construct the renderer for diplay use.
     */
    public GlobalBreakRenderer() {
        super(null);
    }

    /**
     * {@inheritDoc}
     */
    public GlobalAssistantRenderer createPrintRenderer(Printer printer) {
        return new GlobalBreakRenderer(printer);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        if (_gapColor != null) {
            _gapColor.dispose();
        }
    }

    /**
     * {@inheritDoc} Nothing to do.
     */
    public void doRenderingBeforeIntervals(TimeBarViewerDelegate delegate, GC gc, boolean printing) {
        // do nothing
    }

    /**
     * {@inheritDoc} Renders the gaps as gray areas.
     */
    public void doRenderingLast(TimeBarViewerDelegate delegate, GC gc, boolean printing) {
        // safety check
        if (!delegate.hasVariableXScale()) {
            return;
        }
        aquireColors(gc.getDevice());
        Color bg = gc.getBackground();

        gc.setBackground(_gapColor);

        Rectangle rect = delegate.getDiagramRect();
        Interval display = new IntervalImpl(delegate.getStartDate().copy(), delegate.getEndDate().copy());
        for (Interval interval : delegate.getPpsRow().getIntervals()) {
            PPSInterval ppsInterval = (PPSInterval) interval;
            if (ppsInterval.isBreak() && display.intersects(ppsInterval)) {
                int startx = delegate.xForDate(ppsInterval.getBegin());
                int endx = delegate.xForDate(ppsInterval.getEnd());
                if (startx < rect.x) {
                    startx = rect.x;
                }
                if (endx > rect.x + rect.width) {
                    endx = rect.x + rect.width;
                }

                int starty = rect.y;
                int endy = rect.y + rect.height;

                // draw the area
                org.eclipse.swt.graphics.Rectangle r = new org.eclipse.swt.graphics.Rectangle(startx, starty, endx
                        - startx, endy);
                gc.fillRectangle(r);

                // draw dashed lines
                gc.setLineStyle(SWT.LINE_DASH);
                gc.drawLine(startx, starty, startx, endy);
                gc.drawLine(endx, starty, endx, endy);

            }
        }

        gc.setLineStyle(SWT.LINE_SOLID);
        gc.setBackground(bg);

    }

    /**
     * Get the colors needed.
     * 
     * @param device device to aquire the colors for
     */
    private void aquireColors(Device device) {
        if (_gapColor == null) {
            _gapColor = new Color(device, 220, 220, 220);
        }
    }

}
