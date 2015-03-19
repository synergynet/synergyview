/*
 *  File: SwtPdiExample.java 
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
package de.jaret.examples.timebars.pdi.swt;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.jaret.examples.timebars.pdi.swing.PdiExample;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarViewerSynchronizer;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

public class SwtPdiExample extends ApplicationWindow {
    static TimeBarViewer tbv;
    public static TimeBarViewerSynchronizer tbvSync;
    
    public SwtPdiExample() {
        super(null);
    }

    protected Control createContents(Composite parent) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        parent.setLayout(gridLayout);

        // create the models to use
//        PdiExample.createModels(200, 40, 480); // REALLY large model
        PdiExample.createModels(20, 40, 240); // medium model
//        PdiExample.createModels(5, 400, 5); // medium model
//        PdiExample.createModels(20, 10, 20); // small model

        TimeBarModel model = PdiExample._dispoModel;

        SashForm sash = new SashForm(parent, SWT.VERTICAL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        sash.setLayoutData(gd);

        tbv = new TimeBarViewer(sash, SWT.H_SCROLL | SWT.V_SCROLL);

        tbv.setTimeBarRenderer(new de.jaret.examples.timebars.pdi.swt.PdiRenderer());
        tbv.setGridRenderer(new PdiGridRenderer(PdiExample._pdiCalendar));
        tbv.setHeaderRenderer(new PdiHeaderRenderer());
        tbv.setTimeScaleRenderer(new PdiTimeScaleRenderer(PdiExample._pdiCalendar));
        tbv.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);
        tbv.setModel(model);
        tbv.setName("upper");
        tbv.setTitle("PdiExample");
        
        tbv.setPixelPerSecond(0.00178);
        
        TimeBarViewer tbv2 = new TimeBarViewer(sash, SWT.H_SCROLL | SWT.V_SCROLL);
        tbv2.setModel(PdiExample._dutyMass.getTimeBarModel(PdiExample._pdiCalendar));
        tbv2.setGridRenderer(new PdiGridRenderer(PdiExample._pdiCalendar));
        tbv2.setTimeBarRenderer(new de.jaret.examples.timebars.pdi.swt.PdiRenderer());
        tbv2.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_NONE);
        tbv2.setAdjustMinMaxDatesByModel(false);
        tbv2.setMinDate(tbv.getMinDate());
        tbv2.setMaxDate(tbv.getMaxDate());
        tbv2.setName("lower");
        tbv2.setPixelPerSecond(0.00178);

        tbvSync = new TimeBarViewerSynchronizer(true, true, true);
        tbvSync.addViewer(tbv);
        tbvSync.addViewer(tbv2);

        SwtControlPanel ctrl = new SwtControlPanel(parent, SWT.NULL, tbv, tbv2);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        ctrl.setLayoutData(gd);

        return tbv;
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(getClass().getName());
    }

    public static void main(String[] args) {
        SwtPdiExample test = new SwtPdiExample();
        test.setBlockOnOpen(true);
        test.open();

    }

    public static TimeBarModel createRandomModel(int rows, int averageLengthInMinutes, int countPerRow) {
        DefaultTimeBarModel model = new DefaultTimeBarModel();

        for (int row = 0; row < rows; row++) {
            DefaultRowHeader header = new DefaultRowHeader("r" + row);
            // _headerList.add(header);
            DefaultTimeBarRowModel tbr = new DefaultTimeBarRowModel(header);
            JaretDate date = new JaretDate();
            for (int i = 0; i < countPerRow; i++) {
                IntervalImpl interval = new IntervalImpl();
                int length = averageLengthInMinutes / 2 + (int) (Math.random() * (double) averageLengthInMinutes);
                interval.setBegin(date.copy());
                date.advanceMinutes(length);
                interval.setEnd(date.copy());

                tbr.addInterval(interval);

                int pause = (int) (Math.random() * (double) averageLengthInMinutes);
                date.advanceMinutes(pause);
            }
            model.addRow(tbr);
        }

        System.out.println("Created " + (rows * countPerRow) + " Intervals");

        return model;
    }

}