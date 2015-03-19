package synergyviewcore.collection.ui.dialog;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import synergyviewcore.collection.model.MediaCollection;
import synergyviewcore.collection.ui.MediaCollectionEditor;
import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.project.OpenedProjectController;

public class MediaCollectionEntriesEditorDialog extends TitleAreaDialog {
	private MediaCollection mediaCollection;
	private static Logger logger = Logger.getLogger(MediaCollectionEntriesEditorDialog.class);
			
			
	public MediaCollectionEntriesEditorDialog(Shell parentShell, String mediaCollectionId) {
		super(parentShell);
		try {
			this.mediaCollection = OpenedProjectController.getInstance().getMediaCollectionController().findMediaCollectionById(mediaCollectionId);
		} catch (ObjectNotfoundException e) {
			logger.error("Unable to find media collection.", e);
		}
	}
	
	@Override
	protected Control createContents(Composite parent) {
		parent.setSize(900, 600);
	    Control contents = super.createContents(parent);
	    setTitle(mediaCollection.getName());
	    setMessage("Add or remove playable media to collection.", IMessageProvider.INFORMATION);
	    return contents;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		try {
			MediaCollectionEditor mediaCollectionEditor = new MediaCollectionEditor(mediaCollection.getId(), composite, SWT.NONE);
			mediaCollectionEditor.setLayoutData(new GridData(GridData.FILL_BOTH));
		} catch (Exception e) {
			logger.error("Unable to create media collection editor.", e);
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to create media collection editor.", e.getMessage());
			this.close();
		}
		
		return composite;
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);
	} 
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	  }



}
