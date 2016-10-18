package synergyviewcommons.jface.node;

import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcommons.model.PropertySupportObject;

/**
 * The Class AbstractBaseNode.
 * 
 * @param <R>
 *            the generic type
 */
public abstract class AbstractBaseNode<R> extends PropertySupportObject implements INode {

    /** The Constant PROP_ICON_DECORATOR. */
    public static final String PROP_ICON_DECORATOR = "iconDecorator";

    /** The Constant PROP_LABEL. */
    public static final String PROP_LABEL = "label";

    /** The Constant PROP_LABEL_DECORATOR. */
    public static final String PROP_LABEL_DECORATOR = "labelDecorator";

    /** The Constant PROP_PARENT. */
    public static final String PROP_PARENT = "parent";

    /** The icon decorator. */
    private String iconDecorator;

    /** The label. */
    private String label;

    /** The label decorator. */
    private String labelDecorator;

    /** The parent. */
    private IParentNode parent;

    /** The resource. */
    protected R resource;

    /**
     * Instantiates a new abstract base node.
     */
    protected AbstractBaseNode() {
	//
    }

    /**
     * Instantiates a new abstract base node.
     * 
     * @param resourceValue
     *            the resource value
     * @param parentValue
     *            the parent value
     */
    public AbstractBaseNode(R resourceValue, IParentNode parentValue) {
	this.resource = resourceValue;
	setParent(parentValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcommons.jface.node.INode#getIcon()
     */
    public abstract ImageDescriptor getIcon();

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcommons.jface.node.INode#getIconDecorator()
     */
    public String getIconDecorator() {
	return iconDecorator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcommons.jface.node.INode#getLabel()
     */
    public String getLabel() {
	if (label != null) {
	    return label;
	} else {
	    return "Unknown";
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcommons.jface.node.INode#getLabelDecorator()
     */
    public String getLabelDecorator() {
	return labelDecorator;
    }

    /**
     * Gets the last parent.
     * 
     * @return the last parent
     */
    protected IParentNode getLastParent() {
	INode node = this;
	if (node.getParent().getParent() != null) { // If not last parent
	    while (!(node.getParent().getParent() == null)) {
		node = node.getParent();
	    }
	    return (IParentNode) node;
	} else {
	    return null;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcommons.jface.node.INode#getParent()
     */
    public IParentNode getParent() {
	return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcommons.jface.node.INode#getResource()
     */
    public R getResource() {
	return resource;
    }

    /*
     * (non-Javadoc)
     * 
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
     * Sets the icon decorator.
     * 
     * @param iconDecorator
     *            the new icon decorator
     */
    public void setIconDecorator(String iconDecorator) {
	firePropertyChange(PROP_ICON_DECORATOR, this.iconDecorator, this.iconDecorator = iconDecorator);
    }

    /**
     * Sets the label.
     * 
     * @param labelValue
     *            the new label
     */
    public void setLabel(String labelValue) {
	firePropertyChange(PROP_LABEL, this.label, this.label = labelValue);
    }

    /**
     * Sets the label decorator.
     * 
     * @param labelDecorator
     *            the new label decorator
     */
    public void setLabelDecorator(String labelDecorator) {
	firePropertyChange(PROP_LABEL_DECORATOR, this.labelDecorator, this.labelDecorator = labelDecorator);
    }

    /**
     * Sets the parent.
     * 
     * @param parentValue
     *            the new parent
     */
    public void setParent(IParentNode parentValue) {
	this.firePropertyChange(PROP_PARENT, parent, this.parent = parentValue);
    }

}
