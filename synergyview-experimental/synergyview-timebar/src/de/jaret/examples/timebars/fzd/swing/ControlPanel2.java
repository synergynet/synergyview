/*
 *  File: ControlPanel2.java 
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
import de.jaret.examples.timebars.fzd.model.UmlaufKette;
import de.jaret.util.date.Interval;
import de.jaret.util.misc.PropertyObservableBase;
import de.jaret.util.ui.timebars.AbstractTimeBarIntervalFilter;
import de.jaret.util.ui.timebars.TimeBarIntervalFilter;
import de.jaret.util.ui.timebars.TimeBarRowFilter;
import de.jaret.util.ui.timebars.TimeBarRowSorter;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * @author Peter Kliem
 * @version $Id: ControlPanel2.java 259 2007-02-16 13:54:00Z olk $
 */
public class ControlPanel2 extends JPanel implements ChangeListener, ActionListener {
    TimeBarViewer _viewer;
    JSlider _timeScaleSlider;
    JSlider _rowHeigthSlider;
    JComboBox _sorterCombo;
    JComboBox _filterCombo;
    JComboBox _intervalFilterCombo;

    public ControlPanel2(TimeBarViewer viewer) {
        _viewer = viewer;
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

        _filterCombo = new JComboBox();
        _filterCombo.addItem("No Filter");
        _filterCombo.addItem(new UnassignedRowFilter());
        _filterCombo.addItem(new AssignedRowFilter());
        // _filterCombo.addItem(new KmEvenFilter());
        _filterCombo.addActionListener(this);
        add(_filterCombo);

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

    class UnassignedRowFilter extends PropertyObservableBase implements TimeBarRowFilter {
        /**
         * {@inheritDoc}
         */
        public boolean isInResult(TimeBarRow row) {
            UmlaufKette kette = (UmlaufKette) row;
            return !kette.isCompletelyAssigned();
        }

        public String toString() {
            return "Contains unassigned";
        }
    }

    class AssignedRowFilter extends PropertyObservableBase implements TimeBarRowFilter {
        /**
         * {@inheritDoc}
         */
        public boolean isInResult(TimeBarRow row) {
            UmlaufKette kette = (UmlaufKette) row;
            return kette.isCompletelyAssigned();
        }

        public String toString() {
            return "Completely Assigned";
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