/*
 *  File: AbstractTimeBarNode.java 
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
import java.util.Vector;

/**
 * Abstract base implementation for a timebar node.
 * 
 * @author Peter Kliem
 * @version $Id: AbstractTimeBarNode.java 802 2008-12-28 12:30:41Z kliem $
 */
public abstract class AbstractTimeBarNode extends AbstractTimeBarRowModel implements TimeBarNode {
    /** list of registered listeners. */
    protected List<TimeBarNodeListener> _nodeListeners;

    /**
     * Default constructor. Do not forget to set a headre when using this constructor.
     */
    public AbstractTimeBarNode() {

    }

    /**
     * Constructor supplying a header.
     * 
     * @param header the header for teh node
     */
    public AbstractTimeBarNode(TimeBarRowHeader header) {
        super(header);
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
    public synchronized void removeTimeBarNodeListener(TimeBarNodeListener tbnl) {
        if (_nodeListeners == null) {
            return;
        }
        _nodeListeners.remove(tbnl);
        super.remTimeBarRowListener(tbnl);
    }

    /**
     * Inform listeners about a new node.
     * 
     * @param newNode the added node
     */
    protected void fireNodeAdded(TimeBarNode newNode) {
        if (_nodeListeners != null) {
            for (TimeBarNodeListener listener : _nodeListeners) {
                listener.nodeAdded(this, newNode);
            }
        }
    }

    /**
     * Inform listeners about a removed node.
     * 
     * @param removedNode the node that has been removed
     */
    protected void fireNodeRemoved(TimeBarNode removedNode) {
        if (_nodeListeners != null) {
            for (TimeBarNodeListener listener : _nodeListeners) {
                listener.nodeRemoved(this, removedNode);
            }
        }
    }

}
