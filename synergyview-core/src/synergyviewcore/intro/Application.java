package synergyviewcore.intro;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.workspace.ui.dialogs.PickWorkspaceDialog;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) {
		Display display = PlatformUI.createDisplay();
		try {
			setDefaultLocale();
			String workspaceLocation = selectWorkspaceToUse();
			if (workspaceLocation == null)
				return IApplication.EXIT_OK;
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
	}

	private String selectWorkspaceToUse() {
		Location instanceLoc = Platform.getInstanceLocation(); 
		PickWorkspaceDialog pickWorkspaceDialog = new PickWorkspaceDialog(null);
        int returnCode = pickWorkspaceDialog.open(); 
        if (returnCode == Window.CANCEL)
        	return null;
        String workspaceLocation = pickWorkspaceDialog.getSelectedWorkspaceLocation();
        if (workspaceLocation == null)
        	return null;
        try {
			instanceLoc.set(new URL("file", null, workspaceLocation), false);
		} catch (IllegalStateException e) {
			return null;
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		} 
		return workspaceLocation;
        
	}

	private void setDefaultLocale() {
		 ResourceLoader.setBundle(Locale.getDefault());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
