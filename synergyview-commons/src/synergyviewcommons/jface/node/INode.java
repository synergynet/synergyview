package synergyviewcommons.jface.node;

import org.eclipse.jface.resource.ImageDescriptor;

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
	 * Gets the icon.
	 * 
	 * @return the icon
	 */
	ImageDescriptor getIcon();
	
	/**
	 * Gets the icon decorator.
	 * 
	 * @return the icon decorator
	 */
	String getIconDecorator();
	
	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	String getLabel();
	
	/**
	 * Gets the label decorator.
	 * 
	 * @return the label decorator
	 */
	String getLabelDecorator();
	
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
}
