package synergyviewcore.attributes.ui.views;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import synergyviewcore.attributes.model.AttributeNode;
import synergyviewcore.attributes.model.ProjectAttributeRootNode;
import synergyviewcore.navigation.NavigatorContentProvider;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.projects.model.ProjectNode;
import synergyviewcore.projects.ui.ProjectExplorerViewPart;



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

public class CodingExplorerViewPart extends ViewPart implements ISelectionListener {

	/** The is sticky. */
	private boolean isSticky;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		this.getSite().getPage().removeSelectionListener(this);
		if (attributesViewer!=null && attributesViewer.getInput()!=null) 
			attributesViewer.setInput(null);
		super.dispose();
	}

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "synergyviewcore.attributes.ui.CodingExplorerViewPart";

	/** The attributes viewer. */
	private TreeViewer attributesViewer;
	
	/** The label. */
	private Label label;
	/**
	 * The constructor.
	 */
	public CodingExplorerViewPart() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 *
	 * @param parent the parent
	 */
	public void createPartControl(Composite parent) {
		Composite composite = new Composite (parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.
				FILL, true, true));
		composite.setLayout(new GridLayout(1, false));
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		label = new Label(composite,SWT.NONE);
		label.setLayoutData(gd);
		label.setText("No project selected");
		
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;
		attributesViewer = new TreeViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		attributesViewer.getControl().setLayoutData(gd);
		NavigatorContentProvider attributesContentProvider = new  NavigatorContentProvider();
		attributesViewer.setContentProvider(attributesContentProvider);
		TreeViewerColumn c = new TreeViewerColumn(attributesViewer, SWT.NONE);
		c.getColumn().setText("Name");
		c.setLabelProvider(new AttributeTextLabelProvider());
		c.getColumn().setWidth(200);
		TreeViewerColumn c2 = new TreeViewerColumn(attributesViewer, SWT.NONE);
		c2.setLabelProvider(new AttributeColorLabelProvider());
		c2.getColumn().setText("Color");
		c2.getColumn().setWidth(60);

		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu (attributesViewer.getControl());
		attributesViewer.getControl ().setMenu (menu);
		getSite().registerContextMenu (menuManager, attributesViewer);
		getSite().setSelectionProvider(attributesViewer);
		
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
		        ISelection selection = attributesViewer.getSelection();
		        LocalSelectionTransfer.getTransfer().setSelection(selection);
		        LocalSelectionTransfer.getTransfer().setSelectionSetTime(event.time & 0xFFFFFFFFL);
		        event.doit = !selection.isEmpty();
			}

		};
		attributesViewer.addDragSupport(ops, transfers, dsListener);
		this.getSite().getPage().addSelectionListener(this);
		
		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		attributesViewer.getControl().setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof ProjectExplorerViewPart  && selection instanceof IStructuredSelection) {
			if (!selection.isEmpty() && (selection instanceof IStructuredSelection) && (((IStructuredSelection)selection).getFirstElement() instanceof ProjectNode)) {
				ProjectNode node = (ProjectNode) ((IStructuredSelection)selection).getFirstElement();
				if (attributesViewer.getInput() == null) {
					setInput(node.getProjectAttributeRootNode());
				}
				else {
					if (!isSticky) {
						setInput(node.getProjectAttributeRootNode());
					}
				}
			} else {
				if (!isSticky && attributesViewer.getInput()!=null) {
					label.setText("No project selected");
					((ProjectAttributeRootNode) attributesViewer.getInput()).setTreeViewer(null);
					attributesViewer.setInput(null);
				}
			}
		}
	}
	
	/**
	 * Sets the input.
	 *
	 * @param node the new input
	 */
	private void setInput(ProjectAttributeRootNode node) {
		node.setTreeViewer(attributesViewer);
		attributesViewer.setInput(node); 
		label.setText(node.getLabel());
		
	}

	
	/**
	 * Gets the root node.
	 *
	 * @return the root node
	 */
	public INode getRootNode() {
		if (attributesViewer!=null)
			return (INode) attributesViewer.getInput();
		else return null;
	}

	/**
	 * Sets the sticky.
	 *
	 * @param isSticky the new sticky
	 */
	public void setSticky(boolean isSticky) {
		this.isSticky = isSticky;
	}

	/**
	 * Checks if is sticky.
	 *
	 * @return true, if is sticky
	 */
	public boolean isSticky() {
		return isSticky;
	}
	
	/**
	 * The Class AttributeTextLabelProvider.
	 */
	private static class AttributeTextLabelProvider extends StyledCellLabelProvider {

		/** The resource manager. */
		private LocalResourceManager resourceManager = new LocalResourceManager(
				JFaceResources.getResources());
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.StyledCellLabelProvider#dispose()
		 */
		@Override
		public void dispose() {
			resourceManager.dispose();
			super.dispose();
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
		 */
		@Override
		public void update(ViewerCell cell) {
			if (cell.getElement() instanceof AttributeNode) {
				AttributeNode node = (AttributeNode) cell.getElement();
				cell.setText(node.getResource().getName());
				cell.setImage((Image) resourceManager.get(((INode) node).getIcon()));
			}
			
			super.update(cell);
		}
	}
	
	/**
	 * The Class AttributeColorLabelProvider.
	 */
	private static class AttributeColorLabelProvider extends StyledCellLabelProvider {
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
		 */
		@Override
		public void update(ViewerCell cell) {
			if (cell.getElement() instanceof AttributeNode) {
				AttributeNode node = (AttributeNode) cell.getElement();
				String[] rgb = node.getResource().getColorName().split(",");
				if (node.getResource().getChildren().isEmpty())
					cell.setBackground(new Color(Display.getDefault(), Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
				else 
					cell.setBackground(null);
			} 
			
			super.update(cell);
		}
	}
}