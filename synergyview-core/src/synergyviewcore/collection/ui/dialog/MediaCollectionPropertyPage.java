package synergyviewcore.collection.ui.dialog;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import synergyviewcore.collection.model.MediaCollection;
import synergyviewcore.collection.ui.model.MediaCollectionNode;
import synergyviewcore.controller.ModelPersistenceException;
import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.databinding.validation.NotEmptyOrExistValidator;
import synergyviewcore.project.OpenedProjectController;

public class MediaCollectionPropertyPage extends PropertyPage {
	private MediaCollection mediaCollection;
	private Text nameText;
	private Text descriptionText;
	private DataBindingContext dbc = new DataBindingContext();
	
	@Override
	protected Control createContents(Composite parent) {
		IAdaptable a = this.getElement();
		Object forwardedObject = a.getAdapter(MediaCollectionNode.class);
		MediaCollectionNode mediaCollectionNode = (MediaCollectionNode) forwardedObject;
		try {
			mediaCollection = OpenedProjectController.getInstance().getMediaCollectionController().findMediaCollectionById(mediaCollectionNode.getResource());
		} catch (ObjectNotfoundException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to find media collection data", e.getMessage());
		}
		Composite composite = new Composite(parent, SWT.NONE);
		addSectionDetails(composite);
		return composite;
	}

	private void addSectionDetails(final Composite composite) {
		GridLayout layoutData = new GridLayout(2, false);
		composite.setLayout(layoutData);
	
		Label nameLabel = new Label(composite, SWT.NONE);
		nameLabel.setText("Name:");
		nameLabel.setLayoutData(new GridData());
		
		nameText = new Text(composite, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.None, true,false));
		
		Label descriptionLabel = new Label(composite, SWT.NONE);
		descriptionLabel.setText("Details:");
		descriptionLabel.setLayoutData(new GridData(SWT.NONE,SWT.TOP,false,false));
		
		descriptionText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		descriptionText.setLayoutData(new GridData(GridData.FILL_BOTH));		
				
		bindValues();
	}
	private void bindValues() {
		try {
			this.bind(nameText, mediaCollection, MediaCollection.PROP_NAME, new NotEmptyOrExistValidator("Enter media collection name.", OpenedProjectController.getInstance().getMediaCollectionController().getMediaCollectionNamesList(), true));
			this.bind(descriptionText, mediaCollection, MediaCollection.PROP_DESCRIPTION, null);
		} catch (Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to bind media collection data", e.getMessage());
		}
	}
	
	private void bind(Text textWidget, Object bean,
			String property, IValidator validator) {
		UpdateValueStrategy targetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST);
		if (validator != null) 
			targetToModel.setAfterConvertValidator(validator);
		dbc.bindValue(SWTObservables.observeText(textWidget, SWT.Modify),
				BeansObservables.observeValue(bean, property), targetToModel, null);
	}
	
	@Override
	protected void performDefaults() {
		dbc.updateTargets();
		super.performDefaults();
	}
	
	private void saveCollectionData() {
		dbc.updateModels();
		try {
			OpenedProjectController.getInstance().getMediaCollectionController().updateMediaCollection(mediaCollection);
		} catch (ModelPersistenceException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to update media collection.", e.getMessage());
		}
	}

	@Override
	protected void performApply() {
		saveCollectionData();
		super.performApply();
	}

	@Override
	public boolean performOk() {
		saveCollectionData();
		return true;
	}
}
