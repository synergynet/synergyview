package synergyviewcore.navigation.model;

import java.util.List;

import org.eclipse.core.databinding.observable.IObservable;


/**
 * The Interface IParentNode.
 */
public interface IParentNode extends INode {
	
	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	List<INode> getChildren();
	
	/**
	 * Gets the observable children.
	 *
	 * @return the observable children
	 */
	IObservable getObservableChildren();
	
	/**
	 * Delete children.
	 *
	 * @param nodeValue the node value
	 */
	void deleteChildren(INode[] nodeValue);
	
	/**
	 * Gets the children names.
	 *
	 * @return the children names
	 */
	List<String> getChildrenNames();
}
