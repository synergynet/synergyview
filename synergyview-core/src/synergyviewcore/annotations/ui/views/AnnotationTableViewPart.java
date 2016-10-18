package synergyviewcore.annotations.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

import synergyviewcommons.collections.CollectionChangeEvent;
import synergyviewcommons.collections.CollectionChangeListener;
import synergyviewcommons.collections.CollectionDiffEntry;
import synergyviewcore.annotations.model.Annotation;
import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.annotations.model.IntervalAnnotation;
import synergyviewcore.annotations.ui.AnnotationIntervalImpl;
import synergyviewcore.annotations.ui.editors.CollectionMediaClipAnnotationEditor;
import synergyviewcore.attributes.model.Attribute;
import synergyviewcore.attributes.model.AttributeNode;
import synergyviewcore.util.DateTimeHelper;

/**
 * The Class AnnotationTableViewPart.
 */
public class AnnotationTableViewPart extends ViewPart implements ISelectionListener {

    /**
     * The Class AnnotationLabelProvider.
     */
    private static class AnnotationLabelProvider extends StyledCellLabelProvider {

	/** The resource manager. */
	private LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.StyledCellLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
	    resourceManager.dispose();
	    super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse .jface.viewers.ViewerCell)
	 */
	@Override
	public void update(ViewerCell cell) {

	    if (cell.getElement() instanceof Annotation) {
		Annotation annotation = (Annotation) cell.getElement();
		switch (cell.getColumnIndex()) {
		case 0:
		    cell.setText(DateTimeHelper.getHMSFromMilli(annotation.getStartTime()));
		    break;
		case 1:
		    if (annotation instanceof IntervalAnnotation) {
			cell.setText(DateTimeHelper.getHMSFromMilli(((IntervalAnnotation) annotation).getDuration()));
		    } else {
			cell.setText("-");
		    }
		    break;
		case 2:
		    cell.setText(annotation.getSubject().getName());
		    break;
		case 3:
		    cell.setText(annotation.getText());
		    break;
		case 4:
		    cell.setText("");
		    break;
		default:
		    cell.setText("");
		    break;
		}
	    }

	    super.update(cell);
	}
    }

    /**
     * The Class AnnotationsContentProvider.
     */
    private static class AnnotationsContentProvider implements IStructuredContentProvider {

	/** The annotation set node. */
	private AnnotationSetNode annotationSetNode;

	/** The content list. */
	private List<?> contentList;

	/** The known elements. */
	private IObservableSet knownElements;

	/** The listener. */
	private CollectionChangeListener listener = new CollectionChangeListener() {
	    public void listChanged(CollectionChangeEvent event) {
		CollectionDiffEntry<?>[] differences = event.getListDiff().getDifferences();
		for (int i = 0; i < differences.length; i++) {
		    CollectionDiffEntry<?> entry = differences[i];
		    if (entry.isAddition()) {
			knownElements.add(entry.getElement());
			if (viewer instanceof AbstractListViewer) {
			    ((AbstractListViewer) viewer).add(entry.getElement());
			} else {
			    ((TableViewer) viewer).insert(entry.getElement(), entry.getPosition());
			}
		    } else {

			if (viewer instanceof AbstractListViewer) {
			    ((AbstractListViewer) viewer).remove(entry.getElement());
			} else {
			    ((TableViewer) viewer).remove(entry.getElement());
			}
			knownElements.remove(entry.getElement());
		    }
		}
	    }
	};

	/** The viewer. */
	private Viewer viewer;

	/**
	 * Instantiates a new annotations content provider.
	 */
	public AnnotationsContentProvider() {
	    knownElements = new WritableSet(SWTObservables.getRealm(Display.getDefault()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	    if (annotationSetNode != null) {
		annotationSetNode.removeAnnotationListChangeListener(listener);
	    }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements( java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
	    return contentList != null ? contentList.toArray() : new Object[] {};
	}

	/**
	 * Gets the known elements.
	 * 
	 * @return the known elements
	 */
	@SuppressWarnings("unused")
	public IObservableSet getKnownElements() {
	    return knownElements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	    this.viewer = viewer;
	    if ((newInput != null) && (!(newInput instanceof AnnotationSetNode))) {
		throw new IllegalArgumentException("This content provider only works with input of type ObservableList");
	    }

	    if (annotationSetNode != null) {
		annotationSetNode.removeAnnotationListChangeListener(listener);
		annotationSetNode = null;
	    }

	    knownElements.clear();
	    if (newInput != null) {
		contentList = (List<?>) ((AnnotationSetNode) newInput).getAnnotationList();
		knownElements.addAll(contentList);
		annotationSetNode = (AnnotationSetNode) newInput;
		annotationSetNode.addAnnotationListChangeListener(listener);
	    } else {
		contentList = null;
	    }

	}
    }

    /**
     * The listener interface for receiving attributeDrop events. The class that is interested in processing a attributeDrop event implements this interface, and the object created with that class is registered with a component using the component's <code>addAttributeDropListener<code> method. When
     * the attributeDrop event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see AttributeDropEvent
     */
    private static class AttributeDropListener extends ViewerDropAdapter {

	/** The drop target. */
	private Annotation dropTarget;

	/** The node. */
	private AnnotationSetNode node;

	/**
	 * Instantiates a new attribute drop listener.
	 * 
	 * @param viewer
	 *            the viewer
	 */
	public AttributeDropListener(TableViewer viewer) {
	    super(viewer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#dragOver(org.eclipse. swt.dnd.DropTargetEvent)
	 */
	@Override
	public void dragOver(org.eclipse.swt.dnd.DropTargetEvent event) {
	    Object obj = this.determineTarget(event);
	    if (obj != null) {
		event.detail = DND.DROP_COPY;
	    }
	    super.dragOver(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#drop(org.eclipse.swt. dnd.DropTargetEvent)
	 */
	@Override
	public void drop(org.eclipse.swt.dnd.DropTargetEvent event) {
	    Object obj = this.determineTarget(event);
	    if ((obj != null) && (obj instanceof Annotation)) {
		dropTarget = (Annotation) obj;
	    }
	    super.drop(event);
	}

	// This method performs the actual drop
	// We simply add the String we receive to the model and trigger a
	// refresh of the
	// viewer by calling its setInput method.
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#performDrop(java.lang .Object)
	 */
	@Override
	public boolean performDrop(Object data) {
	    if (dropTarget != null) {
		if (data instanceof TreeSelection) {
		    TreeSelection selection = (TreeSelection) data;
		    if (!selection.isEmpty()) {
			@SuppressWarnings("rawtypes")
			Iterator i = selection.iterator();
			List<Attribute> list = new ArrayList<Attribute>();
			while (i.hasNext()) {
			    Object o = i.next();
			    if (o instanceof AttributeNode) {
				AttributeNode attributeNode = (AttributeNode) o;
				if (attributeNode.getChildren().isEmpty()) {
				    if (!list.contains(attributeNode.getResource())) {
					list.add(attributeNode.getResource());
				    }
				}
				if (node != null) {
				    node.getAnnotationAttributeController(dropTarget).addAttributeList(list);
				}
			    }
			}
		    }
		}
	    }
	    dropTarget = null;
	    return true;
	}

	/**
	 * Sets the node.
	 * 
	 * @param node
	 *            the new node
	 */
	public void setNode(AnnotationSetNode node) {
	    this.node = node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(java.lang .Object, int, org.eclipse.swt.dnd.TransferData)
	 */
	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
	    return (LocalSelectionTransfer.getTransfer().isSupportedType(transferType));
	}

    }

    /** The Constant ID. */
    public static final String ID = "synergyviewcore.annotations.AnnotationTableView";

    /** The active editor part listener. */
    private IPartListener2 activeEditorPartListener;

    /** The annotation set label. */
    private Label annotationSetLabel;

    /** The annotation set table viewer. */
    private TableViewer annotationSetTableViewer;

    /** The composite. */
    private Composite composite;

    /** The current annotation set node. */
    private AnnotationSetNode currentAnnotationSetNode;

    /** The drop listener. */
    private AttributeDropListener dropListener;

    /** The is sticky. */
    private boolean isSticky;

    /** The label grid data. */
    private GridData labelGridData;

    /** The viewer grid data. */
    private GridData viewerGridData;

    /**
     * Instantiates a new annotation table view part.
     */
    public AnnotationTableViewPart() {
	//
    }

    /**
     * Clear data.
     */
    private void clearData() {
	if ((annotationSetTableViewer.getContentProvider() != null) && (annotationSetTableViewer.getInput() != null)) {
	    annotationSetTableViewer.setInput(null);
	}
	currentAnnotationSetNode = null;
	dropListener.setNode(null);
	getSite().getPage().removeSelectionListener(this);
    }

    @Override
    public void createPartControl(Composite parent) {
	composite = new Composite(parent, SWT.NONE);
	composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	composite.setLayout(new GridLayout(1, false));
	labelGridData = new GridData();
	labelGridData.horizontalAlignment = SWT.FILL;

	labelGridData.grabExcessHorizontalSpace = true;
	annotationSetLabel = new Label(composite, SWT.NONE);
	annotationSetLabel.setLayoutData(labelGridData);
	annotationSetLabel.setText("");

	viewerGridData = new GridData();
	viewerGridData.horizontalAlignment = SWT.FILL;
	viewerGridData.grabExcessHorizontalSpace = true;
	viewerGridData.verticalAlignment = SWT.FILL;
	viewerGridData.grabExcessVerticalSpace = true;
	annotationSetTableViewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	annotationSetTableViewer.getControl().setLayoutData(viewerGridData);

	TableViewerColumn sTimeColumn = new TableViewerColumn(annotationSetTableViewer, SWT.NONE);
	sTimeColumn.getColumn().setText("Start Time");
	sTimeColumn.getColumn().setWidth(100);
	sTimeColumn.setLabelProvider(new AnnotationLabelProvider());
	TableViewerColumn durationColumn = new TableViewerColumn(annotationSetTableViewer, SWT.NONE);
	durationColumn.getColumn().setText("Duration");
	durationColumn.getColumn().setWidth(100);
	durationColumn.setLabelProvider(new AnnotationLabelProvider());
	TableViewerColumn subjectColumn = new TableViewerColumn(annotationSetTableViewer, SWT.NONE);
	subjectColumn.getColumn().setText("Subject");
	subjectColumn.getColumn().setWidth(100);
	subjectColumn.setLabelProvider(new AnnotationLabelProvider());
	TableViewerColumn textColumn = new TableViewerColumn(annotationSetTableViewer, SWT.NONE);
	textColumn.getColumn().setText("Text");
	textColumn.getColumn().setWidth(800);
	textColumn.setLabelProvider(new AnnotationLabelProvider());
	// textColumn.setLabelProvider(new OwnerDrawLabelProvider() {
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.eclipse.jface.viewers.OwnerDrawLabelProvider#measure(org.
	// * eclipse.swt.widgets.Event, java.lang.Object)
	// */
	// protected void measure(Event event, Object element) {
	// Annotation annotation = (Annotation) element;
	// Point size = event.gc.textExtent(annotation.getText());
	// event.width = annotationSetTableViewer.getTable()
	// .getColumn(event.index).getWidth();
	// int lines = size.x / event.width + 1;
	// event.height = size.y * lines;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.eclipse.jface.viewers.OwnerDrawLabelProvider#paint(org.eclipse
	// * .swt.widgets.Event, java.lang.Object)
	// */
	// protected void paint(Event event, Object element) {
	// Annotation entry = (Annotation) element;
	// event.gc.drawText(entry.getText(), event.x, event.y, true);
	// }
	// });

	TableViewerColumn attributesColumn = new TableViewerColumn(annotationSetTableViewer, SWT.NONE);
	attributesColumn.getColumn().setText("Attributes");
	attributesColumn.getColumn().setWidth(100);
	attributesColumn.setLabelProvider(new AnnotationLabelProvider());
	annotationSetTableViewer.getTable().setLinesVisible(true);
	annotationSetTableViewer.getTable().setHeaderVisible(true);
	annotationSetTableViewer.setContentProvider(new AnnotationsContentProvider());
	// annotationSetTableViewer.getTable().addListener(SWT.EraseItem, new
	// Listener() {
	// public void handleEvent(Event event) {
	// event.detail &= ~SWT.HOT;
	// if ((event.detail & SWT.SELECTED) == 0) return; /// item not selected
	//
	// Table table =(Table)event.widget;
	// int clientWidth = table.getClientArea().width;
	//
	// GC gc = event.gc;
	// Color oldForeground = gc.getForeground();
	// Color oldBackground = gc.getBackground();
	//
	// gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
	// //gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
	// gc.fillRectangle(0, event.y, clientWidth, event.height);
	//
	// gc.setForeground(oldForeground);
	// gc.setBackground(oldBackground);
	// event.detail &= ~SWT.SELECTED;
	// }
	// });

	int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
	Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance(), LocalSelectionTransfer.getTransfer() };
	dropListener = new AttributeDropListener(annotationSetTableViewer);
	annotationSetTableViewer.addDropSupport(operations, transferTypes, dropListener);

	activeEditorPartListener = new IPartListener2() {

	    public void partActivated(IWorkbenchPartReference partRef) {
		if (partRef.getId().compareTo(CollectionMediaClipAnnotationEditor.ID) == 0) {
		    CollectionMediaClipAnnotationEditor editor = (CollectionMediaClipAnnotationEditor) partRef.getPart(false);
		    if (currentAnnotationSetNode == null) {
			currentAnnotationSetNode = editor.getAnnotationSetNode();
			setupData();
		    } else {
			if (!isSticky) {
			    currentAnnotationSetNode = editor.getAnnotationSetNode();
			    setupData();
			}
		    }

		}
	    }

	    public void partBroughtToTop(IWorkbenchPartReference partRef) {
	    }

	    public void partClosed(IWorkbenchPartReference partRef) {
	    }

	    public void partDeactivated(IWorkbenchPartReference partRef) {
		if (partRef.getId().compareTo(CollectionMediaClipAnnotationEditor.ID) == 0) {
		    if (!isSticky) {
			clearData();
		    }
		}
	    }

	    public void partHidden(IWorkbenchPartReference partRef) {
	    }

	    public void partInputChanged(IWorkbenchPartReference partRef) {
	    }

	    public void partOpened(IWorkbenchPartReference partRef) {
	    }

	    public void partVisible(IWorkbenchPartReference partRef) {
	    }

	};

	getSite().getWorkbenchWindow().getPartService().addPartListener(activeEditorPartListener);
	getSite().setSelectionProvider(annotationSetTableViewer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
	getSite().getWorkbenchWindow().getPartService().removePartListener(activeEditorPartListener);
	clearData();
	super.dispose();
    }

    /**
     * Checks if is sticky.
     * 
     * @return true, if is sticky
     */
    public boolean isSticky() {
	return isSticky;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui. IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
	if (part instanceof CollectionMediaClipAnnotationEditor) {
	    if (selection instanceof StructuredSelection) {
		StructuredSelection sSelection = (StructuredSelection) selection;
		sSelection.iterator();
		if ((sSelection.getFirstElement() != null) && (sSelection.getFirstElement() instanceof AnnotationIntervalImpl)) {
		    AnnotationIntervalImpl interval = (AnnotationIntervalImpl) sSelection.getFirstElement();
		    StructuredSelection selfSelection = (StructuredSelection) annotationSetTableViewer.getSelection();
		    if ((selfSelection != null) && (selfSelection.getFirstElement() != null)) {
			if (!selfSelection.getFirstElement().equals(interval.getAnnotation())) {
			    IStructuredSelection newSelection = new StructuredSelection(interval.getAnnotation());
			    annotationSetTableViewer.setSelection(newSelection, true);
			}
		    } else {
			IStructuredSelection newSelection = new StructuredSelection(interval.getAnnotation());
			annotationSetTableViewer.setSelection(newSelection, true);
		    }

		}
	    }
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
	// TODO Auto-generated method stub

    }

    /**
     * Sets the sticky.
     * 
     * @param isSticky
     *            the new sticky
     */
    public void setSticky(boolean isSticky) {
	this.isSticky = isSticky;
    }

    /**
     * Setup data.
     */
    private void setupData() {
	annotationSetTableViewer.setInput(currentAnnotationSetNode);
	getSite().getPage().addSelectionListener(this);
	dropListener.setNode(currentAnnotationSetNode);

    }

}
