/*
 *  File: RenderDelegate.java 
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
package de.jaret.util.ui.timebars.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.model.TimeBarNode;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;
import de.jaret.util.ui.timebars.strategy.OverlapInfo;
import de.jaret.util.ui.timebars.swt.renderer.GridRenderer;
import de.jaret.util.ui.timebars.swt.renderer.HeaderRenderer;
import de.jaret.util.ui.timebars.swt.renderer.HierarchyRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarGapRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarMarkerRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2;
import de.jaret.util.ui.timebars.swt.renderer.TimeScaleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TitleRenderer;

/**
 * This class contains the actual methods used for rendering a time bar viewer. They have been factored out to support
 * both printing (headless) and painting on the screen.
 * 
 * @author Peter Kliem
 * @version $Id: RenderDelegate.java 803 2008-12-28 19:30:23Z kliem $
 */
public class RenderDelegate {

    /**
     * Draw a timebar row (used by the timebar printer).
     * 
     * @param delegate the delegate
     * @param tbPrinter timebar printer
     * @param headerRenderer header renderer
     * @param hierarchyRenderer renderer for the hierarchy
     * @param printing tru for printing
     * @param gc GC to use
     * @param row row to draw
     * @param y begin y
     * @param selected true if the row is selected
     */
    public static void drawRowSimple(TimeBarViewerDelegate delegate, TimeBarPrinter tbPrinter,
            HeaderRenderer headerRenderer, HierarchyRenderer hierarchyRenderer, boolean printing, GC gc,
            TimeBarRow row, int y, boolean selected) {
        // first of all draw the row header
        if (row.getRowHeader() != null) {
            drawRowHeaderHorizontal(delegate, headerRenderer, printing, gc, y, row.getRowHeader(), selected, row);
        }

        // the draw the hierarchy display if configured
        if (hierarchyRenderer != null && delegate.getHierarchyWidth() > 0) {
            drawHierarchy(delegate, hierarchyRenderer, printing, gc, y, row, selected);
        }

        int rowHeight = delegate.getTimeBarViewState().getRowHeight(row);

        Rectangle clipSave = gc.getClipping();
        gc.setClipping(TimeBarViewer.convertRect(delegate.getDiagramRect()));

        // row grid if configured
        if (delegate.getDrawRowGrid()) {
            Color fg = gc.getForeground();
            // check whether the line is inside the diagram rect
            int ly = y + rowHeight - 1;
            if (delegate.getDiagramRect().y + delegate.getDiagramRect().height > ly) {
                gc.drawLine(delegate.getDiagramRect().x, ly, delegate.getDiagramRect().x
                        + delegate.getDiagramRect().width, ly);
            }
            gc.setForeground(fg);
        }

        // use the clipping bounds to reduce the painted intervals
        JaretDate start = delegate.getStartDate();
        JaretDate end = delegate.getEndDate();
        if (gc.isClipped()) {
            start = delegate.dateForCoord(gc.getClipping().x);
            end = delegate.dateForCoord(gc.getClipping().x + gc.getClipping().width);
        }
        List<Interval> intervals = row.getIntervals(start, end);
        for (Interval i : intervals) {
            // apply filter on intervals if set
            if (delegate.getIntervalFilter() == null || delegate.getIntervalFilter().isInResult(i)) {
                // get the renderer for that interval
                TimeBarRenderer renderer = tbPrinter.getRenderer(i.getClass());
                if (delegate.getTimeBarViewState().getDrawOverlapping(row)) {
                    drawIntervalHorizontal(delegate, renderer, printing, gc, y, i, null, row);
                } else {
                    drawIntervalHorizontal(delegate, renderer, printing, gc, y, i, delegate.getOverlapStrategy()
                            .getOverlapInfo(row, i), row);
                }
            }
        }
        gc.setClipping(clipSave);
    }

