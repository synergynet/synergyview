package synergyviewcore.projects.model;

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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;

import synergyviewcore.Activator;
import synergyviewcore.navigation.model.AbstractParent;
import synergyviewcore.navigation.model.IParentNode;
import synergyviewcore.navigation.model.IViewerProvider;

/**
 * The Class WorkspaceRoot.
 */
public class WorkspaceRoot extends AbstractParent<IWorkspaceRoot> implements
		IResourceChangeListener, IViewerProvider {
	
	/** The instance. */
	private static WorkspaceRoot instance;
	
	/**
	 * Gets the single instance of WorkspaceRoot.
	 * 
	 * @param realm
	 *            the realm
	 * @param treeViewerValue
	 *            the tree viewer value
	 * @return single instance of WorkspaceRoot
	 * @throws Exception
	 *             the exception
	 */
	public static WorkspaceRoot getInstance(Realm realm,
			TreeViewer treeViewerValue) throws Exception {
		if (instance == null) {
			instance = new WorkspaceRoot(ResourcesPlugin.getWorkspace()
					.getRoot(), null, realm, treeViewerValue);
		}
		return instance;
	}
	
	/** The _realm. */
	private Realm _realm;
	
	/** The _tree viewer. */
	private TreeViewer _treeViewer;
	
	/**
	 * Instantiates a new workspace root.
	 * 
	 * @param workspaceRootValue
	 *            the workspace root value
	 * @param parentValue
	 *            the parent value
	 * @param realm
	 *            the realm
	 * @param treeViewerValue
	 *            the tree viewer value
	 * @throws Exception
	 *             the exception
	 */
	protected WorkspaceRoot(IWorkspaceRoot workspaceRootValue,
			IParentNode parentValue, Realm realm, TreeViewer treeViewerValue)
			throws Exception {
		super(workspaceRootValue, parentValue);
		_realm = realm;
		_treeViewer = treeViewerValue;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.POST_CHANGE);
		try {
			for (IProject iproject : workspaceRootValue.getProjects()) {
				addProject(new ProjectNode(iproject, this));
			}
		} catch (Exception ex) {
			throw new Exception("Unable to load projects.", ex);
		}
	}
	
	/**
	 * Adds the project.
	 * 
	 * @param projectValue
	 *            the project value
	 */
	public void addProject(ProjectNode projectValue) {
		_children.add(projectValue);
		fireChildrenChanged();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.navigation.model.INode#dispose()
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.navigation.model.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.navigation.model.AbstractNode#getIcon()
	 */
	public ImageDescriptor getIcon() {
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.navigation.model.AbstractNode#getParent()
	 */
	public IParentNode getParent() {
		return null;
	}
	
	/**
	 * Gets the realm.
	 * 
	 * @return the realm
	 */
	public Realm getRealm() {
		return _realm;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.navigation.model.IViewerProvider#getTreeViewer()
	 */
	public TreeViewer getTreeViewer() {
		return _treeViewer;
	}
	
	/**
	 * Removes the project.
	 * 
	 * @param projectValue
	 *            the project value
	 */
	public void removeProject(ProjectNode projectValue) {
		_children.remove(projectValue);
		fireChildrenChanged();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org
	 * .eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			event.getDelta().accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) throws CoreException {
					if ((delta.getResource() instanceof IProject)
							&& (delta.getKind() == IResourceDelta.ADDED)) {
						try {
							addProject(new ProjectNode((IProject) delta
									.getResource(), WorkspaceRoot.this));
						} catch (Exception e) {
							IStatus status = new Status(IStatus.ERROR,
									Activator.PLUGIN_ID, e.getMessage(), e);
							logger.log(status);
						}
					}
					return true;
				}
			});
		} catch (CoreException ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
		}
	}
}
