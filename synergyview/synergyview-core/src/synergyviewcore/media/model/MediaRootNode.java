package synergyviewcore.media.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcore.Activator;
import synergyviewcore.navigation.model.AbstractParent;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.navigation.model.IParentNode;
import synergyviewcore.resource.ResourceLoader;



/**
 * The Class MediaRootNode.
 */
public class MediaRootNode extends AbstractParent<IFolder> implements IResourceChangeListener {
	
	/** The Constant MEDIA_ROOT_NODE_ICON. */
	public static final String MEDIA_ROOT_NODE_ICON = "folder_camera.png";

	/**
	 * Instantiates a new media root node.
	 *
	 * @param folderValue the folder value
	 * @param parentValue the parent value
	 */
	public MediaRootNode(IFolder folderValue, IParentNode parentValue) {
		super(folderValue, parentValue);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
		
		this.setLabel(MediaRootNode.getMediaFolderName());
		try {
			for(IResource resource : folderValue.members()) {
				if (resource instanceof IFile) {
					IFile file = (IFile) resource;
					addNewMedia(file);
				}
			}
		} catch (CoreException ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}
	
	/**
	 * Gets the media folder name.
	 *
	 * @return the media folder name
	 */
	public static String getMediaFolderName() {
		return ResourceLoader.getString("NAME_MEDIA_FOLDER");
	}
	
	/**
	 * Adds the new media.
	 *
	 * @param file the file
	 */
	private void addNewMedia(IFile file) {
		String ext = file.getFileExtension();
		INode mediaFile;
		if (MediaNode.isValidExtension(ext))
		{
			mediaFile = new MediaNode(file, this);
		}
		else if (ImageNode.isValidExtension(ext))
		{
			mediaFile = new ImageNode(file,this);
		}
		else 
		{
			mediaFile = new MiscNode(file,this);
		} 
		_children.add(mediaFile);
		fireChildrenChanged();
	}

	/**
	 * Adds the new.
	 *
	 * @param mediaValue the media value
	 */
	public void addNew(INode mediaValue) {
		
	}


	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.AbstractNode#getIcon()
	 */
	public ImageDescriptor getIcon() {
		return ResourceLoader.getIconDescriptor(MEDIA_ROOT_NODE_ICON);
	}

	/**
	 * Gets the media nodes.
	 *
	 * @param excludedItemNames the excluded item names
	 * @return the media nodes
	 */
	public List<INode> getMediaNodes(String[] excludedItemNames) {
		List<INode> result = new ArrayList<INode>();
		for(INode node : _children) {
			if (node instanceof MediaNode) {
				if (excludedItemNames.length > 0) {
					boolean itemFound = false;
					for (String excludedItemName : excludedItemNames) {
						if (node.getLabel().compareToIgnoreCase(excludedItemName)==0) {
							itemFound = true;
						}
					}
					if (!itemFound)
						result.add(node);
				}
				else 
					result.add(node);
			}
		}
		return result;
	}
	
	/**
	 * Gets the media node.
	 *
	 * @param mediaName the media name
	 * @return the media node
	 */
	public MediaNode getMediaNode(String mediaName) {
		for(INode node : _children) {
			if (node instanceof MediaNode && node.getLabel().compareToIgnoreCase(mediaName)==0)
				return (MediaNode) node;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			event.getDelta().accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) throws CoreException {
					if (delta.getKind() == IResourceDelta.ADDED && delta.getResource() instanceof IFile) {
						IFile mediaFile = (IFile) delta.getResource();
						IFolder resource = MediaRootNode.this.getResource();
						if (mediaFile.getParent().getFullPath().toString().compareTo(resource.getFullPath().toString())==0) {
							addNewMedia(mediaFile);
						}
					} else if (delta.getKind() == IResourceDelta.REMOVED && delta.getResource() == MediaRootNode.this.getResource()) {
						MediaRootNode.this.getParent().deleteChildren(new INode[] {MediaRootNode.this});
					}
					return true;
				}
			});
		} catch (CoreException ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#dispose()
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
