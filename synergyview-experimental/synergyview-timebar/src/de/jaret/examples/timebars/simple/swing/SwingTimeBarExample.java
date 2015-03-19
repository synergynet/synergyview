/*
 *  File: SwingTimeBarExample.java 
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
package de.jaret.examples.timebars.simple.swing;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * Swing: Very simple test: timebar viewer with lots of intervals and a second
 * thread modifying the intervals.
 * 
 * @author Peter Kliem
 * @version $Id: SwingTimeBarExample.java 425 2007-05-13 13:08:03Z olk $
 */
public class SwingTimeBarExample {
    public static final List _headerList = new ArrayList();

    public static void main(String[] args) {
        JFrame f = new JFrame(SwingTimeBarExample.class.getName());
        f.setSize(300, 330);
        f.getContentPane().setLayout(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        TimeBarModel model = createRandomModel(10, 120, 50);
        TimeBarViewer tbv = new TimeBarViewer(model);

        f.getContentPane().add(tbv, BorderLayout.CENTER);

        f.setVisible(true);

        // model will be changed by the main thread
        startChanging(model);

    }

    /**
     * @param model
     */
    private static void startChanging(TimeBarModel model) {
        long delay = 800;
        for (int r = 0; r < model.getRowCount(); r++) {
            TimeBarRow row = model.getRow(r);
            double sum = getIntervalSum(row);
            DefaultRowHeader header = (DefaultRowHeader) _headerList.get(r);
            header.setLabel("R" + r + "(" + sum + ")");
            System.out.println("Changed header " + r);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int r = 0; r < model.getRowCount(); r++) {
            TimeBarRow row = model.getRow(r);
            Iterator it = row.getIntervals().iterator();
            while (it.hasNext()) {
                Interval interval = (Interval) it.next();
                double minutes = interval.getEnd().diffMinutes(interval.getBegin());
                JaretDate date = interval.getEnd().copy();
                date.backMinutes(minutes / 4);
                interval.setEnd(date);
                double sum = getIntervalSum(row);
                DefaultRowHeader header = (DefaultRowHeader) _headerList.get(r);
                header.setLabel("R" + r + "(" + sum + ")");
                System.out.println("Changed interval " + interval);
                try {
                    Thread.sleep(delay / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static double getIntervalSum(TimeBarRow row) {
        double result = 0;
        Iterator it = row.getIntervals().iterator();
        while (it.hasNext()) {
            Interval interval = (Interval) it.next();
            result += interval.getEnd().diffMinutes(interval.getBegin());
        }

        return result;
    }

    public static TimeBarModel createRandomModel(int rows, int averageLengthInMinutes, int countPerRow) {
        DefaultTimeBarModel model = new DefaultTimeBarModel();

        for (int row = 0; row < rows; row++) {
            DefaultRowHeader header = new DefaultRowHeader("r" + row);
            _headerList.add(header);
            DefaultTimeBarRowModel tbr = new DefaultTimeBarRowModel(header);
            JaretDate date = new JaretDate();
            for (int i = 0; i < countPerRow; i++) {
                IntervalImpl interval = new IntervalImpl();
                int length = averageLengthInMinutes / 2 + (int) (Math.random() * (double) averageLengthInMinutes);
                interval.setBegin(date.copy());
                date.advanceMinutes(length);
                interval.setEnd(date.copy());

                tbr.addInterval(interval);

                int pause = (int) (Math.random() * (double) averageLengthInMinutes / 5);
                date.advanceMinutes(pause);
            }
            model.addRow(tbr);
        }

        System.out.println("Created " + (rows * countPerRow) + " Intervals");

        return model;
    }
}
