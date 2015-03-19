/*
 *  File: SwtOverlapExample.java 
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
package de.jaret.examples.timebars.simple.swt;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.jaret.examples.timebars.pdi.swt.SwtControlPanel;
import de.jaret.examples.timebars.simple.OtherIntervalImpl;
import de.jaret.examples.timebars.simple.model.ModelCreator;
import de.jaret.examples.timebars.simple.swt.renderer.OtherIntervalRenderer;
import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.mod.DefaultIntervalModificator;
import de.jaret.util.ui.timebars.mod.IIntervalModificator;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.IRowHeightStrategy;
import de.jaret.util.ui.timebars.model.ITimeBarChangeListener;
import de.jaret.util.ui.timebars.model.ITimeBarViewState;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarSelectionListener;
import de.jaret.util.ui.timebars.model.TimeBarSelectionModel;
import de.jaret.util.ui.timebars.strategy.DefaultOverlapStrategy;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.BoxTimeScaleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultGridRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultTitleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;

/**
 * SWT: example demonstrating the overlap detection and adapted painting of intervals. Also contains an example of using
 * Drag&Drop with the TimeBarViewer.
 * 
 * @author Peter Kliem
 * @version $Id: SwtOverlapExample.java 906 2009-11-13 21:15:27Z kliem $
 */
public class SwtOverlapExample extends ApplicationWindow {
    /** if set to true an ITimeBarChangeListener will be registered for monitoring changes. */
    private static final boolean MONITORINTERVALCHANGES = false;
    private static TimeBarViewer _tbv;

    public SwtOverlapExample() {
        super(null);
    }

    protected Control createContents(Composite parent) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        parent.setLayout(gridLayout);

        TimeBarModel model = ModelCreator.createModel();
        // TimeBarModel model = ModelCreator.createLargeModel();

        GridData gd = new GridData(GridData.FILL_BOTH);

        _tbv = new TimeBarViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        _tbv.setLayoutData(gd);

        _tbv.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);
        _tbv.setModel(model);

        // add a context menu on intervals
        // commented to not introduce a jface dependencay in this example
        // MenuManager ctxMM = new MenuManager();
        // ctxMM.add(new Action() {
        // @Override
        // public String getText() {
        // return "Test";
        // }
        // });
        // Menu intervalContextMenu = ctxMM.createContextMenu(_tbv);
        // _tbv.setIntervalContextMenu(intervalContextMenu);

        // add two interval modificator using different grid snaps
        // the last row (r3) will be filled with other intevals using the other grid snap
        _tbv.addIntervalModificator(new DefaultIntervalModificator() {
            @Override
            public double getSecondGridSnap() {
                return 100;
            }
 
//            @Override
//            public double getSecondGridSnap(TimeBarRow row, Interval interval) {
//            	if (row.getIntervals().size() == 1) {
//            		return 2000;
//            	} 
//            	return -1;
//            }

            @Override
            public boolean isApplicable(TimeBarRow row, Interval interval) {
                return !(interval instanceof OtherIntervalImpl);
            }

        });
        _tbv.addIntervalModificator(new DefaultIntervalModificator() {
            @Override
            public double getSecondGridSnap() {
                return 1000;
            }

            @Override
            public boolean isApplicable(TimeBarRow row, Interval interval) {
                return (interval instanceof OtherIntervalImpl);
            }

        });

        // optional other interval renderer
        //_tbv.registerTimeBarRenderer(OtherIntervalImpl.class, new OtherIntervalRenderer());
        
        
        _tbv.setPixelPerSecond(0.05);
        _tbv.setDrawRowGrid(true);

        _tbv.setSelectionDelta(6);

        // do not assume sorted intervals
        // --> tell the default strategy to do the sorting
        ((DefaultOverlapStrategy) _tbv.getOverlapStrategy()).setAssumeSortedIntervals(false);

        // configure the title renderer with a background image and set the title
        DefaultTitleRenderer titleRenderer = new DefaultTitleRenderer();
        titleRenderer.setBackgroundRscName("/de/jaret/examples/timebars/hierarchy/swt/titlebg.png");
        _tbv.setTitleRenderer(titleRenderer);
        _tbv.setTitle("SwtOverlap");
        
        BoxTimeScaleRenderer btsr = new BoxTimeScaleRenderer();
        // enable DST correction
        //btsr.setCorrectDST(true);
        _tbv.setTimeScaleRenderer(btsr);
        
        
        SwtControlPanel ctrl = new SwtControlPanel(parent, SWT.NULL, _tbv, null);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        ctrl.setLayoutData(gd);

        OverlapControlPanel ctrl2 = new OverlapControlPanel(parent, SWT.NULL, _tbv);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        ctrl2.setLayoutData(gd);

        // add a listener for doubleclicks (and some test code for selecting specially rendered intervals)
        // shows how to get intervals from a point
        _tbv.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                List<Interval> intervals = _tbv.getIntervalsAt(e.x, e.y);
                if (intervals != null && intervals.size() > 0) {
                    for (Interval interval : intervals) {
                        System.out.println("Doubleclicked: " + interval.toString());
                    }
                }
            }
