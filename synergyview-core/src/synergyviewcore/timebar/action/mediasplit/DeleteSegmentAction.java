package synergyviewcore.timebar.action.mediasplit;

import java.util.List;

import synergyviewcore.collections.ui.AbstractMediaCollectionControl;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.timebar.action.BaseTimeBarAction;
import synergyviewcore.timebar.model.MediaSegmentIntervalImpl;
import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * The Class DeleteSegmentAction.
 */
public class DeleteSegmentAction extends BaseTimeBarAction {
	
	/** The collection control. */
	protected AbstractMediaCollectionControl collectionControl;
	
	/**
	 * Instantiates a new delete segment action.
	 * 
	 * @param tbv
	 *            the tbv
	 * @param collectionControl
	 *            the collection control
	 */
	public DeleteSegmentAction(TimeBarViewer tbv,
			AbstractMediaCollectionControl collectionControl) {
		super(tbv);
		this.collectionControl = collectionControl;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getText() {
		return ResourceLoader
				.getString("TIMEBAR_MEDIA_CLIP_CONTEXTMENU_DELETE");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.timebar.action.BaseTimeBarAction#init()
	 */
	@Override
	protected void init() {
		this.setEnabled(true);
		this.setToolTipText(ResourceLoader
				.getString("TIMEBAR_MEDIA_CLIP_CONTEXTMENU_DELETE"));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void run() {
		
		DefaultTimeBarRowModel row = (DefaultTimeBarRowModel) _tbv.getModel()
				.getRow(1);
		List<Interval> intervals = _tbv.getSelectionModel()
				.getSelectedIntervals();
		for (Interval interval : intervals) {
			MediaSegmentIntervalImpl sgementInterval = ((MediaSegmentIntervalImpl) interval);
			this.collectionControl.removeMediaClip(sgementInterval
					.getCollectionMediaClip());
		}
		row.remIntervals(intervals);
		
	}
	
}
