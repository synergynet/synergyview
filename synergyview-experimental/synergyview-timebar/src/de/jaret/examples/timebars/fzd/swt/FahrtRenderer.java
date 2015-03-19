/*
 *  File: FahrtRenderer.java 
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
package de.jaret.examples.timebars.fzd.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.examples.timebars.fzd.model.Fahrt;
import de.jaret.util.date.Interval;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;

/**
 * Renderer for a "Fahrt". A "fahrt" is trip of a vehicle.
 * 
 * @author Peter Kliem
 * @version $Id: FahrtRenderer.java 801 2008-12-27 22:44:54Z kliem $
 */
public class FahrtRenderer implements TimeBarRenderer {

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected,
            boolean printing, boolean overlap) {
        if (printing) {
            throw new RuntimeException("printing not supported");
        }

        Color bg = gc.getBackground();
        Color fg = gc.getForeground();

        if (interval instanceof Fahrt) {
            Fahrt fahrt = (Fahrt) interval;
            drawFahrt(gc, drawingArea, fahrt, selected);
        } else {
            throw new RuntimeException("unsupported");
        }
        gc.setBackground(bg);
        gc.setForeground(fg);
    }

    public void drawFahrt(GC gc, Rectangle drawingArea, Fahrt fahrt, boolean selected) {
        int height = drawingArea.height;
        int width = drawingArea.width;
        int ox = drawingArea.x;
        int oy = drawingArea.y;

        int y = height / 3;
        int bheight = height / 3;
        int yend = y + bheight;

        // balken
        if (selected) {
            gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        } else {
            gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
        }
        // body
        gc.fillRectangle(ox, oy + y, width - 1, bheight);
        // Rahmen
        gc.drawRectangle(ox, oy + y, width - 1, bheight);

        String name = fahrt.getFahrtNummer();
        int twidth = SwtGraphicsHelper.getStringDrawingWidth(gc, name);
        // Balkenbeschriftung nur wenn sie passt
        if (width > twidth + 2) {
            SwtGraphicsHelper.drawStringCenteredVCenter(gc, name, ox, ox + width, oy + height / 2);
        }
        // beginn- und endeort
        int th = gc.textExtent(fahrt.getBeginOrt()).y;
        gc.drawText(fahrt.getBeginOrt(), ox, oy + y - th, true);
        SwtGraphicsHelper.drawStringRightAlignedVTop(gc, fahrt.getEndeOrt(), ox + width - 1, oy + yend);

    }


    /**
     * {@inheritDoc}
     */
    public String getToolTipText(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        int height = drawingArea.height;
        int width = drawingArea.width;
        int ox = drawingArea.x;
        int oy = drawingArea.y;
        if (interval instanceof Fahrt) {
            int yy = oy + height / 3;
            int bheight = height / 3;
            return oy + y >= yy && oy + y <= yy + bheight && ox + x >= ox && ox + x <= ox + width;
        } else {
            int yy = oy + height / 5;
            int bheight = (height / 5) * 3;
            return oy + y >= yy && oy + y <= yy + bheight && ox + x >= ox && ox + x <= ox + width;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getContainingRectangle(Interval interval, Rectangle drawingArea, boolean overlapping) {
        int height = drawingArea.height;
        int width = drawingArea.width;
        int ox = drawingArea.x;
        int oy = drawingArea.y;
        if (interval instanceof Fahrt) {
            int yy = oy + height / 3;
            int bheight = height / 3;
            return new Rectangle(ox, yy, width, bheight);
        } else {
            int yy = oy + height / 5;
            int bheight = (height / 5) * 3;
            return new Rectangle(ox, yy, width, bheight);
        }
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
    public TimeBarRenderer createPrintrenderer(Printer printer) {
        throw new RuntimeException("printing not supported");
    }

}
