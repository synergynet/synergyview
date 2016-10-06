package synergyviewmvc.navigation.model;

import java.util.HashMap;
import java.util.Map;

import synergyviewmvc.navigation.projects.model.IEMFactoryProvider;
import synergyviewmvc.navigation.projects.model.IProjectPathsProvider;
import uk.ac.durham.tel.commons.jface.node.AbstractBaseParent;
import uk.ac.durham.tel.commons.jface.node.INode;
import uk.ac.durham.tel.commons.jface.node.IParentNode;
import uk.ac.durham.tel.commons.jface.node.IViewerProvider;
import uk.ac.durham.tel.commons.model.PropertySupportObject;

public abstract class AbstractParent<R> extends AbstractBaseParent<R> {
	
	protected Map<PropertySupportObject, INode> nodeMapping = new HashMap<PropertySupportObject, INode>();
	
	/**
	 * @param resourceValue
	 * @param parentValue
	 */
	public AbstractParent(R resourceValue, IParentNode parentValue) {
		super(resourceValue, parentValue);
		
	}
	
	protected IEMFactoryProvider getEMFactoryProvider() {
		return (IEMFactoryProvider) this.getLastParent();
	}
	
	public IProjectPathsProvider getProjectPathProvider() {
		return (IProjectPathsProvider) this.getLastParent();
	}
	
	protected IViewerProvider getViewerProvider() {
		return (IViewerProvider) this.getRoot();
	}
}