    /**
     * Render the row gaps horizontal.
     * 
     * @param delegate the delegate
     * @param renderer the gap renderer to use
     * @param printing true for printing
     * @param gc GC
     * @param x begin x
     * @param y begin y
     * @param row row to draw the gaps for
     * @param selected true if the row is selected
     */
    public static void drawRowGaps(TimeBarViewerDelegate delegate, TimeBarGapRenderer renderer, boolean printing,
            GC gc, int x, int y, TimeBarRow row, boolean selected) {
        // TODO VERTICAL
        // use the clip bounds (if given) to shorten the region to be
        // painted
        JaretDate start = delegate.getStartDate();
        JaretDate end = delegate.getEndDate();
        if (gc.isClipped()) {
            start = delegate.dateForXY(gc.getClipping().x, gc.getClipping().y);
            end = delegate.dateForXY(gc.getClipping().x + gc.getClipping().width, gc.getClipping().y
                    + gc.getClipping().height);
        }
        // for the gaps we need a minimum of two intervals
        // those has to be in the displayed intervals, so we apply the
        // filter first (if set) and do the selction
        // ourself. The alogorithm is highly dependet on the rodering of the
        // list! This should be guaranteed by the model
        List<Interval> intervals = new ArrayList<Interval>();
        Interval firstInterval = null;

        for (Interval interval : row.getIntervals()) {
            if (delegate.getIntervalFilter() == null || delegate.getIntervalFilter().isInResult(interval)) {
                if (interval.getEnd().compareTo(start) < 0) {
                    // before the starting date: remember the nearest
                    // interval
                    if (firstInterval == null
                            || start.diffMilliSeconds(interval.getEnd()) < start.diffMilliSeconds(firstInterval
                                    .getEnd())) {
                        firstInterval = interval;
                    }
                } else if (interval.contains(start)) {
                    // direct hit
                    firstInterval = interval;
                } else {
                    // in or after the starting date: copy the intervals
                    // until we have one behind the end date or
                    // a direct hit
                    // First of all, add the firstInterval to the resulting
                    // list once
                    if (firstInterval != null) {
                        intervals.add(firstInterval);
                        firstInterval = null; // we don't need the reference
                        // and by setting it to null
                        // we do not add it twice
                    }
                    if (interval.contains(end)) {
                        // direct hit
                        intervals.add(interval);
                        break; // finished
                    }
                    if (interval.getBegin().compareTo(end) > 0) {
                        intervals.add(interval);
                        break; // found an interval beginning behind the end
                        // date
                    } else {
                        // all between: add
                        intervals.add(interval);
                    }
                }
            }
        }
        Interval lastInterval = null;
        for (Interval i : intervals) {
            // draw gap if there is lastInterval
            if (lastInterval != null) {
                RenderDelegate.drawGap(delegate, renderer, false, gc, row, x, y, lastInterval, i);
            }
            // remember last interval for next turn
            lastInterval = i;
        }
    }

