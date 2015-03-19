/*
 *  File: PdiHeaderRenderer.java 
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

import de.jaret.examples.timebars.pdi.model.Person;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;
import de.jaret.util.ui.timebars.swt.renderer.HeaderRenderer;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;

/**
 * @author Peter Kliem
 * @version $Id: PdiHeaderRenderer.java 261 2007-02-17 23:50:38Z olk $
 */
public class PdiHeaderRenderer extends RendererBase implements HeaderRenderer {

    public PdiHeaderRenderer(Printer printer) {
        super(printer);
    }
    
    public PdiHeaderRenderer() {
        super(null);
    }
    
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate tbv, TimeBarRowHeader header,
            boolean selected, boolean printing) {

        String str = ((Person) header).getName();
        Color bg = gc.getBackground();
        if (selected) {
            gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        }
        SwtGraphicsHelper.drawStringRightAlignedVCenter(gc, str, drawingArea.x + drawingArea.width-10, drawingArea.y
                + drawingArea.height / 2);

        if (tbv.getDrawRowGrid()) {
            gc.drawLine(drawingArea.x, drawingArea.y+drawingArea.height-1, drawingArea.x+drawingArea.width, drawingArea.y+drawingArea.height-1);
        }
        
        gc.setBackground(bg);
    }

    public String getToolTipText(TimeBarRow row, Rectangle drawingArea, int x, int y) {
        return null;
    }

    public boolean contains(Rectangle drawingArea, int x, int y) {
        return true;
    }

    public void dispose() {
    	// nothing to dispose
    }

    public HeaderRenderer createPrintRenderer(Printer printer) {
        return new PdiHeaderRenderer(printer);
    }
}
