package synergyviewcommons.jface.node;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.IObservable;

public abstract class AbstractBaseParent<R> extends AbstractBaseNode<R> implements
		IParentNode {
	protected List<INode> children = new ArrayList<INode>();
	public static final String CHILDREN = "children";
	
	public AbstractBaseParent(R resourceValue, IParentNode parentValue) {
		super(resourceValue, parentValue);
	}
	
	public IObservable getObservableChildren() {
		return BeanProperties.list(CHILDREN).observe(this);
	}

	protected void fireChildrenChanged() {
		firePropertyChange(CHILDREN, null, null);
	}
	
	public void deleteChildren(INode[] nodeValue) throws NodeRemoveException {
		for(INode childValue : nodeValue) {
			if (children.contains(childValue)) {
				try {
					childValue.dispose();
					children.remove(childValue);
				} catch (DisposeException e) {
					e.printStackTrace();
					throw new NodeRemoveException("Unable to dispose children!", e);
				}
				
			}
		}
		fireChildrenChanged();
	}
	
	public List<INode> getChildren() {
		return children;
	}

}
