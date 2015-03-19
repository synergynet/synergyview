/*
 *  File: TimeBarPrinter.java 
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.ViewConfiguration;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.renderer.GlobalAssistantRenderer;
import de.jaret.util.ui.timebars.swt.renderer.GridRenderer;
import de.jaret.util.ui.timebars.swt.renderer.HeaderRenderer;
import de.jaret.util.ui.timebars.swt.renderer.HierarchyRenderer;
import de.jaret.util.ui.timebars.swt.renderer.IRelationRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarGapRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarMarkerRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeScaleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TitleRenderer;

/**
 * Utility class for printing a time bar chart. All renderers used have to support the creation of a print renderer.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarPrinter.java 800 2008-12-27 22:27:33Z kliem $
 */
public class TimeBarPrinter {
    /** default margin in cm. */
    private static final double DEFAULT_MARGIN = 1.0;

    /** default heigth for the footer line in cm. */
    private static final double DEFAULT_FOOTER_HEIGHT = 0.5;

    /** screen dpi on x axis. */
    private static final double SCREEN_DPI_X = 96.0;
    /** screen dpi on y axis. */
    private static final double SCREEN_DPI_Y = 96.0;

    /** one inch in cm. */
    private static final double INCH_IN_CM = 2.54;

    /** printer font name. */
    private static final String PRINTERFONTNAME = "Sans";
    /** size of the printer font. */
    private static final int PRINTERFONTSIZE = 6;

    /** printer device. */
    protected Printer _printer;
    /** scale factor horizontal. */
    protected double _scaleX;
    /** scale factor vertical. */
    protected double _scaleY;

    /** time scale renderer. */
    protected TimeScaleRenderer _timeScaleRenderer;
    /** header renderer. */
    protected HeaderRenderer _headerRenderer;
    /** time bar renderer. */
    protected TimeBarRenderer _renderer;
    /** hierarchy renderer. */
    protected HierarchyRenderer _hierarchyRenderer;
    /** grid renderer. */
    protected GridRenderer _gridRenderer;
    /** gap raenderer. */
    protected TimeBarGapRenderer _gapRenderer;
    /** marker renderer. */
    protected TimeBarMarkerRenderer _markerRenderer;
    /** title renderer. */
    protected TitleRenderer _titleRenderer;
    /** global assistance renderer. */
    protected GlobalAssistantRenderer _globalAssistantRenderer;
    /** relation renderer. */
    protected IRelationRenderer _relationRenderer;

    /** mapping between interval classes and renderers (printer). */
    protected Map<Class<? extends Interval>, TimeBarRenderer> _printerRendererMap = new HashMap<Class<? extends Interval>, TimeBarRenderer>();

    /** timebarviewerdelegate works for the printer. */
    protected TimeBarViewerDelegate _delegate = new TimeBarViewerDelegate(null);

    /** height of the footer in cm. */
    protected double _footerHeight = DEFAULT_FOOTER_HEIGHT;

    /** top margin in cm. */
    protected double _marginTop = DEFAULT_MARGIN;
    /** bottom margin in cm. */
    protected double _marginBottom = DEFAULT_MARGIN;
    /** left margin in cm. */
    protected double _marginLeft = DEFAULT_MARGIN;
    /** right margin in cm. */
    protected double _marginRight = DEFAULT_MARGIN;

    /**
     * Construct a timebar printer.
     * 
     * @param printer printer device
     */
    public TimeBarPrinter(Printer printer) {
        _printer = printer;
        Point dpi = _printer.getDPI();
        _scaleX = (double) dpi.x / SCREEN_DPI_X;
        _scaleY = (double) dpi.y / SCREEN_DPI_Y;
    }

    /**
     * Scale a pixel value to printer coordinates (x axis).
     * 
     * @param in value to scale
     * @return scaled value
     */
    public int scaleX(int in) {
        return (int) Math.round(_scaleX * (double) in);
    }

    /**
     * Retrieve the scale factor for the x axis.
     * 
     * @return scale factor
     */
    public double getScaleX() {
        return _scaleX;
    }

