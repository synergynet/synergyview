package synergyviewcore.attributes.ui.properties;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import synergyviewcore.attributes.model.Attribute;
import synergyviewcore.attributes.model.AttributeNode;

public class AttributePropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {
	private AttributeNode attributeNode;
	private Attribute attribute;
	private Text attributeNameText;
	private Text attributeDetailsText;
	private Label colorLabelColor;
	private DataBindingContext attributeModelBindingCtx = new DataBindingContext();
	private Color currentColor;
	private Color selectedColor;
	public AttributePropertyPage() {
		super();
	}
	
	@Override
	public void dispose() {
		if (attributeModelBindingCtx!=null)
			attributeModelBindingCtx.dispose();
		super.dispose();
		if (currentColor!=null && !currentColor.isDisposed())
			currentColor.dispose();
		if (selectedColor!=null && !selectedColor.isDisposed())
			selectedColor.dispose();
	}


	@Override
	protected Control createContents(Composite parent) {
		attributeNode = (AttributeNode) this.getElement();
		attribute = attributeNode.getResource();
		Composite composite = new Composite(parent, SWT.NONE);
		addSectionDetails(composite);
		return composite;
	}

	private void addSectionDetails(final Composite composite) {

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
		

		final Label colorLabel = new Label(composite, SWT.NONE);
	    colorLabel.setText("Color:");
	    colorLabelColor = new Label(composite, SWT.NONE);
	    colorLabelColor.setText("                              ");
	    String[] rgb = attribute.getColorName().split(",");
	    currentColor = new Color(composite.getDisplay(), new RGB(Integer.parseInt(rgb[0]),Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
	    colorLabelColor.setBackground(currentColor);

		final Button changeColourButton = new Button(composite, SWT.PUSH | SWT.BORDER);
	    changeColourButton.setText("Change Colour");
	    changeColourButton.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	        ColorDialog cd = new ColorDialog(composite.getShell());
	        cd.setText("Attribute Colour Dialog");
	        cd.setRGB(new RGB(255, 255, 255));
	        RGB newColor = cd.open();
	        if (newColor != null) {
	        	if (selectedColor!=null && !selectedColor.isDisposed())
	        		selectedColor.dispose();
	        	selectedColor = new Color(composite.getDisplay(), newColor);
		        colorLabelColor.setBackground(selectedColor);
	        }
	      }
	    });
	}


	private void saveAttributeData() {
		attributeModelBindingCtx.updateModels();
		if (selectedColor!=null && !selectedColor.isDisposed())
		{
			currentColor.dispose();
			currentColor = new Color(this.getShell().getDisplay(), selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue());
			selectedColor.dispose();
			selectedColor = null;
			attribute.setColorName(String.format("%d,%d,%d", currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue()));
		}
		attributeNode.getProjectAttributeRootNode().updateResource(attribute);
	}


	@Override
	protected void performDefaults() {
		if (selectedColor!=null && !selectedColor.isDisposed()) {
    		selectedColor.dispose();
    		selectedColor = null;
		}
		colorLabelColor.setBackground(currentColor);
		attribute.setColorName(String.format("%d,%d,%d", currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue()));
		attributeModelBindingCtx.updateTargets();
		super.performDefaults();
	}

	@Override
	protected void performApply() {
		saveAttributeData();
		super.performApply();
	}

	public boolean performOk() {
		saveAttributeData();
		return true;
	}

}
