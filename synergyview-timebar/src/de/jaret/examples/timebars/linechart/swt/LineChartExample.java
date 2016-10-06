/*
 *  File: LineChartExample.java 
 *  Copyright (c) 2004-2008  Peter Kliem (Peter.Kliem@jaret.de)
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

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.jaret.examples.timebars.linechart.model.LineChartInterval;
import de.jaret.examples.timebars.linechart.model.ModelCreator;
import de.jaret.examples.timebars.linechart.swt.renderer.LineChartHeaderRenderer;
import de.jaret.examples.timebars.linechart.swt.renderer.LineChartRenderer;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.model.IRowHeightStrategy;
import de.jaret.util.ui.timebars.model.ITimeBarViewState;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.BoxTimeScaleRenderer;
import de.jaret.util.ui.timebars.swt.util.TimeScaleDragSupport;

/**
 * Example showing how to draw a simple line chart with the time bars. This is showing one line chart only, but mixing
 * intervals and line chart intervals is possible.
 * 
 * @author Peter Kliem
 * @version $Id: LineChartExample.java 801 2008-12-27 22:44:54Z kliem $
 */
public class LineChartExample extends ApplicationWindow {
    /** timebar viewer. */
    private static TimeBarViewer _tbv;

    public LineChartExample() {
        super(null);
    }

    protected Control createContents(Composite parent) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.verticalSpacing = 0;
        parent.setLayout(gridLayout);

        TimeBarModel model = ModelCreator.createModel();

        GridData gd = new GridData(GridData.FILL_BOTH);

        // create the time bar viewer
        // horizontal scroll bar only! (since we only assume on erow, height of the
        // row is done by a row height strategy)
        _tbv = new TimeBarViewer(parent, SWT.H_SCROLL);
        _tbv.setLayoutData(gd);

        _tbv.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);
        _tbv.setModel(model);

        // choose a nice scale to begin with
        _tbv.setPixelPerSecond(0.0065);

        // do some configurations on the viewer to make it more suitable for chart rendering

        // no selections
        _tbv.getSelectionModel().setRowSelectionAllowed(false);
        _tbv.getSelectionModel().setIntervalSelectionAllowed(false);
        _tbv.getSelectionModel().setMultipleSelectionAllowed(false); // also disable rect selection

        // setup the y axis
        _tbv.setYAxisWidth(20);
        _tbv.setHeaderRenderer(new LineChartHeaderRenderer());

        // disable grid rendering since it is not needed
        _tbv.setGridRenderer(null);
        
        // allow dragging to replace scrolling
        TimeScaleDragSupport tsds = new TimeScaleDragSupport(_tbv, true);

        // use the box timescale renderer
        _tbv.setTimeScaleRenderer(new BoxTimeScaleRenderer());

        // we will only render one row. This row should always be scaled to match the height of the diagram rectangle.
        // the row height strategy will ensure that
        _tbv.getTimeBarViewState().setUseVariableRowHeights(true);
        _tbv.getTimeBarViewState().setRowHeightStrategy(new IRowHeightStrategy() {

            public int calculateRowHeight(TimeBarViewerDelegate delegate, ITimeBarViewState timeBarViewState,
                    TimeBarRow row) {
                return delegate.getDiagramRect().height;
            }

            public boolean overrideDefault() {
                return true;
            }

        });

        
        // register the renderer for the line chart itself
        _tbv.registerTimeBarRenderer(LineChartInterval.class, new LineChartRenderer());

        // add the control panel for a scale slider
        LineChartControlPanel ctrlPanel = new LineChartControlPanel(parent, SWT.NULL, _tbv);

        return _tbv;
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(getClass().getName());
        shell.setSize(new Point(1000, 600));
    }

    public static void main(String[] args) {
        LineChartExample test = new LineChartExample();
        test.setBlockOnOpen(true);
        test.open();
    }

}