//            public void mouseUp(MouseEvent e) {
//            	List<Interval> intervals = _tbv.getIntervalsAt(e.x, e.y);
//                if (intervals == null || intervals.size() == 0) {
//                	// check all intervals in the row in the actual range
//                	TimeBarRow row = _tbv.getRowForXY(e.x, e.y);
//                	if (row != null) {
//                    	intervals = row.getIntervals(_tbv.getStartDate(), _tbv.getEndDate());
//                		for (Interval interval : intervals) {
//							Rectangle bounds = getBounds(row, interval);
//							if (bounds.contains(e.x, e.y)) {
//								_tbv.getSelectionModel().setSelectedInterval(interval);
//								_tbv.redraw();
//							}
//						}
//                	}
//                }
//            }
//            /**
//             * Retrieve bounds of an interval including possible extended rendering space
//             * @param row
//             * @param interval
//             * @return
//             */
//			private Rectangle getBounds(TimeBarRow row, Interval interval) {
//				int PREFWIDTH = 4;
//				org.eclipse.swt.graphics.Rectangle bounds = _tbv.getIntervalBounds(interval);
//				int diff = (PREFWIDTH-bounds.width/2);
//
//				Rectangle result = new Rectangle(bounds.x-diff, bounds.y, bounds.width+2*diff, bounds.height);
//				return result;
//			}
        });

        initDND(_tbv, parent);

        if (MONITORINTERVALCHANGES) {
            registerChangeListener(_tbv);
        }

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
                    for (int i = 0; i < 20; i++) {
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

        // final Button setStartDate = new Button(addPanel, SWT.PUSH);
        // setStartDate.setText("Set Start date-");
        // setStartDate.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // _tbv.setAdjustMinMaxDatesByModel(false);
        // _tbv.setMinDate(_tbv.getMinDate().copy().backHours(2));
        // _tbv.setStartDate(_tbv.getStartDate().copy().backHours(2));
        // }
        // });
        // final Button setStartDate2 = new Button(addPanel, SWT.PUSH);
        // setStartDate2.setText("Set Start date+");
        // setStartDate2.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // _tbv.setAdjustMinMaxDatesByModel(false);
        // _tbv.setMaxDate(_tbv.getMaxDate().copy().advanceHours(2));
        // _tbv.setStartDate(_tbv.getStartDate().copy().advanceHours(2));
        // }
        // });

        
        DefaultOverlapStrategy os = (DefaultOverlapStrategy)_tbv.getOverlapStrategy();
        os.setAssumeSortedIntervals(false);

        
        // TODO temp
        final Button lastrow5 = new Button(addPanel, SWT.PUSH);
        lastrow5.setText("last 5");
        lastrow5.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                _tbv.getDelegate().setLastRow(5);
            }
        });
        final Button lastrow7 = new Button(addPanel, SWT.PUSH);
        lastrow7.setText("last 7");
        lastrow7.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                _tbv.getDelegate().setLastRow(7);
            }
        });
        final Button lastrow0 = new Button(addPanel, SWT.PUSH);
        lastrow0.setText("last 0");
        lastrow0.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                _tbv.getDelegate().setLastRow(0);
            }
        });
        final Button lastrow20 = new Button(addPanel, SWT.PUSH);
        lastrow20.setText("last 20");
        lastrow20.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                _tbv.getDelegate().setLastRow(19);
            }
        });

        
        
        
        return _tbv;
    }

    private void registerChangeListener(TimeBarViewer tbv) {
        tbv.addTimeBarChangeListener(new ITimeBarChangeListener() {

            public void intervalChangeCancelled(TimeBarRow row, Interval interval) {
                System.out.println("CHANGE CANCELLED " + row + " " + interval);
            }

            public void intervalChangeStarted(TimeBarRow row, Interval interval) {
                System.out.println("CHANGE STARTED " + row + " " + interval);
            }

            public void intervalChanged(TimeBarRow row, Interval interval, JaretDate oldBegin, JaretDate oldEnd) {
                System.out.println("CHANGE DONE " + row + " " + interval);
            }

            public void intervalIntermediateChange(TimeBarRow row, Interval interval, JaretDate oldBegin,
                    JaretDate oldEnd) {
                System.out.println("CHANGE INTERMEDIATE " + row + " " + interval);
            }

            public void markerDragStarted(TimeBarMarker marker) {
                System.out.println("Marker drag started "+marker);
            }

            public void markerDragStopped(TimeBarMarker marker) {
                System.out.println("Marker drag stopped "+marker);
            }

        });

    }

    // information about ongoing drag operation
    private Point _dragStart;
    private int _startOffsetX;
    private int _startOffsetY;
    private JaretDate _dragStartDate;
    private List<Interval> _draggedIntervals;
    private List<Integer> _yOffsets;
    private List<Interval> _origIntervals;
    private TimeBarRow _draggedRow;
    private boolean _isRowDrag;

    /**
     * Init the drag source and the drop target for the timebar viewer. Information will be provided as text for
     * dropping outside the table viewer. Drag&Drop inside the viewer uses instance data to transport the information
     * about the dragged interval. This example supports dragging all selected intervals and dragging a single row
     * determined when the drag starts.
     * 
     * @param tbv timebarviewer
     * @param parent parent of the timebarviewer (for disposal listening)
     */
    private void initDND(final TimeBarViewer tbv, Composite parent) {
        // support move and copy
        int operations = DND.DROP_COPY | DND.DROP_MOVE;
        final DragSource source = new DragSource(tbv, operations);

        // Provide data in Text format
        Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
        source.setTransfer(types);

        source.addDragListener(new DragSourceListener() {
            public void dragStart(DragSourceEvent event) {

                // check whether drag occured on header or hierarchy area
                if (_tbv.isInRowAxis(event.x, event.y)) {
                    // possible row drag
                    TimeBarRow row = _tbv.getRowForXY(event.x, event.y);
                    if (row != null && !_tbv.rowLineHit(event.x, event.y)) {
                        // row hit, start row drag
                        _isRowDrag = true;
                        _dragStart = new Point(event.x, event.y);

                        // capture the data for internal use
                        // row drag: use row at starting position
                        _draggedRow = row;

                    } else {
                        event.doit = false;
                    }

                } else {
                    // Only start the drag if there is an interval at hand
                    // and the point of the beginning drag is not near the border of the interval, allowing the
                    // timebarviewers implementation of dragging for moving inside a row and for resizing to be usable
                    // even with an installed drag source (The internal drag feature can be disabled when using
                    // the systems drag and drop support)
                    List<Interval> l = tbv.getIntervalsAt(event.x, event.y);
                    if (l.size() > 0) {
                        Interval interval = l.get(0);
                        JaretDate date = tbv.dateForXY(event.x, event.y);
                        if (Math.abs(date.diffSeconds(interval.getBegin())) < 1000
                                || Math.abs(date.diffSeconds(interval.getEnd())) < 1000) {
                            System.out.println("Near the border of an interval -> no drag");
                            event.doit = false;
                        } else {
                            // remember the point the drag started
                            _dragStart = new Point(event.x, event.y);
                            _dragStartDate = _tbv.dateForXY(event.x, event.y);
                            _isRowDrag = false;
                            // make sure the interval is selected
                            _tbv.getSelectionModel().addSelectedInterval(interval);

                            // capture the data of the dragged intervals
                            // drag all selected intervals
                            // to allow changing the time of the intervals and drawing ghost intervals a copy of the
                            // intervals will be made
                            _draggedIntervals = new ArrayList<Interval>();
                            _origIntervals = new ArrayList<Interval>();
                            _yOffsets = new ArrayList<Integer>();

                            for (Interval i : _tbv.getSelectionModel().getSelectedIntervals()) {
                                _draggedIntervals.add(new IntervalImpl(i.getBegin().copy(), i.getEnd().copy()));
                                _origIntervals.add(i);
                                TimeBarRow row = _tbv.getModel().getRowForInterval(i);
                                int yOffset;
                                if (_tbv.getOrientation().equals(TimeBarViewerInterface.Orientation.HORIZONTAL)) {
                                    yOffset = _tbv.getYForRow(row) - _dragStart.y;
                                } else {
                                    yOffset = _tbv.getYForRow(row) - _dragStart.x;
                                }
                                _yOffsets.add(yOffset);
                            }

                            TimeBarRow row = _tbv.getRowForXY(event.x, event.y);
                            // store both x and y offsets since the orientation may be vertical or horizontal
                            _startOffsetY = event.y - tbv.getYForRow(row);
                            _startOffsetX = event.x - tbv.getYForRow(row);
                        }
                    } else {
                        event.doit = false;
                    }
                }
            }

            public void dragSetData(DragSourceEvent event) {
                // Provide the data of the requested type.
                if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                    // unfortenatly the coordinates in the event are always 0,0 -> use the coordinates transferred in
                    // startDrag to get the interval (a possibly is to use the selection, especially for dragging
                    // multiple intervals)
                    if (_isRowDrag) {
                        event.data = "row: " + _draggedRow.getRowHeader().getLabel();
                    } else {
                        StringBuffer buf = new StringBuffer();
                        buf.append("intervals:");
                        for (Interval interval : _origIntervals) {
                            buf.append(interval);
                            buf.append(";");
                        }
                        event.data = buf.toString();
                    }
                }
            }

            public void dragFinished(DragSourceEvent event) {
                // drag finished -> if it is a move operation remove the original intervals
                // from their rows
                if (event.detail == DND.DROP_MOVE) {
                    System.out.println("Perform move");
                    if (_isRowDrag) {
                        DefaultTimeBarModel model = (DefaultTimeBarModel) _tbv.getModel();
                        model.remRow(_draggedRow);
                    } else {
                        for (Interval interval : _origIntervals) {
                            DefaultTimeBarRowModel row = (DefaultTimeBarRowModel) _tbv.getModel().getRowForInterval(
                                    interval);
                            row.remInterval(interval);
                        }
                    }
                }
                // clear the dragged data
                // and the ghosts in the viewer
                _tbv.deHighlightRow();
                _tbv.setGhostIntervals(null, null);
                _tbv.setGhostRows(null, null);
                _draggedIntervals = null;
                _draggedRow = null;
                _isRowDrag = false;
                _origIntervals = null;
            }
        });

        // ////////////////////
        // Drop target

        // Allow data to be copied or moved to the drop target
        operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        final DropTarget target = new DropTarget(tbv, operations);

        // Receive data in Text
        final TextTransfer textTransfer = TextTransfer.getInstance();
        types = new Transfer[] {textTransfer};
        target.setTransfer(types);

        target.addDropListener(new DropTargetListener() {
        	final static int DELAY = 5; 
        	int _verticalAutoscrollDelay = DELAY;
        	
            public void dragEnter(DropTargetEvent event) {
            }

            public void dragOver(DropTargetEvent event) {
                // event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;

                if (_isRowDrag && _draggedRow != null) {
                    // row drag
                    int destY = Display.getCurrent().map(null, tbv, event.x, event.y).y;
                    int destX = Display.getCurrent().map(null, tbv, event.x, event.y).x;
                    tbv.setGhostRow(_draggedRow, 0);
                    tbv.setGhostOrigin(destX, destY);
                    TimeBarRow overRow = tbv.getRowForXY(destX, destY);
                    if (overRow != null) {
                        tbv.highlightRow(overRow);
                    } else {
                        tbv.deHighlightRow();
                    }

                } else if (!_isRowDrag && _draggedIntervals != null && _draggedIntervals.size() > 0) {
                    // map the coordinates from the event to the timebar viewer widget
                    int destY = Display.getCurrent().map(null, tbv, event.x, event.y).y;
                    int destX = Display.getCurrent().map(null, tbv, event.x, event.y).x;

                    JaretDate curDate = _tbv.dateForXY(destX, destY);
                    long diffSeconds = _dragStartDate.diffSeconds(curDate);
                    // correct the dragged interval bounds
                    for (int i = 0; i < _draggedIntervals.size(); i++) {
                        Interval orig = _origIntervals.get(i);
                        Interval interval = _draggedIntervals.get(i);
                        interval.setBegin(orig.getBegin().copy().advanceSeconds(-diffSeconds));
                        interval.setEnd(orig.getEnd().copy().advanceSeconds(-diffSeconds));
                    }

                    // check if we are over a row and highlight it
                    // this will only highlight the row for the mouse pointer
                    // if multiple intervals are dragged their respective rows will not be highlighted
                    TimeBarRow overRow = tbv.getRowForXY(destX, destY);
                    if (overRow != null) {
                        tbv.highlightRow(overRow);
                    } else {
                        System.out.println("Row is null x,y:" + event.x + "," + event.y);
                        tbv.deHighlightRow();
                    }
                    // tell the timebar viewer
                    tbv.setGhostIntervals(_draggedIntervals, _yOffsets);
                    tbv.setGhostOrigin(destX, destY);
                    
                    // vertical autoscroll
                    // the delay is initialized at the top of the drop target!
                    int range = 15;
                    Rectangle diagramRect = _tbv.getDelegate().getDiagramRect();
                    if (destY<=diagramRect.y) {
                        _verticalAutoscrollDelay--;
                        if (_verticalAutoscrollDelay < 0) {
	                    	_verticalAutoscrollDelay = DELAY;
	                        int ridx = _tbv.getFirstRowDisplayed();
	                        if (ridx>0) {
	                            _tbv.setFirstRowDisplayed(ridx-1);
	                            _tbv.setFirstRowOffset(0);
	                        } else {
	                            _tbv.setFirstRowOffset(0);
	                        }
                        }
                    } else 
                    if (destY>=diagramRect.y+diagramRect.height-range) {
                        _verticalAutoscrollDelay--;
                        if (_verticalAutoscrollDelay < 0) {
	                    	_verticalAutoscrollDelay = DELAY;
                        	
                        	TimeBarRow row = _tbv.rowForY(destY);
	                        int ridx = _tbv.getDelegate().getRowIndex(row);
	                        System.out.println("ridx "+ridx);
	                        if (ridx+1<_tbv.getDelegate().getRowCount()) {
	                            System.out.println("try ridx "+(ridx+1));
	                            TimeBarRow nextRow = _tbv.getDelegate().getRow(ridx+1);
	                            _tbv.setLastRow(nextRow);
//	                            _tbv.scrollRowToVisible(nextRow);
	                        }
                        }
                    } else {
                    	_verticalAutoscrollDelay = DELAY;
                    }
                    
                    
                }
            }

            public void dragOperationChanged(DropTargetEvent event) {
            }

            public void dragLeave(DropTargetEvent event) {
                // leaving: do not leave a row highlighted
                tbv.deHighlightRow();
                tbv.setGhostIntervals(null, null);
            }

            public void dropAccept(DropTargetEvent event) {
            }

            public void drop(DropTargetEvent event) {
                // drop operation. the drop operation places the dragged intervals in the destination rows.
                // if a row can not be determined (no row under the dragged interval) the interval will be discarded
                // The dragged intervals are copies of the original intervals, in a real application it might be better
                // to move the originals in case of a move operation.
                // The dragged row is not modified by the drag operation. So in case of a drop it is copied and
                // inserted.
                // ATTENTION: the implementation herein does only work properly if no row sorters or row filters are
                // applied.
                // If that is the case it is up to the implementation to take of that.
                if (textTransfer.isSupportedType(event.currentDataType)) {
                    String text = (String) event.data;
                    System.out.println("DROP: " + text);

                    if (_isRowDrag && _draggedRow != null) {
                        int destY = Display.getCurrent().map(null, tbv, event.x, event.y).y;
                        int destX = Display.getCurrent().map(null, tbv, event.x, event.y).x;

                        TimeBarRow overRow = tbv.getRowForXY(destX, destY);
                        System.out.println("over row " + overRow.getRowHeader());
                        if (overRow != null) {
                            DefaultTimeBarRowModel row = copyDraggedRow();
                            DefaultTimeBarModel model = (DefaultTimeBarModel) _tbv.getModel();

                            int index = model.getIndexForRow(overRow);
                            System.out.println("index " + index);
                            model.addRow(index, row);
                        }
                    } else if (_draggedIntervals != null) {

                        for (int i = 0; i < _draggedIntervals.size(); i++) {
                            int destY = Display.getCurrent().map(null, tbv, event.x, event.y).y;
                            int destX = Display.getCurrent().map(null, tbv, event.x, event.y).x;
                            int offY = _yOffsets.get(i);
                            TimeBarRow overRow = null;
                            if (_tbv.getOrientation().equals(TimeBarViewerInterface.Orientation.HORIZONTAL)) {
                                overRow = tbv.rowForY(destY + offY + _startOffsetY);
                            } else {
                                overRow = tbv.rowForY(destX + offY + _startOffsetX);
                            }
                            DefaultTimeBarRowModel row = (DefaultTimeBarRowModel) overRow;
                            if (overRow != null) {
                                row.addInterval(_draggedIntervals.get(i));
                            }
                        }
                    }
                }
            }

            private DefaultTimeBarRowModel copyDraggedRow() {
                DefaultRowHeader header = (DefaultRowHeader) _draggedRow.getRowHeader();
                DefaultTimeBarRowModel row = new DefaultTimeBarRowModel(new DefaultRowHeader(header.getLabel()
                        + "(copy)"));
                for (Interval interval : _draggedRow.getIntervals()) {
                    row.addInterval(new IntervalImpl(interval.getBegin().copy(), interval.getEnd().copy()));
                }
                return row;
            }
        });

        // Dispose listener on parent of timebar viewer to dispose the dragsource and dragtarget BEFORE the timebar
        // viewer
        // this prevents an exception beeing thrown by SWT
        parent.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                source.dispose();
                target.dispose();
            }
        });

    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(getClass().getName());
    }

    public static void main(String[] args) {
        SwtOverlapExample test = new SwtOverlapExample();
        test.setBlockOnOpen(true);
        test.open();
    }

    



}