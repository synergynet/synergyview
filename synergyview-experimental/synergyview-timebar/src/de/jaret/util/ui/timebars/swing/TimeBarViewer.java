/*
 *  File: TimeBarViewer.java 
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
package de.jaret.util.ui.timebars.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.misc.Pair;
import de.jaret.util.ui.timebars.TimeBarIntervalFilter;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarRowFilter;
import de.jaret.util.ui.timebars.TimeBarRowSorter;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
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
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;
import de.jaret.util.ui.timebars.model.TimeBarSelectionModel;
import de.jaret.util.ui.timebars.strategy.IOverlapStrategy;
import de.jaret.util.ui.timebars.strategy.ITickProvider;
import de.jaret.util.ui.timebars.strategy.OverlapInfo;
import de.jaret.util.ui.timebars.swing.renderer.DefaultGridRenderer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultHeaderRenderer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultMarkerRenderer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultMiscRenderer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultTimeBarRenderer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultTimeScaleRenderer;
import de.jaret.util.ui.timebars.swing.renderer.GridRenderer;
import de.jaret.util.ui.timebars.swing.renderer.HeaderRenderer;
import de.jaret.util.ui.timebars.swing.renderer.HierarchyRenderer;
import de.jaret.util.ui.timebars.swing.renderer.IGlobalAssistantRenderer;
import de.jaret.util.ui.timebars.swing.renderer.IMarkerRenderer;
import de.jaret.util.ui.timebars.swing.renderer.IMiscRenderer;
import de.jaret.util.ui.timebars.swing.renderer.IRelationRenderer;
import de.jaret.util.ui.timebars.swing.renderer.ITitleRenderer;
import de.jaret.util.ui.timebars.swing.renderer.TimeBarGapRenderer;
import de.jaret.util.ui.timebars.swing.renderer.TimeBarRenderer;
import de.jaret.util.ui.timebars.swing.renderer.TimeScaleRenderer;

/**
 * Viewer for a TimeBarModel (Swing version). Displays the intervals using a renderer. Supports sorting and/or filtering
 * of the rows in the model without affecting the model itself.
 * <p>
 * <b>NOTE: The Swing version is not as complete as the swt version!</b>
 * </p>
 * <p>
 * The implementation depends on the <code>TimeBarViewerDelegate</code> for the operations and calculations that is
 * shared between the Swing and the SWT implementation of the viewer. The delegate is accesible via the method
 * <code>getDelegate()</code>. It supplies additional functionality not delegated by methods in this class (will be done
 * in future releases).
 * </p>
 * <p>
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarViewer.java 906 2009-11-13 21:15:27Z kliem $
 */
@SuppressWarnings("serial")
public class TimeBarViewer extends JPanel implements TimeBarViewerInterface, ChangeListener, ComponentListener {
    /** DEBUGGING OPTION: if set to true the actual paint times will be printed to stdout. */
    private static final boolean SHOWPAINTTIME = false;

    /** the preferred height. */
    private static final int PREFHEIGHT = 300;
    /** the preferred width. */
    private static final int PREFWIDTH = 500;

    /**
     * The delegate is the heart of the joined implementation between Swing and SWT version of the viewer.
     */
    protected TimeBarViewerDelegate _delegate;

    /** renderer used for the stimescale. */
    protected transient TimeScaleRenderer _timeScaleRenderer = new DefaultTimeScaleRenderer();
    /** used to cache the time scale component for static use (tooltips). */
    protected JComponent _timeScaleRendererComponent;
    /** renderer used for rendering the grid/background. */
    protected transient GridRenderer _gridRenderer = new DefaultGridRenderer();
    /** used to cache the grid renderer component. */
    protected JComponent _gridRendererComponent;
    /** gap renderer. */
    protected TimeBarGapRenderer _gapRenderer = null;
    /** renderer for teh row headers. */
    protected HeaderRenderer _headerRenderer;
    /** renderer for the hierarchy section. */
    protected HierarchyRenderer _hierarchyRenderer;
    /** Renderer for various elements. */
    protected transient IMiscRenderer _miscRenderer = new DefaultMiscRenderer();
    /** Renderer used to render the title area. null indicates no renderer. */
    protected transient ITitleRenderer _titleRenderer = null;
    /** Relation render. */
    protected transient IRelationRenderer _relationRenderer = null;
    /** marker renderer. */
    protected transient IMarkerRenderer _markerRenderer = new DefaultMarkerRenderer();
    /** gloab assistant renderer. */
    protected transient IGlobalAssistantRenderer _globalAssistantRenderer;

    /** horizontal scrollbar if existing. */
    protected JScrollBar _xScrollBar;

    /** vertical scrollbar if existing. */
    protected JScrollBar _yScrollBar;

    /** the diagram pane itself. */
    public Diagram _diagram;

    /** mapping between interval classes and renderers. */
    protected Map<Class<? extends Interval>, TimeBarRenderer> _rendererMap = new HashMap<Class<? extends Interval>, TimeBarRenderer>();

    /** map of registered popup menus for intervals. */
    protected Map<Class<? extends Interval>, JPopupMenu> _registeredPopupMenues;

    /** context menu for the body of the viewer. */
    protected JPopupMenu _bodyContextMenu;
    /** context menu for the time scale. */
    protected JPopupMenu _timeScaleContextMenu;
    /** context menu for the header. */
    protected JPopupMenu _headerContextMenu;
    /** context menu for the hierarchy area. */
    protected JPopupMenu _hierarchyContextMenu;
    /** context menu for the title area. */
    protected JPopupMenu _titleContextMenu;

    /** flag indicating we are running on a mac. */
    protected boolean _macOS;

    /** panel the horizontal scrollbar is placed on. */
    protected JPanel _horizontalScrollPanel;
    /** panel the vertical scrollbar is placed on. */
    protected JPanel _verticalScrollPanel;

    /**
     * Constructs a timebar viewer.
     * 
     * @param model TimeBarModel to be used. The model may be <code>null</code>.
     * @param suppressXScroll if true the x scrollbar will not be displayed
     * @param suppressYScroll if true the y scrollbar will not be displayed
     */
    public TimeBarViewer(TimeBarModel model, boolean suppressXScroll, boolean suppressYScroll) {
        _delegate = new TimeBarViewerDelegate(this);
        // store delegate for access from outside (special cases)
        putClientProperty("delegate", _delegate);

        setLayout(new BorderLayout());
        // horizontal scrollbar
        _xScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        BoundedRangeModel brModelX = _xScrollBar.getModel();
        brModelX.addChangeListener(this);
        _xScrollBar.setUnitIncrement(1);
        if (!suppressXScroll) {
            _horizontalScrollPanel = new JPanel(new BorderLayout());
            add(_horizontalScrollPanel, BorderLayout.SOUTH);
            _horizontalScrollPanel.add(_xScrollBar, BorderLayout.CENTER);
        }
        // vertical scrollbar
        _yScrollBar = new JScrollBar(JScrollBar.VERTICAL);
        BoundedRangeModel brModelY = _yScrollBar.getModel();
        brModelY.addChangeListener(this);
        _yScrollBar.setUnitIncrement(1);
        if (!suppressYScroll) {
            _verticalScrollPanel = new JPanel(new BorderLayout());
            add(_verticalScrollPanel, BorderLayout.EAST);
            _verticalScrollPanel.add(_yScrollBar, BorderLayout.CENTER);
        }

        // diagram
        _diagram = new Diagram();
        _diagram._timeBarViewer = this; // parent reference
        add(_diagram, BorderLayout.CENTER);
        // resize listener
        this.addComponentListener(this);
        // set the model if given
        if (model != null) {
            _delegate.setModel(model);
        }
        // default header renderer
        setHeaderRenderer(new DefaultHeaderRenderer());

        // register the default renderer for intervals
        registerTimeBarRenderer(Interval.class, new DefaultTimeBarRenderer());

        // set the timescale renderer as the tick provider for the defaultgrid renderer
        // heavy knowledge of the default setup here ...
        _gridRenderer.setTickProvider((DefaultTimeScaleRenderer) _timeScaleRenderer);

        // check macos
        String osname = System.getProperty("os.name");
        if (osname != null) {
            _macOS = osname.startsWith("Mac");
        }

    }

    /**
     * Constructs a timebarviewer with both y and x scrollbars.
     * 
     * @param model timebarmodel to be displayed. The model may be <code>null</code>.
     */
    public TimeBarViewer(TimeBarModel model) {
        this(model, false, false);
    }

    /**
     * Constructs a timebarviewer without a model.
     * 
     */
    public TimeBarViewer() {
        this(null);
    }

    /**
     * Retrieve the x scroll bar.
     * 
     * @return the x scroll bar or <code>null</code> if the scroll bar has been suppressed
     */
    public JScrollBar getXScrollBar() {
        return _xScrollBar;
    }

