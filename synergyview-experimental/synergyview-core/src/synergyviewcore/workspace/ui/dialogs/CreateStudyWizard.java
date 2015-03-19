package synergyviewcore.workspace.ui.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import synergyviewcore.workspace.WorkspaceController;
import synergyviewcore.workspace.WorkspaceException;

public class CreateStudyWizard extends Wizard implements INewWizard {
	NewProjectInfomationWizardPage newProjectInfomationWizardPage;
	
	@Override
	public void addPages() {
		super.addPages();
		newProjectInfomationWizardPage = new NewProjectInfomationWizardPage("New Study Information");
		this.addPage(newProjectInfomationWizardPage);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		//
	}

	@Override
	public boolean performFinish() {
		try {
			WorkspaceController.getInstance().createStudy(newProjectInfomationWizardPage.getNewProjectName());
			return true;
		} catch (WorkspaceException e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", e.getMessage());
			return false;
		}
		
	}

}
