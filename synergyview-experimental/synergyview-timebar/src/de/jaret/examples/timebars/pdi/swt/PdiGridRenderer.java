/*
 *  File: PdiGridRenderer.java 
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
package de.jaret.examples.timebars.pdi.swt;

import java.util.Calendar;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.examples.timebars.pdi.model.PdiCalendar;
import de.jaret.examples.timebars.pdi.model.PdiDay;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.strategy.ITickProvider;
import de.jaret.util.ui.timebars.swt.renderer.AbstractGridRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultGridRenderer;
import de.jaret.util.ui.timebars.swt.renderer.GridRenderer;

/**
 * @author Peter Kliem
 * @version $Id: PdiGridRenderer.java 855 2009-04-02 18:44:00Z kliem $
 */
public class PdiGridRenderer extends AbstractGridRenderer implements GridRenderer {
    private Color MAJORGRIDCOLOR = new Color(Display.getCurrent(), 200, 200, 200);
    private Color SATURDAY_COLOR = new Color(Display.getCurrent(), 255, 230, 230);
    private Color SUNDAY_COLOR = new Color(Display.getCurrent(), 255, 200, 200);

    protected PdiCalendar _kalender;

    public PdiGridRenderer(PdiCalendar kalender) {
        _kalender = kalender;
    }

    public void draw(GC gc, TimeBarViewerDelegate tbv, Rectangle drawingArea, boolean printing) {
        if (printing) {
            throw new RuntimeException("printing not supported");
        }
        int ox = drawingArea.x;
        int oy = drawingArea.y;
        int width = drawingArea.width;
        int height = drawingArea.height;

        Color bg = gc.getBackground();
        Color fg = gc.getForeground();

        JaretDate date = tbv.getStartDate().copy();
        date.setHours(0);
        while (tbv.getEndDate().compareTo(date) > 0) {
            PdiDay tag = _kalender.getTag(date);
            if (tag != null) {
                int x = tbv.xForDate(tag.getDate());
                int daywidth = (int) (tbv.getPixelPerSecond() * 24.0 * 60.0 * 60.0);
                gc.setForeground(MAJORGRIDCOLOR);
                gc.drawLine(x, oy, x, oy + height);
                int dayOfWeek = date.getDayOfWeek();
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    gc.setBackground(dayOfWeek == Calendar.SATURDAY ? SATURDAY_COLOR : SUNDAY_COLOR);
                    gc.fillRectangle(x, oy, daywidth, height);
                }
            }
            date.advanceDays(1);
        }
        gc.setBackground(bg);
        gc.setForeground(fg);
    }

    public void dispose() {
    	// nothing to dispose
    }

    /**
     * produces a default grid renderer for printing
     */
    public GridRenderer createPrintRenderer(Printer printer) {
        DefaultGridRenderer gridRenderer = new DefaultGridRenderer(printer);
        return gridRenderer;
    }

    
    public void setTickProvider(ITickProvider tickProvider) {
        // TODO Auto-generated method stub
        
    }
}
