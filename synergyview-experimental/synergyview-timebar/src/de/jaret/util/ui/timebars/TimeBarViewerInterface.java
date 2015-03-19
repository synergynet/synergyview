/*
 *  File: TimeBarViewerInterface.java 
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

import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.util.List;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.misc.Pair;
import de.jaret.util.ui.timebars.mod.IntervalModificator;
import de.jaret.util.ui.timebars.model.FocussedIntervalListener;
import de.jaret.util.ui.timebars.model.HierarchicalTimeBarModel;
import de.jaret.util.ui.timebars.model.HierarchicalViewState;
import de.jaret.util.ui.timebars.model.IIntervalRelation;
import de.jaret.util.ui.timebars.model.ISelectionRectListener;
import de.jaret.util.ui.timebars.model.ITimeBarChangeListener;
import de.jaret.util.ui.timebars.model.ITimeBarViewState;
import de.jaret.util.ui.timebars.model.TBRect;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarNode;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarSelectionModel;
import de.jaret.util.ui.timebars.strategy.IOverlapStrategy;

/**
 * This interface has to be implemented by the Swing and SWT version of the timebar viewer. Its use is mainly for the
 * TimeBarViewerDelegate to interact with the concrete implementation it supports. Applications should use the specific
 * TimeBarViewer (Swing or SWT) directly.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarViewerInterface.java 884 2009-10-08 20:25:15Z kliem $
 */
public interface TimeBarViewerInterface {
    /**
     * Constant value indicating the time scale should be drawn on top of the diagram.
     */
    int TIMESCALE_POSITION_TOP = 0;

    /**
     * Constant value indicating the time scale should be drawn at the bottom of the diagram.
     */
    int TIMESCALE_POSITION_BOTTOM = 1;

    /**
     * Constant value indicating no time scale should be painted.
     */
    int TIMESCALE_POSITION_NONE = 2;

    /**
     * enum for the possible orientations.
     * 
     * @see setOrientation.
     */
    enum Orientation {
        /**
         * horizontal orientation.
         */
        HORIZONTAL,
        /**
         * vertical orientation.
         */
        VERTICAL
    };

    /** default for the rowheight. */
    int DEFAULT_ROWHEIGHT = 50;
    /** default for the xaxis (timescale) height. */
    int DEFAULT_XAXISHEIGHT = 50;
    /** default for the yaxis width. */
    int DEFAULT_YAXISWIDTH = 150;

    /** property name constant of the bound property. */
    String PROPERTYNAME_STARTDATE = "StartDate";
    /** property name constant of the bound property. */
    String PROPERTYNAME_FIRSTROW = "FirstRow";
    /** property name constant of the bound property. */
    String PROPERTYNAME_FIRSTROWOFFSET = "FirstRowOffset";
    /** property name constant of the bound property. */
    String PROPERTYNAME_ROWHEIGHT = "RowHeight";
    /** property name constant of the bound property. */
    String PROPERTYNAME_XAXISHEIGHT = "XAxisHeight";
    /** property name constant of the bound property. */
    String PROPERTYNAME_YAXISWIDTH = "YAxisWidth";
    /** property name constant of the bound property. */
    String PROPERTYNAME_ROWFILTER = "RowFilter";
    /** property name constant of the bound property. */
    String PROPERTYNAME_ROWSORTER = "RowSorter";
    /** property name constant of the bound property. */
    String PROPERTYNAME_INTERVALFILTER = "IntervalFilter";
    /** property name constant of the bound property. */
    String PROPERTYNAME_MAXDATE = "MaxDate";
    /** property name constant of the bound property. */
    String PROPERTYNAME_MINDATE = "MinDate";
    /** property name constant of the bound property. */
    String PROPERTYNAME_PIXELPERSECOND = "PixelPerSecond";
    /** property name constant of the bound property. */
    String PROPERTYNAME_ROWHEIGHTDRAGGINGALLOWED = "RowHeightDraggingAllowed";

    /**
     * Retrieve the delegate ATTENTION: the delegate is for mostly internal usage only. The interface and behavior may
     * change.
     * 
     * @return the delegate used by the viewer.
     */
    TimeBarViewerDelegate getDelegate();

