package synergyviewcore.attributes.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.eclipse.persistence.annotations.PrivateOwned;

import synergyviewcore.model.PersistenceModelObject;

/**
 * The Class Attribute.
 */
@Entity
public class Attribute extends PersistenceModelObject {
	
	/** The Constant PROP_CHILDREN. */
	public static final String PROP_CHILDREN = "children";
	
	/** The Constant PROP_COLOR. */
	public static final String PROP_COLOR = "colorName";
	
	/** The Constant PROP_DESCRIPTION. */
	public static final String PROP_DESCRIPTION = "description";
	
	/** The Constant PROP_NAME. */
	public static final String PROP_NAME = "name";
	
	/** The Constant PROP_PARENT. */
	public static final String PROP_PARENT = "parent";
	
	/** The children. */
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@PrivateOwned
	private List<Attribute> children = new ArrayList<Attribute>();
	
	/** The color name. */
	private String colorName;
	
	/** The description. */
	private String description;
	
	/** The name. */
	private String name;
	
	/** The parent. */
	@ManyToOne
	private Attribute parent;
	
	/**
	 * Gets the children.
	 * 
	 * @return the children
	 */
	public List<Attribute> getChildren() {
		return children;
	}
	
	/**
	 * Gets the color name.
	 * 
	 * @return the color name
	 */
	public String getColorName() {
		return colorName;
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	public Attribute getParent() {
		return parent;
	}
	
	/**
	 * Sets the children.
	 * 
	 * @param children
	 *            the new children
	 */
	public void setChildren(List<Attribute> children) {
		this.children = children;
	}
	
	/**
	 * Sets the color name.
	 * 
	 * @param colorName
	 *            the new color name
	 */
	public void setColorName(String colorName) {
		this.firePropertyChange(PROP_COLOR, this.colorName,
				this.colorName = colorName);
	}
	
	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            the new description
	 */
	public void setDescription(String description) {
		this.firePropertyChange(PROP_DESCRIPTION, this.description,
				this.description = description);
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.firePropertyChange(PROP_NAME, this.name, this.name = name);
	}
	
	/**
	 * Sets the parent.
	 * 
	 * @param parent
	 *            the new parent
	 */
	public void setParent(Attribute parent) {
		this.parent = parent;
	}
	
}