    /**
     * Render a single interval.
     * 
     * @param delegate th edelegate
     * @param renderer renderer to use
     * @param printing true for printing
     * @param gc GC
     * @param y begin y
     * @param interval the interval to render
     * @param selected true if the intervals should be drawn selected
     */
    // public static void drawIntervalX(TimeBarViewerDelegate delegate, TimeBarRenderer renderer, boolean printing, GC
    // gc,
    // int y, Interval interval, boolean selected) {
    // if (renderer != null) {
    // int x = delegate.xForDate(interval.getBegin());
    // int x2 = delegate.xForDate(interval.getEnd());
    // int width = x2 - x;
    // if (x2 < delegate.getDiagramRect().x) {
    // // if the end is not shown, simply return
    // return;
    // }
    // int height = delegate.getRowHeight();
    // Rectangle drawingArea = new Rectangle(x, y, width, height);
    //
    // // calculate height for clipping
    // if (y + height > delegate.getDiagramRect().y + delegate.getDiagramRect().height) {
    // height = height - (y + height - (delegate.getDiagramRect().y + delegate.getDiagramRect().height));
    // }
    // // calc x clipping and set clipping rect
    // Rectangle clip = new Rectangle(x < delegate.getDiagramRect().x ? delegate.getDiagramRect().x : x, y, width,
    // height);
    //
    // Rectangle clipSave = gc.getClipping();
    // gc.setClipping(clip.intersection(clipSave));
    // renderer.draw(gc, drawingArea, delegate, interval, selected, printing, false);
    // gc.setClipping(clipSave);
    // }
    // }
    /**
     * Draws a single interval.
     * 
     * @param delegate delegate
     * @param renderer renderer to use
     * @param printing true if this rendering is for a printer
     * @param gc GC to paint on
     * @param y y coordinate of the row
     * @param interval interval to be painted
     * @param oi overlap information (may be null when drawing overlapped)
     * @param row of the interval beeing rendered
     */
    public static void drawIntervalHorizontal(TimeBarViewerDelegate delegate, TimeBarRenderer renderer,
            boolean printing, GC gc, int y, Interval interval, OverlapInfo oi, TimeBarRow row) {
        if (renderer != null) {
            int rowHeight = delegate.getTimeBarViewState().getRowHeight(row);
            int maxOverlapping = 1;
            int oiPos = 0;
            if (oi != null) {
                maxOverlapping = oi.maxOverlapping + 1;
                oiPos = oi.pos;
            }

            int height = 0;
            if (!delegate.getUseUniformHeight()){
                height = rowHeight / maxOverlapping;
            } else {
                height = rowHeight / delegate.getOverlapStrategy().getMaxOverlapCount(row);
            }
            y = y + oiPos * height;
            
            int x = delegate.xForDate(interval.getBegin());
            int x2 = delegate.xForDate(interval.getEnd());
            int width = x2 - x;
            Rectangle intervalArea = new Rectangle(x, y, width, height);

            boolean selected = delegate.getSelectionModel().isSelected(interval);
            boolean overlapping = oi != null ? oi.overlappingCount > 0 : false;

            Rectangle drawingArea = new Rectangle(intervalArea.x, intervalArea.y, intervalArea.width,
                    intervalArea.height);
            if (renderer instanceof TimeBarRenderer2) {
                drawingArea = ((TimeBarRenderer2) renderer).getPreferredDrawingBounds(intervalArea, delegate, interval,
                        selected, printing, overlapping);
            }

            // check whether any portion of the targeted area is in the dirty range
            if (!gc.getClipping().intersects(drawingArea)) {
                // nothing to do
                return;
            }

            // calculate height for clipping
            if (drawingArea.y + drawingArea.height > delegate.getDiagramRect().y + delegate.getDiagramRect().height) {
                height = height
                        - (drawingArea.y + drawingArea.height - (delegate.getDiagramRect().y + delegate
                                .getDiagramRect().height));
            }
            // calc x clipping and set clipping rect
            Rectangle clip = new Rectangle(drawingArea.x < delegate.getDiagramRect().x ? delegate.getDiagramRect().x
                    : drawingArea.x, drawingArea.y, drawingArea.width, height);

            Rectangle clipSave = gc.getClipping();
            gc.setClipping(clip.intersection(clipSave));
            renderer.draw(gc, intervalArea, delegate, interval, selected, printing, overlapping);
            gc.setClipping(clipSave);
        }
    }

