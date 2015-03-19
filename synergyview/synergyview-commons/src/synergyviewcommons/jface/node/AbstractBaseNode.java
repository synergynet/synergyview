package synergyviewcommons.jface.node;

import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcommons.model.PropertySupportObject;

public abstract class AbstractBaseNode<R> extends PropertySupportObject implements INode {
	
	public static final String PROP_LABEL = "label";
	private String label;
	public static final String PROP_LABEL_DECORATOR = "labelDecorator";
	private String labelDecorator;
	public static final String PROP_ICON_DECORATOR = "iconDecorator";
	private String iconDecorator;
	
	protected R resource;
	public static final String PROP_PARENT = "parent";
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
		this.firePropertyChange(PROP_PARENT, parent, this.parent = parentValue);
	}

	public abstract ImageDescriptor getIcon();

	public R getResource() {
		return resource;
	}

	public String getLabel() {
		if (label!=null)
			return label;
		else return "Unknown";
	}

	public void setLabel(String labelValue) {
		firePropertyChange(PROP_LABEL, this.label, this.label = labelValue);
	}

	public void setLabelDecorator(String labelDecorator) {
		firePropertyChange(PROP_LABEL_DECORATOR, this.labelDecorator, this.labelDecorator = labelDecorator);
	}

	public String getLabelDecorator() {
		return labelDecorator;
	}
	
	public void setIconDecorator(String iconDecorator) {
		firePropertyChange(PROP_ICON_DECORATOR, this.iconDecorator, this.iconDecorator = iconDecorator);
	}

	public String getIconDecorator() {
		return iconDecorator;
	}

}
