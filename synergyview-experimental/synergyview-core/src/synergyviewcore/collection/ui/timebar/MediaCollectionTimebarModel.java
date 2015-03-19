package synergyviewcore.collection.ui.timebar;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import synergyviewcore.collection.model.MediaCollection;
import synergyviewcore.collection.model.MediaCollectionEntry;
import synergyviewcore.media.model.PlayableMedia;
import synergyviewcore.project.OpenedProjectController;
import uk.ac.durham.tel.commons.collections.CollectionChangeEvent;
import uk.ac.durham.tel.commons.collections.CollectionChangeListener;
import uk.ac.durham.tel.commons.collections.CollectionDiffEntry;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;

public class MediaCollectionTimebarModel extends DefaultTimeBarModel {
	private static Logger logger = Logger.getLogger(MediaCollectionTimebarModel.class);
	public static final JaretDate START_TIME = new JaretDate().setTime(0, 0, 0, 0);
	private MediaCollection mediaCollection;
	private DefaultTimeBarRowModel defaultMediaCollectionRow = new DefaultTimeBarRowModel(new DefaultRowHeader("Default"));
	private DefaultTimeBarRowModel mediaCollectionEntryRow= new DefaultTimeBarRowModel(new DefaultRowHeader("Additions"));
	private Map<String, MediaCollectionEntryInterval> mediaCollectionEntryIntervalMap = new HashMap<String, MediaCollectionEntryInterval>();
	private CollectionChangeListener mediaCollectionEntryChangeListener = new CollectionChangeListener() {
		@Override
		public void listChanged(final CollectionChangeEvent event) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					for (CollectionDiffEntry<?> entry : event.getListDiff().getDifferences()) {
						if (entry.isAddition()) 
							addMediaCollectionEntryInterval((String) entry.getElement());
						else removeMediaCollectionEntryInterval((String) entry.getElement());
						logger.debug("Media collection entry changed");
					}
				}
				
			});
		}
	};
	
	public MediaCollectionTimebarModel(String mediaCollectionId) {
		setupModel(mediaCollectionId);
		addCollectionChangeListener();
	}

	protected void removeMediaCollectionEntryInterval(String id) {
		mediaCollectionEntryRow.remInterval(mediaCollectionEntryIntervalMap.get(id));
		mediaCollectionEntryIntervalMap.remove(id);
	}

	private void addCollectionChangeListener() {
		OpenedProjectController.getInstance().getMediaCollectionController().getMediaCollectionEntryController().addChangeListener(mediaCollectionEntryChangeListener);
	}
	
	private void removeCollectionChangeListener() {
		OpenedProjectController.getInstance().getMediaCollectionController().getMediaCollectionEntryController().removeChangeListener(mediaCollectionEntryChangeListener);
	}

	private void setupModel(String mediaCollectionId) {
		try {
			mediaCollection = OpenedProjectController.getInstance().getMediaCollectionController().findMediaCollectionById(mediaCollectionId);
			PlayableMedia media = (PlayableMedia) OpenedProjectController.getInstance().getMediaController().find(mediaCollection.getMediaItem().getId());
			defaultMediaCollectionRow.addInterval(new DefaultMediaIntervalImpl(media));
			this.addRow(defaultMediaCollectionRow);
			this._minDate = START_TIME.copy();
			this._maxDate = START_TIME.copy().advanceMillis(media.getMediaInfo().getLengthInMilliSeconds());
			setupMediaCollectionEntries();
			
		} catch (Exception e) {
			logger.error("Unable to setup model for media timerbar.", e);
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to setup model for media timerbar.", e.getMessage());
		}
	}

	private void setupMediaCollectionEntries() {
		for(MediaCollectionEntry mediaCollectionEntry : mediaCollection.getMediaCollectionEntryList())
			addMediaCollectionEntryInterval(mediaCollectionEntry.getId());
		this.addRow(mediaCollectionEntryRow);
	}

	public void dispose() {
		removeCollectionChangeListener();
	}
	
	private void addMediaCollectionEntryInterval(String id) {
		MediaCollectionEntryInterval mediaCollectionEntryInterval = new MediaCollectionEntryInterval(id); 
		mediaCollectionEntryRow.addInterval(mediaCollectionEntryInterval);
		mediaCollectionEntryIntervalMap.put(id, mediaCollectionEntryInterval);
	}
	
	private static class DefaultMediaIntervalImpl extends PlayableMediaIntervalImpl {
		
		private PlayableMedia mediaItem;
		
		public DefaultMediaIntervalImpl(PlayableMedia mediaItem) {
			this.mediaItem = mediaItem;
			setupModel();
		}

		@Override
		public void setupModel() {
			setBegin(START_TIME.copy());
			setEnd(START_TIME.copy().advanceMillis(mediaItem.getMediaInfo().getLengthInMilliSeconds()));
		}

		@Override
		public PlayableMedia getMedia() {
			return mediaItem;
		}
		
	}
}
