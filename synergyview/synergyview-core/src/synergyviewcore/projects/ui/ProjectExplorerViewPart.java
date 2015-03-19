package synergyviewcore.projects.ui;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.navigator.CommonNavigator;

import synergyviewcore.Activator;
import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.collections.model.CollectionNode;
import synergyviewcore.projects.model.WorkspaceRoot;

public class ProjectExplorerViewPart extends CommonNavigator {
	public static final String ID = "synergyviewcore.projects.projectexplorerview";
	private  ILog logger;
	@Override
	public void createPartControl(Composite aParent) {
		super.createPartControl(aParent);
		logger = Activator.getDefault().getLog();
		hookDoubleClickCommand();
	}

	@Override
	protected Object getInitialInput() {
		try {
			return WorkspaceRoot.getInstance(SWTObservables.getRealm(this.getSite().getShell().getDisplay()), this.getCommonViewer());
		} catch (Exception ex) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error loading study workspace!",ex.getMessage());
			//TODO Move!
			System.exit(0); 
		}
		return null;
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
									"synergyviewcore.collections.openMediaCollection", null);
						}
						if (element instanceof AnnotationSetNode) {
							handlerService.executeCommand(
									"synergyviewcore.subtitle.openannotationseteditor", null);
						}
						
					}
				} catch (Exception ex) {
					IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
					logger.log(status);
				}
			}
		});
	}

}
