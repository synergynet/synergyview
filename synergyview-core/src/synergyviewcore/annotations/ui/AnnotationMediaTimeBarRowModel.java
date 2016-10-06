/**
 * File: MediaTimeBarRowModel.java Copyright (c) 2011 phyo This program is free
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

import java.util.ArrayList;
import java.util.List;

import synergyviewcore.annotations.ui.events.MediaTimeChangeListener;
import synergyviewcore.collections.model.CollectionMedia;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;

/**
 * The Class AnnotationMediaTimeBarRowModel.
 * 
 * @author phyo
 */
public class AnnotationMediaTimeBarRowModel extends DefaultTimeBarRowModel {
	
	/** The collection media list. */
	private List<CollectionMedia> collectionMediaList;
	
	/** The current time listener. */
	private MediaClipIntervalImpl currentTimeListener;
	
	/** The media time change listener. */
	private MediaTimeChangeListener mediaTimeChangeListener;
	
	/** The time available interval list. */
	private List<MediaClipIntervalImpl> timeAvailableIntervalList = new ArrayList<MediaClipIntervalImpl>();
	
	/**
	 * Instantiates a new annotation media time bar row model.
	 * 
	 * @param collectionMediaList
	 *            the collection media list
	 */
	public AnnotationMediaTimeBarRowModel(
			List<CollectionMedia> collectionMediaList) {
		super(new DefaultRowHeader("Media Clip"));
		this.collectionMediaList = collectionMediaList;
		
	}
	
	/**
	 * Gets the collection media list.
	 * 
	 * @return the collectionMediaList
	 */
	public List<CollectionMedia> getCollectionMediaList() {
		return collectionMediaList;
	}
	
	/**
	 * Gets the current time listener.
	 * 
	 * @return the currentTimeListener
	 */
	public MediaClipIntervalImpl getCurrentTimeListener() {
		return currentTimeListener;
	}
	
	/**
	 * Gets the media time change listener.
	 * 
	 * @return the mediaTimeChangeListener
	 */
	public MediaTimeChangeListener getMediaTimeChangeListener() {
		return mediaTimeChangeListener;
	}
	
	/**
	 * Gets the time available interval list.
	 * 
	 * @return the timeAvailableIntervalList
	 */
	public List<MediaClipIntervalImpl> getTimeAvailableIntervalList() {
		return timeAvailableIntervalList;
	}
	
	/**
	 * Sets the collection media list.
	 * 
	 * @param collectionMediaList
	 *            the collectionMediaList to set
	 */
	public void setCollectionMediaList(List<CollectionMedia> collectionMediaList) {
		this.collectionMediaList = collectionMediaList;
	}
	
	/**
	 * Sets the current time listener.
	 * 
	 * @param currentTimeListener
	 *            the currentTimeListener to set
	 */
	public void setCurrentTimeListener(MediaClipIntervalImpl currentTimeListener) {
		this.currentTimeListener = currentTimeListener;
	}
	
	/**
	 * Sets the media time change listener.
	 * 
	 * @param mediaTimeChangeListener
	 *            the mediaTimeChangeListener to set
	 */
	public void setMediaTimeChangeListener(
			MediaTimeChangeListener mediaTimeChangeListener) {
		this.mediaTimeChangeListener = mediaTimeChangeListener;
	}
	
	/**
	 * Sets the time available interval list.
	 * 
	 * @param timeAvailableIntervalList
	 *            the timeAvailableIntervalList to set
	 */
	public void setTimeAvailableIntervalList(
			List<MediaClipIntervalImpl> timeAvailableIntervalList) {
		this.timeAvailableIntervalList = timeAvailableIntervalList;
	}
	
}
