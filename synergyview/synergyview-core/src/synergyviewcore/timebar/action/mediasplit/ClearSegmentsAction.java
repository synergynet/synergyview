package synergyviewcore.timebar.action.mediasplit;

import org.eclipse.swt.SWT;

import synergyviewcore.collections.ui.AbstractMediaCollectionControl;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.timebar.action.BaseTimeBarAction;

import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;


/**
 * The Class ClearSegmentsAction.
 */
public class ClearSegmentsAction extends BaseTimeBarAction {

	/** The collection control. */
	protected AbstractMediaCollectionControl collectionControl;
	
    /**
     * Instantiates a new clear segments action.
     *
     * @param tbv the tbv
     * @param collectionControl the collection control
     */
    public ClearSegmentsAction(TimeBarViewer tbv , AbstractMediaCollectionControl collectionControl) {
        super(tbv);
        this.collectionControl = collectionControl;
       
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
    	
    	DefaultTimeBarRowModel row = (DefaultTimeBarRowModel)_tbv.getModel().getRow(1);
    	row.clear();  
    	
    	this.collectionControl.clearMediaClips();
    	 	
    }

	/* (non-Javadoc)
	 * @see synergyviewcore.timebar.action.BaseTimeBarAction#init()
	 */
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
