/*
 *  File: TimeBarViewerDelegate.java 
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
package de.jaret.util.ui.timebars;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.misc.Pair;
import de.jaret.util.ui.timebars.TimeBarViewerInterface.Orientation;
import de.jaret.util.ui.timebars.mod.IIntervalModificator;
import de.jaret.util.ui.timebars.mod.IntervalModificator;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarNode;
import de.jaret.util.ui.timebars.model.DefaultTimeBarViewState;
import de.jaret.util.ui.timebars.model.FocussedIntervalListener;
import de.jaret.util.ui.timebars.model.HierarchicalTimeBarModel;
import de.jaret.util.ui.timebars.model.HierarchicalViewState;
import de.jaret.util.ui.timebars.model.HierarchicalViewStateImpl;
import de.jaret.util.ui.timebars.model.IIntervalRelation;
import de.jaret.util.ui.timebars.model.ISelectionRectListener;
import de.jaret.util.ui.timebars.model.ITimeBarChangeListener;
import de.jaret.util.ui.timebars.model.ITimeBarViewState;
import de.jaret.util.ui.timebars.model.ITimeBarViewStateListener;
import de.jaret.util.ui.timebars.model.PPSInterval;
import de.jaret.util.ui.timebars.model.StdHierarchicalTimeBarModel;
import de.jaret.util.ui.timebars.model.TBRect;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarModelListener;
import de.jaret.util.ui.timebars.model.TimeBarNode;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;
import de.jaret.util.ui.timebars.model.TimeBarRowListener;
import de.jaret.util.ui.timebars.model.TimeBarSelectionListener;
import de.jaret.util.ui.timebars.model.TimeBarSelectionModel;
import de.jaret.util.ui.timebars.model.TimeBarSelectionModelImpl;
import de.jaret.util.ui.timebars.strategy.DefaultIntervalSelectionStrategy;
import de.jaret.util.ui.timebars.strategy.DefaultOverlapStrategy;
import de.jaret.util.ui.timebars.strategy.IIntervalSelectionStrategy;
import de.jaret.util.ui.timebars.strategy.IOverlapStrategy;
import de.jaret.util.ui.timebars.strategy.OverlapInfo;

/**
 * The delegate for supporting a TimeBarViewer. This may be the Swing or the SWT version of the time bar viewer. The
 * delegate encapsulates most of the calculations. It communicates with the viewer for the specific toolkit via the
 * TimeBarViewerInterface.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarViewerDelegate.java 906 2009-11-13 21:15:27Z kliem $
 */
