/*
 *  File: AbstractTimeBarModel.java 
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

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;

/**
 * An abstract implementation of the TimeBarModel interface. The storage of the rows has to be added. Care has to be
 * taken to register with the rows via the TimeBarRowListener interface. If this is ommited changes in the rows will not
 * autmatic propagate to the TimeBarModelListeners registered with the model. The implementation must also care about
 * the min/max fields and set them.
 * 
 * @author Peter Kliem
 * @version $Id: AbstractTimeBarModel.java 800 2008-12-27 22:27:33Z kliem $
 */
public abstract class AbstractTimeBarModel implements TimeBarModel, TimeBarRowListener {
    /** Minimum date of the complete model. */
    protected JaretDate _minDate;

    /** Maximum date of the complete model. */
    protected JaretDate _maxDate;

    /** List of model listeners. */
    protected List<TimeBarModelListener> _listenerList;

    /**
     * {@inheritDoc}
     */
    public JaretDate getMinDate() {
        if (_minDate == null) {
            // no mindate -> keep everybody happy
            return new JaretDate();
        }
        return _minDate.copy();
    }

    /**
     * {@inheritDoc}
     */
    public JaretDate getMaxDate() {
        if (_maxDate == null) {
            // no maxdate -> keep everybody happy
            return new JaretDate();
        }
        return _maxDate.copy();
    }

    /**
     * {@inheritDoc } This default implementation is a brute force implementation. Real implementation should override
     * the default implementation if the data model contains a reference to improve performance.
     */
    public TimeBarRow getRowForInterval(Interval interval) {
        for (int i = 0; i < getRowCount(); i++) {
            if (getRow(i).getIntervals().contains(interval)) {
                return getRow(i);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addTimeBarModelListener(TimeBarModelListener tbml) {
        if (_listenerList == null) {
            _listenerList = new Vector<TimeBarModelListener>();
        }
        _listenerList.add(tbml);
    }

    /**
     * {@inheritDoc}
     */
    public void remTimeBarModelListener(TimeBarModelListener tbml) {
        if (_listenerList != null) {
            _listenerList.remove(tbml);
        }
    }

    /**
     * Inform listeners about unspecific or multiple changes in the model. Do use the more specific methods if possible.
     */
    protected void fireModelDataChanged() {
        if (_listenerList != null) {
            for (TimeBarModelListener listener : _listenerList) {
                listener.modelDataChanged(this);
            }
        }
    }

    /**
     * Inform listeners about a new row.
     * 
     * @param row new row
     */
    protected void fireRowAdded(TimeBarRow row) {
        if (_listenerList != null) {
            for (TimeBarModelListener listener : _listenerList) {
                listener.rowAdded(this, row);
            }
        }
    }

    /**
     * Inform listeners about a removed row.
     * 
     * @param row removed row
     */
    protected void fireRowRemoved(TimeBarRow row) {
        if (_listenerList != null) {
            for (TimeBarModelListener listener : _listenerList) {
                listener.rowRemoved(this, row);
            }
        }
    }

    /**
     * Inform listeners about a changed row.
     * 
     * @param row changed row
     */
    protected void fireRowDataChanged(TimeBarRow row) {
        if (_listenerList != null) {
            for (TimeBarModelListener listener : _listenerList) {
                listener.rowDataChanged(this, row);
            }
        }
    }

    /**
     * Inform listeners about a changed header.
     * 
     * @param row row
     * @param header header
     */
    protected void fireHeaderChanged(TimeBarRow row, TimeBarRowHeader header) {
        if (_listenerList != null) {
            for (TimeBarModelListener listener : _listenerList) {
                listener.headerChanged(this, row, header);
            }
        }
    }

    /**
     * Inform listeners about a changed element in a specific row.
     * 
     * @param row row of the element
     * @param element changed element
     */
    protected void fireElementChanged(TimeBarRow row, Interval element) {
        if (_listenerList != null) {
            for (TimeBarModelListener listener : _listenerList) {
                listener.elementChanged(this, row, element);
            }
        }
    }

    /**
     * Inform listeners about a new element in a specific row.
     * 
     * @param row row of the element
     * @param element new element
     */
    protected void fireElementAdded(TimeBarRow row, Interval element) {
        if (_listenerList != null) {
            for (TimeBarModelListener listener : _listenerList) {
                listener.elementAdded(this, row, element);
            }
        }
    }

    /**
     * Inform listeners about a removed element in a specific row.
     * 
     * @param row row of the element
     * @param element removed element
     */
    protected void fireElementRemoved(TimeBarRow row, Interval element) {
        if (_listenerList != null) {
            for (TimeBarModelListener listener : _listenerList) {
                listener.elementRemoved(this, row, element);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void elementAdded(TimeBarRow row, Interval element) {
        updateMinMax();
        fireElementAdded(row, element);
    }

    /**
     * {@inheritDoc}
     */
    public void elementRemoved(TimeBarRow row, Interval element) {
        updateMinMax();
        fireElementRemoved(row, element);
    }

    /**
     * {@inheritDoc}
     */
    public void elementChanged(TimeBarRow row, Interval element) {
        updateMinMax();
        fireElementChanged(row, element);
    }

    /**
     * {@inheritDoc}
     */
    public void headerChanged(TimeBarRow row, TimeBarRowHeader newHeader) {
        fireHeaderChanged(row, newHeader);
    }

    /**
     * {@inheritDoc}
     */
    public void rowDataChanged(TimeBarRow row) {
        updateMinMax();
        fireRowDataChanged(row);
    }

    /**
     * Update the min/max date of the model.
     * 
     */
    protected void updateMinMax() {
        _minDate = null;
        _maxDate = null;
        for (int i = 0; i < getRowCount(); i++) {
            TimeBarRow r = getRow(i);
            if ((_minDate == null && r.getMinDate() != null)
                    || (r.getMinDate() != null && _minDate.compareTo(r.getMinDate()) > 0)) {
                _minDate = r.getMinDate().copy();
            }
            if ((_maxDate == null && r.getMaxDate() != null)
                    || (r.getMaxDate() != null && _maxDate.compareTo(r.getMaxDate()) < 0)) {
                _maxDate = r.getMaxDate().copy();
            }
        }
    }

}
