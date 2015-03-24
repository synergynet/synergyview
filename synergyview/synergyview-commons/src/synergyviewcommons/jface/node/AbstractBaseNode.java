package synergyviewcommons.jface.node;

import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcommons.model.PropertySupportObject;

/**
 * The Class AbstractBaseNode.
 *
 * @param <R> the generic type
 */
public abstract class AbstractBaseNode<R> extends PropertySupportObject implements INode {
	
	/** The Constant PROP_LABEL. */
	public static final String PROP_LABEL = "label";
	
	/** The label. */
	private String label;
	
	/** The Constant PROP_LABEL_DECORATOR. */
	public static final String PROP_LABEL_DECORATOR = "labelDecorator";
	
	/** The label decorator. */
	private String labelDecorator;
	
	/** The Constant PROP_ICON_DECORATOR. */
	public static final String PROP_ICON_DECORATOR = "iconDecorator";
	
	/** The icon decorator. */
	private String iconDecorator;
	
	/** The resource. */
	protected R resource;
	
	/** The Constant PROP_PARENT. */
	public static final String PROP_PARENT = "parent";
	
	/** The parent. */
	private IParentNode parent;
	
	/**
	 * Instantiates a new abstract base node.
	 *
	 * @param resourceValue the resource value
	 * @param parentValue the parent value
	 */
	public AbstractBaseNode(R resourceValue, IParentNode parentValue) {
		this.resource = resourceValue;
		setParent(parentValue);
	}

	/* (non-Javadoc)
	 * @see synergyviewcommons.jface.node.INode#getParent()
	 */
	public IParentNode getParent() {
		return parent;
	}

	/**
	 * Instantiates a new abstract base node.
	 */
	protected AbstractBaseNode() {
		//
	}

	/* (non-Javadoc)
	 * @see synergyviewcommons.jface.node.INode#getRoot()
	 */
	public IParentNode getRoot() {
		INode node = this;
		while (!(node.getParent() == null)) {
			node = node.getParent();
		}
		return (IParentNode) node;
	}


	/**
	 * Gets the last parent.
	 *
	 * @return the last parent
	 */
	protected IParentNode getLastParent() {
		INode node = this;
		if (node.getParent().getParent() != null) { //If not last parent
			while (!(node.getParent().getParent() == null)) {
				node = node.getParent();
			}
			return (IParentNode) node;
		} else return null;
	}

	/**
	 * Sets the parent.
	 *
	 * @param parentValue the new parent
	 */
	public void setParent(IParentNode parentValue) {
		this.firePropertyChange(PROP_PARENT, parent, this.parent = parentValue);
	}

	/* (non-Javadoc)
	 * @see synergyviewcommons.jface.node.INode#getIcon()
	 */
	public abstract ImageDescriptor getIcon();

	/* (non-Javadoc)
	 * @see synergyviewcommons.jface.node.INode#getResource()
	 */
	public R getResource() {
		return resource;
	}

	/* (non-Javadoc)
	 * @see synergyviewcommons.jface.node.INode#getLabel()
	 */
	public String getLabel() {
		if (label!=null)
			return label;
		else return "Unknown";
	}

	/**
	 * Sets the label.
	 *
	 * @param labelValue the new label
	 */
	public void setLabel(String labelValue) {
		firePropertyChange(PROP_LABEL, this.label, this.label = labelValue);
	}

	/**
	 * Sets the label decorator.
	 *
	 * @param labelDecorator the new label decorator
	 */
	public void setLabelDecorator(String labelDecorator) {
		firePropertyChange(PROP_LABEL_DECORATOR, this.labelDecorator, this.labelDecorator = labelDecorator);
	}

	/* (non-Javadoc)
	 * @see synergyviewcommons.jface.node.INode#getLabelDecorator()
	 */
	public String getLabelDecorator() {
		return labelDecorator;
	}
	
	/**
	 * Sets the icon decorator.
	 *
	 * @param iconDecorator the new icon decorator
	 */
	public void setIconDecorator(String iconDecorator) {
		firePropertyChange(PROP_ICON_DECORATOR, this.iconDecorator, this.iconDecorator = iconDecorator);
	}

	/* (non-Javadoc)
	 * @see synergyviewcommons.jface.node.INode#getIconDecorator()
	 */
	public String getIconDecorator() {
		return iconDecorator;
	}

}
