package synergyviewcore.timebar.action.mediasplit;

import org.eclipse.swt.SWT;

import synergyviewcore.collections.ui.AbstractMediaCollectionControl;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.timebar.action.BaseTimeBarAction;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * The Class CreateSegmentAction.
 */
public class CreateSegmentAction extends BaseTimeBarAction {

    /** The _media collection control. */
    protected AbstractMediaCollectionControl _mediaCollectionControl;

    /**
     * Instantiates a new creates the segment action.
     * 
     * @param tbv
     *            the tbv
     * @param collectionControl
     *            the collection control
     */
    public CreateSegmentAction(TimeBarViewer tbv, AbstractMediaCollectionControl collectionControl) {
	super(tbv);
	this._mediaCollectionControl = collectionControl;
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
	return ResourceLoader.getString("TIMEBAR_MEDIA_CLIP_CONTEXTMENU_CREATE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.timebar.action.BaseTimeBarAction#init()
     */
    @Override
    protected void init() {
	this.setEnabled(true);
	this.setAccelerator(SWT.ALT | 'I');
	this.setToolTipText(ResourceLoader.getString("TIMEBAR_MEDIA_CLIP_CONTEXTMENU_CREATE"));
	this.setImageDescriptor(ResourceLoader.getIconDescriptor("folder_add.png"));
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
	//
    }

}
