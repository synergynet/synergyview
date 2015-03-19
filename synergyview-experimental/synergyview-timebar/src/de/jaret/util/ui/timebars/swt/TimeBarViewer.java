/*
 *  File: TimeBarViewer.java 
 *  Copyright (c) 2004-2008  Peter Kliem (Peter.Kliem@jaret.de)
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

import java.awt.Cursor;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

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
import de.jaret.util.ui.timebars.model.TimeBarSelectionModel;
import de.jaret.util.ui.timebars.model.TimeBarSelectionModelImpl;
import de.jaret.util.ui.timebars.strategy.IOverlapStrategy;
import de.jaret.util.ui.timebars.strategy.ITickProvider;
import de.jaret.util.ui.timebars.swt.renderer.AbstractGridRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultGridRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultHeaderRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultMiscRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultTimeBarMarkerRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultTimeScaleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultTitleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.GlobalAssistantRenderer;
import de.jaret.util.ui.timebars.swt.renderer.GridRenderer;
import de.jaret.util.ui.timebars.swt.renderer.HeaderRenderer;
import de.jaret.util.ui.timebars.swt.renderer.HierarchyRenderer;
import de.jaret.util.ui.timebars.swt.renderer.IMiscRenderer;
import de.jaret.util.ui.timebars.swt.renderer.IRelationRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarGapRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarMarkerRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeScaleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TitleRenderer;
import de.jaret.util.ui.timebars.swt.util.actions.JaretTimeBarsActionFactory;

/**
 * Viewer for a TimeBarModel (SWT version). Displays the intervals using a renderer. Supports sorting and/or filtering
 * of the rows in the model without affecting the model itself.
 * <p>
 * The implementation depends on the <code>TimeBarViewerDelegate</code> for the operations and calculations that is
 * shared between the Swing and the SWT implementation of the viewer. There is no accesor to access the delegate
 * directly. However the documentation of the delegate can be helpful. The delegate can temporarily be accessed
 * <code>getData("delegate")</code> for debugging and experimental features.
 * </p>
 * <p>
 * The timebar viewer is an implementation of the ISelectionProvider working with structured selections containing rows
 * and intervals.
 * </p>
 * <p>
 * <b>Keyboard bindings</b>
 * </p>
 * <p>
 * <ul>
 * <li>cursor keys: move focus between intervals</li>
 * <li>space: toggle select for focused intervals</li>
 * <li>ctrl: if hold: add element to selection</li>
 * <li>shift+arrow right/left: grow interval to the right/left (if allowed, steps can be configured by
 * keyboardchangedelta)</li>
 * <li>alt+arrow right/left: shrink interval from the right left (if allowed, steps can be configured by
 * keyboardchangedelta)</li>
 * <li>ctrl+arrow right/left: move focussed interval (if allowed, steps can be configured by keyboardchangedelta)</li>
 * <li>escape: cancel internal drag/resize operation</li>
 * </ul>
 * </p>
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarViewer.java 894 2009-11-02 22:29:11Z kliem $
 */
public class TimeBarViewer extends Canvas implements TimeBarViewerInterface, ISelectionProvider {
    /** DEBUGGING OPTION: if set to true the actual paint times will be printed to stdout. */
    private static final boolean SHOWPAINTTIME = false;

