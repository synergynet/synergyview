/**
 *  File: MediaIntervalImpl.java
 *  Copyright (c) 2010
 *  phyokyaw
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

package synergyviewcore.annotations.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import synergyviewcore.annotations.model.Annotation;
import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.annotations.model.IntervalAnnotation;
import synergyviewcore.annotations.ui.events.CaptionChangeEvent;
import synergyviewcore.annotations.ui.events.ICaptionChangeListener;
import synergyviewcore.annotations.ui.events.ICaptionChangeListener.CaptionPublishState;
import synergyviewcore.util.DateTimeHelper;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarMarkerListener;


/**
 * The Class AnnotationIntervalImpl.
 *
 * @author phyokyaw
 */
public class AnnotationIntervalImpl extends IntervalImpl {
	
	/** The Constant PROP_LABLE. */
	public static final String PROP_LABLE = "label";
	
	/** The label. */
	protected String label;
	
	/** The marker. */
	private TimeBarMarkerImpl marker;
	
	/** The annotation. */
	private IntervalAnnotation annotation;
	
	/** The owner row. */
	private SubjectRowModel ownerRow;
	
	/** The caption state. */
	private CaptionPublishState captionState = CaptionPublishState.UNSET;
	
	/** The caption change listeners. */
	private List<ICaptionChangeListener> captionChangeListeners = new ArrayList<ICaptionChangeListener>();
	
	/** The time bar marker listener. */
	private TimeBarMarkerListener timeBarMarkerListener;
	
    /**
     * Instantiates a new annotation interval impl.
     */
    @SuppressWarnings("unused")
	private AnnotationIntervalImpl() {
        super();
    }
    
    
    /**
     * Instantiates a new annotation interval impl.
     *
     * @param annotationSetNode the annotation set node
     * @param begin the begin
     * @param end the end
     * @param annotationValue the annotation value
     * @param marker the marker
     * @param owner the owner
     */
    public AnnotationIntervalImpl(AnnotationSetNode annotationSetNode, JaretDate begin, JaretDate end, IntervalAnnotation annotationValue, TimeBarMarkerImpl marker, SubjectRowModel owner) {
        super(begin, end);
        ownerRow = owner;
        this.annotation = annotationValue;
        this.marker = marker;
        timeBarMarkerListener = new TimeBarMarkerListener() {

			public void markerDescriptionChanged(TimeBarMarker arg0,
					String arg1, String arg2) {
				//
				
			}

			public void markerMoved(TimeBarMarker arg0, JaretDate arg1,
					JaretDate arg2) {
				if (isMarkerInrange()) {
					if (captionState == CaptionPublishState.UNSET) {
						captionState = CaptionPublishState.SET;
						for (ICaptionChangeListener captinListener : captionChangeListeners) {
							CaptionChangeEvent event = new CaptionChangeEvent(AnnotationIntervalImpl.this, annotation.getText(),captionState);
							captinListener.captionChange(AnnotationIntervalImpl.this, event);
						}
					}
				} else {
					if (captionState == CaptionPublishState.SET) {
						captionState = CaptionPublishState.UNSET;
						for (ICaptionChangeListener captinListener : captionChangeListeners) {
							CaptionChangeEvent event = new CaptionChangeEvent(AnnotationIntervalImpl.this, annotation.getText(), captionState);
							captinListener.captionChange(AnnotationIntervalImpl.this, event);
						}
					}
				}
			}
        	
        };
        
        this.marker.addTimeBarMarkerListener(timeBarMarkerListener);
        
        annotation.addPropertyChangeListener(Annotation.PROP_TEXT, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				AnnotationIntervalImpl.this.setLabel((String) arg0.getNewValue());
			}
        	
        });
       
    }
    
    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public SubjectRowModel getOwner() {
    	return ownerRow;
    }
    
    /* (non-Javadoc)
     * @see de.jaret.util.date.IntervalImpl#setEnd(de.jaret.util.date.JaretDate)
     */
    @Override
    public void setEnd(JaretDate end) {
    	super.setEnd(end);
    	annotation.setDuration((int) end.diffMilliSeconds(this.getBegin()));
    }
    
    /* (non-Javadoc)
     * @see de.jaret.util.date.IntervalImpl#setBegin(de.jaret.util.date.JaretDate)
     */
    @Override
    public void setBegin(JaretDate begin) {
    	super.setBegin(begin);
		annotation.setHr(this.getBegin().getHours());
		annotation.setMi(this.getBegin().getMinutes());
		annotation.setSec(this.getBegin().getSeconds());
		annotation.setMilliSec(this.getBegin().getMillis());
		annotation.setStartTime(DateTimeHelper.getMilliFromJaretDate(this.getBegin()));
    }

    /**
     * Sets the label.
     *
     * @param label the new label
     */
    public void setLabel(String label) {
    	this.firePropertyChange(AnnotationIntervalImpl.PROP_LABLE, this.label, this.label = label);
    }
    
    /**
     * Checks if is marker inrange.
     *
     * @return true, if is marker inrange
     */
    public boolean isMarkerInrange() {
    	if (marker.getDate().compareTo(this.getBegin()) >= 0 && marker.getDate().compareTo(this.getEnd()) < 0)
			return true;
		else return false;
    }
    
    /**
     * Gets the label.
     *
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }
    
    /* (non-Javadoc)
     * @see de.jaret.util.date.IntervalImpl#toString()
     */
    @Override
    public String toString() {
        return label!=null?label:super.toString();
    }
    
    

	/**
	 * Gets the annotation.
	 *
	 * @return the analysisCaption
	 */
	public Annotation getAnnotation() {
		return annotation;
	}
	
	/**
	 * Adds the caption change listener.
	 *
	 * @param captionChangeListener the caption change listener
	 */
	public void addCaptionChangeListener(ICaptionChangeListener captionChangeListener) {
		captionChangeListeners.add(captionChangeListener);
	}
	
	/**
	 * Removes the caption change listener.
	 *
	 * @param captionChangeListener the caption change listener
	 */
	public void removeCaptionChangeListener(ICaptionChangeListener captionChangeListener) {
		captionChangeListeners.remove(captionChangeListener);
	}
	
	/**
	 * Dispose.
	 */
	public void dispose() {
		if (this.marker !=null)
			this.marker.remTimeBarMarkerListener(timeBarMarkerListener);
	}
	
	
}
