package synergyviewmvc.projects.model;

import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;

import synergyviewmvc.navigation.model.AbstractParent;
import synergyviewmvc.navigation.model.IViewerProvider;
import uk.ac.durham.tel.commons.jface.node.IParentNode;

public class WorkspaceRoot extends AbstractParent<IWorkspaceRoot> implements IResourceChangeListener, IViewerProvider {	
	private static WorkspaceRoot instance; 
	private Realm _realm;
	private TreeViewer _treeViewer;
	
	protected WorkspaceRoot(IWorkspaceRoot workspaceRootValue, IParentNode parentValue, Realm realm, TreeViewer treeViewerValue) {
		super(workspaceRootValue, parentValue);
		_realm = realm;
		_treeViewer = treeViewerValue;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
		for(IProject iproject : workspaceRootValue.getProjects()) {
			addProject(new ProjectNode(iproject, this));
		}
	}
	

	
	public void removeProject(ProjectNode projectValue) {
		children.remove(projectValue);
		fireChildrenChanged();
	}
	
	public void addProject(ProjectNode projectValue) {
		children.add(projectValue);
		fireChildrenChanged();
	}


	public ImageDescriptor getIcon() {
		return null;
	}

	public IParentNode getParent() {
		return null;
	}
	
	public static WorkspaceRoot getInstance(Realm realm, TreeViewer treeViewerValue) {
		if (instance == null) {
			instance = new WorkspaceRoot(ResourcesPlugin.getWorkspace().getRoot(), null, realm, treeViewerValue);
		}
		return instance;
	}
	
	public Realm getRealm() {
		return _realm;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		try {			
			event.getDelta().accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) throws CoreException {
					if ((delta.getResource() instanceof IProject) && (delta.getKind() == IResourceDelta.ADDED)) {
						try {
							addProject(new ProjectNode((IProject) delta.getResource(), WorkspaceRoot.this));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} 
					return true;
				}
			});
		} catch (CoreException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.INode#dispose()
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}



	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.IViewerProvider#getTreeViewer()
	 */
	public TreeViewer getTreeViewer() {
		return _treeViewer;
	}



	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		// TODO Auto-generated method stub
		return null;
	}
}
