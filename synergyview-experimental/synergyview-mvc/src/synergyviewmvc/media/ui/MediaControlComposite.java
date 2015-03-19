/**
 *  File: MediaControl.java
 *  Copyright (c) 2010
 *  phyo
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

package synergyviewmvc.media.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;

import synergyviewmvc.media.model.AbstractMedia;
import synergyviewmvc.resource.ResourceLoader;

import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarMarkerListener;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.mod.DefaultIntervalModificator;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.ITimeBarChangeListener;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultTimeScaleRenderer;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2;

/**
 * @author phyo
 *
 */
public class MediaControlComposite extends Composite {
	private static final int SCALE_ZOOM_TIMES = 4;
	private LocalResourceManager _resourceManager = new LocalResourceManager(JFaceResources.getResources());
	private DataBindingContext _ctx = new DataBindingContext();
	private Button _playPulse;
	private TimeBarViewer _timeBarViewer;
	private Scale _timeScale;
	private TimeBarMarkerImpl _marker;
	private JaretDate _startDate = new JaretDate().setTime(0, 0, 0, 0);
	private PropertyChangeListener _movieTimeListener;
	private boolean _isMarkerDragging = false;
	private DefaultTimeBarModel _model = new DefaultTimeBarModel();
	private DefaultTimeBarRowModel _tbr = new DefaultTimeBarRowModel();
	private IntervalImpl _interval;
	private MarkerInterval _markerInerval;
	private Binding _mediaBinding;
	
	@Override
	public void dispose() {
		if (_ctx!=null)
			_ctx.dispose();
		_resourceManager.dispose();
		super.dispose();
		
	}

	private AbstractMedia _media;
	
	/**
	 * @param parent
	 * @param style
	 */
	public MediaControlComposite(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(3, false));
		createMediaConstrols();
		createMediaTimeSlider();
		createMediaTimeScaler();
		
