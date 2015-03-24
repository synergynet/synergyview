/**
 *  File: Subject.java
 *  Copyright (c) 2010
 *  phyo
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package synergyviewcore.subjects.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import synergyviewcore.model.PersistenceModelObject;


/**
 * The Class Subject.
 *
 * @author phyo
 */
@Entity
public class Subject extends PersistenceModelObject {
	
	/** The name. */
	@Column(unique=true, nullable=false)
	private String name;
	
	/** The Constant PROP_NAME. */
	public static final String PROP_NAME = "name";
	
	/** The description. */
	private String description;
	
	/** The Constant PROP_DESCRIPTION. */
	public static final String PROP_DESCRIPTION = "description";
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.firePropertyChange(PROP_NAME, this.name, this.name = name);
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
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.firePropertyChange(PROP_DESCRIPTION, this.description, this.description = description);
	}
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
}
