/**
 *  File: AbstractMediaCollectionControl.java
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

package synergyviewcore.collections.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import synergyviewcore.Activator;
import synergyviewcore.collections.model.CollectionMedia;
import synergyviewcore.collections.model.CollectionMediaClip;
import synergyviewcore.collections.model.CollectionMediaClipRowModel;
import synergyviewcore.collections.model.CollectionNode;
import synergyviewcore.media.model.AbstractMedia;
import synergyviewcore.media.model.MediaNode;
import synergyviewcore.media.model.MediaRootNode;
import synergyviewcore.media.model.IMedia.PlayRate;
import synergyviewcore.timebar.event.MediaListEvent;
import synergyviewcore.timebar.model.MediaSegmentIntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;


/**
 * The Class AbstractMediaCollectionControl.
 *
 * @author phyo
 */
public abstract class AbstractMediaCollectionControl extends Composite {
	
	/** The time bar viewer. */
	protected TimeBarViewer timeBarViewer;	
	
	/** The listeners. */
	protected List<CollectionMediaListener> listeners = new ArrayList<CollectionMediaListener>();
	
	/** The collection node. */
	protected CollectionNode collectionNode;
	
	/** The media root node. */
	protected MediaRootNode mediaRootNode;
	
	/** The current listened media. */
	protected CollectionMedia currentListenedMedia;
	
	/** The _media map. */
	protected IObservableMap _mediaMap;
	
	/** The logger. */
	private final ILog logger;
	
	/** The duration. */
	protected long duration;


	//TODO May be this class should implement IMedia interface
	/**
	 * Instantiates a new abstract media collection control.
	 *
	 * @param parent the parent
	 * @param style the style
	 * @param mediaMap the media map
	 * @param collectionNode the collection node
	 * @param mediaFolderValue the media folder value
	 */
	public AbstractMediaCollectionControl(Composite parent, int style, IObservableMap mediaMap, CollectionNode collectionNode, MediaRootNode mediaFolderValue) {
		super(parent, style);
		logger = Activator.getDefault().getLog();
		this.collectionNode = collectionNode;
		_mediaMap = mediaMap;
		mediaRootNode = mediaFolderValue;
		attachAbstractMedia();
	}


	/**
	 * Attach abstract media.
	 */
	protected void attachAbstractMedia(){

		for (CollectionMedia media:collectionNode.getResource().getCollectionMediaList()){
			AbstractMedia abstractMedia = mediaRootNode.getMediaNode(media.getMediaName()).createMediaInstance();
			abstractMedia.setMute(media.isMute());
			_mediaMap.put(media.getId(), abstractMedia);
		}	
		updateDuration();	
		updateMediaListener(0);
	}


	/**
	 * Adds the media.
	 *
	 * @param mediaNode the media node
	 */
	public void addMedia(MediaNode mediaNode){
		try{
			this.stopMedia();

			long currentTime = getTime();
			AbstractMedia abstractMedia = mediaNode.createMediaInstance();
			CollectionMedia collectionMedia = new CollectionMedia();
			collectionMedia.setId(UUID.randomUUID().toString());
			collectionMedia.setMediaName(mediaNode.getLabel());
			List<CollectionMedia> mediaToAddList = new ArrayList<CollectionMedia>();
			mediaToAddList.add(collectionMedia);
			collectionNode.addMedia(mediaToAddList);
			_mediaMap.put(collectionMedia.getId(), abstractMedia);
			abstractMedia.setTime((int) currentTime);

			updateDuration();
			this.updateMediaListener((int) currentTime);

			List<CollectionMedia> collectionMedias = new ArrayList<CollectionMedia>();
			collectionMedias.add(collectionMedia);
			for (CollectionMediaListener l:listeners){
				l.mediaCollectionChanged(new MediaListEvent(this, MediaListEvent.CollectionChangeType.MediaAdded, collectionMedias));
			}
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}

	}