    /**
     * Update the x scrollbar.
     * 
     * @param max max value for the scrollbar (seconds)
     * @param pos current positions in seconds
     * @param secondsDisplayed the number of seconds displayed by the viewer
     */
    void updateXScrollBar(int max, int pos, int secondsDisplayed);

    /**
     * Update the y scrollbar.
     * 
     * @param max max value for the scrollbar (row)
     * @param pos first row displayed
     * @param rowsDisplayed number of rows currently displayed
     */
    void updateYScrollBar(int max, int pos, int rowsDisplayed);

    /**
     * Mark the whole viewer as dirty causing a repaint.
     * 
     */
    void repaint();

    /**
     * Mark area to be repainted.
     * 
     * @param rect java.awt.Rectangle that needs to be repainted
     */
    void repaint(Rectangle rect);

    /**
     * Mark area the needs repaint.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @param width width of the area
     * @param height height of the area
     */
    void repaint(int x, int y, int width, int height);

    /**
     * Return the height of the viewer.
     * 
     * @return height
     */
    int getHeight();

    /**
     * Return the width of the viewer.
     * 
     * @return width
     */
    int getWidth();

    /**
     * Get the corresponding date for an x coordinate in the viewer area.
     * 
     * @param x in the viewer area
     * @return corresponding date
     */
    JaretDate dateForX(int x);

    /**
     * Get the corresponding date for a point in the viewer area.
     * 
     * @param x x coordinate in the viewer area
     * @param y y coordinate in the viewer area
     * @return corresponding date
     */
    JaretDate dateForXY(int x, int y);

    /**
     * Get the corresponding x coordinate in the viewer area for a given date.
     * 
     * @param date Date
     * @return x coordinate in the viewer area
     */
    int xForDate(JaretDate date);

    /**
     * Check whether a location is contained in a rendered interval.
     * 
     * @param interval Interval to be checked
     * @param intervalRect bounding rect for the interval
     * @param x location x
     * @param y location y
     * @param overlapping true if there are overlapping intervals and drawing mode is not drawOverlapped
     * @return true if contained
     */
    boolean timeBarContains(Interval interval, Rectangle intervalRect, int x, int y, boolean overlapping);

    /**
     * Calculate/get the containing rectangle of a rendered interval.
     * 
     * @param interval Interval for which the containing rect should be calculated
     * @param intervalRect Rectangle in the viewer (bounding rectangle)
     * @param overlappig true if there are overlapping intervals and drawing mode is not drawOverlapped
     * @return java.awt.Rectangle containing rectangle
     */
    Rectangle timeBarContainingRect(Interval interval, Rectangle intervalRect, boolean overlappig);

    /**
     * Set the cursor type.
     * 
     * @param cursorType java.awt.cursor type
     */
    void setCursor(int cursorType);

    /**
     * Return the tool tip text of the viewer.
     * 
     * @return ToolTipText for the viewer
     */
    String getToolTipText();

    /**
     * Get the tooltip text for a given location in a rendered interval.
     * 
     * @param interval Interval
     * @param intervalRect bounding rectangle for the interval
     * @param x location x
     * @param y location y
     * @return the tooltip text or null if none could be found
     */
    String getIntervalToolTipText(Interval interval, Rectangle intervalRect, int x, int y);

    /**
     * Provide the tooltip for a relation if any.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return tooltip text or <code>null</code>
     */
    String getRelationTooltip(int x, int y);

    /**
     * Return the pixel per second ratio.
     * 
     * @return pixel per second
     */
    double getPixelPerSecond();

    /**
     * Sets the scale ox the x axis as pixel per second, thus a value of 1000.0 / (24.0 * 60 * 60) will result in
     * displaying one day over 1000 pixel. The property is a bound property and can be listened to by a
     * PropertyChangeListener.
     * 
     * @param pixelPerSecond pixel per second.
     */
    void setPixelPerSecond(double pixelPerSecond);

    /**
     * Set the scaling of the x axis by specifying the number of seconds that should be displayed.
     * 
     * @param seconds number of seconds that will be displayed on the x axis
     * @param center if set to <code>true</code> the center date will be fixed while scaling
     */
    void setSecondsDisplayed(int seconds, boolean center);

    /**
     * Set the scaling of the x axis by specifying the number of seconds that should be displayed.
     * 
     * @param seconds number of seconds that will be displayed on the x axis
     * @param centerDate date that will be fixed while scaling
     */
    void setSecondsDisplayed(int seconds, JaretDate centerDate);

