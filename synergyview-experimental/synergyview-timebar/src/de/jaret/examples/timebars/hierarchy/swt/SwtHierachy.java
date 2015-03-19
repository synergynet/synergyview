/*
 *  File: SwtHierachy.java 
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
package de.jaret.examples.timebars.hierarchy.swt;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import de.jaret.examples.timebars.hierarchy.model.DemoTimeBarNode;
import de.jaret.examples.timebars.hierarchy.model.ModelCreator;
import de.jaret.examples.timebars.pdi.swt.SwtControlPanel;
import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.date.holidayenumerator.HolidayEnumeratorFactory;
import de.jaret.util.ui.ResourceImageDescriptor;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.mod.DefaultIntervalModificator;
import de.jaret.util.ui.timebars.model.AddingTimeBarRowModel;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarNode;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.HierarchicalTimeBarModel;
import de.jaret.util.ui.timebars.model.ISelectionRectListener;
import de.jaret.util.ui.timebars.model.PPSInterval;
import de.jaret.util.ui.timebars.model.TBRect;
import de.jaret.util.ui.timebars.model.TimeBarNode;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.RowContextMenuHandler;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.BoxTimeScaleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultGridRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultHierarchyRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultTitleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.RelationRenderer;
import de.jaret.util.ui.timebars.swt.util.actions.JaretTimeBarsActionFactory;

/**
 * Simple hierarchical view, SWT version. Scaling, manipulating the intervals, tree structure. The sum interval is
 * rendered by a specialized sum renderer. The arrows beetwenn the intervals are a demonstration of the relation
 * renderer that in fact is a global assistance renderer. Context menus on scale and hierarchy. Hierarchy using label
 * provider including icons.
 * <p>
 * The example also demonstrates the usage of a variable xaxis definition. To see that version set _variableXAxis to
 * true.
 * </p>
 * 
 * @author Peter Kliem
 * @version $Id: SwtHierachy.java 861 2009-05-07 20:19:06Z kliem $
 */
public class SwtHierachy extends ApplicationWindow {
    // if set to true a variable xaxis scaling will be used
    private static final boolean VARIABLE_XAXIS = false;

    static TimeBarViewer _tbv;

    /**
     * If set to true, DND of nodes will be supported (for testing). This disables drawing of references.
     */
    private static final boolean SUPPORT_DND = false;

    public SwtHierachy() {
        super(null);
    }

    protected Control createContents(Composite parent) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        parent.setLayout(gridLayout);

        // create the model
        HierarchicalTimeBarModel model = ModelCreator.createModel(2, 5);

        _tbv = new TimeBarViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        _tbv.setLayoutData(gd);

