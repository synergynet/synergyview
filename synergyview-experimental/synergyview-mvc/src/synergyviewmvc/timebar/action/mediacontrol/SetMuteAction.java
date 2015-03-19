package synergyviewmvc.timebar.action.mediacontrol;

import java.util.List;

import synergyviewmvc.collections.model.CollectionMedia;
import synergyviewmvc.collections.ui.AbstractMediaCollectionControl;
import synergyviewmvc.resource.ResourceLoader;
import synergyviewmvc.timebar.action.BaseTimeBarAction;
import synergyviewmvc.timebar.model.MediaIntervalImpl;

import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

public class SetMuteAction extends BaseTimeBarAction {
	
	protected final static String MUTE_ON="Mute On";
	protected final static String MUTE_OFF="Mute Off";
	protected String muteState = MUTE_OFF;
	
	AbstractMediaCollectionControl mediaCollection;
    public SetMuteAction(TimeBarViewer tbv, AbstractMediaCollectionControl medias) {
        super(tbv);      
        mediaCollection = medias;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
         	
//    	List<Interval> intervals = _tbv.getSelectionModel().getSelectedIntervals();
//    	for (Interval interval:intervals){
//    		CollectionMedia media = ((MediaIntervalImpl)interval).getCollectionMedia();
//    		if (muteState.equals(MUTE_ON)){
//    			media.getMedia().setMute(false);
//    			muteState=MUTE_OFF;
//    		}
//    		else{
//    			media.getMedia().setMute(true);
//    			muteState=MUTE_ON;
//    		}
//    	}  	
	
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return ResourceLoader.getString("TIMEBAR_MEDIA_CONTEXTMENU_MUTE");
    }
	
	@Override
	protected void init() {
		this.setEnabled(true);
        this.setToolTipText(ResourceLoader.getString("TIMEBAR_MEDIA_CONTEXTMENU_DELETE"));	
	}

}
