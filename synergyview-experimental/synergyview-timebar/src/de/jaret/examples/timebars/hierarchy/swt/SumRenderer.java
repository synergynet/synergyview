/*
 *  File: SumRenderer.java 
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
package de.jaret.examples.timebars.hierarchy.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;

/**
 * A simple sum renderer.
 * 
 * @author Peter Kliem
 * @version $Id: SumRenderer.java 813 2009-01-11 20:45:56Z kliem $
 */
public class SumRenderer extends RendererBase implements TimeBarRenderer {
    public SumRenderer(Printer printer) {
        super(printer);
    }

    public SumRenderer() {
        super(null);
    }

    private static final int WIDTH = 2;

    int limitCoord(int coord) {
    	if (coord<-100) {
    		coord = -100;
    	} else if (coord>15000) {
    		coord = 15000;
    	}
    	return coord;
    }
    void limitCoord(int[] coords) {
    	for (int i=0;i<coords.length;i++) {
    		coords[i] = limitCoord(coords[i]);
    	}
    }
    
    Rectangle limitCoord(Rectangle rect) {
    	if (rect.x<-100) {
    		rect.width = rect.width+rect.x+100;
    		rect.x= -100;
    	}
    	if (rect.width>15000) {
    		rect.width = 15000;
    	}
    	return rect;
    }
    
    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected,
            boolean printing, boolean overlap) {
        if (printing) {
            print(gc, drawingArea, delegate, interval);
        } else {
            int y = drawingArea.y + drawingArea.height / 2 - WIDTH;
            int h = WIDTH;

            Color bg = gc.getBackground();
            if (!selected) {
                gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
            } else {
                gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
            }
            
            drawingArea = limitCoord(drawingArea);
            
            gc.fillRectangle(drawingArea.x, y, drawingArea.width - 1, h);
            
            int left[] = { drawingArea.x, y + h, drawingArea.x, y + h + 7, drawingArea.x + 4, y + h };
            int right[] = { drawingArea.x + drawingArea.width - 1, y + h, drawingArea.x + drawingArea.width - 1,
                    y + h + 7, drawingArea.x + drawingArea.width - 1 - 4, y + h };
            
            gc.fillPolygon(left);
            gc.fillPolygon(right);

            gc.setBackground(bg);
        }
    }

    public void print(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval) {

        int y = drawingArea.y + drawingArea.height / 2 - scaleY(WIDTH);
        int h = scaleY(WIDTH);

        Color bg = gc.getBackground();
        gc.setBackground(_printer.getSystemColor(SWT.COLOR_BLACK));
        gc.fillRectangle(drawingArea.x, y, drawingArea.width - 1, h);

        int left[] = { drawingArea.x, y + h, drawingArea.x, y + h + scaleY(7), drawingArea.x + scaleX(4), y + h };
        int right[] = { drawingArea.x + drawingArea.width - 1, y + h, drawingArea.x + drawingArea.width - 1,
                y + h + scaleY(7), drawingArea.x + drawingArea.width - 1 - scaleX(4), y + h };

        gc.fillPolygon(left);
        gc.fillPolygon(right);

        gc.setBackground(bg);
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        if (contains(interval, drawingArea, x, y, false)) {
            return interval.toString();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        int yy = drawingArea.height / 3;
        int h = drawingArea.height / 3;
        return y >= yy && y <= yy + h;
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getContainingRectangle(Interval interval, Rectangle drawingArea, boolean overlapping) {
        int y = drawingArea.y + drawingArea.height / 3;
        int h = drawingArea.height / 3;

        return new Rectangle(drawingArea.x, y, drawingArea.width - 1, h);
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
        return new SumRenderer(printer);
    }

}
