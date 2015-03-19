package synergyviewcore.timebar.action.mediasplit;

import org.eclipse.swt.SWT;

import synergyviewcore.collections.ui.AbstractMediaCollectionControl;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.timebar.action.BaseTimeBarAction;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

public class CreateSegmentAction extends BaseTimeBarAction {

	protected AbstractMediaCollectionControl _mediaCollectionControl;
	
    public CreateSegmentAction(TimeBarViewer tbv, AbstractMediaCollectionControl collectionControl) {
        super(tbv);  
        this._mediaCollectionControl = collectionControl;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
    	//
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return ResourceLoader.getString("TIMEBAR_MEDIA_CLIP_CONTEXTMENU_CREATE");
    }	

	@Override
	protected void init() {
		this.setEnabled(true);
		this.setAccelerator(SWT.ALT | 'I');
        this.setToolTipText(ResourceLoader.getString("TIMEBAR_MEDIA_CLIP_CONTEXTMENU_CREATE"));
        this.setImageDescriptor(ResourceLoader.getIconDescriptor("folder_add.png"));	
	}
	

}
