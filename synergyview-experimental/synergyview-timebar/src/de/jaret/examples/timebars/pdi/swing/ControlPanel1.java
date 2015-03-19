/*
 *  File: ControlPanel1.java 
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
package de.jaret.examples.timebars.pdi.swing;

import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jaret.examples.timebars.pdi.model.Assignment;
import de.jaret.examples.timebars.pdi.model.PersonenDisposition;
import de.jaret.util.date.Interval;
import de.jaret.util.misc.PropertyObservableBase;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarRowFilter;
import de.jaret.util.ui.timebars.TimeBarRowSorter;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarSelectionListener;
import de.jaret.util.ui.timebars.model.TimeBarSelectionModel;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.dnd.IntervalListTransferable;
import de.jaret.util.ui.timebars.swing.dnd.RowIntervalTuple;
import de.jaret.util.ui.timebars.swing.renderer.DefaultGapRenderer;

/**
 * @author Peter Kliem
 * @version $Id: ControlPanel1.java 426 2007-05-13 15:41:49Z olk $
 */
public class ControlPanel1 extends JPanel implements ChangeListener, ActionListener {
    TimeBarViewer _viewer;
    JSlider _timeScaleSlider;
    JSlider _rowHeigthSlider;
    JComboBox _sorterCombo;
    JComboBox _filterCombo;
    JComboBox _intervalFilterCombo;
    TimeBarMarkerImpl _marker;
    JButton _freisetzenButton;

    public ControlPanel1(TimeBarViewer viewer, TimeBarMarkerImpl marker, boolean hierarchyExampel) {
        _viewer = viewer;
        _marker = marker;
        setLayout(new FlowLayout());
        createControls(hierarchyExampel);
    }

    /**
     * 
     */
    private void createControls(boolean hierarchyExample) {
        _timeScaleSlider = new JSlider(50, 500);
        _timeScaleSlider.addChangeListener(this);
        _timeScaleSlider.setValue((int) (_viewer.getPixelPerSecond() * 60.0 * 60.0 * 24));
        add(_timeScaleSlider);
        _rowHeigthSlider = new JSlider(10, 300);
        _rowHeigthSlider.addChangeListener(this);
        _rowHeigthSlider.setValue(_viewer.getRowHeight());
        add(_rowHeigthSlider);

        if (!hierarchyExample) {
            _sorterCombo = new JComboBox();
            _sorterCombo.addItem("No sorter");
            _sorterCombo.addItem(new NameSorter());
            _sorterCombo.addActionListener(this);
            add(_sorterCombo);

            _freisetzenButton = new JButton(new FreisetzenAction(_viewer.getSelectionModel()));
            _freisetzenButton.setText("Freisetzen");
            add(_freisetzenButton);
            DropTargetListener dtl = new FreisetzenDropTargetListener();
            DropTarget dt = new DropTarget(_freisetzenButton, dtl);

            final JCheckBox gapCheck = new JCheckBox("GapRenderer");
            gapCheck.setSelected(_viewer.getGapRenderer() != null);
            gapCheck.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (gapCheck.isSelected()) {
                        _viewer.setGapRenderer(new DefaultGapRenderer());
                    } else {
                        _viewer.setGapRenderer(null);
                    }
                }
            });
            add(gapCheck);
        }

        final JCheckBox optScrollingCheck = new JCheckBox("Optimize scrolling");
        optScrollingCheck.setSelected(_viewer.getOptimizeScrolling());
        optScrollingCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _viewer.setOptimizeScrolling(optScrollingCheck.isSelected());
            }
        });
        add(optScrollingCheck);

    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == _timeScaleSlider) {
            double pixPerSecond = (double) _timeScaleSlider.getValue() / (24.0 * 60 * 60);
            _viewer.setPixelPerSecond(pixPerSecond);
        } else if (e.getSource() == _rowHeigthSlider) {
            _viewer.setRowHeight(_rowHeigthSlider.getValue());
        }
    }

    class FreisetzenAction extends AbstractAction implements TimeBarSelectionListener {
        TimeBarSelectionModel _selModel;

        public FreisetzenAction(TimeBarSelectionModel selModel) {
            _selModel = selModel;
            _selModel.addTimeBarSelectionListener(this);
            setName("Freisetzen");
        }

        public void actionPerformed(ActionEvent e) {
            List selIntervals = _selModel.getSelectedIntervals();
            Iterator it = selIntervals.iterator();
            while (it.hasNext()) {
                Interval interval = (Interval) it.next();
                if (interval instanceof Assignment) {
                    Assignment v = (Assignment) interval;
                    v.getPersonenDispo().remVerplanung(v);
                }
            }
        }

        public void selectionChanged(TimeBarSelectionModel selectionModel) {
            setEnabled(selectionModel.hasIntervalSelection());
        }

        public void elementAddedToSelection(TimeBarSelectionModel selectionModel, Object element) {
            setEnabled(selectionModel.hasIntervalSelection());
        }

        public void elementRemovedFromSelection(TimeBarSelectionModel selectionModel, Object element) {
            setEnabled(selectionModel.hasIntervalSelection());
        }

    }

    class FreisetzenDropTargetListener extends DropTargetAdapter {

        public void dragOver(DropTargetDragEvent e) {
            if (e.isDataFlavorSupported(IntervalListTransferable.intervalListFlavor)) {
                e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
            }
        }

        public void drop(DropTargetDropEvent e) {
            System.out.println("Freisetzen Drop!");
            try {
                Transferable tr = e.getTransferable();
                DataFlavor[] flavors = tr.getTransferDataFlavors();
                for (int i = 0; i < flavors.length; i++) {
                    if (flavors[i].equals(IntervalListTransferable.intervalListFlavor)) {
                        // e.rejectDrop();
                        e.acceptDrop(e.getDropAction());
                        List l = (List) e.getTransferable()
                                .getTransferData(IntervalListTransferable.intervalListFlavor);
                        List intervals = ((RowIntervalTuple) l.get(0)).getIntervals();
                        Iterator it = intervals.iterator();
                        while (it.hasNext()) {
                            Interval interval = (Interval) it.next();
                            if (interval instanceof Assignment) {
                                Assignment v = (Assignment) interval;
                                v.getPersonenDispo().remVerplanung(v);
                            }
                        }
                        e.dropComplete(true);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            e.rejectDrop();
        }
    }

    class NameSorter extends PropertyObservableBase implements TimeBarRowSorter {
        public int compare(TimeBarRow o1, TimeBarRow o2) {
            PersonenDisposition dp1 = (PersonenDisposition) o1;
            PersonenDisposition dp2 = (PersonenDisposition) o2;
            return dp1.getPerson().getName().compareTo(dp2.getPerson().getName());
        }

        public String toString() {
            return "NameSorter";
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