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

package synergyviewmvc.annotations.ui;

import java.util.ArrayList;
import java.util.List;

import synergyviewmvc.annotations.model.Annotation;
import synergyviewmvc.annotations.model.IntervalAnnotation;
import synergyviewmvc.annotations.ui.events.AnnotationChangeEvent;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarMarkerListener;

/**
 * @author phyokyaw
 *
 */
public class AnnotationIntervalImpl extends IntervalImpl implements IAnnotationListener {

	protected String label;
	private TimeBarMarkerImpl _marker;
	private IntervalAnnotation _analysisCaption;
	private SubjectRowModel _owner;

	private CaptionPublishState captionState = CaptionPublishState.UNSET;
	private List<ICaptionChangeListener> captionChangeListeners = new ArrayList<ICaptionChangeListener>();
	
    public AnnotationIntervalImpl() {
        super();
        
    }
    public AnnotationIntervalImpl(JaretDate begin, JaretDate end, IntervalAnnotation analysisCaption, TimeBarMarkerImpl marker,SubjectRowModel owner, ICaptionChangeListener captionChangeListener) {
        super(begin, end);
        _owner = owner;
        _analysisCaption = analysisCaption;
        this._marker = marker;
        addCaptionListener(captionChangeListener);
        _marker.addTimeBarMarkerListener(new TimeBarMarkerListener() {

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
							AnnotationChangeEvent event = new AnnotationChangeEvent(AnnotationIntervalImpl.this,_analysisCaption.getText(), captionState);
							captinListener.captionChange(AnnotationIntervalImpl.this, event);
						}
					}
				} else {
					if (captionState == CaptionPublishState.SET) {
						captionState = CaptionPublishState.UNSET;
						for (ICaptionChangeListener captinListener : captionChangeListeners) {
							AnnotationChangeEvent event = new AnnotationChangeEvent(AnnotationIntervalImpl.this,_analysisCaption.getText(), captionState);
							captinListener.captionChange(AnnotationIntervalImpl.this, event);
						}
					}
				}
			}
        	
        });
       
    }
    
    public SubjectRowModel getOwner() {
    	return _owner;
    }
    
    @Override
    public void setEnd(JaretDate end) {
    	super.setEnd(end);
    	_analysisCaption.setDuration((int) end.diffMilliSeconds(this.getBegin()));
    }
    
    @Override
    public void setBegin(JaretDate begin) {
    	super.setBegin(begin);
    	int startDateMilliValue = (int) begin.getMillisInDay() + begin.getMillis();
    	_analysisCaption.setStartTime(startDateMilliValue);
    }

    public void setLabel(String label) {
    	_analysisCaption.setText(label);
        this.label = label;
    }
    
    public boolean isMarkerInrange() {
    	if (_marker.getDate().compareTo(this.getBegin()) >= 0 && _marker.getDate().compareTo(this.getEnd()) < 0)
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
	 * @param analysisCaption the analysisCaption to set
	 */
	public void setAnalysisCaption(IntervalAnnotation analysisCaption) {
		this._analysisCaption = analysisCaption;
	}
	/**
	 * @return the analysisCaption
	 */
	public Annotation getAnalysisCaption() {
		return _analysisCaption;
	}
	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.subtitle.ui.ICaptionListener#addCaptionListener(uk.ac.durham.tel.synergynet.ats.subtitle.ui.ICaptionListener.ICaptionChangeListener)
	 */
	public void addCaptionListener(ICaptionChangeListener captionChangeListener) {
		captionChangeListeners.add(captionChangeListener);
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.subtitle.ui.ICaptionListener#removeCaptionListener(uk.ac.durham.tel.synergynet.ats.subtitle.ui.ICaptionListener.ICaptionChangeListener)
	 */
	public void removeCaptionListener(
			ICaptionChangeListener captionChangeListener) {
		if (!captionChangeListeners.contains(captionChangeListener))
			captionChangeListeners.remove(captionChangeListener);
		
	}
	
}
