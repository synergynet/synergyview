package synergyviewcommons.jface.node;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.IObservable;

/**
 * The Class AbstractBaseParent.
 * 
 * @param <R>
 *            the generic type
 */
public abstract class AbstractBaseParent<R> extends AbstractBaseNode<R>
		implements IParentNode {
	
	/** The Constant CHILDREN. */
	public static final String CHILDREN = "children";
	
	/** The children. */
	protected List<INode> children = new ArrayList<INode>();
	
	/**
	 * Instantiates a new abstract base parent.
	 * 
	 * @param resourceValue
	 *            the resource value
	 * @param parentValue
	 *            the parent value
	 */
	public AbstractBaseParent(R resourceValue, IParentNode parentValue) {
		super(resourceValue, parentValue);
	}
	
	/**
	 * Adds the children.
	 * 
	 * @param childNode
	 *            the child node
	 */
	public void addChildren(INode childNode) {
		children.add(childNode);
		fireChildrenChanged();
	}
	
	/**
	 * Clear children.
	 * 
	 * @throws NodeRemoveException
	 *             the node remove exception
	 */
	public void clearChildren() throws NodeRemoveException {
		deleteChildren(children.toArray(new INode[0]));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * synergyviewcommons.jface.node.IParentNode#deleteChildren(synergyviewcommons
	 * .jface.node.INode[])
	 */
	public void deleteChildren(INode[] nodeValue) throws NodeRemoveException {
		List<INode> disposedNodesList = new ArrayList<INode>();
		for (INode childValue : nodeValue) {
			if (children.contains(childValue)) {
				try {
					childValue.dispose();
					disposedNodesList.add(childValue);
				} catch (DisposeException e) {
					e.printStackTrace();
					throw new NodeRemoveException(
							"Unable to dispose children!", e);
				} finally {
					children.removeAll(disposedNodesList);
				}
			}
		}
		fireChildrenChanged();
	}
	
	/**
	 * Fire children changed.
	 */
	protected void fireChildrenChanged() {
		firePropertyChange(CHILDREN, null, null);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcommons.jface.node.IParentNode#getChildren()
	 */
	public List<INode> getChildren() {
		return children;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcommons.jface.node.IParentNode#getObservableChildren()
	 */
	public IObservable getObservableChildren() {
		return BeanProperties.list(CHILDREN).observe(this);
	}
	
}
