/*
 *  File: TimeBarNode.java 
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

import java.util.List;

/**
 * Interface describing a time bar row in a hierarchy of rows.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarNode.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeBarNode extends TimeBarRow {
    /**
     * Retrieve all children of the node.
     * 
     * @return chrildren of the node
     */
    List<TimeBarNode> getChildren();

    /**
     * Retrieve the level in the tree.
     * 
     * @return level in the tree.
     */
    int getLevel();

    /**
     * Tell the node it's level. Storing the level of the node directly with the node is not an optimal solution.
     * However it is fast and straight forward.
     * 
     * @param level level of the node
     */
    void setLevel(int level);

    /**
     * Add a node as a child.
     * 
     * @param node child to be added.
     */
    void addNode(TimeBarNode node);

    /**
     * Remove a child node.
     * 
     * @param node node to remove
     */
    void remNode(TimeBarNode node);

    /**
     * Add a listener to listen for node changes.
     * 
     * @param tbnl listener to add
     */
    void addTimeBarNodeListener(TimeBarNodeListener tbnl);

    /**
     * Remove a listener registered for node changes.
     * 
     * @param tbnl listener to remove
     */
    void removeTimeBarNodeListener(TimeBarNodeListener tbnl);

}
