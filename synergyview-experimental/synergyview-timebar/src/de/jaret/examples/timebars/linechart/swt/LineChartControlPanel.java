/*
 *  File: LineChartControlPanel.java 
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
package de.jaret.examples.timebars.linechart.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;

import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * Control panel for the line chart example.
 * 
 * @author Peter Kliem
 * @version $Id: LineChartControlPanel.java 766 2008-05-28 21:36:48Z kliem $
 */
public class LineChartControlPanel extends Composite {

    private TimeBarViewer _tbv;

    public LineChartControlPanel(Composite parent, int style, TimeBarViewer tbv) {
        super(parent, style);
        _tbv = tbv;
        createControls(this);
    }

    /**
     * @param panel
     */
    private void createControls(LineChartControlPanel panel) {
        panel.setLayout(new RowLayout());

        final Scale pixPerSecondsScale = new Scale(this, SWT.HORIZONTAL);
        pixPerSecondsScale.setMaximum(700);
        pixPerSecondsScale.setMinimum(1);
        if (_tbv.getPixelPerSecond() * (24.0 * 60.0 * 60.0) > 700) {
            pixPerSecondsScale.setMaximum((int) (_tbv.getPixelPerSecond() * (24.0 * 60.0 * 60.0)));
        }
        pixPerSecondsScale.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent ev) {
                int val = pixPerSecondsScale.getSelection();
                double pps = ((double) val) / (24.0 * 60.0 * 60.0);
                System.out.println("scale " + val + "pps " + pps);
                _tbv.setPixelPerSecond(pps);
            }

        });
        pixPerSecondsScale.setSelection((int) (_tbv.getPixelPerSecond() * (24.0 * 60.0 * 60.0)));
        RowData rd = new RowData(800, 40);
        pixPerSecondsScale.setLayoutData(rd);
        
        final Button optScrollingCheck = new Button(this, SWT.CHECK);
        optScrollingCheck.setText("Use optimized scrolling");
        optScrollingCheck.setSelection(_tbv.getOptimizeScrolling());
        optScrollingCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.setOptimizeScrolling(optScrollingCheck.getSelection());
            }
        });

        
    }
}
