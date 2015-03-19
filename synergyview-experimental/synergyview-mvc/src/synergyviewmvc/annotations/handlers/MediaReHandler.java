package synergyviewmvc.annotations.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import synergyviewmvc.annotations.ui.editors.CollectionMediaClipAnnotationEditor;

public class MediaReHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		IWorkbenchPage page = window.getActivePage();
		if (page.getActiveEditor() instanceof CollectionMediaClipAnnotationEditor) {
			CollectionMediaClipAnnotationEditor subtitleEditor = (CollectionMediaClipAnnotationEditor) page.getActiveEditor();
			subtitleEditor.getSubtitleMediaControl().stepRE();
		}
		return null;
	}

}