    /**
     * Scale a pixel value to printer coordinates (y axis).
     * 
     * @param in value to scale
     * @return scaled value
     */
    public int scaleY(int in) {
        return (int) Math.round(_scaleY * (double) in);
    }

    /**
     * Retrieve the scale factor for the y axis.
     * 
     * @return scale factor
     */
    public double getScaleY() {
        return _scaleY;
    }

    /**
     * Convert a value in cm to a number of printer pixels (x axis).
     * 
     * @param cm value to convert
     * @return converted value in printer pixel
     */
    protected int pixelForCmX(double cm) {
        Point dpi = _printer.getDPI();
        double inch = cm / INCH_IN_CM;
        return (int) (dpi.x * inch);
    }

    /**
     * Convert a value in cm to a number of printer pixels (y axis).
     * 
     * @param cm value to convert
     * @return converted value in printer pixel
     */
    protected int pixelForCmY(double cm) {
        Point dpi = _printer.getDPI();
        double inch = cm / INCH_IN_CM;
        return (int) (dpi.y * inch);
    }

    /**
     * Retrieve the printer device.
     * 
     * @return the printer device
     */
    public Printer getPrinter() {
        return _printer;
    }

    /**
     * Access to the delegate.
     * 
     * @return the delegate
     */
    public TimeBarViewerDelegate getDelegate() {
        return _delegate;
    }

    /**
     * Init the timebarprinter with the appropriate rendereres and settings from the timebarviewer.
     * 
     * @param tbv timebarviewer giving the settings
     */
    public void init(TimeBarViewer tbv) {
        // no optimized scrolling for printing
        _delegate.setOptimizeScrolling(false);
        // renderers
        if (tbv.getTimeScaleRenderer() != null) {
            _timeScaleRenderer = tbv.getTimeScaleRenderer().createPrintRenderer(_printer);
        }
        if (tbv.getHeaderRenderer() != null) {
            _headerRenderer = tbv.getHeaderRenderer().createPrintRenderer(_printer);
        }
        if (tbv.getGridRenderer() != null) {
            _gridRenderer = tbv.getGridRenderer().createPrintRenderer(_printer);
        }
        if (tbv.getHierarchyRenderer() != null) {
            _hierarchyRenderer = tbv.getHierarchyRenderer().createPrintRenderer(_printer);
        }
        if (tbv.getGapRenderer() != null) {
            _gapRenderer = tbv.getGapRenderer().createPrintRenderer(_printer);
        }
        if (tbv.getMarkerRenderer() != null) {
            _markerRenderer = tbv.getMarkerRenderer().createPrintRenderer(_printer);
        }
        if (tbv.getTitleRenderer() != null) {
            _titleRenderer = tbv.getTitleRenderer().createPrintRenderer(_printer);
        }
        if (tbv.getGlobalAssistantRenderer() != null) {
            _globalAssistantRenderer = tbv.getGlobalAssistantRenderer().createPrintRenderer(_printer);
        }
        if (tbv.getRelationRenderer() != null) {
            _relationRenderer = tbv.getRelationRenderer().createPrintRenderer(_printer);
        }

        // renderer map
        // create a print renderer for every renderer registerd with the viewer
        Map<Class<? extends Interval>, TimeBarRenderer> rendererMap = tbv.getRendererMapping();
        for (Class<? extends Interval> clazz : rendererMap.keySet()) {
            TimeBarRenderer displayRenderer = rendererMap.get(clazz);
            TimeBarRenderer printRenderer = displayRenderer.createPrintrenderer(_printer);
            _printerRendererMap.put(clazz, printRenderer);
        }

        // orientation
        boolean horizontal = tbv.getOrientation().equals(TimeBarViewerInterface.Orientation.HORIZONTAL);
        _delegate.setOrientation(tbv.getOrientation());

        // markers
        _delegate.addMarkers(tbv.getMarkers());

        _delegate.setModel(tbv.getHierarchicalModel());
        _delegate.setHierarchicalViewState(tbv.getHierarchicalViewState());
        _delegate.setModel(tbv.getModel());

        _delegate.setXAxisHeight(scaleY(tbv.getXAxisHeight()));
        if (_timeScaleRenderer.getHeight() > 0) {
            _delegate.setXAxisHeight(_timeScaleRenderer.getHeight());
        }
        _delegate.setTimeScalePosition(tbv.getTimeScalePosition());

        _delegate.setYAxisWidth(scaleX(tbv.getYAxisWidth()));
        _delegate.setHierarchyWidth(scaleX(tbv.getHierarchyWidth()));
        // row heights
        // default
        _delegate.setRowHeight(scaleY(tbv.getRowHeight()));
        if (tbv.getTimeBarViewState().getUseVariableRowHeights()) {
            _delegate.getTimeBarViewState().setUseVariableRowHeights(true);
            for (int i = 0; i < tbv.getModel().getRowCount(); i++) {
                TimeBarRow row = tbv.getModel().getRow(i);
                int height = tbv.getTimeBarViewState().getRowHeight(row);
                if (horizontal) {
                    height = scaleY(height);
                } else {
                    height = scaleX(height);
                }
                _delegate.getTimeBarViewState().setRowHeight(row, height);
            }
            // use the same strategy if set
            _delegate.getTimeBarViewState().setRowHeightStrategy(tbv.getTimeBarViewState().getRowHeightStrategy());
        }

        _delegate.setDrawRowGrid(tbv.getDrawRowGrid());

        // filter etcs
        _delegate.setRowSorter(tbv.getRowSorter());
        _delegate.setRowFilter(tbv.getRowFilter());
        _delegate.setIntervalFilter(tbv.getIntervalFilter());

        _delegate.setDrawOverlapping(tbv.getDrawOverlapping());
        _delegate.setTitle(tbv.getTitle());
    }

