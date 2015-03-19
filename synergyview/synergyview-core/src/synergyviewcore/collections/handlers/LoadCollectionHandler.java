package synergyviewcore.collections.handlers;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
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
import synergyviewcore.collections.format.ICollectionFormatter;
import synergyviewcore.collections.format.XmlCollectionFormatter;
import synergyviewcore.collections.model.CollectionNode;

public class LoadCollectionHandler extends AbstractHandler implements IHandler {


	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ILog logger = Activator.getDefault().getLog();

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		IWorkbenchPart activePart = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getActivePart();
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection structSel = (IStructuredSelection) selection;
		Object object = structSel.getFirstElement();
		if (object instanceof CollectionNode) {
			CollectionNode collectionNode =  ((CollectionNode) object);
			FileDialog fd = new FileDialog(activePart.getSite().getShell(), SWT.OPEN);
	        fd.setText("Open Collection Media Clips for " + collectionNode.getResource().getName());
	        String[] filterExt = { "*.ccxml" };
	        fd.setFilterExtensions(filterExt);
	        String selected = fd.open();
	        if (selected!=null) {
	        	BufferedInputStream bufferedInputStream = null;
	        	try {
	        		bufferedInputStream = new BufferedInputStream(
                            new FileInputStream(selected));
	        		ICollectionFormatter formatter = new XmlCollectionFormatter();
	        		formatter.read(collectionNode, bufferedInputStream);
	        		MessageDialog.openInformation(activePart.getSite().getShell(), "Successful!", "Annotations successfully loaded.");
	        	} catch (Exception ex) {
	        		IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
	        		logger.log(status);
	        		MessageDialog.openError(activePart.getSite().getShell(), "Open Error", "Unable to open Annotations!");
	        	} finally {
	        		if (bufferedInputStream!=null) {
						try {
							bufferedInputStream.close();
						} catch (IOException ex) {
							IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
							logger.log(status);
							MessageDialog.openError(activePart.getSite().getShell(), "Open Error", "Unable to open Annotations!");
						}
	        		}
	        	}
	        }
		}
		
		return null;
	}

}
