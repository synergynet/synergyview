/**
 * File: CollectionEditor.java Copyright (c) 2010 phyokyaw This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcore.collections.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.part.EditorPart;

import synergyviewcore.collections.model.CollectionMedia;
import synergyviewcore.collections.model.CollectionNode;
import synergyviewcore.collections.ui.AbstractMediaCollectionControl;
import synergyviewcore.collections.ui.MediaPreviewControl;
import synergyviewcore.media.model.MediaNode;
import synergyviewcore.navigation.NavigatorLabelProvider;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.projects.model.ProjectNode;
import synergyviewcore.projects.ui.NodeEditorInput;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.timebar.MediaTimeBar;

/**
 * The Class CollectionEditor.
 * 
 * @author phyokyaw
 */
public class CollectionEditor extends EditorPart {

    /** The Constant ID. */
    public static final String ID = "synergyviewcore.collections.ui.editors.collectionMediaEditor";

    /** The _collection node. */
    private CollectionNode _collectionNode;

    /** The _media collection control. */
    private AbstractMediaCollectionControl _mediaCollectionControl;

    /** The _media preview control. */
    private MediaPreviewControl _mediaPreviewControl;

    /**
     * Adds the collection media.
     */
    public void addCollectionMedia() {
	List<String> existingMediaNames = new ArrayList<String>();
	for (CollectionMedia media : _mediaCollectionControl.getCollectionMediaList()) {
	    existingMediaNames.add(media.getMediaName());
	}
	List<MediaNode> selectedMediaNodes = this.showMediaSelection(existingMediaNames.toArray(new String[] {}), _mediaCollectionControl);
	if (selectedMediaNodes != null) {
	    for (MediaNode node : selectedMediaNodes) {
		_mediaCollectionControl.addMedia(node);
	    }
	}

    }

    /**
     * Adds the collection media clip.
     */
    public void addCollectionMediaClip() {
	_mediaCollectionControl.addMediaClip();
    }

    /**
     * To add media controller.
     * 
     * @param parent
     *            the parent
     */
    private void addMediaController(Composite parent) {
	Composite container = new Composite(parent, SWT.BORDER | SWT.CENTER);
	container.setLayout(new GridLayout());
	_mediaCollectionControl = new MediaTimeBar(container, SWT.NULL, _mediaPreviewControl.getObservableMediaPreviewList(), _collectionNode, ((ProjectNode) _collectionNode.getLastParent()).getMediaRootNode());
	_mediaCollectionControl.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /**
     * Adds the media preview.
     * 
     * @param parent
     *            the parent
     */
    private void addMediaPreview(Composite parent) {
	Composite container = new Composite(parent, SWT.BORDER | SWT.CENTER);
	container.setLayout(new GridLayout());
	_mediaPreviewControl = new MediaPreviewControl(container, SWT.NONE);
	_mediaPreviewControl.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets .Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
	SashForm container = new SashForm(parent, SWT.VERTICAL);
	addMediaPreview(container);
	addMediaController(container);
	MenuManager menuManager = new MenuManager();
	menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	_mediaCollectionControl.getTimeBarViewer().setMenu(menuManager.createContextMenu(_mediaCollectionControl.getTimeBarViewer()));
	getSite().registerContextMenu(menuManager, _mediaCollectionControl.getTimeBarViewer());
	getSite().setSelectionProvider(_mediaCollectionControl.getTimeBarViewer());
	setPartName(String.format("%s (Collection)", _collectionNode.getLabel()));

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime. IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
	//
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
	//
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {

	this.setSite(site);
	this.setInput(input);

	_collectionNode = (CollectionNode) ((NodeEditorInput) input).getNode();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
	return false;
    }

    /**
     * Checks if is media added.
     * 
     * @return true, if is media added
     */
    public boolean isMediaAdded() {
	return !_mediaCollectionControl.getCollectionMediaList().isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
	//
    }

    /**
     * Show media selection.
     * 
     * @param excludeItemList
     *            the exclude item list
     * @param owner
     *            the owner
     * @return the list
     */
    private List<MediaNode> showMediaSelection(String[] excludeItemList, Composite owner) {
	List<INode> availableMediaList = ((ProjectNode) _collectionNode.getLastParent()).getMediaRootNode().getMediaNodes(excludeItemList);
	if (availableMediaList.size() >= 0) {
	    ElementListSelectionDialog dialog = new ElementListSelectionDialog(owner.getShell(), new NavigatorLabelProvider());
	    dialog.setMultipleSelection(true);
	    dialog.setElements(availableMediaList.toArray(new INode[availableMediaList.size()]));
	    dialog.setTitle(ResourceLoader.getString("SESSION_PROPERTY_MEDIA_SELECTOR_TITLE"));
	    dialog.open();
	    Object[] selectedObjects = dialog.getResult();
	    List<MediaNode> nodeList = new ArrayList<MediaNode>();
	    if (selectedObjects != null) {

		for (Object node : selectedObjects) {
		    nodeList.add((MediaNode) node);
		}
	    }
	    return nodeList;

	} else {
	    MessageDialog.openError(owner.getShell(), ResourceLoader.getString("DIALOG_ERROR_TITLE"), ResourceLoader.getString("SESSION_PROPERTY_MEDIA_SELECTOR_NO_MEDIA"));
	    return null;
	}
    }

}
