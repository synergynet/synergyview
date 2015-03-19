package synergyviewcore.intro;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import synergyviewcore.LogUtil;
import synergyviewcore.LogUtil.LogStatus;
import synergyviewcore.project.OpenedProjectController;
import synergyviewcore.sharing.SharingController;
import synergyviewcore.workspace.WorkspaceController;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	@Override
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(1280, 800));
        configurer.setShowCoolBar(false);
        configurer.setShowStatusLine(false);
        configurer.setTitle("Collaborative Video Analysis Tool");
        configurer.setShowProgressIndicator(true);
		initialiseControllers();
    }

	private void initialiseControllers() {
		WorkspaceController.getInstance().initialise();
		SharingController.getInstance().initialise();
		OpenedProjectController.getInstance().initialise();
	}
	
	@Override
	public void postWindowClose() {
		disposeControllers();
		super.postWindowClose();
		try {
			ResourcesPlugin.getWorkspace().save(true, null);
		} catch (CoreException ex) {
			LogUtil.log(LogStatus.ERROR, "Unable to save the workspace", ex);
		}
	}

	private void disposeControllers() {
		OpenedProjectController.getInstance().dispose();
		SharingController.getInstance().dispose();
		WorkspaceController.getInstance().dispose();
	}

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    

}
