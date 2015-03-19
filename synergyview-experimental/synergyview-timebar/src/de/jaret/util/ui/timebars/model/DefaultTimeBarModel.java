/*
 *  File: DefaultTimeBarModel.java 
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

/**
 * A default implementation of the TimeBarModel interface. Extends the AbstractTimeBarModel.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultTimeBarModel.java 886 2009-10-08 22:08:27Z kliem $
 */
public class DefaultTimeBarModel extends AbstractTimeBarModel {
    /** list of the rows. */
    protected List<TimeBarRow> _rows = new ArrayList<TimeBarRow>();

    /**
     * {@inheritDoc}
     */
    public TimeBarRow getRow(int row) {
        return (TimeBarRow) _rows.get(row);
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        return _rows.size();
    }

    /**
     * Add a row.
     * 
     * @param row row to add.
     */
    public void addRow(TimeBarRow row) {
        addRow(-1, row);
    }

    /**
     * Add a row.
     * 
     * @param index index the row should be inserted. -1 marks append to the end.
     * @param row row to add.
     */
    public void addRow(int index, TimeBarRow row) {
        if (index == -1) {
            _rows.add(row);
        } else {
            _rows.add(index, row);
        }
        if (_minDate == null) {
            _minDate = row.getMinDate();
            _maxDate = row.getMaxDate();
        } else if (row.getMinDate() != null && row.getMaxDate() != null) {
            if (_minDate.compareTo(row.getMinDate()) > 0) {
                _minDate = row.getMinDate();
            } 
            if (_maxDate.compareTo(row.getMaxDate()) < 0) {
                _maxDate = row.getMaxDate();
            }
        }
        row.addTimeBarRowListener(this);
        fireRowAdded(row);
    }

    /**
     * Remove a row from the model.
     * 
     * @param row row to remove
     */
    public void remRow(TimeBarRow row) {
        if (_rows.contains(row)) {
            row.remTimeBarRowListener(this);
            _rows.remove(row);
            updateMinMax();
            fireRowRemoved(row);
        }
    }

    /**
     * Retrieve the model index of a given row.
     * 
     * @param row row to check
     * @return index or -1
     */
    public int getIndexForRow(TimeBarRow row) {
        return _rows.indexOf(row);
    }

}
