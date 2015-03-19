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
 * @author phyokyaw
 *
 */
public class AnnotationIntervalImpl extends IntervalImpl {
	public static final String PROP_LABLE = "label";
	protected String label;
	private TimeBarMarkerImpl marker;
	private IntervalAnnotation annotation;
	private SubjectRowModel ownerRow;
	private CaptionPublishState captionState = CaptionPublishState.UNSET;
	private List<ICaptionChangeListener> captionChangeListeners = new ArrayList<ICaptionChangeListener>();
	private TimeBarMarkerListener timeBarMarkerListener;
	
    @SuppressWarnings("unused")
	private AnnotationIntervalImpl() {
        super();
    }
    
    
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
    
    public SubjectRowModel getOwner() {
    	return ownerRow;
    }
    
    @Override
    public void setEnd(JaretDate end) {
    	super.setEnd(end);
    	annotation.setDuration((int) end.diffMilliSeconds(this.getBegin()));
    }
    
    @Override
    public void setBegin(JaretDate begin) {
    	super.setBegin(begin);
		annotation.setHr(this.getBegin().getHours());
		annotation.setMi(this.getBegin().getMinutes());
		annotation.setSec(this.getBegin().getSeconds());
		annotation.setMilliSec(this.getBegin().getMillis());
		annotation.setStartTime(DateTimeHelper.getMilliFromJaretDate(this.getBegin()));
    }

    public void setLabel(String label) {
    	this.firePropertyChange(AnnotationIntervalImpl.PROP_LABLE, this.label, this.label = label);
    }
    
    public boolean isMarkerInrange() {
    	if (marker.getDate().compareTo(this.getBegin()) >= 0 && marker.getDate().compareTo(this.getEnd()) < 0)
			return true;
		else return false;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    @Override
    public String toString() {
        return label!=null?label:super.toString();
    }
    
    

	/**
	 * @return the analysisCaption
	 */
	public Annotation getAnnotation() {
		return annotation;
	}
	
	public void addCaptionChangeListener(ICaptionChangeListener captionChangeListener) {
		captionChangeListeners.add(captionChangeListener);
	}
	
	public void removeCaptionChangeListener(ICaptionChangeListener captionChangeListener) {
		captionChangeListeners.remove(captionChangeListener);
	}
	
	public void dispose() {
		if (this.marker !=null)
			this.marker.remTimeBarMarkerListener(timeBarMarkerListener);
	}
	
	
}