		_ctx.bindValue(SWTObservables.observeEnabled(_playPulse),SWTObservables.observeVisible(_timeBarViewer));
		_ctx.bindValue(SWTObservables.observeEnabled(_playPulse),SWTObservables.observeVisible(_timeScale));
	}
	
	public void registerMedia(AbstractMedia media) {
		_media = media;
		_playPulse.setEnabled(true);
		_ctx.updateModels();
		
		 _model = new DefaultTimeBarModel();
		 _tbr = new DefaultTimeBarRowModel();
		 _interval = new IntervalImpl(_startDate.copy(), _startDate.copy().advanceMillis(media.getDuration()));
		 _tbr.addInterval(_interval);
		 
		 _model.addRow(_tbr);
		_timeBarViewer.setModel(_model);
		_movieTimeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (_timeBarViewer == null || _timeBarViewer.isDisposed()) return;
				_timeBarViewer.getDisplay().asyncExec(new Runnable() {
					public void run() {
						
						if (!_isMarkerDragging) {
							_marker.setDate(_startDate.copy().advanceMillis((long) _media.getTime()));
						}
					}
					
				});
				
			}
		};
		_media.addPropertyChangeListener("time",_movieTimeListener);
		updateScale();
		_mediaBinding = _ctx.bindValue(SWTObservables.observeSelection(_playPulse), BeansObservables.observeValue(_media, "playing"));
	}
	
	public void unregisterMedia() {
		_media.removePropertyChangeListener("time", _movieTimeListener);
		_movieTimeListener = null;
		this._media = null;
		_tbr.remInterval(_interval);
		_interval = null;
		_playPulse.setEnabled(false);
		_ctx.updateModels();
		if (_mediaBinding!=null) {
			_ctx.removeBinding(_mediaBinding);
			_mediaBinding = null;
		}
	}
	
	
	private void createMediaConstrols() {
		Composite control = new Composite(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.LEFT;
		control.setLayoutData(gridData);
		control.setLayout(new GridLayout(1, false));
		_playPulse = new Button(control, SWT.TOGGLE);
		_playPulse.setEnabled(false);
		_playPulse.setImage((Image) _resourceManager.get(ResourceLoader.getIconDescriptor("control_play_blue.png")));
		_playPulse.setLayoutData(new GridData());

		Button test = new Button(control, SWT.NONE);
		test.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				_media.getRate();
			}
		});
		
	}
	
	private void createMediaTimeSlider() {
		Group control = new Group(this, SWT.SHADOW_ETCHED_IN);
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		control.setLayoutData(gridData);
		control.setLayout(new FillLayout());
		_timeBarViewer = new TimeBarViewer(control, SWT.H_SCROLL | SWT.V_SCROLL);
		_timeBarViewer.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);
		_timeBarViewer.setTimeScaleRenderer(new MilliScale());
		_timeBarViewer.setMilliAccuracy(true);
		_timeBarViewer.setPixelPerSecond(1);
		_timeBarViewer.setSelectionDelta(6);
		_timeBarViewer.setYAxisWidth(0);
		_timeBarViewer.setRowHeight(30);  
		_timeBarViewer.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
				//	
			}

			public void controlResized(ControlEvent e) {
				if (_media!=null) {
					updateScale();
				}
			}
			
		});
		_marker = new TimeBarMarkerImpl(true, _startDate);
		_marker.addTimeBarMarkerListener(new TimeBarMarkerListener() {
			public void markerDescriptionChanged(TimeBarMarker arg0,
					String arg1, String arg2) {
				//
			}

			public void markerMoved(TimeBarMarker arg0, JaretDate arg1,
					JaretDate arg2) {
				if (_isMarkerDragging) {
					long value = arg2.getMillisInDay() + arg2.getMillis(); //Bug
					_media.setTime((int) value);

				}
			}
			
		});
		_timeBarViewer.addTimeBarChangeListener(new ITimeBarChangeListener() {
            public void intervalChangeCancelled(TimeBarRow row, Interval interval) {}
            public void intervalChangeStarted(TimeBarRow row, Interval interval) {}
            public void intervalChanged(TimeBarRow row, Interval interval, JaretDate oldBegin, JaretDate oldEnd) {}
            public void intervalIntermediateChange(TimeBarRow arg0,Interval arg1, JaretDate arg2, JaretDate arg3) {}
            public void markerDragStarted(TimeBarMarker marker) {
            	_isMarkerDragging = true;
            }
            	
            public void markerDragStopped(TimeBarMarker marker) {
            	_isMarkerDragging = false;
            }
        });
		_timeBarViewer.addMarker(_marker);
		
		_timeBarViewer.registerTimeBarRenderer(MarkerInterval.class, new MarkerIntervalRenderer());
		_timeBarViewer.addIntervalModificator(new DefaultIntervalModificator(){

			@Override
			public boolean isShiftingAllowed(TimeBarRow row, Interval interval) {
				return true;
			}

			@Override
			public boolean isSizingAllowed(TimeBarRow row, Interval interval) {
				return true;
			}

			@Override
			public boolean newBeginAllowed(TimeBarRow row, Interval interval,
					JaretDate newBegin) {
				return true;
			}

			@Override
			public boolean newEndAllowed(TimeBarRow row, Interval interval,
					JaretDate newEnd) {
				return true;
			}

			@Override
			public boolean shiftAllowed(TimeBarRow row, Interval interval,
					JaretDate newBegin) {
				return true;
			}
			
			
			
		});
	}
	
	private void updateScale() {
		_timeBarViewer.setPixelPerSecond((double)(((Composite) _timeBarViewer).getClientArea().width * 1000)/(double)(_media.getDuration()));
		_timeScale.setMaximum((int) (_timeBarViewer.getPixelPerSecond() * 1000 * SCALE_ZOOM_TIMES));
		_timeScale.setMinimum((int) (_timeBarViewer.getPixelPerSecond() * 1000));         
		_timeScale.setSelection((int) (_timeBarViewer.getPixelPerSecond() * 1000));
	}
	
	private void createMediaTimeScaler() {
		Composite control = new Composite(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.widthHint = 50;
		control.setLayoutData(gridData);
		control.setLayout(new GridLayout(1, false));
		_timeScale = new Scale(control, SWT.VERTICAL);
		_timeScale.setMinimum(0);
		_timeScale.setMinimum(1);
		_timeScale.setLayoutData(new GridData(GridData.FILL_BOTH));
		_timeScale.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// 
			}

			public void widgetSelected(SelectionEvent e) {
				_timeBarViewer.setPixelPerSecond((double) _timeScale.getSelection() / 1000);
			}
		});
	}
	
	public void setMarkerEnable(boolean markerEnableValue) {
		if (markerEnableValue) {
			
			_markerInerval = new MarkerInterval(_startDate.copy());
			_markerInerval.setLabel(_media.getFormattedTime());
			_tbr.addInterval(_markerInerval);
			//_model.addRow(_markerRow);
		} else {
			_tbr.remInterval(_markerInerval);
			_markerInerval = null;
		}
	}
	
	

}

class MilliScale extends DefaultTimeScaleRenderer {
	
    protected static final int PREFERREDHEIGHT = 30;
    
    @Override
    public int getHeight() {
        if (_printer == null) {
            return PREFERREDHEIGHT;
        } else {
            return scaleY(PREFERREDHEIGHT);
        }
    }


}

class MarkerIntervalRenderer extends RendererBase implements TimeBarRenderer, TimeBarRenderer2 {
    /** size of the drawn element. */
    private static final int SIZE = 15;
    /** extend for the label. */
    private static final int MAXLABELWIDTH = 80;
    /** pixeloffset for the label drawing. */
    private static final int LABELOFFSET = 3;

    /** corrected size (for printing). */
    private int _size = SIZE;

    /** cache for the delegate supplying the orientation information. */
    protected TimeBarViewerDelegate _delegate;

