/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors: IBM
 * Corporation - initial API and implementation
 *******************************************************************************/
package synergyviewcore.media.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import synergyviewcore.Activator;
import synergyviewcore.media.model.MediaRootNode;

/**
 * The Class MediaImportWizardPage.
 */
public class MediaImportWizardPage extends WizardNewFileCreationPage {

    /** The editor. */
    protected FileFieldEditor editor;

    /** The logger. */
    private final ILog logger;

    /**
     * Instantiates a new media import wizard page.
     * 
     * @param pageName
     *            the page name
     * @param selection
     *            the selection
     */
    public MediaImportWizardPage(String pageName, IStructuredSelection selection) {
	super(pageName, selection);
	logger = Activator.getDefault().getLog();
	setTitle(pageName); // NON-NLS-1
	setDescription("Browse the file to import and select the target media folder."); // NON-NLS-1
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createAdvancedControls (org.eclipse.swt.widgets.Composite)
     */
    protected void createAdvancedControls(Composite parent) {
	Composite fileSelectionArea = new Composite(parent, SWT.NONE);
	GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
	fileSelectionArea.setLayoutData(fileSelectionData);

	GridLayout fileSelectionLayout = new GridLayout();
	fileSelectionLayout.numColumns = 3;
	fileSelectionLayout.makeColumnsEqualWidth = false;
	fileSelectionLayout.marginWidth = 0;
	fileSelectionLayout.marginHeight = 0;
	fileSelectionArea.setLayout(fileSelectionLayout);

	editor = new FileFieldEditor("fileSelect", "Select File: ", fileSelectionArea); // NON-NLS-1 //NON-NLS-2
	editor.getTextControl(fileSelectionArea).addModifyListener(new ModifyListener() {
	    public void modifyText(ModifyEvent e) {
		IPath path = new Path(MediaImportWizardPage.this.editor.getStringValue());
		setFileName(path.lastSegment());
	    }
	});
	String[] extensions = new String[] { "*.*" }; // NON-NLS-1
	editor.setFileExtensions(extensions);
	fileSelectionArea.moveAbove(null);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createLinkTarget()
     */
    protected void createLinkTarget() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
     */
    protected InputStream getInitialContents() {
	try {
	    return new FileInputStream(new File(editor.getStringValue()));
	} catch (FileNotFoundException ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	    return null;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getNewFileLabel()
     */
    protected String getNewFileLabel() {
	return "New File Name:"; // NON-NLS-1
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#validateLinkedResource()
     */
    protected IStatus validateLinkedResource() {
	if (this.getContainerFullPath().lastSegment().compareTo(MediaRootNode.getMediaFolderName()) == 0) {
	    return new Status(IStatus.OK, "synergyviewcore", IStatus.OK, "", null); // NON-NLS-1 //NON-NLS-2
	} else {
	    return new Status(IStatus.ERROR, "synergyviewcore", IStatus.ERROR, "", null); // NON-NLS-1 //NON-NLS-2
	}
    }
}
