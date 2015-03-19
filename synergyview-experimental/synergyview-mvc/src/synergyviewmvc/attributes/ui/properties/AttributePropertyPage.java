package synergyviewmvc.attributes.ui.properties;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import synergyviewmvc.attributes.model.Attribute;
import synergyviewmvc.attributes.model.AttributeNode;

public class AttributePropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {
	private AttributeNode attributeNode;
	private Attribute attribute;
	private Text attributeNameText;
	private Text attributeDetailsText;
	private DataBindingContext attributeModelBindingCtx = new DataBindingContext();
	
	public AttributePropertyPage() {
		super();
	}
	
	@Override
	public void dispose() {
		if (attributeModelBindingCtx!=null)
			attributeModelBindingCtx.dispose();
		super.dispose();
	}


	@Override
	protected Control createContents(Composite parent) {
		attributeNode = (AttributeNode) this.getElement();
		attribute = attributeNode.getResource();
		Composite composite = new Composite(parent, SWT.NONE);
		addSectionDetails(composite);
		return composite;
	}

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
		attributeNameText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		attributeNameText.setLayoutData(gridData);
		attributeModelBindingCtx.bindValue(SWTObservables.observeText(
				attributeNameText, SWT.Modify), BeansObservables.observeValue(
						attribute, Attribute.PROP_NAME), new UpdateValueStrategy(
								UpdateValueStrategy.POLICY_ON_REQUEST), null);
		
		// Label for session details
		Label sessionDetailsLabel = new Label(composite, SWT.NONE);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		gridData.horizontalAlignment = SWT.RIGHT;
		sessionDetailsLabel.setText("Details");
		sessionDetailsLabel.setLayoutData(gridData);

		// Session text field
		attributeDetailsText = new Text(composite, SWT.WRAP | SWT.BORDER
				| SWT.MULTI);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		attributeDetailsText.setLayoutData(gridData);
		attributeModelBindingCtx.bindValue(SWTObservables.observeText(
				attributeDetailsText, SWT.Modify), BeansObservables.observeValue(
						attribute, Attribute.PROP_DESCRIPTION), new UpdateValueStrategy(
								UpdateValueStrategy.POLICY_ON_REQUEST), null);
		
	}


	private void saveCollectionData() {
		attributeModelBindingCtx.updateModels();
		attributeNode.updateResource();
	}


	@Override
	protected void performDefaults() {
		attributeModelBindingCtx.updateTargets();
		super.performDefaults();
	}

	@Override
	protected void performApply() {
		saveCollectionData();
		super.performApply();
	}

	public boolean performOk() {
		saveCollectionData();
		return true;
	}

}