	/**
	 * Adds the media clip.
	 */
	public void addMediaClip(){
		CollectionMediaClipRowModel row = (CollectionMediaClipRowModel) this.getTimeBarViewer().getModel().getRow(1);
		IInputValidator validator = new IInputValidator() {
			public String isValid(String newText) {
				if (!newText.equalsIgnoreCase("")) 
					 return null;
				else
					return "Name empty!";
			}
		};

		InputDialog dialog = new InputDialog(this.getShell(), "Input Segment Name", "Please input the name of the segment:", "", validator);
		if(dialog.open() == Window.OK) {

			JaretDate startDate = this.getTimeBarViewer().getStartDate();
			JaretDate endDate = this.getTimeBarViewer().getEndDate();
			long timeSpan = endDate.getDate().getTime()-startDate.getDate().getTime();

			int intervalLength = (int) (timeSpan/4);
			JaretDate intervalStartDate = startDate.copy().advanceMillis(timeSpan/2 - intervalLength/2);
			CollectionMediaClip clip = new CollectionMediaClip();

			MediaSegmentIntervalImpl interval = new MediaSegmentIntervalImpl(intervalStartDate, intervalStartDate.copy().advanceMillis(intervalLength), collectionNode, clip, row);
			//interval.setBegin(intervalStartDate);
			//interval.setEnd();
			interval.setLabel(dialog.getValue());
			row.addInterval(interval);
			clip.setId(UUID.randomUUID().toString());
			clip.setClipName(dialog.getValue());
			clip.setDuration(intervalLength);
			clip.setStartOffset((int) (timeSpan/2 - intervalLength/2));
			List<CollectionMediaClip> collectionMediaClips = new ArrayList<CollectionMediaClip>();
			collectionMediaClips.add(clip);
			try {
				this.collectionNode.addClip(collectionMediaClips);
			} catch (Exception e) {
				MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", String.format("Unable to add %s.", clip.getClipName()));
			}
		}
	}

