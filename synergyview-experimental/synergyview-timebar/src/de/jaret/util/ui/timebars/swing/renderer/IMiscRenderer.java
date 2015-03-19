/*
 *  File: IMiscRenderer.java 
 *  Copyright (c) 2004-2008  Peter Kliem (Peter.Kliem@jaret.de)
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
package de.jaret.util.ui.timebars.swing.renderer;

import java.awt.Graphics;
import java.awt.Rectangle;

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * Interface describing a renderer for various elements in the swing time bar viewer.
 * 
 * @author kliem
 * @version $Id: IMiscRenderer.java 802 2008-12-28 12:30:41Z kliem $
 */
public interface IMiscRenderer {
    /**
     * Render the selected region.
     * 
     * @param graphics Graphics to paint with
     * @param tbv timebar viewer
     * @param delegate the delegate
     */
    void renderRegionRect(Graphics graphics, TimeBarViewer tbv, TimeBarViewerDelegate delegate);

    /**
     * Render the selection rectangle.
     * 
     * @param graphics Graphics to paint with
     * @param tbv timebar viewer
     * @param selectionRect the selection rectangle in coordinates
     */
    void renderSelectionRect(Graphics graphics, TimeBarViewer tbv, Rectangle selectionRect);

    /**
     * Draw a row gridline for the time bar viewer.
     * 
     * @param graphics Graphics to draw with
     * @param x1 start x
     * @param y1 start y
     * @param x2 end x
     * @param y2 end y
     */
    void drawRowGridLine(Graphics graphics, int x1, int y1, int x2, int y2);

    /**
     * Draw a row background for the time bar viewer if a row is either selected or highlighted.
     * 
     * @param graphics Graphics to paint with
     * @param x x cord
     * @param y y coord
     * @param width width of the area
     * @param height height of the area
     * @param selected true if the row is selected
     * @param highlighted true if the row is highlighted
     */
    void drawRowBackground(Graphics graphics, int x, int y, int width, int height, boolean selected, boolean highlighted);

}
