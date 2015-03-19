/*
 *  File: DefaultGapRenderer.java 
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

import de.jaret.util.date.Interval;
import de.jaret.util.misc.FormatHelper;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.model.TimeBarRow;

/**
 * Gap renderer rendering a double headed arrow and the time between the intervals.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultGapRenderer.java 781 2008-09-20 20:10:18Z kliem $
 */
public class DefaultGapRenderer extends RendererBase implements TimeBarGapRenderer {
    /** color the arrow is rendered in. */
    private static final Color DEFAULT_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);

    /**
     * Construct a DefaultGapRanderer for a printer.
     * 
     * @param printer printer device to use
     */
    public DefaultGapRenderer(Printer printer) {
        super(printer);
    }

    /**
     * Default constructor.
     */
    public DefaultGapRenderer() {
        super(null);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, TimeBarViewerDelegate delegate, TimeBarRow row, Interval i1, Interval i2,
            Rectangle drawingArea, boolean printing) {
        int ox = drawingArea.x;
        int oy = drawingArea.y;
        int width = drawingArea.width;
        int height = drawingArea.height;

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;

        Color fg = gc.getForeground();

        int diffminutes = (int) i2.getBegin().diffMinutes(i1.getEnd());
        String timeString = FormatHelper.NFInt2Digits().format(diffminutes / 60) + ":"
                + FormatHelper.NFInt2Digits().format(diffminutes % 60);
        int twidth = SwtGraphicsHelper.getStringDrawingWidth(gc, timeString);
        int theight = SwtGraphicsHelper.getStringDrawingHeight(gc, timeString);

        gc.setForeground(DEFAULT_COLOR);
        // center the timeString
        if (horizontal) {
            if (width > twidth + 3 && height / 2 >= theight) {
                SwtGraphicsHelper.drawStringCentered(gc, timeString, ox + width / 2, oy + height / 2 - 2);
            }
        } else {
            if (height > theight + 3 && width / 2 >= twidth) {
                SwtGraphicsHelper.drawStringCenteredAroundPoint(gc, timeString, ox + width / 2, oy + height / 2);
            }
        }
        // draw a line with arrow endings
        if (printing) {
            gc.setLineWidth(getDefaultLineWidth());
        }
        if (horizontal) {
            if (width > 15) {
                SwtGraphicsHelper.drawArrowLine(gc, ox + 2, oy + height / 2, ox + width - 3, oy + height / 2,
                        scaleX(4), scaleY(3), true, true);
            }
        } else {
            if (height > 15) {
                SwtGraphicsHelper.drawArrowLineVertical(gc, ox + width / 2, oy + 2, ox + width / 2, oy + height - 3,
                        scaleX(3), scaleY(4), true, true);
            }
        }
        gc.setLineWidth(1);

        gc.setForeground(fg);
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
    public TimeBarGapRenderer createPrintRenderer(Printer printer) {
        DefaultGapRenderer renderer = new DefaultGapRenderer(printer);
        return renderer;
    }

}
