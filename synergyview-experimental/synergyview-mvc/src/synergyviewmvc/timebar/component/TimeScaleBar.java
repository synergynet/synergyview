package synergyviewmvc.timebar.component;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;

public class TimeScaleBar extends Composite {

	protected List<TimeScaleListener> listeners = new ArrayList<TimeScaleListener>();
	protected Scale pixPerSecondsScale;
	protected double pixelsPerSecond;
		
	public TimeScaleBar(Composite parent, int style, double initialPixelsPerSecond) {
		super(parent, style);
		this.createControls(parent, initialPixelsPerSecond);
	}
	
	public void setPixelsPerSecond(double pixelsPerSecond){
		int maximumScale = (int)(30000*0.25/pixelsPerSecond);
	     pixPerSecondsScale.setMaximum(maximumScale);
	     pixPerSecondsScale.setMinimum((int)(pixelsPerSecond*maximumScale/75));    
	     pixPerSecondsScale.setSelection((int) (pixelsPerSecond*maximumScale/75));
	     this.pixelsPerSecond = pixelsPerSecond;
	         
	    
	}
		
	protected void createControls(Composite parent, double initialPixelsPerSecond){
		this.setLayout(new GridLayout(1,false));
		GridData gd = new GridData();
		gd.widthHint = 30;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;
	    this.setLayoutData(gd);
	    pixPerSecondsScale = new Scale(this, SWT.VERTICAL);
	    gd = new GridData();
	    gd.horizontalAlignment = SWT.LEFT;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;
		pixPerSecondsScale.setLayoutData(gd);
		this.setPixelsPerSecond(initialPixelsPerSecond);
		
		 pixPerSecondsScale.addSelectionListener(new SelectionListener() {
	         public void widgetSelected(SelectionEvent ev) {
	             int val = pixPerSecondsScale.getSelection();
	             double pps = ((double) val) * pixelsPerSecond/100;
	             for (TimeScaleListener l: listeners)
	            	 l.timeScaleChanged(pps);                  
	         }

	         public void widgetDefaultSelected(SelectionEvent arg0) {}
	     });
	}
	
	public void addTimeScaleListener(TimeScaleListener timeScaleListener){
		listeners.add(timeScaleListener);
	}
	
	public interface TimeScaleListener {
		public void timeScaleChanged(double newPixelsPerSecond);
	}
	

}
