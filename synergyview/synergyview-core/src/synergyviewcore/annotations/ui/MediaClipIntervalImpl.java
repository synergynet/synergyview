/**
 * File: MediaIntervalImpl.java Copyright (c) 2010 phyo This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcore.annotations.ui;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.widgets.Composite;

import synergyviewcore.annotations.ui.events.MediaTimeChangeListener;
import synergyviewcore.annotations.ui.events.TimeAvailableEvent;
import synergyviewcore.annotations.ui.events.TimeAvailableListener;
import synergyviewcore.annotations.ui.events.TimeChangeEvent;
import synergyviewcore.collections.model.CollectionMedia;
import synergyviewcore.media.model.AbstractMedia;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarMarkerListener;

/**
 * The Class MediaClipIntervalImpl.
 * 
 * @author phyo
 */
public class MediaClipIntervalImpl extends IntervalImpl {
	
	/** The _clip end date. */
	private JaretDate _clipEndDate;
	
	/** The _clip start date. */
	private JaretDate _clipStartDate;
	
	/** The _collection media. */
	private CollectionMedia _collectionMedia;
	
	/** The _end interval off set. */
	private long _endIntervalOffSet;
	
	/** The _left media. */
	private boolean _leftMedia;
	
	/** The _marker. */
	private TimeBarMarkerImpl _marker;
	
	/** The _media. */
	private AbstractMedia _media;
	
	/** The _media end. */
	@SuppressWarnings("unused")
	private long _mediaEnd;
	
	/** The _media start. */
	private long _mediaStart;
	
	/** The _play back. */
	private boolean _playBack;
	
	/** The _property change listener. */
	private PropertyChangeListener _propertyChangeListener;
	
	/** The _start interval off set. */
	private long _startIntervalOffSet;
	
	/** The _time available. */
	private boolean _timeAvailable;
	
	/** The _ui owner. */
	private Composite _uiOwner;
	
	/** The label. */
	protected String label;
	
	/** The time available listeners. */
	private List<TimeAvailableListener> timeAvailableListeners = new CopyOnWriteArrayList<TimeAvailableListener>();
	
	/** The time changed listeners. */
	private List<MediaTimeChangeListener> timeChangedListeners = new CopyOnWriteArrayList<MediaTimeChangeListener>();
	
	/**
	 * Instantiates a new media clip interval impl.
	 */
	protected MediaClipIntervalImpl() {
		super();
	}
	
	/**
	 * Instantiates a new media clip interval impl.
	 * 
	 * @param collectionMedia
	 *            the collection media
	 * @param mediaPreview
	 *            the media preview
	 * @param clipStartDate
	 *            the clip start date
	 * @param clipDuration
	 *            the clip duration
	 * @param marker
	 *            the marker
	 * @param uiOwnerValue
	 *            the ui owner value
	 */
	public MediaClipIntervalImpl(CollectionMedia collectionMedia,
			AbstractMedia mediaPreview, JaretDate clipStartDate,
			int clipDuration, TimeBarMarkerImpl marker, Composite uiOwnerValue) {
		super();
		_uiOwner = uiOwnerValue;
		_collectionMedia = collectionMedia;
		_marker = marker;
		_media = mediaPreview;
		_clipStartDate = clipStartDate.copy();
		_clipEndDate = clipStartDate.copy().advanceMillis(clipDuration);
		int startDateMilliValue = (int) clipStartDate.getMillisInDay()
				+ clipStartDate.getMillis();
		if (startDateMilliValue >= collectionMedia.getOffSet()) {
			_startIntervalOffSet = 0;
			this.setBegin(clipStartDate.copy());
			_mediaStart = startDateMilliValue
					- (int) collectionMedia.getOffSet();
			_media.setTime((int) _mediaStart);
			_leftMedia = true;
		} else {
			_startIntervalOffSet = collectionMedia.getOffSet()
					- startDateMilliValue;
			_mediaStart = 0;
			this.setBegin(clipStartDate.copy().advanceMillis(
					_startIntervalOffSet));
			_leftMedia = false;
		}
		
		int mediaEndValue = (int) collectionMedia.getOffSet()
				+ _media.getDuration();
		if ((startDateMilliValue + clipDuration) > mediaEndValue) {
			_endIntervalOffSet = (_media.getDuration() + (int) collectionMedia
					.getOffSet()) - startDateMilliValue;
			this.setEnd(clipStartDate.copy().advanceMillis(_endIntervalOffSet));
			_mediaEnd = _media.getDuration();
		} else {
			_endIntervalOffSet = clipDuration;
			this.setEnd(clipStartDate.copy().advanceMillis(_endIntervalOffSet));
			_mediaEnd = (mediaEndValue - _endIntervalOffSet)
					+ startDateMilliValue;
		}
		this.setLabel(collectionMedia.getMediaName());
		
		_marker.addTimeBarMarkerListener(new TimeBarMarkerListener() {
			
			public void markerDescriptionChanged(TimeBarMarker arg0,
					String arg1, String arg2) {
				//
				
			}
			
			public void markerMoved(TimeBarMarker arg0, JaretDate arg1,
					JaretDate arg2) {
				if (isMarkerInrange()) {
					
					if (_playBack && !_media.isPlaying()) {
						_media.setPlaying(true);
						
					}
					if (!_timeAvailable) {
						_timeAvailable = true;
						for (TimeAvailableListener listener : timeAvailableListeners) {
							listener.timeAvailableChanged(new TimeAvailableEvent(
									MediaClipIntervalImpl.this, _timeAvailable));
						}
					}
					
				} else {
					if (_timeAvailable) {
						_timeAvailable = false;
						for (TimeAvailableListener listener : timeAvailableListeners) {
							listener.timeAvailableChanged(new TimeAvailableEvent(
									MediaClipIntervalImpl.this, _timeAvailable));
						}
					}
				}
			}
			
		});
		
		if (isMarkerInrange()) {
			_timeAvailable = true;
		} else {
			_timeAvailable = false;
		}
		
		_propertyChangeListener = new PropertyChangeListener() {
			
			public void propertyChange(java.beans.PropertyChangeEvent evt) {
				if ((_uiOwner != null) && !_uiOwner.isDisposed()) {
					_uiOwner.getDisplay().asyncExec(new Runnable() {
						
						public void run() {
							JaretDate currentTimeDate = getCurrentTimeDate();
							if (currentTimeDate.compareTo(_clipEndDate) >= 0) {
								_media.setPlaying(false);
								_playBack = false;
							}
							
							TimeChangeEvent event = new TimeChangeEvent(
									MediaClipIntervalImpl.this, currentTimeDate);
							for (MediaTimeChangeListener listener : timeChangedListeners) {
								listener.playBackChanged(event);
							}
							
						}
						
					});
				}
			}
			
		};
		
		_media.addPropertyChangeListener(AbstractMedia.PROP_TIME,
				_propertyChangeListener);
		_media.setMute(collectionMedia.isMute());
	}
	
