/*
 *  File: FahrtenTableModel.java 
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
package de.jaret.examples.timebars.fzd.model;

import javax.swing.table.AbstractTableModel;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarMarkerListener;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarModelListener;
import de.jaret.util.ui.timebars.model.TimeBarRow;

/**
 * @author Peter Kliem
 * @version $Id: FahrtenTableModel.java 259 2007-02-16 13:54:00Z olk $
 */
public class FahrtenTableModel extends AbstractTableModel implements TimeBarMarkerListener, TimeBarModelListener {

    private TimeBarMarkerImpl _marker;
    private ZuteilungsModel _zuteilungsModel;

    public FahrtenTableModel(ZuteilungsModel zuteilungsModel, TimeBarMarkerImpl marker) {
        _zuteilungsModel = zuteilungsModel;
        _marker = marker;
        _marker.addTimeBarMarkerListener(this);
        _zuteilungsModel.addTimeBarModelListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public int getColumnCount() {
        return 2;
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        return _zuteilungsModel.getRowCount();
    }

    /**
     * {@inheritDoc}
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Fahrzeug f = (Fahrzeug) _zuteilungsModel.getRow(rowIndex);
        if (columnIndex == 0) {
            return f.getFzdNummer();
        }
        Fahrt fahrt = f.getFahrt(_marker.getDate());
        if (fahrt == null) {
            return "keine";
        } else {
            return fahrt.getFahrtNummer();
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Fahrzeug";
        case 1:
            return "Fahrt";
        default:
            return "??";
        }
    }

    /**
     * {@inheritDoc}
     */
    public void markerMoved(TimeBarMarker marker, JaretDate oldDate, JaretDate currentDate) {
        fireTableDataChanged(); // sehr vereinfacht!
    }

    /**
     * {@inheritDoc}
     */
    public void markerDescriptionChanged(TimeBarMarker marker, String oldValue, String newValue) {

    }

    /**
     * {@inheritDoc}
     */
    public void rowAdded(TimeBarModel model, TimeBarRow row) {
        fireTableDataChanged(); // sehr vereinfacht!
    }

    /**
     * {@inheritDoc}
     */
    public void rowRemoved(TimeBarModel model, TimeBarRow row) {
        fireTableDataChanged(); // sehr vereinfacht!
    }

    /**
     * {@inheritDoc}
     */
    public void elementAdded(TimeBarModel model, TimeBarRow row, Interval element) {
        fireTableDataChanged(); // sehr vereinfacht!
    }

    /**
     * {@inheritDoc}
     */
    public void elementRemoved(TimeBarModel model, TimeBarRow row, Interval element) {
        fireTableDataChanged(); // sehr vereinfacht!
    }

    /**
     * {@inheritDoc}
     */
    public void elementChanged(TimeBarModel model, TimeBarRow row, Interval element) {
        fireTableDataChanged(); // sehr vereinfacht!
    }

    /**
     * {@inheritDoc}
     */
    public void headerChanged(TimeBarModel model, TimeBarRow row, Object newHeader) {
        fireTableDataChanged(); // sehr vereinfacht!
    }

    /**
     * {@inheritDoc}
     */
    public void modelDataChanged(TimeBarModel model) {
        fireTableDataChanged(); // sehr vereinfacht!
    }

    /**
     * {@inheritDoc}
     */
    public void rowDataChanged(TimeBarModel model, TimeBarRow row) {
        fireTableDataChanged(); // sehr vereinfacht!
    }
}
