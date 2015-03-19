package synergyviewcore.sharing.preference;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.DefaultSVNCommitHandler;
import org.tmatesoft.svn.core.wc.ISVNCommitHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import synergyviewcore.plugin.Activator;
import synergyviewcore.sharing.model.SvnServerInfo;

public class SvnServerInfoPreferenceHelper {
	
	private SvnServerInfo svnServerInfo;
	private SVNClientManager svnClientManager;
	private ISVNCommitHandler myCommitEventHandler;
	private ISVNAuthenticationManager authManager;
	private DefaultSVNOptions options;
	private IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			updateSerInfo(event.getProperty(), event.getNewValue().toString());
			setupSubversion();
		}
	};
	
	private void updateSerInfo(String property, String value) {
		if (property.compareTo(SvnServerInfo.PROP_SERVER_URL)==0)
			svnServerInfo.setServerUrl(value);
		if (property.compareTo(SvnServerInfo.PROP_USERNAME)==0)
			svnServerInfo.setUserName(value);
		if (property.compareTo(SvnServerInfo.PROP_PASSWORD)==0)
			svnServerInfo.setPassword(value.toCharArray());
	}

	public SVNClientManager getSVNClientManager() {
		return svnClientManager;
	}
	
	public SvnServerInfo getSvnServerInfo() {
		return svnServerInfo;
	}

	
	public SvnServerInfoPreferenceHelper() {
		initialise();
	}

	private void initialise() {
		loadSvnServerPreference();
		addSvnServerPreferenceChange();
		initSubversion();
		setupSubversion();
	}

	private void addSvnServerPreferenceChange() {
		Activator.getDefault().getPreferenceStore()
				.addPropertyChangeListener(propertyChangeListener);
	}

	private void loadSvnServerPreference() {
		svnServerInfo = new SvnServerInfo();
		svnServerInfo.setServerUrl(Activator.getDefault().getPreferenceStore().getString(SvnServerInfo.PROP_SERVER_URL));
		svnServerInfo.setUserName(Activator.getDefault().getPreferenceStore().getString(SvnServerInfo.PROP_USERNAME));
		svnServerInfo.setPassword(Activator.getDefault().getPreferenceStore().getString(SvnServerInfo.PROP_PASSWORD).toCharArray());
	}
	
	public void dispose() {
		removeSvnServerPreferenceChange();
	}

	private void removeSvnServerPreferenceChange() {
		Activator.getDefault().getPreferenceStore().removePropertyChangeListener(propertyChangeListener);
	}
	
	private void setupSubversion() {
		authManager = SVNWCUtil.createDefaultAuthenticationManager(svnServerInfo.getUserName(), new String(svnServerInfo.getPassword()));
		svnClientManager = SVNClientManager.newInstance(options, authManager);
		svnClientManager.getCommitClient().setCommitHandler(myCommitEventHandler);
	}
	
	private void initSubversion() {
		DAVRepositoryFactory.setup();
		options = SVNWCUtil.createDefaultOptions(false);
		options.addIgnorePattern("*");
		myCommitEventHandler = new DefaultSVNCommitHandler();
	}

}
