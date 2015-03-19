/*
 *  File: FancyGlobalRenderer.java 
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
package de.jaret.examples.timebars.fancy.swt.renderer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.examples.timebars.fancy.model.FancyInterval;
import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.GlobalAssistantRenderer;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;

/**
 * Gloabl renderer that marks intervals by drawing an oval around them.
 * 
 * @author kliem
 * @version $Id: FancyGlobalRenderer.java 558 2007-09-08 07:40:22Z olk $
 */
public class FancyGlobalRenderer extends RendererBase implements GlobalAssistantRenderer {

    private List<Interval> _intervalsToMark;
    private boolean _drawHistory = false;

    public FancyGlobalRenderer() {
        super(null);
    }

    public FancyGlobalRenderer(Printer printer) {
        super(printer);
    }

    public GlobalAssistantRenderer createPrintRenderer(Printer printer) {
        return new FancyGlobalRenderer(printer);
    }

    public void dispose() {

    }

    public void doRenderingBeforeIntervals(TimeBarViewerDelegate delegate, GC gc, boolean printing) {
        if (_drawHistory) {
            for (int r = 0; r < delegate.getModel().getRowCount(); r++) {
                TimeBarRow row = delegate.getModel().getRow(r);
                if (delegate.isRowDisplayed(row)) {
                    for (Interval interval : row.getIntervals()) {
                        drawHistory(delegate, row, interval, gc);
                    }
                }
            }
        }
    }

    private void drawHistory(TimeBarViewerDelegate delegate, TimeBarRow row, Interval interval, GC gc) {
        if (interval instanceof FancyInterval) {
            Rectangle bounds = TimeBarViewer.convertRect(delegate.getIntervalBounds(row, interval));
            int alpha = gc.getAlpha();
            Color bg = gc.getBackground();
            gc.setAlpha(30);
            gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_DARK_GREEN));
            if (bounds != null) {
                for (Interval i : ((FancyInterval) interval).getHistory()) {
                    Rectangle b = TimeBarViewer.convertRect(delegate.getIntervalBounds(row, i));
                    gc.fillRectangle(b);
                }
            }
            gc.setAlpha(alpha);
            gc.setBackground(bg);
        }
    }

    public void doRenderingLast(TimeBarViewerDelegate delegate, GC gc, boolean printing) {
        if (_intervalsToMark != null) {
            for (Interval interval : _intervalsToMark) {
                Rectangle bounds = TimeBarViewer.convertRect(delegate.getIntervalBounds(interval));
                if (bounds != null) {
                    gc.setLineWidth(5);
                    Color fg = gc.getForeground();
                    gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_GREEN));

                    gc.drawOval(bounds.x - 20, bounds.y - 20, bounds.width + 40, bounds.height + 40);

                    gc.setLineWidth(1);
                    gc.setForeground(fg);
                }
            }
        }

    }

    public void setIntervalsToMark(List<Interval> intervals) {
        _intervalsToMark = new ArrayList<Interval>();
        _intervalsToMark.addAll(intervals);
    }

    public boolean isDrawHistory() {
        return _drawHistory;
    }

    public void setDrawHistory(boolean drawHistory) {
        _drawHistory = drawHistory;
    }

}
