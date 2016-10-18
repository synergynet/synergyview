package synergyviewcore;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * The Class ApplicationActionBarAdvisor.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    /**
     * Instantiates a new application action bar advisor.
     * 
     * @param configurer
     *            the configurer
     */
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
	super(configurer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.ActionBarAdvisor#fillMenuBar(org.eclipse.jface .action.IMenuManager)
     */
    protected void fillMenuBar(IMenuManager menuBar) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.ActionBarAdvisor#makeActions(org.eclipse.ui .IWorkbenchWindow)
     */
    protected void makeActions(IWorkbenchWindow window) {
	// IWorkbenchAction saveAction = ActionFactory.SAVE.create(window);
	// register(saveAction);
    }

}