    /**
     * Retrieve the current row height or the default row height if variable row heights/widths are enabled.
     * 
     * @return row height in pixel
     */
    int getRowHeight();

    /**
     * Set the height for the rows in pixel. This property is bound.
     * 
     * @param rowHeight new row height
     */
    void setRowHeight(int rowHeight);

    /**
     * Retrive the current start date of the displayed time span.
     * 
     * @return start date of the visual section
     */
    JaretDate getStartDate();

    /**
     * Set the start date of the display.
     * 
     * @param startDate first date to be displayed
     */
    void setStartDate(JaretDate startDate);

    /**
     * Retrieve the minimum value present in the data of the viewer.
     * 
     * @return minimum date that can be displayed
     */
    JaretDate getMinDate();

    /**
     * Set the minimum date to be displayed. Note: this will only have an effect if min/max is not adjusted by the
     * model.
     * 
     * @param minDate minimum date to be displayed
     */
    void setMinDate(JaretDate minDate);

    /**
     * Retrieve the maximum date that can be displayed.
     * 
     * @return maximum date that can be displayed
     */
    JaretDate getMaxDate();

    /**
     * Set the maximum date to be displayed. Note: this will only have an effect if min/max is not adjusted by the
     * model.
     * 
     * @param maxDate maximum date to be displayed
     */
    void setMaxDate(JaretDate maxDate);

    /**
     * Retrieve the adjustment policy for min and max date.
     * 
     * @return true if min/max are adjusted by the model
     */
    boolean getAdjustMinMaxDatesByModel();

    /**
     * Set the adjustment policy.
     * 
     * @param adjustMinMaxDatesByModel if set to true min and max dates are set by the model
     */
    void setAdjustMinMaxDatesByModel(boolean adjustMinMaxDatesByModel);

    /**
     * Retrieve the index of the first row that is displayed.
     * 
     * @return index of the first displayed row
     */
    int getFirstRowDisplayed();

    /**
     * Set the first row to be displayed.
     * 
     * @param firstRow upmost row to be displayed
     * @param pixOffset pixel offset
     */
    void setFirstRow(int firstRow, int pixOffset);

    /**
     * Set the first row to be displayed.
     * 
     * @param rowIdx index of the first row to be displayed
     */
    void setFirstRowDisplayed(int rowIdx);

    /**
     * Set the the first row to be displayed.
     * 
     * @param row row that should be the topmost row displayed.
     */
    void setFirstRowDisplayed(TimeBarRow row);

    /**
     * Retrieve the pixeloffset for the first row.
     * 
     * @return pixel offset of the first row
     */
    int getFirstRowOffset();

    /**
     * Set the pixeloffset of the first row.
     * 
     * @param offset pixeloffset for the first row
     */
    void setFirstRowOffset(int offset);

    /**
     * Set the last row in the viewer. If there are not enough rows for the row beeing the last row the row will be
     * displayed as far down as possible by setting the first row to 0.
     * 
     * @param index index of the row to be displayed at the bottom of the viewer.
     */
    void setLastRow(int index);

    /**
     * Set the last row in the viewer. If there are not enough rows for the row beeing the last row the row will be
     * displayed as far down as possible by setting the first row to 0.
     * 
     * @param row the row to be displayed at the bottom of the viewer.
     */
    void setLastRow(TimeBarRow row);

    /**
     * Get the selection model of the viewer.
     * 
     * @return the selection model of the viewer
     */
    TimeBarSelectionModel getSelectionModel();

    /**
     * Set the selectionmodel to be used by the viewer.
     * 
     * @param selectionModel selection model to be used
     */
    void setSelectionModel(TimeBarSelectionModel selectionModel);

    /**
     * Set the width of the y axis (the header area). The width is initialized with the width announced by the header
     * renderer.
     * 
     * @param width width in pixel
     */
    void setYAxisWidth(int width);

    /**
     * Retrieve the width of the y axis (header area).
     * 
     * @return width of the header area
     */
    int getYAxisWidth();

    /**
     * Set the width of the hierarchy area of the viewer. The width is initialized by the hierarchy renderer if set and
     * applicable.
     * 
     * @param width width in pixels
     */
    void setHierarchyWidth(int width);