	/**
	 * Adds the time available listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addTimeAvailableListener(TimeAvailableListener listener) {
		timeAvailableListeners.add(listener);
	}
	
	/**
	 * Adds the time change listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addTimeChangeListener(MediaTimeChangeListener listener) {
		timeChangedListeners.add(listener);
	}
	
	/**
	 * Gets the current time date.
	 * 
	 * @return the current time date
	 */
	private JaretDate getCurrentTimeDate() {
		if (_leftMedia) {
			int currentTimeValue = _media.getTime() - (int) _mediaStart;
			return _clipStartDate.copy().advanceMillis(currentTimeValue);
			
		} else {
			long diff = MediaClipIntervalImpl.this.getBegin().diffMilliSeconds(
					_clipStartDate);
			return _clipStartDate.copy().advanceMillis(_media.getTime() + diff);
		}
	}
	
	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * Checks if is marker inrange.
	 * 
	 * @return true, if is marker inrange
	 */
	private boolean isMarkerInrange() {
		if ((_marker.getDate().compareTo(this.getBegin()) >= 0)
				&& (_marker.getDate().compareTo(this.getEnd()) < 0)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if is mute.
	 * 
	 * @return true, if is mute
	 */
	public boolean isMute() {
		return _media.isMute();
	}
	
	/**
	 * Checks if is time available.
	 * 
	 * @return true, if is time available
	 */
	public boolean isTimeAvailable() {
		return _timeAvailable;
	}
	
	/**
	 * Play.
	 */
	public void play() {
		_playBack = true;
		if (isMarkerInrange()) {
			if (!_media.isPlaying()) {
				updateMediaTime();
				_media.setPlaying(true);
			}
		}
	}
	
	/**
	 * Removes the time available listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeTimeAvailableListener(TimeAvailableListener listener) {
		if (timeAvailableListeners.contains(listener)) {
			timeAvailableListeners.remove(listener);
		}
	}
	
	/**
	 * Removes the time change listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeTimeChangeListener(MediaTimeChangeListener listener) {
		if (timeChangedListeners.contains(listener)) {
			timeChangedListeners.remove(listener);
		}
	}
	
	/**
	 * Sets the label.
	 * 
	 * @param label
	 *            the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Sets the mute.
	 * 
	 * @param muteValue
	 *            the new mute
	 */
	public void setMute(boolean muteValue) {
		_media.setMute(muteValue);
	}
	
	/**
	 * Step ff.
	 * 
	 * @param stepMilliValue
	 *            the step milli value
	 */
	public void stepFF(int stepMilliValue) {
		if (_marker.getDate().copy().advanceMillis(stepMilliValue)
				.compareTo(_end) < 0) {
			_media.setTime(_media.getTime() + stepMilliValue);
		}
	}
	
	/**
	 * Step re.
	 * 
	 * @param stepMilliValue
	 *            the step milli value
	 */
	public void stepRE(int stepMilliValue) {
		if ((_media.getTime() - stepMilliValue) > _mediaStart) {
			_media.setTime(_media.getTime() - stepMilliValue);
		}
	}
	
	/**
	 * Stop.
	 */
	public void stop() {
		_playBack = false;
		if (_media.isPlaying()) {
			_media.setPlaying(false);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.jaret.util.date.IntervalImpl#toString()
	 */
	@Override
	public String toString() {
		return label != null ? label : super.toString();
	}
	
	/**
	 * Update media time.
	 */
	public void updateMediaTime() {
		if (isMarkerInrange()) {
			int markerMillivalue = (int) _marker.getDate().getMillisInDay()
					+ _marker.getDate().getMillis();
			_media.setTime(markerMillivalue
					- (int) _collectionMedia.getOffSet());
		}
	}
	
}
