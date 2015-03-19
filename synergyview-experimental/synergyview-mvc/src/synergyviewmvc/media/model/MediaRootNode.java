package synergyviewmvc.media.model;

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
import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewmvc.navigation.model.AbstractParent;
import synergyviewmvc.resource.ResourceLoader;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.INode;
import uk.ac.durham.tel.commons.jface.node.IParentNode;
import uk.ac.durham.tel.commons.jface.node.NodeRemoveException;


public class MediaRootNode extends AbstractParent<IFolder> implements IResourceChangeListener {
	
	
	public static final String MEDIAFOLDER_ICON = "folder_camera.png";

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
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public static String getMediaFolderName() {
		return ResourceLoader.getString("NAME_MEDIA_FOLDER");
	}
	
	private void addNewMedia(IFile file) {
		String ext = file.getFileExtension();
		INode importedFileNode;
		if (MediaNode.isValidExtension(ext))
		{
			importedFileNode = new MediaNode(file, this);
		}
		else if (ImageNode.isValidExtension(ext))
		{
			importedFileNode = new ImageNode(file,this);
		}
		else 
		{
			importedFileNode = new MiscNode(file,this);
		} 
		children.add(importedFileNode);
		fireChildrenChanged();
	}

	public ImageDescriptor getIcon() {
		return ResourceLoader.getIconDescriptor(MEDIAFOLDER_ICON);
	}

	public List<INode> getMediaNodes(String[] excludedItemNames) {
		List<INode> result = new ArrayList<INode>();
		for(INode node : children) {
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
	
	public MediaNode getMediaNode(String mediaName) {
		for(INode node : children) {
			if (node instanceof MediaNode && node.getLabel().compareToIgnoreCase(mediaName)==0)
				return (MediaNode) node;
		}
		return null;
	}
	
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
					} 
					return true;
				}
			});
		} catch (CoreException ex) {
			ex.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.INode#dispose()
	 */
	public void dispose() throws DisposeException {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		try {
			this.deleteChildren(children.toArray(new INode[]{}));
		} catch (NodeRemoveException e) {
			e.printStackTrace();
			throw new DisposeException("Unable to remove Media node.", e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
