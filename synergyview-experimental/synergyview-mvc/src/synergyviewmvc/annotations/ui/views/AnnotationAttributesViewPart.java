package synergyviewmvc.annotations.ui.views;

import java.awt.Font;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManagerFactory;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import synergyviewmvc.annotations.model.Annotation;
import synergyviewmvc.attributes.model.Attribute;

public class AnnotationAttributesViewPart extends ViewPart implements ISelectionListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "uk.ac.durham.tel.synergynet.ats.annotations.ui.views.AnnotationAttributesViewPart";

	private Annotation _annotation;
	private EntityManagerFactory _emFactory;
	private ChartComposite _chartComposite;
	/**
	 * The constructor.
	 */
	public AnnotationAttributesViewPart() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		this.getSite().getPage().addSelectionListener(this);
		_chartComposite = new ChartComposite(parent, SWT.NONE);
	}
	
	private JFreeChart createChart(PieDataset dataset) {

		JFreeChart chart = ChartFactory.createPieChart("", // chart
				// title
				dataset, // data
				false, // include legend
				true, false);
		
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionOutlinesVisible(false);
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
		plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setLabelGap(0.02);
		return chart;

	}


	
	/**
	 * Creates the Dataset for the Pie chart
	 */
	private PieDataset createDataset(Map<Attribute,Integer> data) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for(Entry<Attribute,Integer> entry : data.entrySet()) {
			dataset.setValue(String.format("%s (%d)", entry.getKey().getName(), entry.getValue()), entry.getValue());
		}
		return dataset;
	}



	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		_chartComposite.setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//		final Map<Attribute,Integer> attributesToAdd = new HashMap<Attribute,Integer>();
//		if (part instanceof CollectionMediaClipAnnotationEditor  && selection instanceof IStructuredSelection) {
//			if (selection.isEmpty()) {
//				attributesToAdd.clear();
//
//			} else {
//				AnnotationSetNode annotationSetNode = ((CollectionMediaClipAnnotationEditor) part).getAnnotationSetNode();
//				_emFactory = annotationSetNode.getEMFactoryProvider().getEntityManagerFactory();
//				IStructuredSelection structSel = (IStructuredSelection) selection;
//				@SuppressWarnings("rawtypes")
//				Iterator selectionItem = structSel.iterator();
//				List<AnnotationIntervalImpl> addedList = new ArrayList<AnnotationIntervalImpl>();
//				//TODO refactor duplicated code
//				while (selectionItem.hasNext()) {
//					Object element = selectionItem.next();
//					if (element instanceof AnnotationIntervalImpl) {
//						AnnotationIntervalImpl cElement = (AnnotationIntervalImpl) element;
//						if (!addedList.contains(cElement)) {
//
//							_annotation = cElement.getAnalysisCaption();
//							for (Attribute attribute : cElement.getAnalysisCaption().getAttributes()) {
//								Integer count = attributesToAdd.get(attribute);
//								if (count == null)
//									attributesToAdd.put(attribute, 1);
//								else attributesToAdd.put(attribute, count + 1);
//							}
//							addedList.add(cElement);
//						}
//						
//						
//					}
//					if (element instanceof SubjectRowModel) {
//						SubjectRowModel cRowElement = (SubjectRowModel) element;
//						for (Interval interval : cRowElement.getIntervals()) {
//							if (interval instanceof AnnotationIntervalImpl) {
//								AnnotationIntervalImpl cIntervalElement = (AnnotationIntervalImpl) interval;
//								if (!addedList.contains(cIntervalElement)) {
//									_annotation = cIntervalElement.getAnalysisCaption();
//									for (Attribute attribute : cIntervalElement.getAnalysisCaption().getAttributes()) {
//										Integer count = attributesToAdd.get(attribute);
//										if (count == null)
//											attributesToAdd.put(attribute, 1);
//										else attributesToAdd.put(attribute, count + 1);
//									}
//									addedList.add(cIntervalElement);
//								}
//							}
//						}
//
//					}
//				}
//			}
//			JFreeChart chart = createChart(createDataset(attributesToAdd));
//			_chartComposite.setChart(chart);
//			_chartComposite.redraw();
//			_chartComposite.layout();
//			
//		} 
	}
}