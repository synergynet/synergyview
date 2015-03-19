/*
 *  File: StdHierarchicalTimeBarModel.java 
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
 * An implementation of of a "normal" TimeBarModel based on a hierarchical model and a viewstate. Intended for internal
 * use when using a hierarchical model.
 * 
 * MAYBE Filter some of the events out of the node for visibility (will not hurt if not ...)
 * 
 * @author Peter Kliem
 * @version $Id: StdHierarchicalTimeBarModel.java 800 2008-12-27 22:27:33Z kliem $
 */
public class StdHierarchicalTimeBarModel extends AbstractTimeBarModel implements HierarchicalViewStateListener,
        TimeBarNodeListener {
    /**
     * list of timebar nodes (that are the rows of the model). The list contains always only the visible nodes.
     */
    protected List<TimeBarNode> _rows;

    /** the underlying hierarchical model. */
    protected HierarchicalTimeBarModel _hModel;
    /** hierarchical viewstate holding the folded/unfolded information. */
    protected HierarchicalViewState _hvs;

    /** if set to true the roor node will be seen expanded and will not be represented by a row. */
    protected boolean _hideRoot = false;

    /**
     * Construct a new model.
     * 
     * @param hModel hierachical model
     * @param hvs hierarchical viewstate
     */
    public StdHierarchicalTimeBarModel(HierarchicalTimeBarModel hModel, HierarchicalViewState hvs) {
        _hModel = hModel;
        _hvs = hvs;
        _hvs.addHierarchicalViewstateListener(this);
        updateRowList();
        checkMinMax(_hModel.getRootNode());
    }

    /**
     * Fill the rowlist with the visible nodes.
     * 
     */
    private void updateRowList() {
        _rows = new ArrayList<TimeBarNode>();
        if (!_hideRoot) {
            updateRowList(_rows, 0, _hModel.getRootNode(), true);
        } else {
            if (_hModel.getRootNode().getChildren() != null) {
                for (TimeBarNode n : _hModel.getRootNode().getChildren()) {
                    updateRowList(_rows, 0, n, true);
                }
            }
        }
    }

    /**
     * Fill a given list with the visible nodes.
     * 
     * @param rows list to fill
     * @param level currect level for informing the node about its level
     * @param node current node to add
     * @param visible is the node visible or not?
     */
    private void updateRowList(List<TimeBarNode> rows, int level, TimeBarNode node, boolean visible) {
        if (visible) {
            rows.add(node);
        }

        node.addTimeBarNodeListener(this);
        node.setLevel(level);

        if (_minDate == null) {
            _minDate = node.getMinDate();
            _maxDate = node.getMaxDate();
        } else if (node.getMinDate() != null && node.getMaxDate() != null) {
            if (_minDate.compareTo(node.getMinDate()) > 0) {
                _minDate = node.getMinDate();
            }
            if (_maxDate.compareTo(node.getMaxDate()) < 0) {
                _maxDate = node.getMaxDate();
            }
        }
        if (node.getChildren() != null) {
            for (TimeBarNode n : node.getChildren()) {
                updateRowList(rows, level + 1, n, _hvs.isExpanded(node) && visible);
            }
        }
        if (level > _hModel.getDepth()) {
            _hModel.setDepth(level);
        }
    }

    /**
     * Checks the min and maxdate by looking at all nodes.
     * 
     * @param node starting node (called recursive)
     */
    private void checkMinMax(TimeBarNode node) {
        if (_minDate == null) {
            _minDate = node.getMinDate();
            _maxDate = node.getMaxDate();
        } else if (node.getMinDate() != null && node.getMaxDate() != null) {
            if (_minDate.compareTo(node.getMinDate()) > 0) {
                _minDate = node.getMinDate();
            }
            if (_maxDate.compareTo(node.getMaxDate()) < 0) {
                _maxDate = node.getMaxDate();
            }
        }
        if (node.getChildren() != null) {
            for (TimeBarNode child : node.getChildren()) {
                checkMinMax(child);
            }
        }

    }

    /**
     * Check whether there are sibling nodes for a given node on a specified level.
     * 
     * @param node node to check
     * @param level level
     * @return true if the node itself has more siblings on the given level or if there are nodes on the given level
     */
    public boolean moreSiblings(TimeBarNode node, int level) {
        int idx = _rows.indexOf(node);
        if (idx == -1) {
            return false;
            // throw new RuntimeException();
        }
        if (node.getLevel() == level) {
            return getNextSibling(node) != null;
        } else {
            TimeBarNode n = node;
            for (int l = node.getLevel(); l > level + 1; l--) {
                n = getParent(n);
            }
            return getNextSibling(n) != null;
        }
    }

    /**
     * Retrieve the next sibling of a given node.
     * 
     * @param node node to search sibling for
     * @return next sibling or null if none could be found
     */
    public TimeBarNode getNextSibling(TimeBarNode node) {
        TimeBarNode parent = getParent(node);
        if (parent == null) {
            return null;
        }
        int idx = parent.getChildren().indexOf(node);
        if (parent.getChildren().size() > idx + 1) {
            return parent.getChildren().get(idx + 1);
        } else {
            return null;
        }

    }

    /**
     * Get the parent of a given node.
     * 
     * @param node node to search the parent for
     * @return parent of the node or null if there is no parent
     */
    private TimeBarNode getParent(TimeBarNode node) {
        int idx = _rows.indexOf(node);
        if (idx == -1) {
            return null;
        }
        for (int i = idx - 1; i >= 0; i--) {
            TimeBarNode n = _rows.get(i);
            if (n.getChildren().contains(node)) {
                return n;
            }
        }
        return null;
    }

    /**
     * Check whether a node is visible.
     * 
     * @param node node to chack
     * @return true if the node ist visible
     */
    boolean isVisible(TimeBarNode node) {
        return getIdxForNode(node) != -1;
    }

    /**
     * Get index of a node.
     * 
     * @param node node
     * @return idx or -1
     */
    int getIdxForNode(TimeBarNode node) {
        return _rows.indexOf(node);
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarRow getRow(int rowIdx) {
        return _rows.get(rowIdx);
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        return _rows.size();
    }

    /**
     * {@inheritDoc}
     */
    public void nodeAdded(TimeBarNode parent, TimeBarNode newChild) {
        checkMinMax(newChild);
        newChild.addTimeBarNodeListener(this);
        if (_hvs.isExpanded(parent)) {
            // search the position of the new child and add the row
            Map<TimeBarNode, Integer> map = new HashMap<TimeBarNode, Integer>();
            posForNode(parent, map);
            int pos = map.get(newChild);
            _rows.add(pos, newChild);
            fireRowAdded(newChild);
            // if the new child has children and is expanded, add all of its children
            List<TimeBarNode> toAdd = new ArrayList<TimeBarNode>();
            enumerateChildren(newChild, toAdd);
            pos++;
            for (TimeBarNode timeBarNode : toAdd) {
                _rows.add(pos, timeBarNode);
                fireRowAdded(timeBarNode);
                pos++;
            }

        }
    }

    /**
     * Fill a map with the index positions for the underlying nodes.
     * 
     * @param node starting node
     * @param map map to fill with the indizes
     * @return "inserted" count for recursive call
     */
    private int posForNode(TimeBarNode node, Map<TimeBarNode, Integer> map) {
        int idx = getIdxForNode(node);
        int count = node.getChildren().size();
        int inserted = 0;
        for (int i = 0; i < count; i++) {
            TimeBarNode n = node.getChildren().get(i);
            map.put(n, idx + 1 + inserted);
            inserted++;
            if (_hvs.isExpanded(n) && n.getChildren().size() > 0) {
                inserted += posForNode(n, map);
            }
        }
        return inserted;
    }

    /**
     * {@inheritDoc}
     */
    public void nodeRemoved(TimeBarNode parent, TimeBarNode removedChild) {
        checkMinMax(_hModel.getRootNode()); // check the complete range
        removedChild.removeTimeBarNodeListener(this);
        if (_hvs.isExpanded(parent)) {
            // remove the row of the child
            _rows.remove(removedChild);
            fireRowRemoved(removedChild);
            // remove the rows of all visible children
            List<TimeBarNode> toRemove = new ArrayList<TimeBarNode>();
            enumerateChildren(removedChild, toRemove);
            for (TimeBarNode timeBarNode : toRemove) {
                _rows.remove(timeBarNode);
                fireRowRemoved(timeBarNode);
            }
        }
    }

    /**
     * Fill a list with all children of the given node that are visible.
     * 
     * @param node starting ndoe
     * @param children list to fill
     */
    private void enumerateChildren(TimeBarNode node, List<TimeBarNode> children) {
        if (node.getChildren() != null && _hvs.isExpanded(node)) {
            for (TimeBarNode timeBarNode : node.getChildren()) {
                children.add(timeBarNode);
                enumerateChildren(timeBarNode, children);
            }
        }
    }

    /**
     * Handle expansion of a node. This means adding all rows that become visible when expanding.
     * 
     * @param node the has been expanded
     */
    public void nodeExpanded(TimeBarNode node) {
        nodeExpanded2(node);
        checkMinMax(node); // TODO could be done more efficient (listen to changes on the row itself)
    }

    /**
     * Handle expansion of a node. This means adding all rows that become visible when expanding.
     * 
     * @param node the has been expanded
     * @return number of added rows (that became visible)
     */
    public int nodeExpanded2(TimeBarNode node) {
        int idx = getIdxForNode(node);
        int count = node.getChildren().size();
        int inserted = 0;
        for (int i = 0; i < count; i++) {
            TimeBarNode n = node.getChildren().get(i);
            _rows.add(idx + 1 + inserted, n);
            inserted++;
            fireRowAdded(n);
            if (_hvs.isExpanded(n) && n.getChildren().size() > 0) {
                inserted += nodeExpanded2(n);
            }
        }
        return inserted;
    }

    /**
     * Handle folding of a node. This means removing all rows that "disappear" with folding.
     * 
     * @param node node that has been folded
     */
    public void nodeFolded(TimeBarNode node) {
        int count = node.getChildren().size();
        for (int i = 0; i < count; i++) {
            TimeBarNode n = node.getChildren().get(i);
            if (_hvs.isExpanded(n) && n.getChildren().size() > 0) {
                nodeFolded(n);
            }
            int idx2 = getIdxForNode(n);
            // maybe the node is already hidden ...
            if (idx2 != -1) {
                _rows.remove(idx2);
            }
            fireRowRemoved(n);
        }
        checkMinMax(_hModel.getRootNode()); // TODO could be done more efficient (listen to changes on the row itself)
    }

    /**
     * Retrieve whether the root node is included in the outgoing model.
     * 
     * @return <code>true</code> when the root node is included in the resulting flat model
     */
    public boolean getHideRoot() {
        return _hideRoot;
    }

    /**
     * Set whether the root node should be represented in the flat outgoing model.
     * 
     * @param hideRoot set to <code>true</code> if the root node should not be included in the outgoing flat model
     */
    public void setHideRoot(boolean hideRoot) {
        if (hideRoot != _hideRoot) {
            _hideRoot = hideRoot;
            updateRowList();
            fireModelDataChanged();
        }
    }
}