    /**
     * Retrieve the width of the hierarchy area.
     * 
     * @return the width of the hierarchy area
     */
    int getHierarchyWidth();

    /**
     * Retrieve the height (or width when orientation is vertical) of the timescale.
     * 
     * @return returns the xAxisHeight.
     */
    int getXAxisHeight();

    /**
     * Set the height (or width) of the timescale.
     * 
     * @param height height or width for the time scale
     */
    void setXAxisHeight(int height);

    /**
     * Set the autoscroll behaviour. If autoscroll is enabled, drag and select by selection rect will autoscroll the
     * viewer.
     * 
     * @param enableAutoscroll true for enabling autoscroll
     */
    void setAutoscrollEnabled(boolean enableAutoscroll);

    /**
     * Get the autoscroll behaviour.
     * 
     * @return true if autoscroll is enabled.
     */
    boolean isAutoscrollEnabled();

    /**
     * Retrieve the status of row grid drawing.
     * 
     * @return true if the row grid drawing is enabled
     */
    boolean getDrawRowGrid();

    /**
     * Set the row grid drawing status. If set to true rows will be separated by lines.
     * 
     * @param drawRowGrid if true, rows will be separated by a thin line
     */
    void setDrawRowGrid(boolean drawRowGrid);

    /**
     * Add an intervalModificator controlling changes on the intervals.
     * 
     * @param intervalModificator interval modificator to add
     */
    void addIntervalModificator(IntervalModificator intervalModificator);

    /**
     * Remove an interval modificator.
     * 
     * @param intervalModificator interval modificator to remove
     */
    void remIntervalModificator(IntervalModificator intervalModificator);

    /**
     * Fire a property change.
     * 
     * @param string property name
     * @param oldValue old value
     * @param newValue new value
     */
    void firePropertyChange(String string, double oldValue, double newValue);

    /**
     * Fire a property change.
     * 
     * @param propName property name
     * @param oldValue old value
     * @param newValue new value
     */
    void firePropertyChangeX(String propName, Object oldValue, Object newValue);

    /**
     * Add a PropertyChangeListener.
     * 
     * @param listener property change listener to add
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove a property change listener.
     * 
     * @param listener listener to remove
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Retrieve the tooltip for a position on the timescale.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return the tooltip text or <code>null</code> indicating no tt is available
     */
    String getTimeScaleToolTipText(int x, int y);

    /**
     * Retrieve the tooltip for a position on a row.
     * 
     * @param row row displayed on the location
     * @param x x coordinate
     * @param y y coordinate
     * @return the tooltip text or <code>null</code> indicating no tt is available
     */
    String getHeaderToolTipText(TimeBarRow row, int x, int y);

    /**
     * Retrieve the tooltip for a position in the hierarchy.
     * 
     * @param node node displayed at the location
     * @param x x coordinate
     * @param y y coordinate
     * @return the tooltip text or <code>null</code> indicating no tt is available
     */
    String getHierarchyToolTipText(TimeBarNode node, int x, int y);

    /**
     * Retrieve the statndard viewstate of the viewer containing the row heights/column widths.
     * 
     * @return the viewstate used by the viewer
     */
    ITimeBarViewState getTimeBarViewState();

    /**
     * Retrieve the hierarchical viewstate used by the viewer.
     * 
     * @return the hierarchical viewstate
     */
    HierarchicalViewState getHierarchicalViewState();

    /**
     * Set the hierarchical viewstate to be used. Most probably no one needs to do this, since a default viewstate is
     * used when setting a hierarchical model.
     * 
     * @param hierarchicalViewState viewstate to use
     */
    void setHierarchicalViewState(HierarchicalViewState hierarchicalViewState);

    /**
     * Set the flat model to be displayed.
     * 
     * @param model the model to be displyed
     */
    void setModel(TimeBarModel model);

    /**
     * Set a hierarchical model to be displayed.
     * 
     * @param hModel hierarchial model
     */
    void setModel(HierarchicalTimeBarModel hModel);

    /**
     * Retrieve the flat model used by the viewer. This will always return a flat model, even if a hierarchical model
     * has been set. In this case the warpping flat model is returned.
     * 
     * @return the flat model used by the viewer
     */
    TimeBarModel getModel();