    /**
     * Create renderer for printing.
     * 
     * @param printer printer device
     */
    public MarkerIntervalRenderer(Printer printer) {
        super(printer);
        _size = scaleX(SIZE);
    }

    /**
     * Construct renderer for screen use.
     * 
     */
    public MarkerIntervalRenderer() {
        super(null);
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getPreferredDrawingBounds(Rectangle intervalDrawingArea, TimeBarViewerDelegate delegate,
            Interval interval, boolean selected, boolean printing, boolean overlap) {

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        if (horizontal) {
            return new Rectangle(intervalDrawingArea.x - _size, intervalDrawingArea.y, intervalDrawingArea.width + 2
                    * _size + scaleX(MAXLABELWIDTH), intervalDrawingArea.height);
        } else {
            return new Rectangle(intervalDrawingArea.x, intervalDrawingArea.y - _size, intervalDrawingArea.width,
                    intervalDrawingArea.height + 2 * _size + scaleY(MAXLABELWIDTH));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected,
            boolean printing, boolean overlap) {
        _delegate = delegate;
        defaultDraw(gc, drawingArea, delegate, interval, selected, printing, overlap);
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        return getToolTipText(_delegate, interval, drawingArea, x, y, overlapping);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        return contains(_delegate, interval, drawingArea, x, y, overlapping);
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getContainingRectangle(Interval interval, Rectangle drawingArea, boolean overlapping) {
        return getContainingRectangle(_delegate, interval, drawingArea, overlapping);
    }

    /**
     * {@inheritDoc}. Will create print renderes for all registered renderers.
     */
    public TimeBarRenderer createPrintrenderer(Printer printer) {
    	MarkerIntervalRenderer renderer = new MarkerIntervalRenderer(printer);
        return renderer;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
    }

    /**
     * Drawing method for default rendering.
     * 
     * @param gc GC
     * @param drawingArea drawingArea
     * @param delegate delegate
     * @param interval interval to draw
     * @param selected true for selected drawing
     * @param printing true for printing
     * @param overlap true if the interval overlaps with other intervals
     */
    private void defaultDraw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval,
            boolean selected, boolean printing, boolean overlap) {

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle da = getDrawingRect(drawingArea, horizontal);

        // draw focus
        drawFocus(gc, da, delegate, interval, selected, printing, overlap);

        Color bg = gc.getBackground();

        // draw the diamond
        gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));

        int[] points = new int[] {da.x, da.y + da.height / 2, da.x + da.width / 2, da.y, da.x + da.width,
                da.y + da.height / 2, da.x + da.width / 2, da.y + da.height};

        gc.fillPolygon(points);
        gc.drawPolygon(points);

        if (selected) {
            gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
            gc.setAlpha(60);
            gc.fillPolygon(points);
            gc.setAlpha(255);
        }
        gc.setBackground(bg);

        MarkerInterval fe = (MarkerInterval) interval;

        // draw the label
        if (horizontal) {
            SwtGraphicsHelper.drawStringVCentered(gc, fe.getLabel(), da.x + da.width + scaleX(LABELOFFSET), da.y, da.y
                    + da.height);
        } else {
            SwtGraphicsHelper.drawStringCentered(gc, fe.getLabel(), da.x + da.width / 2, da.y + da.height
                    + scaleY(LABELOFFSET) + gc.textExtent(fe.getLabel()).y);
        }

    }

    /**
     * Calculate the drawing area for the marking symbol.
     * 
     * @param drawingArea drawing area as given for the time
     * @return Rectangle for drawing the main symbol
     */
    private Rectangle getDrawingRect(Rectangle drawingArea, boolean horizontal) {
        if (horizontal) {
            int y = drawingArea.y + (drawingArea.height - 2 * _size) / 2;
            return new Rectangle(drawingArea.x - _size, y, 2 * _size, 2 * _size);
        } else {
            int x = drawingArea.x + (drawingArea.width - 2 * _size) / 2;
            return new Rectangle(x, drawingArea.y - _size, 2 * _size, 2 * _size);
        }
    }

    public String getToolTipText(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x,
            int y, boolean overlapping) {
        if (contains(delegate, interval, drawingArea, x, y, overlapping)) {
            return interval.toString();
        }
        return null;
    }

    public boolean contains(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x, int y,
            boolean overlapping) {
        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle da = getDrawingRect(drawingArea, horizontal);
        return da.contains(drawingArea.x + x, drawingArea.y + y);
    }

    public Rectangle getContainingRectangle(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea,
            boolean overlapping) {
        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle da = getDrawingRect(drawingArea, horizontal);
        return da;
    }

}

class MarkerInterval extends de.jaret.util.date.Event {
    String _label;

    
    public MarkerInterval(JaretDate date) {
        super(date);
    }

    
    public String getLabel() {
        return _label;
    }

    public void setLabel(String label) {
        String oldVal = _label;
        _label = label;
        if (isRealModification(oldVal, label)) {
        	firePropertyChange("Label", null, label);
        }
    }
}

