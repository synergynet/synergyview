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

package synergyviewcore.collections.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcore.Activator;
import synergyviewcore.navigation.model.AbstractParent;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.navigation.model.IParentNode;
import synergyviewcore.resource.ResourceLoader;


/**
 * The Class CollectionRootNode.
 *
 * @author phyo
 */
public class CollectionRootNode extends AbstractParent<Collection> {
	
	/** The _e manager factory. */
	private EntityManagerFactory _eManagerFactory;
	
	/** The Constant COLLECTIONFOLDER_ICON. */
	public static final String COLLECTIONFOLDER_ICON = "folder_star.png";
	
	/**
	 * Instantiates a new collection root node.
	 *
	 * @param parentValue the parent value
	 */
	public CollectionRootNode(IParentNode parentValue) {
		super(null, parentValue);
		this.setLabel("Collections");
		_eManagerFactory = parentValue.getEMFactoryProvider().getEntityManagerFactory();
		loadChildNodes();
	}
	
	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.AbstractNode#getIcon()
	 */
	public ImageDescriptor getIcon() {
		return ResourceLoader.getIconDescriptor(COLLECTIONFOLDER_ICON);
	}
	
	/**
	 * Load child nodes.
	 */
	private void loadChildNodes() {
		EntityManager entityManager = null;
		try {
			entityManager = _eManagerFactory.createEntityManager();
			TypedQuery<Collection> query = entityManager.createQuery("select C from Collection C", Collection.class);
			List<Collection> result = query.getResultList();
			if (result.size() > 0) {
				for(Collection collectionItem : result) {
					CollectionNode collectionNodeFolder = new CollectionNode(collectionItem, this);
					_children.add(collectionNodeFolder);
				}
				this.fireChildrenChanged();
			}
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see synergyviewcore.attributes.model.IAttributeNode#addChildAttribute(synergyviewcore.attributes.model.Attribute)
	 */
	/**
	 * Adds the child collection.
	 *
	 * @param collectionValue the collection value
	 */
	public void addChildCollection(Collection collectionValue) {
		EntityManager entityManager = null;
		try {
			entityManager = _eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(collectionValue);
			entityManager.getTransaction().commit();
			CollectionNode node = new CollectionNode(collectionValue, this);
			_children.add(node);
			this.fireChildrenChanged();
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
	
	/**
	 * Removes the child collection node.
	 *
	 * @param collectionFolderValue the collection folder value
	 */
	public void removeChildCollectionNode(CollectionNode collectionFolderValue) {
		EntityManager entityManager = null;
		try {
			entityManager = _eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			Collection c = entityManager.merge(collectionFolderValue.getResource());
			entityManager.remove(c);
			entityManager.getTransaction().commit();
			this.deleteChildren(new INode[]{((INode) collectionFolderValue)});
			this.fireChildrenChanged();
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#dispose()
	 */
	public void dispose() {
		this.deleteChildren(_children.toArray(new INode[]{}));
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		List<String> nameList = new ArrayList<String>();
		for (INode collectionNode : _children) {
			nameList.add(((CollectionNode) collectionNode).getResource().getName());
		}
		return nameList;
		
	}

}
