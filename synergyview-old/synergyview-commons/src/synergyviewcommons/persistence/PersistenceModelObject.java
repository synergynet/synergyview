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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import synergyviewcommons.model.PropertySupportObject;

/**
 * @author phyo
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class PersistenceModelObject extends PropertySupportObject {
	
	public static final String PROP_ID = "id";
	@Id
	protected String id;
	
	public void setId(String id) {
		this.firePropertyChange(PROP_ID, this.id, this.id = id);
	}

	public String getId() {
		return id;
	}
	
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
 
    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistenceModelObject)) {
            return false;
        }
        PersistenceModelObject other = (PersistenceModelObject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
 
    @Override
    public String toString() {
    	return "Entity [id=" + id + "]";
    }
}
