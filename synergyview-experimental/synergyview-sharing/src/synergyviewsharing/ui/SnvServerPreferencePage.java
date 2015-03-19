package synergyviewsharing.ui;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


import synergyviewsharing.model.SvnServerInfo;
import synergyviewsharing.plugin.Activator;

public class SnvServerPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public SnvServerPreferencePage() {
		super(FLAT);
	}


	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Server Settings");
	}

	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor(SvnServerInfo.PROP_SERVER_URL, "&Server URL:",
				getFieldEditorParent()));
		
	}


}
