/**
 * File: SubjectPropertyPage.java Copyright (c) 2010 phyo This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcore.subjects.properties;

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

import synergyviewcore.subjects.model.Subject;
import synergyviewcore.subjects.model.SubjectNode;

/**
 * The Class SubjectPropertyPage.
 * 
 * @author phyo
 */
public class SubjectPropertyPage extends PropertyPage {
	
	/** The collection model binding ctx. */
	private DataBindingContext collectionModelBindingCtx = new DataBindingContext();
	
	/** The subject. */
	private Subject subject;
	
	/** The subject desc text. */
	private Text subjectDescText;
	
	/** The subject name text. */
	private Text subjectNameText;
	
	/** The subject node. */
	private SubjectNode subjectNode;
	
	/**
	 * Constructor for SamplePropertyPage.
	 */
	public SubjectPropertyPage() {
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
		subjectNameText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		subjectNameText.setLayoutData(gridData);
		collectionModelBindingCtx.bindValue(
				SWTObservables.observeText(subjectNameText, SWT.Modify),
				BeansObservables.observeValue(subject, Subject.PROP_NAME),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST),
				null);
		
		// Label for session details
		Label sessionDetailsLabel = new Label(composite, SWT.NONE);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		gridData.horizontalAlignment = SWT.RIGHT;
		sessionDetailsLabel.setText("Details");
		sessionDetailsLabel.setLayoutData(gridData);
		
		// Session text field
		subjectDescText = new Text(composite, SWT.WRAP | SWT.BORDER | SWT.MULTI);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		subjectDescText.setLayoutData(gridData);
		collectionModelBindingCtx.bindValue(SWTObservables.observeText(
				subjectDescText, SWT.Modify), BeansObservables.observeValue(
				subject, Subject.PROP_DESCRIPTION), new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_ON_REQUEST), null);
		
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
		subjectNode = (SubjectNode) this.getElement();
		subject = subjectNode.getResource();
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
		subjectNode.updateResource();
	}
}
