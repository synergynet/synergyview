/*
 *  File: FzdTest.java 
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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import de.jaret.examples.timebars.fzd.ctrl.FzdOperations;
import de.jaret.examples.timebars.fzd.model.Fahrt;
import de.jaret.examples.timebars.fzd.model.FahrtenTableModel;
import de.jaret.examples.timebars.fzd.model.Fahrzeug;
import de.jaret.examples.timebars.fzd.model.Umlauf;
import de.jaret.examples.timebars.fzd.model.UmlaufKette;
import de.jaret.examples.timebars.fzd.model.UmlaufKettenModel;
import de.jaret.examples.timebars.fzd.model.ZuteilungsModel;
import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarViewerSynchronizer;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * @author Peter Kliem
 * @version $Id: FzdExample.java 791 2008-12-11 23:22:57Z kliem $
 */
public class FzdExample {
    public static UmlaufKettenModel _ukettenModel;
    public static ZuteilungsModel _zuteilungsModel;

    public static void main(String[] args) {
        JFrame f = new JFrame("FZD Test");
        f.setSize(800, 600);
        f.getContentPane().setLayout(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // fahrzeuge
        TimeBarModel model = createFzdModel(10, 30, 24, 5);
        TimeBarViewer tbv = new TimeBarViewer(model, true, false);
        tbv.setDrawOverlapping(true);
        tbv.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_BOTTOM);

        // register renderers for the intervals
        tbv.registerTimeBarRenderer(Fahrt.class, new FahrtRenderer());
        tbv.registerTimeBarRenderer(Umlauf.class, new UmlaufRenderer());
        // header renderer
        tbv.setHeaderRenderer(new FahrzeugInfoHeaderRenderer());
        // umlaufketten
        // TimeBarViewer tbv2 = new TimeBarViewer(model);
        TimeBarViewer tbv2 = new TimeBarViewer(_ukettenModel);
        tbv2.setDrawOverlapping(true);
        // register renderers for the intervals
        tbv2.registerTimeBarRenderer(Fahrt.class, new FahrtRenderer());
        tbv2.registerTimeBarRenderer(Umlauf.class, new UmlaufRenderer());

        tbv2.setYAxisWidth(tbv.getYAxisWidth());

        // synchronize the TimeBarViewers by a synchronizer
        TimeBarViewerSynchronizer synchronizer = new TimeBarViewerSynchronizer(false, true, true);
        synchronizer.addViewer(tbv);
        synchronizer.addViewer(tbv2);

        JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitter.add(tbv);
        splitter.add(tbv2);
        f.getContentPane().add(splitter, BorderLayout.CENTER);

        // marker
        JaretDate date = new JaretDate();
        date.advanceHours(1);
        TimeBarMarkerImpl marker = new TimeBarMarkerImpl(true, date);
        tbv.addMarker(marker);
        tbv2.addMarker(marker);

        // now marker (marker will move with the tie)
        // final TimeBarNowMarker nowMarker;
        // nowMarker = new TimeBarNowMarker();
        // tbv.addMarker(nowMarker);

        // add the control panel fahrzeuge
        ControlPanel cp = new ControlPanel(tbv, marker);
        f.getContentPane().add(cp, BorderLayout.NORTH);
        // add the control panel ketten
        ControlPanel2 cp2 = new ControlPanel2(tbv2);
        f.getContentPane().add(cp2, BorderLayout.SOUTH);

        f.setVisible(true);

        splitter.setDividerLocation(0.5);

        // Action : brutal eingehängt!
        FzdExample x = new FzdExample();
        Action action = x.new AssignAction(_ukettenModel, tbv, tbv2);
        final JPopupMenu pop = new JPopupMenu("Operations");
        pop.add(action);
        tbv.registerPopupMenu(Interval.class, pop);
        // additional table
        final JFrame tableFrame = new JFrame("Current fahrten");
        tableFrame.setBounds(200, 200, 200, 200);
        tableFrame.getContentPane().setLayout(new BorderLayout());
        FahrtenTableModel tmodel = new FahrtenTableModel((ZuteilungsModel) model, marker);
        JTable table = new JTable(tmodel);
        JScrollPane scroll = new JScrollPane(table);
        tableFrame.getContentPane().add(scroll, BorderLayout.CENTER);
        tableFrame.setVisible(true);

        // dispose timebar now marker
        // f.addWindowListener(new WindowAdapter() {
        // public void windowClosed(WindowEvent e) {
        // tableFrame.dispose();
        // if (nowMarker != null) {
        // nowMarker.stop();
        // }
        // }
        // });

    }

