package synergyviewmvc.collections.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewmvc.collections.model.CollectionsRootNode;
import synergyviewmvc.collections.ui.wizards.NewCollectionWizard;
import synergyviewmvc.timebar.model.MediaIntervalImpl;
import synergyviewmvc.timebar.model.MediaSegmentIntervalImpl;

public class RemoveCollectionMediaHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSel = (IStructuredSelection) selection;
		Object element = structSel.iterator().next();
		
		if (element instanceof MediaIntervalImpl) {
			MediaIntervalImpl mediaIntervalImpl = (MediaIntervalImpl) element;
			mediaIntervalImpl.getOwner().remInterval(mediaIntervalImpl);
			return null;
		} else if (element instanceof MediaSegmentIntervalImpl) {
			MediaSegmentIntervalImpl mediaIntervalImpl = (MediaSegmentIntervalImpl) element;
			mediaIntervalImpl.getOwner().remInterval(mediaIntervalImpl);
			return null;
		} 
		
		else return null;
	}

}
