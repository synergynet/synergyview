/*
 *  File: AddingTimeBarRowModel.java 
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

import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;

/**
 * Timebar row model that adds up (merges) all intervals added.
 * 
 * @author Peter Kliem
 * @version $Id: AddingTimeBarRowModel.java 592 2007-10-14 22:02:30Z olk $
 */
public class AddingTimeBarRowModel extends AbstractTimeBarRowModel implements TimeBarRowListener {
    /** intervals in this row (the one added up merged interval, actually). */
    protected List<Interval> _intervals = new ArrayList<Interval>();

    /** the merged interval. */
    protected MergedInterval _mergedInterval;

    /** rows beeing "add up". */
    protected List<TimeBarRow> _rows = new ArrayList<TimeBarRow>();

    /**
     * Create an adding row model.
     * 
     * @param header the headr of the row
     */
    public AddingTimeBarRowModel(TimeBarRowHeader header) {
        super(header);
    }

    /**
     * {@inheritDoc}
     */
    public List<Interval> getIntervals() {
        return _intervals;
    }

    /**
     * Add a row.
     * 
     * @param row row to add
     */
    public void addRow(TimeBarRow row) {
        _rows.add(row);
        if (row.getMinDate() != null) {
            checkMergedInterval();
            if (_mergedInterval.getBegin() == null || row.getMinDate().compareTo(_mergedInterval.getBegin()) < 0) {
                _mergedInterval.setBeginX(row.getMinDate().copy());
                _minDate = _mergedInterval.getBegin().copy();
            }
            if (_mergedInterval.getEnd() == null || row.getMaxDate().compareTo(_mergedInterval.getEnd()) > 0) {
                _mergedInterval.setEndX(row.getMaxDate().copy());
                _maxDate = _mergedInterval.getEnd().copy();
            }
        }
        row.addTimeBarRowListener(this);
    }

    /**
     * Remove a row from the list of rows adding up.
     * 
     * @param row row to remove
     */
    public void remRow(TimeBarRow row) {
        if (_rows.remove(row)) {
            row.remTimeBarRowListener(this);
            checkBounds();
        }
    }

    /**
     * Check existence of merged interval and create one if necessary.
     * 
     */
    protected void checkMergedInterval() {
        if (_mergedInterval == null) {
            _mergedInterval = new MergedInterval();
            _intervals.add(_mergedInterval);
        }
    }

    // ---------------------- TimeBarRowListener
    /**
     * {@inheritDoc} Check whether the bounds are still correct.
     */
    public void rowDataChanged(TimeBarRow row) {
        checkBounds();
    }

    /**
     * {@inheritDoc} Check whether the bounds are still correct.
     */
    public void elementAdded(TimeBarRow row, Interval element) {
        checkBounds();
    }

    /**
     * {@inheritDoc} Check whether the bounds are still correct.
     */
    public void elementRemoved(TimeBarRow row, Interval element) {
        checkBounds();
    }

    /**
     * {@inheritDoc} Check whether the bounds are still correct.
     */
    public void elementChanged(TimeBarRow row, Interval element) {
        checkBounds();
    }

    /**
     * Check the bounds of the merged interval against all added rows.
     */
    protected void checkBounds() {
        // get min and max date of all rows in the row list
        JaretDate min = null;
        JaretDate max = null;
        for (TimeBarRow row : _rows) {
            if (row.getMinDate() != null && row.getIntervals().size() > 0) {
                if (min == null || row.getMinDate().compareTo(min) < 0) {
                    min = row.getMinDate().copy();
                }
            }
            if (row.getMaxDate() != null && row.getIntervals().size() > 0) {
                if (max == null || row.getMaxDate().compareTo(max) > 0) {
                    max = row.getMaxDate().copy();
                }
            }
        }
        // if min ist still null no row contains an interval
        if (min == null) {
            // no interval in any row
            if (_mergedInterval != null) {
                _intervals.remove(_mergedInterval);
                fireElementRemoved(_mergedInterval);
                _mergedInterval = null;
            }

        } else {
            checkMergedInterval(); // make sure the merged interval exists and
            // is added to the intervals list
            boolean changed = false;
            if (_mergedInterval != null
                    && (_mergedInterval.getBegin() == null || !_mergedInterval.getBegin().equals(min))) {
                _mergedInterval.setBeginX(min);
                _minDate = _mergedInterval.getBegin().copy();
                changed = true;
            }
            if (_mergedInterval != null && (_mergedInterval.getEnd() == null || !_mergedInterval.getEnd().equals(max))) {
                _mergedInterval.setEndX(max);
                _maxDate = _mergedInterval.getEnd().copy();
                changed = true;
            }
            if (changed) {
                fireElementChanged(_mergedInterval);
            }
        }
    }

    /**
     * {@inheritDoc} Do nothing!
     */
    public void headerChanged(TimeBarRow row, TimeBarRowHeader newHeader) {
        // never mind
    }

    // -------------------------- END TimeBarRowListener

    /**
     * Simple extension of the basic interval implementation to make it read-only on the standard setters.
     */
    public class MergedInterval extends IntervalImpl {
        /**
         * Set the begin.
         * 
         * @param date begin date
         */
        public void setBeginX(JaretDate date) {
            super.setBegin(date);
        }

        /**
         * Set the end date.
         * 
         * @param date end date
         */
        public void setEndX(JaretDate date) {
            super.setEnd(date);
        }

        /**
         * Overridden to have no function. ({@inheritDoc})
         */
        public void setBegin(JaretDate begin) {
        }

        /**
         * Overridden to have no function. ({@inheritDoc})
         */
        public void setEnd(JaretDate end) {
        }
    }

}
