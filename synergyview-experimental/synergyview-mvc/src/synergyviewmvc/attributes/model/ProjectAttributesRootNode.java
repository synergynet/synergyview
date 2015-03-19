/**
 *  File: ProjectAttribute.java
 *  Copyright (c) 2010
 *  phyokyaw
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

package synergyviewmvc.attributes.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import uk.ac.durham.tel.commons.jface.node.IParentNode;


/**
 * @author phyokyaw
 *
 */
public class ProjectAttributesRootNode extends AttributeNode {	

	/**
	 * @param resourceValue
	 * @param parentValue
	 */
	public ProjectAttributesRootNode(IParentNode parentValue) {
		super(null, parentValue);
		this.setLabel("Attributes");
	}

	@Override
	protected void loadChildAttributeNodes() {
		EntityManager entityManager = null;
		try {
			entityManager = _eManagerFactory.createEntityManager();
			TypedQuery<Attribute> query = entityManager.createQuery("select a from Attribute a where a.parent is null", Attribute.class);
			List<Attribute> result = query.getResultList();
			if (result.size() > 0) {
				for(Attribute attribute : result) {
					AttributeNode attributeNode = new AttributeNode(attribute, this);
					children.add(attributeNode);
				}
				this.fireChildrenChanged();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

}
