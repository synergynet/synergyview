package synergyviewcore.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;

import synergyviewcore.project.ui.model.StudyNode;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.sharing.SharingController;
import synergyviewcore.sharing.SharingException;
import synergyviewcore.sharing.model.NetworkSharingInfo;
import synergyviewcore.sharing.model.OwnSharingInfo;
import synergyviewcore.sharing.model.SharingInfo;


public class ProjectResourceHelper {
	private final static ImageDescriptor sharingOwnImageDescriptor = ResourceLoader.getIconDescriptor("study-shared-decorated.png");
	private final static ImageDescriptor sharingRemoteImageDescriptor = ResourceLoader.getIconDescriptor("study-remote-decorated.gif");
	
	
	public static boolean isProjectNameValid(String name) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus nameStatus = workspace.validateName(name,
                IResource.PROJECT);
        return nameStatus.isOK();
	}
	
	public static boolean isProjectExistInWorkspace(String name) {
		IProject project = getProjectHandle(name);
		return project.exists();
	}
	
	private static IProject getProjectHandle(String name) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(name);
	}
	
	public static Image getProjectNodeDecorator(StudyNode projectNode, Image image) {
		if (projectNode.isProjectShared()) {
			SharingInfo sharingInfo;
			try {
				sharingInfo = SharingController.getInstance().getProjectSharingInfo(projectNode.getResource());
				Image decoratedImage = null;
				
				if (sharingInfo instanceof OwnSharingInfo)
					decoratedImage = new DecorationOverlayIcon(image, sharingOwnImageDescriptor, IDecoration.BOTTOM_RIGHT).createImage();
				else if (sharingInfo instanceof NetworkSharingInfo)
					decoratedImage = new DecorationOverlayIcon(image, sharingRemoteImageDescriptor, IDecoration.BOTTOM_RIGHT).createImage();
				if (decoratedImage != null) {
					image = decoratedImage;
					return image;
				}
			} catch (SharingException e) {
				e.printStackTrace();
			}
			
		}
		return null;
	}
}
