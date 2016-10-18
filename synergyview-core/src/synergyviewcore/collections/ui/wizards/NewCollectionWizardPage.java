package synergyviewcore.collections.ui.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import synergyviewcore.collections.model.Collection;
import synergyviewcore.databinding.validation.NotEmptyOrExistValidator;
import synergyviewcore.resource.ResourceLoader;

/**
 * The Class NewCollectionWizardPage.
 */
public class NewCollectionWizardPage extends WizardPage {

    /** The decorator map. */
    private static HashMap<Control, ControlDecoration> decoratorMap = new HashMap<Control, ControlDecoration>();

    /** The _existing collection names. */
    private List<String> _existingCollectionNames;

    /** The collection. */
    private Collection collection;

    /** The dbc. */
    private DataBindingContext dbc = new DataBindingContext();

    /** The details text. */
    private Text detailsText;

    /** The name text. */
    private Text nameText;

    /**
     * Instantiates a new new collection wizard page.
     * 
     * @param collectionVAlue
     *            the collection v alue
     * @param existingCollectionNameValues
     *            the existing collection name values
     */
    public NewCollectionWizardPage(Collection collectionVAlue, List<String> existingCollectionNameValues) {
	super("New Session Wizard Start Page");
	collection = collectionVAlue;
	this._existingCollectionNames = existingCollectionNameValues;
	setTitle(ResourceLoader.getString("DIALOG_TITLE_START_PAGE_SESSION"));
	setDescription(ResourceLoader.getString("DIALOG_DESCRIPTION_START_PAGE_SESSION"));
    }

    /**
     * Bind.
     * 
     * @param textWidget
     *            the text widget
     * @param bean
     *            the bean
     * @param property
     *            the property
     * @param validator
     *            the validator
     */
    private void bind(Text textWidget, Object bean, String property, IValidator validator) {
	UpdateValueStrategy targetToModel = null;
	if (validator != null) {
	    targetToModel = new UpdateValueStrategy().setAfterConvertValidator(validator);
	}
	dbc.bindValue(SWTObservables.observeText(textWidget, SWT.Modify), BeansObservables.observeValue(bean, property), targetToModel, null);
    }

    /**
     * Bind values.
     */
    private void bindValues() {

	List<String> existingCollectionNames = new ArrayList<String>();
	for (String collectionName : _existingCollectionNames) {
	    existingCollectionNames.add(collectionName);
	}
	bind(nameText, collection, Collection.PROP_NAME, new NotEmptyOrExistValidator(Collection.PROP_NAME, existingCollectionNames));
	bind(detailsText, collection, Collection.PROP_DETAILS, null);
	final AggregateValidationStatus aggregateValidationStatus = new AggregateValidationStatus(dbc.getValidationStatusProviders(), AggregateValidationStatus.MAX_SEVERITY);

	aggregateValidationStatus.addValueChangeListener(new IValueChangeListener() {
	    public void handleValueChange(ValueChangeEvent event) {
		// the invocation of the getValue method is necessary
		// the further changes will be fired
		aggregateValidationStatus.getValue();
		for (Object o : dbc.getBindings()) {
		    Binding binding = (Binding) o;
		    IStatus status = (IStatus) binding.getValidationStatus().getValue();
		    Control control = null;
		    if (binding.getTarget() instanceof ISWTObservable) {
			ISWTObservable swtObservable = (ISWTObservable) binding.getTarget();
			control = (Control) swtObservable.getWidget();
		    }
		    ControlDecoration decoration = decoratorMap.get(control);
		    if (decoration != null) {
			if (status.isOK()) {
			    decoration.hide();
			} else {
			    decoration.setDescriptionText(status.getMessage());
			    decoration.show();
			}
		    }
		}
	    }
	});

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets .Composite)
     */
    public void createControl(Composite parent) {
	final Composite area = new Composite(parent, SWT.NONE);
	setControl(area);
	GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
	area.setLayoutData(gridData);
	GridLayout layoutData = new GridLayout(2, false);
	area.setLayout(layoutData);

	Label nameLabel = new Label(area, SWT.NONE);
	nameLabel.setText("Name:");

	nameText = new Text(area, SWT.BORDER);
	createControlDecoration(nameText);

	gridData = new GridData();
	gridData.horizontalAlignment = SWT.FILL;
	gridData.grabExcessHorizontalSpace = true;
	nameText.setLayoutData(gridData);

	Label detailsLabel = new Label(area, SWT.NONE);
	detailsLabel.setText("Details:");
	gridData = new GridData();
	gridData.verticalAlignment = SWT.TOP;
	detailsLabel.setLayoutData(gridData);

	detailsText = new Text(area, SWT.BORDER | SWT.WRAP | SWT.MULTI);
	createControlDecoration(detailsText);
	gridData = new GridData();
	gridData.horizontalAlignment = SWT.FILL;
	gridData.grabExcessHorizontalSpace = true;
	gridData.verticalAlignment = SWT.FILL;
	gridData.grabExcessVerticalSpace = true;
	detailsText.setLayoutData(gridData);

	bindValues();
	WizardPageSupport.create(this, dbc);

    }

    /**
     * Creates the control decoration.
     * 
     * @param control
     *            the control
     */
    private void createControlDecoration(Control control) {
	ControlDecoration controlDecoration = new ControlDecoration(control, SWT.LEFT | SWT.TOP);
	FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
	controlDecoration.setImage(fieldDecoration.getImage());
	controlDecoration.hide();
	decoratorMap.put(control, controlDecoration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#dispose()
     */
    @Override
    public void dispose() {
	if (dbc != null) {
	    dbc.dispose();
	}
	super.dispose();
    }
}
