/*
 *  File: FancyExample.java 
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
package de.jaret.examples.timebars.fancy.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

import de.jaret.examples.timebars.fancy.model.FancyInterval;
import de.jaret.examples.timebars.fancy.model.ModelCreator;
import de.jaret.examples.timebars.fancy.swt.renderer.FancyEventRenderer;
import de.jaret.examples.timebars.fancy.swt.renderer.FancyGlobalRenderer;
import de.jaret.examples.timebars.fancy.swt.renderer.FancyRenderer;
import de.jaret.util.date.Event;
import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.mod.DefaultIntervalModificator;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.FocussedIntervalListener;
import de.jaret.util.ui.timebars.model.IRowHeightStrategy;
import de.jaret.util.ui.timebars.model.ITimeBarViewState;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarSelectionListener;
import de.jaret.util.ui.timebars.model.TimeBarSelectionModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.BoxTimeScaleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultTitleRenderer;
import de.jaret.util.ui.timebars.swt.util.TimeScaleDragSupport;

/**
 * This example will demonstrate some fancy drawing.
 * 
 * @author Peter Kliem
 * @version $Id: FancyExample.java 801 2008-12-27 22:44:54Z kliem $
 */
public class FancyExample extends ApplicationWindow {
    private static TimeBarViewer _tbv;

    public FancyExample() {
        super(null);
    }

    protected Control createContents(Composite parent) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        parent.setLayout(gridLayout);

        TimeBarModel model = ModelCreator.createModel();

        GridData gd = new GridData(GridData.FILL_BOTH);

        _tbv = new TimeBarViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        _tbv.setLayoutData(gd);

