/*
 *  File: SwtCalendarExample.java 
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.jaret.examples.timebars.calendar.model.Appointment;
import de.jaret.examples.timebars.calendar.model.AppointmentModificator;
import de.jaret.examples.timebars.calendar.model.CalendarIntervalFilter;
import de.jaret.examples.timebars.calendar.model.CalendarModel;
import de.jaret.examples.timebars.calendar.model.Day;
import de.jaret.examples.timebars.calendar.model.ModelCreator;
import de.jaret.examples.timebars.calendar.swt.renderer.AppointmentRenderer;
import de.jaret.examples.timebars.calendar.swt.renderer.CalendarGridRenderer;
import de.jaret.examples.timebars.calendar.swt.renderer.CalendarTimeScaleRenderer;
import de.jaret.examples.timebars.calendar.swt.renderer.DayHeaderRenderer;
import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.strategy.OverlapInfo;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * SWT: example demonstrating the vertical orientation showing a calendar view.
 * 
 * @author Peter Kliem
 * @version $Id: SwtCalendarExample.java 733 2008-03-22 15:41:28Z kliem $
 */
public class SwtCalendarExample extends ApplicationWindow {
    private static TimeBarViewer _tbv;

    public SwtCalendarExample() {
        super(null);
    }

    protected Control createContents(Composite parent) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        parent.setLayout(gridLayout);

        TimeBarModel model = ModelCreator.createCalendarModel();

        GridData gd = new GridData(GridData.FILL_BOTH);

        _tbv = new TimeBarViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        _tbv.setLayoutData(gd);

        _tbv.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);
        _tbv.setModel(model);

        _tbv.setPixelPerSecond(0.018);
        _tbv.setDrawRowGrid(true);

        _tbv.setSelectionDelta(6);
        // this is the col width!
        _tbv.setRowHeight(150);

        _tbv.setOrientation(TimeBarViewerInterface.Orientation.VERTICAL);
        // vertical: the y axiswidth is the height of the row headers!
        _tbv.setYAxisWidth(20);

        // do not adjust the displaed time according to the model
        // use the basedate day!
        _tbv.setAdjustMinMaxDatesByModel(false);
        _tbv.setMinDate(CalendarModel.BASEDATE.copy());
        _tbv.setMaxDate(CalendarModel.BASEDATE.copy().advanceDays(1));

        // set the header renderer
        _tbv.setHeaderRenderer(new DayHeaderRenderer());

        // timescale is default + special tooltip
        _tbv.setTimeScaleRenderer(new CalendarTimeScaleRenderer());

        // register the AppointmentRenderer for rendering appointments
        AppointmentRenderer ar = new AppointmentRenderer();
        _tbv.registerTimeBarRenderer(Appointment.class, ar);

        // modifications are restricted
        _tbv.addIntervalModificator(new AppointmentModificator());

        _tbv.setGridRenderer(new CalendarGridRenderer());

        CalendarControlPanel ctrl = new CalendarControlPanel(parent, SWT.NULL, _tbv, null);
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
                    }
                }
            }
        });

        initDND(_tbv, parent);

        _tbv.setIntervalFilter(new CalendarIntervalFilter());
        _tbv.setYAxisWidth(50);

        return _tbv;
    }

    // information about ongoing drag operation
    private Point _dragStart;
    private int _startOffsetX;
    private int _startOffsetY;
    private JaretDate _dragStartDate;
    private List<Interval> _draggedIntervals;
    private List<Integer> _yOffsets;
    private List<Interval> _origIntervals;
    private List<TimeBarRow> _origRows;
    private TimeBarRow _draggedRow;
    private boolean _isRowDrag;
    int _lastDragOverX;
    int _lastDragOverY;

    /**
     * Init the drag source and the drop target for the timebar viewer. Information will be provided as text for
     * dropping outside the table viewer. Drag&Drop inside the viewer uses instance data to transport the information
     * about the dragged intervals.
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
                boolean horizontal = _tbv.getOrientation().equals(TimeBarViewerInterface.Orientation.HORIZONTAL);
                TimeBarViewerDelegate delegate = (TimeBarViewerDelegate) _tbv.getData("delegate");

                // check whether drag occured on the drag marker in the interval
                if (_tbv.isInDiagram(event.x, event.y)) {
                    List<Interval> l = tbv.getIntervalsAt(event.x, event.y);
                    if (l.size() > 0) {
                        Interval interval = l.get(0);

                        TimeBarRow clickedRow = _tbv.getModel().getRowForInterval(interval);
                        OverlapInfo oi = delegate.getOverlapStrategy().getOverlapInfo(clickedRow, interval);
                        boolean overlap = oi != null ? oi.overlappingCount > 0 : false;

                        Rectangle bounds = _tbv.getIntervalBounds(interval);
                        if (AppointmentRenderer.isInDragMark(bounds, event.x, event.y, horizontal, overlap)) {
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
                            _origRows = new ArrayList<TimeBarRow>();
                            _yOffsets = new ArrayList<Integer>();

                            for (Interval i : _tbv.getSelectionModel().getSelectedIntervals()) {
                                _draggedIntervals.add(((Appointment) i).copy());
                                TimeBarRow row = _tbv.getModel().getRowForInterval(i);
                                _origIntervals.add(i);
                                _origRows.add(row);

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
                            return;
                        }
                    }
                }
                event.doit = false;
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
                    for (Interval interval : _origIntervals) {
                        DefaultTimeBarRowModel row = (DefaultTimeBarRowModel) _tbv.getModel().getRowForInterval(
                                interval);
                        row.remInterval(interval);
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
                _origRows = null;
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
            public void dragEnter(DropTargetEvent event) {
            }

            public void dragOver(DropTargetEvent event) {
                // event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;

                // prevent multiple dragOvers without move
                if (event.x != _lastDragOverX || event.y != _lastDragOverY) {
                    _lastDragOverX = event.x;
                    _lastDragOverY = event.y;

                    // System.out.println("dragover " + event);

                    if (!_isRowDrag && _draggedIntervals != null && _draggedIntervals.size() > 0) {
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
                if (textTransfer.isSupportedType(event.currentDataType)) {
                    String text = (String) event.data;
                    System.out.println("DROP: " + text);

                    if (_draggedIntervals != null) {
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

                            // NOTE: copies are placed in the dest rows
                            if (overRow != null) {
                                row.addInterval(_draggedIntervals.get(i));
                                Appointment app = (Appointment) _draggedIntervals.get(i);
                                Day destDay = (Day) overRow;
                                JaretDate realBegin = app.getRealBegin().copy();
                                realBegin.setDate(destDay.getDayDate().getDay(), destDay.getDayDate().getMonth(),
                                        destDay.getDayDate().getYear());
                                app.setRealBegin(realBegin);
                                JaretDate realEnd = app.getRealEnd().copy();
                                realEnd.setDate(destDay.getDayDate().getDay(), destDay.getDayDate().getMonth(), destDay
                                        .getDayDate().getYear());
                                app.setRealEnd(realEnd);

                            }
                        }
                    }
                }
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
        SwtCalendarExample test = new SwtCalendarExample();
        test.setBlockOnOpen(true);
        test.open();
    }

}