/**
 *  File: SubjectNode.java
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

package synergyviewmvc.subjects.model;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewmvc.annotations.model.AnnotationSet;
import synergyviewmvc.navigation.model.AbstractParent;
import synergyviewmvc.resource.ResourceLoader;
import uk.ac.durham.tel.commons.jface.node.AbstractBaseNode;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.IParentNode;

/**
 * @author phyo
 *
 */
public class SubjectNode extends AbstractParent<Subject> {
	public static final String SUBJECT_ICON = "tag_blue.png";
	private EntityManagerFactory eManagerFactory;
	public SubjectNode(Subject subject, IParentNode parent) {
		super(subject, parent);
		this.setLabel(subject.getName());
		eManagerFactory = this.getEMFactoryProvider().getEntityManagerFactory();
	}
	
	public ImageDescriptor getIcon() {
		return ResourceLoader.getIconDescriptor(SUBJECT_ICON);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.INode#dispose()
	 */
	public void dispose() throws DisposeException {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(resource);
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new DisposeException("Unable to remove the subject resource. This may be referenced by others.", ex);
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.commons.jface.node.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
