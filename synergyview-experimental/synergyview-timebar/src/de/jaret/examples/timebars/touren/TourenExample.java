/*
 *  File: TourenExample.java 
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarRowFilter;
import de.jaret.util.ui.timebars.TimeBarRowSorter;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * This example is a proof of concept showing trips from one point to another point ("Touren") in the timebarviewer and
 * shows additional data in an attached JTable. Since newer versiocns of the viewer component scroll smooth - a feature
 * the JTable lacks to have - the integration with th table is not as nice as it could be. For demonstration purposes a
 * sorter and a filter can be chosen from comboboxes. Scaling is possible. Renderer for the tour elements is
 * specialized. A gap renderer is used to render the point labels.
 * 
 * @author Peter Kliem
 * @version $Id: TourenExample.java 275 2007-02-18 21:50:30Z olk $
 */
public class TourenExample {

    public static void main(String[] args) {
        JFrame f = new JFrame(TourenExample.class.getName());
        f.setSize(1000, 600);
        f.getContentPane().setLayout(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultTimeBarModel model = new DefaultTimeBarModel();
        JaretDate base = new JaretDate();
        base.setHours(0);
        base.setMinutes(0);
        for (int d = 0; d < 3; d++) {
            for (int i = 0; i < 330; i++) {
                Tour t = new Tour(d * 1000 + i, base);
                model.addRow(t);
            }
            base.advanceDays(1);
        }

        final TimeBarViewer tbv = new TimeBarViewer(model, false, false);
        tbv.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);
        tbv.setStartDate(new JaretDate());
        tbv.setDrawRowGrid(true);
        tbv.setTimeBarRenderer(new TourenElementRenderer());
        tbv.setPixelPerSecond(800.0 / (24.0 * 60.0 * 60.0));
        tbv.setGapRenderer(new TourenGapRenderer());

        JPanel tablePanel = new JPanel();
        tablePanel.setBackground(Color.YELLOW);
        tablePanel.setLayout(new BorderLayout());

        final TourenTableModel tmodel = new TourenTableModel(model);
        final JTable table = new JTable(tmodel);
        final JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tablePanel.add(scroll, BorderLayout.CENTER);

        JComponent filler = new JLabel();
        int height = tbv.getTimeScaleRenderer().getHeight() - table.getTableHeader().getPreferredSize().height;
        Dimension size = new Dimension(20, height);
        filler.setMinimumSize(size);
        filler.setMaximumSize(size);
        filler.setPreferredSize(size);
        tablePanel.add(filler, BorderLayout.NORTH);

        table.setRowHeight(tbv.getRowHeight());
        tbv.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("RowHeight")) {
                    table.setRowHeight(tbv.getRowHeight());
                    int firstrow = tbv.getFirstRowDisplayed();
                    Rectangle rect = table.getCellRect(firstrow, 0, true);
                    scroll.getVerticalScrollBar().setValue(rect.y);
                } else if (evt.getPropertyName().equals("FirstRow")) {
                    int firstrow = tbv.getFirstRowDisplayed();
                    Rectangle rect = table.getCellRect(firstrow, 0, true);
                    scroll.getVerticalScrollBar().setValue(rect.y);
                } else if (evt.getPropertyName().equals("RowSorter")) {
                    tmodel.setTimeBarRowSorter((TimeBarRowSorter) evt.getNewValue());
                } else if (evt.getPropertyName().equals("RowFilter")) {
                    tmodel.setTimeBarRowFilter((TimeBarRowFilter) evt.getNewValue());
                }
            }

        });

        JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitter.add(tbv);
        splitter.add(tablePanel);
        f.getContentPane().add(splitter, BorderLayout.CENTER);

        // add the control panel
        ControlPanel cp = new ControlPanel(tbv, null);
        f.getContentPane().add(cp, BorderLayout.NORTH);

        f.setVisible(true);

        splitter.setDividerLocation(0.8);

    }

}
