/*
 *  File: HierarchicalTimeBarModel.java 
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
 * Interface describing a hierarchical model. As of version 0.96 this is not an extension of the TimeBarModel since the
 * extraction of the view state (expanded state) in the hierarchy did imply a stricter separation. Care should be taken
 * if nodes should appear in several positions in the node. There is no restriction on this by the model itself. However
 * the ViewState will not distinguish the state of equal nodes in different positions resulting in the same expanded
 * state of all equal nodes.
 * 
 * @author Peter Kliem
 * @version $Id: HierarchicalTimeBarModel.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface HierarchicalTimeBarModel {
    /**
     * Retrieve the depth of the hierarchy.
     * 
     * @return depth of the hierarchy
     */
    int getDepth();

    /**
     * Little helper. May be removed in future revisions.
     * 
     * @param depth the depth to set
     */
    void setDepth(int depth);

    /**
     * Retrieve the root node of the hierarchy.
     * 
     * @return the root node
     */
    TimeBarNode getRootNode();
}