    /**
     * Retrieve the hierarchical model of the viewer. This may be <code>null</code> if a flat model has been set
     * directly.
     * 
     * @return the hierarchical model or null
     */
    HierarchicalTimeBarModel getHierarchicalModel();

    /**
     * Get the width for marker painting (as requested by a marker renderer).
     * 
     * @param marker marker to check the width for
     * @return overall width for marker painting
     */
    int getMarkerWidth(TimeBarMarker marker);

    /**
     * Set a title for the viewer to be displayed.
     * 
     * @param title or <code>null</code> indicating no title.
     */
    void setTitle(String title);

    /**
     * Retrieve the title.
     * 
     * @return the title.
     */
    String getTitle();

    /**
     * Display the context menu for the normal viewer body.
     * 
     * @param x x coordinate for the menu
     * @param y y coordinate for the menu
     */
    void displayBodyContextMenu(int x, int y);

    /**
     * Display the context menu for the time scale.
     * 
     * @param x x coordinate for the menu
     * @param y y coordinate for the menu
     */
    void displayTimeScaleContextMenu(int x, int y);

    /**
     * Display the context menu for an interval.
     * 
     * @param interval interval clicked on
     * @param x x coordinate for the menu
     * @param y y coordinate for the menu
     */
    void displayIntervalContextMenu(Interval interval, int x, int y);

    /**
     * Display the header context menu.
     * 
     * @param row row clicked on
     * @param x x coordinate for the menu
     * @param y y coordinate for the menu
     */
    void displayHeaderContextMenu(TimeBarRow row, int x, int y);

    /**
     * Display the hierarchy area context menu.
     * 
     * @param row row clicked on
     * @param x x coordinate for the menu
     * @param y y coordinate for the menu
     */
    void displayHierarchyContextMenu(TimeBarRow row, int x, int y);

    /**
     * Display the title area context menu.
     * 
     * @param x x coordinate for the menu
     * @param y y coordinate for the menu
     */
    void displayTitleContextMenu(int x, int y);

    /**
     * Check whether the given position should toggle expanded state of a node.
     * 
     * @param node the node rendered
     * @param x x coordinate
     * @param y y coordinate
     * @return true if toggling should occur
     */
    boolean isInToggleArea(TimeBarNode node, int x, int y);

    /**
     * Check whether the given position is in the selection area of the hierarchy.
     * 
     * @param node the node
     * @param x x coordinate
     * @param y y coordinate
     * @return true if the coordniate is in the hierarchy area
     */
    boolean isInHierarchySelectionArea(TimeBarNode node, int x, int y);

    /**
     * Add a timebar marker.
     * 
     * @param marker marker to add
     */
    void addMarker(TimeBarMarker marker);

    /**
     * Remove a timebar marker.
     * 
     * @param marker marker to remove
     */
    void remMarker(TimeBarMarker marker);

    /**
     * Retrieve the list of al markers.
     * 
     * @return List of all added timebar markers
     */
    List<TimeBarMarker> getMarkers();

    /**
     * Directly set a list of markers.
     * 
     * @param markers list of timebar markers
     */
    void addMarkers(List<TimeBarMarker> markers);

    /**
     * Retrieve the selection delta used to determine whether a marker or interval edge is clicked/dragged.
     * 
     * @return max distance for detection
     */
    int getSelectionDelta();

    /**
     * Set the selection delta used to determine whether a marker or interval edge is clicked/dragged. A larger
     * selection delta will result in easier selection of of interval bounds and other in place draggable elements but
     * may cause problems when draggable elements are near to each other since more elements are in range and the
     * selected element will be determined by the sequence of checks in the code of the viewer.
     * <p>
     * A selection delta of n means clicks willl have to be on the element or (n-1) pixels away to consider an element
     * hit.
     * </p>
     * 
     * @param selectionDelta max distance for detection
     */
    void setSelectionDelta(int selectionDelta);

    /**
     * Check whether it is allowed to drag the limiting lines of the hierarchy ara and the header (yaxis) area.
     * 
     * @return true if dragging is allowed.
     */
    boolean isLineDraggingAllowed();

    /**
     * Set the allowance for line dragging of the limiting lines for hierarchy and header(yaxis) areas.
     * 
     * @param lineDraggingAllowed true for enabling the drag possibility
     */
    void setLineDraggingAllowed(boolean lineDraggingAllowed);

