/*
 *  File: PdiTimeScaleRenderer.java 
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.examples.timebars.pdi.model.PdiCalendar;
import de.jaret.examples.timebars.pdi.model.PdiDay;
import de.jaret.util.date.JaretDate;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.BoxTimeScaleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeScaleRenderer;

/**
 * @author Peter Kliem
 * @version $Id: PdiTimeScaleRenderer.java 260 2007-02-17 20:36:33Z olk $
 */
public class PdiTimeScaleRenderer implements TimeScaleRenderer {
    protected static int RHEIGHT = 50;
    PdiCalendar _kalender;

    public PdiTimeScaleRenderer(PdiCalendar kalender) {
        _kalender = kalender;
    }

    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate tbv, boolean top, boolean printing) {

        Color bg = gc.getBackground();
        JaretDate date = tbv.getStartDate().copy();
        date.setHours(0);
        while (tbv.getEndDate().compareTo(date) > 0) {
            PdiDay tag = _kalender.getTag(date);
            if (tag != null) {
                int x = tbv.xForDate(tag.getDate());
                String text1 = tag.getDate().toDisplayStringDate();
                String text2 = tag.getBetriebstag();
                int width = (int) (tbv.getPixelPerSecond() * 24.0 * 60.0 * 60.0);
                gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
                gc.fillRectangle(x, drawingArea.y, width, RHEIGHT - 1);
                gc.drawRectangle(x, drawingArea.y, width, RHEIGHT - 1);
                SwtGraphicsHelper.drawStringCenteredVCenter(gc, text1, x, x + width, drawingArea.y + RHEIGHT / 3);
                SwtGraphicsHelper.drawStringCenteredVCenter(gc, text2, x, x + width, drawingArea.y + 2 * (RHEIGHT / 3));
            }
            date.advanceDays(1);
        }
        gc.setBackground(bg);
    }

    public String getToolTipText(TimeBarViewer tbv, Rectangle drawingArea, int x, int y) {
        return null;
    }

    public boolean contains(Rectangle drawingArea, int x, int y) {
        return false;
    }

    public int getHeight() {
        return RHEIGHT;
    }

    public void dispose() {
    	// nothing to dispose
    }

    /**
     * Return a default (box) timescale renderer for printing.
     */
    public TimeScaleRenderer createPrintRenderer(Printer printer) {
        return new BoxTimeScaleRenderer(printer);
    }
}