public class TimeBarViewerDelegate implements TimeBarModelListener, TimeBarSelectionListener, TimeBarMarkerListener,
        PropertyChangeListener {

    /** default value for the pps value. */
    private static final double DEFAULT_PIXEL_PER_SECOND = 2000.0 / (24.0 * 60 * 60);

    /**
     * pixel delta added on the left and right of the viewer area to ensure elements on the border can be selected.
     */
    private static final int PADDING_PIXEL = 30;

    /**
     * minimal width for the hierarchy area when dragging. Ensures the area will be selectable after drag.
     */
    private static final int MIN_DRAG_HIERARCHY_WIDTH = 2;
    /**
     * minimal width for the header area when dragging. Ensures the area will be selectable after drag.
     */
    private static final int MIN_DRAG_HEADER_WIDTH = 2;

    /** minimal height for a row when dragging the row height. */
    private static final int MIN_ROW_HEIGHT = 10;

    /** factor for scaling the second vaule to milliseconds. */
    private static final double MILLISCALING = 1000.0;

    /** default delta for modification snap/detection. */
    private static final int DEFAULT_SEL_DELTA = 2;

    /** delta for detecting modification clicks and drags. */
    private int _selectionDelta = DEFAULT_SEL_DELTA;

    /** the delegating time bar viewer. */
    protected final TimeBarViewerInterface _tbvi;

    /** scale. */
    protected double _pixelPerSeconds = DEFAULT_PIXEL_PER_SECOND;

    /** flag indicating a variable xscale. */
    protected boolean _variableXScale = false;
    /**
     * row holding intervals with pps values overriding the default for intervals.
     */
    protected TimeBarNode _xScalePPSIntervalRow;

    /**
     * If true the viewer is used with very short intervals and the scrolling is done using milliseconds.
     */
    protected boolean _milliAccuracy = false;

    /** height of the time scale. */
    protected int _xAxisHeight = TimeBarViewerInterface.DEFAULT_XAXISHEIGHT;

    /** width of the y axis. */
    protected int _yAxisWidth = TimeBarViewerInterface.DEFAULT_YAXISWIDTH;

    /** width of the hierarchy header (in case of an hierachical model). */
    protected int _hierarchyWidth = 0;

    /** true if dragging of the hierarchy and header delimiter is allowed. */
    protected boolean _lineDraggingAllowed = true;

    /** the orientation of the viewer. */
    protected Orientation _orientation = Orientation.HORIZONTAL;

    /** The model. */
    protected TimeBarModel _model;
    /** The hierarchical model when used. */
    protected HierarchicalTimeBarModel _hierarchicalModel;

    /** standard view state. */
    protected ITimeBarViewState _timeBarViewState;

    /** Viewstate holding state information for hierarchical operation. */
    protected HierarchicalViewState _hierarchicalViewState;

    /** title of the viewer. */
    protected String _title;

    /** index of the first row displayed. */
    protected int _firstRow = 0;

    /** pixeloffset of the first row. */
    protected int _firstRowPixelOffset = 0;

    /** filtered and sorted list. */
    protected List<TimeBarRow> _rowList;

    /**
     * Flag controlling min/max setting behaviour. If true the min and max of the viewer are adapted to the models
     * min/max dates.
     */
    protected boolean _adjustMinMaxDatesByModel = true;

    /** if set to true scrolling optimizations will be used. */
    protected boolean _optimizeScrolling = true;

    /** Date marking the first painted date. */
    protected JaretDate _startDate = new JaretDate();

    /** Date marking the last painted date. */
    protected JaretDate _endDate = new JaretDate();

    /** Minimum date to be displayed by the viewer. */
    protected JaretDate _minDate;

    /** Maximum date to be displayed by the viewer. */
    protected JaretDate _maxDate;

    /** start date of the viewer for that the last paint completed. */
    protected JaretDate _lastStartDate;

    /** Filter filtering rows displayed. */
    protected TimeBarRowFilter _rowFilter;
    /** sorter for the rows in the model. */
    protected TimeBarRowSorter _rowSorter;
    /** filter filtering intervals displayed. */
    protected TimeBarIntervalFilter _intervalFilter;

    /** selection model, default implementation as default. */
    protected TimeBarSelectionModel _selectionModel = new TimeBarSelectionModelImpl();

    /** markers added to the viewer. */
    protected List<TimeBarMarker> _markers;

    /** when true the grid/background will be painted. */
    protected boolean _drawGrid = true;

    /** if true a row grid will be painted. */
    protected boolean _drawRowGrid = false; // true -> row grid will be painted

    /** if set to true and drawOverlapping=false all intervals in a row use the same height. */
    private boolean _useUniformHeight = false;

    /**
     * position of the timescale. One of the constants from the viewer interface.
     */
    protected int _timeScalePosition = TimeBarViewerInterface.TIMESCALE_POSITION_BOTTOM;

    /** area in which the actual rows are painted. */
    protected Rectangle _diagramRect = new Rectangle(0, 0, 0, 0);

    /** area in which the x axis (time scale) will be painted. */
    protected Rectangle _xAxisRect = new Rectangle();;

    /** area in which the y axis will be painted. */
    protected Rectangle _yAxisRect = new Rectangle();

    /** Title area. */
    protected Rectangle _titleRect = new Rectangle();;

    /** drawing area of the hierarchy elements. */
    protected Rectangle _hierarchyRect = new Rectangle();;

    /** pixeloffset when rendering (top). */
    protected int _offsetTop;
    /** pixeloffset when rendering (left). */
    protected int _offsetLeft;

    /** true if the selection by a selection rectangle schould be allowed. */
    private boolean _rectSelectionEnabled = true;

    /** marker currently involved in a drag operation. */
    protected TimeBarMarker _draggedMarker;

    /** interval currently changed by dragging. */
    protected Interval _changingInterval;

    /** true indicates dragging of an interval as a whole. */
    protected boolean _draggedInterval;

    /** if not dragging the whole interval true means left bound. */
    protected boolean _draggedIntervalEdgeLeft;

    /** default for the autoscroll delta. */
    protected static final int DEFAULT_AUTOSCROLL_DELTA = 10;
    /** the amount of pixels that is assumed to scroll/drag when the cursor left the diagram area. */
    protected int _autoscrollDelta = DEFAULT_AUTOSCROLL_DELTA;

    /** flag indicating whether all selcted intervals should be dragged whe draggig a iterval. */
    protected boolean _dragAllSelectedIntervals = false;

    /** true if dragging of the hierarchy limiting line is ongoing. */
    protected boolean _hierarchyLineDragging = false;
    /** true if dragging of the header limiting line is ongoing. */
    protected boolean _headerLineDragging = false;
    /** true if the dragginng of rows is allowed. */
    protected boolean _rowHeightDraggingAllowed = false;

    /** row thats height is currently dragged or <code>null</code> when no row height is beeing dragged. */
    protected TimeBarRow _heightDraggedRow = null;

    /** controls autoscroll behaviour: true -> enabled. */
    protected boolean _autoscroll = true;

    /** the current selection rectangle. */
    protected Rectangle _selectionRect = null;

    /** the last selection rect if existing. */
    protected Rectangle _lastSelRect;

    /** highlighted row if any. */
    protected TimeBarRow _highlightedRow;

    /** name of the viewer. */
    private String _name;

    /** flag controlling the drawing behaviour for overlapping intervals. */
    protected boolean _drawOverlapping = false;

    /** Change delta for keyboard modification in seconds. */
    protected int _keyboardChangeDelta = 60 * 60;

    /** if true the viewer will try to scroll to a newly focussed interval. */
    protected boolean _scrollOnFocus = true;

    /**
     * Currenty focussed interval or <code>null</code> if no interval has the focus.
     */
    protected Interval _focussedInterval;

    /**
     * Row of the focussed interval or <code>null</code> if no interval is focussed.
     */
    protected TimeBarRow _focussedRow;

    /** timebar row listener that repaints on every change. */
    private TimeBarRowListener _repaintingRowListener;

    /** number of rows to scale to. -1 for no scaling. */
    private int _autoScaleRows = -1;

    /** List of FocussedInteralListeners. */
    protected List<FocussedIntervalListener> _focussedIntervalListeners;

    /** List of TimeBarChangeListenes. */
    protected List<ITimeBarChangeListener> _timeBarChangeListeners = new Vector<ITimeBarChangeListener>();

    /** List of ISelectionRectListeners. */
    protected List<ISelectionRectListener> _selectionRectListeners = new Vector<ISelectionRectListener>();

    /** List of intervalmodificators that have been registered. */
    protected List<IntervalModificator> _intervalModificators = new ArrayList<IntervalModificator>(2);

    /**
     * If set to true, intervals are filtered strictly by their interval bounds, disallowing rendering beyond the
     * bounding box calculated by the interval bounds.
     */
    protected boolean _strictClipTimeCheck = false;

    /** additional time to take into account (looking back) when determining what intervals have to be painted. */
    protected int _scrollLookBackMinutes = 120;
    /** additional time to take into account (looking forward) when determining what intervals have to be painted. */
    protected int _scrollLookForwardMinutes = 120;

    /** factor used to scale the time scroll bar if integer is not enough. */
    protected double _timeFactor = 1.0;

    /** overlap strategy to be used when drawing non-overlapped. */
    protected IOverlapStrategy _overlapStrategy = new DefaultOverlapStrategy(this);

    /** if set to true and using a hierchical model the root will not be shown. */
    protected boolean _hideRoot = false;

    /** insterval selection strategy. */
    protected IIntervalSelectionStrategy _intervalSelectionStrategy = new DefaultIntervalSelectionStrategy();

    /** true if selecting from regions is enabled. */
    protected boolean _regionRectEnabled = false;

    /** region selection data: current selection. */
    protected TBRect _regionSelection = null;
    /** region selection data: last selection. */
    protected TBRect _lastRegionSelection = null;
    /** start date when selecting a region. */
    protected JaretDate _regionStartDate;
    /** start row when selecting a reion. */
    protected TimeBarRow _regionStartRow;

    /**
     * Constructor will set the TimeBarViewerInterface for the delegate.
     * 
     * @param tbvi the viewer as TimeBarViewerInterface
     */
    public TimeBarViewerDelegate(TimeBarViewerInterface tbvi) {
        _tbvi = tbvi;
        // the delegate listens to selection changes
        _selectionModel.addTimeBarSelectionListener(this);

        // initialize the viewstate
        _timeBarViewState = new DefaultTimeBarViewState(this);
        _timeBarViewState.setDefaultRowHeight(TimeBarViewerInterface.DEFAULT_ROWHEIGHT);

        // register a listener on the viewstate to react on changes
        _timeBarViewState.addTimeBarViewStateListener(new ITimeBarViewStateListener() {
            public void rowHeightChanged(TimeBarRow row, int newHeight) {
                if (_tbvi != null) {
                    updateRowScrollBar();
                    _tbvi.repaint();
                }
            }

            public void viewStateChanged() {
                if (_tbvi != null) {
                    updateRowScrollBar();
                    _tbvi.repaint();
                }
            }
        });

    }

    /**
     * Deregister from all models and free any ressources reserved.
     * 
     */
    public void dispose() {
        if (_model != null) {
            _model.remTimeBarModelListener(this);
        }
        if (_selectionModel != null) {
            _selectionModel.remTimeBarSelectionListener(this);
        }
        if (_markers != null) {
            for (TimeBarMarker marker : _markers) {
                marker.remTimeBarMarkerListener(this);
            }
        }
        if (_rowFilter != null) {
            _rowFilter.removePropertyChangeListener(this);
        }
        if (_rowSorter != null) {
            _rowSorter.removePropertyChangeListener(this);
        }
        if (_focussedIntervalListeners != null) {
            _focussedIntervalListeners.clear();
        }
        if (_selectionRectListeners != null) {
            _selectionRectListeners.clear();
        }
        if (_timeBarChangeListeners != null) {
            _timeBarChangeListeners.clear();
        }
    }

    /**
     * Checks whether a given date is currently visible.
     * 
     * @param date the date to be checked
     * @return true if the date is within the visible area of the diagram.
     */
    public boolean isDisplayed(JaretDate date) {
        return _startDate.compareTo(date) <= 0 && (_endDate == null || _endDate.compareTo(date) >= 0);
    }

    /**
     * Set a filter to select a subset of rows in the model to be displayed. The model itself will go unaffected.
     * 
     * @param rowFilter TimeBarRowFilter to be used. <code>null</code> is an allowed value indicating no row filtering
     */
    public void setRowFilter(TimeBarRowFilter rowFilter) {
        if ((_rowFilter == null && rowFilter != null) || (_rowFilter != null && !_rowFilter.equals(rowFilter))) {
            TimeBarRowFilter oldFilter = _rowFilter;
            if (oldFilter != null) { // dregsiter prop change
                oldFilter.removePropertyChangeListener(this);
            }
            _rowFilter = rowFilter;
            if (_rowFilter != null) { // register for prop changes
                _rowFilter.addPropertyChangeListener(this);
            }
            updateRowList();
            if (_tbvi != null) {
                _tbvi.repaint();
                _tbvi.firePropertyChangeX(TimeBarViewerInterface.PROPERTYNAME_ROWFILTER, oldFilter, rowFilter);
            }
        }
    }

    /**
     * @return Returns the rowFilter.
     */
    public TimeBarRowFilter getRowFilter() {
        return _rowFilter;
    }

    /**
     * Check whether a row is filtered out.
     * 
     * @param row row to check.
     * @return true if the row is filtered out.
     */
    public boolean isFiltered(TimeBarRow row) {
        if (_rowFilter == null) {
            return false;
        }
        return (!_rowFilter.isInResult(row));
    }

    /**
     * Set a sorter for sorting the displayed rows. The model itself will not be affected.
     * 
     * @param rowSorter TimeBarRowSorter to be used. <code>null</code> is an allowed value indicating no special sorting
     */
    public void setRowSorter(TimeBarRowSorter rowSorter) {
        if ((_rowSorter == null && rowSorter != null) || (_rowSorter != null && !_rowSorter.equals(rowSorter))) {
            TimeBarRowSorter oldSorter = _rowSorter;
            if (oldSorter != null) { // deregister
                oldSorter.removePropertyChangeListener(this);
            }
            _rowSorter = rowSorter;
            if (_rowSorter != null) {
                _rowSorter.addPropertyChangeListener(this);
            }
            updateRowList();
            if (_tbvi != null) {
                _tbvi.repaint();
                _tbvi.firePropertyChangeX(TimeBarViewerInterface.PROPERTYNAME_ROWSORTER, oldSorter, rowSorter);
            }
        }
    }

    /**
     * @return Returns the rowSorter.
     */
    public TimeBarRowSorter getRowSorter() {
        return _rowSorter;
    }

    /**
     * Set a filter for displaying only a part of the intervals. The model itself will not be affected.
     * 
     * @param intervalFilter TimeBarIntervalFilter to be used. <code>null</code> is an allowed value indicating no
     * interval filtering
     */
    public void setIntervalFilter(TimeBarIntervalFilter intervalFilter) {
        if ((_intervalFilter == null && intervalFilter != null)
                || (_intervalFilter != null && !_intervalFilter.equals(intervalFilter))) {
            TimeBarIntervalFilter oldFilter = _intervalFilter;
            if (oldFilter != null) {
                oldFilter.removePropertyChangeListener(this);
            }
            _intervalFilter = intervalFilter;
            if (_intervalFilter != null) {
                _intervalFilter.addPropertyChangeListener(this);
                // reset the overlap cache!
                _overlapStrategy.clearCachedData();
            }
            _tbvi.repaint();
            _tbvi.firePropertyChangeX(TimeBarViewerInterface.PROPERTYNAME_INTERVALFILTER, oldFilter, intervalFilter);
        }
    }

    /**
     * @return Returns the intervalFilter.
     */
    public TimeBarIntervalFilter getIntervalFilter() {
        return _intervalFilter;
    }

    /**
     * Check whether an interval is filtered by an interval filter.
     * 
     * @param interval interval to check.
     * @return true if the interval is filtered.
     */
    public boolean isFiltered(Interval interval) {
        if (_intervalFilter == null) {
            return false;
        }
        return !_intervalFilter.isInResult(interval);
    }

    /**
     * Updates the shadow row list of the displayed rows. The method will update the row axis scrollbar (x or y
     * depending on the orientation) in the case that the number of rows changed.
     */
    public void updateRowList() {
        if (_model != null) {
            int oldRowCount = _rowList != null ? _rowList.size() : 0;
            List<TimeBarRow> newRowList = new ArrayList<TimeBarRow>();
            // copy filtered if filter is set
            for (int r = 0; r < _model.getRowCount(); r++) {
                if (_rowFilter != null) {
                    // filter set
                    if (_rowFilter.isInResult(_model.getRow(r))) {
                        newRowList.add(_model.getRow(r));
                    }
                } else {
                    newRowList.add(_model.getRow(r));
                }
            }
            // sorter set? -> sort the row list
            if (_rowSorter != null) {
                Collections.sort(newRowList, _rowSorter);
            }
            // set the rowlist
            // TODO might be necessary to do this synchronized against an ongoing paint
            _rowList = newRowList;

            if (getRowCount() != oldRowCount && _tbvi != null) {
                updateRowScrollBar(); // method switches to the right bar
            }
        }
    }

    /**
     * Get a timebar row from the filtered/sorted list by index.
     * 
     * @param idx index in the list
     * @return the TimeBarRow at the index
     */
    public TimeBarRow getRow(int idx) {
        return _rowList.get(idx);
    }

    /**
     * Retrieve the index of a given row.
     * 
     * @param row row in question
     * @return the index or -1 if the row could not be found
     */
    public int getRowIndex(TimeBarRow row) {
        return _rowList.indexOf(row);
    }

    /**
     * Return the size of the row list.
     * 
     * @return the actual row count of the possibly filtered list of rows
     */
    public int getRowCount() {
        return _rowList != null ? _rowList.size() : 0;
    }

    /**
     * Calculates the number of seconds the complete model spans. If min or max date is not set, the number of seconds
     * is 0.
     * 
     * @return number of seconds
     */
    public long getTotalSeconds() {
        if (getMaxDate() == null || getMinDate() == null) {
            return 0;
        }
        return getMaxDate().diffSecondsL(getMinDate());
    }

    /**
     * Calculates the number of milliseconds the complete model spans.
     * 
     * @return number of milliseconds
     */
    public long getTotalMilliSeconds() {
        return getMaxDate().diffMilliSeconds(getMinDate());
    }

    /**
     * Get the seconds currently displayed by the diagram.
     * 
     * @return the number of seconds currently displayed by the diagram geometry
     */
    public int getSecondsDisplayed() {
        if (_orientation == Orientation.HORIZONTAL) {
            if (!_variableXScale) {
                return (int) ((double) (_diagramRect.width) / _pixelPerSeconds);
            } else {
                int endx = _diagramRect.width;
                JaretDate endDate = dateForCoord(endx);
                return endDate.diffSeconds(_startDate);
            }
        } else {
            if (!_variableXScale) {
                return (int) ((double) (_diagramRect.height) / _pixelPerSeconds);
            } else {
                int endy = _diagramRect.height;
                JaretDate endDate = dateForCoord(endy);
                return endDate.diffSeconds(_startDate);
            }
        }
    }

    /**
     * Get the milli seconds currently displayed by the diagram.
     * 
     * @return the number of milli seconds currently displayed by the diagram geometry
     */
    public long getMilliSecondsDisplayed() {
        if (_orientation == Orientation.HORIZONTAL) {
            if (!_variableXScale) {
                return (int) ((double) _diagramRect.width / getPixelPerMilliSecond());
            } else {
                int endx = _diagramRect.width;
                JaretDate endDate = dateForX(endx);
                return endDate.diffMilliSeconds(_startDate);
            }
        } else {
            if (!_variableXScale) {
                return (int) ((double) _diagramRect.height / getPixelPerMilliSecond());
            } else {
                int endx = _diagramRect.height;
                JaretDate endDate = dateForX(endx);
                return endDate.diffMilliSeconds(_startDate);
            }
        }
    }

    /**
     * @return Returns the selectionModel.
     */
    public TimeBarSelectionModel getSelectionModel() {
        return _selectionModel;
    }

    /**
     * @param selectionModel The selectionModel to set.
     */
    public void setSelectionModel(TimeBarSelectionModel selectionModel) {
        if (_selectionModel != null) {
            _selectionModel.remTimeBarSelectionListener(this);
        }
        _selectionModel = selectionModel;
        _selectionModel.addTimeBarSelectionListener(this);
        _tbvi.repaint();
    }

    /**
     * @return Returns the adjustMinMaxDatesByModel.
     */
    public boolean getAdjustMinMaxDatesByModel() {
        return _adjustMinMaxDatesByModel;
    }

    /**
     * @param adjustMinMaxDatesByModel The adjustMinMaxDatesByModel to set.
     */
    public void setAdjustMinMaxDatesByModel(boolean adjustMinMaxDatesByModel) {
        _adjustMinMaxDatesByModel = adjustMinMaxDatesByModel;
    }

    /**
     * @return Returns the maxDate.
     */
    public JaretDate getMaxDate() {
        return _maxDate;
    }

    /**
     * Set the maximum date for the diagram.
     * 
     * @param maxDate The maxDate to set.
     */
    public void setMaxDate(JaretDate maxDate) {
        // add a padding so that elements sitting on the edge are clearly
        // selectable
        long milliseconds = (int) ((double) PADDING_PIXEL / (_pixelPerSeconds * MILLISCALING));
        JaretDate oldVal = _maxDate;
        _maxDate = maxDate.copy();
        _maxDate.advanceMillis(milliseconds);
        if (oldVal == null || !oldVal.equals(maxDate)) {
            updateTimeScrollBar();
            if (_tbvi != null) {
                _tbvi.repaint();
                _tbvi.firePropertyChangeX(TimeBarViewerInterface.PROPERTYNAME_MAXDATE, oldVal, maxDate);
            }
        }
    }

    /**
     * @return Returns the minDate.
     */
    public JaretDate getMinDate() {
        return _minDate;
    }

    /**
     * @param minDate The minDate to set.
     */
    public void setMinDate(JaretDate minDate) {
        // add a padding so that elements sitting on the edge are clearly
        // selectable
        long milliseconds = (int) ((double) PADDING_PIXEL / (_pixelPerSeconds * MILLISCALING));
        JaretDate oldVal = _minDate;
        _minDate = minDate.copy();
        _minDate.advanceMillis(-milliseconds);
        if (oldVal == null || !oldVal.equals(minDate)) {
            updateTimeScrollBar();
            if (_tbvi != null) {
                _tbvi.repaint();
                _tbvi.firePropertyChangeX(TimeBarViewerInterface.PROPERTYNAME_MINDATE, oldVal, minDate);
            }
        }
    }

    /**
     * Get the starting date, that is the leftmost date displayed.
     * 
     * @return Returns the startDate.
     */
    public synchronized JaretDate getStartDate() {
        return _startDate;
    }

    /**
     * Sets the date to be displayed at the position of the yaxis.
     * 
     * @param startDate The startDate to set.
     */
    public synchronized void setStartDate(JaretDate startDate) {
        _lastStartDate = _startDate;
        JaretDate oldVal = _startDate;
        _startDate = startDate.copy();

        // if the set was a real change, update and tell everyone that is
        // interested
        if ((oldVal == null || !oldVal.equals(_startDate)) && _tbvi != null) {
            if (_diagramRect != null) {
                _endDate = dateForCoord(_diagramRect.x + _diagramRect.width);
            }
            updateTimeScrollBar();
            _tbvi.repaint();
            _tbvi.firePropertyChangeX(TimeBarViewerInterface.PROPERTYNAME_STARTDATE, oldVal, _startDate);
        }
    }

    /**
     * Handle a scrolling operation.
     * 
     * @param startDate new start date
     */
    protected void scrollTo(JaretDate startDate) {
        if (!_optimizeScrolling || _tbvi == null) {
            setStartDate(startDate);
        } else {
            try {
                _lastStartDate = _startDate;
                JaretDate oldVal = _startDate;
                int oldx = xForDateAbs(_startDate);

                int newx = xForDateAbs(startDate);
                // recalculate the startdate to prevent rounding errors from causing
                // rendering artefacts
                JaretDate newStartDate = dateForCoordAbs(newx);

                int diff = newx - oldx;
                int comp;
                if (_orientation.equals(TimeBarViewerInterface.Orientation.HORIZONTAL)) {
                    comp = _tbvi.getWidth();
                } else {
                    comp = _tbvi.getHeight();
                }
                if (Math.abs(diff) > comp / 2) {
                    setStartDate(startDate);
                    return;
                }

                // delegate the optimized scrolling to the toolkit specific
                // implementation
                if (_orientation == Orientation.HORIZONTAL) {
                    _tbvi.doScrollHorizontal(diff);
                } else {
                    _tbvi.doScrollVertical(diff);
                }
                _startDate = newStartDate;

                // if the set was a real change, update and tell everyone that is
                // interested
                if ((oldVal == null || !oldVal.equals(_startDate)) && _tbvi != null) {
                    if (_diagramRect != null) {
                        _endDate = dateForCoord(_diagramRect.x + _diagramRect.width);
                    }
                    updateTimeScrollBar();
                    _tbvi.firePropertyChangeX(TimeBarViewerInterface.PROPERTYNAME_STARTDATE, oldVal, _startDate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the model the viewer displays.
     * 
     * @return TimeBarModel
     */
    public TimeBarModel getModel() {
        return _model;
    }

    /**
     * Set the model to be displayed.
     * 
     * @param model TimeBarModel to be displayed
     */
    public void setModel(TimeBarModel model) {
        if (_model != null) {
            _model.remTimeBarModelListener(this);
        }
        _model = model;
        if (model != null) {
            // min/max if requested
            if (_adjustMinMaxDatesByModel) {
                // direct manipulation for calculations
                _minDate = _model.getMinDate();
                _maxDate = _model.getMaxDate();
                // now do a proper setting
                setMinDate(_model.getMinDate());
                setMaxDate(_model.getMaxDate());

                setStartDate(_model.getMinDate().copy());
                setMinDate(_model.getMinDate());
                setMaxDate(_model.getMaxDate());
            } else {
                _minDate = new JaretDate();
                _maxDate = new JaretDate();
                setStartDate(new JaretDate());
            }

            if (_hierarchicalModel == null) {
                // if not hierarchical -> forget about the hvs
                _hierarchicalViewState = null;
            }

            _model.addTimeBarModelListener(this);
        } else {
            // model is null
            // make sure we forget the hierarchical viewstate
            _hierarchicalViewState = null;
        }
        updateRowList(); // update the sorted/filtered list
        if (_tbvi != null) {
            updateScrollBars();
            _tbvi.repaint();
        }
    }

    /**
     * Set a hierarchical model as the time bar model.
     * 
     * @param hModel hierarchical model to be displayed
     */
    public void setModel(HierarchicalTimeBarModel hModel) {
        _hierarchicalViewState = new HierarchicalViewStateImpl();
        _hierarchicalModel = hModel;
        TimeBarModel model = null;
        if (hModel != null) {
            model = new StdHierarchicalTimeBarModel(hModel, _hierarchicalViewState);
        }
        setModel(model);
    }

    /**
     * Retrieve the hierarchical model displayed if present.
     * 
     * @return hierarchical model or <code>null</code> if a flat model is used
     */
    public HierarchicalTimeBarModel getHierarchicalModel() {
        return _hierarchicalModel;
    }

    /**
     * Correct the min and max dates of the viewer according to the model.
     */
    private void checkAndAdjustMinMax() {
        if (_adjustMinMaxDatesByModel) {
            setMinDate(_model.getMinDate().copy());
            setMaxDate(_model.getMaxDate().copy());
        }
    }

    /**
     * Sets the scale ox the x axis as pixel per second, thus a value of 1000.0 / (24.0 * 60 * 60) will result in
     * displaying one day over 1000 pixel. The property is a bound property and can be listened to by a
     * PropertyChangeListener.
     * 
     * @param pixelPerSecond pixel per second.
     */
    public void setPixelPerSecond(double pixelPerSecond) {
        setPixelPerSecond(pixelPerSecond, true);
    }

    /**
     * Internal version of setPixelPerSecond that allows control of the repaint behaviour.
     * 
     * @param pixelPerSecond pixel per second
     * @param repaint <code>true</code> if a repaint should be triggered
     */
    protected void setPixelPerSecond(double pixelPerSecond, boolean repaint) {
        if (pixelPerSecond != _pixelPerSeconds) { // check for real difference
            // - we don't want to do too
            // much
            double oldValue = _pixelPerSeconds;
            _pixelPerSeconds = pixelPerSecond;
            if (_variableXScale) {
                updateTimeScaleBreaks();
            }
            if (_tbvi != null) {
                updateTimeScrollBar();
                if (repaint) {
                    _tbvi.repaint(); // repaint the diagram
                }
                _tbvi.firePropertyChange(TimeBarViewerInterface.PROPERTYNAME_PIXELPERSECOND, oldValue, pixelPerSecond);
            }
        }
    }

    /**
     * Recalculate the pps values for breaks in the time scale.
     */
    private void updateTimeScaleBreaks() {
        for (Interval ppsInterval : getPpsRow().getIntervals()) {
            PPSInterval pi = (PPSInterval) ppsInterval;
            if (pi.isBreak()) {
                long millis = pi.getEnd().diffMilliSeconds(pi.getBegin());
                double width = (double) pi.getBreakDisplayWidth();
                double targetPPS = width / ((double) millis / MILLISCALING);
                pi.setPps(targetPPS);
            }
        }
    }

    /**
     * Set the scaling of the x axis by specifying the number of seconds that should be displayed. If the viewer has not
     * been drawn yet, the method delegates to setInitialDiaplyRange. The center parameter will not be taken into
     * account (since it is impossible to do the calculations).
     * 
     * @param seconds number of seconds that will be displayed on the x axis
     * @param center if set to <code>true</code> the center date will be fixed while scaling
     */
    public void setSecondsDisplayed(int seconds, boolean center) {
        if (_orientation.equals(Orientation.HORIZONTAL)) {
            if (_diagramRect != null && _diagramRect.width > 1) {
                double pps = (double) _diagramRect.width / (double) seconds;
                if (!center) {
                    setPixelPerSecond(pps);
                } else {
                    int oldSeconds = getSecondsDisplayed();
                    setPixelPerSecond(pps, false);
                    int newSeconds = getSecondsDisplayed();
                    setStartDate(getStartDate().copy().advanceSeconds((oldSeconds - newSeconds) / 2.0)); // will repaint
                }
            } else {
                // calculation not possible since has not been drawn yet
                setInitialDisplayRange(getStartDate(), seconds);
            }
        } else {
            if (_diagramRect != null && _diagramRect.height > 1) {
                double pps = (double) _diagramRect.height / (double) seconds;
                if (!center) {
                    setPixelPerSecond(pps);
                } else {
                    int oldSeconds = getSecondsDisplayed();
                    setPixelPerSecond(pps, false);
                    int newSeconds = getSecondsDisplayed();
                    setStartDate(getStartDate().copy().advanceSeconds((oldSeconds - newSeconds) / 2.0)); // will repaint
                }
            } else {
                // calculation not possible since has not been drawn yet
                setInitialDisplayRange(getStartDate(), seconds);
            }
        }
    }

    /**
     * Set the scaling of the x axis by specifying the number of seconds that should be displayed. If the viewer has not
     * been drawn yet, the method delegates to setInitialDiaplyRange. The centerDate parameter will not be taken into
     * account (since this is impossible to calculate).
     * 
     * @param seconds number of seconds that will be displayed on the x axis
     * @param centerDate date that will be fixed while scaling
     */
    public void setSecondsDisplayed(int seconds, JaretDate centerDate) {
        if (!isDisplayed(centerDate)) {
            setSecondsDisplayed(seconds, true);
        } else {
            if (_orientation.equals(Orientation.HORIZONTAL)) {
                if (_diagramRect != null && _diagramRect.width > 1) {
                    double pps = (double) _diagramRect.width / (double) seconds;
                    if (centerDate == null) {
                        setPixelPerSecond(pps);
                    } else {
                        // disable optimized scrolling for the operation
                        boolean optimizeScrolling = _optimizeScrolling;
                        _optimizeScrolling = false;

                        int oldx = xForDate(centerDate);
                        // set the new scaling
                        setPixelPerSecond(pps, false);
                        JaretDate dateAtOldPos = dateForCoord(oldx);
                        long diffmsec = centerDate.diffMilliSeconds(dateAtOldPos);

                        setStartDate(getStartDate().copy().advanceMillis(diffmsec)); // will repaint
                        _optimizeScrolling = optimizeScrolling;
                    }
                } else {
                    // calculation not possible since has not been drawn yet
                    setInitialDisplayRange(getStartDate(), seconds);
                }
            } else {
                if (_diagramRect != null && _diagramRect.height > 1) {
                    double pps = (double) _diagramRect.height / (double) seconds;
                    if (centerDate == null) {
                        setPixelPerSecond(pps);
                    } else {
                        // disable optimized scrolling for the operation
                        boolean optimizeScrolling = _optimizeScrolling;
                        _optimizeScrolling = false;

                        int oldx = xForDate(centerDate);
                        // set the new scaling
                        setPixelPerSecond(pps, false);
                        JaretDate dateAtOldPos = dateForCoord(oldx);
                        long diffmsec = centerDate.diffMilliSeconds(dateAtOldPos);

                        setStartDate(getStartDate().copy().advanceMillis(diffmsec)); // will repaint
                        _optimizeScrolling = optimizeScrolling;
                    }
                } else {
                    // calculation not possible since has not been drawn yet
                    setInitialDisplayRange(getStartDate(), seconds);
                }
            }
        }
    }

    /**
     * Return the pixel per second ratio.
     * 
     * @return pixel per second
     */
    public double getPixelPerSecond() {
        return _pixelPerSeconds;
    }

    /**
     * Retrieve the corrected pixel per second value for milliseconds.
     * 
     * @return pixel per millisecond
     */
    protected double getPixelPerMilliSecond() {
        return _pixelPerSeconds / MILLISCALING;
    }

    protected JaretDate _initialStartDate;
    protected int _initialSecondsDisplayed;

    /**
     * Set a date range and scaling that will be set as the initial display right after the viewer is displayed.
     * 
     * @param startDate start date
     * @param secondsDisplayed seconds to be displayed in the viewer
     */
    public void setInitialDisplayRange(JaretDate startDate, int secondsDisplayed) {
        _initialStartDate = startDate;
        _initialSecondsDisplayed = secondsDisplayed;
    }

    /**
     * Set the height for the rows in pixel. This property is bound. If the orientation is vertical this sets the width
     * of the columns. If the viewstate is configured to use individual row heights, this will set the default row
     * height.
     * 
     * @param rowHeight new row height or default row height
     */
    public void setRowHeight(int rowHeight) {
        if (rowHeight != _timeBarViewState.getDefaultRowHeight()) { // check for change
            int oldVal = _timeBarViewState.getDefaultRowHeight();
            _timeBarViewState.setDefaultRowHeight(rowHeight);
            if (_tbvi != null) {
                _tbvi.repaint();
                updateRowScrollBar();
                if (_tbvi != null) {
                    _tbvi.firePropertyChange(TimeBarViewerInterface.PROPERTYNAME_ROWHEIGHT, oldVal, rowHeight);
                }
            }
        }
    }

    /**
     * Retrieve the current row height (col width). If individual row heights are used this will return the default row
     * height.
     * 
     * @return row height in pixel or default row height
     */
    public int getRowHeight() {
        return _timeBarViewState.getDefaultRowHeight();
    }

    /**
     * Retrieve the last date displayed.
     * 
     * @return last date displayed
     */
    public JaretDate getEndDate() {
        return _endDate;
    }

    /**
     * Add a marker to be displayed within the diagram.
     * 
     * @param marker TimeBarMarkerImpl to be displayed in the diagram
     */
    public void addMarker(TimeBarMarker marker) {
        if (_markers == null) {
            _markers = new ArrayList<TimeBarMarker>();
        }
        _markers.add(marker);
        marker.addTimeBarMarkerListener(this);
        if (isDisplayed(marker.getDate())) {
            // MAYBE optimize
            _tbvi.repaint();
        }
        if (_minDate != null && marker.getDate().compareTo(_minDate) < 0) {
            setMinDate(marker.getDate().copy());
        } else if (_maxDate != null && marker.getDate().compareTo(_maxDate) > 0) {
            setMaxDate(marker.getDate().copy());
        }
    }

    /**
     * Removes a marker from the diagram.
     * 
     * @param marker TimeBarMarkerImpl to be removed
     */
    public void remMarker(TimeBarMarker marker) {
        if (_markers != null) {
            _markers.remove(marker);
            marker.remTimeBarMarkerListener(this);
            if (isDisplayed(marker.getDate())) {
                // MAYBE optimize
                _tbvi.repaint();
            }
        }
    }

    /**
     * Retrive a marker for a coordinate pair (x or y will be used depending on the orientation).
     * 
     * @param x coordinate
     * @param y coordinate
     * @return marker or <code>null</code>
     */
    public TimeBarMarker getMarkerForXY(int x, int y) {
        if (_orientation == Orientation.HORIZONTAL) {
            return getMarkerForCoord(x);
        } else {
            return getMarkerForCoord(y);
        }
    }

    /**
     * Find a marker for a given coordinate (depending on the orientation).
     * 
     * @param coord coordinate
     * @return a marker or null if none was found
     */
    protected TimeBarMarker getMarkerForCoord(int coord) {
        TimeBarMarker result = null;
        if (_markers != null) {
            for (int i = 0; i < _markers.size(); i++) {
                TimeBarMarker marker = (TimeBarMarker) _markers.get(i);
                int mx = xForDate(marker.getDate());
                if (coord - _selectionDelta < mx && coord + _selectionDelta > mx) {
                    result = marker;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Retrieve all markers added.
     * 
     * @return List of markers.
     */
    public List<TimeBarMarker> getMarkers() {
        return _markers;
    }

    /**
     * Add a list of markers.
     * 
     * @param markers list of markers
     */
    public void addMarkers(List<TimeBarMarker> markers) {
        if (markers != null) {
            for (TimeBarMarker marker : markers) {
                addMarker(marker);
            }
        }
    }

    /**
     * Update the scroll bar bounds to match the current display an data.
     * 
     */
    public void updateScrollBars() {
        if (_model != null && _model.getRowCount() != 0) {
            updateTimeScrollBar();
            updateRowScrollBar();
        }
    }

    /**
     * Update the time scroll bar after a change in the data or presentation of the viewer.
     */
    private void updateTimeScrollBar() {
        if (_tbvi != null && _model != null) {
            if (isMilliAccuracy()) {
                long milliSecondsDisplayed = getMilliSecondsDisplayed();
                long totalMilliSeconds = getTotalMilliSeconds();
                long pos = _startDate.diffMilliSeconds(_minDate);

                // prevent thumb from disappearing if scrolled
                if (totalMilliSeconds - pos < milliSecondsDisplayed) {
                    milliSecondsDisplayed = totalMilliSeconds - pos;
                }

                // correct the number of seconds otally displayed for use with the scrollbar
                // since the scrollbar spans the y axis
                int addWidth = _yAxisWidth + _hierarchyWidth;
                long addMillis;
                if (_orientation.equals(TimeBarViewerInterface.Orientation.HORIZONTAL)) {
                    addMillis = dateForCoord(_diagramRect.x + _diagramRect.width).diffMilliSeconds(
                            dateForCoord(_diagramRect.x + _diagramRect.width - addWidth));
                } else {
                    addMillis = dateForCoord(_diagramRect.y + _diagramRect.height).diffMilliSeconds(
                            dateForCoord(_diagramRect.y + _diagramRect.height - addWidth));
                }

                updateTimeScrollBar((int) totalMilliSeconds, (int) pos, (int) (milliSecondsDisplayed + addMillis));
            } else {
                // if using standard (=second) accuracy scrolling is done in
                // seconds
                long secondsDisplayed = getSecondsDisplayed();
                long totalSeconds = getTotalSeconds();
                long pos = _startDate.diffSecondsL(_minDate);

                // prevent thumb from disappearing if scrolled
                if (totalSeconds - pos < secondsDisplayed) {
                    secondsDisplayed = totalSeconds - pos;
                }

                // correct the number of seconds totally displayed for use with the scrollbar
                // since the scrollbar spans the y axis
                int addWidth = _yAxisWidth + _hierarchyWidth;
                int addSeconds = 0;
                if (_variableXScale) {
                    if (_orientation.equals(TimeBarViewerInterface.Orientation.HORIZONTAL)) {
                        addSeconds = dateForCoord(_diagramRect.x + _diagramRect.width).diffSeconds(
                                dateForCoord(_diagramRect.x + _diagramRect.width - addWidth));
                    } else {
                        addSeconds = dateForCoord(_diagramRect.y + _diagramRect.height).diffSeconds(
                                dateForCoord(_diagramRect.y + _diagramRect.height - addWidth));
                    }
                }
                int tot = 0;
                int p = 0;
                int displayed = 0;

                if (totalSeconds < Integer.MAX_VALUE) {
                    tot = (int) totalSeconds;
                    p = (int) pos;
                    displayed = (int) (secondsDisplayed + addSeconds);
                    _timeFactor = 1.0;
                } else {
                    // integer is not sufficient
                    _timeFactor = (double) Integer.MAX_VALUE / (double) totalSeconds;
                    tot = (int) ((double) totalSeconds * _timeFactor);
                    p = (int) ((double) pos * _timeFactor);
                    displayed = (int) ((double) (secondsDisplayed + addSeconds) * _timeFactor);
                }

                updateTimeScrollBar(tot, p, displayed);
                // updateTimeScrollBar(totalSeconds, pos, secondsDisplayed + addSeconds);
            }
        }
    }

    /**
     * Diapatch updating of the time scrollbar according to orientation.
     * 
     * @param max max value
     * @param pos position
     * @param secondsDisplayed thumb
     */
    private void updateTimeScrollBar(int max, int pos, int secondsDisplayed) {
        if (_orientation == Orientation.HORIZONTAL) {
            _tbvi.updateXScrollBar(max, pos, secondsDisplayed);
        } else if (_orientation == Orientation.VERTICAL) {
            _tbvi.updateYScrollBar(max, pos, secondsDisplayed);
        }
    }

    /**
     * Handle operation of the horizontal scroll bar.
     * 
     * @param value new value
     * @param redirect if true allow redirecteion to the vertical scroll according to orientation
     */
    public void handleHorizontalScroll(int value, boolean redirect) {
        if (!redirect || _orientation == Orientation.HORIZONTAL) {
            if (getMinDate() == null) {
                return;
            }
            JaretDate date = getMinDate().copy();
            if (isMilliAccuracy()) {
                date.advanceMillis((long) value);
            } else {
                date.advanceSeconds(value / _timeFactor);
            }
            scrollTo(date);
        } else if (_orientation == Orientation.VERTICAL) {
            handleVerticalScroll(value, false);
        } else {
            throw new RuntimeException("illegal");
        }
    }

    /**
     * Update the row (column) scroll bar of the viewer.
     * 
     */
    private void updateRowScrollBar() {
        int first = getFirstRow();
        // TODO still a minimal glitch with the scrollbar appearance
        updateRowScrollBar(getTotalHeight(), getAbsPosForRow(first) + _firstRowPixelOffset, _diagramRect.height);
    }

    /**
     * Update the scroll bar controlling the rows.
     * 
     * @param max max value
     * @param pos current value
     * @param thumbSize thumb size
     */
    private void updateRowScrollBar(int max, int pos, int thumbSize) {
        if (_orientation == Orientation.HORIZONTAL) {
            _tbvi.updateYScrollBar(max, pos, thumbSize);
        } else if (_orientation == Orientation.VERTICAL) {
            _tbvi.updateXScrollBar(max, pos, thumbSize);
        }
    }

    /**
     * Calculate the row index for an absolute pixel position (referring to all rows stacked).
     * 
     * @param value absolute position in the imagined all rows display
     * @return the index of the row for the given position
     */
    public int getRowIdxForAbsoluteOffset(int value) {
        if (!_timeBarViewState.getUseVariableRowHeights()) {
            int row = value / _timeBarViewState.getDefaultRowHeight();
            return row;
        } else {
            int y = 0;
            TimeBarRow row = getRow(0);
            int height = _timeBarViewState.getRowHeight(row);
            for (int i = 0; i < _rowList.size(); i++) {
                if (y <= value && value <= y + height) {
                    return i;
                }
                y += height;
                if (i + 1 > _rowList.size() - 1) {
                    break;
                }
                row = getRow(i + 1);
                height = _timeBarViewState.getRowHeight(row);
            }
            // should never happen
            throw new RuntimeException("could not find row idx for offset");
        }
    }

    /**
     * Calculate the pixel offset of the first row for an absolute value (referring to the total height of all rows).
     * 
     * @param rowIdx index of the first row
     * @param value absolute value (height/width offset)
     * @return pixeloffset (to be used as the pixel offset of the first row)
     */
    public int getRowPixOffsetForAbsoluteOffset(int rowIdx, int value) {
        if (!_timeBarViewState.getUseVariableRowHeights()) {
            int off = value % _timeBarViewState.getDefaultRowHeight();
            return off;
        } else {
            int y = getAbsPosForRow(rowIdx);
            return value - y;
        }
    }

    /**
     * Calculate the total height/width of all rows.
     * 
     * @return the total height/row size
     */
    public int getTotalHeight() {
        if (!_timeBarViewState.getUseVariableRowHeights()) {
            return getRowCount() * _timeBarViewState.getDefaultRowHeight();
        } else {
            int h = 0;
            for (int i = 0; i < _rowList.size(); i++) {
                TimeBarRow row = getRow(i);
                h += _timeBarViewState.getRowHeight(row);
            }
            return h;
        }
    }

    /**
     * Get the absolute position (x or y depeding on the orientation) for a row.
     * 
     * @param rowIdx inde of the row
     * @return absolute begin position of the row
     */
    public int getAbsPosForRow(int rowIdx) {
        if (!_timeBarViewState.getUseVariableRowHeights()) {
            return rowIdx * _timeBarViewState.getDefaultRowHeight();
        } else {
            int h = 0;
            for (int i = 0; i < rowIdx; i++) {
                TimeBarRow row = getRow(i);
                h += _timeBarViewState.getRowHeight(row);
            }
            return h;
        }
    }

    /**
     * Handle change of the vertical scroll bar.
     * 
     * @param value new value of the scroll bar
     * @param redirect allow redirection to the horizontal scroll handling according to orientation
     */
    public void handleVerticalScroll(int value, boolean redirect) {
        if (!redirect || _orientation == Orientation.HORIZONTAL) {
            int row = getRowIdxForAbsoluteOffset(value);
            int offset = getRowPixOffsetForAbsoluteOffset(row, value);
            setFirstRow(row, offset);
        } else if (_orientation == Orientation.VERTICAL) {
            handleHorizontalScroll(value, false);
        } else {
            throw new RuntimeException("illegal");
        }
    }

    /**
     * Retrieve the date for a position on the screen. According to the orientation x or y will be used.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return date for the position
     */
    public JaretDate dateForXY(int x, int y) {
        if (_startDate == null) {
            return null;
        }
        int coord;
        if (_orientation == Orientation.HORIZONTAL) {
            coord = x;
        } else {
            coord = y;
        }
        if (!_variableXScale) {
            return dateForCoordPlain(coord);
        } else {
            return dateForCoordVariable(coord);
        }

    }

    /**
     * Calculate the date for a given x coordinate (or y in case of vertical orientation).
     * 
     * @param x the x coordinate
     * @return the JaretDate for the given x location or <code>null</code> if no start date has been set
     * @deprecated use dateForXY or dateForCoord instead
     */
    public JaretDate dateForX(int x) {
        if (_startDate == null) {
            return null;
        }
        if (!_variableXScale) {
            return dateForCoordPlain(x);
        } else {
            return dateForCoordVariable(x);
        }
    }

    /**
     * Calculate the date for a given coordinate on the timescale.
     * 
     * @param coord the coordinate
     * @return the JaretDate for the given location or <code>null</code> if no start date has been set
     */
    public JaretDate dateForCoord(int coord) {
        if (_startDate == null) {
            return null;
        }
        if (!_variableXScale) {
            return dateForCoordPlain(coord);
        } else {
            return dateForCoordVariable(coord);
        }
    }

    /**
     * Calculate the date for a given point.
     * 
     * @param x x
     * @param y y
     * @return date or null
     */
    public JaretDate dateForCoord(int x, int y) {
        if (_orientation == Orientation.HORIZONTAL) {
            return dateForCoord(x);
        } else {
            return dateForCoord(y);
        }
    }

    /**
     * Plain date for coordinate (x or y deppending on orientation) function. (no variable scale)
     * 
     * @param coord coordinate
     * @return date for the coordinate along the timescale
     */
    private JaretDate dateForCoordPlain(int coord) {
        long pixDif = coord - (_yAxisWidth + _hierarchyWidth);
        long diffMilliSec = (long) ((double) pixDif / _pixelPerSeconds * MILLISCALING);
        JaretDate date = new JaretDate(_startDate);
        date.advanceMillis(diffMilliSec);
        return date;
    }

    /**
     * Retrieve the date for an absolute coordinate along the timescale (referencing minDate).
     * 
     * @param coord xposition
     * @return date for the position along the time axis
     */
    public JaretDate dateForCoordAbs(int coord) {
        if (!_variableXScale) {
            return dateForCoordAbsPlain(coord);
        } else {
            return dateForCoordAbsVariable(coord);
        }
    }

    /**
     * Retrieve the date for an absolute coordinate along the timescale (referencing minDate).
     * 
     * @param coord xposition
     * @return date for the position along the time axis
     */
    private JaretDate dateForCoordAbsPlain(int coord) {
        long pixDif = coord - (_yAxisWidth + _hierarchyWidth);
        JaretDate date = new JaretDate(_minDate);
        if (_milliAccuracy) {
            long diffMilliSec = (long) ((double) pixDif / _pixelPerSeconds * MILLISCALING);
            date.advanceMillis(diffMilliSec);
        } else {
            long diffSec = (long) ((double) pixDif / _pixelPerSeconds);
            date.advanceSeconds(diffSec);
        }
        return date;
    }

    /**
     * Retrieve the date for an absolute coordinate along the timescale (referencing minDate).
     * 
     * @param coord xposition
     * @return date for the position along the time axis
     */
    private JaretDate dateForCoordAbsVariable(int coord) {
        int pixDif = coord - (_yAxisWidth + _hierarchyWidth);

        // variable scale
        int pixelToGo = pixDif;
        if (pixDif <= 0) {
            // might be possible, does not matter
            return dateForCoordPlain(coord);
        }
        long milliSeconds = 0;
        JaretDate d = _minDate.copy();

        while (pixelToGo > 0) {
            PPSInterval interval = getPPSInterval(d);

            double pps = 0;
            JaretDate endPPS;
            boolean noFollowingPPSInterval = false;
            if (interval == null) {
                // default pps;
                pps = _pixelPerSeconds;
                Interval i = nextPPSInterval(d);
                if (i != null) {
                    endPPS = i.getBegin().copy();
                } else {
                    noFollowingPPSInterval = true;
                    endPPS = getMaxDate().copy();
                }
            } else {
                pps = interval.getPps();
                endPPS = interval.getEnd().copy();
            }
            long milliSecondsToEndPPS = endPPS.diffMilliSeconds(d);
            int pixelToEndPPS = (int) Math.round(((double) milliSecondsToEndPPS * pps / MILLISCALING));

            if (pixelToGo <= pixelToEndPPS || noFollowingPPSInterval) {
                // all in this section or last section
                milliSeconds = milliSeconds + (long) ((double) pixelToGo / pps * MILLISCALING);
                JaretDate result = _minDate.copy().advanceMillis(milliSeconds);
                return result; // finished
            } else {
                // section
                long ms = endPPS.diffMilliSeconds(d);
                milliSeconds = milliSeconds + ms;
                pixelToGo = pixelToGo - pixelToEndPPS;
                d = endPPS.copy();
            }
        }
        return null;
    }

    /**
     * Date for coordinate along the y axis taking variable scale into account.
     * 
     * @param coord coordinate
     * @return date
     */
    private JaretDate dateForCoordVariable(int coord) {
        int pixDif = coord - (_yAxisWidth + _hierarchyWidth);

        // variable scale
        int pixelToGo = pixDif;
        if (pixDif <= 0) {
            // might be possible, does not matter
            return dateForCoordPlain(coord);
        }
        long milliSeconds = 0;
        JaretDate d = _startDate.copy();

        while (pixelToGo > 0) {
            PPSInterval interval = getPPSInterval(d);

            double pps = 0;
            JaretDate endPPS;
            boolean noFollowingPPSInterval = false;
            if (interval == null) {
                // default pps;
                pps = _pixelPerSeconds;
                Interval i = nextPPSInterval(d);
                if (i != null) {
                    endPPS = i.getBegin().copy();
                } else {
                    noFollowingPPSInterval = true;
                    endPPS = getMaxDate().copy();
                }
            } else {
                pps = interval.getPps();
                endPPS = interval.getEnd().copy();
            }
            long milliSecondsToEndPPS = endPPS.diffMilliSeconds(d);
            int pixelToEndPPS = (int) Math.round(((double) milliSecondsToEndPPS * pps / MILLISCALING));

            if (pixelToGo <= pixelToEndPPS || noFollowingPPSInterval) {
                // all in this section or last section
                milliSeconds = milliSeconds + (long) ((double) pixelToGo / pps * MILLISCALING);
                JaretDate result = _startDate.copy().advanceMillis(milliSeconds);
                return result; // finished
            } else {
                // section
                long ms = endPPS.diffMilliSeconds(d);
                milliSeconds = milliSeconds + ms;
                pixelToGo = pixelToGo - pixelToEndPPS;
                d = endPPS.copy();
            }
        }
        return null;
    }

    /**
     * Calculate the x coordinate for a given date.
     * 
     * @param date JaretDate for which the coordinate is to be calculated
     * @return x coordinate in the current view
     */
    public int xForDate(JaretDate date) {
        if (!_variableXScale) {
            return xForDatePlain(date, false);
        } else {
            return xForDateVariable(date, false);
        }
    }

    /**
     * Calculate the x coordinate for a given date as absolute xvalue beginning with minDate = 0.
     * 
     * @param date JaretDate for which the coordinate is to be calculated
     * @return x coordinate
     */
    public int xForDateAbs(JaretDate date) {
        if (!_variableXScale) {
            return xForDatePlain(date, true);
        } else {
            return xForDateVariable(date, true);
        }
    }

    /**
     * No variable xscale calculation of x for for date.
     * 
     * @param date date
     * @param absolute if set to true absolute x value referencing minDate
     * @return x for date
     */
    protected int xForDatePlain(JaretDate date, boolean absolute) {
        int offset = _orientation.equals(TimeBarViewerInterface.Orientation.HORIZONTAL) ? _offsetLeft : _offsetTop;
        long milliSeconds;
        if (!absolute) {
            milliSeconds = date.diffMilliSeconds(_startDate);
        } else {
            milliSeconds = date.diffMilliSeconds(_minDate);
        }
        // we use Math.round() to get the rounding errors as small as
        // possible
        int x = _yAxisWidth + _hierarchyWidth + offset
                + (int) Math.round((((double) milliSeconds) * _pixelPerSeconds / MILLISCALING));
        return x;
    }

    /**
     * Variable calculation of x for date.
     * 
     * @param date date
     * @param absolute if set to true absolute x value referencing minDate
     * @return x for date
     */
    protected int xForDateVariable(JaretDate date, boolean absolute) {
        // TODO milliseconds may be overflowing
        long milliSeconds;
        if (!absolute) {
            milliSeconds = date.diffMilliSeconds(_startDate);
        } else {
            milliSeconds = date.diffMilliSeconds(_minDate);
        }
        boolean neg = false;
        if (milliSeconds <= 0) {
            if (absolute) {
                return 0;
            }
            // when not doing absolute calculation and the requested date is before x = 0
            // calculate abs value and correct afterwards
            neg = true;
            milliSeconds = date.diffMilliSeconds(_minDate);
        }

        long milliSecondsToGo = milliSeconds;
        JaretDate d = (absolute || neg) ? _minDate.copy() : _startDate.copy();
        int x = 0;
        while (milliSecondsToGo > 0) {
            PPSInterval interval = getPPSInterval(d);

            double pps = 0;
            JaretDate endPPS;
            boolean noFollowingPPSInterval = false;
            if (interval == null) {
                // default pps;
                pps = _pixelPerSeconds;
                Interval i = nextPPSInterval(d);
                if (i != null) {
                    endPPS = i.getBegin().copy();
                } else {
                    noFollowingPPSInterval = true;
                    endPPS = getMaxDate().copy();
                }
            } else {
                pps = interval.getPps();
                endPPS = interval.getEnd().copy();
            }
            if (date.compareTo(endPPS) <= 0 || noFollowingPPSInterval) {
                // all in this section or last section
                x = x + (int) Math.round(((double) milliSecondsToGo * pps / MILLISCALING));
                if (absolute) {
                    return x;
                }
                // TODO insets
                if (neg) {
                    // correct x value
                    int firstOff = xForDateAbs(_startDate);
                    x -= firstOff;
                }
                return _yAxisWidth + _hierarchyWidth + x; // finished
            } else {
                // section
                long ms = endPPS.diffMilliSeconds(d);
                x = x + (int) Math.round(((double) ms * pps / MILLISCALING));
                milliSecondsToGo = milliSecondsToGo - ms;
                d = endPPS.copy();
            }
        }
        if (absolute) {
            return x;
        }
        // TODO insets
        if (neg) {
            // correct x value
            int firstOff = xForDateAbs(_startDate);
            x -= firstOff;
        }
        return _yAxisWidth + _hierarchyWidth + x;
    }

    /**
     * Retrieve the appropriate pps interval for a given date.
     * 
     * @param d date
     * @return interval or <code>null</code>
     */
    public PPSInterval getPPSInterval(JaretDate d) {
        List<Interval> l = _xScalePPSIntervalRow.getIntervals(d);
        if (l.size() == 0) {
            return null;
        }
        if (l.size() == 1) {
            PPSInterval interval = (PPSInterval) l.get(0);
            if (interval.getEnd().equals(d)) {
                return null;
            }
            return interval;
        }
        if (l.size() == 2) {
            PPSInterval interval1 = (PPSInterval) l.get(0);
            PPSInterval interval2 = (PPSInterval) l.get(1);
            if (interval1.getBegin().equals(d)) {
                return interval1;
            }
            if (interval2.getBegin().equals(d)) {
                return interval2;
            }

        }
        throw new RuntimeException("no overlapping intervals in xscale row");
    }

    /**
     * Get the next interval to a date from the pps row (variable x axis).
     * 
     * @param d date
     * @return next interval (future) to the given date or null if there is no such interval
     */
    public PPSInterval nextPPSInterval(JaretDate d) {
        PPSInterval result = null;
        result = getPPSInterval(d);
        if (result != null) {
            return result;
        }
        // assumes sorted intervals
        List<Interval> list = _xScalePPSIntervalRow.getIntervals();
        for (int i = 0; i < list.size(); i++) {
            PPSInterval interval = (PPSInterval) list.get(i);
            long diffMilliSecs = d.diffMilliSeconds(interval.getBegin());
            if (diffMilliSecs <= 0) {
                result = interval;
                break;
            }
        }
        return result;
    }

    /**
     * Get the row located at x,y.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return row at the given location or <code>null</code> if no row could be determined
     */
    public TimeBarRow rowForXY(int x, int y) {
        if (_diagramRect.contains(x, y) || _yAxisRect.contains(x, y) || _hierarchyRect.contains(x, y)) {
            if (_orientation == Orientation.HORIZONTAL) {
                return rowForY(y);
            } else {
                return rowForY(x);
            }
        } else {
            return null;
        }
    }

    /**
     * Retrieve the row for a coordinate (either y or x depending on the orientation).
     * 
     * @param coord y for horizontal, x for vertical orientation
     * @return index of the row at the given position
     */
    private int rowForCoordInternal(int coord) {
        if (!_timeBarViewState.getUseVariableRowHeights()) {
            if (_orientation == Orientation.HORIZONTAL) {
                return _firstRow + (coord + _firstRowPixelOffset - _diagramRect.y)
                        / _timeBarViewState.getDefaultRowHeight();
            } else {
                return _firstRow + (coord + _firstRowPixelOffset - _diagramRect.x)
                        / _timeBarViewState.getDefaultRowHeight();
            }
        }
        int maxY = 0;
        if (_orientation == Orientation.HORIZONTAL) {
            coord -= _diagramRect.y;
            maxY = _diagramRect.height;
        } else {
            coord -= _diagramRect.x;
            maxY = _diagramRect.width;
        }

        int rowStart = -_firstRowPixelOffset;
        int idx = _firstRow;

        while (rowStart < maxY && idx < _rowList.size()) {
            int rHeight = _timeBarViewState.getRowHeight(getRow(idx));
            if (coord >= rowStart && coord <= rowStart + rHeight) {
                return idx;
            }
            rowStart += rHeight;
            idx++;
        }
        return -1;

    }

    /**
     * Get the row for a given y coordinate in the diagram pane. In case of vertical orientation this has to be read as
     * columnForX.
     * 
     * @param y y coordinate (or x coordinate when using vertical orientation)
     * @return the row or null if no row is found for the given y
     */
    public TimeBarRow rowForY(int y) {
        // y = y - _firstRowPixelOffset;
        if (_rowList == null || _model == null) {
            return null;
        }
        int idx = -1;
        idx = rowForCoordInternal(y);
        if (idx < _rowList.size() && idx >= 0) {
            return getRow(idx);
        } else {
            return null;
        }
    }

    /**
     * Calculate the y coordinate in the diagram pane for the given row. Works only on displayed rows!
     * 
     * @param row row
     * @return y coordinate in the diagram pane
     */
    public int yForRow(TimeBarRow row) {
        if (!_timeBarViewState.getUseVariableRowHeights()) {
            int idx = _rowList.indexOf(row);
            if (idx == -1) {
                throw new RuntimeException("row is not in the row list");
            }
            idx = idx - _firstRow;
            if (_orientation == Orientation.HORIZONTAL) {
                return idx * _timeBarViewState.getDefaultRowHeight() + _diagramRect.y - _firstRowPixelOffset;
            } else {
                return idx * _timeBarViewState.getDefaultRowHeight() + _diagramRect.x - _firstRowPixelOffset;
            }
        }

        int height;
        int offset;
        if (_orientation == Orientation.HORIZONTAL) {
            height = _diagramRect.height;
            offset = _diagramRect.y;
        } else {
            height = _diagramRect.width;
            offset = _diagramRect.x;
        }

        int y = -_firstRowPixelOffset;
        int idx = _firstRow;
        TimeBarRow r = getRow(idx);

        while (y <= height) {
            if (row.equals(r)) {
                return y + offset;
            }
            y += _timeBarViewState.getRowHeight(r);
            idx++;
            if (idx >= _rowList.size()) {
                break;
            }
            r = getRow(idx);
        }
        return -1;

    }

    /**
     * Calculate the display index of the row. The display index is 0 for the topmost row.
     * 
     * @param row row
     * @return the display index
     */
    public int dispIdxForRow(TimeBarRow row) {
        int rIdx = _rowList.indexOf(row);
        if (rIdx >= _firstRow && rIdx <= _firstRow + getRowsDisplayed()) {
            return rIdx - _firstRow;
        }
        return -1;
    }

    /**
     * Calculates the bounds of a row. If the row is not displayed the resulting Rectangle will be 0,0,0,0. The
     * ractangle includes header etc.
     * 
     * @param row row to calculate the bounds for
     * @return Rectangle describing teh rectangle for the row in the current view
     */
    public Rectangle getRowBounds(TimeBarRow row) {
        if (!isRowDisplayed(row)) {
            return new Rectangle(0, 0, 0, 0);
        } else {
            if (_orientation == Orientation.HORIZONTAL) {
                int y = yForRow(row);
                return new Rectangle(0, y, _tbvi.getWidth(), y + _timeBarViewState.getRowHeight(row));
            } else {
                int x = yForRow(row);
                return new Rectangle(x, 0, _timeBarViewState.getRowHeight(row), _tbvi.getHeight());
            }
        }
    }

    /**
     * Calculate the bounds for an interval.
     * 
     * @param rowIdx index of the row
     * @param interval interval
     * @return bounding rectangle
     */
    public Rectangle getIntervalBounds(int rowIdx, Interval interval) {
        if (_orientation == Orientation.HORIZONTAL) {
            int x = xForDate(interval.getBegin());
            int x2 = xForDate(interval.getEnd());
            int width = x2 - x;
            int y = rowIdx == -1 ? 0 : yForRow(getRow(rowIdx));
            int height = rowIdx == -1 ? _tbvi.getHeight() : _timeBarViewState.getRowHeight(getRow(rowIdx));
            TimeBarRow row = rowIdx == -1 ? null : _rowList.get(rowIdx);
            if (row != null && !getTimeBarViewState().getDrawOverlapping(row)) {
                OverlapInfo oi = _overlapStrategy.getOverlapInfo(row, interval);
                if (oi != null) {
                    if (!_useUniformHeight) {
                        height = height / (oi.maxOverlapping + 1);
                    } else {
                        height = height / (_overlapStrategy.getMaxOverlapCount(row));
                    }
                    y = y + oi.pos * height;
                }
            }
            return new Rectangle(x, y, width, height);
        } else {
            int y = xForDate(interval.getBegin());
            int y2 = xForDate(interval.getEnd());
            int height = y2 - y;
            int x = rowIdx == -1 ? 0 : yForRow(getRow(rowIdx));
            int width = rowIdx == -1 ? _tbvi.getWidth() : _timeBarViewState.getRowHeight(getRow(rowIdx));
            TimeBarRow row = rowIdx == -1 ? null : _rowList.get(rowIdx);
            if (row != null && !getTimeBarViewState().getDrawOverlapping(row)) {
                OverlapInfo oi = _overlapStrategy.getOverlapInfo(_rowList.get(rowIdx), interval);
                if (oi != null) {
                    if (!_useUniformHeight) {
                        width = width / (oi.maxOverlapping + 1);
                    } else {
                        width = width / (_overlapStrategy.getMaxOverlapCount(row));
                    }
                    x = x + oi.pos * width;
                }
            }
            return new Rectangle(x, y, width, height);
        }
    }

    /**
     * Retrieve the bounding rect of an interval. ATTENTION: this uses the row for interval lookup in the model that may
     * be imperformant.
     * 
     * @param interval interval
     * @return the bounding rect or null
     */
    public Rectangle getIntervalBounds(Interval interval) {
        TimeBarRow row = _model.getRowForInterval(interval);
        if (row != null) {
            return getIntervalBounds(row, interval);
        }
        return null;
    }

    /**
     * Calculates the bounding rectangle for an interval. The row may be given optional- if it isn't the rectangle will
     * span the complete height (vertical: width) of the diagram
     * 
     * @param row TimeBarRow - may be null
     * @param interval Interval in question
     * @return Rectangle
     */
    public Rectangle getIntervalBounds(TimeBarRow row, Interval interval) {
        return getIntervalBounds(row == null ? -1 : _rowList.indexOf(row), interval);
    }

    /**
     * @return the number of rows currently displayed by the viewer
     */
    public int getRowsDisplayed() {
        if (!_timeBarViewState.getUseVariableRowHeights()) {
            // correction by 1 to ensure the last row is always displayed
            if (_orientation == Orientation.HORIZONTAL) {
                return _diagramRect.height / _timeBarViewState.getDefaultRowHeight() + 1;
                // return _tbvi.getHeight() / _timeBarViewState.getDefaultRowHeight() + 1;
            } else {
                return _diagramRect.width / _timeBarViewState.getDefaultRowHeight() + 1;
                // return _tbvi.getWidth() / _timeBarViewState.getDefaultRowHeight() + 1;
            }
        }
        // variable heights
        int end;
        if (_orientation == Orientation.HORIZONTAL) {
            end = _diagramRect.height;
        } else {
            end = _diagramRect.width;
        }
        int count = 0;
        int idx = _firstRow;
        int coord = -_firstRowPixelOffset;
        while (coord <= end && idx < _rowList.size()) {
            coord += _timeBarViewState.getRowHeight(getRow(idx++));
            count++;
        }
        return count;

    }

    /**
     * Check whether a certain row is currently displayed.
     * 
     * @param row row to be checked
     * @return true if the row is in the current view, false otherwise
     */
    public boolean isRowDisplayed(TimeBarRow row) {
        return _rowList.indexOf(row) - _firstRow < getRowsDisplayed();
    }

    /**
     * Retrieves all intervals at a given point in the diagram pane.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return List of all intervals at the point
     */
    public List<Interval> getIntervalsAt(int x, int y) {
        TimeBarRow row = rowForXY(x, y);
        return getIntervalsAt(row, x, y);
    }

    /**
     * Retrieves all intervals for a given row and a x coordinate in the diagram pane. Filtered intervals will be left
     * out.
     * 
     * @param row row to search in
     * @param x x coordinate, will be ignored if set to -1
     * @param y y coordinate, will be ignored if set to -1
     * @return List of intervals for the given x and row
     */
    public List<Interval> getIntervalsAt(TimeBarRow row, int x, int y) {
        List<Interval> result = new ArrayList<Interval>();
        if (row != null) {
            JaretDate date;
            if (_orientation == Orientation.HORIZONTAL) {
                date = dateForCoord(x);
            } else {
                date = dateForCoord(y);
            }
            // correct for milli accuracy
            // (no problem when dealing with scales in second range or above)
            if (_milliAccuracy) {
                date.advanceMillis(1);
            }
            result = row.getIntervals(date);
            // if there is a interval filter
            if (_intervalFilter != null) {
                List<Interval> in = result;
                result = new ArrayList<Interval>();
                for (Interval interval : in) {
                    if (_intervalFilter.isInResult(interval)) {
                        result.add(interval);
                    }
                }
            }
            // check whether up to one interval has been found or no y
            // coordinate is given
            if (result.size() <= 1 || x == -1 || y == -1) {
                return result;
            }
            // check exact interval bounds
            List<Interval> newResult = new ArrayList<Interval>(1);
            for (Interval interval : result) {
                Rectangle rect = getIntervalBounds(row, interval);
                if (rect.contains(x, y)) {
                    newResult.add(interval);
                }
            }
            return newResult;
        } else {
            result = new ArrayList<Interval>();
        }
        return result;

    }

    /**
     * Retrieve intervals in a given row for a given x coordinate.
     * 
     * @param row row to search in
     * @param x x coordinate
     * @return list of intervals
     */
    public List<Interval> getIntervalsAt(TimeBarRow row, int x) {
        if (_orientation == Orientation.HORIZONTAL) {
            return getIntervalsAt(row, x, -1);
        } else {
            return getIntervalsAt(row, -1, x);
        }
    }

    // *** TimeBarMarkerListener
    /**
     * {@inheritDoc}
     */
    public void markerMoved(TimeBarMarker marker, JaretDate oldDate, JaretDate currentDate) {
        int width = _tbvi.getMarkerWidth(marker);
        if (isDisplayed(currentDate)) {
            // repaint only the last position of the marker and the new position
            if (_orientation.equals(Orientation.HORIZONTAL)) {
                _tbvi.repaint(xForDate(oldDate) - width / 2 + 1, 0, width + 1, _tbvi.getHeight());
                _tbvi.repaint(xForDate(currentDate) - width / 2 + 1, 0, width + 1, _tbvi.getHeight());
            } else {
                // vertical
                _tbvi.repaint(0, xForDate(oldDate) - width / 2 + 1, _tbvi.getWidth(), width + 1);
                _tbvi.repaint(0, xForDate(currentDate) - width / 2 + 1, _tbvi.getWidth(), width + 1);
            }
        }
        if (marker.getDate().compareTo(_minDate) < 0) {
            setMinDate(marker.getDate().copy());
        } else if (marker.getDate().compareTo(_maxDate) > 0) {
            setMaxDate(marker.getDate().copy());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void markerDescriptionChanged(TimeBarMarker marker, String oldValue, String newValue) {
        int width = _tbvi.getMarkerWidth(marker);
        if (isDisplayed(marker.getDate())) {
            _tbvi.repaint(xForDate(marker.getDate()) - width / 2 + 1, 0, width / 2 + 1, _tbvi.getHeight());
        }
    }

    // *** End of TimeBarMarkerListener

    // *** TimeBarSelectionListener
    /**
     * {@inheritDoc}
     */
    public void selectionChanged(TimeBarSelectionModel selectionModel) {
        // this should be rarely called by the SelectionModel
        _tbvi.repaint();
        // this is swt only: support for the timebar viewer beeing an
        // ISelectionProvider
        _tbvi.fireSelectionChanged();
    }

    /**
     * {@inheritDoc}
     */
    public void elementAddedToSelection(TimeBarSelectionModel selectionModel, Object element) {
        if (element instanceof TimeBarRow) {
            Rectangle rowRect = getRowBounds((TimeBarRow) element);
            _tbvi.repaint(rowRect);
        } else if (element instanceof Interval) {
            // we don't have a row to go with ... so we have to repaint the
            // whole column
            Rectangle intervalRect = getIntervalBounds(null, (Interval) element);
            _tbvi.repaint(intervalRect);
        } else if (element instanceof IIntervalRelation) {
            // repaint the whole diagram since there is no knowledge about the relation rendering
            _tbvi.repaint(_diagramRect);
        } else {
            throw new RuntimeException("Unknonw object in elementAddedToSelection " + element.getClass().getName());
        }
        // this is swt only: support for the timebar viewer beeing an
        // ISelectionProvider
        _tbvi.fireSelectionChanged();

    }

    /**
     * {@inheritDoc}
     */
    public void elementRemovedFromSelection(TimeBarSelectionModel selectionModel, Object element) {
        if (element instanceof TimeBarRow) {
            // the row may be removed from the model (repainting is done in the
            // model wath then)
            if (_rowList.contains((TimeBarRow) element)) {
                Rectangle rowRect = getRowBounds((TimeBarRow) element);
                _tbvi.repaint(rowRect);
            }
        } else if (element instanceof Interval) {
            Interval interval = (Interval) element;
            TimeBarRow row = _model.getRowForInterval(interval);
            // the interval may have been removed from the model, check
            if (row != null && row.getIntervals() != null && row.getIntervals().contains(interval)) {
                Rectangle intervalRect = getIntervalBounds(row, (Interval) element);
                _tbvi.repaint(intervalRect);
            }
        } else if (element instanceof IIntervalRelation) {
            // repaint the whole diagram since there is no knowledge about the relation rendering
            _tbvi.repaint(_diagramRect);
        } else {
            throw new RuntimeException("Unknown object in elementRemovedFromSelection " + element.getClass().getName());
        }
        // this is swt only: support for the timebar viewer beeing an
        // ISelectionProvider
        _tbvi.fireSelectionChanged();

    }

    // *** End of TimeBarSelectionListener
    // *** TimeBarModelListener
    /**
     * {@inheritDoc}
     */
    public void modelDataChanged(TimeBarModel model) {
        checkAndAdjustMinMax();
        updateRowList();
        _overlapStrategy.clearCachedData();
        _tbvi.repaint();
    }

    /**
     * {@inheritDoc}
     */
    public void rowDataChanged(TimeBarModel model, TimeBarRow row) {
        checkAndAdjustMinMax();
        if (!getTimeBarViewState().getDrawOverlapping(row)) {
            _overlapStrategy.updateOICache(row);
        }
        if (isRowDisplayed(row)) {
            _tbvi.repaint();
        }
        // check which intervals are in the selection and are not in the model
        // anymore
        // TODO -> very expensive and not really needed
    }

    /**
     * {@inheritDoc}
     */
    public void rowAdded(TimeBarModel model, TimeBarRow row) {
        checkAndAdjustMinMax();
        updateRowList();
        _tbvi.repaint();
    }

    /**
     * {@inheritDoc}
     */
    public void rowRemoved(TimeBarModel model, TimeBarRow row) {
        checkAndAdjustMinMax();
        boolean isDisplayed = isRowDisplayed(row);
        updateRowList();
        if (isDisplayed) {
            _tbvi.repaint();
        }
        // remove from selection
        if (_selectionModel != null) {
            _selectionModel.remSelectedRow(row);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void elementAdded(TimeBarModel model, TimeBarRow row, Interval element) {
        checkAndAdjustMinMax();
        if (!getTimeBarViewState().getDrawOverlapping(row)) {
            _overlapStrategy.updateOICache(row);
        }
        if (isRowDisplayed(row)) {
            _tbvi.repaint();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void elementRemoved(TimeBarModel model, TimeBarRow row, Interval element) {
        checkAndAdjustMinMax();
        if (!getTimeBarViewState().getDrawOverlapping(row)) {
            _overlapStrategy.updateOICache(row);
        }
        if (isRowDisplayed(row)) {
            _tbvi.repaint();
        }
        // remove from selection
        if (_selectionModel != null) {
            _selectionModel.remSelectedInterval(element);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void elementChanged(TimeBarModel model, TimeBarRow row, Interval element) {
        checkAndAdjustMinMax();
        if (!getTimeBarViewState().getDrawOverlapping(row)) {
            _overlapStrategy.updateOICache(row);
        }
        if (isRowDisplayed(row)) {
            _tbvi.repaint();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void headerChanged(TimeBarModel model, TimeBarRow row, Object newHeader) {
        if (isRowDisplayed(row)) {
            _tbvi.repaint();
        }
    }

    // *** End of TimeBarModelListener

    /**
     * @return Returns the firstRow.
     */
    public int getFirstRow() {
        return _firstRow;
    }

    /**
     * Sets the first row to be displayed with a pixel offset 0.
     * 
     * @param firstRow The row to be displayed at the topmost position in the viewer.
     */
    public void setFirstRow(int firstRow) {
        setFirstRow(firstRow, 0);
    }

    /**
     * Set the first row to be displayed.
     * 
     * @param row row to be the first row with an offset of 0.
     */
    public void setFirstRow(TimeBarRow row) {
        int index = _rowList.indexOf(row);
        if (index != -1) {
            setFirstRow(index, 0);
        }
    }

    /**
     * Set the first row to be displayed.
     * 
     * @param firstRow upmost row to be displayed
     * @param pixOffset pixel offset
     */
    public void setFirstRow(int firstRow, int pixOffset) {
        if (firstRow != _firstRow || _firstRowPixelOffset != pixOffset) {
            int oldVal = _firstRow;
            int oldOffset = _firstRowPixelOffset;

            if (_tbvi != null) {
                // pixel difference of the old and the new position
                int diff;
                if (!_timeBarViewState.getUseVariableRowHeights()) {
                    diff = (firstRow * _timeBarViewState.getDefaultRowHeight() + pixOffset)
                            - (oldVal * _timeBarViewState.getDefaultRowHeight() + oldOffset);
                } else {
                    diff = (getAbsPosForRow(firstRow) + pixOffset) - (getAbsPosForRow(oldVal) + oldOffset);
                }
                // if not optimized scrolling or more than 2 thirds difference
                // -> do a complete repaint
                int maxOptScroll = _orientation.equals(Orientation.HORIZONTAL) ? (_diagramRect.height - _diagramRect.height)
                        : (_diagramRect.width - _diagramRect.width);

                if (!_optimizeScrolling || Math.abs(diff) > maxOptScroll / 3) {
                    _firstRow = firstRow;
                    _firstRowPixelOffset = pixOffset;
                    _tbvi.repaint();
                } else {
                    // do optimized scrolling
                    if (_orientation == Orientation.HORIZONTAL) {
                        _tbvi.doScrollVertical(diff);
                    } else {
                        _tbvi.doScrollHorizontal(diff);
                    }

                    _firstRow = firstRow;
                    _firstRowPixelOffset = pixOffset;
                }
                if (_tbvi != null) {
                    _tbvi.firePropertyChange(TimeBarViewerInterface.PROPERTYNAME_FIRSTROW, oldVal, firstRow);
                    _tbvi.firePropertyChange(TimeBarViewerInterface.PROPERTYNAME_FIRSTROWOFFSET, oldOffset, pixOffset);
                }
                updateRowScrollBar();
            } else {
                _firstRow = firstRow;
                _firstRowPixelOffset = pixOffset;
            }
        }
    }

    /**
     * Retrieve the pixel offset for the first row.
     * 
     * @return pixel offset of the first row
     */
    public int getFirstRowOffset() {
        return _firstRowPixelOffset;
    }

    /**
     * Set the pixel offset of the first row.
     * 
     * @param offset pixel offset for the first row
     */
    public void setFirstRowOffset(int offset) {
        setFirstRow(getFirstRow(), offset);
    }

    /**
     * Set the last row in the viewer. If there are not enough rows for the row beeing the last row the row will be
     * displayed as far down as possible by setting the first row to 0.
     * 
     * @param index index of the row to be displayed at the bottom of the viewer.
     */
    public void setLastRow(int index) {
//        TimeBarRow row = getRow(index);
//        int absY = getAbsPosForRow(index);
//        int endY = absY + _timeBarViewState.getRowHeight(row);
//
//        int height = _diagramRect.height;
//
//        int upperBound = endY - height;
//        int y = endY;
//        int idx = index;
//        while (idx > 0 && y > upperBound) {
//            row = getRow(idx);
//            y -= _timeBarViewState.getRowHeight(row);
//            idx--;
//        }
//        if (idx >= 0) {
//            int offset = Math.abs(y - upperBound);
//            setFirstRow(idx + 1, offset);
//        } else {
//            setFirstRow(0, 0);
//        }

        TimeBarRow row = getRow(index);
        int absY = getAbsPosForRow(index);
        int endY = absY + _timeBarViewState.getRowHeight(row);

        int height = _diagramRect.height;

        int upperBound = endY - height;
        int y = endY;
        int idx = index;

        if (endY<height) {
            setFirstRow(0,0);
        } else {
            y -= _timeBarViewState.getRowHeight(row);
            idx--;
            while (idx > 0 && y > upperBound) {
                row = getRow(idx);
                y -= _timeBarViewState.getRowHeight(row);
                idx--;
            }
            int offset = Math.abs(y - upperBound);
            if (idx >= 0 ) {
                setFirstRow(idx + 1, offset);
            } else {
                setFirstRow(0, offset);
            }
            
        }
        
    }

    /**
     * Set the last row in the viewer. If there are not enough rows for the row beeing the last row the row will be
     * displayed as far down as possible by setting the first row to 0.
     * 
     * @param row the row to be displayed at the bottom of the viewer.
     */
    public void setLastRow(TimeBarRow row) {
        int index = _rowList.indexOf(row);
        if (index != -1) {
            setLastRow(index);
        }
    }

    /**
     * @return Returns the timeScalePosition.
     */
    public int getTimeScalePosition() {
        return _timeScalePosition;
    }

    /**
     * @param timeScalePosition The timeScalePosition to set.
     */
    public void setTimeScalePosition(int timeScalePosition) {
        _timeScalePosition = timeScalePosition;
        if (_tbvi != null) {
            _tbvi.repaint();
        }
    }

    /**
     * @return Returns the yAxisWidth.
     */
    public int getYAxisWidth() {
        return _yAxisWidth;
    }

    /**
     * @param axisWidth The yAxisWidth to set.
     */
    public void setYAxisWidth(int axisWidth) {
        int oldval = _yAxisWidth;
        _yAxisWidth = axisWidth;
        if (oldval != _yAxisWidth) {
            if (_tbvi != null) {
                _tbvi.repaint();
                _tbvi.firePropertyChange(TimeBarViewerInterface.PROPERTYNAME_YAXISWIDTH, oldval, _yAxisWidth);
            }
        }
    }

    /**
     * @return Returns the xAxisHeight.
     */
    public int getXAxisHeight() {
        return _xAxisHeight;
    }

    /**
     * Set the height of the y axis.
     * 
     * @param axisHeight The xAxisHeight to set.
     */
    public void setXAxisHeight(int axisHeight) {
        if (axisHeight != _xAxisHeight) {
            int oldVal = _xAxisHeight;
            _xAxisHeight = axisHeight;
            if (_tbvi != null) {
                _tbvi.repaint();
                _tbvi.firePropertyChange(TimeBarViewerInterface.PROPERTYNAME_XAXISHEIGHT, oldVal, axisHeight);
            }
        }
    }

    /**
     * Set the width fo rrendering the hierarchy area.
     * 
     * @param width width to use
     */
    public void setHierarchyWidth(int width) {
        _hierarchyWidth = width;
        if (_tbvi != null) {
            _tbvi.repaint();
        }
    }

    /**
     * Retrieve the width used to draw the hierarchy area.
     * 
     * @return width in pixel
     */
    public int getHierarchyWidth() {
        return _hierarchyWidth;
    }

    /**
     * Calculate the boundaries of the diagram elements.
     * 
     * @param cwidth width of the component client area
     * @param cheight width of the component client area
     */
    public void preparePaint(int cwidth, int cheight) {
        if (_orientation == Orientation.HORIZONTAL) {
            preparePaintHorizontal(cwidth, cheight);
        } else {
            preparePaintVertical(cwidth, cheight);
        }

        // handle a possible set initial display range
        if (_initialStartDate != null) {
            JaretDate startDate = _initialStartDate;
            _initialStartDate = null;
            setSecondsDisplayed(_initialSecondsDisplayed, false);
            setStartDate(startDate);
        }

    }

    /**
     * Calculate the boundaries of the diagram elements for the horizontal orientation.
     * 
     * @param cwidth width of the component client area
     * @param cheight width of the component client area
     */
    public void preparePaintHorizontal(int cwidth, int cheight) {

        int topy = (_timeScalePosition == TimeBarViewerInterface.TIMESCALE_POSITION_BOTTOM || _timeScalePosition == TimeBarViewerInterface.TIMESCALE_POSITION_NONE) ? 0
                : _xAxisHeight;
        int height = _timeScalePosition == TimeBarViewerInterface.TIMESCALE_POSITION_NONE ? cheight : cheight
                - _xAxisHeight;
        // diagram area itself
        _diagramRect = new Rectangle(_hierarchyWidth + _yAxisWidth, topy, cwidth - _yAxisWidth - _hierarchyWidth,
                height);
        // y axis
        _yAxisRect = new Rectangle(_hierarchyWidth, topy, _yAxisWidth, height);
        // hierarchy indicators
        _hierarchyRect = new Rectangle(0, topy, _hierarchyWidth, height);

        if (_timeScalePosition != TimeBarViewerInterface.TIMESCALE_POSITION_NONE) {
            _xAxisRect = new Rectangle(_diagramRect.x,
                    _timeScalePosition == TimeBarViewerInterface.TIMESCALE_POSITION_TOP ? 0 : cheight - _xAxisHeight,
                    _diagramRect.width, _xAxisHeight);
        } else {
            _xAxisRect = new Rectangle(0, 0, 0, 0);
        }
        // calculate the end date of the drawing area
        _endDate = dateForX(cwidth);

        // title rect
        _titleRect = new Rectangle(0, _xAxisRect.y, _hierarchyRect.width + _yAxisRect.width, _xAxisRect.height);

        // correct all calculated rectangles by an offset if set
        if (_offsetLeft != 0 || _offsetTop != 0) {
            _diagramRect.x += _offsetLeft;
            _diagramRect.y += _offsetTop;
            _yAxisRect.x += _offsetLeft;
            _yAxisRect.y += _offsetTop;
            _xAxisRect.x += _offsetLeft;
            _xAxisRect.y += _offsetTop;
            _hierarchyRect.x += _offsetLeft;
            _hierarchyRect.y += _offsetTop;
            _titleRect.x += _offsetLeft;
            _titleRect.y += _offsetTop;
        }

        if (_autoScaleRows > 0) {
            int rowHeight = _diagramRect.height / _autoScaleRows;
            setRowHeight(rowHeight);
        }

    }

    /**
     * Calculate the boundaries of the diagram elements for the vertical orientation.
     * 
     * @param cwidth width of the component client area
     * @param cheight width of the component client area
     */
    public void preparePaintVertical(int cwidth, int cheight) {

        int leftX = (_timeScalePosition == TimeBarViewerInterface.TIMESCALE_POSITION_BOTTOM || _timeScalePosition == TimeBarViewerInterface.TIMESCALE_POSITION_NONE) ? 0
                : _xAxisHeight;
        int width = _timeScalePosition == TimeBarViewerInterface.TIMESCALE_POSITION_NONE ? cwidth : cwidth
                - _xAxisHeight;

        // diagram area itself
        _diagramRect = new Rectangle(leftX, _hierarchyWidth + _yAxisWidth, width, cheight - _yAxisWidth
                - _hierarchyWidth);

        // y axis (x axis in the vertical case)
        _yAxisRect = new Rectangle(leftX, _hierarchyWidth, width, _yAxisWidth);
        // hierarchy indicators
        _hierarchyRect = new Rectangle(leftX, 0, width, _hierarchyWidth);

        // rect for the timescale (yaxis for verticalorientation)
        if (_timeScalePosition != TimeBarViewerInterface.TIMESCALE_POSITION_NONE) {
            int tsX = _timeScalePosition == TimeBarViewerInterface.TIMESCALE_POSITION_TOP ? 0 : cwidth - _xAxisHeight;
            _xAxisRect = new Rectangle(tsX, _diagramRect.y, _xAxisHeight, _diagramRect.height);
        } else {
            _xAxisRect = new Rectangle(0, 0, 0, 0);
        }
        // calculate the end date of the drawing area
        _endDate = dateForX(cheight);

        // title rect
        _titleRect = new Rectangle(_xAxisRect.x, 0, _xAxisRect.width, _hierarchyRect.height + _yAxisRect.height);

        // correct all calculated rectangles by an offset if set
        if (_offsetLeft != 0 || _offsetTop != 0) {
            _diagramRect.x += _offsetLeft;
            _diagramRect.y += _offsetTop;
            _yAxisRect.x += _offsetLeft;
            _yAxisRect.y += _offsetTop;
            _xAxisRect.x += _offsetLeft;
            _xAxisRect.y += _offsetTop;
            _hierarchyRect.x += _offsetLeft;
            _hierarchyRect.y += _offsetTop;
            _titleRect.x += _offsetLeft;
            _titleRect.y += _offsetTop;
        }
        if (_autoScaleRows > 0) {
            int rowWidth = _diagramRect.width / _autoScaleRows;
            setRowHeight(rowWidth);
        }

    }

    /**
     * @return Returns the diagramRect.
     */
    public Rectangle getDiagramRect() {
        return _diagramRect;
    }

    /**
     * @return Returns the xAxisRect.
     */
    public Rectangle getXAxisRect() {
        return _xAxisRect;
    }

    /**
     * @return Returns the yAxisRect.
     */
    public Rectangle getYAxisRect() {
        return _yAxisRect;
    }

    /**
     * @return the reactangle to draw the hierchy markers in
     */
    public Rectangle getHierarchyRect() {
        return _hierarchyRect;
    }

    /**
     * Retrieve area for drawing a title.
     * 
     * @return title area rectangle
     */
    public Rectangle getTitleRect() {
        return _titleRect;
    }

    /**
     * @return Returns the lastStartDate.
     */
    public JaretDate getLastStartDate() {
        return _lastStartDate;
    }

    /**
     * @param lastStartDate The lastStartDate to set.
     */
    public void setLastStartDate(JaretDate lastStartDate) {
        _lastStartDate = lastStartDate;
    }

    /**
     * Retrievs the intervals at the given x coordinate and the given row. The result ist sorted by the length of the
     * intervals, shortest first.
     * 
     * @param row row to investigate
     * @param x x coodinate
     * @return list of intervals at the given position
     */
    private List<Interval> getIntervalsSortedAt(TimeBarRow row, int x) {
        List<Interval> intervals = getIntervalsAt(row, x);
        if (intervals.size() == 0) {
            return intervals;
        }
        // intervals will be sorted by length (shortest first)
        // so the shorter intervals will go first for tooltips
        Collections.sort(intervals, new Comparator<Interval>() {
            public int compare(Interval i1, Interval i2) {
                return i1.getSeconds() - i2.getSeconds();
            }

        });
        return intervals;
    }

    /**
     * Retrieves the shortest Interval for a given row and x,y coordinates (including contains check on the component).
     * 
     * @param row the row to search in
     * @param x x coordinate
     * @param y y coordinate
     * @return the Interval or null if none was found
     */
    private Interval intervalAt(TimeBarRow row, int x, int y) {
        Interval result = null;
        // retrieve all intervals in the row for the x coordinate
        List<Interval> intervals;
        if (_orientation == Orientation.HORIZONTAL) {
            intervals = getIntervalsSortedAt(row, x);
        } else {
            intervals = getIntervalsSortedAt(row, y);
        }
        if (intervals.size() == 0) {
            return null;
        }
        for (Interval interval : intervals) {
            Rectangle intervalRect = getIntervalBounds(row, interval);
            boolean overlapping = getTimeBarViewState().getDrawOverlapping(row) ? false : _overlapStrategy
                    .getOverlapInfo(row, interval).overlappingCount > 0
                    || _useUniformHeight;
            if (_tbvi.timeBarContains(interval, intervalRect, x - intervalRect.x, y - intervalRect.y, overlapping)) {
                result = interval;
                // the first interval will get through
                break;
            }
        }
        return result;
    }

    // *** Handling of mouse interactions

    /**
     * Handle press of a mouse button.
     * 
     * @param x x coordinate (control)
     * @param y y coordinate (control)
     * @param isPopupTrigger true if the button is the system's popup trigger
     * @param modifierMask modifier mask for keyboard diversification (AWT input event type)
     */
    public void mousePressed(int x, int y, boolean isPopupTrigger, int modifierMask) {
        boolean nothingHitInDiagramArea = true;
        if (_diagramRect.contains(x, y)) { // selection
            // normal selection only if shift is not pressed
            if ((modifierMask & InputEvent.SHIFT_DOWN_MASK) == 0) {
                // interval region
                TimeBarRow row = rowForXY(x, y);
                // System.out.println(x + "," + y + " row " + _rowList.indexOf(row));
                Interval interval = intervalAt(row, x, y);
                // System.out.println("Interval " + interval);
                // if the interval is filtered, it can't be selected
                if (interval != null && (_intervalFilter == null || _intervalFilter.isInResult(interval))) {
                    // focus handling
                    setFocussedInterval(interval);

                    // differentiate between selection and popup
                    if ((modifierMask & InputEvent.CTRL_DOWN_MASK) != 0) {
                        if (!_selectionModel.isSelected(interval)) {
                            _selectionModel.addSelectedInterval(interval);
                        } else { // deselect
                            _selectionModel.remSelectedInterval(interval);
                        }
                    } else if (_selectionModel.isEmpty()) {
                        _selectionModel.setSelectedInterval(interval);
                    }
                } else {

                    List<IIntervalRelation> relations = _tbvi.getRelationsForCoord(x, y);
                    if (relations != null && relations.size() > 0) {
                        // hit one or more relations
                        handleRelationSelection(relations, modifierMask);
                    } else {
                        // clicked but nothing hit
                        // if not done with control -> deselect all
                        if (!isPopupTrigger && (modifierMask & InputEvent.CTRL_DOWN_MASK) == 0) {
                            // when row selection toggle mode is used only clear interval selection
                            if (!_selectionModel.getRowSelectionToggleMode()) {
                                _selectionModel.clearSelection();
                            } else {
                                _selectionModel.clearIntervalSelection();
                                _selectionModel.clearRelationSelection();
                            }
                        }
                        // clicked but nothing hit
                        // maybe the user starts doing a rectangle selection
                        // this may only be done with select multi and
                        // rectselectenable. Additionally this won't be possible with
                        // the popuptrigger
                        if (!isPopupTrigger && _selectionModel.getMultipleSelectionAllowed() && _rectSelectionEnabled
                                && (row == null || getTouchedInterval(row, x, y) == null)) {
                            _selectionRect = new Rectangle(x, y, 0, 0);
                            fireSelectionRectChanged();
                        }
                        nothingHitInDiagramArea = true;
                    }
                }
            } else {
                if (_regionRectEnabled) {
                    _regionSelection = new TBRect();
                    JaretDate startDate = dateForCoord(x, y);
                    _regionSelection.startDate = startDate;
                    _regionSelection.endDate = startDate.copy();
                    TimeBarRow row = rowForXY(x, y);
                    _regionSelection.startRow = row;
                    _regionSelection.endRow = row;

                    _regionStartDate = startDate.copy();
                    _regionStartRow = row;

                    fireRegionRectChanged();
                }
            }
        } else if (_lineDraggingAllowed && hierarchyLineHit(x, y)) {
            // start drag of hierarchy limiter line
            _hierarchyLineDragging = true;
        } else if (_lineDraggingAllowed && headerLineHit(x, y)) {
            // start drag of header delimiter line
            _headerLineDragging = true;
        } else if (_rowHeightDraggingAllowed && rowLineHit(x, y)) {
            // start drag of row height
            _heightDraggedRow = getRowByBottomLine(x, y);
        } else if (_yAxisRect.contains(x, y)) {
            // area header
            TimeBarRow row = rowForXY(x, y);
            handleRowSelection(row, modifierMask);
        } else if (_hierarchyRect.contains(x, y) && !isPopupTrigger) {
            // in the hierarchy area: toggle or select row
            TimeBarRow row = rowForXY(x, y);
            if (row instanceof TimeBarNode) {
                TimeBarNode node = (TimeBarNode) row;
                if (_tbvi.isInToggleArea(node, x, y)) {
                    _hierarchicalViewState.setExpanded(node, !_hierarchicalViewState.isExpanded(node));
                } else if (_tbvi.isInHierarchySelectionArea(node, x, y)) {
                    handleRowSelection(row, modifierMask);
                }
            }
        } else if (_xAxisRect.contains(x, y)) {
            // mouse press in x axis area
            TimeBarMarker marker = getMarkerForXY(x, y);
            if (marker != null && marker.isDraggable()) { // start drag
                _tbvi.setCursor(Cursor.MOVE_CURSOR);
                _draggedMarker = marker;
                _markerDragStart = marker.getDate().copy();
                fireMarkerDragStarted(marker); // inform listeners
            }
        }
        if (_markerDraggingInDiagramArea && _draggedMarker == null && nothingHitInDiagramArea) {
            TimeBarMarker marker = getMarkerForXY(x, y);
            if (marker != null && marker.isDraggable()) { // start drag
                _tbvi.setCursor(Cursor.MOVE_CURSOR);
                _draggedMarker = marker;
                _markerDragStart = marker.getDate().copy();
                fireMarkerDragStarted(marker); // inform listeners
            }
        }
    }

    /** last selected row. Used for shift-click range selections. */
    protected int _lastRowSelectionIndex = -1;

    /**
     * Handle row selection based on the modifier.
     * 
     * @param row selected row
     * @param modifierMask Swing modifier mask
     */
    private void handleRowSelection(TimeBarRow row, int modifierMask) {
        int rowIdx = _rowList.indexOf(row);
        if ((modifierMask & InputEvent.SHIFT_DOWN_MASK) != 0) {
            if (_lastRowSelectionIndex == -1) {
                _selectionModel.addSelectedRow(row);
                _lastRowSelectionIndex = rowIdx;
            } else {
                _selectionModel.setSelectedRow(row);
                setRowSelection(_lastRowSelectionIndex, rowIdx);
            }
        } else {
            if ((modifierMask & InputEvent.CTRL_DOWN_MASK) != 0 || _selectionModel.getRowSelectionToggleMode()) {
                if (!_selectionModel.isSelected(row)) {
                    _selectionModel.addSelectedRow(row);
                    _lastRowSelectionIndex = rowIdx;
                } else { // deselect
                    _selectionModel.remSelectedRow(row);
                }
            } else if (!_selectionModel.getRowSelectionToggleMode()) {
                _selectionModel.setSelectedRow(row);
                _lastRowSelectionIndex = rowIdx;
            }
        }
    }

    /**
     * Handle a relation selection. Always takes the first relation in the list of cliecked relations.
     * 
     * @param relations cliecked relations
     * @param modifierMask modifier mask of the selcting event
     */
    private void handleRelationSelection(List<IIntervalRelation> relations, int modifierMask) {
        IIntervalRelation relation = relations.get(0);
        if ((modifierMask & InputEvent.CTRL_DOWN_MASK) != 0) {
            if (!_selectionModel.isSelected(relation)) {
                _selectionModel.addSelectedRelation(relation);
            } else { // deselect
                _selectionModel.remSelectedRelation(relation);
            }
        } else {
            _selectionModel.setSelectedRelation(relation);
        }
    }

    /**
     * Set all rows in the range as selected.
     * 
     * @param startIndex start index in the row list
     * @param endIndex end index in the row list
     */
    private void setRowSelection(int startIndex, int endIndex) {
        int start = Math.min(startIndex, endIndex);
        int end = Math.max(startIndex, endIndex);
        for (int i = start; i <= end; i++) {
            _selectionModel.addSelectedRow(_rowList.get(i));
        }
    }

    /**
     * Handle release of mouse button.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @param isPopupTrigger true if the button is the systems popup trigger
     * @param modifierMask modifier mask for keyboard diversification (AWT input event type)
     */
    public void mouseReleased(int x, int y, boolean isPopupTrigger, int modifierMask) {
        if (_headerLineDragging || _hierarchyLineDragging || _heightDraggedRow != null) {
            _headerLineDragging = false;
            _hierarchyLineDragging = false;
            _heightDraggedRow = null;
            _tbvi.setCursor(Cursor.DEFAULT_CURSOR);
            _tbvi.repaint();
        }
        if (_draggedMarker != null) { // stop drag
            TimeBarMarker marker = _draggedMarker;
            _draggedMarker = null;
            _tbvi.setCursor(Cursor.DEFAULT_CURSOR);
            // MAYBE possible optimization: can be more specific
            _tbvi.repaint();
            fireMarkerDragStopped(marker); // inform listeners
        }
        if (_selectionRect != null) {
            // stop selection rectangle
            // all selections are updated!
            _selectionRect = null;
            if (_lastSelRect != null) {
                _tbvi.repaint(_lastSelRect.x, _lastSelRect.y, _lastSelRect.width + 1, _lastSelRect.height + 1);
            }
            fireSelectionRectClosed();
        } else {
            if ((modifierMask & InputEvent.CTRL_DOWN_MASK) == 0 && _diagramRect.contains(x, y)) {
                TimeBarRow row = rowForXY(x, y);
                if (row != null) {
                    Interval interval = intervalAt(row, x, y);
                    if (interval != null) {
                        _selectionModel.setSelectedInterval(interval);
                    }
                }
            }
        }
        if (_changingInterval != null) {
            // inform listeners about the completion of the drag/change
            fireIntervalChanged(_intervalDraggedRow, _changingInterval, _originalInterval.getBegin(), _originalInterval
                    .getEnd());
            _changingInterval = null;
            _tbvi.setCursor(Cursor.DEFAULT_CURSOR);

            if (_dragAllSelectedIntervals && _draggedIntervals != null) {
                for (int i = 0; i < _draggedIntervals.size(); i++) {
                    fireIntervalChanged(_draggedIntervalsRows.get(i), _draggedIntervals.get(i), _originalIntervals.get(
                            i).getBegin(), _originalIntervals.get(i).getEnd());
                }
                _originalIntervals = null;
                _draggedIntervals = null;
                _draggedIntervalsRows = null;
            }

        }

        // popup menues
        if (isPopupTrigger) {
            TimeBarRow row = rowForXY(x, y);
            _ctxCoordinate = new Point(x, y);
            _ctxRow = row;
            _ctxDate = dateForCoord(x, y);
            if (_hierarchyRect.contains(x, y)) {
                _tbvi.displayHierarchyContextMenu(row, x, y);
            } else if (_yAxisRect.contains(x, y)) {
                _tbvi.displayHeaderContextMenu(row, x, y);
            } else if (_titleRect.contains(x, y)) {
                _tbvi.displayTitleContextMenu(x, y);
            } else if (_xAxisRect.contains(x, y)) {
                _tbvi.displayTimeScaleContextMenu(x, y);
            } else if (_diagramRect.contains(x, y)) {
                Interval interval = intervalAt(row, x, y);
                // if the interval is filtered, it can't be selected
                if (interval != null && (_intervalFilter == null || _intervalFilter.isInResult(interval))) {
                    _tbvi.displayIntervalContextMenu(interval, x, y);
                } else {
                    _tbvi.displayBodyContextMenu(x, y);
                }
            }
        }

    }

    /**
     * Cancel ongoing internal drag.
     */
    private void cancelDrag() {
        if (_draggedMarker != null) {
            // dragging a marker
            _draggedMarker.setDate(_markerDragStart);
            _draggedMarker = null;
            _tbvi.setCursor(Cursor.DEFAULT_CURSOR);
            _tbvi.repaint();
        } else if (_changingInterval != null && _draggedInterval) {
            // dragging an interval
            _changingInterval.setBegin(_originalInterval.getBegin());
            _changingInterval.setEnd(_originalInterval.getEnd());

            // inform listeners about the cancellation
            fireIntervalChangeCancelled(_intervalDraggedRow, _changingInterval);

            _changingInterval = null;
            _tbvi.setCursor(Cursor.DEFAULT_CURSOR);

            if (_dragAllSelectedIntervals && _draggedIntervals != null) {
                for (int i = 0; i < _draggedIntervals.size(); i++) {
                    Interval orig = _originalIntervals.get(i);
                    Interval changing = _draggedIntervals.get(i);
                    TimeBarRow row = _draggedIntervalsRows.get(i);
                    changing.setBegin(orig.getBegin());
                    changing.setEnd(orig.getEnd());
                    fireIntervalChangeCancelled(row, changing);
                }
            }

        } else if (_changingInterval != null && !_draggedInterval) {
            // dragging an interval edge
            _changingInterval.setBegin(_originalInterval.getBegin());
            _changingInterval.setEnd(_originalInterval.getEnd());

            // inform listeners about the cancellation
            fireIntervalChangeCancelled(_intervalDraggedRow, _changingInterval);

            _changingInterval = null;
            _tbvi.setCursor(Cursor.DEFAULT_CURSOR);
        }

    }

    /** coordinate a context menu has been requested to pop up. */
    protected Point _ctxCoordinate;
    /** row above that a context menu has been requested to pup up. */
    protected TimeBarRow _ctxRow;
    /** date for the ctx coordinate. */
    protected JaretDate _ctxDate;

    /**
     * Retrieve the location of a context menu popup request.
     * 
     * @return the ccordinate
     */
    public Point getCtxCoordinate() {
        return _ctxCoordinate;
    }

    /**
     * Retrieve the row above that a popup menu request has occurred.
     * 
     * @return the row or <code>null</code> if none could be determined
     */
    public TimeBarRow getCtxRow() {
        return _ctxRow;
    }

    /**
     * Retrieve the row and date of the click leading to the activation of a context menu.
     * 
     * @return Pair containing the row and date of the click position. Might be <code>null</code> if no click has been
     * recorded.
     */
    public Pair<TimeBarRow, JaretDate> getPopUpInformation() {
        Pair<TimeBarRow, JaretDate> result = new Pair<TimeBarRow, JaretDate>(_ctxRow, _ctxDate);
        return result;
    }

    // *** End of MouseListener

    // *** MouseMotionListener
    // TODO the state of the ongoing drags and changes could be wrapped more nicely in some state objects
    // the differentiation between dragging one or more than one is not really nicely coded

    /** copy of a dragged or resized interval containing the original bounds. */
    protected Interval _originalInterval;

    /** list of copies for all intervals selected when a drag started. */
    protected List<Interval> _originalIntervals;

    /** list of draged intervals excluding the one on that the drag started. */
    protected List<Interval> _draggedIntervals;
    /** cache for the row information for the dragged intervals. */
    protected List<TimeBarRow> _draggedIntervalsRows;

    /** x ccordinate of the start of an internal drag/resize. */
    protected int _startIntervalDragX;

    /** date of drag/resize start. */
    protected JaretDate _startIntervalDragDate = null;

    /** last date that has been used in a drag/resize. */
    protected JaretDate _lastDragDate;

    /** start date of a marker drag. */
    protected JaretDate _markerDragStart;

    /** the row of the dragged/resized interval. */
    protected TimeBarRow _intervalDraggedRow;

    /** number of pixels that the viewer has been scrolled during the las drag cycle. */
    protected int _scrolledlastDrag;

    /** last difference to a gridsnap position (used when dragging an interval). */
    protected double _lastGridSnapDifference = 0;
    /** list of lat grid snap differences, used when more than one interval is beeing dragged. */
    protected List<Double> _lastGridSnapDifferences;

    /**
     * Handle mouse dragging.
     * 
     * @param x new x coordinate
     * @param y new y coordinate
     * @param modifierMask modifiers (awt)
     */
    public void mouseDragged(int x, int y, int modifierMask) {
        // limit when out of the diagram rect
        Point coord = limitCoord(x, y);
        JaretDate xdate = dateForXY(coord.x, coord.y);

        boolean horizontal = _orientation == Orientation.HORIZONTAL;
        if (_draggedMarker != null) {
            // dragging a marker
            _draggedMarker.setDate(xdate);
            if (_autoscroll) {
                scrollDateToVisible(xdate);
            }
        } else if (_changingInterval != null && _draggedInterval) {
            dragInterval(_originalInterval, _draggedInterval, _lastDragDate, xdate, x, y, _dragAllSelectedIntervals);
        } else if (_changingInterval != null && !_draggedInterval) {
            // dragging an interval edge
            double diffSeconds = -1.0 * _startIntervalDragDate.diffMilliSeconds(xdate) / 1000.0;
            // gridding
            diffSeconds = calcGridSnap(diffSeconds, 0, _intervalDraggedRow, _changingInterval, -1);
            if (_draggedIntervalEdgeLeft) {
                JaretDate newBegin = _originalInterval.getBegin().copy();
                newBegin.advanceSeconds(diffSeconds);
                // check allowance
                boolean allowed = isNewBeginAllowed(_intervalDraggedRow, _changingInterval, newBegin);
                if (allowed) { // modify if allowed
                    _changingInterval.setBegin(newBegin);
                    if (_autoscroll) {
                        scrollDateToVisible(newBegin);
                    }
                }
            } else {
                JaretDate newEnd = _originalInterval.getEnd().copy();
                newEnd.advanceSeconds(diffSeconds);
                // check allowance
                boolean allowed = isNewEndAllowed(_intervalDraggedRow, _changingInterval, newEnd);
                if (allowed) { // modify if allowed
                    _changingInterval.setEnd(newEnd);
                    if (_autoscroll) {
                        scrollDateToVisible(newEnd);
                    }
                }
            }
            // inform listeners about the intermediate change
            fireIntervalIntermediateChange(_intervalDraggedRow, _changingInterval, _originalInterval.getBegin(),
                    _originalInterval.getEnd());
        } else if (_selectionRect != null) {
            // multiple selection with rectangle selection ongoing
            _selectionRect.width = x - _selectionRect.x;
            _selectionRect.height = y - _selectionRect.y;
            if (_lastSelRect != null) {
                rectRepaint(_lastSelRect);
            }
            Rectangle curSelRect = normalizeRectangle(_selectionRect);
            selectIntervals(curSelRect);
            rectRepaint(curSelRect);
            fireSelectionRectChanged();
        } else if (_regionSelection != null && (modifierMask & InputEvent.SHIFT_DOWN_MASK) != 0) { // check shift
            // pressed!
            if (_lastRegionSelection != null) {
                _tbvi.repaint(calcRect(_lastRegionSelection));
            }
            JaretDate curDate = dateForCoord(x, y);
            TimeBarRow row = rowForXY(x, y);

            // update time
            if (curDate.compareTo(_regionStartDate) > 0) {
                _regionSelection.startDate = _regionStartDate;
                _regionSelection.endDate = curDate;
            } else {
                _regionSelection.endDate = _regionStartDate;
                _regionSelection.startDate = curDate;
            }

            // update rows
            if (row != null) {
                int startRowIdx = _rowList.indexOf(_regionStartRow);
                int curIdx = _rowList.indexOf(row);
                if (startRowIdx <= curIdx) {
                    _regionSelection.startRow = _regionStartRow;
                    _regionSelection.endRow = row;
                } else {
                    _regionSelection.endRow = _regionStartRow;
                    _regionSelection.startRow = row;
                }
            }
            _tbvi.repaint(calcRect(_regionSelection));

            _lastRegionSelection = _regionSelection;

            fireRegionRectChanged();
        } else if (_hierarchyLineDragging) {
            int value = horizontal ? x : y;
            if (value > MIN_DRAG_HIERARCHY_WIDTH) {
                setHierarchyWidth(value);
            }
        } else if (_headerLineDragging) {
            int value = (horizontal ? x : y) - _hierarchyWidth;
            if (value > MIN_DRAG_HEADER_WIDTH) {
                setYAxisWidth(value);
            }
        } else if (_heightDraggedRow != null) {
            int beginCoord = yForRow(_heightDraggedRow);
            int value = (horizontal ? y : x) - beginCoord;
            if (value > MIN_ROW_HEIGHT) {
                _timeBarViewState.setRowHeight(_heightDraggedRow, value);
            }
        } else {
            // no dragging in the moment ... maybe start one
            // first check for edge drag
            TimeBarRow row = rowForXY(x, y);
            if (row != null) {
                Interval interval = getTouchedInterval(row, x, y);
                if (interval != null && isResizingAllowed(row, interval)) {
                    Rectangle rect = getIntervalBounds(row, interval);
                    int diff = horizontal ? Math.abs(x - rect.x) : Math.abs(y - rect.y);
                    if (diff <= _selectionDelta) {
                        // left
                        _draggedIntervalEdgeLeft = true;
                        _tbvi.setCursor(horizontal ? Cursor.E_RESIZE_CURSOR : Cursor.N_RESIZE_CURSOR);
                    } else {
                        // right
                        _draggedIntervalEdgeLeft = false;
                        _tbvi.setCursor(horizontal ? Cursor.W_RESIZE_CURSOR : Cursor.S_RESIZE_CURSOR);
                    }
                    _changingInterval = interval;
                    _draggedInterval = false;
                    _originalInterval = new IntervalImpl();
                    _originalInterval.setBegin(interval.getBegin());
                    _originalInterval.setEnd(interval.getEnd());
                    _startIntervalDragDate = xdate;
                    _intervalDraggedRow = row;
                    // inform listeners about the starting edge darg
                    fireIntervalChangeStarted(_intervalDraggedRow, _changingInterval);
                }
            }
            // if not edge drag check for whole drag
            if (_changingInterval == null) {
                List<Interval> intervals = getIntervalsAt(x, y);
                if (intervals.size() == 1) {
                    Interval interval = intervals.get(0);
                    boolean shiftAllowed = isShiftingAllowed(row, interval);

                    // check whether other intervals have to be shifted and copy them
                    // check allowance for the shift as well
                    // do not include the interval that is the main driver in the drag (that is beeing dragged)
                    if (_dragAllSelectedIntervals && shiftAllowed) {
                        int size = getSelectionModel().getSelectedIntervals().size();
                        _originalIntervals = new ArrayList<Interval>(size);
                        _draggedIntervals = new ArrayList<Interval>(size);
                        _draggedIntervalsRows = new ArrayList<TimeBarRow>(size);
                        _lastGridSnapDifferences = new ArrayList<Double>(size);
                        for (int i = 0; i < size; i++) {
                            _lastGridSnapDifferences.add(0.0);
                        }

                        for (Interval selInterval : getSelectionModel().getSelectedIntervals()) {
                            TimeBarRow selIntervalRow = getModel().getRowForInterval(selInterval);
                            if (!isShiftingAllowed(selIntervalRow, interval)) {
                                shiftAllowed = false;
                                break;
                            }
                            if (!selInterval.equals(interval)) {
                                Interval copy = new IntervalImpl();
                                copy.setBegin(selInterval.getBegin().copy());
                                copy.setEnd(selInterval.getEnd().copy());
                                _originalIntervals.add(copy);
                                _draggedIntervals.add(selInterval);
                                _draggedIntervalsRows.add(selIntervalRow);
                            }

                        }
                    }

                    if (shiftAllowed) {
                        _changingInterval = interval;
                        _draggedInterval = true;
                        _originalInterval = new IntervalImpl();
                        _originalInterval.setBegin(interval.getBegin());
                        _originalInterval.setEnd(interval.getEnd());
                        _startIntervalDragDate = xdate;
                        _intervalDraggedRow = row;
                        _tbvi.setCursor(Cursor.MOVE_CURSOR);

                        // inform listeners about the starting drag
                        fireIntervalChangeStarted(_intervalDraggedRow, _changingInterval);
                        if (_draggedIntervals != null && _draggedIntervals.size() > 0) {
                            for (int i = 0; i < _draggedIntervals.size(); i++) {
                                fireIntervalChangeStarted(_draggedIntervalsRows.get(i), _draggedIntervals.get(i));
                            }
                        }
                    }
                }
            }
        }
        _lastDragDate = xdate;
    }

    /**
     * Handle dragging of an interval. If the dragSelected flag is set, all selected intervals will be dragged. The
     * leading (i.e. autoscrolling) interval will that that caused the drag first time.
     * 
     * @param originalInterval original interval
     * @param draggedInterval interval that is been dragged
     * @param lastDragDate date of the last drag
     * @param currentDragDate current date for the cursor position
     * @param x x coord of the cursor
     * @param y y coord of the cursor
     * @param dragSelected if <code>true</code> all selected intervals will be dragged.
     */
    private void dragInterval(Interval originalInterval, boolean draggedInterval, JaretDate lastDragDate,
            JaretDate currentDragDate, int x, int y, boolean dragSelected) {
        boolean horizontal = _orientation == Orientation.HORIZONTAL;

        // normal case: drag the delta between the two positions
        double deltaSeconds = currentDragDate.diffMilliSeconds(lastDragDate) / 1000.0;
     //   System.out.println("deltaseconds " + deltaSeconds);
        deltaSeconds += _scrolledlastDrag;
      //  System.out.println("deltaseconds2 " + deltaSeconds);

        // if the cursor is outside the diagram rect, use the autoscroll delta to determine the new
        // position
        if (horizontal) {
            if (x > _diagramRect.x + _diagramRect.width) {
                deltaSeconds = secsForPixelDiff(_autoscrollDelta);
            } else if (x < _diagramRect.x) {
                deltaSeconds = -secsForPixelDiff(_autoscrollDelta);
            }
        } else {
            if (y > _diagramRect.y + _diagramRect.height) {
                deltaSeconds = secsForPixelDiff(_autoscrollDelta);
            } else if (y < _diagramRect.y) {
                deltaSeconds = -secsForPixelDiff(_autoscrollDelta);
            }
        }

        double deltaSecondsSnap = calcGridSnap(deltaSeconds, _lastGridSnapDifference, _intervalDraggedRow,
                _changingInterval, -1);

        JaretDate newBegin = _changingInterval.getBegin().copy();
        newBegin.advanceSeconds(deltaSecondsSnap);
        JaretDate newEnd = _changingInterval.getEnd().copy();
        newEnd.advanceSeconds(deltaSecondsSnap);

        List<Double> deltas = new ArrayList<Double>();
        // check whether the shift is allowed
        boolean allowed = isShiftingAllowed(_intervalDraggedRow, _changingInterval, newBegin);
        if (_dragAllSelectedIntervals && allowed && _draggedIntervals != null) {
            for (int i = 0; i < _draggedIntervals.size(); i++) {
                deltaSecondsSnap = calcGridSnap(deltaSeconds, _lastGridSnapDifferences.get(i), _draggedIntervalsRows
                        .get(i), _draggedIntervals.get(i), i);
                deltas.add(deltaSecondsSnap);
                JaretDate nb = _draggedIntervals.get(i).getBegin().copy().advanceSeconds(deltaSecondsSnap);
                allowed = allowed & isShiftingAllowed(_draggedIntervalsRows.get(i), _draggedIntervals.get(i), nb);
            }
        }

        if (allowed) {
            // always do the setting of the interval bounds in the right order to ensure interval consistency with
            // end>begin
            if (deltaSecondsSnap > 0) {
                _changingInterval.setEnd(newEnd);
                _changingInterval.setBegin(newBegin);
            } else {
                _changingInterval.setBegin(newBegin);
                _changingInterval.setEnd(newEnd);
            }
            if (_autoscroll && _lastDragDate != null) {
                if (deltaSeconds < 0) {
                    int scrolledSeconds = scrollDateToVisible(newBegin);
                    _scrolledlastDrag = scrolledSeconds;
                } else {
                    int scrolledSeconds = scrollDateToVisible(newEnd);
                    _scrolledlastDrag = scrolledSeconds;
                }
            }

            if (_dragAllSelectedIntervals) {
                for (int i = 0; i < _draggedIntervals.size(); i++) {
                    Interval di = _draggedIntervals.get(i);
                    double deltaSnap = deltas.get(i);
                    JaretDate nb = di.getBegin().copy().advanceSeconds(deltaSnap);
                    JaretDate ne = di.getEnd().copy().advanceSeconds(deltaSnap);
                    di.setBegin(nb);
                    di.setEnd(ne);
                }
            }

            // inform listeners about the intermediate change
            fireIntervalIntermediateChange(_intervalDraggedRow, _changingInterval, _originalInterval.getBegin(),
                    _originalInterval.getEnd());
            if (_dragAllSelectedIntervals) {
                for (int i = 0; i < _draggedIntervals.size(); i++) {
                    fireIntervalIntermediateChange(_draggedIntervalsRows.get(i), _draggedIntervals.get(i),
                            _originalIntervals.get(i).getBegin(), _originalIntervals.get(i).getEnd());
                }
            }
        }

    }

    /**
     * Calculate a time eqivalent for a pixel delta value. This does not take variable scaling into account.
     * 
     * @param pixDif delta pixel value
     * @return number of seconfs corresponding to the delta
     */
    private int secsForPixelDiff(int pixDif) {
        if (_milliAccuracy) {
            int diffSec = (int) ((double) pixDif / _pixelPerSeconds);
            return diffSec;
        } else {
            int diffSec = (int) ((double) pixDif / _pixelPerSeconds);
            return diffSec;
        }

    }

    /**
     * Limits the distance of the cursor position when aoutside the diagram rect. The limit is set by the
     * autoscrollDelta value.
     * 
     * @param x original coord x
     * @param y original coord y
     * @return Point containing the corrected position
     */
    private Point limitCoord(int x, int y) {
        int limit = _autoscrollDelta;
        if (x < _diagramRect.x - limit) {
            x = _diagramRect.x - limit;
        } else if (x > _diagramRect.x + _diagramRect.width + limit) {
            x = _diagramRect.x + _diagramRect.width + limit;
        }
        if (y < _diagramRect.y - limit) {
            y = _diagramRect.y - limit;
        } else if (y > _diagramRect.y + _diagramRect.height + limit) {
            x = _diagramRect.y + _diagramRect.height + limit;
        }

        return new Point(x, y);
    }

    /**
     * Calculate the difference in seconds to the next grid snap position. The grid snap position is defined by the
     * first(!) interval modificator that claims it is applicable (isApplicable). If no interval modificator is
     * applicable no grid snap will be applied. The last calculated difference to the actual grid snap is stored in a
     * variable. This is not really nice, but is used when dragging an interval.
     * 
     * @param diffSeconds the measured difference in seconds
     * @param additionalDelta an additional value that is taken into account when calculating the snapped value
     * @param row row of the interval
     * @param interval interval in question
     * @param index the index the index to store the difference in (-1 for default)
     * @return the difference to use after applying the grid snap
     */
    private double calcGridSnap(double diffSeconds, double additionalDelta, TimeBarRow row, Interval interval, int index) {
        if (_intervalModificators != null && _intervalModificators.size() > 0) {
            IntervalModificator modificator = null;
            for (IntervalModificator m : _intervalModificators) {
                if (m.isApplicable(row, interval)) {
                    modificator = m;
                    break;
                }
            }
            if (modificator != null) {
                double newVal;
                double gridsnap = -1;
                // if the extended interface is available: try
                if (modificator instanceof IIntervalModificator) {
                    gridsnap = ((IIntervalModificator) modificator).getSecondGridSnap(row, interval);
                }
                // if no gridsnap available try the generic interface
                if (gridsnap < 0) {
                    gridsnap = modificator.getSecondGridSnap();
                }
                if (gridsnap < 0) {
                    return diffSeconds;
                }
                diffSeconds += additionalDelta;
                if (Math.abs(diffSeconds % gridsnap) > gridsnap / 2) {
                    double off;
                    if (diffSeconds < 0) {
                        off = -gridsnap;
                    } else {
                        off = gridsnap;
                    }
                    newVal = diffSeconds - diffSeconds % gridsnap + off;
                } else {
                    newVal = diffSeconds - diffSeconds % gridsnap;
                }
                if (index == -1) {
                    _lastGridSnapDifference = diffSeconds - newVal;
                } else {
                    _lastGridSnapDifferences.set(index, diffSeconds - newVal);
                }
                return newVal;
            }
        }
        return diffSeconds;

    }

    /**
     * Check whether shifting an interval is allowed in general.
     * 
     * @param row row of the interval
     * @param interval interval in question
     * @return true if shifting is allowed
     */
    private boolean isShiftingAllowed(TimeBarRow row, Interval interval) {
        if (_intervalModificators == null || _intervalModificators.size() == 0) {
            return false;
        }
        boolean allowed = true;

        for (IntervalModificator modificator : _intervalModificators) {
            allowed = allowed
                    && (!modificator.isApplicable(row, interval) || modificator.isShiftingAllowed(row, interval));
        }
        return allowed;
    }

    /**
     * Check whether a specific shift is allowed for an interval.
     * 
     * @param row row of the interval
     * @param interval interval to be shifted
     * @param newBegin new begin date
     * @return <code>true</code> if shift is allowed
     */
    private boolean isShiftingAllowed(TimeBarRow row, Interval interval, JaretDate newBegin) {
        if (_intervalModificators == null || _intervalModificators.size() == 0) {
            return false;
        }
        boolean allowed = true;

        for (IntervalModificator modificator : _intervalModificators) {
            allowed = allowed
                    && (!modificator.isApplicable(row, interval) || modificator.shiftAllowed(row, interval, newBegin));
        }
        return allowed;
    }

    /**
     * Check whether resizing is allowed for a given Interval.
     * 
     * @param row row of the interval
     * @param interval interval to check resize allowance for
     * @return true if interval is allowed to be resized
     */
    private boolean isResizingAllowed(TimeBarRow row, Interval interval) {
        if (_intervalModificators == null || _intervalModificators.size() == 0) {
            return false;
        }
        boolean result = true;
        if (_intervalModificators != null) {
            for (IntervalModificator modificator : _intervalModificators) {
                result = result
                        && (!modificator.isApplicable(row, interval) || modificator.isSizingAllowed(row, interval));
            }
        }
        return result;
    }

    /**
     * Check whether a new begin dat efor an interval is allowed to be set.
     * 
     * @param row row of the interval
     * @param interval interal to be changed
     * @param newBegin new begin date for the interval
     * @return <code>true</code> if allowed
     */
    private boolean isNewBeginAllowed(TimeBarRow row, Interval interval, JaretDate newBegin) {
        boolean allowed = true;
        for (IntervalModificator modificator : _intervalModificators) {
            allowed = allowed
                    && (!modificator.isApplicable(row, interval) || modificator
                            .newBeginAllowed(row, interval, newBegin));
        }
        return allowed;
    }

    /**
     * Check whether a new end date for an interval is allowed to be set.
     * 
     * @param row row of the interval
     * @param interval interal to be changed
     * @param newEnd new end date for the interval
     * @return <code>true</code> if allowed
     */
    private boolean isNewEndAllowed(TimeBarRow row, Interval interval, JaretDate newEnd) {
        boolean allowed = true;
        for (IntervalModificator modificator : _intervalModificators) {
            allowed = allowed
                    && (!modificator.isApplicable(row, interval) || modificator.newEndAllowed(row, interval, newEnd));
        }
        return allowed;
    }

    /**
     * Repaint the region that has been covered by the bounds of a rectangle.
     * 
     * @param rect rectangle that defines the regions that need to be repainted (its former bounds)
     */
    private void rectRepaint(Rectangle rect) {
        _tbvi.repaint(rect.x, rect.y, rect.width, 1);
        _tbvi.repaint(rect.x, rect.y + rect.height, rect.width, 1);
        _tbvi.repaint(rect.x, rect.y, 1, rect.height);
        _tbvi.repaint(rect.x + rect.width, rect.y, 1, rect.height);
    }

    /**
     * Handle simple mouse movements. This mainly means: change he cursor for selective areas.
     * 
     * @param x x coordinate
     * @param y y coordinate
     */
    public void mouseMoved(int x, int y) {
        boolean horizontal = _orientation == Orientation.HORIZONTAL;
        boolean nothingHitInDiagramArea = true;
        if (_lineDraggingAllowed && (hierarchyLineHit(x, y) || headerLineHit(x, y))) {
            _tbvi.setCursor(Cursor.HAND_CURSOR);
            nothingHitInDiagramArea = false;
        } else if (_rowHeightDraggingAllowed && rowLineHit(x, y)) {
            _tbvi.setCursor(Cursor.HAND_CURSOR);
            nothingHitInDiagramArea = false;
        } else if (_diagramRect != null && _diagramRect.contains(x, y)) {
            // in the diagram area check for interval bounds and change cursor
            // if an interval modificator
            // is set and resizing is allowed
            TimeBarRow row = rowForXY(x, y);
            if (row != null) {
                Interval interval = getTouchedInterval(row, x, y);
                if (interval != null && isResizingAllowed(row, interval)) {
                    JaretDate d = dateForXY(x, y);
                    long eastDiff = Math.abs(d.diffMilliSeconds(interval.getBegin()));
                    long westDiff = Math.abs(d.diffMilliSeconds(interval.getEnd()));
                    if (eastDiff < westDiff) {
                        _tbvi.setCursor(horizontal ? Cursor.E_RESIZE_CURSOR : Cursor.N_RESIZE_CURSOR);
                    } else {
                        _tbvi.setCursor(horizontal ? Cursor.W_RESIZE_CURSOR : Cursor.S_RESIZE_CURSOR);
                    }
                    nothingHitInDiagramArea = false;
                } else {
                    _tbvi.setCursor(Cursor.DEFAULT_CURSOR);
                }
            }
        } else {
            _tbvi.setCursor(Cursor.DEFAULT_CURSOR);
        }
        // if the mouse is in the axis area and hits a marker change the
        // cursor (or if dragging in the area is allowed)
        if (nothingHitInDiagramArea
                && ((_xAxisRect != null && _xAxisRect.contains(x, y)) || _markerDraggingInDiagramArea)) {
            TimeBarMarker marker = getMarkerForXY(x, y);
            if (marker != null) {
                _tbvi.setCursor(Cursor.HAND_CURSOR);
            } else {
                _tbvi.setCursor(Cursor.DEFAULT_CURSOR);
            }
        }

    }

    /**
     * Check whether the hierarchy delimiting line is hit by the given location.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return true if the location is above or in the range of the selection delta
     */
    private boolean hierarchyLineHit(int x, int y) {
        if (_orientation == Orientation.HORIZONTAL) {
            return (_hierarchyWidth > 0 && Math.abs(_hierarchyWidth - x) < _selectionDelta);
        } else {
            return (_hierarchyWidth > 0 && Math.abs(_hierarchyWidth - y) < _selectionDelta);
        }
    }

    /**
     * Check whether a row delimiter line is hit by a coordinate.
     * 
     * @param x x coord
     * @param y y coord
     * @return <code>true</code> if a row line is hit
     */
    public boolean rowLineHit(int x, int y) {
        if (_yAxisRect.contains(x, y) || _hierarchyRect.contains(x, y)) {
            int coord;
            int max;
            if (_orientation == Orientation.HORIZONTAL) {
                coord = y;
                coord -= _yAxisRect.y;
                max = _yAxisRect.height;
            } else {
                coord = x;
                coord -= _yAxisRect.x;
                max = _yAxisRect.width;
            }
            TimeBarRow row = getRow(_firstRow);
            int idx = _firstRow;
            int endCoord = _timeBarViewState.getRowHeight(row) - _firstRowPixelOffset;

            while (endCoord < max && idx < _rowList.size()) {
                if (Math.abs(endCoord - coord) < _selectionDelta) {
                    return true;
                }
                idx++;
                if (idx > _rowList.size() - 1) {
                    break;
                }
                row = getRow(idx);
                endCoord += _timeBarViewState.getRowHeight(row);
            }
        }
        return false;
    }

    /**
     * Retrieve the row identified by it's bottom border.
     * 
     * @param x x coordinate in the control
     * @param y y coordinate in the control
     * @return row or <code>null</code>
     */
    private TimeBarRow getRowByBottomLine(int x, int y) {
        if (_diagramRect.contains(x, y) || _yAxisRect.contains(x, y) || _hierarchyRect.contains(x, y)) {
            int coord;
            int max;
            if (_orientation == Orientation.HORIZONTAL) {
                coord = y;
                coord -= _diagramRect.y;
                max = _diagramRect.height;
            } else {
                coord = x;
                coord -= _diagramRect.x;
                max = _diagramRect.width;
            }
            TimeBarRow row = getRow(_firstRow);
            int idx = _firstRow;
            int endCoord = _timeBarViewState.getRowHeight(row) - _firstRowPixelOffset;

            while (endCoord < max) {
                if (Math.abs(endCoord - coord) < _selectionDelta) {
                    return row;
                }
                idx++;
                row = getRow(idx);
                endCoord += _timeBarViewState.getRowHeight(row);
            }
        }
        return null;
    }

    /**
     * Check whether the header delimiting line is hit by the given location.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return true if the location is above or in the range of the selection delta
     */
    private boolean headerLineHit(int x, int y) {
        if (_orientation == Orientation.HORIZONTAL) {
            return (_yAxisWidth > 0 && Math.abs(_hierarchyWidth + _yAxisWidth - x) < _selectionDelta);
        } else {
            return (_yAxisWidth > 0 && Math.abs(_hierarchyWidth + _yAxisWidth - y) < _selectionDelta);
        }
    }

    /**
     * Normalizes a rectangle: x,y will be the upper left corner.
     * 
     * @param rect rectangle to normalize
     * @return normalized recangle
     */
    private Rectangle normalizeRectangle(Rectangle rect) {
        int x = Math.min(rect.x, rect.x + rect.width);
        int y = Math.min(rect.y, rect.y + rect.height);
        int width = Math.abs(rect.width);
        int height = Math.abs(rect.height);
        return new Rectangle(x, y, width, height);
    }

    /**
     * Ensures that all intervals in the given rectangle are selected. The intervals have to be either complete in the
     * rectangle (complete area) or if the renderer sets the containinng rectangle this rectangle has to be inside the
     * selecting rectangle
     * 
     * @param curSelRect selction rectangle
     */
    private void selectIntervals(Rectangle curSelRect) {
        // get the intervals in question
        List<Interval> intervals = getIntervals(curSelRect);
        // now ensure they are in the selection of intervals
        // & limit the selection to the rect selection
        for (Interval interval : intervals) {
            if (!_selectionModel.getSelectedIntervals().contains(interval)) {
                _selectionModel.addSelectedInterval(interval);
            }
        }
        List<Interval> selection = new ArrayList<Interval>();
        selection.addAll(_selectionModel.getSelectedIntervals());

        for (Interval interval : selection) {
            if (!intervals.contains(interval)) {
                _selectionModel.remSelectedInterval(interval);
            }
        }
    }

    /**
     * Retrieve all intervals inside a selection rectangle.
     * 
     * @param curSelRect the marking rectangle.
     * @return a list of intervals inside the rectangle
     */
    private List<Interval> getIntervals(Rectangle curSelRect) {
        List<Interval> result = new ArrayList<Interval>();
        boolean horizontal = _orientation == Orientation.HORIZONTAL;
        // first get the rows
        List<TimeBarRow> rows = horizontal ? getRows(curSelRect.y, curSelRect.y + curSelRect.height) : getRows(
                curSelRect.x, curSelRect.x + curSelRect.width);
        // calculate the dates
        JaretDate begin = horizontal ? dateForCoord(curSelRect.x) : dateForCoord(curSelRect.y);
        JaretDate end = horizontal ? dateForCoord(curSelRect.x + curSelRect.width) : dateForCoord(curSelRect.y
                + curSelRect.height);
        // go through all intervals and check whether their represenation is
        // really inside the rect
        for (TimeBarRow row : rows) {
            List<Interval> intervals = row.getIntervals(begin, end);
            for (Interval interval : intervals) {
                boolean overlapping = getTimeBarViewState().getDrawOverlapping(row) ? false : _overlapStrategy
                        .getOverlapInfo(row, interval).overlappingCount > 0;
                Rectangle intervalRect = getIntervalBounds(row, interval);
                // get the containing rect or - if not set by the renderer -
                // use the component bounds
                Rectangle containingRect = _tbvi.timeBarContainingRect(interval, intervalRect, overlapping);
                if (containingRect == null) {
                    containingRect = intervalRect;
                }
                // now check whether the rect is contained
                if (curSelRect.contains(containingRect)) {
                    result.add(interval);
                }
            }
        }

        return result;
    }

    /**
     * Retrieve the interval that has a bound near to the given x coordinate in a given row. If more than one interval
     * might be hit, the exact coordinates are checked. if the location checked is in beetween two intervals in reach,
     * the first interval will be returned.
     * 
     * @param row row in question
     * @param x x coordinate to be checked for intervals
     * @param y y coordinate
     * @return nearest interval with bound inside the area around x or <code>null</code>
     */
    private Interval getTouchedInterval(TimeBarRow row, int x, int y) {
        // check only intervals currently displayed
        List<Interval> intervals = row.getIntervals(getStartDate(), getEndDate());
        List<Interval> candidates = new ArrayList<Interval>(5);
        List<Rectangle> candidateRects = new ArrayList<Rectangle>(5);
        for (Interval interval : intervals) {
            Rectangle intervalRect = getIntervalBounds(row, interval);
            if (_orientation == Orientation.HORIZONTAL) {
                if (y >= intervalRect.y && y <= intervalRect.y + intervalRect.height) {
                    if (Math.abs(x - intervalRect.x) <= _selectionDelta) {
                        candidates.add(interval);
                        candidateRects.add(intervalRect);
                    } else if (Math.abs(intervalRect.x + intervalRect.width - x) <= _selectionDelta) {
                        candidates.add(interval);
                        candidateRects.add(intervalRect);
                    }
                }
            } else {
                if (x >= intervalRect.x && x <= intervalRect.x + intervalRect.width) {
                    if (Math.abs(y - intervalRect.y) <= _selectionDelta) {
                        candidates.add(interval);
                        candidateRects.add(intervalRect);
                    } else if (Math.abs(intervalRect.y + intervalRect.height - y) <= _selectionDelta) {
                        candidates.add(interval);
                        candidateRects.add(intervalRect);
                    }
                }
            }
        }
        // check candidates
        if (candidates.size() == 0) {
            return null;
        }
        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        for (int i = 0; i < candidates.size(); i++) {
            Interval interval = candidates.get(i);
            Rectangle rect = candidateRects.get(i);
            if (rect.contains(x, y)) {
                return interval;
            }
        }
        // there might be the case that no inteval is a direct hit ...
        // then just use the first one
        return candidates.get(0);

    }

    /**
     * Retrieve the rows between two coordinates (either y or x depending on the orientation).
     * 
     * @param c1 c1
     * @param c2 c2
     * @return list of rows
     */
    private List<TimeBarRow> getRows(int c1, int c2) {
        List<TimeBarRow> result = new ArrayList<TimeBarRow>();
        // boolean horizontal = _orientation == Orientation.HORIZONTAL;
        for (int r = _firstRow; r <= _firstRow + getRowsDisplayed() && r < getRowCount(); r++) {
            // int startC = (r - _firstRow) * _rowHeight + (horizontal ? _diagramRect.y : _diagramRect.x)
            // - _firstRowPixelOffset;
            // int endC = startC + _rowHeight;
            int startC = yForRow(getRow(r));
            int endC = startC + _timeBarViewState.getRowHeight(getRow(r));
            if ((startC >= c1 && startC <= c2) || (endC >= c1 && endC <= c2) || (c1 >= startC && c1 <= endC)) {
                result.add(getRow(r));
            }
        }
        return result;
    }

    /**
     * @return Returns the drawRowGrid.
     */
    public boolean getDrawRowGrid() {
        return _drawRowGrid;
    }

    /**
     * @param drawRowGrid The drawRowGrid to set.
     */
    public void setDrawRowGrid(boolean drawRowGrid) {
        _drawRowGrid = drawRowGrid;
        if (_tbvi != null) {
            _tbvi.repaint();
        }
    }

    /**
     * Highlight a row by giving the display y coordinate.
     * 
     * @param y y coordinate
     */
    public void highlightRow(int y) {
        TimeBarRow row = rowForY(y);
        highlightRow(row);
    }

    /**
     * Highlight a row.
     * 
     * @param row the row to be highlighted
     */
    public void highlightRow(TimeBarRow row) {
        if (row != _highlightedRow) {
            TimeBarRow oldRow = _highlightedRow;
            _highlightedRow = row;
            _tbvi.repaint(getRowBounds(_highlightedRow));
            if (oldRow != null) {
                // repaint the row highlighted before
                _tbvi.repaint(getRowBounds(oldRow));
            }
        }
    }

    /**
     * Dehighlight a highlighted row.
     * 
     */
    public void deHighlightRow() {
        if (_highlightedRow != null) {
            TimeBarRow row = _highlightedRow;
            _highlightedRow = null;
            _tbvi.repaint(getRowBounds(row));
        }
    }

    /**
     * Retrieve the highlighted row if prsent.
     * 
     * @return the highlilghted row or <code>null</code> if no row is highlighted
     */
    public TimeBarRow getHighlightedRow() {
        return _highlightedRow;
    }

    /**
     * Retrieve the marker currently dragged.
     * 
     * @return the currently dragged marker or <code>null</code> if no marker is beeing dragged
     */
    public TimeBarMarker getDraggedMarker() {
        return _draggedMarker;
    }

    /**
     * Retrieve the selection rect.
     * 
     * @return the current selection rect if present or <code>null</code> if none is present
     */
    public Rectangle getSelectionRect() {
        return _selectionRect;
    }

    /**
     * Set the last selection rectangle.
     * 
     * @param rect rectangle
     */
    public void setLastSelRect(Rectangle rect) {
        _lastSelRect = rect;
    }

    /**
     * Retrieve the last selection rectangle.
     * 
     * @return rect last selection rect if any or <code>null</code>
     */
    public Rectangle getLastSelRect() {
        return _lastSelRect;
    }

    /**
     * Retrieve the tooltip text for a given location.
     * 
     * @param x x coordinate in the component
     * @param y y coordinate in the component
     * @return the tooltip text or null if there is no text
     */
    public String getToolTipText(int x, int y) {
        // check for Marker
        TimeBarMarker marker = getMarkerForXY(x, y);
        if (marker != null) {
            return marker.getDescription();
        }

        // retrieve the row
        if (_diagramRect.contains(x, y)) {
            TimeBarRow row = rowForXY(x, y);
            if (row != null) {
                // retrieve all intervals in the row for the x coordinate
                String tooltip = null;
                List<Interval> intervals = getIntervalsAt(row, x, y);
                // no intervals? Tooltip of the diagram itself
                if (intervals.size() == 0) {
                    // may be over a relation
                    return _tbvi.getRelationTooltip(x, y);
                }

                Interval interval;
                if (intervals.size() == 1) {
                    interval = intervals.get(0);
                } else {
                    interval = _intervalSelectionStrategy.selectInterval(intervals);
                }
                Rectangle intervalRect = getIntervalBounds(row, interval);
                boolean overlapping = getTimeBarViewState().getDrawOverlapping(row) ? false : _overlapStrategy
                        .getOverlapInfo(row, interval).overlappingCount > 0;
                if (_tbvi.timeBarContains(interval, intervalRect, x - intervalRect.x, y - intervalRect.y, overlapping)) {
                    tooltip = _tbvi.getIntervalToolTipText(interval, intervalRect, x - intervalRect.x, y
                            - intervalRect.y);
                }
                return tooltip;
            }
        } else if (_hierarchyRect.contains(x, y)) {
            // hierarchy
            TimeBarRow row = rowForXY(x, y);
            if (row instanceof TimeBarNode) {
                return _tbvi.getHierarchyToolTipText((TimeBarNode) row, x, y);
            } else {
                // row is not a node ...
                return null;
            }
        } else if (_yAxisRect.contains(x, y)) {
            // header area
            return _tbvi.getHeaderToolTipText(rowForXY(x, y), x, y);
        } else if (_xAxisRect.contains(x, y)) {
            // time scale
            return _tbvi.getTimeScaleToolTipText(x, y);
        }
        return null;
    }

    /**
     * Calculate the rectangle for drawing the header of a given row.
     * 
     * @param row row to calculae the header recct for
     * @return the rectangle for drawing the the header or <code>null</code> if the header is not visible.
     */
    public Rectangle getHeaderRect(TimeBarRow row) {
        if (!isRowDisplayed(row)) {
            return null;
        } else {
            if (_orientation.equals(TimeBarViewerInterface.Orientation.HORIZONTAL)) {
                int y = yForRow(row);
                Rectangle rect = new Rectangle(_yAxisRect.x, y, _yAxisWidth, _timeBarViewState.getRowHeight(row));
                return rect;
            } else {
                int x = yForRow(row);
                Rectangle rect = new Rectangle(x, _yAxisRect.y, _timeBarViewState.getRowHeight(row), _yAxisWidth);
                return rect;
            }
        }
    }

    /**
     * Calculate the rectangle for drawing the hierachy marker of a given row.
     * 
     * @param row row to calculate the hierarchy rect for
     * @return the rectangle for drawing the the hierarchy marker or <code>null</code> if the row is not visible.
     */
    public Rectangle getHierarchyRect(TimeBarRow row) {
        if (!isRowDisplayed(row)) {
            return null;
        } else {
            int y = yForRow(row);
            Rectangle rect = new Rectangle(_hierarchyRect.x, y, _hierarchyWidth, _timeBarViewState.getRowHeight(row));
            return rect;
        }
    }

    /**
     * Produce a simple string representation.
     * 
     * @return simpe string representation
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("TimeBarViewerDelegate[");
        buf.append("Name:" + _name + ",");
        buf.append("StartDate:" + _startDate.toDisplayString() + ",");
        buf.append("MinDate:" + _minDate.toDisplayString() + ",");
        buf.append("MaxDate:" + _maxDate.toDisplayString() + ",");

        buf.append("]");
        return buf.toString();
    }

    /**
     * Set the name of the viewer/component for display or debugging purposes.
     * 
     * @param name name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Get the name of the viewer/component.
     * 
     * @return the name or <code>null</code>
     */
    public String getName() {
        return _name;
    }

    /**
     * Set the autoscroll behaviour. If autoscroll is anabled, drag and select by selection rect will autoscroll the
     * viewer.
     * 
     * @param enableAutoscroll true for enabling autoscroll
     */
    public void setAutoscrollEnabled(boolean enableAutoscroll) {
        _autoscroll = enableAutoscroll;
    }

    /**
     * Get the autoscroll behaviour.
     * 
     * @return true if autoscroll is enabled.
     */
    public boolean isAutoscrollEnabled() {
        return _autoscroll;
    }

    /**
     * Scroll a date into the visible area of the viewer.
     * 
     * @param date date to be shown.
     * @return the number of seconds the start date have been modified
     */
    public int scrollDateToVisible(JaretDate date) {
        JaretDate enddate = getStartDate().copy();
        date = date.copy();
        JaretDate oldStartDate = getStartDate().copy();
        if (_milliAccuracy) {
            enddate.advanceMillis(getMilliSecondsDisplayed());
            if (date.compareTo(getStartDate()) < 0) {
                setStartDate(date);
            } else if (date.compareTo(enddate) > 0) {
                date.advanceMillis(-getMilliSecondsDisplayed());
                setStartDate(date);
            }
        } else {
            enddate.advanceSeconds(getSecondsDisplayed());
            if (date.compareTo(getStartDate()) < 0) {
                setStartDate(date);
            } else if (date.compareTo(enddate) > 0) {
                date.advanceSeconds(-getSecondsDisplayed());
                setStartDate(date);
            }
        }
        return oldStartDate.diffSeconds(getStartDate());
    }

    /**
     * Make sure the specified row is visible.
     * 
     * @param row TimeBarRow to be in the visible area.
     */
    public void scrollRowToVisible(TimeBarRow row) {
        if (!isFiltered(row)) {
            int ridx = _rowList.indexOf(row);
            if (ridx == -1) {
                return;
            }
            if (ridx < _firstRow) {
                setFirstRow(ridx);
            } else if (ridx >= _firstRow + getRowsDisplayed()) {
                setLastRow(ridx);
            } else if (getTimeBarViewState().getUseVariableRowHeights() && ridx >= _firstRow + getRowsDisplayed() - 1) {
                setLastRow(ridx);
            }
        }
    }

    /**
     * Make sure the specified interval is in the visibe area of the viewer. If the interval does not fit in the visible
     * area, the beginning of the interval will be displayed.
     * 
     * @param row TimeBarRow of the interval
     * @param interval inteval.
     */
    public void scrollIntervalToVisible(TimeBarRow row, Interval interval) {
        // MAYBE change, since next call may result in two consequent redraws
        scrollRowToVisible(row);
        if (_milliAccuracy) {
            if (interval.getBegin().compareTo(_startDate) < 0) {
                scrollDateToVisible(interval.getBegin());
            } else if (interval.getEnd().diffMilliSeconds(interval.getBegin()) > getMilliSecondsDisplayed()) {
                setStartDate(interval.getBegin());
            } else {
                scrollDateToVisible(interval.getEnd());
            }
        } else {
            if (interval.getBegin().compareTo(_startDate) < 0) {
                scrollDateToVisible(interval.getBegin());
            } else if (interval.getEnd().diffSeconds(interval.getBegin()) > getSecondsDisplayed()) {
                setStartDate(interval.getBegin());
            } else {
                scrollDateToVisible(interval.getEnd());
            }
        }
    }

    /**
     * Check whether any part of an interval is visible.
     * 
     * @param row row
     * @param interval interval
     * @return true if any part of the interval is rendered.
     */
    public boolean isIntervalVisible(TimeBarRow row, Interval interval) {
        if (!isRowDisplayed(row)) {
            return false;
        }

        if (interval.getBegin().compareTo(_startDate) < 0 && interval.getEnd().compareTo(getEndDate()) >= 0) {
            // interval is partly shown in the middle
            return true;
        } else if (interval.getBegin().compareTo(getEndDate()) > 0) {
            // interval past the displayed area
            return false;
        } else if (interval.getEnd().compareTo(_startDate) < 0) {
            // interval before the displayed area
            return false;
        }
        return true;
    }

    /**
     * Make sure the specified interval is in the visibe area of the viewer. If the interval does not fit in the visible
     * area, the beginning of the interval will be displayed.
     * 
     * @param interval inteval.
     */
    public void scrollIntervalToVisible(Interval interval) {
        TimeBarRow row = _model.getRowForInterval(interval);
        if (row != null) {
            scrollIntervalToVisible(row, interval);
        }
    }

    /**
     * Scroll an interval to specified position (by ration) in the vieable area of the chart. The beginning of the
     * interval will be positioned according to the ratio given.
     * 
     * @param interval interval to scroll to
     * @param horizontalRatio horizontal ratio (0 to 1.0 = left to right)
     * @param verticalRatio vertical ratio (0 to 1.0 = top to bottom)
     */
    public void scrollIntervalToVisible(Interval interval, double horizontalRatio, double verticalRatio) {
        if (horizontalRatio < 0.0 || horizontalRatio > 1.0 || verticalRatio < 0.0 || verticalRatio > 1.0) {
            throw new IllegalArgumentException("ratios have to be in the range from 0.0 to 1.0");
        }
        TimeBarRow row = _model.getRowForInterval(interval);
        if (row != null) {
            scrollIntervalToVisible(row, interval, horizontalRatio, verticalRatio);
        }
    }

    /**
     * Scroll an interval to specified position (by ration) in the vieable area of the chart. The beginning of the
     * interval will be positioned according to the ratio given.
     * 
     * @param interval interval to scroll to
     * @param row row of the interval
     * @param horizontalRatio horizontal ratio (0 to 1.0 = left to right)
     * @param verticalRatio vertical ratio (0 to 1.0 = top to bottom)
     */
    public void scrollIntervalToVisible(TimeBarRow row, Interval interval, double horizontalRatio, double verticalRatio) {
        if (horizontalRatio < 0.0 || horizontalRatio > 1.0 || verticalRatio < 0.0 || verticalRatio > 1.0) {
            throw new IllegalArgumentException("ratios have to be in the range from 0.0 to 1.0");
        }
        // calculate date for ratio
        int secondsDisplayed = getSecondsDisplayed();
        double secondsFromStart = (double) secondsDisplayed * horizontalRatio;
        JaretDate startDate = interval.getBegin().copy().backSeconds(secondsFromStart);

        // calculate row to be the first row
        int pos = (int) (getDiagramRect().height * verticalRatio);
        int destRowIdx = _rowList.indexOf(row);

        // loop to find the new first row
        // no distinction between var/fixed row heights -> just try it
        int togo = pos;
        int idx;
        if (togo > 0) {
            idx = destRowIdx - 1;
        } else {
            idx = destRowIdx;
        }
        int rowHeight = 0;
        while (togo > 0 && idx >= 0) {
            rowHeight = getTimeBarViewState().getRowHeight(_rowList.get(idx));
            togo -= rowHeight;
            idx--;
        }

        if (idx < 0) {
            setFirstRow(0);
        } else {
            setFirstRow(idx, rowHeight - togo);
        }
        setStartDate(startDate);

    }

    /**
     * Add an interval modificator.
     * 
     * @param intervalModificator modificator to add
     */
    public synchronized void addIntervalModificator(IntervalModificator intervalModificator) {
        if (_intervalModificators == null) {
            _intervalModificators = new Vector<IntervalModificator>();
        }
        _intervalModificators.add(intervalModificator);
    }

    /**
     * Remove an interval modificator.
     * 
     * @param intervalModificator modificator to remove
     */
    public void remIntervalModificator(IntervalModificator intervalModificator) {
        if (_intervalModificators != null) {
            _intervalModificators.remove(intervalModificator);
        }
    }

    /**
     * Retrieve the timebarviewstate.
     * 
     * @return the timebar viewstate
     */
    public ITimeBarViewState getTimeBarViewState() {
        return _timeBarViewState;
    }

    /**
     * Retrieve the hierarchical viewstate.
     * 
     * @return the hierarchical viewstate or <code>null</code> if no hierarchical model is used
     */
    public HierarchicalViewState getHierarchicalViewState() {
        return _hierarchicalViewState;
    }

    /**
     * Set the hierarchical viewstate. The hierarchical viewstate will only be used together with a hierarchical model.
     * 
     * @param hvs hierachical viewstate to use
     */
    public void setHierarchicalViewState(HierarchicalViewState hvs) {
        _hierarchicalViewState = hvs;
    }

    /**
     * Set the title string to be displayed.
     * 
     * @param title title to be displayed
     */
    public void setTitle(String title) {
        _title = title;
        if (_tbvi != null) {
            _tbvi.repaint();
        }
    }

    /**
     * Retrieve the title of the viewer.
     * 
     * @return the title
     */
    public String getTitle() {
        return _title;
    }

    /**
     * @return true for overlap drawing mode.
     */
    public boolean isDrawOverlapping() {
        return _drawOverlapping;
    }

    /**
     * Set the drawing mode concerning overlapping intervals.
     * 
     * @param drawOverlapping if set to true all intervals will be painted overlapping each other if they do overlap.
     * False will reduce the space for rendering, stacking the intervals (default).
     */
    public void setDrawOverlapping(boolean drawOverlapping) {
        _drawOverlapping = drawOverlapping;
        if (_tbvi != null) {
            _tbvi.repaint();
        }
    }

    /**
     * Retrieve tha maximal count of overlapping intervals in a row.
     * 
     * @param row row to check
     * @return count of maximum overlapping intervals in the row
     */
    public int getMaxOverlapCount(TimeBarRow row) {
        if (getTimeBarViewState().getDrawOverlapping(row)) {
            return 1;
        } else {
            return _overlapStrategy.getMaxOverlapCount(row);
        }
    }

    /**
     * Applies the interval filter if set.
     * 
     * @param intervals list of intervals to be filtered
     * @return list of intervals that pass the filter
     */
    public List<Interval> filterIntervals(List<Interval> intervals) {
        if (_intervalFilter == null) {
            return intervals;
        }
        ArrayList<Interval> result = new ArrayList<Interval>();
        for (Interval interval : intervals) {
            if (_intervalFilter.isInResult(interval)) {
                result.add(interval);
            }
        }

        return result;
    }

    // ********** Propchangelistener
    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(_rowFilter) || evt.getSource().equals(_rowSorter)) {
            // on changes of filter or sorter do a rowlist refresh and inform
            // the viewer if present
            updateRowList();
            if (_tbvi != null) {
                _tbvi.repaint();
            }
        } else if (evt.getSource() instanceof TimeBarIntervalFilter) {
            if (_tbvi != null) {
                _overlapStrategy.clearCachedData();
                _tbvi.repaint();
            }
        }
    }

    // ********** End propchange listener

    // ************* focus handling
    /**
     * Retrieve the focusef interval.
     * 
     * @return the currently focussed interval or null if none is in focus
     */
    public Interval getFocussedInterval() {
        return _focussedInterval;
    }

    /**
     * @return the currently focussed row or null if none is in focus
     */
    public TimeBarRow getFocussedRow() {
        return _focussedRow;
    }

    /**
     * Check whether an interval is focussed.
     * 
     * @param interval interval to check
     * @return true if focussed
     */
    public boolean isFocussed(Interval interval) {
        if (_focussedInterval == null) {
            return false;
        }
        return _focussedInterval.equals(interval);
    }

    /**
     * Set the new focussed interval.
     * 
     * @param interval new focussed interval
     */
    public void setFocussedInterval(Interval interval) {
        setFocussedInterval(null, interval);
    }

    /**
     * Set the new focussed interval. Ths method should be used, if the row of the interval is known.
     * 
     * @param row row of the interval. May be <code>null</code> if the row of the interval is unknown.
     * @param interval interval to be focussed.
     */
    public void setFocussedInterval(TimeBarRow row, Interval interval) {
        if (_focussedInterval == null && interval == null) {
            return;
        }
        if (_focussedInterval != null && _focussedInterval.equals(interval)) {
            return;
        }
        if (interval != null && isFiltered(interval)) {
            // can not focus filtered interval
            return;
        }
        if (interval != null && row == null) {
            _focussedRow = _model.getRowForInterval(interval);
            if (_focussedRow == null || isFiltered(_focussedRow)) {
                return;
            }
        } else {
            _focussedRow = row;
        }
        // real change
        _focussedInterval = interval;

        if (_focussedInterval != null && _scrollOnFocus) {
            if (!isIntervalVisible(_focussedRow, _focussedInterval)) {
                scrollIntervalToVisible(_focussedRow, _focussedInterval);
            }
        }
        _tbvi.repaint(); // MAYBE optimize with exact redraw
        fireFocussedIntervalChange(_focussedRow, _focussedInterval);
    }

    /**
     * Move the focus to the right.
     */
    public void moveFocusRight() {
        if (_focussedRow != null && _focussedInterval != null) {
            int idx = _focussedRow.getIntervals().indexOf(_focussedInterval);
            if (idx + 1 < _focussedRow.getIntervals().size()) {
                setFocussedInterval(_focussedRow.getIntervals().get(idx + 1));
            }
        }
    }

    /**
     * Move the focus to the left.
     */
    public void moveFocusLeft() {
        if (_focussedRow != null && _focussedInterval != null) {
            int idx = _focussedRow.getIntervals().indexOf(_focussedInterval);
            if (idx - 1 >= 0) {
                setFocussedInterval(_focussedRow.getIntervals().get(idx - 1));
            }
        }
    }

    /**
     * Move the focus up.
     */
    public void moveFocusUp() {
        if (_focussedRow != null && _focussedInterval != null) {
            int ridx = _rowList.indexOf(_focussedRow);
            if (ridx > 0) {
                TimeBarRow focussedRow = _rowList.get(ridx - 1);
                List<Interval> intervals = focussedRow.getIntervals(_focussedInterval.getBegin(), _focussedInterval
                        .getEnd());
                if (intervals != null && intervals.size() > 0) {
                    setFocussedInterval(focussedRow, intervals.get(0));
                } else {
                    Interval interval = getNearestInterval(focussedRow, _focussedInterval);
                    if (interval != null) {
                        setFocussedInterval(focussedRow, interval);
                    }
                }
            }
        }
    }

    /**
     * Move the focus down.
     */
    public void moveFocusDown() {
        if (_focussedRow != null && _focussedInterval != null) {
            int ridx = _rowList.indexOf(_focussedRow);
            if (ridx + 1 < _rowList.size()) {
                TimeBarRow focussedRow = _rowList.get(ridx + 1);
                List<Interval> intervals = focussedRow.getIntervals(_focussedInterval.getBegin(), _focussedInterval
                        .getEnd());
                if (intervals != null && intervals.size() > 0) {
                    setFocussedInterval(focussedRow, intervals.get(0));
                } else {
                    Interval interval = getNearestInterval(focussedRow, _focussedInterval);
                    if (interval != null) {
                        setFocussedInterval(focussedRow, interval);
                    }
                }
            }
        }
    }

    /**
     * Retrieve the nearest interval in a row relative to the middle of a given interval.
     * 
     * @param row row to search in
     * @param interval the iterval the nearest interval is searched for (using the middle of the interval)
     * @return the nearest intervals relative to the middle if the given interval or <code>null</code> if no interval is
     * in the row.
     */
    private Interval getNearestInterval(TimeBarRow row, Interval interval) {
        Interval result = null;
        long deltaSec = -1;
        JaretDate d = new JaretDate(interval.getBegin());
        d.advanceSeconds(interval.getSeconds() / 2.0);
        for (Interval i : row.getIntervals()) {
            int delta = Math.abs(i.getBegin().diffSeconds(d));
            if (delta < deltaSec || deltaSec == -1) {
                result = i;
                deltaSec = delta;
            }
            delta = Math.abs(i.getEnd().diffSeconds(d));
            if (delta < deltaSec || deltaSec == -1) {
                result = i;
                deltaSec = delta;
            }
        }
        return result;
    }

    /**
     * Add a listener to be informed when the focus changes.
     * 
     * @param listener listener to be added.
     */
    public synchronized void addFocussedIntervalListener(FocussedIntervalListener listener) {
        if (_focussedIntervalListeners == null) {
            _focussedIntervalListeners = new Vector<FocussedIntervalListener>(2);
        }
        _focussedIntervalListeners.add(listener);
    }

    /**
     * Remove a focussedIntervalListener.
     * 
     * @param listener listener to be removed from the listener list.
     */
    public synchronized void remFocussedIntervalListener(FocussedIntervalListener listener) {
        if (_focussedIntervalListeners != null) {
            _focussedIntervalListeners.remove(listener);
        }
    }

    /**
     * Inform listeners about a focus change.
     * 
     * @param newFocussedRow new (currently focussed row)
     * @param newFocussedInterval new (currently) focussed interval
     */
    protected synchronized void fireFocussedIntervalChange(TimeBarRow newFocussedRow, Interval newFocussedInterval) {
        if (_focussedIntervalListeners != null) {
            for (FocussedIntervalListener listener : _focussedIntervalListeners) {
                listener.focussedIntervalChanged(_tbvi, newFocussedRow, newFocussedInterval);
            }
        }
    }

    /**
     * Add a listener to be informed when the selection rect changes.
     * 
     * @param listener listener to be added.
     */
    public synchronized void addSelectionRectListener(ISelectionRectListener listener) {
        if (_selectionRectListeners == null) {
            _selectionRectListeners = new Vector<ISelectionRectListener>(2);
        }
        _selectionRectListeners.add(listener);
    }

    /**
     * Remove a selection rect listener.
     * 
     * @param listener listener to be removed from the listener list.
     */
    public synchronized void remSelectionRectListener(ISelectionRectListener listener) {
        if (_selectionRectListeners != null) {
            _selectionRectListeners.remove(listener);
        }
    }

    /**
     * Inform selection rect listeners about a change of the selection rect.
     */
    protected void fireSelectionRectChanged() {
        if (_selectionRectListeners != null) {
            for (ISelectionRectListener listener : _selectionRectListeners) {
                boolean horizontal = _orientation == Orientation.HORIZONTAL;
                Rectangle curSelRect = _selectionRect;
                // first get the rows
                List<TimeBarRow> rows = horizontal ? getRows(curSelRect.y, curSelRect.y + curSelRect.height) : getRows(
                        curSelRect.x, curSelRect.x + curSelRect.width);
                // calculate the dates
                JaretDate begin = horizontal ? dateForCoord(curSelRect.x) : dateForCoord(curSelRect.y);
                JaretDate end = horizontal ? dateForCoord(curSelRect.x + curSelRect.width) : dateForCoord(curSelRect.y
                        + curSelRect.height);
                // inform the listeners
                listener.selectionRectChanged(this, begin, end, rows);
            }
        }
    }

    /**
     * Inform selectionRectListeners about the end of the selection rect.
     */
    protected void fireSelectionRectClosed() {
        if (_selectionRectListeners != null) {
            for (ISelectionRectListener listener : _selectionRectListeners) {
                listener.selectionRectClosed(this);
            }
        }
    }

    /**
     * Inform selection rect listeners about a change of the region rect.
     */
    protected void fireRegionRectChanged() {
        if (_selectionRectListeners != null) {
            for (ISelectionRectListener listener : _selectionRectListeners) {
                // inform the listeners
                listener.regionRectChanged(this, _regionSelection);
            }
        }
    }

    /**
     * Inform selectionRectListeners about the end of the region rect.
     */
    protected void fireRegionRectClosed() {
        if (_selectionRectListeners != null) {
            for (ISelectionRectListener listener : _selectionRectListeners) {
                listener.regionRectClosed(this);
            }
        }
    }

    // ************* end focus handling

    /**
     * Handle key events from the time bar viewers.
     * 
     * @param keyCode keyCode (Swing)
     * @param modifierMask (Swing)
     */
    public void handleKeyPressed(int keyCode, int modifierMask) {
        boolean horizontal = _orientation.equals(Orientation.HORIZONTAL);
        if (modifierMask == 0) {
            switch (keyCode) {
            case KeyEvent.VK_RIGHT:
                if (horizontal) {
                    moveFocusRight();
                } else {
                    moveFocusDown();
                }
                break;
            case KeyEvent.VK_LEFT:
                if (horizontal) {
                    moveFocusLeft();
                } else {
                    moveFocusUp();
                }
                break;
            case KeyEvent.VK_UP:
                if (horizontal) {
                    moveFocusUp();
                } else {
                    moveFocusLeft();
                }
                break;
            case KeyEvent.VK_DOWN:
                if (horizontal) {
                    moveFocusDown();
                } else {
                    moveFocusRight();
                }
                break;
            case KeyEvent.VK_SPACE:
                selectFocussedInterval(false);
                break;
            case KeyEvent.VK_ESCAPE:
                cancelDrag();
                break;

            default:
                // do nothing
                break;
            }
        } else if ((modifierMask & InputEvent.SHIFT_DOWN_MASK) != 0) {
            switch (keyCode) {
            case KeyEvent.VK_RIGHT:
                growRight(_focussedRow, _focussedInterval, _keyboardChangeDelta);
                break;
            case KeyEvent.VK_LEFT:
                growLeft(_focussedRow, _focussedInterval, _keyboardChangeDelta);
                break;
            default:
                // do nothing
                break;
            }
        } else if ((modifierMask & InputEvent.ALT_DOWN_MASK) != 0) {
            switch (keyCode) {
            case KeyEvent.VK_RIGHT:
                growLeft(_focussedRow, _focussedInterval, -_keyboardChangeDelta);
                break;
            case KeyEvent.VK_LEFT:
                growRight(_focussedRow, _focussedInterval, -_keyboardChangeDelta);
                break;
            default:
                // do nothing
                break;
            }
        } else if ((modifierMask & InputEvent.CTRL_DOWN_MASK) != 0) {
            switch (keyCode) {
            case KeyEvent.VK_RIGHT:
                moveInterval(_focussedRow, _focussedInterval, _keyboardChangeDelta);
                break;
            case KeyEvent.VK_LEFT:
                moveInterval(_focussedRow, _focussedInterval, -_keyboardChangeDelta);
                break;
            case KeyEvent.VK_SPACE:
                selectFocussedInterval(true);
                break;
            case KeyEvent.VK_ESCAPE:
                cancelDrag();
                break;
            default:
                // do nothing
                break;
            }
        }
    }

    /**
     * Select (add to selection) the curently focussed interval.
     * 
     * @param add if true add the interval to selection, if false selection will be replaced
     */
    private void selectFocussedInterval(boolean add) {
        if (_focussedInterval != null) {
            if (!add) {
                _selectionModel.setSelectedInterval(_focussedInterval);
            } else {
                if (!_selectionModel.isSelected(_focussedInterval)) {
                    _selectionModel.addSelectedInterval(_focussedInterval);
                } else { // deselect
                    _selectionModel.remSelectedInterval(_focussedInterval);
                }
            }
        }
    }

    /**
     * Move an interval.
     * 
     * @param row row of the interval
     * @param interval the interval to move
     * @param deltaSeconds seconds delta
     */
    private void moveInterval(TimeBarRow row, Interval interval, int deltaSeconds) {
        if (interval == null) {
            return;
        }
        JaretDate newBegin = interval.getBegin().copy().advanceSeconds(deltaSeconds);
        boolean allowed = isShiftingAllowed(row, interval, newBegin);
        if (allowed) {
            JaretDate oldBegin = interval.getBegin();
            JaretDate oldEnd = interval.getEnd();

            interval.setBegin(newBegin);
            interval.setEnd(interval.getEnd().advanceSeconds(deltaSeconds));

            // inform the timebar change listener
            fireIntervalChangeStarted(row, interval);
            fireIntervalChanged(row, interval, oldBegin, oldEnd);
        }

    }

    /**
     * Alter the duration of an interval by a delta of seconds altering the end of the interval.
     * 
     * @param row row of the interval
     * @param interval interval to alter
     * @param deltaSeconds delta in seconds
     */
    private void growLeft(TimeBarRow row, Interval interval, int deltaSeconds) {
        if (interval == null) {
            return;
        }
        JaretDate newBegin = interval.getBegin().copy().backSeconds(deltaSeconds);
        boolean allowed = isNewBeginAllowed(row, interval, newBegin);
        if (allowed) {
            JaretDate oldBegin = interval.getBegin();
            interval.setBegin(newBegin);

            // inform the timebar change listener
            fireIntervalChangeStarted(row, interval);
            fireIntervalChanged(row, interval, oldBegin, interval.getEnd());
        }
    }

    /**
     * Alter the duration of an interval by a delta of seconds altering the begin of the interval.
     * 
     * @param row row of the interval
     * @param interval interval to alter
     * @param deltaSeconds delta in seconds
     */
    private void growRight(TimeBarRow row, Interval interval, int deltaSeconds) {
        if (interval == null) {
            return;
        }
        JaretDate newEnd = interval.getEnd().copy().advanceSeconds(deltaSeconds);
        boolean allowed = isNewEndAllowed(row, interval, newEnd);
        if (allowed) {
            JaretDate oldEnd = interval.getEnd();
            interval.setEnd(newEnd);

            // inform the timebar change listener
            fireIntervalChangeStarted(row, interval);
            fireIntervalChanged(row, interval, interval.getBegin(), oldEnd);
        }
    }

    /**
     * Retrieve the keyboardChangeDelta currently used.
     * 
     * @return the keyboardChangeDelta in seconds
     */
    public int getKeyboardChangeDelta() {
        return _keyboardChangeDelta;
    }

    /**
     * Set the delta for resizing and moving via keyboard.
     * 
     * @param keyboardChangeDelta the keyboardChangeDelta in seconds to set
     */
    public void setKeyboardChangeDelta(int keyboardChangeDelta) {
        _keyboardChangeDelta = keyboardChangeDelta;
    }

    /**
     * Retrieve the state of the variable xscale state. If true a list of intervals contlrols different pps values for
     * different intervals on the axis.
     * 
     * @return true if a varying pps value is used
     */
    public boolean hasVariableXScale() {
        return _variableXScale;
    }

    /**
     * Set the state for the variable xscale.
     * 
     * @param state true if a variable scale should be used.
     */
    public void setVariableXScale(boolean state) {
        if (state != _variableXScale) {
            _variableXScale = state;
            if (state) {
                setOptimizeScrolling(false); // optimized scrolling will not
                // work together with a variable
                // xscale
                _xScalePPSIntervalRow = new DefaultTimeBarNode(new DefaultRowHeader("PPSROW"));
                if (_repaintingRowListener == null) {
                    _repaintingRowListener = new TimeBarRowListener() {
                        // TimeBarRow listener that repaints every change
                        // and recalculates the break pps values
                        public void elementAdded(TimeBarRow row, Interval element) {
                            updateTimeScaleBreaks();
                            _tbvi.repaint();
                        }

                        public void elementChanged(TimeBarRow row, Interval element) {
                            updateTimeScaleBreaks();
                            _tbvi.repaint();
                        }

                        public void elementRemoved(TimeBarRow row, Interval element) {
                            _tbvi.repaint();
                        }

                        public void headerChanged(TimeBarRow row, TimeBarRowHeader newHeader) {
                            _tbvi.repaint();
                        }

                        public void rowDataChanged(TimeBarRow row) {
                            updateTimeScaleBreaks();
                            _tbvi.repaint();
                        }
                    };
                }
                _xScalePPSIntervalRow.addTimeBarRowListener(_repaintingRowListener);
            } else {
                if (_xScalePPSIntervalRow != null && _repaintingRowListener != null) {
                    _xScalePPSIntervalRow.remTimeBarRowListener(_repaintingRowListener);
                }
                _xScalePPSIntervalRow = null;
            }
            if (_tbvi != null) {
                _tbvi.repaint();
            }
            updateScrollBars();
        }
    }

    /**
     * Retrieve the row that hold intervals (PpsIntervals) defining the pps value for different intervals.
     * 
     * @return the row or <code>null</code> if no variable xscale has been defined.
     */
    public TimeBarNode getPpsRow() {
        return _xScalePPSIntervalRow;
    }

    /**
     * Retrieve the selection delta used to determine whether a marker or interval edge is clicked/dragged.
     * 
     * @return max distance for detection
     */
    public int getSelectionDelta() {
        return _selectionDelta;
    }

    /**
     * Set the selection delta used to determine whether a marker or interval edge is clicked/dragged.
     * 
     * @param selectionDelta max distance for detection
     */
    public void setSelectionDelta(int selectionDelta) {
        _selectionDelta = selectionDelta;
    }

    /**
     * Check whether it is allowed to drag the limiting lines of the hierarhy ara and the header (yaxis) area.
     * 
     * @return true if dragging is allowed.
     */
    public boolean isLineDraggingAllowed() {
        return _lineDraggingAllowed;
    }

    /**
     * Set the allowance for line dragging of the limiting lines for hierarchy and header(yaxis) areas.
     * 
     * @param lineDraggingAllowed true for enabling the drag possibility
     */
    public void setLineDraggingAllowed(boolean lineDraggingAllowed) {
        _lineDraggingAllowed = lineDraggingAllowed;
    }

    /**
     * Check whether th delegate is setup for millisecond accuracy. This will only have an impact on the x scroll bar.
     * 
     * @return true if ms accuracy ist set
     */
    public boolean isMilliAccuracy() {
        return _milliAccuracy;
    }

    /**
     * Set the delegates status concerning millisecond accuracy. If set to true the x scroll bar will operate in
     * milliseconds.
     * 
     * @param milliAccuracy true to use ms accuracy
     */
    public void setMilliAccuracy(boolean milliAccuracy) {
        _milliAccuracy = milliAccuracy;
    }

    /**
     * Set margins to the left and top to be included in geometry calculations. This is introduced for supporting
     * printing (margins) but might be useful for insets if necessary.
     * 
     * @param marginLeft left margin (pixel)
     * @param marginTop top margin (pixel)
     */
    public void setDrawingOffset(int marginLeft, int marginTop) {
        _offsetLeft = marginLeft;
        _offsetTop = marginTop;
    }

    /**
     * Get whether optimzed scrollnig is used.
     * 
     * @return true if optimized scrolling is used
     */
    public boolean getOptimizeScrolling() {
        return _optimizeScrolling;
    }

    /**
     * Set whether optimized scrolling should be used (not allowed togeter with a variable xscale).
     * 
     * @param optimizeScrolling true for optimized scrolling
     */
    public void setOptimizeScrolling(boolean optimizeScrolling) {
        if (_variableXScale && optimizeScrolling) {
            throw new RuntimeException("Optimized scrolling can not be used together with a variable xscale");
        }
        _optimizeScrolling = optimizeScrolling;
    }

    /**
     * Retrieve the orientation of the viewer.
     * 
     * @return the orientation of the viewer
     */
    public Orientation getOrientation() {
        return _orientation;
    }

    /**
     * Set the orientation of the viewer.
     * 
     * @param orientation the new orientation for the viewer
     */
    public void setOrientation(Orientation orientation) {
        if (_orientation != orientation) {
            _orientation = orientation;
            updateScrollBars();
            if (_tbvi != null) {
                _tbvi.repaint();
            }
        }
    }

    /**
     * Get the number of rows (columns) that the viewer scales itself to.
     * 
     * @return number of rows to display or -1 if no scale has been set
     */
    public int getAutoScaleRows() {
        return _autoScaleRows;
    }

    /**
     * Set a number of rows (columns) to be displayed by the viewer. The row height will always be changed to math the
     * number of rows to display.
     * 
     * @param rows the number of rows or -1 for no special scaling (default)
     */
    public void setAutoScaleRows(int rows) {
        if (_autoScaleRows != rows) {
            _autoScaleRows = rows;
            if (_tbvi != null) {
                _tbvi.repaint();
            }
        }
    }

    /**
     * Add a listener to be informed about interval changes.
     * 
     * @param listener listener
     */
    public void addTimeBarChangeListener(ITimeBarChangeListener listener) {
        if (!_timeBarChangeListeners.contains(listener)) {
            _timeBarChangeListeners.add(listener);
        }
    }

    /**
     * Remove a timebar change listener.
     * 
     * @param listener listener to remove
     */
    public void removeTimeBarChangeListener(ITimeBarChangeListener listener) {
        _timeBarChangeListeners.remove(listener);
    }

    /**
     * Inform time bar change listeners about a beginning interval change.
     * 
     * @param row row involved
     * @param interval interval to be changed
     */
    protected void fireIntervalChangeStarted(TimeBarRow row, Interval interval) {
        for (ITimeBarChangeListener listener : _timeBarChangeListeners) {
            listener.intervalChangeStarted(row, interval);
        }
    }

    /**
     * Inform time bar change listeners about an intermediate interval change.
     * 
     * @param row row involved
     * @param interval interval changing
     * @param oldBegin begin when then change started
     * @param oldEnd end when the change started
     */
    protected void fireIntervalIntermediateChange(TimeBarRow row, Interval interval, JaretDate oldBegin,
            JaretDate oldEnd) {
        for (ITimeBarChangeListener listener : _timeBarChangeListeners) {
            listener.intervalIntermediateChange(row, interval, oldBegin, oldEnd);
        }

    }

    /**
     * Inform time bar change listeners about a finished interval change.
     * 
     * @param row row involved
     * @param interval interval that has been changed
     * @param oldBegin begin when then change started
     * @param oldEnd end when the change started
     */
    protected void fireIntervalChanged(TimeBarRow row, Interval interval, JaretDate oldBegin, JaretDate oldEnd) {
        for (ITimeBarChangeListener listener : _timeBarChangeListeners) {
            listener.intervalChanged(row, interval, oldBegin, oldEnd);
        }

    }

    /**
     * Inform time bar change listeners about a cancelled interval change.
     * 
     * @param row row involved
     * @param interval interval that's change has been cancelled
     */
    protected void fireIntervalChangeCancelled(TimeBarRow row, Interval interval) {
        for (ITimeBarChangeListener listener : _timeBarChangeListeners) {
            listener.intervalChangeCancelled(row, interval);
        }
    }

    /**
     * Informa listener about a started marker drag.
     * 
     * @param marker marker that is beeing dragged
     */
    protected void fireMarkerDragStarted(TimeBarMarker marker) {
        for (ITimeBarChangeListener listener : _timeBarChangeListeners) {
            listener.markerDragStarted(marker);
        }
    }

    /**
     * Inform listeners about stopping of a marker drag.
     * 
     * @param marker marker that has been dragged
     */
    protected void fireMarkerDragStopped(TimeBarMarker marker) {
        for (ITimeBarChangeListener listener : _timeBarChangeListeners) {
            listener.markerDragStopped(marker);
        }
    }

    /**
     * Check whether row height dragging is allowed.
     * 
     * @return true if row height dragging is enabled
     */
    public boolean isRowHeightDraggingAllowed() {
        return _rowHeightDraggingAllowed;
    }

    /**
     * Set whether row height dragging should be allowed.
     * 
     * @param rowHeightDraggingAllowed true for allowance
     */
    public void setRowHeightDraggingAllowed(boolean rowHeightDraggingAllowed) {
        if (_rowHeightDraggingAllowed != rowHeightDraggingAllowed) {
            _rowHeightDraggingAllowed = rowHeightDraggingAllowed;
            if (_tbvi != null) {
                _tbvi.firePropertyChangeX(TimeBarViewerInterface.PROPERTYNAME_ROWHEIGHTDRAGGINGALLOWED,
                        !rowHeightDraggingAllowed, rowHeightDraggingAllowed);
            }
        }
    }

    /**
     * Check whether a given point is in the row axis area (hierarchy or header).
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return <code>true</code> if the point is in either hierarchy or header area
     */
    public boolean isInRowAxis(int x, int y) {
        return _hierarchyRect.contains(x, y) || _yAxisRect.contains(x, y);
    }

    /**
     * Check whether a given point is in the main diagram area.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return <code>true</code> if the point is in the diagram rectangle
     */
    public boolean isInDiagram(int x, int y) {
        return _diagramRect.contains(x, y);
    }

    /**
     * Retrieve the strategy for filtering when painting (see {@link #setStrictClipTimeCheck(boolean)}).
     * 
     * @return <code>true</code> if strict checking is enabled
     */
    public boolean getStrictClipTimeCheck() {
        return _strictClipTimeCheck;
    }

    /**
     * Set the optimization strategy for interval filetering when painting.
     * 
     * @param strictClipTimeCheck If set to true, intervals are filtered strictly by their interval bounds, disallowing
     * rendering beyond the bounding box calculated by the interval bounds. Defaults to false resulting in filtering by
     * the preferred drawing area.
     */
    public void setStrictClipTimeCheck(boolean strictClipTimeCheck) {
        _strictClipTimeCheck = strictClipTimeCheck;
    }

    /**
     * Retrieve the time to be additionally considered (looking back) when deciding which intervals are to be painted.
     * 
     * @return time in mnutes
     */
    public int getScrollLookBackMinutes() {
        return _scrollLookBackMinutes;
    }

    /**
     * Set the additional time to be considered when deciding whether to draw an interval looking back.
     * 
     * @param scrollLookBackMinutes time in minutes
     */
    public void setScrollLookBackMinutes(int scrollLookBackMinutes) {
        _scrollLookBackMinutes = scrollLookBackMinutes;
    }

    /**
     * Retrieve the time to be additionally considered (looking forward) when deciding which intervals are to be
     * painted.
     * 
     * @return time in mnutes
     */
    public int getScrollLookForwardMinutes() {
        return _scrollLookForwardMinutes;
    }

    /**
     * Set the additional time to be considered when deciding whether to draw an interval looking forward.
     * 
     * @param scrollLookForwardMinutes time in minutes
     */
    public void setScrollLookForwardMinutes(int scrollLookForwardMinutes) {
        _scrollLookForwardMinutes = scrollLookForwardMinutes;
    }

    /**
     * Retrieve the used strategy for determing overlap information.
     * 
     * @return the overlap strategy
     */
    public IOverlapStrategy getOverlapStrategy() {
        return _overlapStrategy;
    }

    /**
     * Set the strategy to be used for calculating overlap information.
     * 
     * @param overlapStrategy the strytegy to be used. May not be <code>null</code>.
     */
    public void setOverlapStrategy(IOverlapStrategy overlapStrategy) {
        if (overlapStrategy == null) {
            throw new IllegalArgumentException("Strategy may not be null");
        }
        IOverlapStrategy oldStrategy = _overlapStrategy;
        _overlapStrategy = overlapStrategy;
        oldStrategy.dispose(); // tell the old strategy it is no longer used
    }

    /**
     * Retrieve the currently set autoscroll delta.
     * 
     * @return the autoscroll delat in pixel
     */
    public int getAutoscrollDelta() {
        return _autoscrollDelta;
    }

    /**
     * Set the autoscroll delta. This value will be used to deteremine the autoscroll deltas when the mouse pointer is
     * not in the diagram rectangle. It is specified in pixel so it is always relative to the timescale. The value will
     * also be used to limit the maximum delta when resizing an interval (edge dragging) with the cursor outside the
     * diagram rectangle.
     * 
     * @param autoscrollDelta delta in pixel
     */
    public void setAutoscrollDelta(int autoscrollDelta) {
        _autoscrollDelta = autoscrollDelta;
    }

    /**
     * If <code>true</code> all selected intervals will be dragged together with the interval on that the drag happened.
     * 
     * @return the state of the flag
     */
    public boolean getDragAllSelectedIntervals() {
        return _dragAllSelectedIntervals;
    }

    /**
     * If set to <code>true</code> all selected intervals are dragged when an interval is dragged. The default is false.
     * 
     * @param dragAllSelectedIntervals <code>true</code> to drag all selcted intervals
     */
    public void setDragAllSelectedIntervals(boolean dragAllSelectedIntervals) {
        _dragAllSelectedIntervals = dragAllSelectedIntervals;
    }

    /**
     * Retrieve the state of the scroll to focus flag.
     * 
     * @return <code>true</code> if the viewer should scroll to the focussed interval
     */
    public boolean getScrollOnFocus() {
        return _scrollOnFocus;
    }

    /**
     * If set to true the viewer will scroll to the begin of an interval if it's focussed.
     * 
     * @param scrollOnFocus <code>true</code> for scrolling to the focussed interval
     */
    public void setScrollOnFocus(boolean scrollOnFocus) {
        _scrollOnFocus = scrollOnFocus;
    }

    /**
     * Retrieve whether the root node is shown when using a hierachical model.
     * 
     * @return <code>true</code> if the root is not shown
     */
    public boolean getHideRoot() {
        return _hideRoot;
    }

    /**
     * Set whether the root node should be shown when using a hierachical model.
     * 
     * @param hideRoot <code>true</code> if the root node should be hidden
     */
    public void setHideRoot(boolean hideRoot) {
        _hideRoot = hideRoot;
        if (_model instanceof StdHierarchicalTimeBarModel) {
            ((StdHierarchicalTimeBarModel) _model).setHideRoot(hideRoot);
        }
    }

    /** if true markers can be dragged in the diagram area. */
    protected boolean _markerDraggingInDiagramArea = false;

    /**
     * Retrieve whether marker dragging in the diagram area is activated.
     * 
     * @return <code>true</code> if marker dragging in the diagram area is allowed
     */
    public boolean getMarkerDraggingInDiagramArea() {
        return _markerDraggingInDiagramArea;
    }

    /**
     * Set whether marker dragging is allowed in the diagram area (If intervals are modificable the marker will only be
     * grabbed when no other operation is applicable).
     * 
     * @param allowed <code>true</code> for allowing marker drag in the diagram area.
     */
    public void setMarkerDraggingInDiagramArea(boolean allowed) {
        _markerDraggingInDiagramArea = allowed;
    }

    /**
     * Check whether a marker is beeing dragged.
     * 
     * @return <code>true</code> if a marker drag is in progress
     */
    public boolean isMarkerDraggingInProgress() {
        return _draggedMarker != null;
    }

    /**
     * Retrieve the interval selection strategy used.
     * 
     * @return the strategy
     */
    public IIntervalSelectionStrategy getIntervalSelectionStrategy() {
        return _intervalSelectionStrategy;
    }

    /**
     * Set the interval selection strategy.
     * 
     * @param intervalSelectionStrategy the interval selection strategy to use.
     */
    public void setIntervalSelectionStrategy(IIntervalSelectionStrategy intervalSelectionStrategy) {
        _intervalSelectionStrategy = intervalSelectionStrategy;
    }

    /**
     * Calculate the rectangle for screen representation for a tbrect (time and rows).
     * 
     * @param tbrect tbrect to be painted
     * @return rectangle in screen coordinates
     */
    public Rectangle calcRect(TBRect tbrect) {
        Rectangle result = new Rectangle();
        if (_orientation == Orientation.HORIZONTAL) {
            result.x = xForDate(tbrect.startDate);
            result.width = xForDate(tbrect.endDate) - result.x;
            result.y = getRowBounds(tbrect.startRow).y;
            result.height = getRowBounds(tbrect.endRow).y + getTimeBarViewState().getRowHeight(tbrect.endRow)
                    - result.y;
        } else {
            result.y = xForDate(tbrect.startDate);
            result.height = xForDate(tbrect.endDate) - result.x;
            result.x = getRowBounds(tbrect.startRow).y;
            result.width = getRowBounds(tbrect.endRow).y + getTimeBarViewState().getRowHeight(tbrect.endRow) - result.y;
        }
        return result;
    }

    /**
     * Retrieve the selected region.
     * 
     * @return the selected region or <code>null</code>
     */
    public TBRect getRegionRect() {
        return _regionSelection;
    }

    /**
     * Remove the selction of a region if existent.
     */
    public void clearRegionRect() {
        if (_regionSelection != null) {
            _regionSelection = null;
            _lastRegionSelection = null;
            _tbvi.repaint();
            fireRegionRectClosed();
        }
    }

    /**
     * Enable/Disable region selections.
     * 
     * @param enabled <code>true</code> enabling
     */
    public void setRegionRectEnable(boolean enabled) {
        _regionRectEnabled = enabled;
        clearRegionRect();
    }

    /**
     * Retrieve whether region selections are enabled.
     * 
     * @return true if region selections are enabled
     */
    public boolean getRegionRectEnable() {
        return _regionRectEnabled;
    }

    /**
     * Set whether all intervals in a non overlapping drawn ro should use the same height/width.
     * 
     * @param useUniformHeight <code>true</code> for uniform heights/widths
     */
    public void setUseUniformHeight(boolean useUniformHeight) {
        if (useUniformHeight != _useUniformHeight) {
            _useUniformHeight = useUniformHeight;
            _tbvi.repaint();
        }
    }

    /**
     * Retrieve whether uniform height is use for all intervals in a row.
     * 
     * @return <code>true</code> if all intervals in a non overlapping drawn row should use the same height/width
     */
    public boolean getUseUniformHeight() {
        return _useUniformHeight;
    }

    /**
     * Handle a component resize and scroll down if an empty space would be shown in the newly exposed screen estate.
     */
    public void componentResized() {
        if (_diagramRect.width != 0) { // do not do any scrolling if the component has not been painted before
            preparePaint(_tbvi.getWidth(), _tbvi.getHeight());
            TimeBarRow row = _rowList.get(_rowList.size() - 1);
            
            int absStartY = getAbsPosForRow(_firstRow)+_firstRowPixelOffset;
            int absLastY = getAbsPosForRow(_rowList.size() - 1);
            
            if ((absLastY+_timeBarViewState.getRowHeight(row))-absStartY < _diagramRect.height) {
                setLastRow(row);
            }
        }
        updateScrollBars();
    }

}
