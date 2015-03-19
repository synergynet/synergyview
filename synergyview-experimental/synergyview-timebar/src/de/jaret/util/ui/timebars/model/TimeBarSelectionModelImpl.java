/*
 *  File: TimeBarSelectionModelImpl.java 
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

import de.jaret.util.date.Interval;

/**
 * Implementation of TimeBarSelectionModel: straight forward.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarSelectionModelImpl.java 757 2008-04-27 22:42:37Z kliem $
 */
public class TimeBarSelectionModelImpl implements TimeBarSelectionModel {
    /** listener list. */
    protected List<TimeBarSelectionListener> _listenerList;

    /** list of selecetd intervals. */
    protected List<Interval> _selectedIntervals = new ArrayList<Interval>();

    /** list of selecetd rows. */
    protected List<TimeBarRow> _selectedRows = new ArrayList<TimeBarRow>();

    /** list of selecetd relations. */
    protected List<IIntervalRelation> _selectedRelations = new ArrayList<IIntervalRelation>();

    /** flag indicating that row selection is allowed. */
    protected boolean _rowSelectAllowed = true;

    /** flag indicatig that the selection of intervals is allowed. */
    protected boolean _intervalSelectAllow = true;

    /** flag indicatig that the selection of relations is allowed. */
    protected boolean _relationSelectAllow = true;

    /** flag indicating that the selection of multiple elements is allowed. */
    protected boolean _multiAllowed = true;

