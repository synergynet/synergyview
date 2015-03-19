/**
 *  File: NewAttributeWizardPage.java
 *  Copyright (c) 2010
 *  phyo
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package synergyviewmvc.attributes.ui.wizards;


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
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import synergyviewmvc.attributes.model.Attribute;
import synergyviewmvc.collections.model.Collection;
import synergyviewmvc.databinding.validation.NotEmptyOrExistValidator;

/**
 * @author phyo
 *
 */
public class NewAttributeWizardPage extends WizardPage implements IWizardPage {
	private static HashMap<Control, ControlDecoration> decoratorMap = new HashMap<Control, ControlDecoration>();
	private Text _nameText;
	private Text _detailsText;
	 private Color _color;
	 private Attribute _attribute;
		private DataBindingContext _dbc = new DataBindingContext();
	/**
	 * @param pageName
	 */
	protected NewAttributeWizardPage(String pageName, Attribute attributeValue) {
		super(pageName);
		this.setTitle("New Attributes");
		_attribute = attributeValue;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite arg0) {
		final Composite composite = new Composite(arg0, SWT.NULL);
		GridData gridData = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		GridLayout layoutData = new GridLayout(3, false);
		composite.setLayout(layoutData);
		
		Label nameLabel = new Label(composite, SWT.NONE);
		nameLabel.setText("Name:");
		
		_nameText = new Text(composite, SWT.BORDER);
		createControlDecoration(_nameText);

		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		_nameText.setLayoutData(gridData);
		
		Label detailsLabel = new Label(composite, SWT.NONE);
		detailsLabel.setText("Details:");
		gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		detailsLabel.setLayoutData(gridData);
		
		_detailsText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		createControlDecoration(_detailsText);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		_detailsText.setLayoutData(gridData);

		final Label colorLabel = new Label(composite, SWT.NONE);
	    colorLabel.setText("Color");
	    

//		// Use a label full of spaces to show the color
//	    final Label colorLabelColor = new Label(composite, SWT.NONE);
//	    colorLabelColor.setText("                              ");
//	    _color = new Color(composite.getDisplay(), new RGB(0, 0, 0));
//	    colorLabelColor.setBackground(_color);
//
//	    Button button = new Button(composite, SWT.PUSH);
//	    button.setText("Change...");
//	    
//	    button.addSelectionListener(new SelectionAdapter() {
//	      public void widgetSelected(SelectionEvent event) {
//	        // Create the color-change dialog
//	        ColorDialog dlg = new ColorDialog(event.display.getActiveShell(), SWT.NONE);
//
//	        // Set the selected color in the dialog from
//	        // user's selected color
//	        dlg.setRGB(colorLabelColor.getBackground().getRGB());
//
//	        // Change the title bar text
//	        //dlg.setText("Choose a Color");
//
//	        // Open the dialog and retrieve the selected color
//	        RGB rgb = dlg.open();
//	        
//	        if (rgb != null) {
//	          // Dispose the old color, create the
//	          // new one, and set into the label
//	          _color.dispose();
//	          _color = new Color(composite.getDisplay(), rgb);
//	          colorLabelColor.setBackground(_color);
//	        }
//	      }
//	    });
        setControl(composite);
        bindValues();
        WizardPageSupport.create(this, _dbc);
	}

	private void bindValues() {

		List<String> existingNames = new ArrayList<String>();
//		if (_attribute.getParent()!=null)
//		for(Attribute attribute : _attribute.getParent().getChildren()) {
//			existingNames.add(attribute.getName());
//		}
		bind(_nameText, _attribute, Attribute.PROP_NAME, new NotEmptyOrExistValidator(Collection.PROP_NAME, existingNames));
		bind(_detailsText, _attribute, Attribute.PROP_DESCRIPTION, null);
		final AggregateValidationStatus aggregateValidationStatus = new AggregateValidationStatus(
				_dbc.getValidationStatusProviders(),
				AggregateValidationStatus.MAX_SEVERITY);

		aggregateValidationStatus
				.addValueChangeListener(new IValueChangeListener() {
					public void handleValueChange(ValueChangeEvent event) {
						// the invocation of the getValue method is necessary
						// the further changes will be fired
						aggregateValidationStatus.getValue();
						for (Object o : _dbc.getBindings()) {
							Binding binding = (Binding) o;
							IStatus status = (IStatus) binding
									.getValidationStatus().getValue();
							Control control = null;
							if (binding.getTarget() instanceof ISWTObservable) {
								ISWTObservable swtObservable = (ISWTObservable) binding
										.getTarget();
								control = (Control) swtObservable.getWidget();
							}
							ControlDecoration decoration = decoratorMap
									.get(control);
							if (decoration != null) {
								if (status.isOK()) {
									decoration.hide();
								} else {
									decoration.setDescriptionText(status
											.getMessage());
									decoration.show();
								}
							}
						}
					}
				});

	}
	
	private void bind(Text textWidget, Object bean,
			String property, IValidator validator) {
		UpdateValueStrategy targetToModel = null;
		if (validator != null) {
			targetToModel = new UpdateValueStrategy()
					.setAfterConvertValidator(validator);
		}
		_dbc.bindValue(SWTObservables.observeText(textWidget, SWT.Modify),
				BeansObservables.observeValue(bean, property), targetToModel,
				null);
	}
	private void createControlDecoration(Control control) {
		ControlDecoration controlDecoration = new ControlDecoration(control,
				SWT.LEFT | SWT.TOP);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		controlDecoration.setImage(fieldDecoration.getImage());
		controlDecoration.hide();
		decoratorMap.put(control, controlDecoration);
	}

}