    /**
     * Draws a single interval (Vertical orientation).
     * 
     * @param delegate delegate
     * @param renderer renderer to use
     * @param printing true if this rendering is for a printer
     * @param gc GC tio paint on
     * @param x x coordinate of the row
     * @param interval interval to be painted
     * @param oi overlap information (may be null when drawing overlapped)
     * @param row of the interval beeing rendered
     */
    public static void drawIntervalVertical(TimeBarViewerDelegate delegate, TimeBarRenderer renderer, boolean printing,
            GC gc, int x, Interval interval, OverlapInfo oi, TimeBarRow row) {
        if (renderer != null) {
            int rowHeight = delegate.getTimeBarViewState().getRowHeight(row);
            int maxOverlapping = 1;
            int oiPos = 0;
            if (oi != null) {
                maxOverlapping = oi.maxOverlapping + 1;
                oiPos = oi.pos;
            }

            int width = 0;
            if (!delegate.getUseUniformHeight()){
                width = rowHeight / maxOverlapping;
            } else {
                width = rowHeight / delegate.getOverlapStrategy().getMaxOverlapCount(row);
            }
            x = x + oiPos * width;
            int y = delegate.xForDate(interval.getBegin());
            int y2 = delegate.xForDate(interval.getEnd());
            int height = y2 - y;
            Rectangle intervalArea = new Rectangle(x, y, width, height);

            boolean selected = delegate.getSelectionModel().isSelected(interval);
            boolean overlapping = oi != null ? oi.overlappingCount > 0 : false;

            Rectangle drawingArea = new Rectangle(intervalArea.x, intervalArea.y, intervalArea.width,
                    intervalArea.height);
            if (renderer instanceof TimeBarRenderer2) {
                drawingArea = ((TimeBarRenderer2) renderer).getPreferredDrawingBounds(intervalArea, delegate, interval,
                        selected, printing, overlapping);
            }

            // check whether any portion of the targeted area is in the dirty range
            if (!gc.getClipping().intersects(drawingArea)) {
                // nothing to do
                return;
            }

            // calculate width for clipping
            if (drawingArea.x + drawingArea.width > delegate.getDiagramRect().x + delegate.getDiagramRect().width) {
                width = width
                        - (drawingArea.x + drawingArea.width - (delegate.getDiagramRect().x + delegate.getDiagramRect().width));
            }
            // calc y clipping and set clipping rect
            Rectangle clip = new Rectangle(drawingArea.x, drawingArea.y < delegate.getDiagramRect().y ? delegate
                    .getDiagramRect().y : drawingArea.y, width, drawingArea.height);

            // // calculate width for clipping
            // if (x + width > delegate.getDiagramRect().x + delegate.getDiagramRect().width) {
            // width = width - (x + width - (delegate.getDiagramRect().x + delegate.getDiagramRect().width));
            // }
            // // calc y clipping and set clipping rect
            // Rectangle clip = new Rectangle(x, y < delegate.getDiagramRect().y ? delegate.getDiagramRect().y : y,
            // width,
            // height);

            Rectangle clipSave = gc.getClipping();
            gc.setClipping(clip.intersection(clipSave));
            renderer.draw(gc, intervalArea, delegate, interval, delegate.getSelectionModel().isSelected(interval),
                    printing, oi.overlappingCount > 0);
            gc.setClipping(clipSave);
        }
    }

    /**
     * Draw a row header (horizontal orientation).
     * 
     * @param delegate the delegate
     * @param renderer renderer to use
     * @param printing true for printing
     * @param gc GC
     * @param y begin y
     * @param header header to draw
     * @param selected true for selected
     * @param row of the header beeing rendered
     */
    public static void drawRowHeaderHorizontal(TimeBarViewerDelegate delegate, HeaderRenderer renderer,
            boolean printing, GC gc, int y, TimeBarRowHeader header, boolean selected, TimeBarRow row) {
        if (renderer != null && delegate.getYAxisWidth() > 0) {
            int x = delegate.getYAxisRect().x;
            int width = delegate.getYAxisWidth() - 1;
            int rowHeight = delegate.getTimeBarViewState().getRowHeight(row);
            Rectangle drawingArea = new Rectangle(x, y, width, rowHeight);

            int clipheight = rowHeight;
            if (y + rowHeight > delegate.getDiagramRect().y + delegate.getDiagramRect().height) {
                clipheight = clipheight
                        - (y + clipheight - (delegate.getDiagramRect().y + delegate.getDiagramRect().height));
            }
            Rectangle clip = new Rectangle(x, y, width, clipheight);
            Rectangle clipSave = gc.getClipping();
            gc.setClipping(clip.intersection(clipSave));
            renderer.draw(gc, drawingArea, delegate, header, selected, printing);
            gc.setClipping(clipSave);
        }
    }

