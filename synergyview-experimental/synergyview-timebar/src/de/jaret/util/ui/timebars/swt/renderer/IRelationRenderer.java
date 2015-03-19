/*
 *  File: IRelationRenderer.java 
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
package de.jaret.util.ui.timebars.swt.renderer;

import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.model.IIntervalRelation;

/**
 * Interface describing the rendering part for relations between intervals. The relation renderer is responsible for all
 * the calculations to be done when rendering (so it can decide whether to paint lines for relations between intervals
 * that are not shown). It is also responsible for supplying a hit detection for selecting of relations.
 * 
 * @author kliem
 * @version $Id: IRelationRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface IRelationRenderer {

    /**
     * Do the complete relation rendering.
     * 
     * @param delegate the delegate
     * @param gc GC to paint on
     * @param printing <code>true</code> when used throughout printing
     */
    void renderRelations(TimeBarViewerDelegate delegate, GC gc, boolean printing);

    /**
     * Retrieve the list of relations that are hit a the given coordinate.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return List of realations that may be empty but must not be <code>null</code>
     */
    List<IIntervalRelation> getRelationsForCoord(int x, int y);

    /**
     * Supply a tooltip for a position in the diagram area.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return <code>null</code> for no tooltip contribution or tooltip to be displayed
     */
    String getTooltip(int x, int y);

    /**
     * Create a renderer suitable for printing.
     * 
     * @param printer printer device
     * @return renderer configured and ready for printing
     */
    IRelationRenderer createPrintRenderer(Printer printer);

    /**
     * Dispose resources the renderer might have aquired.
     */
    void dispose();
}
