package synergyviewmvc.navigation;


import java.util.Set;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.databinding.observable.map.MapDiff;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;

import uk.ac.durham.tel.commons.jface.node.INode;

public  class NavigatorLabelProvider extends LabelProvider {

	private IObservableMap[] attributeMaps = null;
	
	private LocalResourceManager resourceManager = new LocalResourceManager(
			JFaceResources.getResources());
	
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

	public NavigatorLabelProvider() {
		//
	}
	public NavigatorLabelProvider(IObservableSet knownElements) {
		setObserable(Properties.observeEach(knownElements, BeanProperties.values(new String[] { "label" })));
	}
	
	public void setObserable(IObservableMap[] attributeMaps) {
		         this.attributeMaps = attributeMaps;
		         for (int i = 0; i < attributeMaps.length; i++) {
		            attributeMaps[i].addMapChangeListener(mapChangeListener);
		         }
		     }

	
	public String getText(Object element) {
		if (element instanceof INode) {
			return ((INode) element).getLabel();
		}
		return null;
	}
	
	
	public Image getImage(Object element) {
		if (element instanceof INode) {
			return (Image) resourceManager.get(((INode) element).getIcon());
		}
		return null;
	}
	
	
	public void dispose() {
		if (attributeMaps!=null) {
			for (int i = 0; i < attributeMaps.length; i++) {
		           attributeMaps[i].removeMapChangeListener(mapChangeListener);
		      }
		}
		resourceManager.dispose();
		super.dispose();
		
	}
}
