/*
 *  File: DefaultHierarchicalTimeBarModel.java 
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
 * Default implementation of t a HIerarchicalTimeBarModel.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultHierarchicalTimeBarModel.java 800 2008-12-27 22:27:33Z kliem $
 */
public class DefaultHierarchicalTimeBarModel implements HierarchicalTimeBarModel {
    /** root node of the tree. */
    protected TimeBarNode _rootNode;

    /** current depth of the model tree. */
    protected int _depth = 0;

    /**
     * Construct the model.
     * 
     * @param root root node
     */
    public DefaultHierarchicalTimeBarModel(TimeBarNode root) {
        _rootNode = root;
    }

    /**
     * {@inheritDoc}
     */
    public int getDepth() {
        return _depth;
    }

    /**
     * {@inheritDoc}
     */
    public void setDepth(int depth) {
        _depth = depth;
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarNode getRootNode() {
        return _rootNode;
    }

}
