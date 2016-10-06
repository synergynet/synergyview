package synergyviewmvc.timebar.model;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;

public class MediaTimeBarMarkerImpl extends TimeBarMarkerImpl {
	
	public MediaTimeBarMarkerImpl(boolean draggable, JaretDate date) {
		super(draggable, date);
	}
	 
	public void setDate(JaretDate date, boolean fireMarkerChangeEvent){
		JaretDate oldVal = _date;
	    _date = date.copy();
	    if (fireMarkerChangeEvent)
	        fireMarkerChanged(oldVal, _date);
	}

}
