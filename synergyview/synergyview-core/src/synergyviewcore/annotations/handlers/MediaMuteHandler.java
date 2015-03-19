package synergyviewcore.annotations.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.annotations.ui.MediaClipIntervalImpl;

public class MediaMuteHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		Object element = structSel.iterator().next();
		
		
		if (element instanceof MediaClipIntervalImpl) {
			MediaClipIntervalImpl mediaClipIntervalImpl = (MediaClipIntervalImpl) element;
			if (mediaClipIntervalImpl.isMute())
				mediaClipIntervalImpl.setMute(false);
			else mediaClipIntervalImpl.setMute(true);
		}
		return null;
	}

}
