package synergyviewmvc.timebar.action.mediasplit;

import java.util.UUID;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import synergyviewmvc.collections.model.CollectionMediaClip;
import synergyviewmvc.collections.ui.AbstractMediaCollectionControl;
import synergyviewmvc.resource.ResourceLoader;
import synergyviewmvc.timebar.action.BaseTimeBarAction;
import synergyviewmvc.timebar.model.MediaSegmentIntervalImpl;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
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
