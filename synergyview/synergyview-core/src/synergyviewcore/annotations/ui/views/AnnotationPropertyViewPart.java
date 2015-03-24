package synergyviewcore.annotations.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import synergyviewcommons.collections.CollectionChangeEvent;
import synergyviewcommons.collections.CollectionChangeListener;
import synergyviewcommons.collections.CollectionDiffEntry;
import synergyviewcore.annotations.model.Annotation;
import synergyviewcore.annotations.model.AnnotationAttributeController;
import synergyviewcore.annotations.ui.AnnotationIntervalImpl;
import synergyviewcore.annotations.ui.editors.CollectionMediaClipAnnotationEditor;
import synergyviewcore.attributes.model.Attribute;
import synergyviewcore.attributes.model.AttributeNode;
import synergyviewcore.attributes.model.ProjectAttributeRootNode;
import synergyviewcore.projects.model.ProjectNode;
import synergyviewcore.resource.ResourceLoader;

/**
 * The Class AnnotationPropertyViewPart.
 */
public class AnnotationPropertyViewPart extends ViewPart implements
		ISelectionListener, CollectionChangeListener {
	
	/**
	 * The Class AttributeColorLabelProvider.
	 */
	private static class AttributeColorLabelProvider extends
			StyledCellLabelProvider {
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse
		 * .jface.viewers.ViewerCell)
		 */
		@Override
		public void update(ViewerCell cell) {
			if (cell.getElement() instanceof Attribute) {
				Attribute attribute = (Attribute) cell.getElement();
				String[] rgb = attribute.getColorName().split(",");
				if (attribute.getChildren().isEmpty()) {
					cell.setBackground(new Color(Display.getDefault(), Integer
							.parseInt(rgb[0]), Integer.parseInt(rgb[1]),
							Integer.parseInt(rgb[2])));
				} else {
					cell.setBackground(null);
				}
			}
			
			super.update(cell);
		}
	}
	
	/**
	 * The Class AttributesContentProvider.
	 */
	class AttributesContentProvider implements IStructuredContentProvider {
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
			// TODO Auto-generated method stub
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			@SuppressWarnings("unchecked")
			List<Attribute> attributes = (List<Attribute>) inputElement;
			return attributes.toArray();
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}
	}
	
	/**
	 * The Class AttributeTextLabelProvider.
	 */
	private static class AttributeTextLabelProvider extends
			StyledCellLabelProvider {
		
		/** The resource manager. */
		private LocalResourceManager resourceManager = new LocalResourceManager(
				JFaceResources.getResources());
		
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
		 * @see
		 * org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse
		 * .jface.viewers.ViewerCell)
		 */
		@Override
		public void update(ViewerCell cell) {
			if (cell.getElement() instanceof Attribute) {
				Attribute attribute = (Attribute) cell.getElement();
				cell.setText(attribute.getName());
				cell.setImage((Image) resourceManager.get(ResourceLoader
						.getIconDescriptor(AttributeNode.ATTRIBUTE_ICON)));
			}
			
			super.update(cell);
		}
	}
	
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "synergyviewcore.annotations.ui.views.AnnotationPropertyViewPart";
	
	/** The annotation. */
	private Annotation annotation;
	
	/** The annotation text. */
	private Text annotationText;
	
	/** The attribute list. */
	private List<Attribute> attributeList = new ArrayList<Attribute>();
	
	/** The attribute property change listener. */
	private PropertyChangeListener attributePropertyChangeListener;
	
	/** The attribute table viewer. */
	private TableViewer attributeTableViewer;
	
	/** The composite. */
	private Composite composite;
	
	/** The controller. */
	private AnnotationAttributeController controller;
	
	/** The label grid data. */
	private GridData labelGridData;
	/** The project attribute root node. */
	private ProjectAttributeRootNode projectAttributeRootNode;
	
	/** The viewer grid data. */
	private GridData viewerGridData;
	
	/**
	 * The constructor.
	 */
	public AnnotationPropertyViewPart() {
		//
	}
	
	/**
	 * Clear data.
	 */
	private void clearData() {
		if (!annotationText.isDisposed()) {
			annotationText.setText("");
		}
		if (controller != null) {
			controller.removeAttributeListChangeListener(this);
		}
		for (Attribute attribute : attributeList) {
			attribute
					.removePropertyChangeListener(attributePropertyChangeListener);
		}
		attributeList.clear();
		if (!attributeTableViewer.getControl().isDisposed()
				&& (attributeTableViewer.getContentProvider() != null)
				&& (attributeTableViewer.getInput() != null)) {
			attributeTableViewer.setInput(null);
		}
		controller = null;
		
	}
	
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * 
	 * @param parent
	 *            the parent
	 */
	public void createPartControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));
		labelGridData = new GridData();
		labelGridData.horizontalAlignment = SWT.FILL;
		labelGridData.heightHint = 40;
		labelGridData.grabExcessHorizontalSpace = true;
		annotationText = new Text(composite, SWT.WRAP | SWT.MULTI | SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
		annotationText.setBackground(new Color(Display.getDefault(), 0, 0, 0));
		annotationText.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_BLACK));
		annotationText.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_YELLOW));
		annotationText.setLayoutData(labelGridData);
		annotationText.setText("");
		
		viewerGridData = new GridData();
		viewerGridData.horizontalAlignment = SWT.FILL;
		viewerGridData.grabExcessHorizontalSpace = true;
		viewerGridData.verticalAlignment = SWT.FILL;
		viewerGridData.grabExcessVerticalSpace = true;
		attributeTableViewer = new TableViewer(composite, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		attributeTableViewer.getControl().setLayoutData(viewerGridData);
		TableViewerColumn c = new TableViewerColumn(attributeTableViewer,
				SWT.NONE);
		c.getColumn().setText("Name");
		c.setLabelProvider(new AttributeTextLabelProvider());
		c.getColumn().setWidth(200);
		TableViewerColumn c2 = new TableViewerColumn(attributeTableViewer,
				SWT.NONE);
		c2.setLabelProvider(new AttributeColorLabelProvider());
		c2.getColumn().setText("Color");
		c2.getColumn().setWidth(60);
		attributeTableViewer.getTable().setLinesVisible(true);
		attributeTableViewer.getTable().setHeaderVisible(true);
		attributeTableViewer
				.setContentProvider(new AttributesContentProvider());
		this.getSite().getPage().addSelectionListener(this);
		getSite().setSelectionProvider(attributeTableViewer);
		attributeTableViewer.setInput(attributeList);
		attributePropertyChangeListener = new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getSource() instanceof Attribute) {
					AnnotationPropertyViewPart.this.attributeTableViewer
							.refresh(evt.getSource());
				}
			}
			
		};
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		clearData();
		this.getSite().getPage().removeSelectionListener(this);
		super.dispose();
	}
	
	public void listChanged(CollectionChangeEvent event) {
		
		for (CollectionDiffEntry<?> entry : event.getListDiff()
				.getDifferences()) {
			Attribute attribute = (Attribute) entry.getElement();
			if (entry.isAddition()) {
				attributeList.add(attribute);
				attribute
						.addPropertyChangeListener(attributePropertyChangeListener);
			} else {
				attributeList.remove(attribute);
				attribute
						.removePropertyChangeListener(attributePropertyChangeListener);
			}
		}
		attributeTableViewer.refresh();
	}
	
	/**
	 * Removes the selected attributes.
	 */
	public void removeSelectedAttributes() {
		IStructuredSelection structSel = (IStructuredSelection) attributeTableViewer
				.getSelection();
		List<Attribute> listToRemove = new ArrayList<Attribute>();
		Iterator<?> iteratorAttributes = structSel.iterator();
		while (iteratorAttributes.hasNext()) {
			Object element = iteratorAttributes.next();
			if (element instanceof Attribute) {
				Attribute cElement = (Attribute) element;
				listToRemove.add(cElement);
			}
		}
		controller.removeAttributeList(listToRemove);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcommons.collections.ListChangeListener#listChanged(
	 * synergyviewcommons.collections.ListChangeEvent)
	 */
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.
	 * IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if ((part instanceof CollectionMediaClipAnnotationEditor)
				&& (selection instanceof IStructuredSelection)) {
			if (selection.isEmpty()
					|| (!(((IStructuredSelection) selection).getFirstElement() instanceof AnnotationIntervalImpl))) {
				clearData();
				return;
			}
			AnnotationIntervalImpl selectedInterval = (AnnotationIntervalImpl) ((IStructuredSelection) selection)
					.getFirstElement();
			annotation = selectedInterval.getAnnotation();
			projectAttributeRootNode = ((ProjectNode) selectedInterval
					.getOwner().getAnnotationSetNode().getLastParent())
					.getProjectAttributeRootNode();
			AnnotationAttributeController selectedController = selectedInterval
					.getOwner().getAnnotationSetNode()
					.getAnnotationAttributeController(annotation);
			annotationText.setText(annotation.getText());
			if ((controller != null) && (controller != selectedController)) {
				clearData();
				updateData(selectedController);
			} else if (controller == null) {
				updateData(selectedController);
			}
			
		}
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		attributeTableViewer.getControl().setFocus();
	}
	
	/**
	 * Update data.
	 * 
	 * @param selectedController
	 *            the selected controller
	 */
	private void updateData(AnnotationAttributeController selectedController) {
		controller = selectedController;
		controller.addAttributeListChangeListener(this);
		attributeTableViewer.setInput(controller.getAttributeList());
		// Getting different set of attributes because controller attribute list
		// is different
		// TODO fix!!!
		for (Attribute attribute : controller.getAttributeList()) {
			if (projectAttributeRootNode.getAttribute(attribute) != null) {
				Attribute newAttribute = projectAttributeRootNode
						.getAttribute(attribute);
				newAttribute
						.addPropertyChangeListener(attributePropertyChangeListener);
			}
		}
	}
	
}