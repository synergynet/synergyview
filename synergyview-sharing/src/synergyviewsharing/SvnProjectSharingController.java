package synergyviewsharing;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import synergyviewsharing.model.SvnServerInfo;
import synergyviewsharing.plugin.Activator;

public class SvnProjectSharingController {
	
	private SvnServerInfo svnServerInfo;

	public SvnProjectSharingController() {
		loadSvnServerPreference();
		registerSvnServerPreferenceChange();
	}

	private void registerSvnServerPreferenceChange() {
		Activator.getDefault().getPreferenceStore()
				.addPropertyChangeListener(new IPropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent event) {
						svnServerInfo.setServerUrl(event.getNewValue().toString());
					}
				});
	}

	private void loadSvnServerPreference() {
		svnServerInfo = new SvnServerInfo();
		svnServerInfo.setServerUrl(Activator.getDefault().getPreferenceStore()
				.getString(SvnServerInfo.PROP_SERVER_URL));
	}

}
