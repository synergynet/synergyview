package synergyviewmvc.timebar.action.mediasplit;

import org.eclipse.swt.SWT;

import synergyviewmvc.collections.ui.AbstractMediaCollectionControl;
import synergyviewmvc.resource.ResourceLoader;
import synergyviewmvc.timebar.action.BaseTimeBarAction;

import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

public class ClearSegmentsAction extends BaseTimeBarAction {

	protected AbstractMediaCollectionControl collectionControl;
	
    public ClearSegmentsAction(TimeBarViewer tbv , AbstractMediaCollectionControl collectionControl) {
        super(tbv);
        this.collectionControl = collectionControl;
       
    }

    public void run() {
    	
    	DefaultTimeBarRowModel row = (DefaultTimeBarRowModel)_tbv.getModel().getRow(1);
    	row.clear();  
    	
    	this.collectionControl.clearMediaClips();
    	 	
    }

	@Override
	protected void init() {
		this.setEnabled(true);
        this.setAccelerator(SWT.DEL);
        this.setToolTipText(ResourceLoader.getString("TIMEBAR_MEDIA_CLIP_CONTEXTMENU_CLEAR"));		
	}
	
	/**
     * {@inheritDoc}
     */
    public String getText() {
        return ResourceLoader.getString("TIMEBAR_MEDIA_CLIP_CONTEXTMENU_CLEAR");
    }
    

}
