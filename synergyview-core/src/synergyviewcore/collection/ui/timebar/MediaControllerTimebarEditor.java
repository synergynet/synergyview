package synergyviewcore.collection.ui.timebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

import synergyviewcore.collection.model.MediaCollection;
import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.project.OpenedProjectController;
import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.mod.DefaultIntervalModificator;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultHeaderRenderer;

public class MediaControllerTimebarEditor extends TimeBarViewer {
	public static final long SPACER_IN_MILLI_SEC = 3000;
	private MediaCollectionTimebarModel mediaCollectionTimebarModel;

	public MediaControllerTimebarEditor(Composite parent, String mediaCollectionId) throws ObjectNotfoundException {
		super(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		MediaCollection mediaCollection = OpenedProjectController.getInstance().getMediaCollectionController().findMediaCollectionById(mediaCollectionId);
		setupViewer(mediaCollection);
	}
	private void setupViewer(MediaCollection mediaCollection) {
		mediaCollectionTimebarModel = new MediaCollectionTimebarModel(mediaCollection.getId());
		this.setModel(mediaCollectionTimebarModel);
		setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);
		setMilliAccuracy(true);
		setDrawRowGrid(true);
		setInitialDisplayRange(MediaCollectionTimebarModel.START_TIME.copy(), (int) ((mediaCollection.getDuration() + SPACER_IN_MILLI_SEC) / 1000));
		setHeaderRenderer(new DefaultHeaderRenderer());
		setAdjustMinMaxDatesByModel(true);
		this.addIntervalModificator(new DefaultIntervalModificator() {

			@Override
			public boolean isShiftingAllowed(TimeBarRow row, Interval interval) {
				return (interval instanceof MediaCollectionEntryInterval) ? true : false;
			}

			@Override
			public boolean shiftAllowed(TimeBarRow row, Interval interval,
					JaretDate newBegin) {
				return super.shiftAllowed(row, interval, newBegin);
				
			}
		});
	 	
		this.setAutoScaleRows(2);
		this.getSelectionModel().setRowSelectionAllowed(false);
		this.getSelectionModel().setMultipleSelectionAllowed(false);
		this.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				disposeResources();			
			}
		});
		this.setTimeBarRenderer(new PlayableMediaRenderer());
	}
	
	private void disposeResources() {
		if (mediaCollectionTimebarModel!=null) {
			mediaCollectionTimebarModel.dispose();
		}
	}

}
