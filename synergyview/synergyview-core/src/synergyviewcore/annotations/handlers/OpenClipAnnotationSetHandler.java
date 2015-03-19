package synergyviewcore.annotations.handlers;

import java.util.NoSuchElementException;

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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.Activator;
import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.annotations.ui.editors.CollectionMediaClipAnnotationEditor;
import synergyviewcore.collections.ui.editors.CollectionEditor;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.projects.ui.NodeEditorInput;

public class OpenClipAnnotationSetHandler extends AbstractHandler implements IHandler {

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
			if (element instanceof AnnotationSetNode) {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();
	
				NodeEditorInput editorInput = new NodeEditorInput((INode) element);
				
				//TODO Refactor design
				for (IWorkbenchPage ref : window.getPages()) {
					if (ref.getActiveEditor() instanceof CollectionEditor) {
						CollectionEditor colEditor = (CollectionEditor) ref.getActiveEditor();
						NodeEditorInput cNodeEditorInput = (NodeEditorInput) colEditor.getEditorInput();
						if (editorInput.getNode().getParent() == cNodeEditorInput.getNode()) {
							MessageDialog.openError(window.getShell(), "Collection Editor Opened", "Please close the collection editor before editing the clips");
							return null;
						}
					}
				}
				try {
					page.openEditor(editorInput, CollectionMediaClipAnnotationEditor.ID);
				} catch (PartInitException ex) {
					IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
					logger.log(status);
				}	
			}		
			return null;
		}else{
			return null;
		}
	}
	
}
