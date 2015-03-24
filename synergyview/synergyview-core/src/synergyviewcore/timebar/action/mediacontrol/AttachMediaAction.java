package synergyviewcore.timebar.action.mediacontrol;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import synergyviewcore.collections.ui.AbstractMediaCollectionControl;
import synergyviewcore.media.model.MediaNode;
import synergyviewcore.media.model.MediaRootNode;
import synergyviewcore.navigation.NavigatorLabelProvider;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.timebar.action.BaseTimeBarAction;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;


/**
 * The Class AttachMediaAction.
 */
public class AttachMediaAction extends BaseTimeBarAction {

	/** The media folder. */
	protected MediaRootNode mediaFolder;
	
	/** The collection control. */
	protected AbstractMediaCollectionControl collectionControl;
    
    /**
     * Instantiates a new attach media action.
     *
     * @param tbv the tbv
     * @param mediaFolder the media folder
     * @param collectionControl the collection control
     */
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

	/* (non-Javadoc)
	 * @see synergyviewcore.timebar.action.BaseTimeBarAction#init()
	 */
	@Override
	protected void init() {
		this.setEnabled(true);
        this.setAccelerator(SWT.ALT | 'A');
        this.setToolTipText(ResourceLoader.getString("TIMEBAR_MEDIA_CONTEXTMENU_CREATE"));
        this.setImageDescriptor(ResourceLoader.getIconDescriptor("folder_add.png"));		
	}	

	/**
	 * Show media selection.
	 *
	 * @param excludeItemList the exclude item list
	 * @param owner the owner
	 * @return the media node
	 */
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
