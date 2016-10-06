package synergyviewcore.collection.ui.dialog;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import synergyviewcore.collection.model.MediaCollection;
import synergyviewcore.databinding.validation.NotEmptyOrExistValidator;
import synergyviewcore.media.model.Media;
import synergyviewcore.media.model.PlayableMedia;
import synergyviewcore.project.OpenedProjectController;

public class NewMediaCollectionWizardPage extends WizardPage implements IWizardPage {

	private LocalResourceManager resourceManager = new LocalResourceManager(
			JFaceResources.getResources());
	private ComboViewer mediaListCombo;
	private Text nameText;
	private Text descriptionText;
	private MediaCollection mediaCollection = OpenedProjectController.getInstance().getMediaCollectionController().createNew();
	private DataBindingContext dbc = new DataBindingContext();
	
	protected NewMediaCollectionWizardPage() {
		super("Media selection page");
		this.setTitle("New media collection");
		setDescription("Enter details to create a media collection");
	}

	@Override
	public void createControl(Composite parent) {
		final Composite area = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		area.setLayoutData(gridData);
		GridLayout layoutData = new GridLayout(2, false);
		area.setLayout(layoutData);
		this.setPageComplete(false);
		
		Label mediaLabel = new Label(area, SWT.NONE);
		mediaLabel.setText("Media:");
		mediaLabel.setLayoutData(new GridData());
		
		mediaListCombo = new ComboViewer(area, SWT.NONE);
		mediaListCombo.getControl().setLayoutData(new GridData(SWT.FILL,SWT.NONE,true,false));

		setupMediaList();

		
		Label nameLabel = new Label(area, SWT.NONE);
		nameLabel.setText("Name:");
		nameLabel.setLayoutData(new GridData());
		
		nameText = new Text(area, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.None, true,false));
		
		Label descriptionLabel = new Label(area, SWT.NONE);
		descriptionLabel.setText("Details:");
		descriptionLabel.setLayoutData(new GridData(SWT.NONE,SWT.TOP,false,false));
		
		descriptionText = new Text(area, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		descriptionText.setLayoutData(new GridData(GridData.FILL_BOTH));		
				
		setControl(area);
		bindValues();
		WizardPageSupport.create(this, dbc);
	}

	public void setupMediaList() {
		mediaListCombo.setContentProvider(new ArrayContentProvider());
		mediaListCombo.setLabelProvider(new LabelProvider() {
			
			@Override
			public String getText(Object element) {
				PlayableMedia mediaDataItem = (PlayableMedia) element;
				return mediaDataItem.getId();
			}
		});
		try {
			mediaListCombo.setInput(OpenedProjectController.getInstance().getMediaController().getPlayableMediaList());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void bindValues() {
		try {
			this.bindSelction(mediaListCombo.getControl(), mediaCollection, MediaCollection.PROP_MEDIAITEM, new NotEmptyOrExistValidator("Please select media to be used as media collection", OpenedProjectController.getInstance().getMediaController().getPlayableMediaIdList(), false));
			this.bind(nameText, mediaCollection, MediaCollection.PROP_NAME, new NotEmptyOrExistValidator("Enter media collection name.", OpenedProjectController.getInstance().getMediaCollectionController().getMediaCollectionNamesList(), true));
			this.bind(descriptionText, mediaCollection, MediaCollection.PROP_DESCRIPTION, null);
		} catch (Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to bind media collection data", e.getMessage());
		}
	}
	
	private void bind(Text textWidget, Object bean,
			String property, IValidator validator) {
		UpdateValueStrategy targetToModel = null;
		if (validator != null) {
			targetToModel = new UpdateValueStrategy()
					.setAfterConvertValidator(validator);
		}
		dbc.bindValue(SWTObservables.observeText(textWidget, SWT.Modify),
				BeansObservables.observeValue(bean, property), targetToModel,
				null);
	}

	private void bindSelction(Control slectionControl, Object bean,
			String property, IValidator validator) {
		UpdateValueStrategy targetToModel = new UpdateValueStrategy() {

			@Override
			protected IStatus doSet(IObservableValue observableValue,
					Object value) {
				try {
					Media media = OpenedProjectController.getInstance().getMediaController().find((String) value);
					return super.doSet(observableValue, media);
				} catch (Exception e) {
					return super.doSet(observableValue, null);
				}
				
			}
			
		};
		if (validator != null) {
			targetToModel.setAfterConvertValidator(validator);
		}
		dbc.bindValue(SWTObservables.observeSelection(slectionControl),
				BeansObservables.observeValue(bean, property), targetToModel,
				null);
	}
	
	@Override
	public void dispose() {
		resourceManager.dispose();
		super.dispose();
	}

	public MediaCollection getMediaCollection() {
		return mediaCollection;
	}
}
