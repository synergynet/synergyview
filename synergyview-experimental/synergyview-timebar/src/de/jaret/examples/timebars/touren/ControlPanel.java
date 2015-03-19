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
package de.jaret.examples.timebars.touren;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jaret.util.misc.PropertyObservableBase;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarRowFilter;
import de.jaret.util.ui.timebars.TimeBarRowSorter;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * @author Peter Kliem
 * @version $Id: ControlPanel.java 234 2007-02-10 00:22:45Z olk $
 */
public class ControlPanel extends JPanel implements ChangeListener, ActionListener {
    TimeBarViewer _viewer;
    JSlider _timeScaleSlider;
    JSlider _rowHeigthSlider;
    JComboBox _sorterCombo;
    JComboBox _filterCombo;
    JComboBox _intervalFilterCombo;
    TimeBarMarkerImpl _marker;
    JCheckBox _gapCheck;
    JButton _freisetzenButton;

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
        _timeScaleSlider = new JSlider(50, 5000);
        _timeScaleSlider.addChangeListener(this);
        _timeScaleSlider.setValue((int) (_viewer.getPixelPerSecond() * 60.0 * 60.0 * 24));
        _timeScaleSlider.setPreferredSize(new Dimension(300, 50));
        add(_timeScaleSlider);
        _rowHeigthSlider = new JSlider(10, 300);
        _rowHeigthSlider.addChangeListener(this);
        _rowHeigthSlider.setValue(_viewer.getRowHeight());
        add(_rowHeigthSlider);

        _sorterCombo = new JComboBox();
        _sorterCombo.addItem("No sorter");
        _sorterCombo.addItem(new ElementCountSorter());
        _sorterCombo.addActionListener(this);
        add(_sorterCombo);

        _filterCombo = new JComboBox();
        _filterCombo.addItem("No Filter");
        _filterCombo.addItem(new ElementGT20Filter());
        _filterCombo.addActionListener(this);
        add(_filterCombo);
        /*
         * _intervalFilterCombo = new JComboBox(); _intervalFilterCombo.addItem("No Interval Filter");
         * _intervalFilterCombo.addItem(new UmlaufFilter()); _intervalFilterCombo.addItem(new FahrtFilter());
         * _intervalFilterCombo.addActionListener(this); add(_intervalFilterCombo);
         */

        /*
         * _freisetzenButton = new JButton(new FreisetzenAction(_viewer.getDelegate().getSelectionModel()));
         * _freisetzenButton.setText("Freisetzen"); add(_freisetzenButton); DropTargetListener dtl = new
         * FreisetzenDropTargetListener(); DropTarget dt = new DropTarget(_freisetzenButton, dtl);
         * 
         * _gapCheck = new JCheckBox("GapRenderer"); _gapCheck.setSelected(_viewer.getGapRenderer()!=null);
         * _gapCheck.addActionListener(this); add(_gapCheck);
         */
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == _timeScaleSlider) {
            double pixPerSecond = (double) _timeScaleSlider.getValue() / (24.0 * 60 * 60);
            _viewer.setPixelPerSecond(pixPerSecond);
        } else if (e.getSource() == _rowHeigthSlider) {
            _viewer.setRowHeight(_rowHeigthSlider.getValue());
        }
    }

    class ElementCountSorter extends PropertyObservableBase implements TimeBarRowSorter {
        /*
         * (non-Javadoc)
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(TimeBarRow o1, TimeBarRow o2) {
            Tour t1 = (Tour) o1;
            Tour t2 = (Tour) o2;
            return t2.getIntervals().size() - t1.getIntervals().size();
        }

        public String toString() {
            return "ElementCountSorter";
        }
    }

    class ElementGT20Filter extends PropertyObservableBase implements TimeBarRowFilter {
        public boolean isInResult(TimeBarRow row) {
            Tour t = (Tour) row;
            return t.getIntervals().size() > 15;
        }

        public String toString() {
            return "#Elements > 15";
        }
    }

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
        }
    }

}