    /**
     * Draw a row header (vertical orientation).
     * 
     * @param delegate the delegate
     * @param renderer renderer to use
     * @param printing true for printing
     * @param gc GC
     * @param x begin x
     * @param header header to draw
     * @param selected true for selected
     * @param row of the header beeing rendered
     */
    public static void drawRowHeaderVertical(TimeBarViewerDelegate delegate, HeaderRenderer renderer, boolean printing,
            GC gc, int x, TimeBarRowHeader header, boolean selected, TimeBarRow row) {
        if (renderer != null && delegate.getYAxisWidth() > 0) {
            int y = delegate.getYAxisRect().y;
            int height = delegate.getYAxisWidth() - 1;
            int rowWidth = delegate.getTimeBarViewState().getRowHeight(row);
            Rectangle drawingArea = new Rectangle(x, y, rowWidth, height);

            int clipwidth = rowWidth;
            if (x + rowWidth > delegate.getDiagramRect().x + delegate.getDiagramRect().width) {
                clipwidth = clipwidth
                        - (x + clipwidth - (delegate.getDiagramRect().x + delegate.getDiagramRect().width));
            }
            Rectangle clip = new Rectangle(x, y, clipwidth, height);
            Rectangle clipSave = gc.getClipping();
            gc.setClipping(clip.intersection(clipSave));
            renderer.draw(gc, drawingArea, delegate, header, selected, printing);
            gc.setClipping(clipSave);
        }
    }

    /**
     * Draw hierarchy element.
     * 
     * @param delegate the delegate
     * @param renderer the renderer to use
     * @param printing true for printing
     * @param gc GC
     * @param y begin y
     * @param row the element is for
     * @param selected true for selected
     */
    public static void drawHierarchy(TimeBarViewerDelegate delegate, HierarchyRenderer renderer, boolean printing,
            GC gc, int y, TimeBarRow row, boolean selected) {
        if (renderer != null) {
            int rowHeight = delegate.getTimeBarViewState().getRowHeight(row);
            int level = 0;
            int depth = 0;
            boolean expanded = false;
            boolean leaf = true;
            if (row instanceof TimeBarNode) {
                TimeBarNode node = (TimeBarNode) row;
                if (delegate.getHierarchicalViewState().isExpanded(node)) {
                    expanded = true;
                }
                leaf = node.getChildren().size() == 0;
                level = node.getLevel();
                depth = delegate.getHierarchicalModel().getDepth();
            }

            int x = delegate.getHierarchyRect().x;
            int width = delegate.getHierarchyWidth() - 1;
            Rectangle drawingArea = new Rectangle(x, y, width, rowHeight);

            int clipheight = rowHeight;
            if (y + rowHeight > delegate.getDiagramRect().y + delegate.getDiagramRect().height) {
                clipheight = clipheight
                        - (y + clipheight - (delegate.getDiagramRect().y + delegate.getDiagramRect().height));
            }
            Rectangle clip = new Rectangle(x, y, width, clipheight);
            Rectangle clipSave = gc.getClipping();
            gc.setClipping(clip.intersection(clipSave));
            // draw!
            renderer.draw(gc, drawingArea, delegate, row, selected, expanded, leaf, level, depth, printing);
            gc.setClipping(clipSave);
        }
    }