        _tbv.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);

        _tbv.setModel(model);
        
        // test initial range
        //_tbv.setInitialDisplayRange(new JaretDate().advanceDays(1), 120);
        
        _tbv.setPixelPerSecond(0.00144);

        // configure the title renderer with a background image and set the title
        DefaultTitleRenderer titleRenderer = new DefaultTitleRenderer();
        titleRenderer.setBackgroundRscName("/de/jaret/examples/timebars/hierarchy/swt/titlebg.png");
        _tbv.setTitleRenderer(titleRenderer);
        _tbv.setTitle("SwtHierarchy");

        // variable xaxis demonstration
        if (VARIABLE_XAXIS) {
            _tbv.setVariableXScale(true);
            DefaultTimeBarNode scaleRow = (DefaultTimeBarNode) _tbv.getPpsRow();
            scaleRow.setLevel(1);
            model.getRootNode().addNode(scaleRow);
            PPSInterval pps1 = new PPSInterval(_tbv.getPixelPerSecond() / 2);
            pps1.setBegin(new JaretDate());
            pps1.setEnd(new JaretDate().advanceDays(1));
            scaleRow.addInterval(pps1);

            PPSInterval pps2 = new PPSInterval(_tbv.getPixelPerSecond() / 4);
            pps2.setBegin(new JaretDate().advanceDays(3));
            pps2.setEnd(new JaretDate().advanceDays(5));
            scaleRow.addInterval(pps2);
        }

        // create and configure hierarchy renderer
        DefaultHierarchyRenderer hrenderer = new DefaultHierarchyRenderer();
        hrenderer.setLabelProvider(new LabelProvider());
        hrenderer.setDrawLabels(true);
        hrenderer.setDrawIcons(true);
        hrenderer.setLevelWidth(25);
        hrenderer.setFixedLevelWidth(true);
        hrenderer.setRscNames("/de/jaret/examples/timebars/hierarchy/swt/collapsed.png",
                "/de/jaret/examples/timebars/hierarchy/swt/expanded.png",
                "/de/jaret/examples/timebars/hierarchy/swt/leaf.png");
        hrenderer.setSize(16);
        _tbv.setHierarchyRenderer(hrenderer);
        _tbv.setHierarchyWidth(200);

        // allow interval modifications using the default modificator
        _tbv.addIntervalModificator(new DefaultIntervalModificator());

        // register sum renderer for merged intervals
        _tbv.registerTimeBarRenderer(AddingTimeBarRowModel.MergedInterval.class, new SumRenderer());

        // create and configure a grid renderer
        DefaultGridRenderer gridRenderer = new DefaultGridRenderer();
        gridRenderer.setHolidayEnumerator(HolidayEnumeratorFactory.getHolidayEnumeratorInstance(Locale.getDefault(),
                "NRW"));
        _tbv.setGridRenderer(gridRenderer);

        // time scale renderer
        // DefaultTimeScaleRenderer scaleRenderer = new DefaultTimeScaleRenderer();
        // scaleRenderer.setHolidayEnumerator(HolidayEnumeratorFactory.getHolidayEnumeratorInstance(Locale.getDefault(),
        // "NRW"));
        // _tbv.setTimeScaleRenderer(scaleRenderer);

        _tbv.setTimeScaleRenderer(new BoxTimeScaleRenderer());

        final RelationRenderer relationRenderer = new RelationRenderer();
        if (!SUPPORT_DND) {
            _tbv.setRelationRenderer(relationRenderer);
        }
        // sample time bar marker
        TimeBarMarkerImpl tm = new TimeBarMarkerImpl(true, new JaretDate().advanceDays(1));
        tm.setDescription("TimeBarMarker");
        _tbv.addMarker(tm);

        // control panel
        SwtControlPanel ctrl = new SwtControlPanel(parent, SWT.NULL, _tbv, null);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        ctrl.setLayoutData(gd);

        // hierarchy control panel
        SwtHierarchyControlPanel ctrl2 = new SwtHierarchyControlPanel(parent, SWT.NULL, _tbv);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        ctrl2.setLayoutData(gd);

        // hierarchy control panel
        SwtRelationRendererControlPanel ctrl3 = new SwtRelationRendererControlPanel(parent, SWT.NULL, _tbv);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        ctrl3.setLayoutData(gd);

        // context menues
        // timescale context menu
        MenuManager mm = new MenuManager();
        mm.add(_tbv.getActionFactory().createStdAction(JaretTimeBarsActionFactory.ACTION_SCALETOWEEK));
        mm.add(_tbv.getActionFactory().createStdAction(JaretTimeBarsActionFactory.ACTION_SCALETOMONTH));
        mm.add(_tbv.getActionFactory().createStdAction(JaretTimeBarsActionFactory.ACTION_SCALETOYEAR));
        mm.add(_tbv.getActionFactory().createStdAction(JaretTimeBarsActionFactory.ACTION_CENTERSCALE));
        _tbv.setScaleContextMenu(mm.createContextMenu(_tbv));

        // row context menu
        RowContextMenuHandler hierarchyCtxHandler = new HCtxHandler();
        _tbv.setHierarchyCtxHandler(hierarchyCtxHandler);

        // expand all nodes
        TimeBarNode root = model.getRootNode();
        _tbv.getHierarchicalViewState().setExpandedRecursive(root, true);

        // Zoom action and rect region selection
        _tbv.setRegionRectEnable(true); // enable region selections

        // set up the body context menu
        MenuManager bodyMM = new MenuManager();
        bodyMM.add(new ZoomAction(_tbv));
        bodyMM.add(new ClearRegionAction(_tbv));
        Menu bodyContextMenu = bodyMM.createContextMenu(_tbv);
        _tbv.setBodyContextMenu(bodyContextMenu);

        
        if (SUPPORT_DND) {
            initDND(_tbv, parent);
        }

        // Interval modificator preventing intervals from beeing overlapped by shifting or sizing
        // _tbv.addIntervalModificator(new DefaultIntervalModificator() {
        // 
        //            
        // @Override
        // public boolean newBeginAllowed(TimeBarRow row, Interval interval, JaretDate newBegin) {
        // boolean result = true;
        // for (Interval i : row.getIntervals()) {
        // if (i != interval && i.contains(newBegin)) {
        // result = false;
        // break;
        // }
        // }
        //                
        // return result;
        // }
        // @Override
        // public boolean newEndAllowed(TimeBarRow row, Interval interval, JaretDate newBegin) {
        // boolean result = true;
        // for (Interval i : row.getIntervals()) {
        // if (i != interval && i.contains(newBegin)) {
        // result = false;
        // break;
        // }
        // }
        //                
        // return result;
        // }
        //            
        // @Override
        // public boolean shiftAllowed(TimeBarRow row, Interval interval, JaretDate newBegin) {
        // boolean result = true;
        // for (Interval i : row.getIntervals()) {
        // if (i != interval && i.contains(newBegin)) {
        // result = false;
        // break;
        // }
        // }
        //                
        // return result;
        // }
        //            
        // @Override
        // public boolean isApplicable(TimeBarRow row, Interval interval) {
        // return true;
        // }
        //
        // });

        // add an Iselectionrectlistener for demonstration purposes
        // _tbv.addSelectionRectListener(new ISelectionRectListener() {
        //
        // @Override
        // public void selectionRectChanged(TimeBarViewerDelegate delegate, JaretDate beginDate, JaretDate endDate,
        // List<TimeBarRow> rows) {
        // System.out.println("SelRectChanged: "+beginDate.toDisplayString()+"--"+endDate.toDisplayString()+" rows:");
        // for (TimeBarRow timeBarRow : rows) {
        // System.out.println(timeBarRow.getRowHeader().getLabel());
        // }
        // }
        //
        // @Override
        // public void selectionRectClosed(TimeBarViewerDelegate delegate) {
        // System.out.println("SelRectClosed");
        // }
        //
        //        
        // @Override
        // public void regionRectClosed(TimeBarViewerDelegate delegate) {
        // System.out.println("region rect closed");
        // }
        //
        // @Override
        // public void regionRectChanged(TimeBarViewerDelegate delegate, TBRect tbrect) {
        // System.out.println("region rect changed");
        // }
        //            
        // });

        return _tbv;
    }

    /**
     * Action that zooms the timeline to the selection if present. It listens to changes on the region selection to
     * determine its enablement.
     * 
     * @author kliem
     * @version $Id: SwtHierachy.java 861 2009-05-07 20:19:06Z kliem $
     */
    public class ZoomAction extends Action implements ISelectionRectListener {
        private TimeBarViewer _tbv;

        public ZoomAction(TimeBarViewer tbv) {
            _tbv = tbv;
            setEnabled(false);
            _tbv.addSelectionRectListener(this);
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            if (_tbv.getRegionRect() != null) {
                TBRect tbrect = _tbv.getRegionRect();
                JaretDate startDate = tbrect.startDate;
                int seconds = tbrect.endDate.diffSeconds(tbrect.startDate);
                int pixel = _tbv.getDelegate().getDiagramRect().width;
                double pps = ((double) pixel) / ((double) seconds);
                _tbv.clearRegionRect();
                _tbv.setPixelPerSecond(pps);
                _tbv.setStartDate(startDate);
                // TODO row scaling
            }
        }

        /**
         * {@inheritDoc}
         */
        public String getText() {
            return "Zoom to region";
        }

        public void regionRectChanged(TimeBarViewerDelegate delegate, TBRect tbrect) {
            setEnabled(true);
        }

        public void regionRectClosed(TimeBarViewerDelegate delegate) {
            setEnabled(false);
        }

        public void selectionRectChanged(TimeBarViewerDelegate delegate, JaretDate beginDate, JaretDate endDate,
                List<TimeBarRow> rows) {
            // TODO Auto-generated method stub

        }

        public void selectionRectClosed(TimeBarViewerDelegate delegate) {
            // TODO Auto-generated method stub
        }
    }

    /**
     * Action that clears the region rect.
     * 
     * @author kliem
     * @version $Id: SwtHierachy.java 861 2009-05-07 20:19:06Z kliem $
     */
    public class ClearRegionAction extends Action {
        private TimeBarViewer _tbv;

        public ClearRegionAction(TimeBarViewer tbv) {
            _tbv = tbv;
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            _tbv.clearRegionRect();
        }

        /**
         * {@inheritDoc}
         */
        public String getText() {
            return "Clear region";
        }

    }

    public class HCtxHandler implements RowContextMenuHandler {
        Menu ctx;

        public HCtxHandler() {
            MenuManager mm = new MenuManager();
            mm.add(_tbv.getActionFactory().createStdAction(JaretTimeBarsActionFactory.ACTION_EXPANDNODE));
            mm.add(_tbv.getActionFactory().createStdAction(JaretTimeBarsActionFactory.ACTION_COLLAPSENODE));
            mm.add(_tbv.getActionFactory().createStdAction(JaretTimeBarsActionFactory.ACTION_EXPANDALL));
            mm.add(_tbv.getActionFactory().createStdAction(JaretTimeBarsActionFactory.ACTION_COLLAPSEALL));
            ctx = mm.createContextMenu(_tbv);
        }

        public Menu getContextMenu(TimeBarViewer tbv, TimeBarRow row) {
            return ctx;
        }
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(getClass().getName());
        shell.setSize(800, 800);
    }

    public static void main(String[] args) {
        SwtHierachy test = new SwtHierachy();
        test.setBlockOnOpen(true);
        test.open();
    }

    public class LabelProvider implements ILabelProvider {
        ImageRegistry _imageRegistry;

        public Image getImage(Object element) {
            if (element instanceof DemoTimeBarNode) {
                DemoTimeBarNode node = (DemoTimeBarNode) element;
                return node.isType() ? getImageRegistry().get("true") : getImageRegistry().get("false");
            } else {
                return getImageRegistry().get("adding");
            }

        }

        public String getText(Object element) {
            if (element instanceof DemoTimeBarNode) {
                DemoTimeBarNode node = (DemoTimeBarNode) element;
                return node.getText();
            } else if (element instanceof DefaultTimeBarNode && VARIABLE_XAXIS) {
                return "PPS";
            } else {
                return "adding node";
            }
        }

        public void addListener(ILabelProviderListener listener) {
        }

        public void dispose() {
            if (_imageRegistry != null) {
                _imageRegistry.dispose();
            }
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
        }

        public ImageRegistry getImageRegistry() {
            if (_imageRegistry == null) {
                _imageRegistry = new ImageRegistry();
                ImageDescriptor imgDesc = new ResourceImageDescriptor("/de/jaret/examples/timebars/hierarchy/true.gif");
                _imageRegistry.put("true", imgDesc);
                imgDesc = new ResourceImageDescriptor("/de/jaret/examples/timebars/hierarchy/false.gif");
                _imageRegistry.put("false", imgDesc);
                imgDesc = new ResourceImageDescriptor("/de/jaret/examples/timebars/hierarchy/adding.gif");
                _imageRegistry.put("adding", imgDesc);
            }
            return _imageRegistry;
        }
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
    private TimeBarNode _parentNode;
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
        // support move only
        int operations = DND.DROP_MOVE;
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
                        _parentNode = getParent(_tbv.getHierarchicalModel().getRootNode(), (TimeBarNode) row);

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
                        _parentNode.remNode((TimeBarNode) _draggedRow);
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
                            // this is an action from the drag source listener ...
                            // this has to be done right here because otherwise the node would be at two places
                            // at the same time causing some redraw trouble ...
                            _parentNode.remNode((TimeBarNode) _draggedRow);
                            TimeBarNode node = (TimeBarNode) overRow;
                            node.addNode((TimeBarNode) _draggedRow);
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
                            if (overRow instanceof DefaultTimeBarRowModel) {
                                DefaultTimeBarRowModel row = (DefaultTimeBarRowModel) overRow;
                                if (overRow != null) {
                                    row.addInterval(_draggedIntervals.get(i));
                                }
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

    /**
     * Removes a node from it's parent node.
     * 
     * @param model
     * @param draggedRow
     */
    private boolean removeNode(TimeBarNode root, TimeBarNode draggedRow) {
        if (root.getChildren().contains(draggedRow)) {
            root.remNode(draggedRow);
            return true;
        } else {
            for (TimeBarNode node : root.getChildren()) {
                boolean result = removeNode(node, draggedRow);
                if (result) {
                    return true;
                }
            }
        }
        return false;
    }

    private TimeBarNode getParent(TimeBarNode root, TimeBarNode draggedRow) {
        if (root.getChildren().contains(draggedRow)) {
            return root;
        } else {
            for (TimeBarNode node : root.getChildren()) {
                TimeBarNode result = getParent(node, draggedRow);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

}