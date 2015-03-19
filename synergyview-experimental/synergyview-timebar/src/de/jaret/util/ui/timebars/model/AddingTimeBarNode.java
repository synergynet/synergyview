/*
 *  File: AddingTimeBarNode.java 
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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * An implementation of a TimeBarNode that adds up all children. It also may carry an object.
 * 
 * @author Peter Kliem
 * @version $Id: AddingTimeBarNode.java 800 2008-12-27 22:27:33Z kliem $
 */
public class AddingTimeBarNode extends AddingTimeBarRowModel implements TimeBarNode {
    /** children. */
    protected List<TimeBarNode> _children = new ArrayList<TimeBarNode>();

    /** level of the node. */
    protected int _level = -1;

    /** NodeListeners regsitered. */
    protected List<TimeBarNodeListener> _nodeListeners;

    /** carried object. */
    protected Object _object;

    /**
     * Constructor.
     * 
     * @param header required header
     */
    public AddingTimeBarNode(TimeBarRowHeader header) {
        super(header);
    }

    /**
     * Set the carried object.
     * 
     * @param o object to store
     */
    public void setObject(Object o) {
        _object = o;
    }

    /**
     * Retrieve the stored object.
     * 
     * @return the stored object
     */
    public Object getObject() {
        return _object;
    }

    /**
     * {@inheritDoc}
     */
    public List<TimeBarNode> getChildren() {
        return _children;
    }

    /**
     * {@inheritDoc}
     */
    public void addNode(TimeBarNode node) {
        node.setLevel(getLevel() + 1); // set the level of the added node to be under the current node
        _children.add(node);
        this.addRow(node);
        fireNodeAdded(node);
    }

    /**
     * {@inheritDoc}
     */
    public void remNode(TimeBarNode node) {
        if (_children.remove(node)) {
            this.remRow(node);
            fireNodeRemoved(node);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getLevel() {
        return _level;
    }

    /**
     * {@inheritDoc}
     */
    public void setLevel(int level) {
        _level = level;
        if (_children != null) {
            for (TimeBarNode node : _children) {
                node.setLevel(level + 1);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addTimeBarNodeListener(TimeBarNodeListener tbnl) {
        if (_nodeListeners == null) {
            _nodeListeners = new Vector<TimeBarNodeListener>();
        }
        _nodeListeners.add(tbnl);
        super.addTimeBarRowListener(tbnl);
    }

    /**
     * {@inheritDoc}
     */
    public void removeTimeBarNodeListener(TimeBarNodeListener tbnl) {
        if (_nodeListeners == null) {
            return;
        }
        _nodeListeners.remove(tbnl);
        super.remTimeBarRowListener(tbnl);
    }

    /**
     * Inform listeners about a new child.
     * 
     * @param newNode new child node
     */
    protected void fireNodeAdded(TimeBarNode newNode) {
        if (_nodeListeners != null) {
            for (TimeBarNodeListener listener : _nodeListeners) {
                listener.nodeAdded(this, newNode);
            }
        }
    }

    /**
     * Inform listeners about a removed child.
     * 
     * @param removedNode removed child node
     */
    protected void fireNodeRemoved(TimeBarNode removedNode) {
        if (_nodeListeners != null) {
            for (TimeBarNodeListener listener : _nodeListeners) {
                listener.nodeRemoved(this, removedNode);
            }
        }
    }

}
