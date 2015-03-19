package synergyviewcore.intro;

import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	@Override
	public void postStartup() {
		hideUnusedPreferencePages();
		super.postStartup();
	}

	public void hideUnusedPreferencePages() { //These are contributed by other plugins that is not directly used in this application
		PreferenceManager pm = PlatformUI.getWorkbench().getPreferenceManager(); 
		pm.remove("org.eclipse.ui.preferencePages.Workbench");
		pm.remove("org.eclipse.team.ui.TeamPreferences");
		pm.remove("org.eclipse.help.ui.browsersPreferencePage");
		pm.remove("org.eclipse.debug.ui.DebugPreferencePage");
	}

	@Override
	public boolean preShutdown() {
		return super.preShutdown();
	}

	private static final String PERSPECTIVE_ID = "uk.ac.durham.tel.synergynet.covanto.perspective";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(false);
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
}
