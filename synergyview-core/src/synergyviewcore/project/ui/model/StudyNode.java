package synergyviewcore.project.ui.model;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcore.LogUtil;
import synergyviewcore.LogUtil.LogStatus;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.sharing.SharingController;
import synergyviewcore.sharing.SharingException;
import synergyviewcore.sharing.model.NetworkSharingInfo;
import synergyviewcore.sharing.model.OwnSharingInfo;
import synergyviewcore.sharing.model.SharingInfo;
import uk.ac.durham.tel.commons.jface.node.AbstractBaseParent;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.IParentNode;

public class StudyNode extends AbstractBaseParent<IProject> {
	private boolean isProjectShared;
	private final static String studyOpenedImageDescriptor = "study-opened.png";
	private final static String studyClosedImageDescriptor = "study-closed.png";
	
	private IResourceChangeListener projectResourceChangeListener = new IResourceChangeListener() {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			try {
				event.getDelta().accept(new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta delta) throws CoreException {
						if (delta.getResource() instanceof IProject) {
							if (delta.getKind() == IResourceDelta.CHANGED) {
								updateResourceState(delta.getResource());
							}
						}
						return true;
					}
				});
			} catch (CoreException ex) {
				LogUtil.log(LogStatus.ERROR, "Unable to ave XML data.", ex);
			}
		}
	};
	
	public StudyNode(IProject resourceValue, IParentNode parentValue) {
		super(resourceValue, parentValue);
		this.setLabel(resource.getName());
		updateLabelDecorator();
		addProjectInfoPropertyChangeListener();
	}

	private void updateResourceState(IResource projectResource) {
		if (this.resource == projectResource) {
			updateLabelDecorator();
		}
	}

	private void addProjectInfoPropertyChangeListener() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(projectResourceChangeListener, IResourceChangeEvent.POST_CHANGE);
	}

	private void updateLabelDecorator() {
		if (resource.isOpen())
			setIconDecorator("opened");
		else setIconDecorator("");
		isProjectShared = SharingController.getInstance().isProjectShared(resource.getProject());
		if (!isProjectShared) 
			setLabelDecorator("");
		else {
			try {
				SharingInfo sharingInfo = SharingController.getInstance().getProjectSharingInfo(resource.getProject());
				if (sharingInfo instanceof OwnSharingInfo)
					setLabelDecorator("Shared");
				else if (sharingInfo instanceof NetworkSharingInfo)
					setLabelDecorator(String.format("Shared by %s", sharingInfo.getOwnerName())); 
			} catch (SharingException e) {
				LogUtil.log(LogStatus.ERROR, "Unable to update label decorator.", e);
			}
		}
		
	}
	
	public boolean isProjectShared() {
		return isProjectShared;
	}

	@Override
	public List<String> getChildrenNames() {
		return null;
	}

	@Override
	public void dispose() throws DisposeException {
		removeProjectInfoPropertyChangeListener();
	}

	private void removeProjectInfoPropertyChangeListener() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(projectResourceChangeListener);
	}

	@Override
	public ImageDescriptor getIcon() {
		if (!this.getResource().isOpen())
			return ResourceLoader.getIconDescriptor(studyClosedImageDescriptor);
		else
			return ResourceLoader.getIconDescriptor(studyOpenedImageDescriptor);
	}


}
