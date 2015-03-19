/*
 *  File: TourenTableModel.java 
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
package de.jaret.examples.timebars.touren;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.jaret.util.ui.timebars.TimeBarRowFilter;
import de.jaret.util.ui.timebars.TimeBarRowSorter;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;

/**
 * @author Peter Kliem
 * @version $Id: TourenTableModel.java 160 2007-01-02 22:02:40Z olk $
 */
public class TourenTableModel extends AbstractTableModel {
    TimeBarModel _timeBarModel;
    TimeBarRowFilter _rowFilter;
    TimeBarRowSorter _rowSorter;
    List _rowList = new ArrayList();

    public TourenTableModel(TimeBarModel tbm) {
        _timeBarModel = tbm;
        updateRowList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 4;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Tour t = (Tour) getRow(rowIndex);
        switch (columnIndex) {
        case 0:
            return t.getRowHeader().toString();
        case 1:
            return new Integer(t.getIntervals().size());
        case 2:
            return t.getMinDate().toDisplayString();
        case 3:
            return t.getMaxDate().toDisplayString();
        default:
            return "Fehler";
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "header";
        case 1:
            return "#Elements";
        case 2:
            return "min";
        case 3:
            return "max";
        default:
            return "Fehler";
        }
    }

    public void updateRowList() {
        int oldRowCount = _rowList != null ? _rowList.size() : 0;
        _rowList = new ArrayList();
        // copy filtered if filter is set
        for (int r = 0; r < _timeBarModel.getRowCount(); r++) {
            if (_rowFilter != null) {
                // filter set
                if (_rowFilter.isInResult(_timeBarModel.getRow(r))) {
                    _rowList.add(_timeBarModel.getRow(r));
                }
            } else {
                _rowList.add(_timeBarModel.getRow(r));
            }
        }
        // sorter set? -> sort the row list
        if (_rowSorter != null) {
            Collections.sort(_rowList, _rowSorter);
        }
    }

    /**
     * get a timebar row from the filterd/sorted list
     * 
     * @param idx
     * @return
     */
    public TimeBarRow getRow(int idx) {
        return (TimeBarRow) _rowList.get(idx);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return _rowList.size();
    }

    public void setTimeBarRowFilter(TimeBarRowFilter rowFilter) {
        _rowFilter = rowFilter;
        updateRowList();
        fireTableDataChanged();
    }

    public void setTimeBarRowSorter(TimeBarRowSorter rowSorter) {
        _rowSorter = rowSorter;
        updateRowList();
        fireTableDataChanged();
    }

}
