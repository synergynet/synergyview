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

public class DeleteMediaAction extends BaseTimeBarAction {
	
	CollectionNode mediaCollection;
    public DeleteMediaAction(TimeBarViewer tbv, CollectionNode control) {
        super(tbv);      
        mediaCollection = control;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
         	
    
    	DefaultTimeBarRowModel row = (DefaultTimeBarRowModel)_tbv.getModel().getRow(0);
    	List<Interval> intervals = _tbv.getSelectionModel().getSelectedIntervals();
    	List<CollectionMedia> collectionToRemove = new ArrayList<CollectionMedia>();
    	for (Interval interval:intervals){
    		CollectionMedia media = ((MediaIntervalImpl)interval).getCollectionMedia();
    		collectionToRemove.add(media);
    	} 
    	mediaCollection.removeMedia(collectionToRemove);
    	row.remIntervals(intervals);
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return ResourceLoader.getString("TIMEBAR_MEDIA_CONTEXTMENU_DELETE");
    }
	
	@Override
	protected void init() {
		this.setEnabled(true);
        this.setToolTipText(ResourceLoader.getString("TIMEBAR_MEDIA_CONTEXTMENU_DELETE"));	
	}

}
