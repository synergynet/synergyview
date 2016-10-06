package synergyviewcore.navigation.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import synergyviewcore.Activator;

/**
 * The Class AbstractParent.
 * 
 * @param <R>
 *            the generic type
 */
public abstract class AbstractParent<R> extends AbstractNode<R> implements
		IParentNode {
	
	/** The Constant CHILDREN. */
	public static final String CHILDREN = "children";
	
	/** The _children. */
	protected List<INode> _children = new ArrayList<INode>();
	
	/**
	 * Instantiates a new abstract parent.
	 * 
	 * @param resourceValue
	 *            the resource value
	 * @param parentValue
	 *            the parent value
	 */
	public AbstractParent(R resourceValue, IParentNode parentValue) {
		super(resourceValue, parentValue);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * synergyviewcore.navigation.model.IParentNode#deleteChildren(synergyviewcore
	 * .navigation.model.INode[])
	 */
	public void deleteChildren(INode[] nodeValue) {
		for (INode childValue : nodeValue) {
			if (_children.contains(childValue)) {
				try {
					childValue.dispose();
					_children.remove(childValue);
				} catch (DisposeException ex) {
					IStatus status = new Status(IStatus.WARNING,
							Activator.PLUGIN_ID, ex.getMessage(), ex);
					logger.log(status);
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
	 * @see synergyviewcore.navigation.model.IParentNode#getChildren()
	 */
	public List<INode> getChildren() {
		return _children;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.navigation.model.IParentNode#getObservableChildren()
	 */
	public IObservable getObservableChildren() {
		return BeanProperties.list(CHILDREN).observe(this);
	}
	
}
