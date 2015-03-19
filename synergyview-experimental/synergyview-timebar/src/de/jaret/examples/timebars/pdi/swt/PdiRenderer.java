/*
 *  File: PdiRenderer.java 
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

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.examples.timebars.pdi.model.Assignment;
import de.jaret.examples.timebars.pdi.model.Duty;
import de.jaret.examples.timebars.pdi.model.Taetigkeit;
import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDateFormatter;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;

/**
 * @author Peter Kliem
 * @version $Id: PdiRenderer.java 260 2007-02-17 20:36:33Z olk $
 */
public class PdiRenderer extends RendererBase implements TimeBarRenderer {

    public PdiRenderer(Printer printer) {
        super(printer);
    }

    public PdiRenderer() {
        super(null);
    }

    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate tbv, Interval interval, boolean selected,
            boolean printing, boolean overlap) {
        drawFocus(gc, drawingArea, tbv, interval, selected, printing, overlap);

        Taetigkeit taetigkeit;
        if (interval instanceof Assignment) {
            taetigkeit = (Taetigkeit) ((Assignment) interval).getTaetigkeit();
        } else {
            taetigkeit = (Duty) interval;
        }
        Color bg = gc.getBackground();

        int height = drawingArea.height;
        int width = drawingArea.width;
        int ox = drawingArea.x;
        int oy = drawingArea.y;

        int y = height / 3;
        int bheight = height / 3;

        // balken
        if (selected) {
            gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        } else {
            gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
        }
        // body
        gc.fillRectangle(ox, oy + y, width - 1, bheight);
        Color bgtemp = gc.getBackground();
        // Ecke falls Probleme vorhanden sind
        if (taetigkeit.getProbleme() != null && taetigkeit.getProbleme().size() != 0) {
            gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            gc.fillRectangle(ox + width - 1 - bheight / 2, oy + y, bheight / 2, bheight / 2);
        }
        gc.setBackground(bgtemp);

        // Rahmen
        gc.drawRectangle(ox, oy + y, width - 1, bheight);

        String name;
        if (taetigkeit instanceof Duty) {
            name = ((Duty) taetigkeit).getDienstNr();
        } else {
            name = "Tätigkeit";
        }

        int twidth = SwtGraphicsHelper.getStringDrawingWidth(gc, name);
        // Balkenbeschriftung nur wenn sie passt
        if (width > twidth + 2) {
            SwtGraphicsHelper.drawStringCenteredVCenter(gc, name, ox, ox + width, oy + height / 2);
        }

        gc.setBackground(bg);
    }

    public String getToolTipText(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        Taetigkeit t;
        if (interval instanceof Assignment) {
            t = ((Assignment) interval).getTaetigkeit();
        } else {
            t = (Duty) interval;
        }
        if (t.getProbleme() != null && t.getProbleme().size() > 0 && inProblemIndicator(drawingArea, x, y)) {
            return problemTT(t);
        } else {
            if (t instanceof Duty) {
                Duty dienst = (Duty) t;
                return dienst.getDienstNr() + "\n" + t.getBegin().toDisplayString() + " - "
                        + t.getEnd().toDisplayString() + "\nAz.:"
                        + JaretDateFormatter.secondsToDisplayString(t.getEnd().diffSeconds(t.getBegin()));
            } else {
                return "Ttigkeit\n" + t.getBegin().toDisplayString() + " - " + t.getEnd().toDisplayString();
            }
        }
    }

    public boolean inProblemIndicator(Rectangle rect, int x, int y) {
        int bheight = rect.height / 3;
        int yy = rect.height / 3;
        return x >= rect.width - 1 - bheight / 2 && y >= yy && y <= yy + bheight / 2;
    }

    private String problemTT(Taetigkeit t) {
        StringBuffer buf = new StringBuffer();
        Iterator it = t.getProbleme().iterator();
        while (it.hasNext()) {
            String problem = (String) it.next();
            buf.append(problem);
            if (it.hasNext()) {
                buf.append("\n");
            }
        }
        return buf.toString();
    }

    public boolean contains(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        int height = drawingArea.height;
        int width = drawingArea.width;
        int ox = drawingArea.x;
        int oy = drawingArea.y;

        int yy = oy + height / 3;
        int bheight = height / 3;
        return oy + y >= yy && oy + y <= yy + bheight && ox + x >= ox && ox + x <= ox + width;
    }

    public Rectangle getContainingRectangle(Interval interval, Rectangle drawingArea, boolean overlapping) {
        int height = drawingArea.height;
        int width = drawingArea.width;
        int ox = drawingArea.x;
        int oy = drawingArea.y;
        int yy = oy + height / 3;
        int bheight = height / 3;
        return new Rectangle(ox, yy, width, bheight);
    }

    public void dispose() {
    	// nothing to dispose
    }

    public TimeBarRenderer createPrintrenderer(Printer printer) {
        return new PdiRenderer(printer);
    }
}
