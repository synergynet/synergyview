/**
 *  File: SubjectFolder.java
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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewmvc.navigation.model.AbstractParent;
import synergyviewmvc.resource.ResourceLoader;
import uk.ac.durham.tel.commons.jface.node.INode;
import uk.ac.durham.tel.commons.jface.node.IParentNode;
import uk.ac.durham.tel.commons.jface.node.NodeRemoveException;

/**
 * @author phyo
 *
 */
public class SubjectsRootNode extends AbstractParent<Subject> {
	private EntityManagerFactory eManagerFactory;
	public static final String SUBJECTSFOLDER_ICON = "tag_green.png";
	/**
	 * @param resourceValue
	 * @param parentValue
	 */
	public SubjectsRootNode(IParentNode parentValue) {
		super(null, parentValue);
		this.setLabel("Subjects");
		eManagerFactory = this.getEMFactoryProvider().getEntityManagerFactory();
		loadChildNodes();
	}
	
	public ImageDescriptor getIcon() {
		return ResourceLoader.getIconDescriptor(SUBJECTSFOLDER_ICON);
	}
	
	private void loadChildNodes() {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			TypedQuery<Subject> query = entityManager.createQuery("select S from Subject S", Subject.class);
			List<Subject> result = query.getResultList();
			if (result.size() > 0) {
				for(Subject subjectItem : result) {
					SubjectNode subjectNode = new SubjectNode(subjectItem, this);
					children.add(subjectNode);
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
	
	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.attributes.model.IAttributeNode#addChildAttribute(uk.ac.durham.tel.synergynet.ats.attributes.model.Attribute)
	 */
	public void addChildCollection(Subject subjectValue) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(subjectValue);
			entityManager.getTransaction().commit();
			SubjectNode node = new SubjectNode(subjectValue, this);
			children.add(node);
			this.fireChildrenChanged();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
	
	public void removeChildCollectionNode(SubjectNode subjectNodeValue) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			Subject c = entityManager.merge(subjectNodeValue.getResource());
			entityManager.remove(c);
			entityManager.getTransaction().commit();
			this.deleteChildren(new INode[]{((INode) subjectNodeValue)});
			this.fireChildrenChanged();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.INode#dispose()
	 */
	public void dispose() {
		try {
			this.deleteChildren(children.toArray(new INode[]{}));
		} catch (NodeRemoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		List<String> nameList = new ArrayList<String>();
		for (INode subjectNode : children) {
			nameList.add(((SubjectNode) subjectNode).getResource().getName());
		}
		return nameList;
	}
	
	public INode[] getSubjectNodes(String[] excludedSubjects) {
		return null;
	}
}
