package synergyviewcore.navigation.model;

import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcore.navigation.projects.model.IEMFactoryProvider;


/**
 * The Interface INode.
 */
public interface INode {
	
	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	String getLabel();
	
	/**
	 * Sets the label.
	 *
	 * @param labelValue the new label
	 */
	void setLabel(String labelValue);
	
	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	IParentNode getParent();
	
	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	IParentNode getRoot();
	
	/**
	 * Gets the last parent.
	 *
	 * @return the last parent
	 */
	IParentNode getLastParent();
	
	/**
	 * Gets the viewer provider.
	 *
	 * @return the viewer provider
	 */
	IViewerProvider getViewerProvider();
	
	/**
	 * Gets the EM factory provider.
	 *
	 * @return the EM factory provider
	 */
	IEMFactoryProvider getEMFactoryProvider();
	
	/**
	 * Sets the parent.
	 *
	 * @param parentValue the new parent
	 */
	void setParent(IParentNode parentValue);
	
	/**
	 * Gets the icon.
	 *
	 * @return the icon
	 */
	ImageDescriptor getIcon();
	
	/**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	Object getResource();
	
	/**
	 * Dispose.
	 *
	 * @throws DisposeException the dispose exception
	 */
	void dispose() throws DisposeException;
}