    /**
     * Retrieve a renderer for a given class. Checks all interfaces and all superclasses.
     * 
     * @param clazz class in question
     * @return renderer or null
     */
    protected TimeBarRenderer getRenderer(Class<? extends Interval> clazz) {
        TimeBarRenderer result = null;
        result = _printerRendererMap.get(clazz);
        if (result != null) {
            return result;
        }

        // direct interfaces
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> c : interfaces) {
            result = _printerRendererMap.get(c);
            if (result != null) {
                return result;
            }
        }

        // superclasses
        Class<?> sc = clazz.getSuperclass();

        while (sc != null) {
            result = _printerRendererMap.get(sc);
            if (result != null) {
                return result;
            }
            // interfaces of the superclass
            Class<?>[] scinterfaces = sc.getInterfaces();
            for (Class<?> c : scinterfaces) {
                result = _printerRendererMap.get(c);
                if (result != null) {
                    return result;
                }
            }
            sc = sc.getSuperclass();
        }

        return result;
    }

    /**
     * Do the printing.
     * 
     * @param vc configuration to use for printing.
     */
    public void print(ViewConfiguration vc) {

        int marginTop = pixelForCmY(_marginTop);
        int marginBottom = pixelForCmY(_marginBottom);
        int marginLeft = pixelForCmX(_marginLeft);
        int marginRight = pixelForCmX(_marginRight);

        int footerHeight = pixelForCmX(_footerHeight);

        int width = _printer.getClientArea().width - marginLeft - marginRight;
        int height = _printer.getClientArea().height - marginTop - marginBottom - footerHeight;

        _printer.startJob(vc.getName() != null ? vc.getName() : "timebars_print");

        // set a font that roughly matches the font size on screen
        GC gc = new GC(_printer);
        Font oldfont = gc.getFont();
        FontData fontdata = new FontData(PRINTERFONTNAME, PRINTERFONTSIZE, SWT.NULL);
        Font printerFont = new Font(_printer, fontdata);
        gc.setFont(printerFont);

        int secTotal;
        if (vc.getEndDate() != null) {
            secTotal = vc.getEndDate().diffSeconds(vc.getStartDate());
        } else {
            secTotal = _delegate.getModel().getMaxDate().diffSeconds(vc.getStartDate());
        }

        int totalyaxiswidth = _delegate.getHierarchyWidth() + _delegate.getYAxisWidth();

        _delegate.setDrawingOffset(marginLeft, marginTop);
        _delegate.preparePaint(width, height);
        // pps is determined by the seconds per page (first page with scales)
        double pps = (double) _delegate.getDiagramRect().width / (double) vc.getSecondsPerPage();
        _delegate.setPixelPerSecond(pps);

        // the number of pixels that will be needed to print all data
        long totalPixelWidth = (long) ((double) secTotal * pps);

        // calculate the number of pages along the x axis
        int pagesX;
        if (vc.getRepeatYAxis()) {
            pagesX = secTotal / vc.getSecondsPerPage();
            // check for "broken page"
            if (secTotal % vc.getSecondsPerPage() > 0) {
                pagesX += 1;
            }
        } else {
            if (secTotal <= vc.getSecondsPerPage()) {
                pagesX = 1;
            } else {
                long remain = totalPixelWidth;
                pagesX = 0;
                // first page with yscale
                pagesX++;
                remain -= _delegate.getDiagramRect().width;
                // subsequent pages
                pagesX += remain / (_delegate.getDiagramRect().width + totalyaxiswidth);
                // check for "broken page"
                if (remain % (_delegate.getDiagramRect().width + totalyaxiswidth) > 0) {
                    pagesX++;
                }
            }
        }

        // calculate the number of pages along the y axis
        int pagesY;
        List<Integer> firstRows = new ArrayList<Integer>();
        List<Integer> firstRowOffsets = new ArrayList<Integer>();

        int y = 0;
        int pageIdx = 0;
        int rowIdx = 0;

        while (rowIdx < _delegate.getRowCount()) {
            rowIdx = _delegate.getRowIdxForAbsoluteOffset(y);
            firstRows.add(rowIdx);
            firstRowOffsets.add(_delegate.getRowPixOffsetForAbsoluteOffset(rowIdx, y));
            if (vc.getRepeatScale() || pageIdx == 0) {
                y += _delegate.getDiagramRect().height;
            } else {
                y += _delegate.getDiagramRect().height + _delegate.getXAxisHeight();
            }

            pageIdx++;
            try {
                rowIdx = _delegate.getRowIdxForAbsoluteOffset(y);
            } catch (Exception e) {
                rowIdx = -1;
            }
            if (rowIdx >= _delegate.getRowCount() || rowIdx == -1) {
                break;
            }
        }
        pagesY = pageIdx;

        // System.out.println("pages x:" + pagesX + " pagesY:" + pagesY);

        int xaxisheigth = _delegate.getXAxisHeight();
        int yaxisWidth = _delegate.getYAxisWidth();
        int hierarchyWidth = _delegate.getHierarchyWidth();

        JaretDate pageStart = vc.getStartDate().copy();
        for (int px = 0; px < pagesX; px++) {
            _delegate.setStartDate(pageStart);
            for (int py = 0; py < pagesY; py++) {
                String footerText = vc.getFootLine() != null ? vc.getFootLine() : "";
                footerText += "(" + (px + 1) + "/" + pagesX + "," + (py + 1) + "/" + pagesY + ")";
                _printer.startPage();
                if (vc.getRepeatScale()
                        || (py == 0 && _delegate.getTimeScalePosition() == TimeBarViewerInterface.TIMESCALE_POSITION_TOP)
                        || (py == pagesY - 1 && _delegate.getTimeScalePosition() == TimeBarViewerInterface.TIMESCALE_POSITION_BOTTOM)) {
                    _delegate.setXAxisHeight(xaxisheigth);
                } else {
                    _delegate.setXAxisHeight(0);
                }
                if (vc.getRepeatYAxis() || px == 0) {
                    _delegate.setHierarchyWidth(hierarchyWidth);
                    _delegate.setYAxisWidth(yaxisWidth);
                } else {
                    _delegate.setHierarchyWidth(0);
                    _delegate.setYAxisWidth(0);
                }

                // do the geometry calculation
                _delegate.preparePaint(width, height);

                print(gc, footerText, firstRows.get(py), firstRowOffsets.get(py), _delegate.getDiagramRect().height,
                        footerHeight);
                _printer.endPage();
            }
            // advance the start date
            pageStart = _delegate.getEndDate().copy();

        }
        _printer.endJob();
        gc.setFont(oldfont);
        printerFont.dispose();
        gc.dispose();
    }

    /**
     * Do the actual printing of one page.
     * 
     * @param gc GC to use
     * @param footer footer string for the page
     * @param firstRow the index of the first row to be painted
     * @param firstRowOffset offset of the first row
     * @param diagramHeight height of the diagram
     * @param footerHeight the height of the footer in pixel
     */
    private void print(GC gc, String footer, int firstRow, int firstRowOffset, int diagramHeight, int footerHeight) {
        // System.out.println("print "+firstRow+" offset "+firstRowOffset);
        _delegate.setFirstRow(firstRow, firstRowOffset);

        RenderDelegate.drawGrid(_delegate, _gridRenderer, true, gc);
        if (_globalAssistantRenderer != null) {
            _globalAssistantRenderer.doRenderingBeforeIntervals(_delegate, gc, true);
        }
        RenderDelegate.drawXAxis(_delegate, _timeScaleRenderer, true, gc);
        drawRows(gc);
        RenderDelegate.drawMarkers(_delegate, _markerRenderer, true, gc);
        if (_delegate.getTitleRect().width > 0 && _delegate.getTitleRect().height > 0) {
            RenderDelegate.drawTitle(_delegate, _titleRenderer, true, gc);
        }
        if (footer != null) {
            drawFooter(gc, footer, _delegate.getDiagramRect().y + _delegate.getDiagramRect().height, footerHeight);
        }
        if (_globalAssistantRenderer != null) {
            _globalAssistantRenderer.doRenderingLast(_delegate, gc, true);
        }

        // WORKAROUND: since clipping seems to be unsupported by the printer gc
        // clear the right margin
        Color bg = gc.getBackground();
        gc.fillRectangle(_delegate.getDiagramRect().x + _delegate.getDiagramRect().width, _delegate.getDiagramRect().y,
                pixelForCmX(_marginLeft), _delegate.getDiagramRect().height);
        gc.setBackground(bg);

    }

    /**
     * Draw the footer.
     * 
     * @param gc GC
     * @param footer footer text
     * @param footerStartY the y position of the footer
     * @param footerHeight the height of the footer in pixel
     */
    private void drawFooter(GC gc, String footer, int footerStartY, int footerHeight) {
        Point extent = gc.textExtent(footer);
        int y = footerStartY + (footerHeight - extent.y) / 2;
        gc.drawString(footer, _delegate.getHierarchyRect().x, y);
    }

    /**
     * Render all rows to the given gc.
     * 
     * @param gc GC to use
     */
    private void drawRows(GC gc) {

        // relation rendering
        if (_relationRenderer != null) {
            _relationRenderer.renderRelations(_delegate, gc, true);
        }

        int firstRow = _delegate.getFirstRow();
        // separating line to the header
        // and the hierarchy area
        int startY = Math.min(_delegate.getXAxisRect().y, _delegate.getDiagramRect().y);
        int endY = Math.max(_delegate.getXAxisRect().y + _delegate.getXAxisRect().height, _delegate.getDiagramRect().y
                + _delegate.getDiagramRect().height);
        // line between header and diagram
        if (_delegate.getYAxisWidth() > 0) {
            gc.drawLine(_delegate.getDiagramRect().x - 1, startY, _delegate.getDiagramRect().x - 1, endY);
        }
        // line between hierarchy and header
        if (_delegate.getHierarchyWidth() > 0) {
            gc.drawLine(_delegate.getHierarchyRect().x + _delegate.getHierarchyRect().width - 1, startY, _delegate
                    .getHierarchyRect().x
                    + _delegate.getHierarchyRect().width - 1, endY);
        }
        int upperYBound = 0;
        int lowerYBound = _printer.getClientArea().height;
        if (gc.isClipped()) {
            upperYBound = gc.getClipping().y;
            lowerYBound = upperYBound + gc.getClipping().height;
        }
        for (int r = firstRow; r <= firstRow + _delegate.getRowsDisplayed() + 1 && r < _delegate.getRowCount(); r++) {
            TimeBarRow row = _delegate.getRow(r);
            int y = _delegate.yForRow(row);
            if (y == -1) {
                // no coord -> is not displayed
                break;
            }
            // row is drawn if either the beginning or the end is inside the
            // clipping rect
            // or if the upperBound is inside the row rect (clipping rect is
            // inside the row rect
            int rowHeight = _delegate.getTimeBarViewState().getRowHeight(row);
            if ((y >= upperYBound && y <= lowerYBound)
                    || (y + rowHeight >= upperYBound && y + rowHeight <= lowerYBound)
                    || (upperYBound > y && upperYBound < y + rowHeight)) {
                RenderDelegate.drawRowSimple(_delegate, this, _headerRenderer, _hierarchyRenderer, true, gc, _delegate
                        .getRow(r), y, false);
                // draw gaps if a renderer is set
                if (_gapRenderer != null) {
                    RenderDelegate.drawRowGaps(_delegate, _gapRenderer, true, gc, 0, y, _delegate.getRow(r), false);
                }
            }
        }
    }

    /**
     * Dispose whatever needs disposal. The method has to be called by the user of the the timebar printer.
     */
    public void dispose() {
        _delegate.dispose();
        if (_headerRenderer != null) {
            _headerRenderer.dispose();
        }
        if (_timeScaleRenderer != null) {
            _timeScaleRenderer.dispose();
        }
        if (_renderer != null) {
            _renderer.dispose();
        }
        if (_hierarchyRenderer != null) {
            _hierarchyRenderer.dispose();
        }
        if (_gridRenderer != null) {
            _gridRenderer.dispose();
        }
        if (_markerRenderer != null) {
            _markerRenderer.dispose();
        }
        if (_titleRenderer != null) {
            _titleRenderer.dispose();
        }
        if (_gapRenderer != null) {
            _gapRenderer.dispose();
        }
        if (_globalAssistantRenderer != null) {
            _globalAssistantRenderer.dispose();
        }
        if (_relationRenderer != null) {
            _relationRenderer.dispose();
        }

        // dispose all interval renderers
        for (TimeBarRenderer renderer : _printerRendererMap.values()) {
            renderer.dispose();
        }

    }

    /**
     * @return footerheigth in cm
     */
    public double getFooterHeight() {
        return _footerHeight;
    }

    /**
     * 
     * @param footerHeight height of the footer line in cm
     */
    public void setFooterHeight(double footerHeight) {
        _footerHeight = footerHeight;
    }

    /**
     * Retrieve the top margin (cm).
     * 
     * @return margin in cm
     */
    public double getMarginTop() {
        return _marginTop;
    }

    /**
     * Set the top margin (cm).
     * 
     * @param marginTop margin in cm
     */
    public void setMarginTop(double marginTop) {
        _marginTop = marginTop;
    }

    /**
     * Retrieve the bottom margin (cm).
     * 
     * @return margin in cm
     */
    public double getMarginBottom() {
        return _marginBottom;
    }

    /**
     * Set the bottom margin (cm).
     * 
     * @param marginBottom margin in cm
     */
    public void setMarginBottom(double marginBottom) {
        _marginBottom = marginBottom;
    }

    /**
     * Retrieve the left margin (cm).
     * 
     * @return margin in cm
     */
    public double getMarginLeft() {
        return _marginLeft;
    }

    /**
     * Set the left margin (cm).
     * 
     * @param marginLeft margin in cm
     */
    public void setMarginLeft(double marginLeft) {
        _marginLeft = marginLeft;
    }

    /**
     * Retrieve the right margin (cm).
     * 
     * @return margin in cm
     */
    public double getMarginRight() {
        return _marginRight;
    }

    /**
     * Set the right margin (cm).
     * 
     * @param marginRight margin in cm
     */
    public void setMarginRight(double marginRight) {
        _marginRight = marginRight;
    }
}