    /**
     * Draw hierarchy element (vertical orientation).
     * 
     * @param delegate the delegate
     * @param renderer the renderer to use
     * @param printing true for printing
     * @param gc GC
     * @param x begin x
     * @param row the element is for
     * @param selected true for selected
     */
    public static void drawHierarchyVertical(TimeBarViewerDelegate delegate, HierarchyRenderer renderer,
            boolean printing, GC gc, int x, TimeBarRow row, boolean selected) {
        if (renderer != null) {
            int level = 0;
            int depth = 0;
            boolean expanded = false;
            boolean leaf = true;
            if (row instanceof TimeBarNode) {
                TimeBarNode node = (TimeBarNode) row;
                if (delegate.getHierarchicalViewState().isExpanded(node)) {
                    expanded = true;
                }
                leaf = node.getChildren().size() == 0;
                level = node.getLevel();
                depth = delegate.getHierarchicalModel().getDepth();
            }

            int y = delegate.getHierarchyRect().y;
            int height = delegate.getHierarchyWidth() - 1;
            int rowWidth = delegate.getTimeBarViewState().getRowHeight(row);
            Rectangle drawingArea = new Rectangle(x, y, rowWidth, height);

            int clipwidth = rowWidth;
            if (x + rowWidth > delegate.getDiagramRect().x + delegate.getDiagramRect().width) {
                clipwidth = clipwidth
                        - (x + clipwidth - (delegate.getDiagramRect().x + delegate.getDiagramRect().width));
            }
            Rectangle clip = new Rectangle(x, y, clipwidth, height);
            Rectangle clipSave = gc.getClipping();
            gc.setClipping(clip.intersection(clipSave));
            // draw!
            renderer.draw(gc, drawingArea, delegate, row, selected, expanded, leaf, level, depth, printing);
            gc.setClipping(clipSave);

        }
    }

    /**
     * Draw the xaxis (timescale).
     * 
     * @param delegate the delegate
     * @param renderer the renderer
     * @param printing true for printing
     * @param gc GC
     */
    public static void drawXAxis(TimeBarViewerDelegate delegate, TimeScaleRenderer renderer, boolean printing, GC gc) {
        if (renderer != null && delegate.getTimeScalePosition() != TimeBarViewerInterface.TIMESCALE_POSITION_NONE) {
            Rectangle clipSave = gc.getClipping();
            gc.setClipping(convertRect(delegate.getXAxisRect()).intersection(clipSave));
            renderer.draw(gc, convertRect(delegate.getXAxisRect()), delegate,
                    delegate.getTimeScalePosition() == TimeBarViewerInterface.TIMESCALE_POSITION_TOP, printing);
            gc.setClipping(clipSave);
        }
    }

    /**
     * Render the grid (background).
     * 
     * @param delegate delegate
     * @param gridRenderer the renderer to use
     * @param printing true for printing
     * @param gc GC
     */
    public static void drawGrid(TimeBarViewerDelegate delegate, GridRenderer gridRenderer, boolean printing, GC gc) {
        if (gridRenderer != null) {
            Rectangle clipSave = gc.getClipping();
            gc.setClipping(convertRect(delegate.getDiagramRect()).intersection(clipSave));
            gridRenderer.draw(gc, delegate, convertRect(delegate.getDiagramRect()), printing);
            gc.setClipping(clipSave);
        }
    }

    /**
     * Render the title area.
     * 
     * @param delegate the delegate
     * @param titleRenderer the renderer
     * @param printing true for printing
     * @param gc GC
     */
    public static void drawTitle(TimeBarViewerDelegate delegate, TitleRenderer titleRenderer, boolean printing, GC gc) {
        if (titleRenderer != null && delegate.getTimeScalePosition() != TimeBarViewerInterface.TIMESCALE_POSITION_NONE) {
            Rectangle clipSave = gc.getClipping();
            gc.setClipping(convertRect(delegate.getTitleRect()).intersection(clipSave));
            titleRenderer.draw(gc, convertRect(delegate.getTitleRect()), delegate, delegate.getTitle(), printing);
            gc.setClipping(clipSave);
        }
    }

