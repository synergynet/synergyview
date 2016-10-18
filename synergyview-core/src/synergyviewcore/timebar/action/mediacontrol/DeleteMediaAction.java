package synergyviewcore.timebar.action.mediacontrol;

import java.util.ArrayList;
import java.util.List;

import synergyviewcore.collections.model.CollectionMedia;
import synergyviewcore.collections.model.CollectionNode;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.timebar.action.BaseTimeBarAction;
import synergyviewcore.timebar.model.MediaIntervalImpl;
import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * The Class DeleteMediaAction.
 */
public class DeleteMediaAction extends BaseTimeBarAction {

    /** The media collection. */
    CollectionNode mediaCollection;

    /**
     * Instantiates a new delete media action.
     * 
     * @param tbv
     *            the tbv
     * @param control
     *            the control
     */
    public DeleteMediaAction(TimeBarViewer tbv, CollectionNode control) {
	super(tbv);
	mediaCollection = control;
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
	return ResourceLoader.getString("TIMEBAR_MEDIA_CONTEXTMENU_DELETE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.timebar.action.BaseTimeBarAction#init()
     */
    @Override
    protected void init() {
	this.setEnabled(true);
	this.setToolTipText(ResourceLoader.getString("TIMEBAR_MEDIA_CONTEXTMENU_DELETE"));
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

	DefaultTimeBarRowModel row = (DefaultTimeBarRowModel) _tbv.getModel().getRow(0);
	List<Interval> intervals = _tbv.getSelectionModel().getSelectedIntervals();
	List<CollectionMedia> collectionToRemove = new ArrayList<CollectionMedia>();
	for (Interval interval : intervals) {
	    CollectionMedia media = ((MediaIntervalImpl) interval).getCollectionMedia();
	    collectionToRemove.add(media);
	}
	mediaCollection.removeMedia(collectionToRemove);
	row.remIntervals(intervals);
    }

}
