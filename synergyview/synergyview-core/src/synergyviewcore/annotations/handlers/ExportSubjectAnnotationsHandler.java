package synergyviewcore.annotations.handlers;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.Activator;
import synergyviewcore.annotations.format.IAnnotationFormatter;
import synergyviewcore.annotations.format.XmlAnnotationFormatter;
import synergyviewcore.annotations.ui.SubjectRowModel;


/**
 * The Class ExportSubjectAnnotationsHandler.
 */
public class ExportSubjectAnnotationsHandler extends AbstractHandler implements IHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ILog logger = Activator.getDefault().getLog();
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		IWorkbenchPart activePart = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getActivePart();
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection structSel = (IStructuredSelection) selection;
		Object object = structSel.getFirstElement();
		if (object instanceof SubjectRowModel) {
			SubjectRowModel selectedSubjectRow =  ((SubjectRowModel) object);
			FileDialog fd = new FileDialog(activePart.getSite().getShell(), SWT.SAVE);
	        fd.setText("Save Annotations for " + selectedSubjectRow.getSubject().getName());
	        String[] filterExt = { "*.axml" };
	        fd.setFilterExtensions(filterExt);
	        String selected = fd.open();
	        if (selected!=null) {
	        	BufferedOutputStream bufferedOutputStream = null;
	        	try {
	        		bufferedOutputStream = new BufferedOutputStream(
                            new FileOutputStream(selected));
	        		IAnnotationFormatter formatter = new XmlAnnotationFormatter();
	        		formatter.write(selectedSubjectRow.getAnnotationSetNode(), selectedSubjectRow.getSubject(), bufferedOutputStream);
	        	} catch (Exception ex) {
	        		IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
	        		logger.log(status);
	        		MessageDialog.openError(activePart.getSite().getShell(), "Save Error", "Unable to save Annotations!");
	        	} finally {
	        		if (bufferedOutputStream!=null) {
						try {
							bufferedOutputStream.close();
							MessageDialog.openInformation(activePart.getSite().getShell(), "Successful!", "File successfully saved.");
						} catch (IOException ex) {
							IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			        		logger.log(status);
							MessageDialog.openError(activePart.getSite().getShell(), "Save Error", "Unable to save Annotations!");
						}
	        		}
	        	}
	        }
		}
		
		return null;
	}

}
