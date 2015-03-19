/*
 *  File: MilliExample.java 
 *  Copyright (c) 2004-2009  Peter Kliem (Peter.Kliem@jaret.de)
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
package de.jaret.examples.timebars.millis.swt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.jaret.examples.timebars.millis.model.BreakIntervalFilter;
import de.jaret.examples.timebars.millis.model.ModelCreator;
import de.jaret.examples.timebars.millis.swt.renderer.GlobalBreakRenderer;
import de.jaret.examples.timebars.millis.swt.renderer.MilliGrid;
import de.jaret.examples.timebars.millis.swt.renderer.MilliScale;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.date.iterator.DateIterator;
import de.jaret.util.date.iterator.DayIterator;
import de.jaret.util.date.iterator.HourIterator;
import de.jaret.util.date.iterator.IIteratorFormatter;
import de.jaret.util.date.iterator.MillisecondIterator;
import de.jaret.util.date.iterator.MinuteIterator;
import de.jaret.util.date.iterator.MonthIterator;
import de.jaret.util.date.iterator.SecondIterator;
import de.jaret.util.date.iterator.WeekIterator;
import de.jaret.util.date.iterator.YearIterator;
import de.jaret.util.date.iterator.DateIterator.Format;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarMarkerListener;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.mod.DefaultIntervalModificator;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarNode;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.PPSInterval;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.BoxTimeScaleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultTimeScaleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultTitleRenderer;

/**
 * Example showing millisecond accuracy usage.
 * 
 * @author Peter Kliem
 * @version $Id: MilliExample.java 894 2009-11-02 22:29:11Z kliem $
 */
public class MilliExample extends ApplicationWindow {
    private TimeBarViewer _tbv;
    private TimeBarViewer _tbv2;

    public MilliExample() {
        super(null);
    }

    protected Control createContents(Composite parent) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        parent.setLayout(gridLayout);

        // create the model
        ModelCreator creator = new ModelCreator();
        TimeBarModel model = creator.createModel();

        // create timebarviewer
        _tbv = new TimeBarViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        _tbv.setLayoutData(gd);

        
        _tbv.addIntervalModificator(new DefaultIntervalModificator());
        
        // turn on milli mode
        _tbv.setMilliAccuracy(true);

        _tbv.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);
        _tbv.setModel(model);

        _tbv.setPixelPerSecond(6000);
        _tbv.setDrawRowGrid(true);

        // use special grid and timescale renderers
        
        _tbv.setTimeScaleRenderer(new MilliScale());
//        _tbv.setTimeScaleRenderer(new DefaultTimeScaleRenderer() {
//            
//            protected void initIterators() {
//                final NumberFormat NF = NumberFormat.getNumberInstance();
//                NF.setMinimumIntegerDigits(5);
//                _iterators = new ArrayList<DateIterator>();
//                _formats = new ArrayList<DateIterator.Format>();
//
//                DateIterator iterator = new MillisecondIterator(1);
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//                iterator.setFormatter(new IIteratorFormatter() {
//                    public String getLabel(JaretDate date, Format format) {
//                        return NF.format(date.getDate().getTime());
//                    }
//                });
//
//                iterator = new MillisecondIterator(10);
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//                iterator.setFormatter(new IIteratorFormatter() {
//                    public String getLabel(JaretDate date, Format format) {
//                        return NF.format(date.getDate().getTime());
//                    }
//                });
//
//                iterator = new MillisecondIterator(100);
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//                iterator.setFormatter(new IIteratorFormatter() {
//                    public String getLabel(JaretDate date, Format format) {
//                        return NF.format(date.getDate().getTime());
//                    }
//                });
//
//                iterator = new MillisecondIterator(500);
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//                iterator.setFormatter(new IIteratorFormatter() {
//                    public String getLabel(JaretDate date, Format format) {
//                        return NF.format(date.getDate().getTime());
//                    }
//                });
//
//                iterator = new SecondIterator(1);
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//
//                iterator = new SecondIterator(5);
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//
//                iterator = new SecondIterator(30);
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//
//                iterator = new MinuteIterator(1);
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//
//                iterator = new MinuteIterator(10);
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//                _upperMap.put(iterator, new DayIterator(1));
//
//                iterator = new MinuteIterator(30);
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//                _upperMap.put(iterator, new DayIterator(1));
//
//                iterator = new HourIterator(3);
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//                _upperMap.put(iterator, new DayIterator(1));
//
//                iterator = new HourIterator(12);
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//                _upperMap.put(iterator, new DayIterator(1));
//
//                iterator = new DayIterator();
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//
//                iterator = new WeekIterator();
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//                _upperMap.put(iterator, new MonthIterator());
//
//                iterator = new MonthIterator();
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//                _upperMap.put(iterator, new YearIterator());
//
//                iterator = new YearIterator();
//                _iterators.add(iterator);
//                _formats.add(DateIterator.Format.LONG);
//            }
// 
//        });
        
        
        _tbv.setGridRenderer(new MilliGrid());
        
        
