/**
 *  File: ExportCollectionAnnotationFileSelectorWizardPage.java
 *  Copyright (c) 2011
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

package synergyviewcore.collections.ui.wizards;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

/**
 * @author phyo
 *
 */
public class ExportCollectionAnnotationFileSelectorWizardPage extends
		WizardPage {
	
	private File selectedFile = null;
	/**
	 * @param pageName
	 */
	protected ExportCollectionAnnotationFileSelectorWizardPage() {
		super("Export Annotations in the Collection File Browser Page");
		this.setTitle("Export Annotations in the Collection");
		setDescription("Please choose a destination file to export the selected Annotations.");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		
		final Composite area = new Composite(parent, SWT.NONE);
		setControl(area);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		area.setLayoutData(gridData);
		GridLayout layoutData = new GridLayout(3, false);
		area.setLayout(layoutData);
		this.setPageComplete(false);
		Label destLabel = new Label (area, SWT.NONE);
		destLabel.setText("Destination:");
		gridData = new GridData();
		destLabel.setLayoutData(gridData);
		
		final Text dirText = new Text(area,SWT.READ_ONLY);	
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		dirText.setLayoutData(gridData);
		
		Button browseButton = new Button(area, SWT.NONE);
		browseButton.setText("Browse");
		gridData = new GridData();
		browseButton.setLayoutData(gridData);
		
		browseButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				boolean done = false;
				ExportCollectionAnnotationFileSelectorWizardPage.this.setPageComplete(false);
				FileDialog fd = new FileDialog(area.getShell(), SWT.SAVE);
		        fd.setText("Export Annotations in the Collection");
		        String[] filterExt = { "*.tsv" };
		        fd.setFilterExtensions(filterExt);
		        String selected = fd.open();
		        if (selected!=null) {
		        	selectedFile = new File(selected);
		        	 if (selectedFile.exists()) {
		                 // The file already exists; asks for confirmation
		                 MessageBox mb = new MessageBox(fd.getParent(), SWT.ICON_WARNING
		                     | SWT.YES | SWT.NO);
		                 mb.setMessage(selected + " already exists. Do you want to replace it?");
		                 if (mb.open() == SWT.YES) {
		                	 done = true;
		                 } 
		        	 } else {
		        		 done = true;
		        	 }
		        	 if (done) {
		        		 dirText.setText(selectedFile.getAbsolutePath());
	                	 ExportCollectionAnnotationFileSelectorWizardPage.this.setPageComplete(true);
		        	 }
		        }		
			}
		});
		
	}
	
	public File getSelectedFile() {
		return selectedFile;
	}

}
