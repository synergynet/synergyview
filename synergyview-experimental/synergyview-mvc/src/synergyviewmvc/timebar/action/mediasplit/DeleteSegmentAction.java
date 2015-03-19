package synergyviewmvc.timebar.action.mediasplit;

import java.util.List;

import synergyviewmvc.collections.ui.AbstractMediaCollectionControl;
import synergyviewmvc.resource.ResourceLoader;
import synergyviewmvc.timebar.action.BaseTimeBarAction;
import synergyviewmvc.timebar.model.MediaSegmentIntervalImpl;

import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

public class DeleteSegmentAction extends BaseTimeBarAction {

	protected AbstractMediaCollectionControl collectionControl;
    public DeleteSegmentAction(TimeBarViewer tbv, AbstractMediaCollectionControl collectionControl) {
        super(tbv);
        this.collectionControl = collectionControl;
       
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
         	
    	DefaultTimeBarRowModel row = (DefaultTimeBarRowModel)_tbv.getModel().getRow(1);
    	List<Interval> intervals = _tbv.getSelectionModel().getSelectedIntervals();
    	for (Interval interval:intervals){
    		MediaSegmentIntervalImpl sgementInterval =((MediaSegmentIntervalImpl)interval);
    		this.collectionControl.removeMediaClip(sgementInterval.getCollectionMediaClip());
    	}
    	row.remIntervals(intervals);
    	
    	
    	
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return ResourceLoader.getString("TIMEBAR_MEDIA_CLIP_CONTEXTMENU_DELETE");
    }
	
	@Override
	protected void init() {
		this.setEnabled(true);
        this.setToolTipText(ResourceLoader.getString("TIMEBAR_MEDIA_CLIP_CONTEXTMENU_DELETE"));	
	}

}
