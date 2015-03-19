/*
 *  File: FancyControlPanel.java 
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


import java.util.List;
import java.util.Locale;

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

import de.jaret.examples.timebars.fancy.swt.renderer.FancyGlobalRenderer;
import de.jaret.examples.timebars.fancy.swt.renderer.FancyRenderer;
import de.jaret.util.date.Interval;
import de.jaret.util.date.holidayenumerator.HolidayEnumeratorFactory;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.ViewConfiguration;
import de.jaret.util.ui.timebars.swt.TimeBarPrinter;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.BoxTimeScaleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultGapRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultTimeScaleRenderer;

/**
 * Control panel for the fancy example.
 * 
 * @author Peter Kliem
 * @version $Id: FancyControlPanel.java 801 2008-12-27 22:44:54Z kliem $
 */
public class FancyControlPanel extends Composite {

    private TimeBarViewer _tbv;
    

    public FancyControlPanel(Composite arg0, int arg1, TimeBarViewer tbv) {
        super(arg0, arg1);
        _tbv = tbv;
        createControls(this);
    }

    /**
     * @param panel
     */
    private void createControls(FancyControlPanel panel) {
        panel.setLayout(new RowLayout());

        // check for correct initialization
        if (_tbv == null) {
            return;
        }
        
        if (_tbv.getPixelPerSecond() < 300) {
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
        } else {
            // this is for the millisecond example
            final Scale pixPerSecondsScale = new Scale(this, SWT.HORIZONTAL);
            pixPerSecondsScale.setMaximum(8000);
            pixPerSecondsScale.setMinimum(300);
            pixPerSecondsScale.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent ev) {
                    int val = pixPerSecondsScale.getSelection();
                    double pps = (double) val ;
                    System.out.println("scale " + val + "pps " + pps);
                    _tbv.setPixelPerSecond(pps);
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });
            pixPerSecondsScale.setSelection((int) _tbv.getPixelPerSecond());
            RowData rd = new RowData(800, 40);
            pixPerSecondsScale.setLayoutData(rd);
            
        }
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

        final Button drawOverlappingCheck = new Button(this, SWT.CHECK);
        drawOverlappingCheck.setText("Draw overlapping");
        drawOverlappingCheck.setSelection(_tbv.getDrawOverlapping());
        drawOverlappingCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.setDrawOverlapping(drawOverlappingCheck.getSelection());
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

        final Button boxTSCheck = new Button(this, SWT.CHECK);
        boxTSCheck.setText("Use BoxTimeScaleRenderer");
        boxTSCheck.setSelection(_tbv.getTimeScaleRenderer() instanceof BoxTimeScaleRenderer);
        boxTSCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if (boxTSCheck.getSelection()) {
                    _tbv.setTimeScaleRenderer(new BoxTimeScaleRenderer());
                } else {
                    DefaultTimeScaleRenderer scaleRenderer = new DefaultTimeScaleRenderer();
                    scaleRenderer.setHolidayEnumerator(HolidayEnumeratorFactory.getHolidayEnumeratorInstance(Locale.getDefault(),
                            "NRW"));
                    _tbv.setTimeScaleRenderer(scaleRenderer);
                }
            }
        });
        
        final Button optScrollingCheck = new Button(this, SWT.CHECK);
        optScrollingCheck.setText("Use optimized scrolling");
        optScrollingCheck.setSelection(_tbv.getOptimizeScrolling());
        optScrollingCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.setOptimizeScrolling(optScrollingCheck.getSelection());
            }
        });

        
        
        l = new Label(this, SWT.NULL);
        l.setText("Orientation:");

        final CCombo orientationCombo = new CCombo(this, SWT.READ_ONLY | SWT.BORDER);
        orientationCombo.setItems(new String[] {TimeBarViewerInterface.Orientation.HORIZONTAL.toString(), TimeBarViewerInterface.Orientation.VERTICAL.toString()});
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

        // var row heights
        final Button varHeightsCheck = new Button(this, SWT.CHECK);
        varHeightsCheck.setText("Var heights/widths");
        varHeightsCheck.setSelection(_tbv.getTimeBarViewState().getUseVariableRowHeights());
        varHeightsCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.getTimeBarViewState().setUseVariableRowHeights(varHeightsCheck.getSelection());
            }
        });
        // allow height drag
        final Button heightDragCheck = new Button(this, SWT.CHECK);
        heightDragCheck.setText("Drag heights/widths");
        heightDragCheck.setSelection(_tbv.isRowHeightDragginAllowed());
        heightDragCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.setRowHeightDraggingAllowed(heightDragCheck.getSelection());
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
        
        createFanyControls();
        
    }

    private void createFanyControls() {
        Label l = new Label(this, SWT.NULL);
        l.setText("Rounding:");

        final Scale roundingScale = new Scale(this, SWT.HORIZONTAL);
        roundingScale.setMaximum(30);
        roundingScale.setMinimum(0);
        roundingScale.setSelection(FancyRenderer.getRounding());
        roundingScale.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent ev) {
                int val = roundingScale.getSelection();
                FancyRenderer.setRounding(val);
                _tbv.redraw();
            }
        });

        l = new Label(this, SWT.NULL);
        l.setText("Effects:");

        final CCombo effectsCombo = new CCombo(this, SWT.READ_ONLY | SWT.BORDER);
        effectsCombo.setItems(new String[] {"none", "drop shadow", "reflection"});
        effectsCombo.select(FancyRenderer.getDrawMode());
        effectsCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                FancyRenderer.setDrawMode(effectsCombo.getSelectionIndex());
                _tbv.redraw();
            }
        });
        
        Button b = new Button(this, SWT.PUSH);
        b.setText("Special mark selected");
        b.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                List<Interval> selected = _tbv.getSelectionModel().getSelectedIntervals();
                ((FancyGlobalRenderer)_tbv.getGlobalAssistantRenderer()).setIntervalsToMark(selected);
                _tbv.redraw();
            }
        });
        
        // history painting
        final Button historyCheck = new Button(this, SWT.CHECK);
        historyCheck.setText("draw history");
        historyCheck.setSelection(((FancyGlobalRenderer)_tbv.getGlobalAssistantRenderer()).isDrawHistory());
        historyCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                ((FancyGlobalRenderer)_tbv.getGlobalAssistantRenderer()).setDrawHistory(historyCheck.getSelection());
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
        viewConfiguration.setStartDate(_tbv.getStartDate().copy());
        // set the footer text
        viewConfiguration.setFootLine(_tbv.getTitle());
        // set a name for the viewconfiguration (this will be the name of the printing job)
        viewConfiguration.setName(_tbv.getTitle()+"_print");
        
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
