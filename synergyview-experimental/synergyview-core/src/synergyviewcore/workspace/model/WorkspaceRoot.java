package synergyviewcore.workspace.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import uk.ac.durham.tel.commons.collections.CollectionChangeListener;
import uk.ac.durham.tel.commons.collections.ObservableList;

public class WorkspaceRoot {
	
	private List<IProject> projectResourceList;
	private ObservableList<List<IProject>, IProject> observableProjectResourceList;

	private IResourceChangeListener projectResourceChangeListener = new IResourceChangeListener() {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			try {
				event.getDelta().accept(new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta delta) throws CoreException {
						if (delta.getResource() instanceof IProject) {
							if (delta.getKind() == IResourceDelta.ADDED) {
								addProjectResourceToList((IProject) delta.getResource());
							} else if (delta.getKind() == IResourceDelta.REMOVED) {
								removeProjectResourceFromList((IProject) delta.getResource());
							}  
						}
						
						return true;
					}
				});
			} catch (CoreException ex) {
				//
			}
		}
	};
	
	public WorkspaceRoot(IWorkspaceRoot workspaceRoot) {
		loadProjectInfoList();
		addResourceChangeListener();
	}
	
	private void addResourceChangeListener() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(projectResourceChangeListener, IResourceChangeEvent.POST_CHANGE);
	}

	public void addProjectListChangeListener(CollectionChangeListener collectionChangeListener) {
		observableProjectResourceList.addChangeListener(collectionChangeListener);
	}
	
	public void removeProjectListChangeListener(CollectionChangeListener collectionChangeListener) {
		observableProjectResourceList.removeChangeListener(collectionChangeListener);
	}
	
	private void removeProjectResourceFromList(IProject projectResource) {
		observableProjectResourceList.remove(projectResource);
	}
	
	private void addProjectResourceToList(IProject projectResource) {
		observableProjectResourceList.add(projectResource);
	}
	
	private void loadProjectInfoList() {
		projectResourceList = new ArrayList<IProject>();
		observableProjectResourceList = new ObservableList<List<IProject>, IProject>(projectResourceList);
		IProject[] projectResources = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject projectResource : projectResources) {			
			observableProjectResourceList.add(projectResource);
		}
	}
	
	public List<IProject> getProjectResourceList() {
		return observableProjectResourceList.getReadOnlyList();
	}

	public void dispose() {
		removeResourceChangeListener();
	}

	private void removeResourceChangeListener() {
		if (projectResourceChangeListener!=null)
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(projectResourceChangeListener);
	}
}
