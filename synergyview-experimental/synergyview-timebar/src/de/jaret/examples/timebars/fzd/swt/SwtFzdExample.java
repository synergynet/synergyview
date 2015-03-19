/*
 *  File: SwtFzdExample.java 
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
package de.jaret.examples.timebars.fzd.swt;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.jaret.examples.timebars.fzd.model.Fahrt;
import de.jaret.examples.timebars.fzd.model.Umlauf;
import de.jaret.examples.timebars.fzd.swing.FzdExample;
import de.jaret.examples.timebars.pdi.swt.SwtControlPanel;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * Fahrt example: SWT version.
 * 
 * @author kliem
 * @version $Id: SwtFzdExample.java 801 2008-12-27 22:44:54Z kliem $
 */
public class SwtFzdExample extends ApplicationWindow {
    static TimeBarViewer tbv;

    public SwtFzdExample() {
        super(null);
    }

    protected Control createContents(Composite parent) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        parent.setLayout(gridLayout);

        // create a model
        FzdExample.createFzdModel(10, 30, 24, 5);
        TimeBarModel model = FzdExample._zuteilungsModel;
        
        // create the viewer
        tbv = new TimeBarViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        // intervals are painted overlapping
        tbv.setDrawOverlapping(true);
        GridData gd = new GridData(GridData.FILL_BOTH);
        tbv.setLayoutData(gd);

        // register renderers for the different intervals
        tbv.registerTimeBarRenderer(Fahrt.class, new FahrtRenderer());
        tbv.registerTimeBarRenderer(Umlauf.class, new UmlaufRenderer());
        // set the model
        tbv.setModel(model);

        SwtControlPanel ctrl = new SwtControlPanel(parent, SWT.NULL, tbv, null);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        ctrl.setLayoutData(gd);

        // marker
        JaretDate date = new JaretDate();
        date.advanceHours(1);
        TimeBarMarker marker = new TimeBarMarkerImpl(true, date);
        tbv.addMarker(marker);

        return tbv;
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(getClass().getName());
    }


    public static void main(String[] args) {
        SwtFzdExample test = new SwtFzdExample();
        test.setBlockOnOpen(true);
        test.open();
    }

}