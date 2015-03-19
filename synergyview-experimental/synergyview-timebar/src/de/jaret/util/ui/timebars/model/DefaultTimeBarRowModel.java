/*
 *  File: DefaultTimeBarRowModel.java 
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.jaret.util.date.Interval;

/**
 * A base implementation of the TimeBarRow interface as an extension of the abstract implementation
 * AbstractTimeBarRowModel. When this default model is used be aware of some possible performance optimizations that can
 * be made when dealing with a specialized model (see comments).
 * 
 * @author Peter Kliem
 * @version $Id: DefaultTimeBarRowModel.java 858 2009-05-06 22:15:51Z kliem $
 */
public class DefaultTimeBarRowModel extends AbstractTimeBarRowModel {
    /** list storing the intervals. */
    protected List<Interval> _intervals = new ArrayList<Interval>();

    /**
     * Default constructor.
     * 
     */
    public DefaultTimeBarRowModel() {
    }

    /**
     * Construct a row supplying a header.
     * 
     * @param header row header
     */
    public DefaultTimeBarRowModel(TimeBarRowHeader header) {
        setRowHeader(header);
    }

    /**
     * {@inheritDoc}
     */
    public List<Interval> getIntervals() {
        return _intervals;
    }

    /**
     * Add an interval.
     * 
     * @param interval interval to add
     */
    public void addInterval(Interval interval) {
        _intervals.add(interval);
        // Keep intervals sorted. This is not a performance optimized solution ...
        // Optimize in cutom implementations
        Collections.sort(_intervals, new Comparator<Interval>() {
            public int compare(Interval i1, Interval i2) {
                return i1.getBegin().compareTo(i2.getBegin());
            }
        });
        // Check min/max modifications by the added interval
        if (_minDate == null || _intervals.size() == 1) {
            _minDate = interval.getBegin().copy();
            _maxDate = interval.getEnd().copy();
        } else {
            if (_minDate.compareTo(interval.getBegin()) > 0) {
                _minDate = interval.getBegin().copy();
            } 
            if (_maxDate.compareTo(interval.getEnd()) < 0) {
                _maxDate = interval.getEnd().copy();
            }
        }
        interval.addPropertyChangeListener(this);
        fireElementAdded(interval);
    }

    /**
     * Add more than one interval to the row (avoiding unnecessary updates for every interval).
     * 
     * @param intervals list of intervals
     */
    public void addIntervals(List<Interval> intervals) {
        _intervals.addAll(intervals);
        // Keep intervals sorted. This is not a permance optimized solution ...
        // Optimize in cutom implementations
        Collections.sort(_intervals, new Comparator<Interval>() {
            public int compare(Interval i1, Interval i2) {
                return i1.getBegin().compareTo(i2.getBegin());
            }
        });
        // Check min/max modifications by the added intervals
        // min date is easy because we sorted by begin date
        _minDate = _intervals.get(0).getBegin().copy();
        // max date is not that siple since a longer interval may begin before the last beginnign interval
        _maxDate = _minDate.copy(); // safe start
        for (Interval interval : _intervals) {
            if (interval.getEnd().compareTo(_maxDate) > 0) {
                _maxDate = interval.getEnd();
            }
        }
        // now copy the date because we are holding a reference
        _maxDate = _maxDate.copy();

        // be a property change listener on every interval
        _maxDate = _intervals.get(_intervals.size() - 1).getEnd().copy();
        for (Interval interval : intervals) {
            interval.addPropertyChangeListener(this);
        }
        // the whole row has become invalid -> tell the world
        fireRowDataChanged();
    }

    /**
     * Remove an interval.
     * 
     * @param interval interval to remove
     */
    public void remInterval(Interval interval) {
        if (_intervals.contains(interval)) {
            _intervals.remove(interval);
            // check min/max the hard way (optimize in custom implementations!)
            updateMinMax();
            interval.removePropertyChangeListener(this);
            fireElementRemoved(interval);
        }
    }

    /**
     * Remove a list of intervals. Please note that the selection can not be adjusted correctly -> do it yourself.
     * 
     * @param intervals list of intervals to remove
     */
    public void remIntervals(List<Interval> intervals) {
        boolean hasChanges = false;
        for (Interval interval : intervals) {
            if (_intervals.contains(interval)) {
                _intervals.remove(interval);
                interval.removePropertyChangeListener(this);
                hasChanges = true;
            }
        }
        if (hasChanges) {
            // check min/max the hard way (optimize in custom implementations!)
            updateMinMax();
            // the whole row has become invalid -> tell the world
            fireRowDataChanged();
        }
    }

    /**
     * Clear the row of all intervals. Please note that the selection can not be adjusted correctly -> do it yourself.
     */
    public void clear() {
        for (Interval interval : _intervals) {
            interval.removePropertyChangeListener(this);
        }
        _intervals.clear();
        _minDate = null;
        _maxDate = null;
        // the whole row has become invalid -> tell the world
        fireRowDataChanged();
    }

}
