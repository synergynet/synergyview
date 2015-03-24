package synergyviewcore.projects.ui;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.navigator.CommonDragAdapterAssistant;


/**
 * The Class AttributesDragAdapter.
 */
public class AttributesDragAdapter extends CommonDragAdapterAssistant {
	
	/**
	 * Instantiates a new attributes drag adapter.
	 */
	public AttributesDragAdapter() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.navigator.CommonDragAdapterAssistant#getSupportedTransferTypes()
	 */
	@Override
	public Transfer[] getSupportedTransferTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.navigator.CommonDragAdapterAssistant#setDragData(org.eclipse.swt.dnd.DragSourceEvent, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public boolean setDragData(DragSourceEvent anEvent,
			IStructuredSelection aSelection) {
		// TODO Auto-generated method stub
		return false;
	}

}
