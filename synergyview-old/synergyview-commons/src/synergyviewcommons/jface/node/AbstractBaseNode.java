package synergyviewcommons.jface.node;

import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcommons.model.PropertySupportObject;

public abstract class AbstractBaseNode<R> extends PropertySupportObject implements INode {

	private String label;
	protected R resource;
	private IParentNode parent;
	public AbstractBaseNode(R resourceValue, IParentNode parentValue) {
		this.resource = resourceValue;
		setParent(parentValue);
	}

	public IParentNode getParent() {
		return parent;
	}

	protected AbstractBaseNode() {
		//
	}

	public IParentNode getRoot() {
		INode node = this;
		while (!(node.getParent() == null)) {
			node = node.getParent();
		}
		return (IParentNode) node;
	}


	protected IParentNode getLastParent() {
		INode node = this;
		if (node.getParent().getParent() != null) { //If not last parent
			while (!(node.getParent().getParent() == null)) {
				node = node.getParent();
			}
			return (IParentNode) node;
		} else return null;
	}

	public void setParent(IParentNode parentValue) {
		this.firePropertyChange("parent", parent, this.parent = parentValue);
	}

	public abstract ImageDescriptor getIcon();

	public R getResource() {
		return resource;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String labelValue) {
		firePropertyChange("label", this.label, this.label = labelValue);
	}

}
