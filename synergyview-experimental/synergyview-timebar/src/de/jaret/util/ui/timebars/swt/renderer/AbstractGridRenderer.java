/*
 *  File: AbstractGridRenderer.java 
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

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * Abtsract base implementation pof a grid renderer holding the common used rendering methods.
 * 
 * @author kliem
 * @version $Id: AbstractGridRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public abstract class AbstractGridRenderer extends RendererBase implements GridRenderer {
    /** default color for highlighted row. */
    public final Color HIGHLIGHT_COLOR = new Color(Display.getCurrent(), 255, 200, 200);
    /** default color for background of a row selection. */
    public final Color ROWSELECT_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
    /** color for the highlighted row. */
    protected Color _highlightColor = HIGHLIGHT_COLOR;
    /** alpha for drawing the highlight. */
    protected int _highlightAlpha = TimeBarViewer.DEFAULT_ALPHA;
    /** color for selected rows. */
    protected Color _rowSelectColor = ROWSELECT_COLOR;

    /** alpha value for selection drawing. */
    protected int _rowSelectAlpha = 100;

    /**
     * Constructor for use with a printer device.
     * 
     * @param printer printer device
     */
    public AbstractGridRenderer(Printer printer) {
        super(printer);
    }

    /**
     * Constructor for screen use.
     */
    public AbstractGridRenderer() {
        super(null);
    }

    /**
     * {@inheritDoc}
     */
    public void drawRowBeforeIntervals(GC gc, TimeBarViewerDelegate delegate, Rectangle drawingArea, TimeBarRow row,
            boolean selected, boolean printing) {

        boolean highlighted = delegate.getHighlightedRow() == row;

        // draw a background if selected or highlighted
        // highlighting is at higher priority than selection
        if (selected || highlighted) {
            Color bg = gc.getBackground();
            int alpha = gc.getAlpha();
            if (highlighted) {
                gc.setBackground(_highlightColor);
                gc.setAlpha(_highlightAlpha);
            } else {
                gc.setBackground(_rowSelectColor);
                gc.setAlpha(_rowSelectAlpha);
            }
            // TODO
            // if (highlighted) {
            // // ghosted intervals/rows will not be painted on gtk when the highlight is done using transprancy
            // // so do not use alpha when there are ghostetd rows
            // if ((_ghostIntervals != null || _ghostRows != null) && !SWT.getPlatform().equals("gtk")) {
            // gc.setAlpha(_highlightAlpha);
            // }
            // }

            gc.fillRectangle(drawingArea);

            gc.setAlpha(alpha);
            gc.setBackground(bg);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void drawRowAfterIntervals(GC gc, TimeBarViewerDelegate delegate, Rectangle drawingArea, TimeBarRow row,
            boolean selected, boolean printing) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        HIGHLIGHT_COLOR.dispose();

    }

    /**
     * retrieve the color used for highlighting rows.
     * 
     * @return the highlight color
     */
    public Color getHighlightColor() {
        return _highlightColor;
    }

    /**
     * Set the color for highlighting a row. The color will not be disposed by the viewer. Highlighting is done with the
     * methods <code>highlightRow</code> and <code>deHighlightRow</code>.
     * 
     * @param highlightColor color to be used for highlighting rows
     */
    public void setHighlightColor(Color highlightColor) {
        _highlightColor = highlightColor;
    }

    /**
     * Retrieve the color used for marking selected rows.
     * 
     * @return color used for selecteted rows
     */
    public Color getRowSelectColor() {
        return _rowSelectColor;
    }

    /**
     * Set the color for drawing selected rows. The color will not be disposed by the viewer.
     * 
     * @param rowSelectColor color to be used to select rows
     */
    public void setRowSelectColor(Color rowSelectColor) {
        _rowSelectColor = rowSelectColor;
    }

    /**
     * Get the alpha used when drawing the highlighted row.
     * 
     * @return alpha for drawing the hightlight
     */
    public int getHighlightAlpha() {
        return _highlightAlpha;
    }

    /**
     * Set the alpha value used for drawing the highlighted row.
     * 
     * @param highlightAlpha alpha to use
     */
    public void setHighlightAlpha(int highlightAlpha) {
        _highlightAlpha = highlightAlpha;
    }

    /**
     * Retrieve the alpha value used for painting row selections.
     * 
     * @return alpha value for row seletions
     */
    public int getRowSelectAlpha() {
        return _rowSelectAlpha;
    }

    /**
     * Set the alpha value for painting row selections.
     * 
     * @param rowSelectAlpha alpha value for row selections
     */
    public void setRowSelectAlpha(int rowSelectAlpha) {
        _rowSelectAlpha = rowSelectAlpha;
    }

}
