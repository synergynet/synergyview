package synergyviewcore.sharing.ui.preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


import synergyviewcore.plugin.Activator;
import synergyviewcore.sharing.model.SvnServerInfo;

public class SnvServerPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public SnvServerPreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Subversion server settings");
	}

	@Override
	protected void createFieldEditors() {
		StringFieldEditor serverUrl = new StringFieldEditor(SvnServerInfo.PROP_SERVER_URL, "&Server URL:",
				getFieldEditorParent());
		serverUrl.setEmptyStringAllowed(false);
		addField(serverUrl);
		StringFieldEditor userNameField = new StringFieldEditor(SvnServerInfo.PROP_USERNAME, "&User Name:",
				getFieldEditorParent());
		userNameField.setEmptyStringAllowed(false);
		addField(userNameField);
		StringFieldEditor passwordField =
			new StringFieldEditor( SvnServerInfo.PROP_PASSWORD, "&Password:", getFieldEditorParent());
		passwordField.getTextControl(getFieldEditorParent()).setEchoChar( '*' );
		passwordField.setEmptyStringAllowed(false);
		passwordField.setTextLimit(15);
		addField(passwordField);
	}


}
