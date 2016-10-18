package synergyviewcore.collections.ui.properties;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import synergyviewcore.collections.model.Collection;
import synergyviewcore.collections.model.CollectionNode;

/**
 * The Class CollectionPropertyPage.
 */
public class CollectionPropertyPage extends PropertyPage {

    /** The _collection. */
    private Collection _collection;

    /** The _collection details text. */
    private Text _collectionDetailsText;

    /** The _collection name text. */
    private Text _collectionNameText;

    /** The collection model binding ctx. */
    private DataBindingContext collectionModelBindingCtx = new DataBindingContext();

    /** The collection node. */
    private CollectionNode collectionNode;

    /**
     * Constructor for SamplePropertyPage.
     */
    public CollectionPropertyPage() {
	super();
    }

    /**
     * Adds the section details.
     * 
     * @param composite
     *            the composite
     */
    private void addSectionDetails(Composite composite) {

	// Creates a new tab item for session details
	GridLayout layout = new GridLayout(2, false);
	composite.setLayout(layout);

	GridData gridData = new GridData();

	// Label for session name field
	Label sessionNameLabel = new Label(composite, SWT.NONE);
	gridData = new GridData();
	gridData.verticalAlignment = SWT.TOP;
	gridData.horizontalAlignment = SWT.RIGHT;
	sessionNameLabel.setLayoutData(gridData);
	sessionNameLabel.setText("Name");

	// Session name text field
	_collectionNameText = new Text(composite, SWT.BORDER);
	gridData = new GridData();
	gridData.horizontalAlignment = SWT.FILL;
	gridData.grabExcessHorizontalSpace = true;
	_collectionNameText.setLayoutData(gridData);
	collectionModelBindingCtx.bindValue(SWTObservables.observeText(_collectionNameText, SWT.Modify), BeansObservables.observeValue(_collection, Collection.PROP_NAME), new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST), null);

	// Label for session details
	Label sessionDetailsLabel = new Label(composite, SWT.NONE);
	gridData = new GridData();
	gridData.verticalAlignment = SWT.TOP;
	gridData.horizontalAlignment = SWT.RIGHT;
	sessionDetailsLabel.setText("Details");
	sessionDetailsLabel.setLayoutData(gridData);

	// Session text field
	_collectionDetailsText = new Text(composite, SWT.WRAP | SWT.BORDER | SWT.MULTI);
	gridData = new GridData();
	gridData.horizontalAlignment = SWT.FILL;
	gridData.grabExcessHorizontalSpace = true;
	gridData.verticalAlignment = SWT.FILL;
	gridData.grabExcessVerticalSpace = true;
	_collectionDetailsText.setLayoutData(gridData);
	collectionModelBindingCtx.bindValue(SWTObservables.observeText(_collectionDetailsText, SWT.Modify), BeansObservables.observeValue(_collection, Collection.PROP_DETAILS), new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST), null);

    }

    /**
     * Creates the contents.
     * 
     * @param parent
     *            the parent
     * @return the control
     * @see PreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {

	collectionNode = (CollectionNode) this.getElement();
	_collection = collectionNode.getResource();
	Composite composite = new Composite(parent, SWT.NONE);
	addSectionDetails(composite);
	return composite;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#dispose()
     */
    @Override
    public void dispose() {
	if (collectionModelBindingCtx != null) {
	    collectionModelBindingCtx.dispose();
	}
	super.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performApply()
     */
    @Override
    protected void performApply() {
	saveCollectionData();
	super.performApply();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
	collectionModelBindingCtx.updateTargets();
	super.performDefaults();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    public boolean performOk() {
	saveCollectionData();
	return true;
    }

    /**
     * Save collection data.
     */
    private void saveCollectionData() {
	collectionModelBindingCtx.updateModels();
	collectionNode.updateResource();
    }

}