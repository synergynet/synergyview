package synergyviewcore.navigation;


import java.util.Set;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.databinding.observable.map.MapDiff;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.collections.model.CollectionNode;
import synergyviewcore.navigation.model.INode;


/**
 * The Class NavigatorLabelProvider.
 */
public class NavigatorLabelProvider extends ColumnLabelProvider implements IStyledLabelProvider {


	/** The attribute maps. */
	private IObservableMap[] attributeMaps = null;
	
	/** The resource manager. */
	private LocalResourceManager resourceManager = new LocalResourceManager(
			JFaceResources.getResources());
	
	 /** The map change listener. */
 	private IMapChangeListener  mapChangeListener = new IMapChangeListener() {
		          @SuppressWarnings("unchecked")
				public void handleMapChange(MapChangeEvent event) {
		             Set<MapDiff> affectedElements = event.diff.getChangedKeys();
		              LabelProviderChangedEvent newEvent = new LabelProviderChangedEvent(
		            		  NavigatorLabelProvider.this, affectedElements
		                             .toArray());
		              fireLabelProviderChanged(newEvent);
		         }
		     };

	/**
	 * Instantiates a new navigator label provider.
	 */
	public NavigatorLabelProvider() {
		//
	}
	
	/**
	 * Instantiates a new navigator label provider.
	 *
	 * @param knownElements the known elements
	 */
	public NavigatorLabelProvider(IObservableSet knownElements) {
		setObserable(Properties.observeEach(knownElements, BeanProperties.values(new String[] { "label" })));
	}
	
	/**
	 * Sets the obserable.
	 *
	 * @param attributeMaps the new obserable
	 */
	public void setObserable(IObservableMap[] attributeMaps) {
		         this.attributeMaps = attributeMaps;
		         for (int i = 0; i < attributeMaps.length; i++) {
		            attributeMaps[i].addMapChangeListener(mapChangeListener);

		         }
		     }

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof INode) {
			return ((INode) element).getLabel();
		}
		return "Unknown";
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof INode) {
			return (Image) resourceManager.get(((INode) element).getIcon());
		}
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
	 */
	public void dispose() {
		if (attributeMaps!=null) {
			for (int i = 0; i < attributeMaps.length; i++) {
		           attributeMaps[i].removeMapChangeListener(mapChangeListener);
		      }
		}
		resourceManager.dispose();
		super.dispose();
		
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider#getStyledText(java.lang.Object)
	 */
	public StyledString getStyledText(Object element) {
		StyledString styledString= new StyledString();
		if (element instanceof CollectionNode) {
			styledString.append(((CollectionNode) element).getLabel());
			styledString.append(String.format(" (%s / %s)",((CollectionNode) element).getResource().getCollectionMediaClipList().size(),((CollectionNode) element).getResource().getCollectionMediaList().size()),StyledString.COUNTER_STYLER);
		} 
		else if  (element instanceof AnnotationSetNode) {
			AnnotationSetNode aElement = (AnnotationSetNode) element;
			styledString.append(aElement.getLabel());
			if (aElement.getResource().isLock())
				styledString.append(" (Completed)",StyledString.COUNTER_STYLER);
		}
		else if (element instanceof INode) {
			INode node = (INode) element;
			if (node.getLabel()!=null)
				styledString.append(((INode) element).getLabel());
		} else  
		styledString.append("Unknown");
		return styledString;
	}
}
