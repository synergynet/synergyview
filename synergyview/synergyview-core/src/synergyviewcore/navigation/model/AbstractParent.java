package synergyviewcore.navigation.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import synergyviewcore.Activator;

public abstract class AbstractParent<R> extends AbstractNode<R> implements
		IParentNode {
	protected List<INode> _children = new ArrayList<INode>();
	
	public AbstractParent(R resourceValue, IParentNode parentValue) {
		super(resourceValue, parentValue);
	}

	public static final String CHILDREN = "children";
	
	public IObservable getObservableChildren() {
		return BeanProperties.list(CHILDREN).observe(this);
	}

	protected void fireChildrenChanged() {
		firePropertyChange(CHILDREN, null, null);
	}
	
	public void deleteChildren(INode[] nodeValue) {
		for(INode childValue : nodeValue) {
			if (_children.contains(childValue)) {
				try {
					childValue.dispose();
					_children.remove(childValue);
				} catch (DisposeException ex) {
					IStatus status = new Status(IStatus.WARNING,Activator.PLUGIN_ID,ex.getMessage(), ex);
					logger.log(status);
				}
				
			}
		}
		fireChildrenChanged();
	}
	
	public List<INode> getChildren() {
		return _children;
	}

}
