package synergyviewcore.media.model;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import synergyviewcore.Activator;
import synergyviewcore.navigation.model.AbstractNode;
import synergyviewcore.navigation.model.IParentNode;
import synergyviewcore.projects.ResourceHelper;

/**
 * The Class MediaNode.
 */
public class MediaNode extends AbstractNode<IFile> implements IResourceChangeListener {

    /**
     * Checks if is valid extension.
     * 
     * @param extValue
     *            the ext value
     * @return true, if is valid extension
     */
    public static boolean isValidExtension(String extValue) {
	return extValue.equalsIgnoreCase("wmv") || extValue.equalsIgnoreCase("mpg") || extValue.equalsIgnoreCase("mov") || extValue.equalsIgnoreCase("m4v") || extValue.equalsIgnoreCase("mp4") || extValue.equalsIgnoreCase("avi") || extValue.equalsIgnoreCase("mp3") || extValue.equalsIgnoreCase("wav") || extValue.equalsIgnoreCase("au");
    }

    /** The media file url. */
    private URI mediaFileUrl;

    /**
     * Instantiates a new media node.
     * 
     * @param fileValue
     *            the file value
     * @param parentValue
     *            the parent value
     */
    public MediaNode(IFile fileValue, IParentNode parentValue) {
	super(fileValue, parentValue);
	// ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
	// IResourceChangeEvent.POST_CHANGE);
	setLabel(fileValue.getName());
	String dir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
	mediaFileUrl = new File(String.format("%s%s", dir, fileValue.getFullPath().toString())).toURI();
    }

    /**
     * TODO Make sure the instances for controllers are matched with the previews.
     * 
     * @return the abstract media
     */
    public AbstractMedia createMediaInstance() {
	return new VLCMedia(mediaFileUrl, this.getLabel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.navigation.model.INode#dispose()
     */
    public void dispose() {
	// ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	ResourceHelper.deleteResources(new IResource[] { this.resource });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org .eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
	try {
	    event.getDelta().accept(new IResourceDeltaVisitor() {
		public boolean visit(IResourceDelta delta) throws CoreException {
		    // IResource affectedResource = delta.getResource();
		    // if (delta.getKind() == IResourceDelta.REMOVED &&
		    // affectedResource instanceof IFile) {
		    // if
		    // (affectedResource.getFullPath().toString().compareTo(MediaNode.this.resource.getFullPath().toString())==0)
		    // {
		    // MediaNode.this.getParent().deleteChildren(new INode[]
		    // {MediaNode.this});
		    // }
		    // }
		    return true;
		}
	    });
	} catch (CoreException ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	}

    }
}
