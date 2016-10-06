package synergyviewmvc.media.model;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import synergyviewmvc.navigation.model.AbstractParent;
import synergyviewmvc.projects.ResourceHelper;
import synergyviewmvc.resource.ResourceLoader;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.IParentNode;

public class MediaNode extends AbstractParent<IFile> {
	private URI mediaFileUrl;
	
	public MediaNode(IFile fileValue, IParentNode parentValue) {
		super(fileValue, parentValue);
//		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
		setLabel(fileValue.getName());
		String dir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		mediaFileUrl = new File(String.format("%s%s",dir,fileValue.getFullPath().toString())).toURI();
	}

	/**
	 * TODO Make sure the instances for controllers are matched with the previews
	 * 
	 * @return
	 */
	public AbstractMedia createMediaInstance() {
		return new QuickTimeMedia(mediaFileUrl, this.getLabel());
	}

	public static boolean isValidExtension(String extValue) {
		return extValue.equalsIgnoreCase("mpg") || extValue.equalsIgnoreCase("mov") ||  extValue.equalsIgnoreCase("m4v") || extValue.equalsIgnoreCase("mp4") || extValue.equalsIgnoreCase("avi") || extValue.equalsIgnoreCase("mp3") || extValue.equalsIgnoreCase("wav") || extValue.equalsIgnoreCase("au");
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.INode#dispose()
	 */
	public void dispose() throws DisposeException {
		if (this.getProjectPathProvider().getCollectionsRootNode().isMediaReferenced(this.resource.getName())) {
			throw new DisposeException("Unable to removed media. It is referenced by others");
		}
		ResourceHelper.deleteResources(new IResource[]{this.resource});
	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.commons.jface.node.AbstractBaseNode#getIcon()
	 */
	@Override
	public ImageDescriptor getIcon() {
            if (resource instanceof IFile) {
                    IFile file = (IFile) resource;
                    return ResourceLoader.getIconFromProgram(Program.findProgram(file.getFileExtension()));
            }
            return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT);
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.commons.jface.node.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		// TODO Auto-generated method stub
		return null;
	}
}
