package synergyviewmvc.timebar.action.mediacontrol;

import org.eclipse.swt.SWT;

import synergyviewmvc.collections.model.CollectionNode;
import synergyviewmvc.resource.ResourceLoader;
import synergyviewmvc.timebar.action.BaseTimeBarAction;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

public class ClearMediaAction extends BaseTimeBarAction {

	CollectionNode _mediaControl;
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

	@Override
	protected void init() {
		this.setEnabled(true);
        this.setAccelerator(SWT.DEL);
        this.setToolTipText(ResourceLoader.getString("TIMEBAR_MEDIA_CONTEXTMENU_CLEAR"));		
	}

}
