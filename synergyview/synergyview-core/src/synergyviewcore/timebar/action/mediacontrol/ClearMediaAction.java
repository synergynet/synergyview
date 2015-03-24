package synergyviewcore.timebar.action.mediacontrol;

import org.eclipse.swt.SWT;

import synergyviewcore.collections.model.CollectionNode;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.timebar.action.BaseTimeBarAction;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;


/**
 * The Class ClearMediaAction.
 */
public class ClearMediaAction extends BaseTimeBarAction {

	/** The _media control. */
	CollectionNode _mediaControl;
    
    /**
     * Instantiates a new clear media action.
     *
     * @param tbv the tbv
     * @param control the control
     */
    public ClearMediaAction(TimeBarViewer tbv, CollectionNode control) {
        super(tbv);      
        _mediaControl = control;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {     
    	
    	_mediaControl.clearMediaCollection();
    	DefaultTimeBarRowModel row = (DefaultTimeBarRowModel)_tbv.getModel().getRow(0);
    	row.clear(); 
	
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
    	return ResourceLoader.getString("TIMEBAR_MEDIA_CONTEXTMENU_CLEAR");
    }

	/* (non-Javadoc)
	 * @see synergyviewcore.timebar.action.BaseTimeBarAction#init()
	 */
	@Override
	protected void init() {
		this.setEnabled(true);
        this.setAccelerator(SWT.DEL);
        this.setToolTipText(ResourceLoader.getString("TIMEBAR_MEDIA_CONTEXTMENU_CLEAR"));		
	}

}
