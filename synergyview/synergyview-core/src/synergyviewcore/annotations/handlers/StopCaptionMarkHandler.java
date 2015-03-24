package synergyviewcore.annotations.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import synergyviewcore.annotations.ui.editors.CollectionMediaClipAnnotationEditor;

/**
 * The Class StopCaptionMarkHandler.
 */
public class StopCaptionMarkHandler extends AbstractHandler implements IHandler {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		if (page.getActiveEditor() instanceof CollectionMediaClipAnnotationEditor) {
			CollectionMediaClipAnnotationEditor subtitleEditor = (CollectionMediaClipAnnotationEditor) page
					.getActiveEditor();
			subtitleEditor.getAnnotationMediaControl().stopCaptionMark();
		}
		return null;
	}
	
}
