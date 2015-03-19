/*
 *  File: ControlPanel.java 
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
package de.jaret.examples.timebars.fzd.swing;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jaret.examples.timebars.fzd.model.Fahrt;
import de.jaret.examples.timebars.fzd.model.Fahrzeug;
import de.jaret.examples.timebars.fzd.model.Umlauf;
import de.jaret.util.date.Interval;
import de.jaret.util.misc.PropertyObservableBase;
import de.jaret.util.ui.timebars.AbstractTimeBarIntervalFilter;
import de.jaret.util.ui.timebars.TimeBarIntervalFilter;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarRowFilter;
import de.jaret.util.ui.timebars.TimeBarRowSorter;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarSelectionModel;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * @author Peter Kliem
 * @version $Id: ControlPanel.java 259 2007-02-16 13:54:00Z olk $
 */
public class ControlPanel extends JPanel implements ChangeListener, ActionListener {
    TimeBarViewer _viewer;
    JSlider _timeScaleSlider;
    JSlider _rowHeigthSlider;
    JComboBox _sorterCombo;
    JComboBox _filterCombo;
    JComboBox _intervalFilterCombo;
    TimeBarMarkerImpl _marker;

    public ControlPanel(TimeBarViewer viewer, TimeBarMarkerImpl marker) {
        _viewer = viewer;
        _marker = marker;
        setLayout(new FlowLayout());
        createControls();
    }

