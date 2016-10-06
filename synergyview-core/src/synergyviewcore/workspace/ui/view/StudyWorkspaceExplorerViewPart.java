package synergyviewcore.workspace.ui.view;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

import synergyviewcore.LogUtil;
import synergyviewcore.LogUtil.LogStatus;
import synergyviewcore.navigation.NavigatorContentProvider;
import synergyviewcore.navigation.NavigatorLabelDecorator;
import synergyviewcore.navigation.ObservableNavigatorLabelProvider;
import synergyviewcore.workspace.ui.model.WorkspaceNode;
import uk.ac.durham.tel.commons.jface.node.DisposeException;

public class StudyWorkspaceExplorerViewPart extends ViewPart {
	
	public static final String ID = "uk.ac.durham.tel.synergynet.covanto.workspace.ui.view.StudyWorkspaceExplorerViewPart";
	
	private TreeViewer projectsTreeViewer;
	private WorkspaceNode workspaceNode;


	public void createPartControl(Composite parent) {
		projectsTreeViewer = new TreeViewer(parent, SWT.MULTI);
		setupProjectsTreeViewerContent();
		setupMenuContribution();
		setupSelectionProvider();

	}

	private void setupSelectionProvider() {
		getSite().setSelectionProvider(projectsTreeViewer);
	}

	private void setupMenuContribution() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu (projectsTreeViewer.getControl());
		projectsTreeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, projectsTreeViewer);
	}

	private void setupProjectsTreeViewerContent() {
		NavigatorContentProvider workspaceProjectsContentProvider = new NavigatorContentProvider(); 
		projectsTreeViewer.setContentProvider(workspaceProjectsContentProvider);
		projectsTreeViewer.setLabelProvider(new DecoratingStyledCellLabelProvider(new ObservableNavigatorLabelProvider(workspaceProjectsContentProvider.getKnownElements()), new NavigatorLabelDecorator(), null));
		workspaceNode = new WorkspaceNode(null);
		projectsTreeViewer.setInput(workspaceNode);
		projectsTreeViewer.getControl().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				try {
					if (workspaceNode != null) {
						workspaceNode.dispose();
						workspaceNode=null;
					}
				} catch (DisposeException ex) {
					LogUtil.log(LogStatus.ERROR, "Unable to dispose Workspace Node.", ex);
				}
			}
		});
	}

	@Override
	public void setFocus() {
		projectsTreeViewer.getControl().setFocus();
	}

}
