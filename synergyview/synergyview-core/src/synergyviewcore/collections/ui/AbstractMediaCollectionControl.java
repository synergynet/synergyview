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
 * @author phyo
 */
public abstract class AbstractMediaCollectionControl extends Composite {
	protected TimeBarViewer timeBarViewer;	
	protected List<CollectionMediaListener> listeners = new ArrayList<CollectionMediaListener>();
	protected CollectionNode collectionNode;
	protected MediaRootNode mediaRootNode;
	protected CollectionMedia currentListenedMedia;
	protected IObservableMap _mediaMap;
	private final ILog logger;
	protected long duration;


	//TODO May be this class should implement IMedia interface
	public AbstractMediaCollectionControl(Composite parent, int style, IObservableMap mediaMap, CollectionNode collectionNode, MediaRootNode mediaFolderValue) {
		super(parent, style);
		logger = Activator.getDefault().getLog();
		this.collectionNode = collectionNode;
		_mediaMap = mediaMap;
		mediaRootNode = mediaFolderValue;
		attachAbstractMedia();
	}


	protected void attachAbstractMedia(){

		for (CollectionMedia media:collectionNode.getResource().getCollectionMediaList()){
			AbstractMedia abstractMedia = mediaRootNode.getMediaNode(media.getMediaName()).createMediaInstance();
			abstractMedia.setMute(media.isMute());
			_mediaMap.put(media.getId(), abstractMedia);
		}	
		updateDuration();	
		updateMediaListener(0);
	}


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

	protected int getMediaCount(){
		return this.collectionNode.getResource().getCollectionMediaList().size();
	}

	public List<CollectionMedia> getCollectionMediaList(){
		return this.collectionNode.getResource().getCollectionMediaList();
	}

	public List<CollectionMediaClip> getCollectionMediaClip(){
		return this.collectionNode.getResource().getCollectionMediaClipList();
	}

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

	public void clearMediaClips(){
		this.collectionNode.clearClipCollection();

		for (CollectionMediaListener l:listeners){	
			l.MediaClipChanged();
		}		
	}

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

	protected void setMute(boolean mute){
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			((AbstractMedia)_mediaMap.get(media.getId())).setMute(mute);
		}
	}

	protected long getTime() {			
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			int mediaTime = ((AbstractMedia)_mediaMap.get(media.getId())).getTime();
			if (mediaTime > 0 && mediaTime < ((AbstractMedia)_mediaMap.get(media.getId())).getDuration()){
				return mediaTime + media.getOffSet();
			}
		}
		return 0;
	}

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

	protected long getDuration(){
		return this.duration;
	}

	protected String getFormattedDuration(){		
		return this.getFormattedTime((int) this.getDuration());
	}

	protected String getFormattedTime(){
		return this.getFormattedTime((int) this.getTime());
	}

	//TODO This is used in many places, may be this can be moved as a static method under media.Util class
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

	protected void playMedia(){
		long currentTime = getTime();

		this.playMedia(currentTime);

	}

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

	protected void stopMedia(){
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			((AbstractMedia)_mediaMap.get(media.getId())).setPlaying(false);
		}
	}
	
	protected void muteMedia(boolean mute){
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			((AbstractMedia)_mediaMap.get(media.getId())).setMute(mute);
		}
	}

	protected void setRate(PlayRate rate){
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			((AbstractMedia)_mediaMap.get(media.getId())).setRate(rate);
		}
	}

	protected PlayRate getRate(PlayRate rate){
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			return ((AbstractMedia)_mediaMap.get(media.getId())).getRate();
		}		
		return null;
	}

	protected boolean isEmpty(){
		if (this.collectionNode.getResource().getCollectionMediaList().size()<=0) 
			return true;
		else 
			return false;
	}

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

	protected void clearMediaListener(){
		for (CollectionMedia media:this.collectionNode.getResource().getCollectionMediaList()){
			((AbstractMedia) _mediaMap.get(media.getId())).removePropertyChangeListener(AbstractMedia.PROP_TIME, mediaChangeListener);
		}
	}

	public void addCollectionMediaListener(CollectionMediaListener mediaListener){
		listeners.add(mediaListener);
	}

	public interface CollectionMediaListener {
		public void mediaPlaying(long currentTime);
		public void mediaCollectionChanged(MediaListEvent arg);
		public void MediaClipChanged();
	}

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
	 * @return
	 */
	public TimeBarViewer getTimeBarViewer() {
		return timeBarViewer;
	}

}
