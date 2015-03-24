/**
 *  File: PersistenceModelObject.java
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

package synergyviewcommons.persistence;


import java.util.UUID;

import synergyviewcommons.model.PropertySupportObject;


/**
 * The Class IdBasedObject.
 */
public abstract class IdBasedObject extends PropertySupportObject {
	
	/** The Constant PROP_ID. */
	public static final String PROP_ID = "id";
	
	/** The id. */
	protected String id;
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.firePropertyChange(PROP_ID, this.id, this.id = id);
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
 
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IdBasedObject)) {
            return false;
        }
        IdBasedObject other = (IdBasedObject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
 
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return "Entity [id=" + id + "]";
    }
    
    /**
     * Creates the uuid.
     *
     * @return the string
     */
    public static String createUUID() {
    	return UUID.randomUUID().toString();
    }
    
    /**
     * Creates the.
     *
     * @param <T> the generic type
     * @param className the class name
     * @return the t
     */
    public static <T extends IdBasedObject> T create(Class<T> className) {
    	T instance = null;
		try {
			instance = className.newInstance();
			instance.setId(createUUID());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return instance;
    }
}