    /**
     * Draws all markers for the diagram. If a marker is not currently displayed it will not be painted.
     * 
     * @param delegate the delegate
     * @param renderer the renderer to use
     * @param printing true for printing
     * @param gc GC
     */
    public static void drawMarkers(TimeBarViewerDelegate delegate, TimeBarMarkerRenderer renderer, boolean printing,
            GC gc) {
        if (delegate.getMarkers() != null) {
            for (TimeBarMarker marker : delegate.getMarkers()) {
                if (delegate.isDisplayed(marker.getDate())) {
                    drawMarker(delegate, renderer, printing, gc, marker);
                }
            }
        }
    }

    /**
     * Draw a single marker.
     * 
     * @param delegate the delegate
     * @param renderer renderer to use
     * @param printing true for printing
     * @param gc GC
     * @param marker the marker
     */
    public static void drawMarker(TimeBarViewerDelegate delegate, TimeBarMarkerRenderer renderer, boolean printing,
            GC gc, TimeBarMarker marker) {
        if (renderer != null) {
            boolean isDragged = false;
            if (delegate.getDraggedMarker() == marker) {
                isDragged = true;
            }
            renderer.draw(gc, delegate, marker, isDragged, printing);
        }
    }

    /**
     * Draw a gap beetween two intervals.
     * 
     * @param delegate the delegate
     * @param renderer the renderer to use
     * @param printing true for printing
     * @param gc GC
     * @param row the row of both intervals
     * @param xx begin x
     * @param y begin y
     * @param i1 first interval
     * @param i2 second interval
     */
    public static void drawGap(TimeBarViewerDelegate delegate, TimeBarGapRenderer renderer, boolean printing, GC gc,
            TimeBarRow row, int xx, int y, Interval i1, Interval i2) {
        if (renderer != null) {
            if (delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL) {
                int x = delegate.xForDate(i1.getEnd());
                int width = delegate.xForDate(i2.getBegin()) - x;
                int height = delegate.getTimeBarViewState().getRowHeight(row);
                Rectangle drawingArea = new Rectangle(x, y, width, height);
                // calculate height for clipping
                if (y + height > delegate.getDiagramRect().y + delegate.getDiagramRect().height) {
                    height = height - (y + height - (delegate.getDiagramRect().y + delegate.getDiagramRect().height));
                }
                // calc x clipping and set clipping rect
                Rectangle clip = new Rectangle(x < delegate.getDiagramRect().x ? delegate.getDiagramRect().x : x, y,
                        width, height);
                Rectangle clipSave = gc.getClipping();
                gc.setClipping(clip.intersection(clipSave));
                renderer.draw(gc, delegate, row, i1, i2, drawingArea, printing);
                gc.setClipping(clipSave);
            } else {
                // vertical
                int yy = delegate.xForDate(i1.getEnd());
                int height = delegate.xForDate(i2.getBegin()) - yy;
                int width = delegate.getTimeBarViewState().getRowHeight(row);
                Rectangle drawingArea = new Rectangle(xx, yy, width, height);
                // calculate width for clipping
                if (xx + width > delegate.getDiagramRect().x + delegate.getDiagramRect().width) {
                    width = width - (xx + width - (delegate.getDiagramRect().x + delegate.getDiagramRect().width));
                }
                // calc y clipping and set clipping rect
                Rectangle clip = new Rectangle(xx, yy < delegate.getDiagramRect().y ? delegate.getDiagramRect().y : yy,
                        width, height);
                Rectangle clipSave = gc.getClipping();
                gc.setClipping(clip.intersection(clipSave));
                renderer.draw(gc, delegate, row, i1, i2, drawingArea, printing);
                gc.setClipping(clipSave);
            }
        }
    }

    /**
     * Convert a java.awt.Rectangle to an swt one.
     * 
     * @param rect awt.graphics.Rectangle
     * @return swt.graphics.Rectangle
     */
    private static Rectangle convertRect(java.awt.Rectangle rect) {
        return new Rectangle(rect.x, rect.y, rect.width, rect.height);
    }

}
