package synergyviewcore;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * The Class ApplicationWorkbenchWindowAdvisor.
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    /** The logger. */
    private final ILog logger;

    /**
     * Instantiates a new application workbench window advisor.
     * 
     * @param configurer
     *            the configurer
     */
    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
	super(configurer);
	logger = Activator.getDefault().getLog();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#createActionBarAdvisor (org.eclipse.ui.application.IActionBarConfigurer)
     */
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
	return new ApplicationActionBarAdvisor(configurer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowClose()
     */
    @Override
    public void postWindowClose() {
	super.postWindowClose();
	try {
	    ResourcesPlugin.getWorkspace().save(true, null);
	} catch (CoreException ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#preWindowOpen()
     */
    @Override
    public void preWindowOpen() {
	IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
	configurer.setInitialSize(new Point(1280, 800));
	configurer.setShowCoolBar(false);
	configurer.setShowStatusLine(true);
    }
}
