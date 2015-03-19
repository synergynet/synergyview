/*
 *  File: SwingCalendarExample.java 
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
package de.jaret.examples.timebars.calendar.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import de.jaret.examples.timebars.calendar.model.Appointment;
import de.jaret.examples.timebars.calendar.model.AppointmentModificator;
import de.jaret.examples.timebars.calendar.model.CalendarModel;
import de.jaret.examples.timebars.calendar.model.ModelCreator;
import de.jaret.examples.timebars.calendar.swing.renderer.AppointmentRenderer;
import de.jaret.examples.timebars.calendar.swing.renderer.CalendarGridRenderer;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.mod.DefaultIntervalModificator;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.OldDefaultTimeScaleRenderer;

/**
 * Swing: the swing version of the calendar example (without drag and drop).
 * 
 * @author Peter Kliem
 * @version $Id: SwingCalendarExample.java 841 2009-02-17 21:17:42Z kliem $
 */
public class SwingCalendarExample {
    static TimeBarViewer _tbv;
    
    public static void main(String[] args) {
        JFrame f = new JFrame(SwingCalendarExample.class.getName());
        f.setSize(800, 500);
        f.getContentPane().setLayout(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        TimeBarModel model = ModelCreator.createCalendarModel();;
        _tbv = new TimeBarViewer(model);

        _tbv.addIntervalModificator(new DefaultIntervalModificator());

        _tbv.setPixelPerSecond(0.018);
        _tbv.setDrawRowGrid(true);

        _tbv.setSelectionDelta(6);
        // this is the col width!
        _tbv.setRowHeight(150);
        
        _tbv.setOrientation(TimeBarViewerInterface.Orientation.VERTICAL);
        // vertical: the y axiswidth is the height of the row headers!
        _tbv.setYAxisWidth(20);
        
        // do not adjust the displayed time according to the model 
        // use the basedate day!
        _tbv.setAdjustMinMaxDatesByModel(false);
        _tbv.setMinDate(CalendarModel.BASEDATE.copy());
        _tbv.setMaxDate(CalendarModel.BASEDATE.copy().advanceDays(1));

        _tbv.setDrawOverlapping(false);
        _tbv.setSelectionDelta(6);
        _tbv.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_TOP);

        // modifications are restricted
        _tbv.addIntervalModificator(new AppointmentModificator());

        _tbv.setTimeScaleRenderer(new OldDefaultTimeScaleRenderer());
        
        _tbv.setGridRenderer(new CalendarGridRenderer());
        
        _tbv.registerTimeBarRenderer(Appointment.class, new AppointmentRenderer());
        
        
        f.getContentPane().add(_tbv, BorderLayout.CENTER);

        f.getContentPane().add(new CalendarControlPanel(_tbv), BorderLayout.SOUTH);
        
        f.setVisible(true);


    }
    public static TimeBarModel createModel() {
        DefaultTimeBarModel model = new DefaultTimeBarModel();

        JaretDate date = new JaretDate();

        int length = 120;

        DefaultRowHeader header = new DefaultRowHeader("r1");
        DefaultTimeBarRowModel tbr = new DefaultTimeBarRowModel(header);
        IntervalImpl interval = new IntervalImpl();
        interval.setBegin(date.copy());
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new IntervalImpl();
        interval.setBegin(date.copy().advanceMinutes(30));
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new IntervalImpl();
        interval.setBegin(date.copy().advanceMinutes(60));
        interval.setEnd(interval.getBegin().copy().advanceMinutes(length));
        tbr.addInterval(interval);

        model.addRow(tbr);

        header = new DefaultRowHeader("r2");
        tbr = new DefaultTimeBarRowModel(header);
        interval = new IntervalImpl();
        interval.setBegin(date.copy());
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new IntervalImpl();
        interval.setBegin(date.copy().advanceMinutes(120));
        interval.setEnd(date.copy().advanceMinutes(length + length));
        tbr.addInterval(interval);

        model.addRow(tbr);

        header = new DefaultRowHeader("r3");
        tbr = new DefaultTimeBarRowModel(header);
        interval = new IntervalImpl();
        interval.setBegin(date.copy());
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new IntervalImpl();
        interval.setBegin(date.copy().advanceMinutes(30));
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new IntervalImpl();
        interval.setBegin(date.copy().advanceMinutes(60));
        interval.setEnd(interval.getBegin().copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new IntervalImpl();
        interval.setBegin(date.copy().advanceMinutes(90));
        interval.setEnd(interval.getBegin().copy().advanceMinutes(length));
        tbr.addInterval(interval);

        model.addRow(tbr);

        // add some empty rows for drag&drop fun
        header = new DefaultRowHeader("r4");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        header = new DefaultRowHeader("r5");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        header = new DefaultRowHeader("r6");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        header = new DefaultRowHeader("r7");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        header = new DefaultRowHeader("r8");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        header = new DefaultRowHeader("r9");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        return model;
    }
}
