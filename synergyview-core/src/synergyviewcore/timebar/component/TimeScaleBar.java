package synergyviewcore.timebar.component;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;

/**
 * The Class TimeScaleBar.
 */
public class TimeScaleBar extends Composite {
	
	/**
	 * The listener interface for receiving timeScale events. The class that is
	 * interested in processing a timeScale event implements this interface, and
	 * the object created with that class is registered with a component using
	 * the component's <code>addTimeScaleListener<code> method. When
	 * the timeScale event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see TimeScaleEvent
	 */
	public interface TimeScaleListener {
		
		/**
		 * Time scale changed.
		 * 
		 * @param newPixelsPerSecond
		 *            the new pixels per second
		 */
		public void timeScaleChanged(double newPixelsPerSecond);
	}
	
	/** The listeners. */
	protected List<TimeScaleListener> listeners = new ArrayList<TimeScaleListener>();
	
	/** The pixels per second. */
	protected double pixelsPerSecond;
	
	/** The pix per seconds scale. */
	protected Scale pixPerSecondsScale;
	
	/**
	 * Instantiates a new time scale bar.
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 * @param initialPixelsPerSecond
	 *            the initial pixels per second
	 */
	public TimeScaleBar(Composite parent, int style,
			double initialPixelsPerSecond) {
		super(parent, style);
		this.createControls(parent, initialPixelsPerSecond);
	}
	
	/**
	 * Adds the time scale listener.
	 * 
	 * @param timeScaleListener
	 *            the time scale listener
	 */
	public void addTimeScaleListener(TimeScaleListener timeScaleListener) {
		listeners.add(timeScaleListener);
	}
	
	/**
	 * Creates the controls.
	 * 
	 * @param parent
	 *            the parent
	 * @param initialPixelsPerSecond
	 *            the initial pixels per second
	 */
	protected void createControls(Composite parent,
			double initialPixelsPerSecond) {
		this.setLayout(new GridLayout(1, false));
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 2;
		this.setLayoutData(gd);
		pixPerSecondsScale = new Scale(this, SWT.HORIZONTAL);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		pixPerSecondsScale.setLayoutData(gd);
		this.setPixelsPerSecond(initialPixelsPerSecond);
		
		pixPerSecondsScale.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			
			public void widgetSelected(SelectionEvent ev) {
				int val = pixPerSecondsScale.getSelection();
				double pps = (((double) val) * pixelsPerSecond) / 100;
				for (TimeScaleListener l : listeners) {
					l.timeScaleChanged(pps);
				}
			}
		});
	}
	
	/**
	 * Sets the pixels per second.
	 * 
	 * @param pixelsPerSecond
	 *            the new pixels per second
	 */
	public void setPixelsPerSecond(double pixelsPerSecond) {
		int maximumScale = (int) ((30000 * 0.25) / pixelsPerSecond);
		pixPerSecondsScale.setMaximum(maximumScale);
		pixPerSecondsScale
				.setMinimum((int) ((pixelsPerSecond * maximumScale) / 75));
		pixPerSecondsScale
				.setSelection((int) ((pixelsPerSecond * maximumScale) / 75));
		this.pixelsPerSecond = pixelsPerSecond;
		
	}
	
}
