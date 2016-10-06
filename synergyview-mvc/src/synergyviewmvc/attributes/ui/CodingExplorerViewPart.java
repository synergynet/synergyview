package synergyviewmvc.attributes.ui;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

import synergyviewmvc.attributes.model.CodingRoot;
import synergyviewmvc.navigation.NavigatorContentProvider;
import synergyviewmvc.navigation.NavigatorLabelProvider;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class CodingExplorerViewPart extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "uk.ac.durham.tel.synergynet.ats.attributes.ui.CodingExplorerViewPart";

	private TreeViewer _attributesViewer;

	/**
	 * The constructor.
	 */
	public CodingExplorerViewPart() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		_attributesViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		NavigatorContentProvider attributesContentProvider = new  NavigatorContentProvider();
		_attributesViewer.setContentProvider(attributesContentProvider);
		_attributesViewer.setLabelProvider(new NavigatorLabelProvider(attributesContentProvider.getKnownElements()));	
		CodingRoot root = CodingRoot.getInstance();
		_attributesViewer.setInput(root);
		root.setTreeViewer(_attributesViewer);
		
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu (_attributesViewer.getControl());
		_attributesViewer.getControl ().setMenu (menu);
		getSite().registerContextMenu (menuManager, _attributesViewer);
		getSite().setSelectionProvider(_attributesViewer);
		
		int ops = DND.DROP_COPY | DND.DROP_DEFAULT;
		Transfer[] transfers = new Transfer[] { LocalSelectionTransfer.getTransfer() };
		DragSourceListener dsListener = new DragSourceListener() {

			public void dragFinished(DragSourceEvent event) {
				LocalSelectionTransfer.getTransfer().setSelection(null);
				LocalSelectionTransfer.getTransfer().setSelectionSetTime(0);
			}

			public void dragSetData(DragSourceEvent event) {
				event.data = LocalSelectionTransfer.getTransfer().getSelection();
			}

			public void dragStart(DragSourceEvent event) {
		        ISelection selection = _attributesViewer.getSelection();
		        LocalSelectionTransfer.getTransfer().setSelection(selection);
		        LocalSelectionTransfer.getTransfer().setSelectionSetTime(event.time & 0xFFFFFFFFL);
		        event.doit = !selection.isEmpty();
			}

		};
		_attributesViewer.addDragSupport(ops, transfers, dsListener);


	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		_attributesViewer.getControl().setFocus();
	}
}