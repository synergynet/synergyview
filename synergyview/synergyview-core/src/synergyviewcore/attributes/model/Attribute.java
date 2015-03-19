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

@Entity
public class Attribute extends PersistenceModelObject {

	private String name;
	public static final String PROP_NAME = "name";
	
	private String description;
	public static final String PROP_DESCRIPTION = "description";
	
	private String colorName;
	public static final String PROP_COLOR = "colorName";
	
	public static final String PROP_CHILDREN = "children";
	
	@OneToMany(mappedBy="parent", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@PrivateOwned
	private List<Attribute> children = new ArrayList<Attribute>();
	
	public static final String PROP_PARENT = "parent";
	
	@ManyToOne
	private Attribute parent;

	public void setName(String name) {
		this.firePropertyChange(PROP_NAME, this.name, this.name = name);
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.firePropertyChange(PROP_DESCRIPTION, this.description, this.description = description);
	}

	public String getDescription() {
		return description;
	}

	

	public void setChildren(List<Attribute> children) {
		this.children = children;
	}

	public List<Attribute> getChildren() {
		return children;
	}

	public void setParent(Attribute parent) {
		this.parent = parent;
	}

	public Attribute getParent() {
		return parent;
	}

	public void setColorName(String colorName) {
		this.firePropertyChange(PROP_COLOR, this.colorName, this.colorName = colorName);
	}

	public String getColorName() {
		return colorName;
	}

}