    /**
     * 
     */
    private void createControls() {
        _timeScaleSlider = new JSlider(200, 500000);
        _timeScaleSlider.addChangeListener(this);
        add(_timeScaleSlider);
        _rowHeigthSlider = new JSlider(10, 300);
        _rowHeigthSlider.addChangeListener(this);
        add(_rowHeigthSlider);

        _sorterCombo = new JComboBox();
        _sorterCombo.addItem("No sorter");
        _sorterCombo.addItem(new LabelSorter());
        _sorterCombo.addItem(new KmSorter());
        _sorterCombo.addItem(new SelectionSorter(_viewer));
        _sorterCombo.addItem(new FahrtRestSorter(_marker));
        _sorterCombo.addActionListener(this);
        add(_sorterCombo);

        _filterCombo = new JComboBox();
        _filterCombo.addItem("No Filter");
        _filterCombo.addItem(new KmGt500Filter());
        _filterCombo.addItem(new KmEvenFilter());
        _filterCombo.addItem(new HasAssignementFilter());
        _filterCombo.addActionListener(this);
        add(_filterCombo);

        _intervalFilterCombo = new JComboBox();
        _intervalFilterCombo.addItem("No Interval Filter");
        _intervalFilterCombo.addItem(new UmlaufFilter());
        _intervalFilterCombo.addItem(new FahrtFilter());
        _intervalFilterCombo.addActionListener(this);
        add(_intervalFilterCombo);

    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == _timeScaleSlider) {
            double pixPerSecond = (double) _timeScaleSlider.getValue() / (24.0 * 60 * 60);
            _viewer.setPixelPerSecond(pixPerSecond);
        } else if (e.getSource() == _rowHeigthSlider) {
            _viewer.setRowHeight(_rowHeigthSlider.getValue());
        }
    }

    class LabelSorter extends PropertyObservableBase implements TimeBarRowSorter {
        /**
         * {@inheritDoc}
         */
        public int compare(TimeBarRow o1, TimeBarRow o2) {
            Fahrzeug f1 = (Fahrzeug) o1;
            Fahrzeug f2 = (Fahrzeug) o2;
            return f1.getFahrzeugInfo().getLabel().compareTo(f2.getFahrzeugInfo().getLabel());
        }

        public String toString() {
            return "LabelSorter";
        }
    }

    class KmSorter extends PropertyObservableBase implements TimeBarRowSorter {
        /**
         * {@inheritDoc}
         */
        public int compare(TimeBarRow o1, TimeBarRow o2) {
            Fahrzeug f1 = (Fahrzeug) o1;
            Fahrzeug f2 = (Fahrzeug) o2;
            return f1.getFahrzeugInfo().getKilometer() - f2.getFahrzeugInfo().getKilometer();
        }

        public String toString() {
            return "KM-Sorter";
        }
    }

    class SelectionSorter extends PropertyObservableBase implements TimeBarRowSorter {
        TimeBarViewer _viewer;

        public SelectionSorter(TimeBarViewer viewer) {
            _viewer = viewer;
        }

        /**
         * {@inheritDoc}
         */
        public int compare(TimeBarRow o1, TimeBarRow o2) {
            Fahrzeug f1 = (Fahrzeug) o1;
            Fahrzeug f2 = (Fahrzeug) o2;
            TimeBarSelectionModel selModel = _viewer.getSelectionModel();
            if (selModel.isSelected(f1) && selModel.isSelected(f2)) {
                return 0;
            } else if (selModel.isSelected(f1) && !selModel.isSelected(f2)) {
                return -1;
            } else {
                return 1;
            }
        }

        public String toString() {
            return "Selection Sorter";
        }
    }

    class FahrtRestSorter extends PropertyObservableBase implements TimeBarRowSorter {
        TimeBarMarkerImpl _marker;

        public FahrtRestSorter(TimeBarMarkerImpl marker) {
            _marker = marker;
            ;
        }

        /**
         * {@inheritDoc}
         */
        public int compare(TimeBarRow o1, TimeBarRow o2) {
            Fahrzeug f1 = (Fahrzeug) o1;
            Fahrzeug f2 = (Fahrzeug) o2;
            int restInSeconds1 = f1.getFahrtRest(_marker.getDate());
            int restInSeconds2 = f2.getFahrtRest(_marker.getDate());
            return restInSeconds1 - restInSeconds2;
        }

        public String toString() {
            return "FahrtRest Sorter";
        }
    }

    class KmGt500Filter extends PropertyObservableBase implements TimeBarRowFilter {
        /**
         * {@inheritDoc}
         */
        public boolean isInResult(TimeBarRow row) {
            Fahrzeug f = (Fahrzeug) row;
            return f.getFahrzeugInfo().getKilometer() > 500;
        }

        public String toString() {
            return "km > 500";
        }
    }

    class KmEvenFilter extends PropertyObservableBase implements TimeBarRowFilter {
        /**
         * {@inheritDoc}
         */
        public boolean isInResult(TimeBarRow row) {
            Fahrzeug f = (Fahrzeug) row;
            return f.getFahrzeugInfo().getKilometer() % 2 == 0;
        }

        public String toString() {
            return "km even";
        }
    }

    class HasAssignementFilter extends PropertyObservableBase implements TimeBarRowFilter {
        /**
         * {@inheritDoc}
         */
        public boolean isInResult(TimeBarRow row) {
            Fahrzeug f = (Fahrzeug) row;
            return f.getUmlaeufe().size() > 0;
        }

        public String toString() {
            return "HasAssignment";
        }
    }

    class UmlaufFilter extends AbstractTimeBarIntervalFilter implements TimeBarIntervalFilter {
        public boolean isInResult(Interval interval) {
            return interval instanceof Umlauf;
        }

        public String toString() {
            return "Nur Umläufe";
        }
    }

    class FahrtFilter extends AbstractTimeBarIntervalFilter implements TimeBarIntervalFilter {
        public boolean isInResult(Interval interval) {
            return interval instanceof Fahrt;
        }

        public String toString() {
            return "Nur Fahrten";
        }
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == _sorterCombo) {
            Object sel = _sorterCombo.getSelectedItem();
            if (sel instanceof TimeBarRowSorter) {
                _viewer.setRowSorter((TimeBarRowSorter) sel);
            } else {
                _viewer.setRowSorter(null);
            }
        } else if (e.getSource() == _filterCombo) {
            Object sel = _filterCombo.getSelectedItem();
            if (sel instanceof TimeBarRowFilter) {
                _viewer.setRowFilter((TimeBarRowFilter) sel);
            } else {
                _viewer.setRowFilter(null);
            }
        } else if (e.getSource() == _intervalFilterCombo) {
            Object sel = _intervalFilterCombo.getSelectedItem();
            if (sel instanceof TimeBarIntervalFilter) {
                _viewer.setIntervalFilter((TimeBarIntervalFilter) sel);
            } else {
                _viewer.setIntervalFilter(null);
            }
        }
    }

}