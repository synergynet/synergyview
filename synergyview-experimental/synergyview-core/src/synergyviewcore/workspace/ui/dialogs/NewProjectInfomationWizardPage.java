package synergyviewcore.workspace.ui.dialogs;


import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import synergyviewcore.project.ProjectResourceHelper;

public class NewProjectInfomationWizardPage extends WizardPage implements
		IWizardPage {
    private static final int SIZING_TEXT_FIELD_WIDTH = 250;
    private Text projectNameField;
    private String newProjectName;
    
    private Listener nameModifyListener = new Listener() {
        public void handleEvent(Event e) {
            boolean valid = validatePage();
            if (valid)
            	newProjectName = getProjectNameFieldValue();
            setPageComplete(valid);
        }
    };
    
	protected NewProjectInfomationWizardPage(String pageName) {
		super(pageName);
		this.setTitle("New Study");
		this.setDescription("Enter Study information.");
	}

	protected boolean validatePage() {
        String projectFieldContents = getProjectNameFieldValue();
        if (projectFieldContents.equals("")) { //$NON-NLS-1$
            setErrorMessage("Study name empty.");
            return false;
        }
        if (!ProjectResourceHelper.isProjectNameValid(getProjectNameFieldValue())) {
            setErrorMessage("Invalid Study name.");
            return false;
        }
        if (ProjectResourceHelper.isProjectExistInWorkspace(getProjectNameFieldValue())) {
            setErrorMessage("Study name already exist.");
            return false;
        }
        setErrorMessage(null);
        setMessage(null);
        return true;
	}



	@Override
	public void createControl(Composite parent) {
		Composite controlsArea = new Composite(parent, SWT.NULL);
		controlsArea.setLayout(new GridLayout());
        controlsArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        setControl(controlsArea);
        createProjectNameGroup(controlsArea);
        initializeDialogUnits(parent);
	}

	private void createProjectNameGroup(Composite parent) {
	       // project specification group
        Composite projectGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        projectGroup.setLayout(layout);
        projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // new project label
        Label projectLabel = new Label(projectGroup, SWT.NONE);
        projectLabel.setText("Study Name");
        projectLabel.setFont(parent.getFont());

        // new project name entry field
        projectNameField = new Text(projectGroup, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        projectNameField.setLayoutData(data);
        projectNameField.setFont(parent.getFont());
		
        projectNameField.addListener(SWT.Modify, nameModifyListener);
	}
	
    private String getProjectNameFieldValue() {
        if (projectNameField == null) {
			return ""; 
		}
        return projectNameField.getText().trim();
    }

	public String getNewProjectName() {
		return newProjectName;
	}

}
