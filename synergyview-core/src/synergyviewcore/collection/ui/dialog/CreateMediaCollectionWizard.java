package synergyviewcore.collection.ui.dialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;

import synergyviewcore.collection.model.MediaCollection;
import synergyviewcore.controller.ModelPersistenceException;
import synergyviewcore.project.OpenedProjectController;

public class CreateMediaCollectionWizard extends Wizard {
	private NewMediaCollectionWizardPage mediaSelectionWizardPage = new NewMediaCollectionWizardPage();

	@Override
	public void addPages() {
		addPage(mediaSelectionWizardPage);
	}

	@Override
	public String getWindowTitle() {
		return "Create a Media Collection";
	}

	@Override
	public boolean performFinish() {
		MediaCollection mediaCollection = mediaSelectionWizardPage.getMediaCollection();		
		try {
			OpenedProjectController.getInstance().getMediaCollectionController().createMediaCollection(mediaCollection);
			return true;
		} catch (ModelPersistenceException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to create media collection.", e.getMessage());
			return false;
		}
	}

}