//        DefaultTimeScaleRenderer dtscr = new DefaultTimeScaleRenderer();
//        _tbv.setTimeScaleRenderer(dtscr);
//        DefaultGridRenderer dgr = new DefaultGridRenderer();
//        dgr.setTickProvider(dtscr);
//        _tbv.setGridRenderer(dgr);

        
        // configure the title renderer with a background image and set the title
        DefaultTitleRenderer titleRenderer = new DefaultTitleRenderer();
        titleRenderer.setBackgroundRscName("/de/jaret/examples/timebars/hierarchy/swt/titlebg.png");
        _tbv.setTitleRenderer(titleRenderer);
        _tbv.setTitle("Millis");

        // create a control panel for manipulation
        MilliControlPanel ctrl = new MilliControlPanel(parent, SWT.NULL, _tbv, null);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        ctrl.setLayoutData(gd);

        // configure the variable x scale
        // activate variable x scale
        _tbv.setVariableXScale(true);
        DefaultTimeBarNode scaleRow = (DefaultTimeBarNode) _tbv.getPpsRow();

        // create pps intervals
        creator.createPPSIntervals(_tbv.getPixelPerSecond(), scaleRow);

        
        
        // setup for breaks in the timescale
        _tbv.setIntervalFilter(new BreakIntervalFilter(_tbv.getPpsRow()));
        _tbv.setGlobalAssistantRenderer(new GlobalBreakRenderer());
        

        
        
        // create a second timebarviewer controlling the pps intervals
        // create timebarviewer

        DefaultTimeBarModel model2 = new DefaultTimeBarModel();
        model2.addRow(scaleRow);

        _tbv2 = new TimeBarViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        gd = new GridData(GridData.FILL_BOTH);
        _tbv2.setLayoutData(gd);

        // retrieve delegate for experimental api
        TimeBarViewerDelegate delegate2 = (TimeBarViewerDelegate) _tbv2.getData("delegate");
        // turn on milli mode
        delegate2.setMilliAccuracy(true);

        _tbv2.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);
        _tbv2.setModel(model2);

        _tbv2.setPixelPerSecond(1000.0 / 30.0);
        _tbv2.setDrawRowGrid(true);

        _tbv2.setGridRenderer(null); // no grid
        _tbv2.setTimeScaleRenderer(new MilliScale(1000, 10000));

        // allow modifications
        _tbv2.addIntervalModificator(new DefaultIntervalModificator());

        // configure the title renderer with a background image and set the title
        _tbv2.setTitleRenderer(titleRenderer);
        _tbv2.setTitle("Scale");

        // Add a marker that shows the start date of the upper viewer and can be dragged top scroll
        // the upper viewer
        final TimeBarMarker marker = new TimeBarMarkerImpl(true, _tbv.getStartDate().copy());
        _tbv2.addMarker(marker);
        _tbv.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("StartDate")) {
                    marker.setDate(((JaretDate) evt.getNewValue()).copy());
                }
            }
        });

        marker.addTimeBarMarkerListener(new TimeBarMarkerListener() {
            public void markerDescriptionChanged(TimeBarMarker marker, String oldValue, String newValue) {
            }

            public void markerMoved(TimeBarMarker marker, JaretDate oldDate, JaretDate currentDate) {
                _tbv.setStartDate(currentDate.copy());
            }
        });

        return _tbv;
    }


    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(getClass().getName());
        shell.setSize(1200, 800);
    }

    public static void main(String[] args) {
        MilliExample test = new MilliExample();
        test.setBlockOnOpen(true);
        test.open();
    }




}