    /**
     * Check whether row height dragging is allowed.
     * 
     * @return <code>true</code> if enabled
     */
    boolean isRowHeightDragginAllowed();

    /**
     * Set whether row height dragging should be allowed.
     * 
     * @param rowHeightDraggingAllowed <code>true</code> to allow interactive dragging of row heights
     */
    void setRowHeightDraggingAllowed(boolean rowHeightDraggingAllowed);

    /**
     * Retrieve whether marker dragging in the diagram area is activated.
     * 
     * @return <code>true</code> if marker dragging in the diagram area is allowed
     */
    boolean getMarkerDraggingInDiagramArea();

    /**
     * Set whether marker dragging is allowed in the diagram area (If intervals are modificable the marker will only be
     * grabbed when no other operation is applicable).
     * 
     * @param allowed <code>true</code> for allowing marker drag in the diagram area.
     */
    void setMarkerDraggingInDiagramArea(boolean allowed);

    /**
     * Retrieve the y coordinate in the diagram pane for the given row.
     * 
     * @param row row
     * @return y coordinate in the diagram pane or -1 if the y coordinate could not be retrieved
     */
    int getYForRow(TimeBarRow row);

    /**
     * Retrieve the row for a given y coodinate (x if oriented vertcal; use getRowForXY instead!).
     * 
     * @param y coordinate
     * @return row or <code>null</code>
     */
    TimeBarRow getRowForY(int y);

    /**
     * Retrieve the row for a given point.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return row or <code>null</code>
     */
    TimeBarRow getRowForXY(int x, int y);

    /**
     * Check whether th delegate is setup for millisecond accuracy. This will only have an impact on the x scroll bar.
     * 
     * @return true if ms accuracy ist set
     */
    boolean isMilliAccuracy();

    /**
     * Set the delegates status concerning millisecond accuracy. If set to true the x scroll bar will operate in
     * milliseconds (normal operation: seconds).
     * 
     * @param milliAccuracy true to use ms accuracy
     */
    void setMilliAccuracy(boolean milliAccuracy);

    /**
     * Retrieve the state of the variable xscale state. If true a list of intervals contlrols different pps values for
     * different intervals on the axis.
     * 
     * @return true if a varying pps value is used
     */
    boolean hasVariableXScale();

    /**
     * Set the state for the variable xscale.
     * 
     * @param state true if a variable scale should be used.
     */
    void setVariableXScale(boolean state);

    /**
     * Retrieve the row that hold intervals (PpsIntervals) defining the pps value for different intervals.
     * 
     * @return the row or <code>null</code> if no variable xscale has been defined.
     */
    TimeBarNode getPpsRow();

    /**
     * Do horizontal scrolling by diff pixel. The method ought to copy the content of the diagramRect and produce dirty
     * regions for the parts not affected by the scroll. The method is intended to be used by the TimeBarViewerDelegate
     * only.
     * 
     * @param diff pixel difference (positive for right scroll)
     */
    void doScrollHorizontal(int diff);

    /**
     * Do vertical scrolling by diff pixel. The method ought to copy the content of the diagramRect and produce dirty
     * regions for the parts not affected by the scroll. The method is intended to be used by the TimeBarViewerDelegate
     * only.
     * 
     * @param diff pixel difference (positive for downwards scroll)
     */
    void doScrollVertical(int diff);

    /**
     * Get whether optimzed scrollnig is used.
     * 
     * @return true if optimized scrolling is used
     */
    boolean getOptimizeScrolling();

    /**
     * Set whether optimized scrolling should be used. The default is <code>false</code> since this may cause problems
     * on some platforms using SWT (Linux/GTK and OSX/intel). Optimized scrolling can not be used together with a
     * variable xscale.
     * 
     * @param optimizeScrolling true for optimized scrolling
     */
    void setOptimizeScrolling(boolean optimizeScrolling);

    /**
     * Retrieve the orientation of the viewer.
     * 
     * @return the orientation of the viewer
     */
    Orientation getOrientation();

    /**
     * Set the orientation of the viewer.
     * 
     * @param orientation the new orientation for the viewer
     */
    void setOrientation(Orientation orientation);

    /**
     * Get the number of rows (columns) that the viewer scales itself to.
     * 
     * @return number of rows to display or -1 if no scale has been set
     */
    int getAutoScaleRows();

