/**
 *  File: MediaIntervalImpl.java
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

package synergyviewmvc.annotations.ui;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.widgets.Composite;

import synergyviewmvc.annotations.ui.events.MarkerInRangeEvent;
import synergyviewmvc.annotations.ui.events.MarkerInRangeListener;
import synergyviewmvc.annotations.ui.events.MediaTimeChangeListener;
import synergyviewmvc.annotations.ui.events.TimeChangeEvent;
import synergyviewmvc.collections.model.CollectionMedia;
import synergyviewmvc.media.model.AbstractMedia;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarMarkerListener;

/**
 * @author phyo
 *
 */
public class MediaClipIntervalImpl extends IntervalImpl {
	protected String label;
	private CollectionMedia _collectionMedia;
	private AbstractMedia _media;
	private long _startIntervalOffSet;
	private long _endIntervalOffSet;
	private long _mediaStart;
	private long _mediaEnd;
	private boolean _playBack;
	private boolean _timeAvailable;
	private boolean _leftMedia;
	private TimeBarMarkerImpl _marker;
	private JaretDate _clipStartDate;
	private JaretDate _clipEndDate;
	private Composite _uiOwner;
	private List<MediaTimeChangeListener> timeChangedListeners = new CopyOnWriteArrayList<MediaTimeChangeListener>();
	private List<MarkerInRangeListener> timeAvailableListeners = new CopyOnWriteArrayList<MarkerInRangeListener>();

	private PropertyChangeListener _propertyChangeListener;

	protected MediaClipIntervalImpl() {
		super();
	}

	public void setMute(boolean muteValue) {
		_media.setMute(muteValue);
	}

	public boolean isMute() {
		return _media.isMute();
	}

	public MediaClipIntervalImpl(CollectionMedia collectionMedia, AbstractMedia mediaPreview, JaretDate clipStartDate, int clipDuration, TimeBarMarkerImpl marker, Composite uiOwnerValue) {
		super();
		_uiOwner = uiOwnerValue;
		_collectionMedia = collectionMedia;
		_marker = marker;
		_media = mediaPreview;
		_clipStartDate = clipStartDate.copy();
		_clipEndDate = clipStartDate.copy().advanceMillis(clipDuration);
		int startDateMilliValue = (int) clipStartDate.getMillisInDay() + clipStartDate.getMillis();
		if (startDateMilliValue >=  collectionMedia.getOffSet()) {
			_startIntervalOffSet = 0;
			this.setBegin(clipStartDate.copy());
			_mediaStart = startDateMilliValue - (int)collectionMedia.getOffSet();
			_media.setTime((int) _mediaStart);
			_leftMedia = true;
		}
		else {
			_startIntervalOffSet = collectionMedia.getOffSet() - startDateMilliValue;
			_mediaStart = 0;
			this.setBegin(clipStartDate.copy().advanceMillis(_startIntervalOffSet));
			_leftMedia = false;
		}

		int mediaEndValue = (int) collectionMedia.getOffSet() + _media.getDuration();
		if (startDateMilliValue + clipDuration > mediaEndValue) {
			_endIntervalOffSet = _media.getDuration() + (int) collectionMedia.getOffSet() - startDateMilliValue;
			this.setEnd(clipStartDate.copy().advanceMillis(_endIntervalOffSet));
			_mediaEnd = _media.getDuration();
		}
		else 
		{
			_endIntervalOffSet = clipDuration;
			this.setEnd(clipStartDate.copy().advanceMillis(_endIntervalOffSet));
			_mediaEnd = mediaEndValue - _endIntervalOffSet + startDateMilliValue;
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
						for (MarkerInRangeListener listener : timeAvailableListeners) {
							listener.markerInRangeChanged(new MarkerInRangeEvent(MediaClipIntervalImpl.this, _timeAvailable));
						}
					}

				} else {
					if (_timeAvailable) {
						_timeAvailable = false;
						for (MarkerInRangeListener listener : timeAvailableListeners) {
							listener.markerInRangeChanged(new MarkerInRangeEvent(MediaClipIntervalImpl.this, _timeAvailable));
						}
					}
				}
			}

		});

		if (isMarkerInrange()) 
			_timeAvailable = true;
		else 
			_timeAvailable = false;

		_propertyChangeListener = new PropertyChangeListener() {

			public void propertyChange(java.beans.PropertyChangeEvent evt) {
				if (_uiOwner!=null && !_uiOwner.isDisposed()){
					_uiOwner.getDisplay().asyncExec(new Runnable() {

						public void run() {
							JaretDate currentTimeDate = getCurrentTimeDate();
							if (currentTimeDate.compareTo(_clipEndDate)>=0) {
								_media.setPlaying(false);
								_playBack = false;
							}

							TimeChangeEvent event = new TimeChangeEvent(MediaClipIntervalImpl.this, currentTimeDate);
							for (MediaTimeChangeListener listener : timeChangedListeners) {
								listener.playBackChanged(event);
							}

						}

					});
				}
			}

		};

		_media.addPropertyChangeListener(AbstractMedia.PROP_TIME, _propertyChangeListener);
		_media.setMute(collectionMedia.isMute());
	}

	private JaretDate getCurrentTimeDate() {
		if (_leftMedia) {
			int currentTimeValue = _media.getTime() - (int)_mediaStart;
			return  _clipStartDate.copy().advanceMillis(currentTimeValue);

		} else {
			long diff = MediaClipIntervalImpl.this.getBegin().diffMilliSeconds(_clipStartDate);
			return _clipStartDate.copy().advanceMillis(_media.getTime() + diff);
		}
	}

	public boolean isTimeAvailable() {
		return _timeAvailable;
	}

	public void stepRE(int stepMilliValue) {
		if (_media.getTime() - stepMilliValue > _mediaStart) {
			_media.setTime(_media.getTime() - stepMilliValue);
		}
	}

	public void updateMediaTime() {
		if (isMarkerInrange()) {
			int markerMillivalue = (int) _marker.getDate().getMillisInDay() + _marker.getDate().getMillis();
			_media.setTime(markerMillivalue - (int)_collectionMedia.getOffSet());
		}
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

	@Override
	public String toString() {
		return label!=null?label:super.toString();
	}

	public void play() {
		_playBack = true;
		if (isMarkerInrange()) {
			if (!_media.isPlaying()) {
				updateMediaTime();
				_media.setPlaying(true);
			}
		}
	}

	public void stop() {
		_playBack = false;
		if (_media.isPlaying())
			_media.setPlaying(false);
	}

	public void addTimeChangeListener(MediaTimeChangeListener listener) {
		timeChangedListeners.add(listener);
	}

	public void removeTimeChangeListener(MediaTimeChangeListener listener) {
		if (timeChangedListeners.contains(listener))
			timeChangedListeners.remove(listener);
	}

	public void addClipInMarkerRangeListener(MarkerInRangeListener listener) {
		timeAvailableListeners.add(listener);
	}

	public void removeTimeAvailableListener(MarkerInRangeListener listener) {
		if (timeAvailableListeners.contains(listener))
			timeAvailableListeners.remove(listener);
	}

	private boolean isMarkerInrange() {
		if (_marker.getDate().compareTo(this.getBegin()) >= 0 && _marker.getDate().compareTo(this.getEnd()) < 0)
			return true;
		else return false;
	}

	/**
	 * @param stepReMilli
	 */
	public void stepFF(int stepMilliValue) {
		if (_marker.getDate().copy().advanceMillis(stepMilliValue).compareTo(_end) < 0 ) {
			_media.setTime(_media.getTime() + stepMilliValue);
		}
	}

}
