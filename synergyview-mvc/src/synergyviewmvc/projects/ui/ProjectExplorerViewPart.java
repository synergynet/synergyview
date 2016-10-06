package synergyviewmvc.projects.ui;

import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.navigator.CommonNavigator;

import synergyviewmvc.annotations.model.AnnotationSetNode;
import synergyviewmvc.collections.model.CollectionMediaClipNode;
import synergyviewmvc.collections.model.CollectionNode;
import synergyviewmvc.navigation.NavigatorContentProvider;
import synergyviewmvc.navigation.NavigatorLabelProvider;
import synergyviewmvc.projects.model.WorkspaceRoot;

public class ProjectExplorerViewPart extends CommonNavigator {
	public static final String ID = "uk.ac.durham.tel.synergynet.ats.projects.projectexplorerview";

	@Override
	public void createPartControl(Composite aParent) {
		super.createPartControl(aParent);
		hookDoubleClickCommand();
	}

	@Override
	protected Object getInitialInput() {
		return WorkspaceRoot.getInstance(SWTObservables.getRealm(this.getSite().getShell().getDisplay()), this.getCommonViewer());
	}
	
	private void hookDoubleClickCommand() {
		
		this.getCommonViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try {
					if (event.getSelection()!=null && event.getSelection() instanceof IStructuredSelection) {
						IStructuredSelection structSel = (IStructuredSelection) event.getSelection();
						Object element = structSel.iterator().next();

						if (element instanceof CollectionNode) {
							handlerService.executeCommand(
									"uk.ac.durham.tel.synergynet.ats.collections.openMediaCollection", null);
						}
						if (element instanceof AnnotationSetNode) {
							handlerService.executeCommand(
									"uk.ac.durham.tel.synergynet.ats.subtitle.openannotationseteditor", null);
						}
						
					}
				} catch (Exception ex) {
					throw new RuntimeException(
					"Requested command not found!");
				}
			}
		});
	}

}
