/*
 *  File: HierarchicalViewStateImpl.java 
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of a HIerarchicalViewState based on a map, not supporting paths to nodes.
 * 
 * @author Peter Kliem
 * @version $Id: HierarchicalViewStateImpl.java 800 2008-12-27 22:27:33Z kliem $
 */

public class HierarchicalViewStateImpl implements HierarchicalViewState {
    /** listener list. */
    protected List<HierarchicalViewStateListener> _listenerList;

    /** Map holding the state for each node. */
    protected Map<TimeBarNode, Boolean> _stateMap = new HashMap<TimeBarNode, Boolean>();

    /**
     * {@inheritDoc}
     */
    public boolean isExpanded(TimeBarNode node) {
        Boolean state = _stateMap.get(node);
        if (state == null) {
            return false;
        }
        return state.booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    public void setExpanded(TimeBarNode node, boolean expanded) {
        Boolean oldState = _stateMap.get(node);
        Boolean state = Boolean.valueOf(expanded);
        _stateMap.put(node, state);
        if (expanded) {
            if (oldState == null || !oldState.booleanValue()) {
                fireNodeExpanded(node);
            }
        } else {
            if (oldState != null && oldState.booleanValue()) {
                fireNodeFolded(node);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setExpandedRecursive(TimeBarNode node, boolean expanded) {
        if (node.getChildren().size() > 0) {
            setExpanded(node, expanded);
            for (TimeBarNode child : node.getChildren()) {
                setExpandedRecursive(child, expanded);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addHierarchicalViewstateListener(HierarchicalViewStateListener hvsListener) {
        if (_listenerList == null) {
            _listenerList = new ArrayList<HierarchicalViewStateListener>();
        }
        _listenerList.add(hvsListener);
    }

    /**
     * {@inheritDoc}
     */
    public void remHierarchicalViewStateListener(HierarchicalViewStateListener hvsListener) {
        if (_listenerList != null) {
            _listenerList.remove(hvsListener);
        }
    }

    /**
     * Inform listeners that a node has been expanded.
     * 
     * @param node expanded node
     */
    private void fireNodeExpanded(TimeBarNode node) {
        if (_listenerList != null) {
            for (HierarchicalViewStateListener hvsl : _listenerList) {
                hvsl.nodeExpanded(node);
            }
        }
    }

    /**
     * Inform listeners that a node has been collapsed.
     * 
     * @param node collapsed node
     */
    private void fireNodeFolded(TimeBarNode node) {
        if (_listenerList != null) {
            for (HierarchicalViewStateListener hvsl : _listenerList) {
                hvsl.nodeFolded(node);
            }
        }
    }

}
