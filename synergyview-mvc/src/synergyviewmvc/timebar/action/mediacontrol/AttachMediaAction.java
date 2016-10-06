package synergyviewmvc.timebar.action.mediacontrol;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import synergyviewmvc.collections.ui.AbstractMediaCollectionControl;
import synergyviewmvc.media.model.MediaNode;
import synergyviewmvc.media.model.MediaRootNode;
import synergyviewmvc.navigation.NavigatorLabelProvider;
import synergyviewmvc.resource.ResourceLoader;
import synergyviewmvc.timebar.action.BaseTimeBarAction;
import uk.ac.durham.tel.commons.jface.node.INode;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

public class AttachMediaAction extends BaseTimeBarAction {

	protected MediaRootNode mediaFolder;
	protected AbstractMediaCollectionControl collectionControl;
    public AttachMediaAction(TimeBarViewer tbv, MediaRootNode mediaFolder, AbstractMediaCollectionControl collectionControl) {
        super(tbv);   
        this.mediaFolder = mediaFolder;		
        this.collectionControl = collectionControl;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
         		
    	String[] arr=new String[0];
    	MediaNode mediaNode=this.showMediaSelection(arr, this._tbv.getParent());
    	collectionControl.addMedia(mediaNode);
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return ResourceLoader.getString("TIMEBAR_MEDIA_CONTEXTMENU_CREATE");
    }

	@Override
	protected void init() {
		this.setEnabled(true);
        this.setAccelerator(SWT.ALT | 'A');
        this.setToolTipText(ResourceLoader.getString("TIMEBAR_MEDIA_CONTEXTMENU_CREATE"));
        this.setImageDescriptor(ResourceLoader.getIconDescriptor("folder_add.png"));		
	}	

	private MediaNode showMediaSelection(String[] excludeItemList,
			Composite owner) {
		List<INode> availableMediaList = mediaFolder.getMediaNodes(
						excludeItemList);
		if (availableMediaList.size() >= 0) {
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(
					owner.getShell(), new NavigatorLabelProvider());
			dialog.setMultipleSelection(false);
			dialog.setElements(availableMediaList
					.toArray(new INode[availableMediaList.size()]));
			dialog.setTitle(ResourceLoader
					.getString("SESSION_PROPERTY_MEDIA_SELECTOR_TITLE"));
			dialog.open();
			return ((MediaNode) dialog.getFirstResult());
		} else {
			MessageDialog.openError(owner.getShell(), ResourceLoader
					.getString("DIALOG_ERROR_TITLE"), ResourceLoader
					.getString("SESSION_PROPERTY_MEDIA_SELECTOR_NO_MEDIA"));
			return null;
		}
	}
	
	
}