    /**
     * Set a number of rows (columns) to be displayed by the viewer. The row height will always be changed to math the
     * number of rows to display.
     * 
     * @param rows the number of rows or -1 for no special scaling (default)
     */
    void setAutoScaleRows(int rows);

    /**
     * Called by the delegate whenever the selection changed (to support selction provider under SWT).
     */
    void fireSelectionChanged();

    /**
     * Add a listener to be informaed about interval changes.
     * 
     * @param listener listener
     */
    void addTimeBarChangeListener(ITimeBarChangeListener listener);

    /**
     * Remove a timebar change listener.
     * 
     * @param listener listener to remove
     */
    void removeTimeBarChangeListener(ITimeBarChangeListener listener);

    /**
     * Add a listener to be informed when the focus inside the viewer changes.
     * 
     * @param listener listener to be added.
     */
    void addFocussedIntervalListener(FocussedIntervalListener listener);

    /**
     * Remove a focussedIntervalListener.
     * 
     * @param listener listener to be removed from the listener list.
     */
    void remFocussedIntervalListener(FocussedIntervalListener listener);

    /**
     * Add a listener to be informed when the selection rect changes.
     * 
     * @param listener listener to be added.
     */
    void addSelectionRectListener(ISelectionRectListener listener);

    /**
     * Remove a selection rect listener.
     * 
     * @param listener listener to be removed from the listener list.
     */
    void remSelectionRectListener(ISelectionRectListener listener);

    /**
     * Check whether a row delimiter line is hit by a coordinate.
     * 
     * @param x x coord
     * @param y y coord
     * @return <code>true</code> if a row line is hit
     */
    boolean rowLineHit(int x, int y);

    /**
     * Check whether a given point is in the row axis area (hierarchy or header).
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return <code>true</code> if the point is in either hierarchy or header area
     */
    boolean isInRowAxis(int x, int y);

    /**
     * Check whether a given point is in the main diagram area.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return <code>true</code> if the point is in the diagram rectangle
     */
    boolean isInDiagram(int x, int y);

    /**
     * Check whether a date is currently visible.
     * 
     * @param date dat eto check
     * @return <code>true</code> if the date is currently displayed
     */
    boolean isDisplayed(JaretDate date);

    /**
     * Retrieve the strategy for filtering when painting (see {@link #setStrictClipTimeCheck(boolean)}).
     * 
     * @return <code>true</code> if strict checking is enabled
     */
    boolean getStrictClipTimeCheck();

    /**
     * Set the optimization strategy for interval filtering when painting.
     * 
     * @param strictClipTimeCheck If set to true, intervals are filtered strictly by their interval bounds, disallowing
     * rendering beyond the bounding box calculated by the interval bounds. Defaults to false resulting in filtering by
     * the preferred drawing area.
     */
    void setStrictClipTimeCheck(boolean strictClipTimeCheck);

    /**
     * Retrieve the time to be additionally considered (looking back) when deciding which intervals are to be painted.
     * Only taken into accoutn when StrictClipTimeCheck is false. Default is 120 Minutes.
     * 
     * @return time in minutes
     */
    int getScrollLookBackMinutes();

    /**
     * Set the additional time to be considered when deciding whether to draw an interval looking back. Only taken into
     * account when StrictClipTimeCheck is true.
     * 
     * @param scrollLookBackMinutes time in minutes
     */
    void setScrollLookBackMinutes(int scrollLookBackMinutes);

    /**
     * Retrieve the time to be additionally considered (looking forward) when deciding which intervals are to be
     * painted. Only taken into account when StrictClipTimeCheck is true.
     * 
     * @return time in mnutes
     */
    int getScrollLookForwardMinutes();

    /**
     * Set the additional time to be considered when deciding whether to draw an interval looking forward. Only taken
     * into account when StrictClipTimeCheck is true.
     * 
     * @param scrollLookForwardMinutes time in minutes
     */
    void setScrollLookForwardMinutes(int scrollLookForwardMinutes);

    /**
     * Get the seconds currently displayed by the diagram.
     * 
     * @return the number of seconds currently displayed by the diagram geometry
     */
    int getSecondsDisplayed();

    /**
     * Retrieve the used strategy for determing overlap information.
     * 
     * @return the overlap strategy
     */
    IOverlapStrategy getOverlapStrategy();

