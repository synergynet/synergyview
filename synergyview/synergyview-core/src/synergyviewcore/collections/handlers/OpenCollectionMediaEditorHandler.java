package synergyviewcore.collections.handlers;

import java.util.NoSuchElementException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.Activator;
import synergyviewcore.collections.model.CollectionNode;
import synergyviewcore.collections.ui.editors.CollectionEditor;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.projects.ui.NodeEditorInput;


/**
 * The Class OpenCollectionMediaEditorHandler.
 */
public class OpenCollectionMediaEditorHandler extends AbstractHandler implements IHandler  {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ILog logger = Activator.getDefault().getLog();

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
	
		Object element = null;
		
		try{
			element = structSel.iterator().next();
		}catch (NoSuchElementException e){}

		if (element != null){
		
		
			if (element instanceof CollectionNode) {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();
				IEditorInput editorInput = new NodeEditorInput((INode) element);
				try {
					page.openEditor(editorInput, CollectionEditor.ID);
				} catch (PartInitException ex) {
					IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
					logger.log(status);
				}	
			};
		}
			
		
		return null;
	}

}