        _tbv.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);
        _tbv.setModel(model);
        _tbv.addIntervalModificator(new DefaultIntervalModificator());

        _tbv.setPixelPerSecond(0.05);
        _tbv.setDrawRowGrid(true);

        _tbv.setSelectionDelta(6);

        // configure the title renderer with a background image and set the title
        DefaultTitleRenderer titleRenderer = new DefaultTitleRenderer();
        titleRenderer.setBackgroundRscName("/de/jaret/examples/timebars/hierarchy/swt/titlebg.png");
        _tbv.setTitleRenderer(titleRenderer);
        _tbv.setTitle("Fancy");
        _tbv.setTimeScaleRenderer(new BoxTimeScaleRenderer());

        // register the fancy renderer
        _tbv.registerTimeBarRenderer(FancyInterval.class, new FancyRenderer());
        // register the event renderer
        _tbv.registerTimeBarRenderer(Event.class, new FancyEventRenderer());
        
        
        _tbv.setGlobalAssistantRenderer(new FancyGlobalRenderer());
        
        // enable dragging of the scale
        TimeScaleDragSupport scaleDragger = new TimeScaleDragSupport(_tbv);
        
        
        
        FancyControlPanel ctrl = new FancyControlPanel(parent, SWT.NULL, _tbv);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        ctrl.setLayoutData(gd);

        // add a listener for doubleclicks
        // shows how to get intervals from a point
        _tbv.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                List<Interval> intervals = _tbv.getIntervalsAt(e.x, e.y);
                if (intervals != null && intervals.size() > 0) {
                    for (Interval interval : intervals) {
                        System.out.println("Doubleclicked: " + interval.toString());
                        FancyInterval fi = (FancyInterval)interval;
                        fi.setState(!fi.getState());
                    }
                }
            }
        });


        // additional controls
        Composite addPanel = new Composite(parent, SWT.NULL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        addPanel.setLayoutData(gd);
        addPanel.setLayout(new RowLayout());

        final Button addIntervals = new Button(addPanel, SWT.PUSH);
        addIntervals.setText("Add intervals to selected row");
        addIntervals.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                List<TimeBarRow> selectedRows = _tbv.getSelectionModel().getSelectedRows();
                if (selectedRows.size() == 1) {
                    // create some random intervals and add them at once
                    List<Interval> intervals = new ArrayList<Interval>();
                    for (int i = 0; i < 5; i++) {
                        Interval interval = new IntervalImpl();
                        JaretDate startDate = _tbv.getStartDate().copy().advanceHours(Math.random() * 10);
                        JaretDate endDate = startDate.copy().advanceHours(Math.random() * 5);
                        interval.setBegin(startDate);
                        interval.setEnd(endDate);
                        intervals.add(interval);
                    }
                    // add all intervals at once
                    ((DefaultTimeBarRowModel) selectedRows.get(0)).addIntervals(intervals);
                }
            }
        });

        final Button clearIntervals = new Button(addPanel, SWT.PUSH);
        clearIntervals.setText("Clear selected row");
        clearIntervals.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                List<TimeBarRow> selectedRows = _tbv.getSelectionModel().getSelectedRows();
                if (selectedRows.size() == 1) {
                    // correct selection
                    _tbv.getSelectionModel().remSelectedIntervals(
                            ((DefaultTimeBarRowModel) selectedRows.get(0)).getIntervals());
                    // clear all intervals at once
                    ((DefaultTimeBarRowModel) selectedRows.get(0)).clear();
                }
            }
        });
        final Button remIntervals = new Button(addPanel, SWT.PUSH);
        remIntervals.setText("Remove selected intervals");
        remIntervals.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                List<Interval> selectedIntervals = _tbv.getSelectionModel().getSelectedIntervals();
                if (selectedIntervals.size() > 0) {
                    selectedIntervals = new ArrayList<Interval>(selectedIntervals);
                    // remove from selection
                    _tbv.getSelectionModel().remSelectedIntervals(selectedIntervals);
                    // go through all rows, try to remove the intervals
                    // (This is the hard way ...)
                    for (int i = 0; i < _tbv.getModel().getRowCount(); i++) {
                        ((DefaultTimeBarRowModel) _tbv.getModel().getRow(i)).remIntervals(selectedIntervals);
                    }
                }
            }
        });
        final Button changeIntervals = new Button(addPanel, SWT.PUSH);
        changeIntervals.setText("Change selected intervals");
        changeIntervals.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                List<Interval> selectedIntervals = _tbv.getSelectionModel().getSelectedIntervals();
                if (selectedIntervals.size() > 0) {
                    for (Interval interval : selectedIntervals) {
                        // can be done like this (discouraged): interval.getBegin().advanceHours(1);
                        interval.setBegin(interval.getBegin().copy().advanceHours(1));
                    }
                }
            }
        });

        addIntervals.setEnabled(false);
        clearIntervals.setEnabled(false);
        remIntervals.setEnabled(false);
        changeIntervals.setEnabled(false);
        _tbv.getSelectionModel().addTimeBarSelectionListener(new TimeBarSelectionListener() {

            private void check(TimeBarSelectionModel selectionModel) {
                boolean oneRowSelected = selectionModel.getSelectedRows().size() == 1;
                addIntervals.setEnabled(oneRowSelected);
                clearIntervals.setEnabled(oneRowSelected);

                boolean containsIntervals = selectionModel.getSelectedIntervals().size() > 0;
                remIntervals.setEnabled(containsIntervals);
                changeIntervals.setEnabled(containsIntervals);
            }

            public void elementAddedToSelection(TimeBarSelectionModel selectionModel, Object element) {
                check(selectionModel);
            }

            public void elementRemovedFromSelection(TimeBarSelectionModel selectionModel, Object element) {
                check(selectionModel);
            }

            public void selectionChanged(TimeBarSelectionModel selectionModel) {
                check(selectionModel);
            }

        });

        // Strategy checkbox
        final Button heightStrategyCheck = new Button(addPanel, SWT.CHECK);
        heightStrategyCheck.setText("Height strategy");
        heightStrategyCheck.setSelection(_tbv.getTimeBarViewState().getRowHeightStrategy() != null);
        heightStrategyCheck.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                if (heightStrategyCheck.getSelection()) {
                    _tbv.getTimeBarViewState().setRowHeightStrategy(new IRowHeightStrategy() {
                        public int calculateRowHeight(TimeBarViewerDelegate delegate,
                                ITimeBarViewState timeBarViewState, TimeBarRow row) {
                            int maxOverlap = timeBarViewState.getDefaultRowHeight();
                            int height = delegate.getMaxOverlapCount(row) * maxOverlap;
                            return height;
                        }

                        public boolean overrideDefault() {
                            return true;
                        }
                    });
                } else {
                    _tbv.getTimeBarViewState().setRowHeightStrategy(null);
                }

            }

        });

        // slider for the percentage
        Label l = new Label(addPanel, SWT.NULL);
        l.setText("Percentage:");

        final Scale percentageScale = new Scale(addPanel, SWT.HORIZONTAL);
        percentageScale.setMaximum(100);
        percentageScale.setMinimum(0);
        percentageScale.setEnabled(false);
        percentageScale.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent ev) {
                int val = percentageScale.getSelection();
                FancyInterval interval = (FancyInterval)_tbv.getFocussedInterval();
                if (interval != null) {
                    interval.setPercentage(val);
                }
            }
        });

        // focussed interval listener enabling and disabling the perecentage scale
        _tbv.addFocussedIntervalListener(new FocussedIntervalListener() {
            public void focussedIntervalChanged(Object source, TimeBarRow row, Interval interval) {
                if (interval != null && interval instanceof FancyInterval) {
                    percentageScale.setEnabled(true);
                    percentageScale.setSelection(((FancyInterval)interval).getPercentage());
                } else {
                    percentageScale.setEnabled(false);
                }
            }
            
        });
        
        
        return _tbv;
    }


    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(getClass().getName());
    }

    public static void main(String[] args) {
        FancyExample test = new FancyExample();
        test.setBlockOnOpen(true);
        test.open();
    }



}