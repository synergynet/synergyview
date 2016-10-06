package synergyviewcore.collection.ui.timebar;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import synergyviewcore.collection.model.MediaCollectionEntry;
import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.media.model.PlayableMedia;
import synergyviewcore.project.OpenedProjectController;

public class MediaCollectionEntryInterval extends PlayableMediaIntervalImpl {
	private MediaCollectionEntry mediaCollectionEntry;
	
	public MediaCollectionEntryInterval(String id) {
		
		try {
			mediaCollectionEntry = OpenedProjectController.getInstance().getMediaCollectionController().getMediaCollectionEntryController().findMediaCollectionEntryById(id);
			setupModel();
		} catch (ObjectNotfoundException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to find Media Collection Entry.", e.getMessage());
		}
	}
	
	@Override
	public void setupModel() {
		this.setBegin(MediaCollectionTimebarModel.START_TIME.copy().advanceMillis(mediaCollectionEntry.getOffset()));
		this.setEnd(_begin.copy().advanceMillis(mediaCollectionEntry.getMediaItem().getMediaInfo().getLengthInMilliSeconds()));
		this.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateOffset();
			}
			
		});
	}
	
	private void updateOffset() {
		try {
			mediaCollectionEntry.setOffset(getBegin().diffMilliSeconds(MediaCollectionTimebarModel.START_TIME));
			OpenedProjectController.getInstance().getMediaCollectionController().getMediaCollectionEntryController().updateMediaCollectionEntry(mediaCollectionEntry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getMediaCollectionEntryId() {
		return mediaCollectionEntry.getId();
	}

	@Override
	public PlayableMedia getMedia() {
		return mediaCollectionEntry.getMediaItem();
	}

}