    public static ZuteilungsModel createFzdModel(int fahrzeugAnzahl, int averageLengthInMinutes, int countPerUmlauf,
            int umlaufCount) {
        ZuteilungsModel model = new ZuteilungsModel();
        _ukettenModel = new UmlaufKettenModel();
        int row;
        for (row = 0; row < fahrzeugAnzahl; row++) {
            Fahrzeug fahrzeug = new Fahrzeug("4230" + row);
            UmlaufKette kette = new UmlaufKette("kette " + row);
            int km = (int) (Math.random() * 1000);
            fahrzeug.getFahrzeugInfo().setKilometer(km);
            JaretDate date = new JaretDate();
            for (int i = 0; i < umlaufCount; i++) {
                Umlauf umlauf = new Umlauf("U-" + row + "-" + i);
                for (int f = 0; f < countPerUmlauf; f++) {
                    Fahrt fahrt = new Fahrt("MSTH", "MSTH", "F-" + umlauf.getUmlaufbezeichnug() + ":" + f);
                    int length = averageLengthInMinutes / 2 + (int) (Math.random() * (double) averageLengthInMinutes);
                    fahrt.setBegin(date.copy());
                    date.advanceMinutes(length);
                    fahrt.setEnd(date.copy());

                    umlauf.addFahrt(fahrt);
                    fahrt.setUmlauf(umlauf);

                    int pause = (int) (Math.random() * (double) averageLengthInMinutes / 5);
                    date.advanceMinutes(pause);
                }
                fahrzeug.addUmlauf(umlauf);
                kette.addUmlauf(umlauf);
                date.advanceMinutes(120);
            }
            model.addFahrzeug(fahrzeug);
            _ukettenModel.addUmlaufKette(kette);
        }
        // leere Fahrzeuge
        int addFahrzeuge = 5;
        for (; row < fahrzeugAnzahl + addFahrzeuge; row++) {
            Fahrzeug fahrzeug = new Fahrzeug("4230" + row);
            int km = (int) (Math.random() * 1000);
            fahrzeug.getFahrzeugInfo().setKilometer(km);
            model.addFahrzeug(fahrzeug);
        }
        // nicht zugeteilte Umläufe und Ketten
        int additionalKetten = 5;
        for (row = 0; row < additionalKetten; row++) {
            UmlaufKette kette = new UmlaufKette("Zuskette " + row);
            JaretDate date = new JaretDate();
            for (int i = 0; i < umlaufCount; i++) {
                Umlauf umlauf = new Umlauf("U-" + row + "-" + i);
                for (int f = 0; f < countPerUmlauf; f++) {
                    Fahrt fahrt = new Fahrt("MSTH", "MSTH", "F-" + umlauf.getUmlaufbezeichnug() + ":" + f);
                    int length = averageLengthInMinutes / 2 + (int) (Math.random() * (double) averageLengthInMinutes);
                    fahrt.setBegin(date.copy());
                    date.advanceMinutes(length);
                    fahrt.setEnd(date.copy());

                    umlauf.addFahrt(fahrt);
                    fahrt.setUmlauf(umlauf);

                    int pause = (int) (Math.random() * (double) averageLengthInMinutes / 5);
                    date.advanceMinutes(pause);
                }
                kette.addUmlauf(umlauf);
                date.advanceMinutes(120);
            }
            _ukettenModel.addUmlaufKette(kette);
        }

        System.out.println("Created " + ((fahrzeugAnzahl + additionalKetten) * countPerUmlauf * umlaufCount)
                + " Intervals");
        _zuteilungsModel = model;
        return model;
    }

    class AssignAction extends AbstractAction {
        UmlaufKettenModel _kettenModel;
        TimeBarViewer _fahrzeugViewer;
        TimeBarViewer _kettenViewer;

        /**
         * @param kettenModel
         * @param fahrzeugViewer
         * @param kettenViewer
         */
        public AssignAction(UmlaufKettenModel kettenModel, TimeBarViewer fahrzeugViewer, TimeBarViewer kettenViewer) {
            _kettenModel = kettenModel;
            _fahrzeugViewer = fahrzeugViewer;
            _kettenViewer = kettenViewer;
            putValue(NAME, "Assign vehicle");
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            Umlauf u;
            if (_kettenViewer.getSelectionModel().getSelectedIntervals().size() == 1) {
                Interval i = (Interval) _kettenViewer.getSelectionModel().getSelectedIntervals().get(0);
                if (!(i instanceof Umlauf)) {
                    System.out.println("need exactly one selected umlauf");
                    return;
                } else {
                    u = (Umlauf) i;
                }
            } else {
                System.out.println("need exactly one selected umlauf");
                return;
            }
            UmlaufKette kette = _kettenModel.getKetteForUmlauf(u);
            if (kette == null) {
                throw new RuntimeException("Kette not found");
            }

            if (_fahrzeugViewer.getSelectionModel().getSelectedRows().size() != 1) {
                System.out.println("need exactly one selected fahrzeug");
                return;
            }
            Fahrzeug f = (Fahrzeug) _fahrzeugViewer.getSelectionModel().getSelectedRows().get(0);
            FzdOperations.assign(_kettenModel, kette, u, f);
        }

    }

}