    /**
     * Set the strategy to be used for calculating overlap information.
     * 
     * @param overlapStrategy the strytegy to be used. May not be <code>null</code>.
     */
    void setOverlapStrategy(IOverlapStrategy overlapStrategy);

    /**
     * Retrieve the name set on the viewer.
     * 
     * @return the name or <code>null</code> if no name has been set
     */
    String getName();

    /**
     * Set a name as a simple string property for internal application use.
     * 
     * @param name name of the viewer
     */
    void setName(String name);

    /**
     * Retrieve the currently set autoscroll delta.
     * 
     * @return the autoscroll delat in pixel
     */
    int getAutoscrollDelta();

    /**
     * Set the autoscroll delta. This value will be used to deteremine the autoscroll deltas when the mouse pointer is
     * not in the diagram rectangle. It is specified in pixel so it is always relative to the timescale. The value will
     * also be used to limit the maximum delta when resizing an interval (edge dragging) with the cursor outside the
     * diagram rectangle.
     * 
     * @param autoscrollDelta delta in pixel
     */
    void setAutoscrollDelta(int autoscrollDelta);

    /**
     * If <code>true</code> all selected intervals will be dragged together with the interval on that the drag happened.
     * 
     * @return the state of the flag
     */
    boolean getDragAllSelectedIntervals();

    /**
     * If set to <code>true</code> all selected intervals are dragged when an interval is dragged. The default is false.
     * 
     * @param dragAllSelectedIntervals <code>true</code> to drag all selcted intervals
     */
    void setDragAllSelectedIntervals(boolean dragAllSelectedIntervals);

    /**
     * Retrieve the list of relations hit for a coordinate. This method is in the interface to allow tookit independent
     * implementation of the relation select mechanism.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return the list of relations or <code>null</code> if no relations can be found
     */
    List<IIntervalRelation> getRelationsForCoord(int x, int y);

    /**
     * Retrieve the state of the scroll to focus flag.
     * 
     * @return <code>true</code> if the viewer should scroll to the focussed interval
     */
    boolean getScrollOnFocus();

    /**
     * If set to true the viewer will scroll to the begin of an interval if it's focussed.
     * 
     * @param scrollOnFocus <code>true</code> for scrolling to the focussed interval
     */
    void setScrollOnFocus(boolean scrollOnFocus);

    /**
     * Retrieve whether the root node is shown when using a hierachical model.
     * 
     * @return <code>true</code> if the root is not shown
     */
    boolean getHideRoot();

    /**
     * Set whether the root node should be shown when using a hierachical model.
     * 
     * @param hideRoot <code>true</code> if the root node should be hidden
     */
    void setHideRoot(boolean hideRoot);

    /**
     * Retrieve the selected region.
     * 
     * @return the selected region or <code>null</code>
     */
    TBRect getRegionRect();

    /**
     * Remove the selction of a region if existent.
     */
    void clearRegionRect();

    /**
     * Enable/Disable region selections.
     * 
     * @param enabled <code>true</code> enabling
     */
    void setRegionRectEnable(boolean enabled);

    /**
     * Retrieve whether region selections are enabled.
     * 
     * @return true if region selections are enabled
     */
    boolean getRegionRectEnable();

    /**
     * Set whether all intervals in a non overlapping drawn row should use the same height/width.
     * 
     * @param useUniformHeight <code>true</code> for uniform heights/widths
     */
    void setUseUniformHeight(boolean useUniformHeight);

    /**
     * Retrieve whether uniform height is use for all intervals in a row.
     * 
     * @return <code>true</code> if all intervals in a non overlapping drawn row should use the same height/width
     */
    boolean getUseUniformHeight();

    /**
     * Set a date range and scaling that will be set as the initial display right after the viewer is displayed.
     * 
     * @param startDate start date
     * @param secondsDisplayed seconds to be displayed in the viewer
     */
    void setInitialDisplayRange(JaretDate startDate, int secondsDisplayed);

    /**
     * Retrieve the row and date of the click leading to the activation of a context menu.
     * 
     * @return Pair containing the row and date of the click position. Might be <code>null</code> if no click has been
     * recorded.
     */
    Pair<TimeBarRow, JaretDate> getPopUpInformation();

}
