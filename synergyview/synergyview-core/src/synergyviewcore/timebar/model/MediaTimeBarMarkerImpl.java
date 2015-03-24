package synergyviewcore.timebar.model;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;

/**
 * The Class MediaTimeBarMarkerImpl.
 */
public class MediaTimeBarMarkerImpl extends TimeBarMarkerImpl {
	
	/**
	 * Instantiates a new media time bar marker impl.
	 * 
	 * @param draggable
	 *            the draggable
	 * @param date
	 *            the date
	 */
	public MediaTimeBarMarkerImpl(boolean draggable, JaretDate date) {
		super(draggable, date);
	}
	
	/**
	 * Sets the date.
	 * 
	 * @param date
	 *            the date
	 * @param fireMarkerChangeEvent
	 *            the fire marker change event
	 */
	public void setDate(JaretDate date, boolean fireMarkerChangeEvent) {
		JaretDate oldVal = _date;
		_date = date.copy();
		if (fireMarkerChangeEvent) {
			fireMarkerChanged(oldVal, _date);
		}
	}
	
}
