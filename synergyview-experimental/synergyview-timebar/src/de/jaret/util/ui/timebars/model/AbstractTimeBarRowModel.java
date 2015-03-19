/*
 *  File: AbstractTimeBarRowModel.java 
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;

/**
 * An abstract base implementation of the TimeBarRow interface. The part to implement is the storage and retrieval of
 * the intervals in the row. Care has to be taken to update the min/max date fields with the intervals. Implementations
 * for some additional methods are given, but are not guaranteed to be optimal (in fact they are not!).
 * 
 * @author Peter Kliem
 * @version $Id: AbstractTimeBarRowModel.java 800 2008-12-27 22:27:33Z kliem $
 */
public abstract class AbstractTimeBarRowModel implements TimeBarRow, PropertyChangeListener {
    /** the row header of the row. */
    protected TimeBarRowHeader _header;

    /**
     * the minimum date of all intervals. This should always be up to date by updating.
     */
    protected JaretDate _minDate;

    /**
     * the maximum date of all intervals. This should always be up to date by updating.
     */
    protected JaretDate _maxDate;

    /** the registered listeners. */
    protected List<TimeBarRowListener> _listenerList;

    /**
     * Default constructor. Note that a header should be set.
     * 
     */
    public AbstractTimeBarRowModel() {
    }

    /**
     * onstructor supplying a header.
     * 
     * @param header row header to be used
     */
    public AbstractTimeBarRowModel(TimeBarRowHeader header) {
        setRowHeader(header);
    }

    /**
     * {@inheritDoc} Remains abstract and has to implemented.
     */
    public abstract List<Interval> getIntervals();

    /**
     * {@inheritDoc} Very simple (imperformant) implementation.
     */
    public List<Interval> getIntervals(JaretDate beginDate, JaretDate endDate) {
        // List is sorted
        List<Interval> result = new ArrayList<Interval>();
        if (getIntervals() != null) {
            for (Interval interval : getIntervals()) {
                if ((interval.getBegin().compareTo(beginDate) >= 0 && interval.getBegin().compareTo(endDate) <= 0)
                        || (interval.getEnd().compareTo(beginDate) >= 0 && interval.getEnd().compareTo(endDate) <= 0)
                        || (interval.getBegin().compareTo(beginDate) <= 0 && interval.getEnd().compareTo(endDate) >= 0)) {
                    result.add(interval);
                }
                // this may be an optimization if it is guaranteed that the
                // intervals are sorted.
                // else if (interval.getBegin().compareTo(endDate) >= 0) {
                // break;
                // }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<Interval> getIntervals(JaretDate date) {
        // Liste ist sortiert
        List<Interval> result = new ArrayList<Interval>();
        for (Interval interval : getIntervals()) {
            if (interval.contains(date)) {
                result.add(interval);
            }
            // see above
            // else if (interval.getBegin().compareTo(date) >= 0) {
            // break;
            // }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarRowHeader getRowHeader() {
        return _header;
    }

    /**
     * Set the row header.
     * 
     * @param header header to set
     */
    public void setRowHeader(TimeBarRowHeader header) {
        if (_header != null) {
            _header.removePropertyChangeListener(this);
        }
        _header = header;
        if (_header != null) {
            _header.addPropertyChangeListener(this);
        }
        fireHeaderChanged();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addTimeBarRowListener(TimeBarRowListener tbrl) {
        if (_listenerList == null) {
            _listenerList = new Vector<TimeBarRowListener>();
        }
        _listenerList.add(tbrl);
    }

    /**
     * {@inheritDoc}
     */
    public void remTimeBarRowListener(TimeBarRowListener tbrl) {
        if (_listenerList != null) {
            _listenerList.remove(tbrl);
        }
    }

    /**
     * Inform listeners that all row data may have changed.
     * 
     */
    protected void fireRowDataChanged() {
        if (_listenerList != null) {
            for (TimeBarRowListener tbrl : _listenerList) {
                tbrl.rowDataChanged(this);
            }
        }
    }

    /**
     * Inform listeners that a new interval has been added.
     * 
     * @param element added interval
     */
    protected void fireElementAdded(Interval element) {
        if (_listenerList != null) {
            for (TimeBarRowListener tbrl : _listenerList) {
                tbrl.elementAdded(this, element);
            }
        }
    }

    /**
     * Inform listeners that an interval has been removed.
     * 
     * @param element removed interval
     */
    protected void fireElementRemoved(Interval element) {
        if (_listenerList != null) {
            for (TimeBarRowListener tbrl : _listenerList) {
                tbrl.elementRemoved(this, element);
            }
        }
    }

    /**
     * Inform listeners that an element has changed.
     * 
     * @param element changed interval
     */
    protected void fireElementChanged(Interval element) {
        if (_listenerList != null) {
            for (TimeBarRowListener tbrl : _listenerList) {
                tbrl.elementChanged(this, element);
            }
        }
    }

    /**
     * Inform listeners about a chnage of the header.
     * 
     */
    protected void fireHeaderChanged() {
        if (_listenerList != null) {
            for (TimeBarRowListener tbrl : _listenerList) {
                tbrl.headerChanged(this, getRowHeader());
            }
        }
    }

    /**
     * @return Returns the maxDate.
     */
    public JaretDate getMaxDate() {
        return _maxDate;
    }

    /**
     * @return Returns the minDate.
     */
    public JaretDate getMinDate() {
        return _minDate;
    }

    /**
     * Handle property changes of the intervals in the row and of the header.
     * 
     * @param evt propChange event from the model elements
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof Interval) {
            updateMinMax();
            fireElementChanged((Interval) evt.getSource());
        } else if (evt.getSource() == _header) {
            fireHeaderChanged();
        } else {
            throw new RuntimeException("Unknown sender " + evt.getSource());
        }
    }

    /**
     * Updates the minimum and the maximum dates that may be queried by users of the row.
     * 
     */
    protected void updateMinMax() {
        // need to check all intervals since the limits may have been reduced
        // ...
        _minDate = null;
        _maxDate = null;
        for (Interval i : getIntervals()) {
            if (_minDate == null || _minDate.compareTo(i.getBegin()) > 0) {
                _minDate = i.getBegin().copy();
            }
            if (_maxDate == null || _maxDate.compareTo(i.getEnd()) < 0) {
                _maxDate = i.getEnd().copy();
            }
        }
        if (_minDate == null) {
            _minDate = new JaretDate();
        }
        if (_maxDate == null) {
            _maxDate = new JaretDate();
        }
    }
}
