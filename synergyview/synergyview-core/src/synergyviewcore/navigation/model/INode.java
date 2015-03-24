package synergyviewcore.navigation.model;

import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcore.navigation.projects.model.IEMFactoryProvider;

/**
 * The Interface INode.
 */
public interface INode {
	
	/**
	 * Dispose.
	 * 
	 * @throws DisposeException
	 *             the dispose exception
	 */
	void dispose() throws DisposeException;
	
	/**
	 * Gets the EM factory provider.
	 * 
	 * @return the EM factory provider
	 */
	IEMFactoryProvider getEMFactoryProvider();
	
	/**
	 * Gets the icon.
	 * 
	 * @return the icon
	 */
	ImageDescriptor getIcon();
	
	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	String getLabel();
	
	/**
	 * Gets the last parent.
	 * 
	 * @return the last parent
	 */
	IParentNode getLastParent();
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	IParentNode getParent();
	
	/**
	 * Gets the resource.
	 * 
	 * @return the resource
	 */
	Object getResource();
	
	/**
	 * Gets the root.
	 * 
	 * @return the root
	 */
	IParentNode getRoot();
	
	/**
	 * Gets the viewer provider.
	 * 
	 * @return the viewer provider
	 */
	IViewerProvider getViewerProvider();
	
	/**
	 * Sets the label.
	 * 
	 * @param labelValue
	 *            the new label
	 */
	void setLabel(String labelValue);
	
	/**
	 * Sets the parent.
	 * 
	 * @param parentValue
	 *            the new parent
	 */
	void setParent(IParentNode parentValue);
}
