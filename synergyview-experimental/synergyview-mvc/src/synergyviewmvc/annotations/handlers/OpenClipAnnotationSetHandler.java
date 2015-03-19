package synergyviewmvc.annotations.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewmvc.annotations.model.AnnotationSetNode;
import synergyviewmvc.annotations.ui.editors.CollectionMediaClipAnnotationEditor;
import synergyviewmvc.collections.ui.editors.CollectionEditor;
import synergyviewmvc.projects.ui.NodeEditorInput;
import uk.ac.durham.tel.commons.jface.node.INode;

public class OpenClipAnnotationSetHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		Object element = structSel.iterator().next();
		
		
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
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
			
		
		return null;
	}
}
