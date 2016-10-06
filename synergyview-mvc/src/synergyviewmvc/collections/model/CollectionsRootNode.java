/**
 *  File: CollectionFolder.java
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

package synergyviewmvc.collections.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewmvc.navigation.model.AbstractParent;
import synergyviewmvc.projects.ResourceHelper;
import synergyviewmvc.resource.ResourceLoader;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.INode;
import uk.ac.durham.tel.commons.jface.node.IParentNode;
import uk.ac.durham.tel.commons.jface.node.NodeRemoveException;

/**
 * @author phyo
 *
 */
public class CollectionsRootNode extends AbstractParent<Collection> {
	private EntityManagerFactory eManagerFactory;
	public static final String COLLECTIONFOLDER_ICON = "folder_star.png";
	
	/**
	 * @param resourceValue
	 * @param parentValue
	 */
	public CollectionsRootNode(IParentNode parentValue) {
		super(null, parentValue);
		this.setLabel("Collections");
		eManagerFactory = this.getEMFactoryProvider().getEntityManagerFactory();
		loadChildNodes();
	}
	
	public ImageDescriptor getIcon() {
		return ResourceLoader.getIconDescriptor(COLLECTIONFOLDER_ICON);
	}
	
	private void loadChildNodes() {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			TypedQuery<Collection> query = entityManager.createQuery("select C from Collection C", Collection.class);
			List<Collection> result = query.getResultList();
			if (result.size() > 0) {
				for(Collection collectionItem : result) {
					CollectionNode collectionNodeFolder = new CollectionNode(collectionItem, this);
					children.add(collectionNodeFolder);
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
	public void addChildCollection(Collection collectionValue) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(collectionValue);
			entityManager.getTransaction().commit();
			CollectionNode node = new CollectionNode(collectionValue, this);
			children.add(node);
			this.fireChildrenChanged();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
	
	public void removeChildCollectionNode(CollectionNode collectionFolderValue) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			Collection c = entityManager.merge(collectionFolderValue.getResource());
			entityManager.remove(c);
			entityManager.getTransaction().commit();
			this.deleteChildren(new INode[]{((INode) collectionFolderValue)});
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
	public void dispose() throws DisposeException {
		try {
			
			this.deleteChildren(children.toArray(new INode[]{}));
		} catch (NodeRemoveException e) {
			e.printStackTrace();
			throw new DisposeException("Unable to remove Collection root node.", e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		List<String> nameList = new ArrayList<String>();
		for (INode collectionNode : children) {
			nameList.add(((CollectionNode) collectionNode).getResource().getName());
		}
		return nameList;
	}

	/**
	 * @param name
	 * @return
	 */
	public boolean isMediaReferenced(String name) {
		for (INode collectionNode : children) {
			if (((CollectionNode) collectionNode).getMediaNames().contains(name))
				return true;
		}
		return false;
	}
}
