/*
 *  File: HierarchyRenderer.java 
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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.model.TimeBarNode;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * This interface describes a renderer used to render the hierarchy column.
 * 
 * @author Peter Kliem
 * @version $Id: HierarchyRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface HierarchyRenderer {
    /**
     * Drwa one hierarchy element.
     * 
     * @param gc GC to draw with
     * @param drawingArea rectangle to draw within
     * @param delegate TimeBarViewerDelegate for supporting information
     * @param row the row (node) for which the hierarchy should be painted
     * @param selected wehether the row is selscted or not
     * @param expanded expanded status of the row
     * @param leaf if true the row is a leaf
     * @param level level in the tree of the current element
     * @param depth depth of the tree. If the depth is not known a value of -1 will indicate this fact.
     * @param printing falg indicate the current paint operation is for a printer
     */
    void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, TimeBarRow row, boolean selected,
            boolean expanded, boolean leaf, int level, int depth, boolean printing);

    /**
     * Retrieve the tooltip for a position inside the header.
     * 
     * @param node TimeBarNode that the hierarchy element has been painted for
     * @param drawingArea area in which the header has been drawn
     * @param x x of the position in question
     * @param y of the position in question
     * @return the tooltip for the position or <code>null</code>
     */
    String getToolTipText(TimeBarNode node, Rectangle drawingArea, int x, int y);

    /**
     * Check whether a position in the header should be active for folding/expanding.
     * 
     * @param tbv TimeBarViewer
     * @param node node
     * @param drawingArea area in which the header has been drawn
     * @param xx x of the position in question
     * @param yy y of the position in question
     * @return true if this position hits an active part leeding to a fold/unfold operation.
     */
    boolean isInToggleArea(TimeBarViewerInterface tbv, TimeBarNode node, Rectangle drawingArea, int xx, int yy);

    /**
     * Check whether a position should trigger a row selection.
     * 
     * @param viewer the asking viewer
     * @param node node
     * @param drawingArea drawingarea
     * @param xx x coordinate
     * @param yy y coordinate
     * @return true if the position should trigger the selection
     */
    boolean isInHierarchySelectionArea(TimeBarViewer viewer, TimeBarNode node, Rectangle drawingArea, int xx, int yy);

    /**
     * Return the preferred width for the hierachy display.
     * 
     * @return preferred with or -1 for "don't care"
     */
    int getPreferredWidth();

    /**
     * Dispose the renderer. Should free up any resources locked.
     * 
     */
    void dispose();

    /**
     * Create a similar renderer for printing. The creation should copy settings made to the producing renderer.
     * 
     * @param printer Printer device
     * @return a configured renderer for printing.
     */
    HierarchyRenderer createPrintRenderer(Printer printer);

}