    /** flag indicating activated toggle mode for row selections. */
    protected boolean _rowSelectionToggleMode = false;

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return _selectedIntervals.size() == 0 && _selectedRows.size() == 0 && _selectedRelations.size() == 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasRowSelection() {
        return _selectedRows.size() != 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasIntervalSelection() {
        return _selectedIntervals.size() != 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasRelationSelection() {
        return _selectedRelations.size() != 0;
    }

    /**
     * {@inheritDoc}
     */
    public List<TimeBarRow> getSelectedRows() {
        return _selectedRows;
    }

    /**
     * {@inheritDoc}
     */
    public List<Interval> getSelectedIntervals() {
        return _selectedIntervals;
    }

    /**
     * {@inheritDoc}
     */
    public List<IIntervalRelation> getSelectedRelations() {
        return _selectedRelations;
    }

    /**
     * {@inheritDoc}
     */
    public void setRowSelectionAllowed(boolean allowed) {
        _rowSelectAllowed = allowed;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRowSelectionAllowed() {
        return _rowSelectAllowed;
    }

    /**
     * {@inheritDoc}
     */
    public void setIntervalSelectionAllowed(boolean allowed) {
        _intervalSelectAllow = allowed;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isIntervalSelectionAllowed() {
        return _intervalSelectAllow;
    }

    /**
     * {@inheritDoc}
     */
    public void setRelationSelectionAllowed(boolean allowed) {
        _relationSelectAllow = allowed;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRelationSelectionAllowed() {
        return _relationSelectAllow;
    }

    /**
     * {@inheritDoc}
     */
    public void setMultipleSelectionAllowed(boolean allowed) {
        _multiAllowed = allowed;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getMultipleSelectionAllowed() {
        return _multiAllowed;
    }

    /**
     * {@inheritDoc}
     */
    public void clearSelection() {
        if (!isEmpty()) {
            _selectedIntervals.clear();
            _selectedRows.clear();
            _selectedRelations.clear();
            fireSelectionChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clearIntervalSelection() {
        if (hasIntervalSelection()) {
            _selectedIntervals.clear();
            fireSelectionChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clearRowSelection() {
        if (hasRowSelection()) {
            _selectedRows.clear();
            fireSelectionChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clearRelationSelection() {
        if (hasRelationSelection()) {
            _selectedRelations.clear();
            fireSelectionChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectedRow(TimeBarRow row) {
        if (_rowSelectAllowed) {
            _selectedRows.clear();
            _selectedRows.add(row);
            fireSelectionChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectedRow(TimeBarRow row) {
        if (_rowSelectAllowed) {
            if (!_multiAllowed) {
                _selectedRows.clear();
                fireSelectionChanged();
            }
            if (!_selectedRows.contains(row)) {
                _selectedRows.add(row);
                fireElementAdded(row);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remSelectedRow(TimeBarRow row) {
        if (isSelected(row)) {
            _selectedRows.remove(row);
            fireElementRemoved(row);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSelected(TimeBarRow row) {
        return _selectedRows.contains(row);
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectedInterval(Interval interval) {
        if (_intervalSelectAllow) {
            boolean hasSelection = hasIntervalSelection();
            _selectedIntervals.clear();
            _selectedIntervals.add(interval);
            if (hasSelection) {
                fireSelectionChanged();
            } else {
                fireElementAdded(interval);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectedInterval(Interval interval) {
        if (_intervalSelectAllow) {
            if (!_multiAllowed) {
                _selectedIntervals.clear();
                fireSelectionChanged();
            }
            if (!_selectedIntervals.contains(interval)) {
                _selectedIntervals.add(interval);
                fireElementAdded(interval);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remSelectedInterval(Interval interval) {
        if (isSelected(interval)) {
            _selectedIntervals.remove(interval);
            fireElementRemoved(interval);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remSelectedIntervals(List<Interval> intervals) {
        _selectedIntervals.removeAll(intervals);
        fireSelectionChanged();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSelected(Interval interval) {
        return _selectedIntervals.contains(interval);
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectedRelation(IIntervalRelation relation) {
        if (_relationSelectAllow) {
            _selectedRelations.clear();
            _selectedRelations.add(relation);
            fireSelectionChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectedRelation(IIntervalRelation relation) {
        if (_relationSelectAllow) {
            if (!_multiAllowed) {
                _selectedRelations.clear();
                fireSelectionChanged();
            }
            if (!_selectedRelations.contains(relation)) {
                _selectedRelations.add(relation);
                fireElementAdded(relation);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remSelectedRelation(IIntervalRelation relation) {
        if (isSelected(relation)) {
            _selectedRelations.remove(relation);
            fireElementRemoved(relation);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remSelectedRelations(List<IIntervalRelation> relations) {
        _selectedRelations.removeAll(relations);
        fireSelectionChanged();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSelected(IIntervalRelation relation) {
        return _selectedRelations.contains(relation);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addTimeBarSelectionListener(TimeBarSelectionListener tbsl) {
        if (_listenerList == null) {
            _listenerList = new Vector<TimeBarSelectionListener>();
        }
        _listenerList.add(tbsl);
    }

    /**
     * {@inheritDoc}
     */
    public void remTimeBarSelectionListener(TimeBarSelectionListener tbsl) {
        if (_listenerList != null) {
            _listenerList.remove(tbsl);
        }
    }

    /**
     * Inform listeners about a general change of the selection.
     * 
     */
    protected void fireSelectionChanged() {
        if (_listenerList != null) {
            for (TimeBarSelectionListener listener : _listenerList) {
                listener.selectionChanged(this);
            }
        }
    }

    /**
     * Inform listeners about the addition of a new element in the selection.
     * 
     * @param element newly selected element
     */
    protected void fireElementAdded(Object element) {
        if (_listenerList != null) {
            for (TimeBarSelectionListener listener : _listenerList) {
                listener.elementAddedToSelection(this, element);
            }
        }
    }

    /**
     * Inform listeners about the removal of an element from the selection.
     * 
     * @param element the removed element
     */
    protected void fireElementRemoved(Object element) {
        if (_listenerList != null) {
            for (TimeBarSelectionListener listener : _listenerList) {
                listener.elementRemovedFromSelection(this, element);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean getRowSelectionToggleMode() {
        return _rowSelectionToggleMode;
    }

    /**
     * {@inheritDoc}
     */
    public void setRowSelectionToggleMode(boolean activated) {
        _rowSelectionToggleMode = activated;
    }

}
