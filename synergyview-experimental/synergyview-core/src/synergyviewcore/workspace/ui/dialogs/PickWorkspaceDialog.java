package synergyviewcore.workspace.ui.dialogs;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import synergyviewcore.workspace.WorkspaceLocationPreferenceHelper;

/**
 * Dialog that lets/forces a user to enter/select a workspace.
 * 
 * @author Phyo Kyaw 
 */
public class PickWorkspaceDialog extends TitleAreaDialog {

	// the name of the file that tells us that the workspace directory belongs
	// to our application
	public static final String WS_IDENTIFIER = ".covantoprojects.xml";

	private static final String HEADER_DESCRIPTION = "Your study workspace directory is where settings and various data files will be stored.";
	private static final String HEADER_TITLE_BAR = "Study Workspace Selection";
	private static final String HEADER_TITLE = "Choose Study Workspace Directory";
	private static final String DIRECTORY_BROWSER_DIALOG_HEADER_DESCRIPTION = "Please select a directory that will be the study workspace directory";
	private static final String DIRECTORY_BROWSER_DIALOG_HEADER = "Select Study Workspace Directory";
	
	private Combo workspacePathComboBox;


	private String selectedWorkspaceRootLocation;

	/**
	 * Creates a new workspace dialog with a specific image as title-area image.
	 * 
	 * @param switchWorkspace
	 *            true if we're using this dialog as a switch workspace dialog
	 * @param wizardImage
	 *            Image to show
	 */
	public PickWorkspaceDialog(Image wizardImage) {
		super(Display.getDefault().getActiveShell());
		if (wizardImage != null) {
			setTitleImage(wizardImage);
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(HEADER_TITLE_BAR);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(HEADER_TITLE);
		setMessage(HEADER_DESCRIPTION);

		Composite inner = new Composite(parent, SWT.NONE);
		inner.setLayout(new GridLayout(3, false));
		inner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// label on left
		CLabel label = new CLabel(inner, SWT.NONE);
		label.setText("Study Workspace Directory");
		label.setLayoutData(new GridData());

		// comboBox in middle
		workspacePathComboBox = new Combo(inner, SWT.BORDER);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		workspacePathComboBox.setLayoutData(gd);

		// browse button on right
		Button browseDirectoryButton = new Button(inner, SWT.PUSH);
		browseDirectoryButton.setText("Browse...");
		browseDirectoryButton.setLayoutData(new GridData());
		browseDirectoryButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				selectExistingDirectory();
			}
		});

		fillWorkspacePathComboWithWorkspaceLocationHistoryList();
		addValidationSupportToDirectoryPathText();
		return inner;
	}

	private void selectExistingDirectory() {
		String existingDirectoryPath = showDirectorySelectionDialog();
		if (existingDirectoryPath == null)
			return;
		workspacePathComboBox.setText(existingDirectoryPath);
	}

	private void addValidationSupportToDirectoryPathText() {
		workspacePathComboBox.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
	}

	private void validate() {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (workspacePathComboBox.getText().isEmpty()) {
			this.setMessage("Directory cannot be empty",IMessageProvider.ERROR);
			if (okButton!=null)
				okButton.setEnabled(false); 
		} else {
			this.setMessage(HEADER_DESCRIPTION);
			if (okButton!=null)
				okButton.setEnabled(true);
		}
	}

	private String showDirectorySelectionDialog() {
		DirectoryDialog directoryDialog = new DirectoryDialog(getParentShell());
		directoryDialog.setText(DIRECTORY_BROWSER_DIALOG_HEADER);
		directoryDialog.setMessage(DIRECTORY_BROWSER_DIALOG_HEADER_DESCRIPTION);
		directoryDialog.setFilterPath(workspacePathComboBox.getText());
		return directoryDialog.open();
	}

	private void fillWorkspacePathComboWithWorkspaceLocationHistoryList() {
		List<String> currentWorkspaceLocationHistoryList = WorkspaceLocationPreferenceHelper
				.getWorkspaceLocationHistory();
		if (!currentWorkspaceLocationHistoryList.isEmpty()) {
			for (String locationHistoryEntry : currentWorkspaceLocationHistoryList) {
				workspacePathComboBox.add(locationHistoryEntry);
			}
			workspacePathComboBox.setText(currentWorkspaceLocationHistoryList.get(currentWorkspaceLocationHistoryList.size()-1));
		} else {
			String newLocationSuggestion = WorkspaceLocationPreferenceHelper
					.getWorkspacePathSuggestion();
			if (newLocationSuggestion != null)
				workspacePathComboBox.setText(newLocationSuggestion);
		}
	}

	public String getSelectedWorkspaceLocation() {
		return selectedWorkspaceRootLocation;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		String newWorkspaceLocationPath = workspacePathComboBox.getText();
		boolean workspaceCreated = checkAndCreateWorkspace(newWorkspaceLocationPath);
		if (workspaceCreated) {
			WorkspaceLocationPreferenceHelper.addWorkspaceLocationHistory(newWorkspaceLocationPath);
			selectedWorkspaceRootLocation = newWorkspaceLocationPath;
			super.okPressed();
		} else {
			this.setMessage(String.format("Unable to create study workspace in %s.", newWorkspaceLocationPath),IMessageProvider.ERROR);
		}
	}

	private boolean checkAndCreateWorkspace(String newWorkspaceLocationPath) {
		File newWorkspaceDirectory = new File(newWorkspaceLocationPath);
		boolean workspaceCreated = false;
		if (!newWorkspaceDirectory.exists()) { // Needs to create a new directory
			workspaceCreated = checkNewDirectoryCreationAndCreateWorkspace(newWorkspaceDirectory);
		} else { // A Directory is already there
			workspaceCreated = checkExistingDirectoryAndCreateWorkspace(newWorkspaceDirectory);
		}
		return workspaceCreated;
	}

	private boolean checkNewDirectoryCreationAndCreateWorkspace(
			File newWorkspaceDirectory) {
		boolean isDirectoryCreationConfirmed = confirmNewDirectoryCreation(newWorkspaceDirectory);
		if (!isDirectoryCreationConfirmed)
			return false;
		if (!newWorkspaceDirectory.mkdirs())
			return false;
		return WorkspaceLocationPreferenceHelper.createWorkspace(newWorkspaceDirectory);
	}

	private boolean checkExistingDirectoryAndCreateWorkspace(File newWorkspaceDirectory) {
		boolean isValidWorkspace = WorkspaceLocationPreferenceHelper
				.isValidWorkspace(newWorkspaceDirectory);
		if (isValidWorkspace) 
			return true;
		else {
			if (!WorkspaceLocationPreferenceHelper.isWorkspaceCreateable(newWorkspaceDirectory)) 
				return false;
			else {
				boolean directoryNeedsToBeTransformed = confirmTransformDirectoryIntoWorkspaceDialog(newWorkspaceDirectory);
				return (directoryNeedsToBeTransformed) ? WorkspaceLocationPreferenceHelper.createWorkspace(newWorkspaceDirectory) : false;
			}
		}
	}

	private boolean confirmTransformDirectoryIntoWorkspaceDialog(File existingDirectory) {
		return showConfirmDialog("Existing Directory", String.format(
				"The directory %s is not set to be a study workspace directory. \nWould you like to create a study workspace in the selected directory?",
				existingDirectory.getAbsolutePath()));
	}

	private boolean confirmNewDirectoryCreation(
			File newWorkspaceDirectory) {
		return showConfirmDialog("New Workspace Directory", String.format(
				"The directory %s does not exist. Would you like to create it?",
				newWorkspaceDirectory.getAbsolutePath()));
	}
	
	private boolean showConfirmDialog(String title, String text) {
		return MessageDialog.openConfirm(this.getShell(), title, text);
	}
}
