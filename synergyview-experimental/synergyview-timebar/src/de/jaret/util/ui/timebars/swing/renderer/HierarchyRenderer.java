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
package de.jaret.util.ui.timebars.swing.renderer;

import javax.swing.JComponent;

import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * This interface describes a renderer used to render the hierarchy column.
 * 
 * @author Peter Kliem
 * @version $Id: HierarchyRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface HierarchyRenderer {
    /**
     * Provide a configured JComponent for rendering the hierarchy.
     * 
     * @param tbv the requesting timebar viewer
     * @param row the row (node) for which the hierarchy should be painted
     * @param selected wehether the row is selscted or not
     * @param expanded expanded status of the row
     * @param leaf if true the row is a leaf
     * @param level level in the tree of the current element
     * @param depth depth of the tree. If the depth is not known a value of -1 will indicate this fact.
     * @return the configured JComponent for rendering
     */
    JComponent getHierarchyRendererComponent(TimeBarViewer tbv, TimeBarRow row, boolean selected, boolean expanded,
            boolean leaf, int level, int depth);

    /**
     * Return the preferred width for the hierachy display.
     * 
     * @return preferred with or -1 for "don't care"
     */
    int getWidth();

}