    /**
     * Retrieve the y scroll bar.
     * 
     * @return the y scroll bar or <code>null</code> if the scroll bar has been suppressed
     */
    public JScrollBar getYScrollBar() {
        return _yScrollBar;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addMouseListener(MouseListener l) {
        // TODO remove, handling of the other possible listeners
        super.addMouseListener(l);
        _diagram.addMouseListener(l);
    }

    /**
     * {@inheritDoc}
     */
    public void setModel(TimeBarModel model) {
        _delegate.setModel(model);
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarModel getModel() {
        return _delegate.getModel();
    }

    /**
     * {@inheritDoc}
     */
    public void setRowFilter(TimeBarRowFilter rowFilter) {
        _delegate.setRowFilter(rowFilter);
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarRowFilter getRowFilter() {
        return _delegate.getRowFilter();
    }

    /**
     * {@inheritDoc}
     */
    public void setRowSorter(TimeBarRowSorter rowSorter) {
        _delegate.setRowSorter(rowSorter);
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarRowSorter getRowSorter() {
        return _delegate.getRowSorter();
    }

    /**
     * {@inheritDoc}
     */
    public void setIntervalFilter(TimeBarIntervalFilter intervalFilter) {
        _delegate.setIntervalFilter(intervalFilter);
    }

    /**
     * Set the default renderer to be used for rendering the timebars.
     * 
     * @param renderer the renderer to be used if no other registered renderer is appropriate
     */
    public void setTimeBarRenderer(TimeBarRenderer renderer) {
        registerTimeBarRenderer(Interval.class, renderer);
    }

    /**
     * Retrieve the default renderer currently used for rendering intervals (regsitered for Interval.class).
     * 
     * @return the renderer
     */
    public TimeBarRenderer getTimeBarRenderer() {
        return _rendererMap.get(Interval.class);
    }

    /**
     * Register a renderer for an interval class or interface. The renderer registered for Interval.class is the default
     * renderer if no other renderer can be found (obviously).
     * 
     * @param intervalClass class of the intervals
     * @param renderer renderer for the given class
     */
    public void registerTimeBarRenderer(Class<? extends Interval> intervalClass, TimeBarRenderer renderer) {
        _rendererMap.put(intervalClass, renderer);
        repaint();
    }

    /**
     * Retrieve the complete renderer map. This method's purpose is mainly to feed the TimeBarPrinter.
     * 
     * @return the renderer map
     */
    public Map<Class<? extends Interval>, TimeBarRenderer> getRendererMapping() {
        return _rendererMap;
    }

    /**
     * Retrieve a renderer for a given class. Checks all interfaces and all superclasses.
     * 
     * @param clazz class in question
     * @return renderer or null
     */
    protected TimeBarRenderer getRenderer(Class<? extends Interval> clazz) {
        TimeBarRenderer result = null;
        result = _rendererMap.get(clazz);
        if (result != null) {
            return result;
        }

        // direct interfaces
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> c : interfaces) {
            result = _rendererMap.get(c);
            if (result != null) {
                return result;
            }
        }

        // superclasses
        Class<?> sc = clazz.getSuperclass();

        while (sc != null) {
            result = _rendererMap.get(sc);
            if (result != null) {
                return result;
            }
            // interfaces of the superclass
            Class<?>[] scinterfaces = sc.getInterfaces();
            for (Class<?> c : scinterfaces) {
                result = _rendererMap.get(c);
                if (result != null) {
                    return result;
                }
            }
            sc = sc.getSuperclass();
        }

        return result;
    }

    /**
     * Set the renderer to be used for rendering the row headers.
     * 
     * @param renderer HeaderRenderer to be used or <code>null</code> indicating headers should not be rendered
     */
    public void setHeaderRenderer(HeaderRenderer renderer) {
        _headerRenderer = renderer;
        if (_headerRenderer != null) {
            _delegate.setYAxisWidth(renderer.getWidth());
        } else {
            _delegate.setYAxisWidth(0);
        }
        _diagram.safeRepaint();
    }

    /**
     * Set the renderer to be used for rendering the hierarchy area.
     * 
     * @param renderer HierarchyRenderer to be used or <code>null</code> indicating headers should not be rendered
     */
    public void setHierarchyRenderer(HierarchyRenderer renderer) {
        _hierarchyRenderer = renderer;
        if (_hierarchyRenderer != null) {
            _delegate.setHierarchyWidth(renderer.getWidth());
        } else {
            _delegate.setHierarchyWidth(0);
        }
        _diagram.safeRepaint();
    }

    /**
     * Sets the scale of the x axis as pixel per second, thus a value of 1000.0 / (24.0 * 60 * 60) will result in
     * displaying one day over 1000 pixel. The property is a bound property and can be listened to by a
     * PropertyChangeListener
     * 
     * @param pixelPerSecond pixel per second
     */
    public void setPixelPerSecond(double pixelPerSecond) {
        _delegate.setPixelPerSecond(pixelPerSecond);
    }

    /**
     * {@inheritDoc}
     */
    public double getPixelPerSecond() {
        return _delegate.getPixelPerSecond();
    }

    /**
     * {@inheritDoc}
     */
    public void setRowHeight(int rowHeight) {
        _delegate.setRowHeight(rowHeight);
    }

    /**
     * {@inheritDoc}
     */
    public int getRowHeight() {
        return _delegate.getTimeBarViewState().getDefaultRowHeight();
    }

    /**
     * {@inheritDoc}
     */
    public void updateXScrollBar(int max, int pos, int secondsDisplayed) {
        BoundedRangeModel brModel = _xScrollBar.getModel();
        if (brModel != null) {
            // remove changelistener to avoid cycling
            brModel.removeChangeListener(this);
            brModel.setMinimum(0);
            brModel.setMaximum(max);
            brModel.setExtent(secondsDisplayed);
            brModel.setValue(pos);
            brModel.addChangeListener(this);
            _xScrollBar.setBlockIncrement(secondsDisplayed / 10);
            _xScrollBar.setUnitIncrement(secondsDisplayed / 10);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateYScrollBar(int max, int pos, int rowsDisplayed) {
        BoundedRangeModel brModel = _yScrollBar.getModel();
        // remove changelistener to avoid cycling
        brModel.removeChangeListener(this);
        brModel.setMinimum(0);
        brModel.setMaximum(max);
        brModel.setExtent(rowsDisplayed);
        brModel.setValue(pos);
        brModel.addChangeListener(this);
        _yScrollBar.setBlockIncrement(rowsDisplayed / 10 + 1);
        _yScrollBar.setUnitIncrement(rowsDisplayed / 20 + 1);
    }

    /**
     * Invoked when one of the scrollbars was moved.
     * 
     * @param e event
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == _xScrollBar.getModel()) {
            BoundedRangeModel brModel = _xScrollBar.getModel();
            int value = brModel.getValue();
            _delegate.handleHorizontalScroll(value, true);
        } else if (e.getSource() == _yScrollBar.getModel()) {
            BoundedRangeModel brModel = _yScrollBar.getModel();
            int value = brModel.getValue();
            _delegate.handleVerticalScroll(value, true);
            // _diagram.safeRepaint();
        } else {
            throw new RuntimeException("Unknown sender");
        }
    }

    // *** ComponentListener for registering resizes
    /**
     * {@inheritDoc}
     */
    public void componentHidden(ComponentEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void componentMoved(ComponentEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void componentResized(ComponentEvent e) {
        // _delegate.updateScrollBars();
        _delegate.componentResized();
    }

    /**
     * {@inheritDoc}
     */
    public void componentShown(ComponentEvent e) {
    }

    // *** End of ComponentListener

    /**
     * Retrieve the JComponent for a given row and interval.
     * 
     * @param row row
     * @param interval interval
     * @return JComponent set up for rendering or other tasks (bounds are set)
     */
    protected JComponent getIntervalComponent(TimeBarRow row, Interval interval) {
        Rectangle intervalRect = _delegate.getIntervalBounds(row, interval);

        return getIntervalComponent(interval, intervalRect);
    }

    /**
     * Retrieve the JComponent for a given row and interval.
     * 
     * @param interval interval
     * @param intervalRect rectangle denoting the area of the interval
     * @return JComponent set up for rendering or other tasks (bounds are set)
     */
    protected JComponent getIntervalComponent(Interval interval, Rectangle intervalRect) {
        boolean overlapping = false;
        OverlapInfo oi = null;
        TimeBarRow row = _delegate.getModel().getRowForInterval(interval);
        if (!_delegate.getTimeBarViewState().getDrawOverlapping(row)) {
            oi = _delegate.getOverlapStrategy().getOverlapInfo(row, interval);
            if (oi != null && (oi.maxOverlapping > 0 || _delegate.getUseUniformHeight())) {
                overlapping = true;
            }
        }
        return getIntervalComponent(interval, intervalRect, overlapping);
    }

    /**
     * Retrieve the JComponent for an interval.
     * 
     * @param interval the interval
     * @param intervalRect rectangle marking the drawing bounds
     * @param overlapping if the interval is overlapping
     * @return the configured JComponent
     */
    protected JComponent getIntervalComponent(Interval interval, Rectangle intervalRect, boolean overlapping) {
        TimeBarRenderer renderer = getRenderer(interval.getClass());
        if (renderer == null) {
            throw new RuntimeException("no suitable renderer registered");
        }

        JComponent component = renderer.getTimeBarRendererComponent(this, interval, false, overlapping);
        component.setBounds(intervalRect);
        return component;
    }

    /**
     * The component drawing the viewer itself.
     * 
     * @author Peter Kliem
     * @version $Id: TimeBarViewer.java 906 2009-11-13 21:15:27Z kliem $
     */
    private class Diagram extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener {
        /** surrounding timebar viewer. */
        protected TimeBarViewer _timeBarViewer;

        /**
         * Default constructor.
         */
        public Diagram() {
            // setDoubleBuffered(false);
            // setOpaque(false);
            setBackground(Color.WHITE);
            setToolTipText(""); // set a tooltip since otherwise swing wont ask
            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);
        }

        /**
         * {@inheritDoc}
         */
        public String getToolTipText(MouseEvent event) {
            int x = event.getX();
            int y = event.getY();
            return _delegate.getToolTipText(x, y);
        }

        /**
         * Thread safe repaint (mybe the repaint method is thread safe by itself ... won't hurt)
         */
        public void safeRepaint() {
            Runnable doRepaint = new Runnable() {
                public void run() {
                    repaint();
                }
            };
            SwingUtilities.invokeLater(doRepaint);
        }

        /**
         * {@inheritDoc} Paint the component. The clipping bounds of the graphics are used to reduce painting to the
         * region that needs update.
         */
        public void paintComponent(Graphics g) {
            long time = System.currentTimeMillis();
            long nanoTime = System.nanoTime();

            _delegate.preparePaint(getWidth(), getHeight()); // prepare the geometry

            g.setColor(Color.WHITE);
            g.fillRect(g.getClipBounds().x, g.getClipBounds().y, g.getClipBounds().width, g.getClipBounds().height);
            g.setColor(Color.BLACK);

            // draw x axis if enabled
            if (_delegate.getTimeScalePosition() != TIMESCALE_POSITION_NONE) {
                drawXAxis(g);
            }
            drawGrid(g); // draw the grid if enabled

            // kick in the global assistant renderer
            if (_globalAssistantRenderer != null) {
                _globalAssistantRenderer.doRenderingBeforeIntervals(_delegate, g);
            }

            // draw rows (including gaps)
            drawRows(g);
            // draw markers
            drawMarkers(g);

            // draw selection rectangle
            Rectangle clipSave = g.getClipBounds();
            g.setClip(clipSave.intersection(_delegate.getDiagramRect()));
            drawSelectionRect(g);
            g.setClip(clipSave);

            // draw region rect
            clipSave = g.getClipBounds();
            g.setClip(clipSave.intersection(_delegate.getDiagramRect()));
            _miscRenderer.renderRegionRect(g, this._timeBarViewer, _delegate);
            g.setClip(clipSave);

            // draw the title if a title renderer has been set
            if (_titleRenderer != null) {
                JComponent titleComponent = _titleRenderer.getTitleRendererComponent(this._timeBarViewer);
                titleComponent.setBounds(_delegate.getTitleRect());
                titleComponent.paint(g);
            }

            // kick in the global assistant renderer
            if (_globalAssistantRenderer != null) {
                _globalAssistantRenderer.doRenderingLast(_delegate, g);
            }

            // primitive debug for monitoring paint time
            if (SHOWPAINTTIME) {
                System.out.println(_delegate.getName() + " : paintTime " + (System.currentTimeMillis() - time) + " ms "
                        + (System.nanoTime() - nanoTime) + " ns");
            }
        }

        /**
         * Draws the selection rectangle if present.
         * 
         * @param g graphics
         */
        private void drawSelectionRect(Graphics g) {
            if (_delegate.getSelectionRect() != null) {
                // normalize and remember
                _delegate.setLastSelRect(normalizeRectangle(_delegate.getSelectionRect()));
                Rectangle lastSelRect = _delegate.getLastSelRect();
                _miscRenderer.renderSelectionRect(g, this._timeBarViewer, lastSelRect);
            }

        }

        /**
         * Normalize a rectangle.
         * 
         * @param rect rectangle to normalize
         * @return normalized (pos width nd heigth) rectangle
         */
        private Rectangle normalizeRectangle(Rectangle rect) {
            int x = Math.min(rect.x, rect.x + rect.width);
            int y = Math.min(rect.y, rect.y + rect.height);
            int width = Math.abs(rect.width);
            int height = Math.abs(rect.height);
            return new Rectangle(x, y, width, height);
        }

        /**
         * Draws all markers for the diagram. If a marker is not currently displayed it will not be painted.
         * 
         * @param g Graphics to use
         */
        private void drawMarkers(Graphics g) {
            if (_delegate.getMarkers() != null) {
                for (TimeBarMarker marker : _delegate.getMarkers()) {
                    if (_delegate.isDisplayed(marker.getDate())) {
                        drawMarker(g, marker);
                    }
                }
            }
        }

        /**
         * Draw a single marker.
         * 
         * @param g Graphics to be used
         * @param marker marker to draw
         */
        private void drawMarker(Graphics g, TimeBarMarker marker) {
            if (_markerRenderer != null) {
                int x = _delegate.xForDate(marker.getDate());
                boolean dragged = _delegate.getDraggedMarker() == marker;
                _markerRenderer.renderMarker(_delegate, g, marker, x, dragged);
            }
        }

        /**
         * Draws the x axis using the time scale renderer.
         * 
         * @param g Graphics context to use
         */
        private void drawXAxis(Graphics g) {
            if (_timeScaleRenderer != null) {
                _timeScaleRendererComponent = _timeScaleRenderer.getRendererComponent(_timeBarViewer, _delegate

                .getTimeScalePosition() == TimeBarViewerInterface.TIMESCALE_POSITION_TOP);
                _timeScaleRendererComponent.setBounds(_delegate.getXAxisRect());
                Graphics gg = g.create(_delegate.getXAxisRect().x, _delegate.getXAxisRect().y,
                        _delegate.getXAxisRect().width, _delegate.getXAxisRect().height);
                _timeScaleRendererComponent.paint(gg);
                gg.dispose();
            }
        }

        /**
         * Draws the grid/background using the gridRenderer.
         * 
         * @param g graphics
         */
        private void drawGrid(Graphics g) {
            if (_gridRenderer != null) {
                _gridRendererComponent = _gridRenderer.getRendererComponent(_timeBarViewer);
                _gridRendererComponent.setBounds(_delegate.getDiagramRect());
                Graphics gg = g.create(_delegate.getDiagramRect().x, _delegate.getDiagramRect().y, _delegate
                        .getDiagramRect().width, _delegate.getDiagramRect().height);
                _gridRendererComponent.paint(gg);
                gg.dispose();
            }
        }

        /**
         * Draw all rows. Rows outside the clipping bounds of the graphics context are not painted.
         * 
         * @param g Graphics to use
         */
        private void drawRows(Graphics g) {
            if (_delegate.getOrientation() == Orientation.HORIZONTAL) {
                drawRowsHorizontal(g);
            } else {
                drawRowsVertical(g);
            }
            // relation rendering
            if (_relationRenderer != null) {
                _relationRenderer.renderRelations(_delegate, g);
            }
        }

        /**
         * Draw rows for horizontal orientation.
         * 
         * @param g Graphics to use
         */
        private void drawRowsHorizontal(Graphics g) {
            g.setColor(Color.BLACK);

            // set the clipping to include only the heigth of the diagram rect
            Rectangle clipSave = g.getClipBounds();
            g.setClip(0, _delegate.getDiagramRect().y, getWidth(), _delegate.getDiagramRect().height);

            // separating line to the header
            // MAYBE color configurable
            g.drawLine(_delegate.getDiagramRect().x - 1, 0, _delegate.getDiagramRect().x - 1, getHeight());
            g.drawLine(_delegate.getHierarchyRect().x + _delegate.getHierarchyWidth() - 1, 0, _delegate
                    .getHierarchyRect().x
                    + _delegate.getHierarchyWidth() - 1, getHeight());

            int upperYBound = _delegate.getDiagramRect().y;
            int lowerYBound = upperYBound + _delegate.getDiagramRect().height;
            if (g.getClipBounds() != null) {
                upperYBound = g.getClipBounds().y;
                lowerYBound = upperYBound + g.getClipBounds().height;
            }
            for (int r = _delegate.getFirstRow(); r <= _delegate.getFirstRow() + _delegate.getRowsDisplayed()
                    && r < _delegate.getRowCount(); r++) {
                TimeBarRow row = _delegate.getRow(r);
                int rowHeight = _delegate.getTimeBarViewState().getRowHeight(row);
                // int y = (r - _delegate.getFirstRow()) * rowHeight + _delegate.getDiagramRect().y
                // - _delegate.getFirstRowOffset();
                int y = _delegate.yForRow(row);
                if (y == -1) {
                    // no coord -> is not displayed
                    break;
                }

                // row is drawn if either the beginning or the end is inside the
                // clipping rect
                // or if the upperBound is inside the row rect (clipping rect is
                // inside the row rect
                if ((y >= upperYBound && y <= lowerYBound)
                        || (y + rowHeight >= upperYBound && y + rowHeight <= lowerYBound)
                        || (upperYBound > y && upperYBound < y + rowHeight)) {
                    drawRow(g, y, rowHeight, _delegate.getRow(r), _delegate.getSelectionModel().isSelected(
                            _delegate.getRow(r)));
                    // draw gaps if a renderer is set
                    if (_gapRenderer != null) {
                        drawRowGaps(g, y, rowHeight, _delegate.getRow(r), _delegate.getSelectionModel().isSelected(
                                _delegate.getRow(r)));
                    }
                }
            }
            g.setClip(clipSave);
        }

        /**
         * Draw rows for vertical orientation.
         * 
         * @param g Graphics to use
         */
        private void drawRowsVertical(Graphics g) {
            g.setColor(Color.BLACK);

            // set the clipping to include only the heigth of the diagram rect
            Rectangle clipSave = g.getClipBounds();
            g.setClip(0, _delegate.getDiagramRect().y, getWidth(), _delegate.getDiagramRect().height);

            // separating line to the header
            // MAYBE coloro configurable
            g.drawLine(0, _delegate.getDiagramRect().y - 1, getWidth(), _delegate.getDiagramRect().y - 1);
            g.drawLine(0, _delegate.getHierarchyRect().y + _delegate.getHierarchyWidth() - 1, getWidth(), _delegate
                    .getHierarchyRect().y
                    + _delegate.getHierarchyWidth() - 1);

            int leftXBound = _delegate.getDiagramRect().x;
            int rightXBound = leftXBound + _delegate.getDiagramRect().width;
            if (g.getClipBounds() != null) {
                leftXBound = g.getClipBounds().x;
                rightXBound = leftXBound + g.getClipBounds().width;
            }

            for (int r = _delegate.getFirstRow(); r <= _delegate.getFirstRow() + _delegate.getRowsDisplayed()
                    && r < _delegate.getRowCount(); r++) {
                TimeBarRow row = _delegate.getRow(r);
                int rowWidth = _delegate.getTimeBarViewState().getRowHeight(row);
                // int x = (r - _delegate.getFirstRow()) * rowWidth + _delegate.getDiagramRect().x
                // - _delegate.getFirstRowOffset();

                int x = _delegate.yForRow(row);
                if (x == -1) {
                    // no coord -> is not displayed
                    break;
                }

                // row is drawn if either the beginning or the end is inside the
                // clipping rect
                // or if the upperBound is inside the row rect (clipping rect is
                // inside the row rect
                if ((x >= leftXBound && x <= rightXBound)
                        || (x + rowWidth >= leftXBound && x + rowWidth <= rightXBound)
                        || (leftXBound > x && leftXBound < x + rowWidth)) {
                    drawRowVertical(g, x, rowWidth, _delegate.getRow(r), _delegate.getSelectionModel().isSelected(
                            _delegate.getRow(r)));
                    // draw gaps if a renderer is set
                    if (_gapRenderer != null) {
                        drawRowGapsVertical(g, x, rowWidth, _delegate.getRow(r), _delegate.getSelectionModel()
                                .isSelected(_delegate.getRow(r)));
                    }
                }
            }
            g.setClip(clipSave);
        }

        /**
         * Draw a single row (horizontal).
         * 
         * @param g graphics
         * @param y starting pos y
         * @param height height
         * @param row row to draw
         * @param selected true if the row is selected
         */
        private void drawRow(Graphics g, int y, int height, TimeBarRow row, boolean selected) {
            // draw the row header
            drawRowHeader(g, y, height, row.getRowHeader(), selected);
            // draw the hierarchy area
            drawHierarchy(g, y, height, row, selected);
            // draw a line at the bottom of the row if enabled
            Rectangle diagramRect = _delegate.getDiagramRect();
            if (_delegate.getDrawRowGrid()) {
                _miscRenderer.drawRowGridLine(g, diagramRect.x, y + height - 1, diagramRect.x + diagramRect.width, y
                        + height - 1);
            }

            // draw a background if selected or highlighted
            // highlighting is at higher priority than selection
            if (selected || _delegate.getHighlightedRow() == row) {
                boolean highlighted = _delegate.getHighlightedRow() == row;
                int markerHeight = height;
                // calculate height for clipping
                if (y + markerHeight > diagramRect.y + diagramRect.height) {
                    markerHeight = markerHeight - (y + markerHeight - (diagramRect.y + diagramRect.height));
                }
                _miscRenderer.drawRowBackground(g, diagramRect.x, y, getWidth() - _delegate.getYAxisWidth()
                        - _delegate.getHierarchyWidth(), markerHeight, selected, highlighted);

            }
            // use the clip bounds (if given) to shorten the region to be
            // painted
            JaretDate start = _delegate.getStartDate();
            JaretDate end = _delegate.getEndDate();
            if (g.getClipBounds() != null) {
                start = _delegate.dateForCoord(g.getClipBounds().x);
                end = _delegate.dateForCoord(g.getClipBounds().x + g.getClipBounds().width);
            }
            // List intervals = row.getIntervals(_startDate, _endDate);
            List<Interval> intervals = row.getIntervals(start, end);
            for (Interval i : intervals) {
                // apply filter on intervals if set
                if (_delegate.getIntervalFilter() == null || _delegate.getIntervalFilter().isInResult(i)) {
                    if (_delegate.getTimeBarViewState().getDrawOverlapping(row)) {
                        drawInterval(g, y, height, i, _delegate.getSelectionModel().isSelected(i), null, row);
                    } else {
                        drawInterval(g, y, height, i, _delegate.getSelectionModel().isSelected(i), _delegate
                                .getOverlapStrategy().getOverlapInfo(row, i), row);
                    }
                }
            }
        }

        /**
         * Draw a single row (vertical).
         * 
         * @param g graphics
         * @param x starting pos x
         * @param width width
         * @param row row to draw
         * @param selected true if the row is selected
         */
        private void drawRowVertical(Graphics g, int x, int width, TimeBarRow row, boolean selected) {
            // draw the row header
            drawRowHeaderVertical(g, x, width, row.getRowHeader(), selected);
            // draw the hierarchy area
            drawHierarchyVertical(g, x, width, row, selected);
            // draw a line at the bottom of the row if enabled
            Rectangle diagramRect = _delegate.getDiagramRect();
            if (_delegate.getDrawRowGrid()) {
                _miscRenderer.drawRowGridLine(g, x + width - 1, diagramRect.y, x + width - 1, diagramRect.y
                        + diagramRect.height);
            }

            // draw a background if selected or highlighted
            // highlighting is at higher priority than selection
            if (selected || _delegate.getHighlightedRow() == row) {
                boolean highlighted = _delegate.getHighlightedRow() == row;
                int markerWidth = width;
                // calculate width for clipping
                if (x + markerWidth > diagramRect.x + diagramRect.width) {
                    markerWidth = markerWidth - (x + markerWidth - (diagramRect.x + diagramRect.width));
                }
                _miscRenderer.drawRowBackground(g, x, diagramRect.y, markerWidth, getHeight()
                        - _delegate.getYAxisWidth() - _delegate.getHierarchyWidth(), selected, highlighted);
            }
            // use the clip bounds (if given) to shorten the region to be
            // painted
            JaretDate start = _delegate.getStartDate();
            JaretDate end = _delegate.getEndDate();
            if (g.getClipBounds() != null) {
                start = _delegate.dateForCoord(g.getClipBounds().y);
                end = _delegate.dateForCoord(g.getClipBounds().y + g.getClipBounds().height);
            }
            List<Interval> intervals = row.getIntervals(start, end);
            for (Interval i : intervals) {
                // apply filter on intervals if set
                if (_delegate.getIntervalFilter() == null || _delegate.getIntervalFilter().isInResult(i)) {
                    if (_delegate.getTimeBarViewState().getDrawOverlapping(row)) {
                        drawIntervalVertical(g, x, width, i, _delegate.getSelectionModel().isSelected(i), null, row);
                    } else {
                        drawIntervalVertical(g, x, width, i, _delegate.getSelectionModel().isSelected(i), _delegate
                                .getOverlapStrategy().getOverlapInfo(row, i), row);
                    }
                }
            }
        }

        /**
         * Draws the gaps beetwenn intervals (horizontal). The selection of the bordering interval sis done inside this
         * method. TODO maybe move the selection routine out TODO call for edges i.e. one interval is null
         * 
         * @param g Graphics for painting
         * @param y y coordinate
         * @param height rowHeight
         * @param row TimeBarRow to be painted
         * @param selected selection status of the row (not used)
         */
        private void drawRowGaps(Graphics g, int y, int height, TimeBarRow row, boolean selected) {
            // use the clip bounds (if given) to shorten the region to be
            // painted
            JaretDate start = _delegate.getStartDate();
            JaretDate end = _delegate.getEndDate();
            if (g.getClipBounds() != null) {
                start = _delegate.dateForCoord(g.getClipBounds().x);
                end = _delegate.dateForCoord(g.getClipBounds().x + g.getClipBounds().width);
            }
            // for the gaps we need a minimum of two intervals
            // those has to be in the displayed intervals, so we apply the
            // filter first (if set) and do the selection
            // ourself. The alogorithm is highly dependet on the ordering of the
            // list! This should be guaranteed by the model
            List<Interval> intervals = new ArrayList<Interval>();
            Interval firstInterval = null;
            for (Interval interval : row.getIntervals()) {
                if (_delegate.getIntervalFilter() == null || _delegate.getIntervalFilter().isInResult(interval)) {
                    if (interval.getEnd().compareTo(start) < 0) {
                        // before the starting date: remember the nearest
                        // interval
                        if (firstInterval == null
                                || start.diffSeconds(interval.getEnd()) < start.diffSeconds(firstInterval.getEnd())) {
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
                            firstInterval = null; // we don't need the
                            // reference
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
                            break; // found an interval beginning behind the
                            // end
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
                    drawGap(g, row, y, height, lastInterval, i);
                }
                // remember last interval for next turn
                lastInterval = i;
            }
        }

        /**
         * Draws the gaps beetwenn intervals (horizontal). The selection of the bordering interval sis done inside this
         * method. TODO maybe move the selection routine out TODO call for edges i.e. one interval is null
         * 
         * @param g Graphics for painting
         * @param x x coordinate
         * @param width rowHeight
         * @param row TimeBarRow to be painted
         * @param selected selection status of the row (not used)
         */
        private void drawRowGapsVertical(Graphics g, int x, int width, TimeBarRow row, boolean selected) {
            // use the clip bounds (if given) to shorten the region to be
            // painted
            JaretDate start = _delegate.getStartDate();
            JaretDate end = _delegate.getEndDate();
            if (g.getClipBounds() != null) {
                start = _delegate.dateForCoord(g.getClipBounds().y);
                end = _delegate.dateForCoord(g.getClipBounds().y + g.getClipBounds().height);
            }
            // for the gaps we need a minimum of two intervals
            // those has to be in the displayed intervals, so we apply the
            // filter first (if set) and do the selection
            // ourself. The alogorithm is highly dependet on the ordering of the
            // list! This should be guaranteed by the model
            List<Interval> intervals = new ArrayList<Interval>();
            Interval firstInterval = null;
            for (Interval interval : row.getIntervals()) {
                if (_delegate.getIntervalFilter() == null || _delegate.getIntervalFilter().isInResult(interval)) {
                    if (interval.getEnd().compareTo(start) < 0) {
                        // before the starting date: remember the nearest
                        // interval
                        if (firstInterval == null
                                || start.diffSeconds(interval.getEnd()) < start.diffSeconds(firstInterval.getEnd())) {
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
                            firstInterval = null; // we don't need the
                            // reference
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
                            break; // found an interval beginning behind the
                            // end
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
                    drawGapVertical(g, row, x, width, lastInterval, i);
                }
                // remember last interval for next turn
                lastInterval = i;
            }
        }

        /**
         * Draw a single Interval (horizontal).
         * 
         * @param g Graphics to paint with
         * @param y starting y
         * @param height height for painting
         * @param i interval to be painted
         * @param selected true if the interval should be drawn selecetd
         * @param oiInfo overlap information or <code>null</code> if intervals are drawn overlapping
         * @param row row of the interval
         */
        private void drawInterval(Graphics g, int y, int height, Interval i, boolean selected, OverlapInfo oiInfo,
                TimeBarRow row) {
            TimeBarRenderer renderer = getRenderer(i.getClass());
            if (renderer == null) {
                throw new RuntimeException("no suitable renderer registered for " + i.getClass().getName());
            }

            boolean overlapping = oiInfo != null && oiInfo.maxOverlapping > 0;

            if (oiInfo != null) {
                // correct the bounds when overlapping
                if (!_delegate.getUseUniformHeight()) {
                    height = _delegate.getTimeBarViewState().getRowHeight(row) / (oiInfo.maxOverlapping + 1);
                } else {
                    height = _delegate.getTimeBarViewState().getRowHeight(row)
                            / (_delegate.getOverlapStrategy().getMaxOverlapCount(row));
                }
                y = y + oiInfo.pos * height;
            }

            Component component = renderer.getTimeBarRendererComponent(_timeBarViewer, i, selected, overlapping);
            int x = _delegate.xForDate(i.getBegin());
            int width = _delegate.xForDate(i.getEnd()) - x;

            // check preferred drawing bounds
            Rectangle intervalDrawingArea = new Rectangle(x, y, width, height);
            Rectangle drawingArea = renderer.getPreferredDrawingBounds(intervalDrawingArea, _delegate, i, selected,
                    overlapping);
            x = drawingArea.x;
            width = drawingArea.width;
            y = drawingArea.y;
            height = drawingArea.height;

            component.setBounds(x, y, width, height);
            Graphics gg = g.create(x, y, width, height);
            // calculate height for clipping
            Rectangle diagramRect = _delegate.getDiagramRect();
            if (y + height > diagramRect.y + diagramRect.height) {
                height = height - (y + height - (diagramRect.y + diagramRect.height));
            }
            int upperClipBound = 0;
            if (y < diagramRect.y) {
                upperClipBound = diagramRect.y - y;
            }

            // calculate width for clipping
            if (x + width > diagramRect.x + diagramRect.width) {
                width = width - (x + width - (diagramRect.x + diagramRect.width));
            }
            // calc x clipping and set clipping rect
            gg.setClip(x < diagramRect.x ? diagramRect.x - x : 0, upperClipBound, width, height);

            component.paint(gg);
            gg.dispose();
        }

        /**
         * Draw a single Interval (vertical).
         * 
         * @param g Graphics to paint with
         * @param x starting x
         * @param width width for painting
         * @param i interval to be painted
         * @param selected true if the interval should be drawn selecetd
         * @param oiInfo overlap information or <code>null</code> if intervals are drawn overlapping
         * @param row row of the interval
         */
        private void drawIntervalVertical(Graphics g, int x, int width, Interval i, boolean selected,
                OverlapInfo oiInfo, TimeBarRow row) {
            TimeBarRenderer renderer = getRenderer(i.getClass());
            if (renderer == null) {
                throw new RuntimeException("no suitable renderer registered");
            }

            boolean overlapping = oiInfo != null && oiInfo.maxOverlapping > 0;

            if (oiInfo != null) {
                // correct the bounds when overlapping
                if (!_delegate.getUseUniformHeight()) {
                    width = _delegate.getTimeBarViewState().getRowHeight(row) / (oiInfo.maxOverlapping + 1);
                } else {
                    width = _delegate.getTimeBarViewState().getRowHeight(row)
                            / (_delegate.getOverlapStrategy().getMaxOverlapCount(row));
                }
                x = x + oiInfo.pos * width;
            }

            Component component = renderer.getTimeBarRendererComponent(_timeBarViewer, i, selected, overlapping);
            int y = _delegate.xForDate(i.getBegin());
            int height = _delegate.xForDate(i.getEnd()) - y;

            // check preferred drawing bounds
            Rectangle intervalDrawingArea = new Rectangle(x, y, width, height);
            Rectangle drawingArea = renderer.getPreferredDrawingBounds(intervalDrawingArea, _delegate, i, selected,
                    overlapping);
            x = drawingArea.x;
            width = drawingArea.width;
            y = drawingArea.y;
            height = drawingArea.height;

            component.setBounds(x, y, width, height);
            Graphics gg = g.create(x, y, width, height);
            // calculate height for clipping
            Rectangle diagramRect = _delegate.getDiagramRect();
            if (x + width > diagramRect.x + diagramRect.width) {
                width = width - (x + width - (diagramRect.x + diagramRect.width));
            }
            int upperClipBound = 0;
            if (x < diagramRect.x) {
                upperClipBound = diagramRect.x - x;
            }

            // calculate height for clipping
            if (y + height > diagramRect.y + diagramRect.height) {
                height = height - (y + height - (diagramRect.y + diagramRect.height));
            }
            // calc x clipping and set clipping rect
            gg.setClip(upperClipBound, y < diagramRect.y ? diagramRect.y - y : 0, width, height);
            component.paint(gg);
            gg.dispose();
        }

        /**
         * Draw the gap beetween to intervals using a GapRenderer (horizontal).
         * 
         * @param g Graphics for painting
         * @param row TimeBarRow the gap "belongs to"
         * @param y starting ccordinate y
         * @param height height for painting
         * @param i1 earlier interval
         * @param i2 later interval
         */
        private void drawGap(Graphics g, TimeBarRow row, int y, int height, Interval i1, Interval i2) {
            Component component = _gapRenderer.getTimeBarGapRendererComponent(_timeBarViewer, row, i1, i2);
            int x = _delegate.xForDate(i1.getEnd());
            int width = _delegate.xForDate(i2.getBegin()) - x;
            // handle minimum width requirements of the gap renderer
            if (_gapRenderer.getMinimumWidth() > 0) {
                if (width < _gapRenderer.getMinimumWidth()) {
                    int diff = _gapRenderer.getMinimumWidth() - width;
                    width += diff;
                    x -= diff / 2;
                }
            }
            // TODO clipping width
            component.setBounds(x, y, width, height);
            Graphics gg = g.create(x, y, width, height);
            // calculate height for clipping
            Rectangle diagramRect = _delegate.getDiagramRect();
            if (y + height > diagramRect.y + diagramRect.height) {
                height = height - (y + height - (diagramRect.y + diagramRect.height));
            }
            int upperClipBound = 0;
            if (y < diagramRect.y) {
                upperClipBound = diagramRect.y - y;
            }

            // calc x clipping and set clipping rect
            gg.setClip(x < diagramRect.x ? diagramRect.x - x : 0, upperClipBound, width, height);
            component.paint(gg);
            gg.dispose();
        }

        /**
         * Draw the gap beetween to intervals using a GapRenderer (vertical).
         * 
         * @param g Graphics for painting
         * @param row TimeBarRow the gap "belongs to"
         * @param x starting cordinate x
         * @param width width for painting
         * @param i1 earlier interval
         * @param i2 later interval
         */
        private void drawGapVertical(Graphics g, TimeBarRow row, int x, int width, Interval i1, Interval i2) {
            Component component = _gapRenderer.getTimeBarGapRendererComponent(_timeBarViewer, row, i1, i2);
            int y = _delegate.xForDate(i1.getEnd());
            int height = _delegate.xForDate(i2.getBegin()) - y;
            // handle minimum width requirements of the gap renderer
            if (_gapRenderer.getMinimumWidth() > 0) {
                if (height < _gapRenderer.getMinimumWidth()) {
                    int diff = _gapRenderer.getMinimumWidth() - height;
                    height += diff;
                    y -= diff / 2;
                }
            }
            // TODO clipping width
            component.setBounds(x, y, width, height);
            Graphics gg = g.create(x, y, width, height);
            // calculate height for clipping
            Rectangle diagramRect = _delegate.getDiagramRect();
            if (x + width > diagramRect.x + diagramRect.width) {
                width = width - (x + width - (diagramRect.x + diagramRect.width));
            }
            int upperClipBound = 0;
            if (x < diagramRect.x) {
                upperClipBound = diagramRect.x - x;
            }

            // calc x clipping and set clipping rect
            gg.setClip(upperClipBound, y < diagramRect.y ? diagramRect.y - y : 0, width, height);
            component.paint(gg);
            gg.dispose();
        }

        /**
         * Draw a row header (horizontal). If the header claims a smaller preferred size than the rowheight it is
         * vertically centered.
         * 
         * @param g Graphics
         * @param y upper bound
         * @param height row height
         * @param header the header object
         * @param selected true if the row is selected
         */
        private void drawRowHeader(Graphics g, int y, int height, TimeBarRowHeader header, boolean selected) {
            // draw only if a header renderer is set and the width is >0
            if (_headerRenderer != null && _delegate.getYAxisWidth() > 0) {
                JComponent component = _headerRenderer.getHeaderRendererComponent(_timeBarViewer, header, selected);
                int x = _delegate.getYAxisRect().x;
                int width = _delegate.getYAxisWidth() - 1;
                Rectangle diagramRect = _delegate.getDiagramRect();
                Graphics gg;
                int prefHeight = component.getPreferredSize().height;
                if (prefHeight < height) {
                    int hCor = (height - prefHeight) / 2;
                    gg = g.create(x, y + hCor, width, prefHeight);
                    component.setBounds(x, y + hCor, width, prefHeight);
                    int upperClipBound = 0;
                    if (y + hCor < diagramRect.y) {
                        upperClipBound = diagramRect.y - y - hCor;
                    }
                    if (y + hCor + height > diagramRect.y + diagramRect.height) {
                        prefHeight = prefHeight - (y + hCor + prefHeight - (diagramRect.y + diagramRect.height));
                    }
                    // calc x clipping and set clipping rect
                    gg.setClip(0, upperClipBound, width, prefHeight);
                } else {
                    gg = g.create(x, y, width, height);
                    component.setBounds(x, y, width, height);
                    int upperClipBound = 0;
                    if (y < diagramRect.y) {
                        upperClipBound = diagramRect.y - y;
                    }

                    if (y + height > diagramRect.y + diagramRect.height) {
                        height = height - (y + height - (diagramRect.y + diagramRect.height));
                    }
                    // calc x clipping and set clipping rect
                    gg.setClip(x, upperClipBound, width, height);
                }
                component.paint(gg);
                gg.dispose();
            }
        }

        /**
         * Draw a row header (vertical). If the hedare claims a smaller preferred size than the rowheight it is
         * vertically centered.
         * 
         * @param g Graphics
         * @param x left bound
         * @param width width
         * @param header the header object
         * @param selected true if the row is selected
         */
        private void drawRowHeaderVertical(Graphics g, int x, int width, TimeBarRowHeader header, boolean selected) {
            // draw only if a header renderer is set and the width is >0
            if (_headerRenderer != null && _delegate.getYAxisWidth() > 0) {
                JComponent component = _headerRenderer.getHeaderRendererComponent(_timeBarViewer, header, selected);
                int y = _delegate.getYAxisRect().y;
                int height = _delegate.getYAxisWidth() - 1;
                Rectangle diagramRect = _delegate.getDiagramRect();
                Graphics gg;
                int prefWidth = component.getPreferredSize().width;
                if (prefWidth < width) {
                    int wCor = (width - prefWidth) / 2;
                    gg = g.create(x + wCor, y, prefWidth, height);
                    component.setBounds(x + wCor, y, prefWidth, height);
                    int upperClipBound = 0;
                    if (x + wCor < diagramRect.x) {
                        upperClipBound = diagramRect.x - x - wCor;
                    }
                    if (x + wCor + width > diagramRect.x + diagramRect.width) {
                        prefWidth = prefWidth - (x + wCor + prefWidth - (diagramRect.x + diagramRect.width));
                    }
                    // calc y clipping and set clipping rect
                    gg.setClip(upperClipBound, 0, prefWidth, height);
                } else {
                    gg = g.create(x, y, width, height);
                    component.setBounds(x, y, width, height);
                    int upperClipBound = 0;
                    if (x < diagramRect.x) {
                        upperClipBound = diagramRect.x - x;
                    }

                    if (x + width > diagramRect.x + diagramRect.width) {
                        width = width - (x + width - (diagramRect.x + diagramRect.width));
                    }
                    // calc y clipping and set clipping rect
                    gg.setClip(upperClipBound, 0, width, height);
                }
                component.paint(gg);
                gg.dispose();
            }
        }

        /**
         * Draw the hierachy section for a row/node (horizontal).
         * 
         * @param g graphics
         * @param y begin y
         * @param height height of a row
         * @param row the row
         * @param selected true if the row is selected
         */
        private void drawHierarchy(Graphics g, int y, int height, TimeBarRow row, boolean selected) {
            // draw only if a hierarchy renderer is set and the width is >0
            if (_hierarchyRenderer != null && _delegate.getHierarchyWidth() > 0) {
                int level = 0;
                int depth = 0;
                boolean expanded = false;
                boolean leaf = true;
                if (row instanceof TimeBarNode) {
                    TimeBarNode node = (TimeBarNode) row;
                    if (_delegate.getHierarchicalViewState().isExpanded(node)) {
                        expanded = true;
                    }
                    leaf = node.getChildren().size() == 0;
                    level = node.getLevel();
                    depth = _delegate.getHierarchicalModel().getDepth();
                }

                int x = _delegate.getHierarchyRect().x;
                int width = _delegate.getHierarchyWidth() - 1;

                JComponent component = _hierarchyRenderer.getHierarchyRendererComponent(_timeBarViewer, row, selected,
                        expanded, leaf, level, depth);
                Rectangle diagramRect = _delegate.getDiagramRect();
                Graphics gg;

                gg = g.create(x, y, width, height);
                component.setBounds(x, y, width, height);
                if (y + height > diagramRect.y + diagramRect.height) {
                    height = height - (y + height - (diagramRect.y + diagramRect.height));
                }
                int upperClipBound = 0;
                if (y < diagramRect.y) {
                    upperClipBound = diagramRect.y - y;
                }
                // calc x clipping and set clipping rect
                gg.setClip(x, upperClipBound, width, height);

                component.paint(gg);
                gg.dispose();
            }

        }

        /**
         * Draw the hierachy section for a row/node (vertical).
         * 
         * @param g graphics
         * @param x begin x
         * @param width width of a col
         * @param row the row
         * @param selected true if the row is selected
         */
        private void drawHierarchyVertical(Graphics g, int x, int width, TimeBarRow row, boolean selected) {
            // draw only if a hierarchy renderer is set and the width is >0
            if (_hierarchyRenderer != null && _delegate.getHierarchyWidth() > 0) {
                int level = 0;
                int depth = 0;
                boolean expanded = false;
                boolean leaf = true;
                if (row instanceof TimeBarNode) {
                    TimeBarNode node = (TimeBarNode) row;
                    if (_delegate.getHierarchicalViewState().isExpanded(node)) {
                        expanded = true;
                    }
                    leaf = node.getChildren().size() == 0;
                    level = node.getLevel();
                    depth = _delegate.getHierarchicalModel().getDepth();
                }

                int y = _delegate.getHierarchyRect().y;
                int height = _delegate.getHierarchyWidth() - 1;

                JComponent component = _hierarchyRenderer.getHierarchyRendererComponent(_timeBarViewer, row, selected,
                        expanded, leaf, level, depth);
                Rectangle diagramRect = _delegate.getDiagramRect();
                Graphics gg;

                gg = g.create(x, y, width, height);
                component.setBounds(x, y, width, height);

                if (x + width > diagramRect.x + diagramRect.width) {
                    width = width - (x + width - (diagramRect.x + diagramRect.width));
                }
                int upperClipBound = 0;
                if (x < diagramRect.x) {
                    upperClipBound = diagramRect.x - x;
                }
                // calc y clipping and set clipping rect
                gg.setClip(upperClipBound, y, width, height);

                component.paint(gg);
                gg.dispose();
            }

        }

        /**
         * {@inheritDoc}
         */
        public Dimension getPreferredSize() {
            return new Dimension(PREFWIDTH, PREFHEIGHT);
        }

        // *** MouseListener
        /**
         * {@inheritDoc}
         */
        public void mouseEntered(MouseEvent e) {
        }

        /**
         * {@inheritDoc}
         */
        public void mouseExited(MouseEvent e) {
        }

        /**
         * {@inheritDoc}
         */
        public void mousePressed(MouseEvent e) {
            _delegate.mousePressed(e.getX(), e.getY(), e.isPopupTrigger(), e.getModifiersEx());
        }

        /**
         * {@inheritDoc}
         */
        public void mouseClicked(MouseEvent e) {
        }

        /**
         * {@inheritDoc}
         */
        public void mouseReleased(MouseEvent e) {
            boolean popupTrigger = e.isPopupTrigger();
            if (!popupTrigger && _macOS) {
                popupTrigger = e.getButton() == MouseEvent.BUTTON3;
            }

            _delegate.mouseReleased(e.getX(), e.getY(), popupTrigger, e.getModifiersEx());
        }

        // *** End of MouseListener

        // *** MouseMotionListener
        /**
         * {@inheritDoc}
         */
        public void mouseDragged(MouseEvent e) {
            _delegate.mouseDragged(e.getX(), e.getY(), e.getModifiersEx());
        }

        /**
         * {@inheritDoc}
         */
        public void mouseMoved(MouseEvent e) {
            _delegate.mouseMoved(e.getX(), e.getY());
        }

        // *** End of MouseMotionListener
        // *** MouseWheelListener
        /**
         * {@inheritDoc}
         */
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                int val = e.getUnitsToScroll();
                if (_delegate.getXAxisRect() != null && e.getY() > _delegate.getXAxisRect().y
                        && e.getY() < _delegate.getXAxisRect().y + _delegate.getXAxisRect().height) {
                    // on the xaxis
                    _xScrollBar.setValue(_xScrollBar.getValue() + val * _xScrollBar.getModel().getExtent() / 5);
                } else {
                    // y axis
                    _yScrollBar.getModel().setValue(_yScrollBar.getModel().getValue() + val);
                }
            }
        }
        // *** End of MouseWheelListener
    }

    /**
     * @return Returns the timeScaleRenderer.
     */
    public TimeScaleRenderer getTimeScaleRenderer() {
        return _timeScaleRenderer;
    }

    /**
     * Set a renderer for the x axis. The Height for the x axis will be set to the preferred height of the renderer if
     * the renderer supplies one.
     * 
     * @param timeScaleRenderer The timeScaleRenderer to set.
     */
    public void setTimeScaleRenderer(TimeScaleRenderer timeScaleRenderer) {
        _timeScaleRenderer = timeScaleRenderer;
        if (_timeScaleRenderer != null && _timeScaleRenderer.getHeight() != -1) {
            // if the renderer announces a preferred height, get it and set it
            _delegate.setXAxisHeight(_timeScaleRenderer.getHeight());
        }
        // maybe the new time scale renderer is not a TickProvider ... tell the grid rederer
        if (_gridRenderer != null) {
            _gridRenderer.setTickProvider(null);
        }
        if (_timeScaleRenderer != null && _gridRenderer != null && _timeScaleRenderer instanceof ITickProvider
                && _delegate.getTimeScalePosition() != TimeBarViewerInterface.TIMESCALE_POSITION_NONE) {
            _gridRenderer.setTickProvider((ITickProvider) _timeScaleRenderer);
        }
        _diagram.safeRepaint();
    }

    /**
     * Get the current misc renderer.
     * 
     * @return the misc renderer
     */
    public IMiscRenderer getMiscRenderer() {
        return _miscRenderer;
    }

    /**
     * Set the misc renderer to be used for rendering some parts/elements in the viewer.
     * 
     * @param miscRenderer the renderer to use
     */
    public void setMiscRenderer(IMiscRenderer miscRenderer) {
        _miscRenderer = miscRenderer;
        _diagram.safeRepaint();
    }

    /**
     * Retrieve the renderer that is currently used to render the title area.
     * 
     * @return the renderer or <code>null</code> if no renderer is set.
     */
    public ITitleRenderer getTitleRenderer() {
        return _titleRenderer;
    }

    /**
     * Set the title renderer.
     * 
     * @param titleRenderer the new renderer or <code>null</code> to disable titel rendering.
     */
    public void setTitleRenderer(ITitleRenderer titleRenderer) {
        _titleRenderer = titleRenderer;
        _diagram.safeRepaint();
    }

    /**
     * Retrieve the relation renderer currently set.
     * 
     * @return the relation renderer or <code>null</code> if none is set.
     */
    public IRelationRenderer getRelationRenderer() {
        return _relationRenderer;
    }

    /**
     * Set the relation renderer to use.
     * 
     * @param relationRenderer the renderer or <code>null</code> to disable relation rendering.
     */
    public void setRelationRenderer(IRelationRenderer relationRenderer) {
        _relationRenderer = relationRenderer;
        _diagram.safeRepaint();
    }

    /**
     * Retrieve the configured marker renderer.
     * 
     * @return the marker renderer
     */
    public IMarkerRenderer getMarkerRenderer() {
        return _markerRenderer;
    }

    /**
     * Set the marker renderer to be used.
     * 
     * @param markerRenderer the marker renderer to be used.
     */
    public void setMarkerRenderer(IMarkerRenderer markerRenderer) {
        _markerRenderer = markerRenderer;
        _diagram.safeRepaint();
    }

    /**
     * Get the current global assistant renderer.
     * 
     * @return the renderer currently in use
     */
    public IGlobalAssistantRenderer getGlobalAssistantRenderer() {
        return _globalAssistantRenderer;
    }

    /**
     * Set a gloabl assistant renederer.
     * 
     * @param globalAssistantRenderer the renderer
     */
    public void setGlobalAssistantRenderer(IGlobalAssistantRenderer globalAssistantRenderer) {
        _globalAssistantRenderer = globalAssistantRenderer;
        _diagram.safeRepaint();
    }

    /**
     * @return Returns the gridRenderer.
     */
    public GridRenderer getGridRenderer() {
        return _gridRenderer;
    }

    /**
     * @param gridRenderer The gridRenderer to set.
     */
    public void setGridRenderer(GridRenderer gridRenderer) {
        _gridRenderer = gridRenderer;
        // set the tick provider on the grid renderer
        if (_gridRenderer != null && _timeScaleRenderer != null && _timeScaleRenderer instanceof ITickProvider) {
            _gridRenderer.setTickProvider((ITickProvider) _timeScaleRenderer);
        }
        _diagram.safeRepaint();
    }

    /**
     * @return Returns the gapRenderer.
     */
    public TimeBarGapRenderer getGapRenderer() {
        return _gapRenderer;
    }

    /**
     * @param gapRenderer The gapRenderer to set.
     */
    public void setGapRenderer(TimeBarGapRenderer gapRenderer) {
        _gapRenderer = gapRenderer;
        _diagram.safeRepaint();
    }

    /**
     * Register a popup menu for a given interval class.
     * 
     * @param clazz class that the menu is for
     * @param popup popup menu to show
     */
    public void registerPopupMenu(Class<? extends Interval> clazz, JPopupMenu popup) {
        if (_registeredPopupMenues == null) {
            _registeredPopupMenues = new HashMap<Class<? extends Interval>, JPopupMenu>();
        }
        _registeredPopupMenues.put(clazz, popup);
    }

    /**
     * Retrieve the popup menu registered for a given interval class.
     * 
     * @param clazz class in question
     * @return menu or <code>null</code>
     */
    public JPopupMenu getPopupMenu(Class<? extends Interval> clazz) {
        if (_registeredPopupMenues == null) {
            return null;
        }
        JPopupMenu result = null;
        result = _registeredPopupMenues.get(clazz);
        if (result != null) {
            return result;
        }

        // direct interfaces
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> c : interfaces) {
            result = _registeredPopupMenues.get(c);
            if (result != null) {
                return result;
            }
        }

        // superclasses
        Class<?> sc = clazz.getSuperclass();

        while (sc != null) {
            result = _registeredPopupMenues.get(sc);
            if (result != null) {
                return result;
            }
            // interfaces of the superclass
            Class<?>[] scinterfaces = sc.getInterfaces();
            for (Class<?> c : scinterfaces) {
                result = _registeredPopupMenues.get(c);
                if (result != null) {
                    return result;
                }
            }
            sc = sc.getSuperclass();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void firePropertyChangeX(String propName, Object oldVal, Object newVal) {
        firePropertyChange(propName, oldVal, newVal);
    }

    /**
     * {@inheritDoc}
     */
    public boolean timeBarContains(Interval interval, Rectangle intervalRect, int x, int y, boolean overlapping) {
        JComponent component = getIntervalComponent(interval, intervalRect, overlapping);
        return component.contains(x, y);
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle timeBarContainingRect(Interval interval, Rectangle intervalRect, boolean overlapping) {
        JComponent component = getIntervalComponent(interval, intervalRect);
        // get the containing rect or - if not set by the renderer -
        // use the component bounds
        Rectangle containingRect = (Rectangle) component.getClientProperty(TimeBarRenderer.CONTAINING_RECTANGLE);
        return containingRect;
    }

    /**
     * {@inheritDoc}
     */
    public void setCursor(int cursorType) {
        setCursor(Cursor.getPredefinedCursor(cursorType));
    }

    /**
     * {@inheritDoc}
     */
    public String getIntervalToolTipText(Interval interval, Rectangle intervalRect, int x, int y) {
        JComponent component = getIntervalComponent(interval, intervalRect);
        // String tooltip = component.getToolTipText(new MouseEvent(this, 0, 0,
        // 0, x - component.getX(), y - component.getY(), 0, false));
        String tooltip = component.getToolTipText(new MouseEvent(this, 0, 0, 0, x, y, 0, false));
        return tooltip;
    }

    /**
     * {@inheritDoc}
     */
    public JaretDate getStartDate() {
        return _delegate.getStartDate();
    }

    /**
     * {@inheritDoc}
     */
    public void setStartDate(JaretDate startDate) {
        _delegate.setStartDate(startDate);
    }

    /**
     * {@inheritDoc}
     */
    public JaretDate getMinDate() {
        return _delegate.getMinDate();
    }

    /**
     * {@inheritDoc}
     */
    public void setMinDate(JaretDate minDate) {
        _delegate.setMinDate(minDate);
    }

    /**
     * {@inheritDoc}
     */
    public JaretDate getMaxDate() {
        return _delegate.getMaxDate();
    }

    /**
     * {@inheritDoc}
     */
    public void setMaxDate(JaretDate maxDate) {
        _delegate.setMaxDate(maxDate);
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarSelectionModel getSelectionModel() {
        return _delegate.getSelectionModel();
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectionModel(TimeBarSelectionModel selectionModel) {
        _delegate.setSelectionModel(selectionModel);
    }

    /**
     * {@inheritDoc}
     */
    public int getFirstRowDisplayed() {
        return _delegate.getFirstRow();
    }

    /**
     * {@inheritDoc}
     */
    public void setFirstRowDisplayed(int rowIdx) {
        _delegate.setFirstRow(rowIdx);
    }

    /**
     * {@inheritDoc}
     */
    public void setFirstRowDisplayed(TimeBarRow row) {
        _delegate.setFirstRow(row);
    }

    /**
     * {@inheritDoc}
     */
    public void setFirstRow(int firstRow, int pixOffset) {
        _delegate.setFirstRow(firstRow, pixOffset);
    }

    /**
     * {@inheritDoc}
     */
    public void setLastRow(int index) {
        _delegate.setLastRow(index);
    }

    /**
     * {@inheritDoc}
     */
    public void setLastRow(TimeBarRow row) {
        _delegate.setLastRow(row);
    }

    /**
     * {@inheritDoc}
     */
    public JaretDate getEndDate() {
        return _delegate.getEndDate();
    }

    /**
     * {@inheritDoc}
     */
    public int getFirstRowOffset() {
        return _delegate.getFirstRowOffset();
    }

    /**
     * {@inheritDoc}
     */
    public void setFirstRowOffset(int offset) {
        _delegate.setFirstRowOffset(offset);
    }

    /**
     * {@inheritDoc}
     */
    public void setTimeScalePosition(int timeScalePosition) {
        if (timeScalePosition == TimeBarViewerInterface.TIMESCALE_POSITION_NONE) {
            if (_gridRenderer != null) {
                _gridRenderer.setTickProvider(null);
            }
        } else {
            if (_gridRenderer != null && _timeScaleRenderer instanceof ITickProvider) {
                _gridRenderer.setTickProvider((ITickProvider) _timeScaleRenderer);
            }
        }
        _delegate.setTimeScalePosition(timeScalePosition);
    }

    /**
     * {@inheritDoc}
     */
    public void setAdjustMinMaxDatesByModel(boolean adjust) {
        _delegate.setAdjustMinMaxDatesByModel(adjust);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getAdjustMinMaxDatesByModel() {
        return _delegate.getAdjustMinMaxDatesByModel();
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarRow rowForY(int y) {
        return _delegate.rowForY(y);
    }

    /**
     * {@inheritDoc}
     */
    public JaretDate dateForX(int x) {
        return _delegate.dateForCoord(x);
    }

    /**
     * {@inheritDoc}
     */
    public JaretDate dateForXY(int x, int y) {
        return _delegate.dateForCoord(x, y);
    }

    /**
     * {@inheritDoc}
     */
    public int xForDate(JaretDate date) {
        return _delegate.xForDate(date);
    }

    /**
     * {@inheritDoc}
     */
    public void highlightRow(int y) {
        _delegate.highlightRow(y);
    }

    /**
     * {@inheritDoc}
     */
    public void highlightRow(TimeBarRow timeBarRow) {
        _delegate.highlightRow(timeBarRow);
    }

    /**
     * {@inheritDoc}
     */
    public void deHighlightRow() {
        _delegate.deHighlightRow();
    }

    /**
     * {@inheritDoc}
     */
    public void setDrawRowGrid(boolean drawRowGrid) {
        _delegate.setDrawRowGrid(drawRowGrid);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getDrawRowGrid() {
        return _delegate.getDrawRowGrid();
    }

    /**
     * {@inheritDoc}
     */
    public void setYAxisWidth(int width) {
        _delegate.setYAxisWidth(width);
    }

    /**
     * {@inheritDoc}
     */
    public int getYAxisWidth() {
        return _delegate.getYAxisWidth();
    }

    /**
     * {@inheritDoc}
     */
    public void setHierarchyWidth(int width) {
        _delegate.setHierarchyWidth(width);
    }

    /**
     * {@inheritDoc}
     */
    public int getHierarchyWidth() {
        return _delegate.getHierarchyWidth();
    }

    /**
     * {@inheritDoc}
     */
    public void addIntervalModificator(IntervalModificator intervalModificator) {
        _delegate.addIntervalModificator(intervalModificator);
    }

    /**
     * {@inheritDoc}
     */
    public void remIntervalModificator(IntervalModificator intervalModificator) {
        _delegate.remIntervalModificator(intervalModificator);
    }

    /**
     * {@inheritDoc}
     */
    public void setAutoscrollEnabled(boolean enableAutoscroll) {
        _delegate.setAutoscrollEnabled(enableAutoscroll);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAutoscrollEnabled() {
        return _delegate.isAutoscrollEnabled();
    }

    /**
     * {@inheritDoc}
     */
    public String getTimeScaleToolTipText(int x, int y) {
        if (_timeScaleRendererComponent != null) {
            return _timeScaleRendererComponent.getToolTipText(new MouseEvent(this, 0, 0, 0, x
                    - _delegate.getDiagramRect().x, y - _delegate.getXAxisRect().y, 0, false));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getHeaderToolTipText(TimeBarRow row, int x, int y) {
        if (_headerRenderer != null && row != null) {
            JComponent component = _headerRenderer.getHeaderRendererComponent(this, row.getRowHeader(), false);
            if (component != null) {
                component.setBounds(_delegate.getHeaderRect(row));
                return component.getToolTipText(new MouseEvent(this, 0, 0, 0, x - _delegate.getYAxisRect().x, y
                        - _delegate.getYAxisRect().y, 0, false));
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getHierarchyToolTipText(TimeBarNode node, int x, int y) {
        if (_hierarchyRenderer != null && node != null) {
            // TODO check additional parameters to be corrrect - maybe these are
            // useful for generating the tooltip
            JComponent component = _hierarchyRenderer.getHierarchyRendererComponent(this, node, false, false, false, 0,
                    1);
            component.setBounds(_delegate.getHierarchyRect(node));
            return component.getToolTipText(new MouseEvent(this, 0, 0, 0, x - _delegate.getHierarchyRect().x, y
                    - _delegate.getHierarchyRect().y, 0, false));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public HierarchicalViewState getHierarchicalViewState() {
        return _delegate.getHierarchicalViewState();
    }

    /**
     * {@inheritDoc}
     */
    public void setHierarchicalViewState(HierarchicalViewState hierarchicalViewState) {
        _delegate.setHierarchicalViewState(hierarchicalViewState);
    }

    /**
     * {@inheritDoc}
     */
    public void setModel(HierarchicalTimeBarModel hModel) {
        _delegate.setModel(hModel);
    }

    /**
     * {@inheritDoc}
     */
    public HierarchicalTimeBarModel getHierarchicalModel() {
        return _delegate.getHierarchicalModel();
    }

    /**
     * {@inheritDoc}
     */
    public int getMarkerWidth(TimeBarMarker marker) {
        if (_markerRenderer != null) {
            return _markerRenderer.getMarkerWidth(marker);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void setTitle(String title) {
        _delegate.setTitle(title);
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return _delegate.getTitle();
    }

    /**
     * Retrieve the context menu set for the body.
     * 
     * @return the context menu or <code>null</code>
     */
    public JPopupMenu getBodyContextMenu() {
        return _bodyContextMenu;
    }

    /**
     * Set the context menu to be used for the body area.
     * 
     * @param bodyContextMenu context menu or <code>null</code> for no context menu
     */
    public void setBodyContextMenu(JPopupMenu bodyContextMenu) {
        _bodyContextMenu = bodyContextMenu;
    }

    /**
     * Retrieve the context menu set for the time scale.
     * 
     * @return the context menu or <code>null</code>
     */
    public JPopupMenu getTimeScaleContextMenu() {
        return _timeScaleContextMenu;
    }

    /**
     * Set the context menu to be used for the time scale area.
     * 
     * @param timeScaleContextMenu context menu or <code>null</code> for no context menu
     */
    public void setTimeScaleContextMenu(JPopupMenu timeScaleContextMenu) {
        _timeScaleContextMenu = timeScaleContextMenu;
    }

    /**
     * Retrieve the context menu set for the header.
     * 
     * @return the context menu or <code>null</code>
     */

    public JPopupMenu getHeaderContextMenu() {
        return _headerContextMenu;
    }

    /**
     * Set the context menu to be used for the header area.
     * 
     * @param headerContextMenu context menu or <code>null</code> for no context menu
     */
    public void setHeaderContextMenu(JPopupMenu headerContextMenu) {
        _headerContextMenu = headerContextMenu;
    }

    /**
     * Retrieve the context menu set for the hierrarchy.
     * 
     * @return the context menu or <code>null</code>
     */
    public JPopupMenu getHierarchyContextMenu() {
        return _hierarchyContextMenu;
    }

    /**
     * Set the context menu to be used for the hierarchy area.
     * 
     * @param hierarchyContextMenu context menu or <code>null</code> for no context menu
     */
    public void setHierarchyContextMenu(JPopupMenu hierarchyContextMenu) {
        _hierarchyContextMenu = hierarchyContextMenu;
    }

    /**
     * Retrieve the context menu set for the title area.
     * 
     * @return the context menu or <code>null</code>
     */
    public JPopupMenu getTitleContextMenu() {
        return _titleContextMenu;
    }

    /**
     * Set the context menu to be used for the titel area.
     * 
     * @param titleContextMenu context menu or <code>null</code> for no context menu
     */
    public void setTitleContextMenu(JPopupMenu titleContextMenu) {
        _titleContextMenu = titleContextMenu;
    }

    /**
     * {@inheritDoc}
     */
    public void displayBodyContextMenu(int x, int y) {
        if (_bodyContextMenu != null) {
            _bodyContextMenu.show(_diagram, x, y);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void displayTimeScaleContextMenu(int x, int y) {
        if (_timeScaleContextMenu != null) {
            _timeScaleContextMenu.show(_diagram, x, y);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void displayIntervalContextMenu(Interval interval, int x, int y) {
        JPopupMenu menu = getPopupMenu(interval.getClass());
        if (menu != null) {
            menu.show(_diagram, x, y);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void displayHeaderContextMenu(TimeBarRow row, int x, int y) {
        if (_headerContextMenu != null) {
            _headerContextMenu.show(_diagram, x, y);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void displayHierarchyContextMenu(TimeBarRow row, int x, int y) {
        if (_hierarchyContextMenu != null) {
            _hierarchyContextMenu.show(_diagram, x, y);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void displayTitleContextMenu(int x, int y) {
        if (_titleContextMenu != null) {
            _titleContextMenu.show(_diagram, x, y);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInToggleArea(TimeBarNode node, int x, int y) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInHierarchySelectionArea(TimeBarNode node, int x, int y) {
        return false;
    }

    /**
     * Set the drawing mode.
     * 
     * @param drawOverlapping if set to true intervals will be painted on another. If set to false, every interval will
     * only get a fraction of the space corresponding to the count of overlapping intervals.
     */
    public void setDrawOverlapping(boolean drawOverlapping) {
        _delegate.setDrawOverlapping(drawOverlapping);
    }

    /**
     * Retrieve the drawing mode.
     * 
     * @return the drawing mode
     */
    public boolean getDrawOverlapping() {
        return _delegate.isDrawOverlapping();
    }

    /**
     * {@inheritDoc}
     */
    public void addMarker(TimeBarMarker marker) {
        _delegate.addMarker(marker);
    }

    /**
     * {@inheritDoc}
     */
    public void remMarker(TimeBarMarker marker) {
        _delegate.remMarker(marker);
    }

    /**
     * {@inheritDoc}
     */
    public List<TimeBarMarker> getMarkers() {
        return _delegate.getMarkers();
    }

    /**
     * {@inheritDoc}
     */
    public void addMarkers(List<TimeBarMarker> markers) {
        _delegate.addMarkers(markers);
    }

    /**
     * {@inheritDoc}
     */
    public int getSelectionDelta() {
        return _delegate.getSelectionDelta();
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectionDelta(int selectionDelta) {
        _delegate.setSelectionDelta(selectionDelta);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLineDraggingAllowed() {
        return _delegate.isLineDraggingAllowed();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineDraggingAllowed(boolean lineDraggingAllowed) {
        _delegate.setLineDraggingAllowed(lineDraggingAllowed);
    }

    /**
     * {@inheritDoc}
     */
    public int getYForRow(TimeBarRow row) {
        return _delegate.yForRow(row);
    }

    /**
     * {@inheritDoc}. This is the same as rowForY.
     */
    public TimeBarRow getRowForY(int y) {
        return _delegate.rowForY(y);
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarRow getRowForXY(int x, int y) {
        return _delegate.rowForXY(x, y);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMilliAccuracy() {
        return _delegate.isMilliAccuracy();
    }

    /**
     * {@inheritDoc}
     */
    public void setMilliAccuracy(boolean milliAccuracy) {
        _delegate.setMilliAccuracy(milliAccuracy);
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarNode getPpsRow() {
        return _delegate.getPpsRow();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasVariableXScale() {
        return _delegate.hasVariableXScale();
    }

    /**
     * {@inheritDoc}
     */
    public void setVariableXScale(boolean state) {
        _delegate.setVariableXScale(state);
    }

    /**
     * {@inheritDoc} Includes the timescale if present.
     */
    public void doScrollHorizontal(int diff) {
        Graphics g = _diagram.getGraphics();
        if (g == null) {
            return;
        }
        if (_delegate.getDiagramRect() == null) {
            return;
        }

        Rectangle d = new Rectangle(_delegate.getDiagramRect());
        if (_delegate.getOrientation().equals(Orientation.HORIZONTAL)) {
            if (_delegate.getTimeScalePosition() != TIMESCALE_POSITION_NONE) {
                if (_delegate.getTimeScalePosition() == TIMESCALE_POSITION_TOP) {
                    d.y = _delegate.getXAxisRect().y;
                }
                d.height += _delegate.getXAxisRect().height;
            }
        } else {
            d.y = 0;
            d.height = d.height + _delegate.getHierarchyWidth() + _delegate.getYAxisWidth();
        }

        if (diff > 0) {
            // to the right
            g.copyArea(d.x + diff, d.y, d.width - diff, d.height, -diff, 0);
            _diagram.repaint(d.x + d.width - diff, d.y, diff, d.height);
        } else {
            diff = -diff;
            g.copyArea(d.x, d.y, d.width - diff, d.height, diff, 0);
            _diagram.repaint(d.x, d.y, diff, d.height);
        }
        g.dispose();
    }

    /**
     * {@inheritDoc} Includes header and/or hierarchy if present.
     */
    public void doScrollVertical(int diff) {
        Graphics g = _diagram.getGraphics();
        if (g == null) {
            return;
        }

        if (_delegate.getDiagramRect() == null) {
            return;
        }

        Rectangle d = new Rectangle(_delegate.getDiagramRect());
        if (_delegate.getOrientation().equals(Orientation.HORIZONTAL)) {
            d.x = d.x - _delegate.getHierarchyWidth() - _delegate.getYAxisWidth();
            d.width = d.width + _delegate.getHierarchyWidth() + _delegate.getYAxisWidth();
        } else {
            if (_delegate.getTimeScalePosition() == TIMESCALE_POSITION_TOP) {
                d.x = d.x - _delegate.getXAxisHeight();
            }
            d.width = d.width + _delegate.getXAxisHeight();
        }
        if (diff > 0) {
            // downwards
            g.copyArea(d.x, d.y + diff, d.width, d.height - diff, 0, -diff);
            _diagram.repaint(d.x, d.y + d.height - diff, d.width, diff);
        } else {
            diff = -diff;
            g.copyArea(d.x, d.y, d.width, d.height - diff, 0, diff);
            _diagram.repaint(d.x, d.y, d.width, diff);
        }
        g.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public boolean getOptimizeScrolling() {
        return _delegate.getOptimizeScrolling();
    }

    /**
     * {@inheritDoc}
     */
    public void setOptimizeScrolling(boolean optimizeScrolling) {
        _delegate.setOptimizeScrolling(optimizeScrolling);
    }

    /**
     * {@inheritDoc}
     */
    public Orientation getOrientation() {
        return _delegate.getOrientation();
    }

    /**
     * {@inheritDoc}
     */
    public void setOrientation(Orientation orientation) {
        _delegate.setOrientation(orientation);
    }

    /**
     * {@inheritDoc}
     */
    public int getAutoScaleRows() {
        return _delegate.getAutoScaleRows();
    }

    /**
     * {@inheritDoc}
     */
    public void setAutoScaleRows(int rows) {
        _delegate.setAutoScaleRows(rows);
    }

    /**
     * {@inheritDoc}
     */
    public int getXAxisHeight() {
        return _delegate.getXAxisHeight();
    }

    /**
     * {@inheritDoc}
     */
    public void setXAxisHeight(int height) {
        _delegate.setXAxisHeight(height);
    }

    /**
     * {@inheritDoc}
     */
    public void fireSelectionChanged() {
        // nothing to do in the swing version
    }

    /**
     * {@inheritDoc}
     */
    public void addTimeBarChangeListener(ITimeBarChangeListener listener) {
        _delegate.addTimeBarChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removeTimeBarChangeListener(ITimeBarChangeListener listener) {
        _delegate.removeTimeBarChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void addFocussedIntervalListener(FocussedIntervalListener listener) {
        _delegate.addFocussedIntervalListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void remFocussedIntervalListener(FocussedIntervalListener listener) {
        _delegate.remFocussedIntervalListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public ITimeBarViewState getTimeBarViewState() {
        return _delegate.getTimeBarViewState();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRowHeightDragginAllowed() {
        return _delegate.isRowHeightDraggingAllowed();
    }

    /**
     * {@inheritDoc}
     */
    public void setRowHeightDraggingAllowed(boolean rowHeightDraggingAllowed) {
        _delegate.setRowHeightDraggingAllowed(rowHeightDraggingAllowed);
    }

    /**
     * {@inheritDoc}
     */
    public boolean rowLineHit(int x, int y) {
        return _delegate.rowLineHit(x, y);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInRowAxis(int x, int y) {
        return _delegate.isInRowAxis(x, y);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInDiagram(int x, int y) {
        return _delegate.isInDiagram(x, y);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getStrictClipTimeCheck() {
        return _delegate.getStrictClipTimeCheck();
    }

    /**
     * {@inheritDoc}
     */
    public void setStrictClipTimeCheck(boolean strictClipTimeCheck) {
        _delegate.setStrictClipTimeCheck(strictClipTimeCheck);
    }

    /**
     * {@inheritDoc}
     */
    public int getSecondsDisplayed() {
        return _delegate.getSecondsDisplayed();
    }

    /**
     * {@inheritDoc}
     */
    public IOverlapStrategy getOverlapStrategy() {
        return _delegate.getOverlapStrategy();
    }

    /**
     * {@inheritDoc}
     */
    public void setOverlapStrategy(IOverlapStrategy overlapStrategy) {
        _delegate.setOverlapStrategy(overlapStrategy);
    }

    /**
     * {@inheritDoc}
     */
    public int getScrollLookBackMinutes() {
        return _delegate.getScrollLookBackMinutes();
    }

    /**
     * {@inheritDoc}
     */
    public void setScrollLookBackMinutes(int scrollLookBackMinutes) {
        _delegate.setScrollLookBackMinutes(scrollLookBackMinutes);
    }

    /**
     * {@inheritDoc}
     */
    public void setScrollLookForwardMinutes(int scrollLookForwardMinutes) {
        _delegate.setScrollLookForwardMinutes(scrollLookForwardMinutes);
    }

    /**
     * {@inheritDoc}
     */
    public int getScrollLookForwardMinutes() {
        return _delegate.getScrollLookForwardMinutes();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        // null check becuase of the gtk plaf that calls getName before the component is fully initialized
        if (_delegate != null) {
            return _delegate.getName();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        _delegate.setName(name);
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarViewerDelegate getDelegate() {
        return _delegate;
    }

    /**
     * {@inheritDoc}
     */
    public int getAutoscrollDelta() {
        return _delegate.getAutoscrollDelta();
    }

    /**
     * {@inheritDoc}
     */
    public void setAutoscrollDelta(int autoscrollDelta) {
        _delegate.setAutoscrollDelta(autoscrollDelta);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getDragAllSelectedIntervals() {
        return _delegate.getDragAllSelectedIntervals();
    }

    /**
     * {@inheritDoc}
     */
    public void setDragAllSelectedIntervals(boolean dragAllSelectedIntervals) {
        _delegate.setDragAllSelectedIntervals(dragAllSelectedIntervals);
    }

    /**
     * {@inheritDoc}
     */
    public List<IIntervalRelation> getRelationsForCoord(int x, int y) {
        if (_relationRenderer != null) {
            return _relationRenderer.getRelationsForCoord(x, y);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getRelationTooltip(int x, int y) {
        if (_relationRenderer != null) {
            return _relationRenderer.getTooltip(x, y);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getScrollOnFocus() {
        return _delegate.getScrollOnFocus();
    }

    /**
     * {@inheritDoc}
     */
    public void setScrollOnFocus(boolean scrollOnFocus) {
        _delegate.setScrollOnFocus(scrollOnFocus);
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectionRectListener(ISelectionRectListener listener) {
        _delegate.addSelectionRectListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void remSelectionRectListener(ISelectionRectListener listener) {
        _delegate.remSelectionRectListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getHideRoot() {
        return _delegate.getHideRoot();
    }

    /**
     * {@inheritDoc}
     */
    public void setHideRoot(boolean hideRoot) {
        _delegate.setHideRoot(hideRoot);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getMarkerDraggingInDiagramArea() {
        return _delegate.getMarkerDraggingInDiagramArea();
    }

    /**
     * {@inheritDoc}
     */
    public void setMarkerDraggingInDiagramArea(boolean allowed) {
        _delegate.setMarkerDraggingInDiagramArea(allowed);
    }

    /**
     * {@inheritDoc}
     */
    public void clearRegionRect() {
        _delegate.clearRegionRect();
    }

    /**
     * {@inheritDoc}
     */
    public TBRect getRegionRect() {
        return _delegate.getRegionRect();
    }

    /**
     * {@inheritDoc}
     */
    public boolean getRegionRectEnable() {
        return _delegate.getRegionRectEnable();
    }

    /**
     * {@inheritDoc}
     */
    public void setRegionRectEnable(boolean enabled) {
        _delegate.setRegionRectEnable(enabled);
    }

    /**
     * {@inheritDoc} Repaint a rectangle. Overridden and extended widht and height by 1.
     */
    public void repaint(Rectangle r) {
        super.repaint(r.x, r.y, r.width + 1, r.height + 1);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUseUniformHeight() {
        return _delegate.getUseUniformHeight();
    }

    /**
     * {@inheritDoc}
     */
    public void setUseUniformHeight(boolean useUniformHeight) {
        _delegate.setUseUniformHeight(useUniformHeight);
    }

    /**
     * {@inheritDoc}
     */
    public void setSecondsDisplayed(int seconds, boolean center) {
        _delegate.setSecondsDisplayed(seconds, center);
    }

    /**
     * {@inheritDoc}
     */
    public void setSecondsDisplayed(int seconds, JaretDate centerDate) {
        _delegate.setSecondsDisplayed(seconds, centerDate);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDisplayed(JaretDate date) {
        return _delegate.isDisplayed(date);
    }

    /**
     * {@inheritDoc}
     */
    public void setInitialDisplayRange(JaretDate startDate, int secondsDisplayed) {
        _delegate.setInitialDisplayRange(startDate, secondsDisplayed);
    }

    /**
     * Retrieve the panel that the horizontal scroll bar is plcaed on (BorderLayout, CENETER). This can be used to add
     * special extensions in the scroll bar area.
     * 
     * @return th panel or <code>null</code> if the scroll bar has been suppressed
     */
    public JPanel getHorizontalScrollPanel() {
        return _horizontalScrollPanel;
    }

    /**
     * Retrieve the panel that the vertical scroll bar is plcaed on (BorderLayout, CENETER). This can be used to add
     * special extensions in the scroll bar area.
     * 
     * @return th panel or <code>null</code> if the scroll bar has been suppressed
     */
    public JPanel getVerticalScrollPanel() {
        return _verticalScrollPanel;
    }

    /**
     * {@inheritDoc}
     */
    public Pair<TimeBarRow, JaretDate> getPopUpInformation() {
        return _delegate.getPopUpInformation();
    }

}
