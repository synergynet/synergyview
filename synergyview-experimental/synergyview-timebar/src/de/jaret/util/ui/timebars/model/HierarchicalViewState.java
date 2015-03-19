/*
 *  File: HierarchicalViewState.java 
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
package de.jaret.util.ui.timebars.model;

/**
 * Interface describing the viewstate of a hierarchical time bar model (expanded/not expanded). Please note that the
 * HierarchicalViewStateListener does not support the selection of nodes by path, so equal nodes will always have an
 * equal state. Maybe this will change in future version.
 * 
 * @author Peter Kliem
 * @version $Id: HierarchicalViewState.java 531 2007-08-12 22:25:36Z olk $
 */
public interface HierarchicalViewState {
    /**
     * Check whether a node is expanded.
     * 
     * @param node node to check
     * @return true if expanded
     */
    boolean isExpanded(TimeBarNode node);

    /**
     * Set the expanded state for a single node.
     * 
     * @param node node to set the expand state for
     * @param expanded true for expanded
     */
    void setExpanded(TimeBarNode node, boolean expanded);

    /**
     * Set the expanded state for a node and all of it's children.
     * 
     * @param node starting node
     * @param expanded expanded state to set
     */
    void setExpandedRecursive(TimeBarNode node, boolean expanded);

    /**
     * Add a view state listener.
     * 
     * @param hvsListener listener to add
     */
    void addHierarchicalViewstateListener(HierarchicalViewStateListener hvsListener);

    /**
     * Remove a view state listener.
     * 
     * @param hvsListener listener to remove
     */
    void remHierarchicalViewStateListener(HierarchicalViewStateListener hvsListener);
}