    /** default color for rowgrid. */
    public static final Color ROWGRID_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);

    /** Number of seconds will be divided by this divisor for determing arrow incremtn of the scrollbar. */
    protected static final int INCREMENTDIVISOR_X = 10;
    /** Number of rows will be divided by this divisor for determing arrow incremtn of the scrollbar. */
    protected static final int INCREMENTDIVISOR_Y = 10;

    /** Color used to render the limiting lines. */
    protected static final Color LINECOLOR = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
    /** default alpha for ghost draw and row highlighting. */
    public static final int DEFAULT_ALPHA = 70;

    /** color for row grid lines. */
    protected Color _rowGridColor = ROWGRID_COLOR;
    /** alpha for ghost drawing. */
    protected int _ghostAlpha = DEFAULT_ALPHA;

    /** Identifier of the pop up button. */
    private static final int POPUP_BUTTON = 3;

    /** scale factor for converting mouse wheel clicks for scrolling. */
    private static final double MOUSEWHEEL_FACTOR = 2.5;

    /**
     * This delegate encapsules the main part of the functionality of the the viewer.
     */
    protected TimeBarViewerDelegate _delegate;

    /** Renderer used to render the timescale. */
    protected TimeScaleRenderer _timeScaleRenderer = new DefaultTimeScaleRenderer();
    /** mapping between interval classes and renderers. */
    protected Map<Class<? extends Interval>, TimeBarRenderer> _rendererMap = new HashMap<Class<? extends Interval>, TimeBarRenderer>();

    /** Renderer for the hierarchy view (tree structure). */
    protected HierarchyRenderer _hierarchyRenderer;
    /** Renderer for rendering the grid. */
    protected GridRenderer _gridRenderer = new DefaultGridRenderer();
    /** Renderer for doing global rendering. */
    protected GlobalAssistantRenderer _globalRenderer;
    /** Renderer for doing rendering in th egaps between intervals. */
    protected TimeBarGapRenderer _gapRenderer = null;
    /** Renderer to render the row headings. */
    protected HeaderRenderer _headerRenderer = new DefaultHeaderRenderer();
    /** renderer for drawing interval relations. no default renderer is setup. */
    protected IRelationRenderer _relationRenderer = null;

    /** Renderer for the timebar markers. */
    protected TimeBarMarkerRenderer _markerRenderer = new DefaultTimeBarMarkerRenderer();
    /** Renderer for rendering the title area. */
    protected TitleRenderer _titleRenderer = new DefaultTitleRenderer();

    /** Renderer for rendering various bits. */
    protected IMiscRenderer _miscRenderer = new DefaultMiscRenderer();

    /** factory for creating standard actions. */
    protected JaretTimeBarsActionFactory _actionFactory;

    /** context menu for the main viewer body. */
    protected Menu _bodyContextMenu;

    /** context menu displayed for intervals. */
    protected Menu _intervalContextMenu;

    /** context menu displayed for the time scale. */
    protected Menu _scaleContextMenu;

    /** context menu for the title area. */
    protected Menu _titleContextMenu;

    /** handler for retrieving a context menu for the hierarchy. */
    protected RowContextMenuHandler _hierarchyCtxHandler;

    /** handler for retrieving a context menu for the header area. */
    protected RowContextMenuHandler _headerCtxHandler;

    /** Delegate to handle property change listener support. */
    protected PropertyChangeSupport _propertyChangeSupport;

    /** list of Iselction listeners. * */
    protected List<ISelectionChangedListener> _selectionChangeListeners;

    /**
     * Constructor for a timebarviewer. Scrollbars can be added using SWT.H_SCROLL and SWT.V_SCROLL style bits. All
     * other stylebits will not be useful.
     * 
     * @param parent parent composite
     * @param style style bits
     */
    public TimeBarViewer(Composite parent, int style) {
        // super should have no background since otherweise the handling of the
        // paint events
        // will clear the widgets background causing the opimized scroll to fail
        super(parent, style | SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
        // most of the operations are done by the delgate
        _delegate = new TimeBarViewerDelegate(this);

        // set the delegate as a data object for accessing it in special cases
        setData("delegate", _delegate);

        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event) {
                onPaint(event);
            }
        });

        addMouseListener(new MouseListener() {
            public void mouseDoubleClick(MouseEvent me) {
            }

            public void mouseDown(MouseEvent me) {
                forceFocus();
                _delegate.mousePressed(me.x, me.y, me.button == POPUP_BUTTON, convertModifierMaskToSwing(me.stateMask));
            }

            public void mouseUp(MouseEvent me) {
                _delegate
                        .mouseReleased(me.x, me.y, me.button == POPUP_BUTTON, convertModifierMaskToSwing(me.stateMask));
            }
        });

        addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(MouseEvent me) {
                if ((me.stateMask & SWT.BUTTON1) != 0) {
                    _delegate.mouseDragged(me.x, me.y, convertModifierMaskToSwing(me.stateMask));
                } else {
                    _delegate.mouseMoved(me.x, me.y);
                }
            }
        });

        addMouseTrackListener(new MouseTrackListener() {
            public void mouseEnter(MouseEvent arg0) {
            }

            public void mouseExit(MouseEvent arg0) {
            }

            public void mouseHover(MouseEvent me) {
                setToolTipText(_delegate.getToolTipText(me.x, me.y));
            }
        });

        addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                _delegate.handleKeyPressed(convertKeyCodeToSwing(e.keyCode), convertModifierMaskToSwing(e.stateMask));
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                onDispose();
            }
        });

        ScrollBar verticalBar = getVerticalBar();
        if (verticalBar != null) {
            verticalBar.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    handleVerticalScroll(event);
                }
            });
        }
        ScrollBar horizontalBar = getHorizontalBar();
        if (horizontalBar != null) {
            horizontalBar.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    handleHorizontalScroll(event);
                }
            });
        }

        Listener listener = new Listener() {
            public void handleEvent(Event event) {
                switch (event.type) {
                case SWT.Resize:
                    //_delegate.updateScrollBars();
                    _delegate.componentResized();
                    break;
                default:
                    // do nothing
                    break;
                }
            }
        };
        addListener(SWT.Resize, listener);

        // mousewheel for zooming (together with control)
        addListener(SWT.MouseWheel, new Listener() {
            public void handleEvent(Event event) {
                if ((event.stateMask & SWT.CONTROL) != 0) {
                    int c = event.count;
                    double factor = (double) Math.abs(c) / MOUSEWHEEL_FACTOR;
                    if (c > 0) {
                        _delegate.setPixelPerSecond(_delegate.getPixelPerSecond() * factor);
                    } else {
                        _delegate.setPixelPerSecond(_delegate.getPixelPerSecond() / factor);
                    }
                }
            }
        });

        // register the default renderer for intervals
        registerTimeBarRenderer(Interval.class, new DefaultRenderer());

        // default background is white
        setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        // do the coupling of grid and timescale
        _gridRenderer.setTickProvider((ITickProvider) _timeScaleRenderer);

    }

    /**
     * Dispose all renderes after disposing the widget itself.
     */
    public void onDispose() {
        super.dispose();
        _delegate.dispose();
        if (_hierarchyRenderer != null) {
            _hierarchyRenderer.dispose();
        }
        if (_headerRenderer != null) {
            _headerRenderer.dispose();
        }
        if (_timeScaleRenderer != null) {
            _timeScaleRenderer.dispose();
        }
        if (_gridRenderer != null) {
            _gridRenderer.dispose();
        }
        if (_gapRenderer != null) {
            _gapRenderer.dispose();
        }
        disposeTimeBarRenderers();
        if (_markerRenderer != null) {
            _markerRenderer.dispose();
        }
        if (_titleRenderer != null) {
            _titleRenderer.dispose();
        }
        if (_globalRenderer != null) {
            _globalRenderer.dispose();
        }
        if (_relationRenderer != null) {
            _relationRenderer.dispose();
        }
    }

    /**
     * Dispose all registered renderers.
     */
    private void disposeTimeBarRenderers() {
        for (TimeBarRenderer tbr : _rendererMap.values()) {
            tbr.dispose();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getWidth() {
        return getClientArea().width;
    }

    /**
     * {@inheritDoc}
     */
    public int getHeight() {
        return getClientArea().height;
    }

    /**
     * {@inheritDoc}
     */
    public void repaint() {
        redraw();
    }

    /**
     * {@inheritDoc}
     */
    public void repaint(int x, int y, int width, int height) {
        redraw(x, y, width, height, false);
    }

    /**
     * {@inheritDoc}
     */
    public void repaint(java.awt.Rectangle rectangle) {
        repaint(rectangle.x, rectangle.y, rectangle.width + 1, rectangle.height + 1);
    }

    /**
     * Converts a swt statemask to an awt statemask (extended modifier mask of InputEvent).
     * 
     * @param stateMask swt statemask
     * @return awt statemask
     */
    private int convertModifierMaskToSwing(int stateMask) {
        int mask = 0;
        if ((stateMask & SWT.CONTROL) != 0) {
            mask = mask | InputEvent.CTRL_DOWN_MASK;
        }
        if ((stateMask & SWT.SHIFT) != 0) {
            mask = mask | InputEvent.SHIFT_DOWN_MASK;
        }
        if ((stateMask & SWT.ALT) != 0) {
            mask = mask | InputEvent.ALT_DOWN_MASK;
        }
        return mask;
    }

    /**
     * Convert SWT keyCodes to AWT/Swing constants.
     * 
     * @param keyCode key code to convert
     * @return converted keycode.
     */
    private int convertKeyCodeToSwing(int keyCode) {
        int result = keyCode;
        switch (keyCode) {
        case SWT.ARROW_LEFT:
            result = java.awt.event.KeyEvent.VK_LEFT;
            break;
        case SWT.ARROW_RIGHT:
            result = java.awt.event.KeyEvent.VK_RIGHT;
            break;
        case SWT.ARROW_UP:
            result = java.awt.event.KeyEvent.VK_UP;
            break;
        case SWT.ARROW_DOWN:
            result = java.awt.event.KeyEvent.VK_DOWN;
            break;
        case ' ': // SPACE
            result = java.awt.event.KeyEvent.VK_SPACE;
            break;
        case SWT.ESC:
            result = java.awt.event.KeyEvent.VK_ESCAPE;
            break;

        default:
            result = keyCode;
            break;
        }
        return result;
    }

    /**
     * Handle movement of the horizontal scrollbar.
     * 
     * @param event SelectionEvent from the scroll bar
     */
    private void handleHorizontalScroll(SelectionEvent event) {
        int value = getHorizontalBar().getSelection();
        _delegate.handleHorizontalScroll(value, true);
    }

    /**
     * Handles value changes from the vertical scrollbar.
     * 
     * @param event Selection event from the vertical scroll bar
     */
    private void handleVerticalScroll(SelectionEvent event) {
        int value = getVerticalBar().getSelection();
        _delegate.handleVerticalScroll(value, true);
    }

    /**
     * {@inheritDoc}
     */
    public void updateXScrollBar(int max, int pos, int secondsDisplayed) {
        ScrollBar scroll = getHorizontalBar();
        if (scroll != null) {
            scroll.setMinimum(0);
            scroll.setMaximum(max);
            scroll.setThumb(secondsDisplayed);
            scroll.setIncrement(secondsDisplayed / INCREMENTDIVISOR_X); // increment for arrows
            scroll.setPageIncrement(secondsDisplayed); // page increment areas
            scroll.setSelection(pos);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateYScrollBar(int max, int pos, int rowsDisplayed) {
        ScrollBar scroll = getVerticalBar();
        // scroll may be null
        if (scroll != null) {
            scroll.setMinimum(0);
            scroll.setMaximum(max);
            scroll.setThumb(rowsDisplayed);
            scroll.setIncrement(rowsDisplayed / INCREMENTDIVISOR_Y); // increment for arrows
            scroll.setPageIncrement(rowsDisplayed); // page increment areas
            scroll.setSelection(pos);
        }
    }

    /**
     * Calculate the x coordinate for a given date.
     * 
     * @param date date to get the coordinate for
     * @return x coordinate for the given date in the diagram area
     */
    public int xForDate(JaretDate date) {
        return _delegate.xForDate(date);
    }

    /**
     * {@inheritDoc}
     */
    public Point computeSize(int wHint, int hHint, boolean changed) {
        // if a hint is given just return the hint
        if (wHint != SWT.DEFAULT || hHint != SWT.DEFAULT) {
            return new Point(wHint != SWT.DEFAULT ? wHint : 100, hHint != SWT.DEFAULT ? hHint : 100);
        }
        // MAYBE: this only uses the default rowheight instead of the actual row heights
        Point e = new Point((int) ((double) _delegate.getTotalSeconds() * _delegate.getPixelPerSecond()), _delegate
                .getRowCount()
                * _delegate.getTimeBarViewState().getDefaultRowHeight());
        return e;
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
    public void setModel(HierarchicalTimeBarModel hModel) {
        _delegate.setModel(hModel);
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
    public HierarchicalTimeBarModel getHierarchicalModel() {
        return _delegate.getHierarchicalModel();
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

    // *** Propertychange handling
    /**
     * {@inheritDoc}
     */
    public void firePropertyChange(String string, double oldValue, double newValue) {
        firePropertyChangeX(string, Double.valueOf(oldValue), Double.valueOf(newValue));
    }

    /**
     * {@inheritDoc}
     */
    public void firePropertyChangeX(String propName, Object oldVal, Object newVal) {
        if (_propertyChangeSupport != null) {
            _propertyChangeSupport.firePropertyChange(propName, oldVal, newVal);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (_propertyChangeSupport == null) {
            _propertyChangeSupport = new PropertyChangeSupport(this);
        }
        _propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (_propertyChangeSupport != null) {
            _propertyChangeSupport.removePropertyChangeListener(listener);
        }
    }

    // *** end of PropertyChange handling

    /**
     * Do the painting.
     * 
     * @param event PaintEvent to be processed
     */
    synchronized void onPaint(PaintEvent event) {
        GC gc = event.gc;
        // first calculate the layout of the areas
        _delegate.preparePaint(getWidth(), getHeight());

        // update the scrollbars
        // especially for the first paint!
        // TODO MAYBE restrict to one call for the first paint
        _delegate.updateScrollBars();

        long time = System.currentTimeMillis();
        long ntime = System.nanoTime();

        // clear background in the clipping rect
        gc.setBackground(getBackground());
        // gc.setBackground(new Color(Display.getCurrent(), 200, 200, 200));
        gc.fillRectangle(gc.getClipping()); // background painting
        try {
            // draw x axis
            RenderDelegate.drawXAxis(_delegate, _timeScaleRenderer, false, gc);
            // draw grid
            RenderDelegate.drawGrid(_delegate, _gridRenderer, false, gc);

            // if a global renderer is set, do rendering!
            if (_globalRenderer != null) {
                _globalRenderer.doRenderingBeforeIntervals(_delegate, gc, false);
            }
            // draw rows
            drawRows(gc);
            // draw markers
            RenderDelegate.drawMarkers(_delegate, _markerRenderer, false, gc);

            // draw the selection rect
            Rectangle clipSave = gc.getClipping();
            gc.setClipping(clipSave.intersection(convertRect(_delegate.getDiagramRect())));
            drawSelectionRect(gc);
            gc.setClipping(clipSave);

            // draw the title area
            RenderDelegate.drawTitle(_delegate, _titleRenderer, false, gc);

            // draw the region rect
            clipSave = gc.getClipping();
            gc.setClipping(clipSave.intersection(convertRect(_delegate.getDiagramRect())));
            _miscRenderer.renderRegionSelection(gc, this, _delegate);
            gc.setClipping(clipSave);

            // if a global renderer is set, do rendering!
            if (_globalRenderer != null) {
                _globalRenderer.doRenderingLast(_delegate, gc, false);
            }

            // draw ghosts if present
            drawGhosts(gc);

            time = System.currentTimeMillis() - time;
            ntime = System.nanoTime() - ntime;
            // primitive debug method to watch time that is spent during the painting
            if (SHOWPAINTTIME) {
                System.out.println(_delegate.getName() + ": paint " + time + " ms " + ntime + " ns");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Draw the rows.
     * 
     * @param gc GC
     */
    private void drawRows(GC gc) {
        boolean horizontal = true;
        if (_delegate.getOrientation() == Orientation.VERTICAL) {
            horizontal = false;
        }

        // separating line to the header
        // and the hierarchy area
        Color fg = gc.getForeground(); // save fg color
        gc.setForeground(LINECOLOR);
        if (horizontal) {
            gc.drawLine(_delegate.getYAxisWidth() + _delegate.getHierarchyWidth() - 1, 0, _delegate.getYAxisWidth()
                    + _delegate.getHierarchyWidth() - 1, getClientArea().height);
            gc
                    .drawLine(_delegate.getHierarchyWidth() - 1, 0, _delegate.getHierarchyWidth() - 1,
                            getClientArea().height);
        } else {
            gc.drawLine(0, _delegate.getYAxisWidth() + _delegate.getHierarchyWidth() - 1, getClientArea().width,
                    _delegate.getYAxisWidth() + _delegate.getHierarchyWidth() - 1);
            gc.drawLine(0, _delegate.getHierarchyWidth() - 1, getClientArea().width, _delegate.getHierarchyWidth() - 1);
        }
        gc.setForeground(fg); // restore color

        // relation rendering
        if (_relationRenderer != null) {
            _relationRenderer.renderRelations(_delegate, gc, false);
        }

        if (horizontal) {
            drawRowsHorizontal(gc);
        } else {
            drawRowsVertical(gc);
        }
    }

    /**
     * Draws the rows when orientation is horizontal.
     * 
     * @param gc GC
     */
    private void drawRowsHorizontal(GC gc) {
        int firstRow = _delegate.getFirstRow();

        // set the clipping to include only the heigth of the diagram rect
        Rectangle clipSave = gc.getClipping();
        Rectangle nc = new Rectangle(0, _delegate.getDiagramRect().y, getWidth(), _delegate.getDiagramRect().height);
        gc.setClipping(gc.getClipping().intersection(nc));

        int upperYBound = _delegate.getDiagramRect().y;
        int lowerYBound = upperYBound + _delegate.getDiagramRect().height;
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
                drawRowHorizontal(gc, _delegate.getRow(r), y, _delegate.getSelectionModel().isSelected(
                        _delegate.getRow(r)));
                // draw gaps if a renderer is set
                if (_gapRenderer != null) {
                    RenderDelegate.drawRowGaps(_delegate, _gapRenderer, false, gc, 0, y, _delegate.getRow(r), _delegate
                            .getSelectionModel().isSelected(_delegate.getRow(r)));
                }
            }
        }
        gc.setClipping(clipSave);
    }

    /**
     * Draws the rows when orientation is vertical.
     * 
     * @param gc GC
     */
    private void drawRowsVertical(GC gc) {
        int firstRow = _delegate.getFirstRow();

        // set the clipping to include only the width of the diagram rect
        Rectangle clipSave = gc.getClipping();
        Rectangle nc = new Rectangle(_delegate.getDiagramRect().x, 0, _delegate.getDiagramRect().width, getHeight());
        gc.setClipping(gc.getClipping().intersection(nc));

        int upperXBound = _delegate.getDiagramRect().x;
        int lowerXBound = upperXBound + _delegate.getDiagramRect().width;
        if (gc.isClipped()) {
            upperXBound = gc.getClipping().x;
            lowerXBound = upperXBound + gc.getClipping().width;
        }

        for (int r = firstRow; r <= firstRow + _delegate.getRowsDisplayed() + 1 && r < _delegate.getRowCount(); r++) {
            TimeBarRow row = _delegate.getRow(r);
            int x = _delegate.yForRow(row);
            if (x == -1) {
                // no coord
                break;
            }
            // row is drawn if either the beginning or the end is inside the
            // clipping rect
            // or if the upperBound is inside the row rect (clipping rect is
            // inside the row rect
            int rowHeight = _delegate.getTimeBarViewState().getRowHeight(row);
            if ((x >= upperXBound && x <= lowerXBound)
                    || (x + rowHeight >= upperXBound && x + rowHeight <= lowerXBound)
                    || (upperXBound > x && upperXBound < x + rowHeight)) {
                drawRowVertical(gc, _delegate.getRow(r), x, _delegate.getSelectionModel().isSelected(
                        _delegate.getRow(r)));
                // draw gaps if a renderer is set
                if (_gapRenderer != null) {
                    RenderDelegate.drawRowGaps(_delegate, _gapRenderer, false, gc, x, 0, _delegate.getRow(r), _delegate
                            .getSelectionModel().isSelected(_delegate.getRow(r)));
                }
            }
        }
        gc.setClipping(clipSave);
    }

    /**
     * Draws the selection rectangle if present.
     * 
     * @param gc GC
     */
    private void drawSelectionRect(GC gc) {
        if (_delegate.getSelectionRect() != null) {
            // normalize and remember
            _delegate.setLastSelRect(normalizeRectangle(_delegate.getSelectionRect()));
            java.awt.Rectangle selRect = _delegate.getLastSelRect();
            _miscRenderer.renderSelectionRect(gc, selRect);
        }

    }

    /**
     * Normalizes a rectangle to have its origin in the upper left corner.
     * 
     * @param rect rectangle to normalize
     * @return normalized rectangle
     */
    private java.awt.Rectangle normalizeRectangle(java.awt.Rectangle rect) {
        int x = Math.min(rect.x, rect.x + rect.width);
        int y = Math.min(rect.y, rect.y + rect.height);
        int width = Math.abs(rect.width);
        int height = Math.abs(rect.height);
        return new java.awt.Rectangle(x, y, width, height);
    }

    /**
     * Convert a java.awt.Rectangle to a org.eclipse.Rectangle.
     * 
     * @param rect awt rect
     * @return eclipse rect or <code>null</code> if a null is passed in
     */
    public static Rectangle convertRect(java.awt.Rectangle rect) {
        if (rect == null) {
            return null;
        }
        return new Rectangle(rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * Convert eclipse rect to awt rect.
     * 
     * @param rect eclipse rect
     * @return awt rect
     */
    private java.awt.Rectangle convertRect(Rectangle rect) {
        return new java.awt.Rectangle(rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * Draw a single row (horizontal).
     * 
     * @param gc GC
     * @param row row to paint
     * @param y upper screen y for painting
     * @param selected true if the row is selected
     */
    private void drawRowHorizontal(GC gc, TimeBarRow row, int y, boolean selected) {
        int rowHeight = _delegate.getTimeBarViewState().getRowHeight(row);
        // first of all draw the row header
        if (row.getRowHeader() != null) {
            RenderDelegate.drawRowHeaderHorizontal(_delegate, _headerRenderer, false, gc, y, row.getRowHeader(),
                    selected, row);
        }

        // the draw the hierarchy display if configured
        if (_hierarchyRenderer != null && _delegate.getHierarchyWidth() > 0) {
            RenderDelegate.drawHierarchy(_delegate, _hierarchyRenderer, false, gc, y, row, selected);
        }

        // TODO move to grid renderer
        // row grid if configured
        if (_delegate.getDrawRowGrid()) {
            Color fg = gc.getForeground();
            gc.setForeground(_rowGridColor);
            gc.drawLine(_delegate.getDiagramRect().x, y + rowHeight - 1, _delegate.getDiagramRect().x
                    + _delegate.getDiagramRect().width, y + rowHeight - 1);
            gc.setForeground(fg);
        }

        // do row grid rendering/selection rendering BEFORE interval painting
        Rectangle rowRect = null;
        if (_gridRenderer != null) {
            int markerHeight = rowHeight;
            // calculate height for clipping
            if (y + markerHeight > _delegate.getDiagramRect().y + _delegate.getDiagramRect().height) {
                markerHeight = markerHeight
                        - (y + markerHeight - (_delegate.getDiagramRect().y + _delegate.getDiagramRect().height));
            }
            rowRect = new Rectangle(_delegate.getDiagramRect().x, y, getWidth() - _delegate.getDiagramRect().x,
                    markerHeight);
            _gridRenderer.drawRowBeforeIntervals(gc, _delegate, rowRect, row, selected, false);
        }

        // all intervals inside the bounds of the diagram plus the additional look ahead/lookBack time
        // if strictChecking is enabled
        JaretDate start;
        JaretDate end;
        if (_delegate.getStrictClipTimeCheck()) {
            start = _delegate.getStartDate();
            end = _delegate.getEndDate();
        } else {
            start = _delegate.getStartDate().copy().backMinutes(_delegate.getScrollLookBackMinutes());
            end = _delegate.getEndDate().copy().advanceMinutes(_delegate.getScrollLookForwardMinutes());
        }
        // use the clipping bounds to reduce the painted intervals
        // (when strict checking is enabled)
        if (gc.isClipped() && _delegate.getStrictClipTimeCheck()) {
            start = _delegate.dateForCoord(gc.getClipping().x);
            end = _delegate.dateForCoord(gc.getClipping().x + gc.getClipping().width);
        }
        List<Interval> intervalsUnfiltered = row.getIntervals(start, end);

        List<Interval> intervals = new ArrayList<Interval>();
        // apply filter on intervals if set
        if (_delegate.getIntervalFilter() != null) {
            for (Interval i : intervalsUnfiltered) {
                if (_delegate.getIntervalFilter().isInResult(i)) {
                    intervals.add(i);
                }
            }
        } else {
            intervals = intervalsUnfiltered;
        }

        for (Interval i : intervals) {
            TimeBarRenderer renderer = getRenderer(i.getClass());
            if (renderer == null) {
                throw new RuntimeException("No suitable renderer for " + i.getClass());
            }
            if (_delegate.getTimeBarViewState().getDrawOverlapping(row)) {
                RenderDelegate.drawIntervalHorizontal(_delegate, renderer, false, gc, y, i, null, row);
            } else {
                RenderDelegate.drawIntervalHorizontal(_delegate, renderer, false, gc, y, i, _delegate
                        .getOverlapStrategy().getOverlapInfo(row, i), row);
            }
        }
        // do AFTER interval row rendering
        if (_gridRenderer != null) {
            _gridRenderer.drawRowAfterIntervals(gc, _delegate, rowRect, row, selected, false);
        }
    }

    /**
     * Draw a single row (vertical).
     * 
     * @param gc GC
     * @param row row to paint
     * @param x left screen x for painting
     * @param selected true if the row is selected
     */
    private void drawRowVertical(GC gc, TimeBarRow row, int x, boolean selected) {
        int rowHeight = _delegate.getTimeBarViewState().getRowHeight(row);
        // first of all draw the row header
        if (row.getRowHeader() != null) {
            RenderDelegate.drawRowHeaderVertical(_delegate, _headerRenderer, false, gc, x, row.getRowHeader(),
                    selected, row);
        }

        // the draw the hierarchy display if configured
        if (_hierarchyRenderer != null && _delegate.getHierarchyWidth() > 0) {
            RenderDelegate.drawHierarchyVertical(_delegate, _hierarchyRenderer, false, gc, x, row, selected);
        }

        // row grid if configured
        if (_delegate.getDrawRowGrid()) {
            Color fg = gc.getForeground();
            gc.setForeground(_rowGridColor);
            gc.drawLine(x + rowHeight - 1, _delegate.getDiagramRect().y, x + rowHeight - 1,
                    _delegate.getDiagramRect().y + _delegate.getDiagramRect().height);
            gc.setForeground(fg);
        }

        // do rendering BEFORE intervals
        Rectangle rowRect = null;
        if (_gridRenderer != null) {
            int markerWidth = rowHeight;
            // calculate width for clipping
            if (x + markerWidth > _delegate.getDiagramRect().x + _delegate.getDiagramRect().width) {
                markerWidth = markerWidth
                        - (x + markerWidth - (_delegate.getDiagramRect().x + _delegate.getDiagramRect().width));
            }
            rowRect = new Rectangle(x, _delegate.getDiagramRect().y, markerWidth, getHeight()
                    - _delegate.getDiagramRect().y);
            _gridRenderer.drawRowBeforeIntervals(gc, _delegate, rowRect, row, selected, false);
        }

        // all intervals inside the bounds of the diagram plus the additional look ahead/lookBack time
        // if strictChecking is enabled
        JaretDate start;
        JaretDate end;
        if (_delegate.getStrictClipTimeCheck()) {
            start = _delegate.getStartDate();
            end = _delegate.getEndDate();
        } else {
            start = _delegate.getStartDate().copy().backMinutes(_delegate.getScrollLookBackMinutes());
            end = _delegate.getEndDate().copy().advanceMinutes(_delegate.getScrollLookForwardMinutes());
        }
        // use the clipping bounds to reduce the painted intervals
        // (when strict checking is enabled)
        if (gc.isClipped() && _delegate.getStrictClipTimeCheck()) {
            start = _delegate.dateForCoord(gc.getClipping().y);
            end = _delegate.dateForCoord(gc.getClipping().y + gc.getClipping().height);
        }
        List<Interval> intervalsUnfiltered = row.getIntervals(start, end);
        List<Interval> intervals = new ArrayList<Interval>();
        // apply filter on intervals if set
        if (_delegate.getIntervalFilter() != null) {
            for (Interval i : intervalsUnfiltered) {
                if (_delegate.getIntervalFilter().isInResult(i)) {
                    intervals.add(i);
                }
            }
        } else {
            intervals = intervalsUnfiltered;
        }

        for (Interval i : intervals) {
            TimeBarRenderer renderer = getRenderer(i.getClass());
            if (renderer == null) {
                throw new RuntimeException("no suitable renderer for class " + i.getClass());
            }
            if (_delegate.getTimeBarViewState().getDrawOverlapping(row)) {
                RenderDelegate.drawIntervalVertical(_delegate, renderer, false, gc, x, i, null, row);
            } else {
                RenderDelegate.drawIntervalVertical(_delegate, renderer, false, gc, x, i, _delegate
                        .getOverlapStrategy().getOverlapInfo(row, i), row);
            }
        }

        // row rendering after intervals
        if (_gridRenderer != null) {
            _gridRenderer.drawRowAfterIntervals(gc, _delegate, rowRect, row, selected, false);
        }

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
        if (_gridRenderer != null && _timeScaleRenderer != null && _timeScaleRenderer instanceof ITickProvider) {
            _gridRenderer.setTickProvider((ITickProvider) _timeScaleRenderer);
        }
        repaint();
    }

    /**
     * Set a global renderer doing rendering work other than oriented on one interval or row.
     * 
     * @param gar the renderer to be used
     */
    public void setGlobalAssistantRenderer(GlobalAssistantRenderer gar) {
        _globalRenderer = gar;
    }

    /**
     * Retrive the global assistant renderer if set.
     * 
     * @return global assistant renderer or null if not set
     */
    public GlobalAssistantRenderer getGlobalAssistantRenderer() {
        return _globalRenderer;
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
        repaint();
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
            // get and set the height the renderer needs
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
        repaint();
    }

    /**
     * Retrieve the current misc renderer.
     * 
     * @return the current misc renderer
     */
    public IMiscRenderer getMiscRenderer() {
        return _miscRenderer;
    }

    /**
     * Set the misc renderer to use.
     * 
     * @param miscRenderer the misc renderer to be used
     */
    public void setMiscRenderer(IMiscRenderer miscRenderer) {
        _miscRenderer = miscRenderer;
        repaint();
    }

    /**
     * Set a renderer to be used for renering markers.
     * 
     * @param markerRenderer marker renderer
     */
    public void setMarkerRenderer(TimeBarMarkerRenderer markerRenderer) {
        _markerRenderer = markerRenderer;
        repaint();
    }

    /**
     * Retrieve the used marker renderer.
     * 
     * @return the marker renderer
     */
    public TimeBarMarkerRenderer getMarkerRenderer() {
        return _markerRenderer;
    }

    /**
     * {@inheritDoc}
     */
    public int getMarkerWidth(TimeBarMarker marker) {
        if (_markerRenderer != null) {
            return _markerRenderer.getWidth(marker);
        } else {
            return 0;
        }
    }

    /**
     * Set a renderer for the hierachy elements. If the renderer announces a preferred width this will be set.
     * 
     * @param hierarchyRenderer the renderer to be used for hieryrchy painting
     */
    public void setHierarchyRenderer(HierarchyRenderer hierarchyRenderer) {
        _hierarchyRenderer = hierarchyRenderer;
        if (_hierarchyRenderer != null && _hierarchyRenderer.getPreferredWidth() != -1) {
            _delegate.setHierarchyWidth(_hierarchyRenderer.getPreferredWidth());
        }
    }

    /**
     * Retrieve the hierarchy renderer.
     * 
     * @return hierarchy renderer
     */
    public HierarchyRenderer getHierarchyRenderer() {
        return _hierarchyRenderer;
    }

    /**
     * @return Returns the headerRenderer.
     */
    public HeaderRenderer getHeaderRenderer() {
        return _headerRenderer;
    }

    /**
     * @param headerRenderer The headerRenderer to set.
     */
    public void setHeaderRenderer(HeaderRenderer headerRenderer) {
        _headerRenderer = headerRenderer;
        repaint();
    }

    /**
     * @return Returns the titleRenderer.
     */
    public TitleRenderer getTitleRenderer() {
        return _titleRenderer;
    }

    /**
     * @param titleRenderer The titleRenderer to set.
     */
    public void setTitleRenderer(TitleRenderer titleRenderer) {
        _titleRenderer = titleRenderer;
        repaint();
    }

    /**
     * Retrieve the relation renderer.
     * 
     * @return the releation renderer
     */
    public IRelationRenderer getRelationRenderer() {
        return _relationRenderer;
    }

    /**
     * Set the relation renderer. There is no relation render setup as a default.
     * 
     * @param relationRenderer renderer to use
     */
    public void setRelationRenderer(IRelationRenderer relationRenderer) {
        _relationRenderer = relationRenderer;
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    public boolean timeBarContains(Interval interval, java.awt.Rectangle intervalRect, int x, int y, boolean overlapping) {
        TimeBarRenderer renderer = getRenderer(interval.getClass());
        if (renderer == null) {
            throw new RuntimeException("no suitable renderer");
        }

        return renderer.contains(interval, convertRect(intervalRect), x, y, overlapping);
    }

    /**
     * {@inheritDoc}
     */
    public java.awt.Rectangle timeBarContainingRect(Interval interval, java.awt.Rectangle intervalRect,
            boolean overlapping) {
        TimeBarRenderer renderer = getRenderer(interval.getClass());
        if (renderer == null) {
            throw new RuntimeException("no suitable renderer");
        }
        return convertRect(renderer.getContainingRectangle(interval, convertRect(intervalRect), overlapping));
    }

    /**
     * {@inheritDoc}
     */
    public void setCursor(int cursorType) {
        cursorType = convertCursorType(cursorType);
        setCursor(Display.getCurrent().getSystemCursor(cursorType));
    }

    /**
     * Convert Swing cursor type to SWT-Cursor type.
     * 
     * @param cursorType swing cursor constant
     * @return swt cursor constant
     */
    private int convertCursorType(int cursorType) {
        int result = SWT.CURSOR_ARROW;
        switch (cursorType) {
        case Cursor.HAND_CURSOR:
            result = SWT.CURSOR_HAND;
            break;
        case Cursor.MOVE_CURSOR:
            result = SWT.CURSOR_SIZEALL;
            break;
        case Cursor.E_RESIZE_CURSOR:
            result = SWT.CURSOR_SIZEE;
            break;
        case Cursor.W_RESIZE_CURSOR:
            result = SWT.CURSOR_SIZEW;
            break;
        case Cursor.N_RESIZE_CURSOR:
            result = SWT.CURSOR_SIZEN;
            break;
        case Cursor.S_RESIZE_CURSOR:
            result = SWT.CURSOR_SIZES;
            break;

        default:
            break;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getIntervalToolTipText(Interval interval, java.awt.Rectangle intervalRect, int x, int y) {
        TimeBarRenderer renderer = getRenderer(interval.getClass());
        if (renderer == null) {
            throw new RuntimeException("no suitable renderer");
        }
        return renderer.getToolTipText(interval, convertRect(intervalRect), x, y, false);
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
    public void setPixelPerSecond(double pixPerSecond) {
        _delegate.setPixelPerSecond(pixPerSecond);
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
    public void setRowHeight(int rowHeight) {
        _delegate.setRowHeight(rowHeight);
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
     * Get the las date painted.
     * 
     * @return the last date that has been painted
     */
    public JaretDate getEndDate() {
        return _delegate.getEndDate();
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
    public int getTimeScalePosition() {
        return _delegate.getTimeScalePosition();
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
        return _delegate.dateForXY(x, y);
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
     * Set a filter to select a subset of rows in the model to be displayed. The model itself will go unaffected.
     * 
     * @param rowFilter TimeBarRowFilter to be used or null to disable row filtering
     */
    public void setRowFilter(TimeBarRowFilter rowFilter) {
        _delegate.setRowFilter(rowFilter);
    }

    /**
     * Get the used row filter.
     * 
     * @return the row filter or null.
     */
    public TimeBarRowFilter getRowFilter() {
        return _delegate.getRowFilter();
    }

    /**
     * Set an interval filter to select a subset of intervals that will be displayed. The model will be unaffected.
     * 
     * @param intervalFilter filter to use or null to disable filtering
     */
    public void setIntervalFilter(TimeBarIntervalFilter intervalFilter) {
        _delegate.setIntervalFilter(intervalFilter);
    }

    /**
     * Retrieve the used interval filter.
     * 
     * @return interval filter or null
     */
    public TimeBarIntervalFilter getIntervalFilter() {
        return _delegate.getIntervalFilter();
    }

    /**
     * Set a sorter for sorting the displayed rows. The model itself will not be affected.
     * 
     * @param rowSorter TimeBarRowSorter to be used or <code>null</code> to disable sorting
     */
    public void setRowSorter(TimeBarRowSorter rowSorter) {
        _delegate.setRowSorter(rowSorter);
    }

    /**
     * Retrive sorter used.
     * 
     * @return sorter or <code>null</code>
     */
    public TimeBarRowSorter getRowSorter() {
        return _delegate.getRowSorter();
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
    public String getTimeScaleToolTipText(int x, int y) {
        if (_timeScaleRenderer != null) {
            String tooltip = _timeScaleRenderer.getToolTipText(this, convertRect(_delegate.getXAxisRect()), x, y);
            return tooltip;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getHeaderToolTipText(TimeBarRow row, int x, int y) {
        if (_headerRenderer != null) {
            return _headerRenderer.getToolTipText(row, convertRect(_delegate.getYAxisRect()), x, y);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getHierarchyToolTipText(TimeBarNode node, int x, int y) {
        if (_hierarchyRenderer != null) {
            return _hierarchyRenderer.getToolTipText(node, convertRect(_delegate.getHierarchyRect()), x, y);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInToggleArea(TimeBarNode node, int xx, int yy) {
        int x = _delegate.getHierarchyRect().x;
        int width = _delegate.getHierarchyWidth() - 1;
        int rowHeight = _delegate.getTimeBarViewState().getRowHeight(node);
        int y = _delegate.yForRow(node);
        Rectangle drawingArea = new Rectangle(x, y, width, rowHeight);
        return _hierarchyRenderer.isInToggleArea(this, node, drawingArea, xx, yy);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInHierarchySelectionArea(TimeBarNode node, int xx, int yy) {
        int x = _delegate.getHierarchyRect().x;
        int width = _delegate.getHierarchyWidth() - 1;
        int rowHeight = _delegate.getTimeBarViewState().getRowHeight(node);
        int y = _delegate.yForRow(node);
        Rectangle drawingArea = new Rectangle(x, y, width, rowHeight);
        return _hierarchyRenderer.isInHierarchySelectionArea(this, node, drawingArea, xx, yy);
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
     * Helper method displaying a context menu.
     * 
     * @param contextMenu the menu to display
     * @param x x coordinate in the viewer
     * @param y y coordinate in the viewer
     */
    private void dispContextMenu(Menu contextMenu, int x, int y) {
        // System.out.println("x,y:" + x + "," + y + " locy " +
        // getLocation().y);
        Shell shell = Display.getCurrent().getActiveShell();
        if (shell != null && contextMenu != null) {
        	Point coords = Display.getCurrent().map(this, shell, x, y);
            contextMenu.setLocation(coords.x + shell.getLocation().x, coords.y + shell.getLocation().y);
            contextMenu.setVisible(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void displayBodyContextMenu(int x, int y) {
        if (_bodyContextMenu != null) {
            dispContextMenu(_bodyContextMenu, x, y);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void displayTimeScaleContextMenu(int x, int y) {
        if (_scaleContextMenu != null) {
            dispContextMenu(_scaleContextMenu, x, y);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void displayIntervalContextMenu(Interval interval, int x, int y) {
        if (_intervalContextMenu != null) {
            dispContextMenu(_intervalContextMenu, x, y);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void displayHeaderContextMenu(TimeBarRow row, int x, int y) {
        if (_headerCtxHandler != null) {
            Menu ctxMenu = _headerCtxHandler.getContextMenu(this, row);
            if (ctxMenu != null) {
                dispContextMenu(ctxMenu, x, y);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void displayHierarchyContextMenu(TimeBarRow row, int x, int y) {
        if (_hierarchyCtxHandler != null) {
            Menu ctxMenu = _hierarchyCtxHandler.getContextMenu(this, row);
            if (ctxMenu != null) {
                dispContextMenu(ctxMenu, x, y);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void displayTitleContextMenu(int x, int y) {
        if (_titleContextMenu != null) {
            dispContextMenu(_titleContextMenu, x, y);
        }
    }

    /**
     * @return Returns the bodyContextMenu.
     */
    public Menu getBodyContextMenu() {
        return _bodyContextMenu;
    }

    /**
     * @param bodyContextMenu The bodyContextMenu to set.
     */
    public void setBodyContextMenu(Menu bodyContextMenu) {
        _bodyContextMenu = bodyContextMenu;
    }

    /**
     * @return Returns the intervalContextMenu.
     */
    public Menu getIntervalContextMenu() {
        return _intervalContextMenu;
    }

    /**
     * Set the interval context menu.
     * 
     * @param intervalContextMenu context menu to set
     */
    public void setIntervalContextMenu(Menu intervalContextMenu) {
        _intervalContextMenu = intervalContextMenu;
    }

    /**
     * @return Returns the scaleContextMenu.
     */
    public Menu getScaleContextMenu() {
        return _scaleContextMenu;
    }

    /**
     * @param scaleContextMenu The scaleContextMenu to set.
     */
    public void setScaleContextMenu(Menu scaleContextMenu) {
        _scaleContextMenu = scaleContextMenu;
    }

    /**
     * @return Returns the titleContextMenu.
     */
    public Menu getTitleContextMenu() {
        return _titleContextMenu;
    }

    /**
     * @param titleContextMenu The titleContextMenu to set.
     */
    public void setTitleContextMenu(Menu titleContextMenu) {
        _titleContextMenu = titleContextMenu;
    }

    /**
     * @return Returns the headerCtxHandler.
     */
    public RowContextMenuHandler getHeaderCtxHandler() {
        return _headerCtxHandler;
    }

    /**
     * @param headerCtxHandler The headerCtxHandler to set.
     */
    public void setHeaderCtxHandler(RowContextMenuHandler headerCtxHandler) {
        _headerCtxHandler = headerCtxHandler;
    }

    /**
     * @return Returns the hierarchyCtxHandler.
     */
    public RowContextMenuHandler getHierarchyCtxHandler() {
        return _hierarchyCtxHandler;
    }

    /**
     * @param hierarchyCtxHandler The hierarchyCtxHandler to set.
     */
    public void setHierarchyCtxHandler(RowContextMenuHandler hierarchyCtxHandler) {
        _hierarchyCtxHandler = hierarchyCtxHandler;
    }

    /**
     * Retrieve the keyboardChangeDelta currently used.
     * 
     * @return the keyboardChangeDelta in seconds
     */
    public int getKeyboardChangeDelta() {
        return _delegate.getKeyboardChangeDelta();
    }

    /**
     * Set the delta for resizing and moving via keyboard.
     * 
     * @param keyboardChangeDelta the keyboardChangeDelta in seconds to set
     */
    public void setKeyboardChangeDelta(int keyboardChangeDelta) {
        _delegate.setKeyboardChangeDelta(keyboardChangeDelta);
    }

    /**
     * Set the new focussed interval.
     * 
     * @param interval new focussed interval
     */
    public void setFocussedInterval(Interval interval) {
        _delegate.setFocussedInterval(interval);
    }

    /**
     * Set the new focussed interval. Ths method should be used, if the row of the interval is known.
     * 
     * @param row row of the interval. May be <code>null</code> if the row of the interval is unknown.
     * @param interval interval to be focussed.
     */
    public void setFocussedInterval(TimeBarRow row, Interval interval) {
        _delegate.setFocussedInterval(row, interval);
    }

    /**
     * Retrieve the focussed interval.
     * 
     * @return the currently focussed interval or null if none is in focus
     */
    public Interval getFocussedInterval() {
        return _delegate.getFocussedInterval();
    }

    /**
     * @return the currently focussed row or null if none is in focus
     */
    public TimeBarRow getFocussedRow() {
        return _delegate.getFocussedRow();
    }

    /**
     * Check whether an interval is focussed.
     * 
     * @param interval interval to check
     * @return true if focussed
     */
    public boolean isFocussed(Interval interval) {
        return _delegate.isFocussed(interval);
    }

    /**
     * Scroll a date into the visible area of the viewer.
     * 
     * @param date date to be shown.
     */
    public void scrollDateToVisible(JaretDate date) {
        _delegate.scrollDateToVisible(date);
    }

    /**
     * Make sure the specified row is visible.
     * 
     * @param row TimeBarRow to be in the visible area.
     */
    public void scrollRowToVisible(TimeBarRow row) {
        _delegate.scrollRowToVisible(row);
    }

    /**
     * Make sure the specified interval is in the visibe area of the viewer. If the interval does not fit in the visible
     * area, the beginning of the interval will be displayed.
     * 
     * @param row TimeBarRow of the interval
     * @param interval inteval.
     */
    public void scrollIntervalToVisible(TimeBarRow row, Interval interval) {
        _delegate.scrollIntervalToVisible(row, interval);
    }

    /**
     * Make sure the specified interval is in the visibe area of the viewer. If the interval does not fit in the visible
     * area, the beginning of the interval will be displayed.
     * 
     * @param interval interval
     */
    public void scrollIntervalToVisible(Interval interval) {
        _delegate.scrollIntervalToVisible(interval);
    }

    /**
     * Scroll an intervall into the visible area on a position specified by a ratio. The position according to the ratio
     * will be set for the upper row border and the begin of the interval. The horizontal position might be calculated
     * incorrectly when a variable x axis is used.
     * 
     * @param interval interval to scroll to
     * @param horizontalRatio ration between 0 and 1.0 (left to right)
     * @param verticalRatio ratio between 0 and 1.0 (top to bottom)
     */
    public void scrollIntervalToVisible(Interval interval, double horizontalRatio, double verticalRatio) {
        _delegate.scrollIntervalToVisible(interval, horizontalRatio, verticalRatio);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addSelectionChangedListener(ISelectionChangedListener listener) {
        if (_selectionChangeListeners == null) {
            _selectionChangeListeners = new Vector<ISelectionChangedListener>();
        }
        _selectionChangeListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        if (_selectionChangeListeners != null) {
            _selectionChangeListeners.remove(listener);
        }
    }

    /**
     * {@inheritDoc} Will return a structured selection conaining selected rows and selected intervals.
     */
    public ISelection getSelection() {
        return getStrucuredSelection();
    }

    /**
     * {@inheritDoc} Will process a structured selection containing rows and intervals.
     */
    public void setSelection(ISelection selection) {
        setStructuredSelection(selection);
    }

    // ************** support
    /**
     * Create a StructuredSelection from the current selection of the viewer.
     * 
     * @return structured selection containing rows and intervals
     */
    @SuppressWarnings("unchecked")
    private ISelection getStrucuredSelection() {
        TimeBarSelectionModel tmSelection = getSelectionModel();
        if (tmSelection != null && !tmSelection.isEmpty()) {
            List list = new ArrayList();
            list.addAll(tmSelection.getSelectedRows());
            list.addAll(tmSelection.getSelectedIntervals());
            list.addAll(tmSelection.getSelectedRelations());
            StructuredSelection selection = new StructuredSelection(list);
            return selection;
        }
        return new StructuredSelection();
    }

    /**
     * Set the selection in the timebar viewer according to a structured selection.
     * 
     * @param selection structured selection to set
     */
    private void setStructuredSelection(ISelection selection) {
        TimeBarSelectionModel tmSelection = new TimeBarSelectionModelImpl();
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structured = (IStructuredSelection) selection;
            for (Object o : structured.toList()) {
                if (o instanceof Interval) {
                    tmSelection.addSelectedInterval((Interval) o);
                } else if (o instanceof TimeBarRow) {
                    tmSelection.addSelectedRow((TimeBarRow) o);
                } else if (o instanceof IIntervalRelation) {
                    tmSelection.addSelectedRelation((IIntervalRelation) o);
                } else {
                    throw new IllegalArgumentException("Type " + o.getClass().getName()
                            + " not supported for selection");
                }
            }
        }
        setSelectionModel(tmSelection);

    }

    /**
     * Callback for delegate. This is not an ideal design but it will be ok to check in the delegate.
     */
    public void fireSelectionChanged() {
        SelectionChangedEvent evt = new SelectionChangedEvent(this, getStrucuredSelection());
        fireSelectionChanged(evt);
    }

    /**
     * Inform ISelection listeners about a change in the selection.
     * 
     * @param event event to be sent to the listeners.
     */
    private void fireSelectionChanged(SelectionChangedEvent event) {
        if (_selectionChangeListeners != null) {
            for (ISelectionChangedListener listener : _selectionChangeListeners) {
                listener.selectionChanged(event);
            }
        }
    }

    // *************** ISelectionProvider

    /**
     * Retrieve a configured action factory.
     * 
     * @return a configured action factory
     */
    public JaretTimeBarsActionFactory getActionFactory() {
        if (_actionFactory == null) {
            _actionFactory = new JaretTimeBarsActionFactory(this, _delegate);
        }
        return _actionFactory;
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
     * Retrieves all intervals at a given point in the diagram pane.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return List of all intervals at the point
     */
    public List<Interval> getIntervalsAt(int x, int y) {
        return _delegate.getIntervalsAt(x, y);
    }

    /**
     * Retrieve color for the row grid.
     * 
     * @return Color of the row grid
     */
    public Color getRowGridColor() {
        return _rowGridColor;
    }

    /**
     * Set the color for the row grid. The color will not be disposed by the viewer.
     * 
     * @param rowGridColor Color to be used for horizontal grid lines
     */
    public void setRowGridColor(Color rowGridColor) {
        _rowGridColor = rowGridColor;
    }

    /**
     * retrieve the color used for highlighting rows.
     * 
     * @return the highlight color
     * @deprecated use the default grid renderer directly (or your own grid renderer)
     */
    public Color getHighlightColor() {
        if (_gridRenderer != null && _gridRenderer instanceof AbstractGridRenderer) {
            AbstractGridRenderer renderer = (AbstractGridRenderer) _gridRenderer;
            return renderer.getHighlightColor();
        } else {
            throw new RuntimeException("not applicable since the grid renderer is not an AbstractGridRenderer");
        }
    }

    /**
     * Set the color for highlighting a row. The color will not be disposed by the viewer. Highlighting is done with the
     * methods <code>highlightRow</code> and <code>deHighlightRow</code>.
     * 
     * @param highlightColor color to be used for highlighting rows
     * @deprecated use the default grid renderer directly (or your own grid renderer)
     */
    public void setHighlightColor(Color highlightColor) {
        if (_gridRenderer != null && _gridRenderer instanceof AbstractGridRenderer) {
            AbstractGridRenderer renderer = (AbstractGridRenderer) _gridRenderer;
            renderer.setHighlightColor(highlightColor);
        } else {
            throw new RuntimeException("not applicable since the grid renderer is not an AbstractGridRenderer");
        }
    }

    /**
     * Retrieve the color used for marking selected rows.
     * 
     * @return color used for selecteted rows
     * @deprecated use the default grid renderer directly (or your own grid renderer)
     */
    public Color getRowSelectColor() {
        if (_gridRenderer != null && _gridRenderer instanceof AbstractGridRenderer) {
            AbstractGridRenderer renderer = (AbstractGridRenderer) _gridRenderer;
            return renderer.getRowSelectColor();
        } else {
            throw new RuntimeException("not applicable since the grid renderer is not an AbstractGridRenderer");
        }
    }

    /**
     * Set the color for drawing selected rows. The color will not be disposed by the viewer.
     * 
     * @param rowSelectColor color to be used to select rows
     * @deprecated use the default grid renderer directly (or your own grid renderer)
     */
    public void setRowSelectColor(Color rowSelectColor) {
        if (_gridRenderer != null && _gridRenderer instanceof AbstractGridRenderer) {
            AbstractGridRenderer renderer = (AbstractGridRenderer) _gridRenderer;
            renderer.setRowSelectColor(rowSelectColor);
        } else {
            throw new RuntimeException("not applicable since the grid renderer is not an AbstractGridRenderer");
        }
    }

    /**
     * Get the alpha used when drawing the highlighted row.
     * 
     * @return alpha for drawing the hightlight
     * @deprecated use the default grid renderer directly (or your own grid renderer)
     */
    public int getHighlightAlpha() {
        if (_gridRenderer != null && _gridRenderer instanceof AbstractGridRenderer) {
            AbstractGridRenderer renderer = (AbstractGridRenderer) _gridRenderer;
            return renderer.getHighlightAlpha();
        } else {
            throw new RuntimeException("not applicable since the grid renderer is not an AbstractGridRenderer");
        }
    }

    /**
     * Set the alpha value used for drawing the highlighted row.
     * 
     * @param highlightAlpha alpha to use
     * @deprecated use the default grid renderer directly (or your own grid renderer)
     */
    public void setHighlightAlpha(int highlightAlpha) {
        if (_gridRenderer != null && _gridRenderer instanceof AbstractGridRenderer) {
            AbstractGridRenderer renderer = (AbstractGridRenderer) _gridRenderer;
            renderer.setHighlightAlpha(highlightAlpha);
        } else {
            throw new RuntimeException("not applicable since the grid renderer is not an AbstractGridRenderer");
        }
    }

    /**
     * Get the alpha used when drawing ghosted intervals.
     * 
     * @return alpha for drawing ghost intervals
     */
    public int getGhostAlpha() {
        return _ghostAlpha;
    }

    /**
     * Set the alpha value used for drawing ghost intervals.
     * 
     * @param ghostAlpha alpha to use
     */
    public void setGhostAlpha(int ghostAlpha) {
        _ghostAlpha = ghostAlpha;
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

    /** list of ghost intervals to be painted. */
    protected List<Interval> _ghostIntervals;
    /** y offsets for the ghost intervals. */
    protected List<Integer> _ghostIntervalYCoordinates;
    /**
     * the origin for painting the ghost intervals/rows. The ghosted elements will paintetd relative to the y
     * coordinate.
     */
    protected Point _ghostOrigin;
    /** list of ghost rows to paint. */
    protected List<TimeBarRow> _ghostRows;
    /** y offsets for the ghost rows. */
    protected List<Integer> _ghostRowYCoordinates;

    /**
     * Set the list of ghost intervals to be drawn.
     * 
     * @param intervals list of intervals or <code>null</code> to delete ghosted intervals.
     * @param yCoordinates list of y offsets for the ghost intervals (maybe <code>null</code> when deleting ghost
     * intervals
     */
    public void setGhostIntervals(List<Interval> intervals, List<Integer> yCoordinates) {
        _ghostIntervals = intervals;
        _ghostIntervalYCoordinates = yCoordinates;
        redraw();
    }

    /**
     * Convenience method to set a single ghost interval.
     * 
     * @param interval interval or <code>null</code> to delete
     * @param y y offset
     */
    public void setGhostInterval(Interval interval, int y) {
        if (interval == null) {
            setGhostIntervals(null, null);
        } else {
            List<Interval> l = new ArrayList<Interval>(1);
            List<Integer> l2 = new ArrayList<Integer>(1);
            l.add(interval);
            l2.add(y);
            setGhostIntervals(l, l2);
        }
    }

    /**
     * Set the list of ghost rows to be drawn.
     * 
     * @param rows list of rows or <code>null</code> to delete ghosted rows.
     * @param yCoordinates list of y offsets for the ghost rows (maybe <code>null</code> when deleting ghost rows
     */
    public void setGhostRows(List<TimeBarRow> rows, List<Integer> yCoordinates) {
        _ghostRows = rows;
        _ghostRowYCoordinates = yCoordinates;
        redraw();
    }

    /**
     * Convenience method to set a single ghost row.
     * 
     * @param row row or <code>null</code> to delete
     * @param y y offset
     */
    public void setGhostRow(TimeBarRow row, int y) {
        if (row == null) {
            setGhostRows(null, null);
        } else {
            List<TimeBarRow> l = new ArrayList<TimeBarRow>(1);
            List<Integer> l2 = new ArrayList<Integer>(1);
            l.add(row);
            l2.add(y);
            setGhostRows(l, l2);
        }
    }

    /**
     * Set the origin (current drag position) to shift the ghost elements.
     * 
     * @param x x coordinate
     * @param y y coordniate
     */
    public void setGhostOrigin(int x, int y) {
        _ghostOrigin = new Point(x, y);
        if (_ghostIntervals != null || _ghostRows != null) {
            redraw();
        }
    }

    /**
     * Draw ghost intervals and rows.
     * 
     * @param gc GC
     */
    private void drawGhosts(GC gc) {
        drawGhostIntervals(gc);
        drawGhostRows(gc);
    }

    /**
     * Draw ghost intervals. Y positions are dependant from the ghost y offsets and the ghost origin. (Always uses the
     * defaultRowHeight.)
     * 
     * @param gc GC
     */
    private void drawGhostIntervals(GC gc) {
        if (_ghostOrigin != null && _ghostIntervals != null) {
            for (int i = 0; i < _ghostIntervals.size(); i++) {
                Interval interval = _ghostIntervals.get(i);
                int yoff = _ghostIntervalYCoordinates.get(i);

                Rectangle drawingArea = convertRect(_delegate.getIntervalBounds(-1, interval));
                if (_delegate.getOrientation().equals(Orientation.HORIZONTAL)) {
                    drawingArea.y = _ghostOrigin.y + yoff;
                    drawingArea.height = _delegate.getTimeBarViewState().getDefaultRowHeight();
                } else {
                    drawingArea.x = _ghostOrigin.x + yoff;
                    drawingArea.width = _delegate.getTimeBarViewState().getDefaultRowHeight();
                }
                int alpha = gc.getAlpha();
                gc.setAlpha(_ghostAlpha);

                TimeBarRenderer renderer = getRenderer(interval.getClass());
                if (renderer == null) {
                    throw new RuntimeException("no suitable renderer");
                }

                renderer.draw(gc, drawingArea, _delegate, interval, false, false, false);
                gc.setAlpha(alpha);
            }
        }
    }

    /**
     * Draw ghost rows. Y positions are dependent from the ghost y offsets and the ghost origin.
     * 
     * @param gc GC
     */
    private void drawGhostRows(GC gc) {
        if (_ghostOrigin != null && _ghostRows != null) {
            int alpha = gc.getAlpha();
            gc.setAlpha(_ghostAlpha);
            for (int i = 0; i < _ghostRows.size(); i++) {
                TimeBarRow row = _ghostRows.get(i);
                int yoff = _ghostRowYCoordinates.get(i);

                if (_delegate.getOrientation().equals(Orientation.HORIZONTAL)) {
                    int y = _ghostOrigin.y + yoff;
                    drawRowHorizontal(gc, row, y, false);
                } else {
                    int x = _ghostOrigin.x + yoff;
                    drawRowVertical(gc, row, x, false);
                }
            }
            gc.setAlpha(alpha);
        }
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
     * {@inheritDoc}
     */
    public void doScrollHorizontal(int diff) {
        java.awt.Rectangle d = new java.awt.Rectangle(_delegate.getDiagramRect());
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
            scroll(d.x, d.y, d.x + diff, d.y, d.width - diff, d.height, false);
        } else {
            diff = -diff;
            scroll(d.x + diff, d.y, d.x, d.y, d.width - diff, d.height, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void doScrollVertical(int diff) {
        java.awt.Rectangle d = new java.awt.Rectangle(_delegate.getDiagramRect());
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
            scroll(d.x, d.y, d.x, d.y + diff, d.width, d.height - diff, false);
        } else {
            diff = -diff;
            scroll(d.x, d.y + diff, d.x, d.y, d.width, d.height - diff, false);
        }
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
     * Retrieve the bounding rect of an interval. ATTENTION: this uses the row for interval lookup in the model that may
     * be imperformant.
     * 
     * @param interval interval
     * @return the bounding rect or null
     */
    public Rectangle getIntervalBounds(Interval interval) {
        return convertRect(_delegate.getIntervalBounds(interval));
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
        return _delegate.getName();
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
    public void setInitialDisplayRange(JaretDate startDate, int secondsDisplayed){
    	_delegate.setInitialDisplayRange(startDate, secondsDisplayed);
    }

    /**
     * {@inheritDoc}
     */
    public Pair<TimeBarRow, JaretDate> getPopUpInformation() {
        return _delegate.getPopUpInformation();
    }

}
