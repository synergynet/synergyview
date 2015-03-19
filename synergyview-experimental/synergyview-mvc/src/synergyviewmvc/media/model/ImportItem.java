/**
 *  File: ImportItem.java
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

package synergyviewmvc.media.model;

import javax.persistence.Entity;

import uk.ac.durham.tel.commons.persistence.PersistenceModelObject;

/**
 * @author phyo
 *
 */
@Entity
public class ImportItem extends PersistenceModelObject {
	
	private String fileName;
	public static final String PROP_FILENAME = "fileName";
	
	public void setFileName(String fileName) {
		this.firePropertyChange(PROP_FILENAME, this.fileName, this.fileName = fileName);
	}
	public String getFileName() {
		return fileName;
	}
}
