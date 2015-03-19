package synergyviewcore.workspace.ui.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcore.LogUtil;
import synergyviewcore.LogUtil.LogStatus;
import synergyviewcore.project.ui.model.StudyNode;
import synergyviewcore.workspace.WorkspaceController;
import uk.ac.durham.tel.commons.collections.CollectionChangeEvent;
import uk.ac.durham.tel.commons.collections.CollectionChangeListener;
import uk.ac.durham.tel.commons.collections.CollectionDiffEntry;
import uk.ac.durham.tel.commons.jface.node.AbstractBaseParent;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.INode;
import uk.ac.durham.tel.commons.jface.node.NodeRemoveException;

public class WorkspaceNode extends AbstractBaseParent<Object> {
	
	private Map<IProject, StudyNode> childrenNodesMap = new HashMap<IProject, StudyNode>(); 
	
	public WorkspaceNode(Object resourceValue) {
		super(resourceValue, null);
		loadProjectInfoList();
		addProjectInfoListChangeListener();
	}
	
	private CollectionChangeListener projectResourceListChangeListener = new CollectionChangeListener() {
		@Override
		public void listChanged(CollectionChangeEvent event) {
			for (CollectionDiffEntry<?> diffEntry : event.getListDiff().getDifferences()) {
				if (diffEntry.isAddition())
					addProjectNode((IProject) diffEntry.getElement());
				else removeProjectNode((IProject) diffEntry.getElement());
			}
		}
	};

	
	private void loadProjectInfoList() {
		for (IProject projectResourceEntry : WorkspaceController.getInstance().getWorkspaceRoot().getProjectResourceList()) {
			addProjectNode(projectResourceEntry);
		}
	}

	private void addProjectInfoListChangeListener() {
		WorkspaceController.getInstance().getWorkspaceRoot().addProjectListChangeListener(projectResourceListChangeListener);
	}

	
	private void removeProjectNode(IProject projectResource) {
		try {
			this.deleteChildren(new INode[] {childrenNodesMap.get(projectResource)});
		} catch (NodeRemoveException e) {
			LogUtil.log(LogStatus.ERROR, "Unable to remove Study node.", e);
		}
	}

	private void addProjectNode(IProject projectResource) {
		StudyNode projectNode = new StudyNode(projectResource, this);
		childrenNodesMap.put(projectResource, projectNode);
		this.addChildren(projectNode);
	}
	
	private void removeProjectInfoListChangeListener() {
		WorkspaceController.getInstance().getWorkspaceRoot().removeProjectListChangeListener(projectResourceListChangeListener);
	}

	@Override
	public String getLabel() {
		return "Root Workspace";
	}

	@Override
	public List<String> getChildrenNames() {
		return null;
	}

	@Override
	public void dispose() throws DisposeException {
		removeProjectInfoListChangeListener();
		try {
			clearChildren();
		} catch (NodeRemoveException e) {
			LogUtil.log(LogStatus.ERROR, "Unable to dispose children.", e);
		}
	}


	@Override
	public ImageDescriptor getIcon() {
		return null;
	}

}
