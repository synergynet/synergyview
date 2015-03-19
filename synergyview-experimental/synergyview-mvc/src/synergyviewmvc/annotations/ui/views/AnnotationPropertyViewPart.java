package synergyviewmvc.annotations.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import synergyviewmvc.annotations.ui.AnnotationIntervalImpl;
import synergyviewmvc.attributes.model.Attribute;



public class AnnotationPropertyViewPart extends ViewPart implements ISelectionListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "uk.ac.durham.tel.synergynet.ats.annotations.ui.views.AnnotationPropertyViewPart";

	private TableViewer _viewer;
	private EntityManagerFactory _emFactory;
	private AnnotationIntervalImpl _captionIntervalImpl;
	/**
	 * The constructor.
	 */
	public AnnotationPropertyViewPart() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		_viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		createColumns(_viewer);
		_viewer.setContentProvider(new AttributesContentProvider());
		_viewer.setLabelProvider(new AttributesLabelProvider());

		this.getSite().getPage().addSelectionListener(this);
		getSite().setSelectionProvider(_viewer);
	}

	class AttributesContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			@SuppressWarnings("unchecked")
			List<Attribute> attributes = (List<Attribute>) inputElement;
			return attributes.toArray();
		}


		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub

		}


		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
			// TODO Auto-generated method stub

		}

	}

	class AttributesLabelProvider extends LabelProvider implements ITableLabelProvider {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			Attribute attribute = (Attribute) element;
			switch (columnIndex) {
			case 0:
				return attribute.getName();
			case 1:
				return attribute.getDescription();
			default:
				throw new RuntimeException("Should not happen");
			}

		}
	}



	// This will create the columns for the table
	private void createColumns(TableViewer viewer) {

		String[] titles = { "Name", "Description" };
		int[] bounds = { 100, 200};

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
		}
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		_viewer.getControl().setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//		if (part instanceof CollectionMediaClipAnnotationEditor  && selection instanceof IStructuredSelection) {
//			if (selection.isEmpty() || (!(selection instanceof IStructuredSelection))) {
//				_viewer.setInput(null);
//				return;
//			}
//			IStructuredSelection structSel = (IStructuredSelection) selection;
//			if (structSel.size() != 1) {
//				_viewer.setInput(null);
//				return;
//			}
//
//
//			AnnotationSetNode annotationSetNode = ((CollectionMediaClipAnnotationEditor) part).getAnnotationSetNode();
//			_emFactory = annotationSetNode.getEMFactoryProvider().getEntityManagerFactory();
//
//			@SuppressWarnings("rawtypes")
//			Iterator iteratorCaptionInterval = structSel.iterator();
//			
//			Object element = iteratorCaptionInterval.next();
//			if (element instanceof AnnotationIntervalImpl) {
//				_captionIntervalImpl = (AnnotationIntervalImpl) element;
//				
//			}
//			updateViewer();
//		} 
	}

	private void updateViewer() {
		List<Attribute> attributesToAdd = new ArrayList<Attribute>();
		if (_captionIntervalImpl!=null)
		for (Attribute attribute : _captionIntervalImpl.getAnalysisCaption().getAttributes()) {
			if (!attributesToAdd.contains(attribute))
				attributesToAdd.add(attribute);
		}
		_viewer.setInput(attributesToAdd);
	}

	public void removeSelectedAttributes() {
		IStructuredSelection structSel = (IStructuredSelection) _viewer.getSelection();
		@SuppressWarnings("rawtypes")
		Iterator iteratorAttributes = structSel.iterator();
		EntityManager entityManager = _emFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			while (iteratorAttributes.hasNext()) {
				Object element = iteratorAttributes.next();
				if (element instanceof Attribute) {
					Attribute cElement = (Attribute) element;
					if ( _captionIntervalImpl.getAnalysisCaption().getAttributes().contains(cElement))
					{
						_captionIntervalImpl.getAnalysisCaption().getAttributes().remove(cElement);
					}
				}
			}
			entityManager.merge(_captionIntervalImpl.getAnalysisCaption());
			entityManager.getTransaction().commit();
			updateViewer();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}

	}


}