	/**
	 * Removes the media.
	 *
	 * @param media the media
	 */
	public void removeMedia(CollectionMedia media){
		try {
			//		if (!_collection.getCollectionMediaList().contains(media)) return;
			//		
			//		this.stopMedia();
			//		
			//		//TODO Why remove all listeners, this is also called in updateMediaListener!
			clearMediaListener();
			//		
			//		//TODO What if currentListenedMedia is assign to other media and not the one that is to be removed
			this.currentListenedMedia = null;
			//		
			//		if (media.getMedia()!=null) {
			//			media.getMedia().dispose();
			//			media.setMedia(null);		
			//		}
			//		_collection.removeMedia(media);
			List<CollectionMedia> collectionMedias = new ArrayList<CollectionMedia>();
			collectionMedias.add(media);
			this.collectionNode.removeMedia(collectionMedias);
			_mediaMap.remove(media.getId());
			updateDuration();
			updateMediaListener(getTime());

			// Do we need to update removeMedia method to accept multiple media objects

			for (CollectionMediaListener l:listeners){	
				l.mediaCollectionChanged(new MediaListEvent(this, MediaListEvent.CollectionChangeType.MediaRemoved, collectionMedias ));
			}		
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);	
		}
	}

	//TODO This is not called from any where 
	/**
	 * Removes the media clip.
	 *
	 * @param mediaClip the media clip
	 */
	public void removeMediaClip(CollectionMediaClip mediaClip){
		List<CollectionMediaClip> collectionMediaClips = new ArrayList<CollectionMediaClip>();
		collectionMediaClips.add(mediaClip);
		try {
			this.collectionNode.removeClip(collectionMediaClips);

			for (CollectionMediaListener l:listeners){	
				l.MediaClipChanged();
			}
		} catch (Exception e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", String.format("Unable to delete media clips."));
		}
			
	}

	/**
	 * Gets the media count.
	 *
	 * @return the media count
	 */
	protected int getMediaCount(){
		return this.collectionNode.getResource().getCollectionMediaList().size();
	}

	/**
	 * Gets the collection media list.
	 *
	 * @return the collection media list
	 */
	public List<CollectionMedia> getCollectionMediaList(){
		return this.collectionNode.getResource().getCollectionMediaList();
	}

	/**
	 * Gets the collection media clip.
	 *
	 * @return the collection media clip
	 */
	public List<CollectionMediaClip> getCollectionMediaClip(){
		return this.collectionNode.getResource().getCollectionMediaClipList();
	}

	/**
	 * Clear medias.
	 */
	public void clearMedias(){	
		this.stopMedia();

		clearMediaListener();

		this.collectionNode.clearMediaCollection();
		for (CollectionMediaListener l:listeners){
			l.mediaCollectionChanged(new MediaListEvent(this, MediaListEvent.CollectionChangeType.MediaRemoved, this.collectionNode.getResource().getCollectionMediaList() ));
		}

		updateDuration();

		//TODO May be this should be set on the media list change listener
		this.currentListenedMedia = null;
	}

	/**
	 * Clear media clips.
	 */
	public void clearMediaClips(){
		this.collectionNode.clearClipCollection();

		for (CollectionMediaListener l:listeners){	
			l.MediaClipChanged();
		}		
	}

	/**
	 * Sets the time.
	 *
	 * @param time the new time
	 */
	protected void setTime(long time){

		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			if (time<media.getOffSet()){
				((AbstractMedia)_mediaMap.get(media.getId())).setTime(0);
			}
			else if (time>media.getOffSet()+((AbstractMedia)_mediaMap.get(media.getId())).getDuration()){
				((AbstractMedia)_mediaMap.get(media.getId())).setTime(((AbstractMedia)_mediaMap.get(media.getId())).getDuration());
			}
			else{
				((AbstractMedia)_mediaMap.get(media.getId())).setTime((int) (time-media.getOffSet()));
			}
		}
	}

	/**
	 * Sets the mute.
	 *
	 * @param mute the new mute
	 */
	protected void setMute(boolean mute){
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			((AbstractMedia)_mediaMap.get(media.getId())).setMute(mute);
		}
	}

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	protected long getTime() {			
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			int mediaTime = ((AbstractMedia)_mediaMap.get(media.getId())).getTime();
			if (mediaTime > 0 && mediaTime < ((AbstractMedia)_mediaMap.get(media.getId())).getDuration()){
				return mediaTime + media.getOffSet();
			}
		}
		return 0;
	}

	/**
	 * Update duration.
	 */
	protected void updateDuration(){
		long duration=0;
		for (CollectionMedia media: this.collectionNode.getResource().getCollectionMediaList()) {
			if (_mediaMap.containsKey(media.getId())) {
				long currentDuration = ((AbstractMedia)_mediaMap.get(media.getId())).getDuration() + media.getOffSet();
				if (duration<currentDuration)
					duration = currentDuration;
			}
		}
		this.duration = duration;
	}

	/**
	 * Gets the duration.
	 *
	 * @return the duration
	 */
	protected long getDuration(){
		return this.duration;
	}

	/**
	 * Gets the formatted duration.
	 *
	 * @return the formatted duration
	 */
	protected String getFormattedDuration(){		
		return this.getFormattedTime((int) this.getDuration());
	}

	/**
	 * Gets the formatted time.
	 *
	 * @return the formatted time
	 */
	protected String getFormattedTime(){
		return this.getFormattedTime((int) this.getTime());
	}

	//TODO This is used in many places, may be this can be moved as a static method under media.Util class
	/**
	 * Gets the formatted time.
	 *
	 * @param time the time
	 * @return the formatted time
	 */
	protected String getFormattedTime(int time){
		try {
			if (time == 0)
				return "00:00:00,000";
			Calendar currentTime = Calendar.getInstance();
			currentTime.setTimeInMillis(time);
			NumberFormat formatter = new DecimalFormat("00");
			NumberFormat miFormatter = new DecimalFormat("000");
			return String.format("%s:%s:%s,%s", formatter.format(currentTime
					.get(Calendar.HOUR_OF_DAY) - 1), formatter
					.format(currentTime.get(Calendar.MINUTE)), formatter
					.format(currentTime.get(Calendar.SECOND)), miFormatter
					.format(currentTime.get(Calendar.MILLISECOND)));
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.WARNING,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			return "00:00:00,000";
		}
	}

	/**
	 * Play media.
	 */
	protected void playMedia(){
		long currentTime = getTime();

		this.playMedia(currentTime);

	}

	/**
	 * Play media.
	 *
	 * @param currentTime the current time
	 */
	protected void playMedia(long currentTime){
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			if (currentTime>=media.getOffSet() && currentTime<=media.getOffSet()+((AbstractMedia)_mediaMap.get(media.getId())).getDuration()){
				((AbstractMedia)_mediaMap.get(media.getId())).setTime((int) (currentTime - media.getOffSet()));
				((AbstractMedia)_mediaMap.get(media.getId())).setPlaying(true);
			}
			else{
				((AbstractMedia)_mediaMap.get(media.getId())).setPlaying(false);
			}
		}
	}

	/**
	 * Stop media.
	 */
	protected void stopMedia(){
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			((AbstractMedia)_mediaMap.get(media.getId())).setPlaying(false);
		}
	}
	
	/**
	 * Mute media.
	 *
	 * @param mute the mute
	 */
	protected void muteMedia(boolean mute){
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			((AbstractMedia)_mediaMap.get(media.getId())).setMute(mute);
		}
	}

	/**
	 * Sets the rate.
	 *
	 * @param rate the new rate
	 */
	protected void setRate(PlayRate rate){
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			((AbstractMedia)_mediaMap.get(media.getId())).setRate(rate);
		}
	}

	/**
	 * Gets the rate.
	 *
	 * @param rate the rate
	 * @return the rate
	 */
	protected PlayRate getRate(PlayRate rate){
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			return ((AbstractMedia)_mediaMap.get(media.getId())).getRate();
		}		
		return null;
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	protected boolean isEmpty(){
		if (this.collectionNode.getResource().getCollectionMediaList().size()<=0) 
			return true;
		else 
			return false;
	}

	/**
	 * Gets the earlist media.
	 *
	 * @return the earlist media
	 */
	protected CollectionMedia getEarlistMedia(){
		CollectionMedia earlistMedia=null;
		if (this.collectionNode.getResource().getCollectionMediaList().size()>0)
			earlistMedia = this.collectionNode.getResource().getCollectionMediaList().get(0);
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			if (media.getOffSet()<earlistMedia.getOffSet())
				earlistMedia = media;
		}

		return earlistMedia;
	}


	/**
	 * Update media listener.
	 *
	 * @param time the time
	 */
	protected void updateMediaListener(long time){

		if (this.collectionNode.getResource().getCollectionMediaList().size()<=0) return;
		if (this.currentListenedMedia!=null && time<=this.currentListenedMedia.getOffSet()+((AbstractMedia)_mediaMap.get(currentListenedMedia.getId())).getDuration() && time>currentListenedMedia.getOffSet())
			return;

		clearMediaListener();
		CollectionMedia mediaListenTo = getEarlistMedia();
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			if (_mediaMap.containsKey(media.getId())) {
				if (time<=media.getOffSet()+((AbstractMedia)_mediaMap.get(media.getId())).getDuration() && time>media.getOffSet()){
					mediaListenTo = media;
					break;
				}
			}
		}	
		((AbstractMedia)_mediaMap.get(mediaListenTo.getId())).addPropertyChangeListener(AbstractMedia.PROP_TIME, mediaChangeListener);
		((AbstractMedia)_mediaMap.get(mediaListenTo.getId())).addPropertyChangeListener(AbstractMedia.PROP_TIME, mediaPlayingStatusChangeListener);
		this.currentListenedMedia = mediaListenTo;
	}

	/**
	 * Clear media listener.
	 */
	protected void clearMediaListener(){
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			((AbstractMedia) _mediaMap.get(media.getId())).removePropertyChangeListener(AbstractMedia.PROP_TIME, mediaChangeListener);
		}
	}

	/**
	 * Adds the collection media listener.
	 *
	 * @param mediaListener the media listener
	 */
	public void addCollectionMediaListener(CollectionMediaListener mediaListener){
		listeners.add(mediaListener);
	}

	/**
	 * The listener interface for receiving collectionMedia events.
	 * The class that is interested in processing a collectionMedia
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addCollectionMediaListener<code> method. When
	 * the collectionMedia event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see CollectionMediaEvent
	 */
	public interface CollectionMediaListener {
		
		/**
		 * Media playing.
		 *
		 * @param currentTime the current time
		 */
		public void mediaPlaying(long currentTime);
		
		/**
		 * Media collection changed.
		 *
		 * @param arg the arg
		 */
		public void mediaCollectionChanged(MediaListEvent arg);
		
		/**
		 * Media clip changed.
		 */
		public void MediaClipChanged();
	}

	/** The media change listener. */
	private PropertyChangeListener mediaChangeListener = new PropertyChangeListener(){
		public void propertyChange(final PropertyChangeEvent arg0) {
			AbstractMediaCollectionControl.this.getDisplay().asyncExec(new Runnable() {

				public void run() {
					if (arg0.getSource().hashCode()!=((AbstractMedia)_mediaMap.get(currentListenedMedia.getId())).hashCode()) return;
					final long newValue = Long.parseLong(arg0.getNewValue().toString());
					for (CollectionMediaListener l:listeners){

						l.mediaPlaying(newValue);
					}	           		            
				} 	
			});
		}
	};

	/** The media playing status change listener. */
	private PropertyChangeListener mediaPlayingStatusChangeListener = new PropertyChangeListener(){

		public void propertyChange(final PropertyChangeEvent arg0) {
			AbstractMediaCollectionControl.this.getDisplay().asyncExec(new Runnable() {

				public void run() {
					try{
						if (arg0.getSource().hashCode()!=((AbstractMedia)_mediaMap.get(currentListenedMedia.getId())).hashCode()) return;
					}catch(NullPointerException e){return;} 
					final boolean newValue = Boolean.parseBoolean(arg0.getNewValue().toString());

					if (!newValue){
						updateMediaListener(getTime());
					}

				} 
			});
		}
	};


	/**
	 * Gets the time bar viewer.
	 *
	 * @return the time bar viewer
	 */
	public TimeBarViewer getTimeBarViewer() {
		return timeBarViewer;
	}

}
