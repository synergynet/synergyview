/*
 *  File: CalendarControlPanel.java 
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
package de.jaret.examples.timebars.calendar.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import de.jaret.examples.timebars.calendar.model.CalendarModel;
import de.jaret.examples.timebars.calendar.model.Day;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.ViewConfiguration;
import de.jaret.util.ui.timebars.model.DefaultTimeBarNode;
import de.jaret.util.ui.timebars.model.IRowHeightStrategy;
import de.jaret.util.ui.timebars.model.ITimeBarViewState;
import de.jaret.util.ui.timebars.model.PPSInterval;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.TimeBarPrinter;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultGapRenderer;

/**
 * Control panel for the calendar example.
 * 
 * @author Peter Kliem
 * @version $Id: CalendarControlPanel.java 874 2009-09-03 20:34:06Z kliem $
 */
public class CalendarControlPanel extends Composite {

    private TimeBarViewer _tbv;
    private TimeBarViewer _tbv2;

    public CalendarControlPanel(Composite arg0, int arg1, TimeBarViewer tbv, TimeBarViewer tbv2) {
        super(arg0, arg1);
        _tbv = tbv;
        _tbv2 = tbv2;
        createControls(this);
    }

    /**
     * @param panel
     */
    private void createControls(CalendarControlPanel panel) {
        panel.setLayout(new RowLayout());

// set secondsDisplayed test
//        final Scale pixPerSecondsScale = new Scale(this, SWT.HORIZONTAL);
//        pixPerSecondsScale.setMaximum(60*60*24);
//        pixPerSecondsScale.setMinimum(60*60*24/2);
//        if (_tbv.getPixelPerSecond() * (24.0 * 60.0 * 60.0) > 700) {
//            pixPerSecondsScale.setMaximum((int) (_tbv.getPixelPerSecond() * (24.0 * 60.0 * 60.0)));
//        }
//        pixPerSecondsScale.addSelectionListener(new SelectionListener() {
//            public void widgetSelected(SelectionEvent ev) {
//                int val = pixPerSecondsScale.getSelection();
//                double pps = ((double) val) / (24.0 * 60.0 * 60.0);
//                System.out.println("scale " + val + "pps " + pps);
//                _tbv.setSecondsDisplayed(val, true);
//                //_tbv.setPixelPerSecond(pps);
//            }
//
//            public void widgetDefaultSelected(SelectionEvent arg0) {
//            }
//        });
//        pixPerSecondsScale.setSelection((int) (_tbv.getPixelPerSecond() * (24.0 * 60.0 * 60.0)));
//        RowData rd = new RowData(800, 40);
//        pixPerSecondsScale.setLayoutData(rd);

        final Scale pixPerSecondsScale = new Scale(this, SWT.HORIZONTAL);
        pixPerSecondsScale.setMaximum(700);
        pixPerSecondsScale.setMinimum(1);
        if (_tbv.getPixelPerSecond() * (24.0 * 60.0 * 60.0) > 700) {
            pixPerSecondsScale.setMaximum((int) (_tbv.getPixelPerSecond() * (24.0 * 60.0 * 60.0)));
        }
        pixPerSecondsScale.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent ev) {
                int val = pixPerSecondsScale.getSelection();
                double pps = ((double) val) / (24.0 * 60.0 * 60.0);
                System.out.println("scale " + val + "pps " + pps);
                _tbv.setPixelPerSecond(pps);
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        pixPerSecondsScale.setSelection((int) (_tbv.getPixelPerSecond() * (24.0 * 60.0 * 60.0)));
        RowData rd = new RowData(800, 40);
        pixPerSecondsScale.setLayoutData(rd);

        final Scale rowHeightScale = new Scale(this, SWT.HORIZONTAL);
        rowHeightScale.setMaximum(300);
        rowHeightScale.setMinimum(5);
        rowHeightScale.setSelection(_tbv.getRowHeight());
        rowHeightScale.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent ev) {
                int val = rowHeightScale.getSelection();
                _tbv.setRowHeight(val);
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        final Button gapCheck = new Button(this, SWT.CHECK);
        gapCheck.setText("GapRenderer");
        gapCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if (gapCheck.getSelection()) {
                    _tbv.setGapRenderer(new DefaultGapRenderer());
                } else {
                    _tbv.setGapRenderer(null);
                }
            }
        });

        Button b = new Button(this, SWT.PUSH);
        b.setText("Print");
        b.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent arg0) {
                print();
            }
        });

        final Button drawRowGridCheck = new Button(this, SWT.CHECK);
        drawRowGridCheck.setText("Draw row grid");
        drawRowGridCheck.setSelection(_tbv.getDrawRowGrid());
        drawRowGridCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.setDrawRowGrid(drawRowGridCheck.getSelection());
            }
        });

        final Button lineDragCheck = new Button(this, SWT.CHECK);
        lineDragCheck.setText("Allow line drag");
        lineDragCheck.setSelection(_tbv.isLineDraggingAllowed());
        lineDragCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.setLineDraggingAllowed(lineDragCheck.getSelection());
            }
        });

        Label l = new Label(this, SWT.NULL);
        l.setText("Time scale position:");

        final CCombo tsPositionCombo = new CCombo(this, SWT.READ_ONLY | SWT.BORDER);
        tsPositionCombo.setItems(new String[] {"Top", "Bottom", "none"});
        int index = 0; // top
        if (_tbv.getTimeScalePosition() == TimeBarViewerInterface.TIMESCALE_POSITION_BOTTOM) {
            index = 1;
        } else if (_tbv.getTimeScalePosition() == TimeBarViewerInterface.TIMESCALE_POSITION_NONE) {
            index = 2;
        }
        tsPositionCombo.select(index);
        tsPositionCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if (tsPositionCombo.getSelectionIndex() == 0) {
                    _tbv.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_TOP);
                } else if (tsPositionCombo.getSelectionIndex() == 1) {
                    _tbv.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_BOTTOM);
                } else if (tsPositionCombo.getSelectionIndex() == 2) {
                    _tbv.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_NONE);
                }
            }
        });

        final Button optScrollingCheck = new Button(this, SWT.CHECK);
        optScrollingCheck.setText("Use optimized scrolling");
        optScrollingCheck.setSelection(_tbv.getOptimizeScrolling());
        optScrollingCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.setOptimizeScrolling(optScrollingCheck.getSelection());
                if (_tbv2 != null) {
                    _tbv2.setOptimizeScrolling(optScrollingCheck.getSelection());
                }
            }
        });

        l = new Label(this, SWT.NULL);
        l.setText("Orientation:");

        final CCombo orientationCombo = new CCombo(this, SWT.READ_ONLY | SWT.BORDER);
        orientationCombo.setItems(new String[] {TimeBarViewerInterface.Orientation.HORIZONTAL.toString(),
                TimeBarViewerInterface.Orientation.VERTICAL.toString()});
        index = 0; // horizontal
        if (_tbv.getOrientation() == TimeBarViewerInterface.Orientation.VERTICAL) {
            index = 1;
        }
        orientationCombo.select(index);
        orientationCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if (orientationCombo.getSelectionIndex() == 0) {
                    _tbv.setOrientation(TimeBarViewerInterface.Orientation.HORIZONTAL);
                } else if (orientationCombo.getSelectionIndex() == 1) {
                    _tbv.setOrientation(TimeBarViewerInterface.Orientation.VERTICAL);
                } else {
                    throw new RuntimeException("illegal");
                }
            }
        });

        l = new Label(this, SWT.NULL);
        l.setText("Header width:");

        final Scale headerWidthScale = new Scale(this, SWT.HORIZONTAL);
        headerWidthScale.setMaximum(300);
        headerWidthScale.setMinimum(0);
        headerWidthScale.setSelection(_tbv.getYAxisWidth());
        headerWidthScale.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent ev) {
                int val = headerWidthScale.getSelection();
                _tbv.setYAxisWidth(val);
                _tbv.redraw();
            }
        });

        final Button varScaleCheck = new Button(this, SWT.CHECK);
        varScaleCheck.setText("Use variable scale");
        varScaleCheck.setSelection(_tbv.hasVariableXScale());
        varScaleCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {

                if (varScaleCheck.getSelection()) {
                    _tbv.setVariableXScale(true);
                    if (_tbv.getPpsRow().getIntervals().size() == 0) {
                        DefaultTimeBarNode scaleRow = (DefaultTimeBarNode) _tbv.getPpsRow();
                        PPSInterval i = new PPSInterval(_tbv.getPixelPerSecond() / 2);
                        i.setBegin(CalendarModel.BASEDATE.copy().setTime(0, 0, 0));
                        i.setEnd(CalendarModel.BASEDATE.copy().setTime(8, 0, 0));
                        scaleRow.addInterval(i);
                        i = new PPSInterval(_tbv.getPixelPerSecond() / 2);
                        i.setBegin(CalendarModel.BASEDATE.copy().setTime(18, 0, 0));
                        i.setEnd(CalendarModel.BASEDATE.copy().setTime(23, 59, 59));
                        scaleRow.addInterval(i);
                    }
                } else {
                    _tbv.setVariableXScale(false);
                }
            }
        });

        final Button noRowScaling = new Button(this, SWT.PUSH);
        noRowScaling.setText("noRowScaling");
        noRowScaling.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.setAutoScaleRows(-1);
            }
        });
        final Button oneRowScaling = new Button(this, SWT.PUSH);
        oneRowScaling.setText("1Day");
        oneRowScaling.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.setAutoScaleRows(1);
            }
        });
        final Button fiveRowScaling = new Button(this, SWT.PUSH);
        fiveRowScaling.setText("5Days");
        fiveRowScaling.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.setAutoScaleRows(5);
            }
        });
        final Button fourteenRowScaling = new Button(this, SWT.PUSH);
        fourteenRowScaling.setText("14Days");
        fourteenRowScaling.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.setAutoScaleRows(14);
            }
        });

        final Button scalingStrategy = new Button(this, SWT.PUSH);
        scalingStrategy.setText("Add strategy for scaling weekends");
        scalingStrategy.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.getTimeBarViewState().setUseVariableRowHeights(true);
                _tbv.setAutoScaleRows(-1);

                _tbv.getTimeBarViewState().setRowHeightStrategy(new IRowHeightStrategy() {

                    public int calculateRowHeight(TimeBarViewerDelegate delegate, ITimeBarViewState timeBarViewState,
                            TimeBarRow row) {
                        Day day = (Day) row;
                        if (day.getDayDate().isWeekendDay()) {
                            return timeBarViewState.getDefaultRowHeight() / 2;
                        } else {
                            return timeBarViewState.getDefaultRowHeight();
                        }
                    }

                    public boolean overrideDefault() {
                        return true;
                    }

                });
            }
        });

    }

    /**
     * Very simple print command.
     */
    public void print() {
        // use a stanard print dialog to choose a printer
        PrintDialog pd = new PrintDialog(Display.getCurrent().getActiveShell());
        PrinterData pdata = pd.open();
        // create the printer device
        Printer printer = new Printer(pdata);

        // create a view configuration for configuring the print
        ViewConfiguration viewConfiguration = new ViewConfiguration();
        // set the seconds per page to be the seconds displayed by the viewer
        viewConfiguration.setSecondsPerPage(_tbv.getEndDate().diffSeconds(_tbv.getStartDate()));
        // start date is the current start on the screen
        // the end date is left null -> printing will print all remaining data
        viewConfiguration.setStartDate(_tbv.getStartDate());
        // set the footer text
        viewConfiguration.setFootLine(_tbv.getTitle());
        // set a name for the viewconfiguration (this will be the name of the printing job)
        viewConfiguration.setName(_tbv.getTitle() + "_print");

        // create the time bar printer instance and initilaize it
        TimeBarPrinter tbp = new TimeBarPrinter(printer);
        tbp.init(_tbv);
        // do the actual print
        tbp.print(viewConfiguration);

        // dispose the time bar printer
        tbp.dispose();
        // dispose the printer device
        printer.dispose();
    }

}
