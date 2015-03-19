/**
 *  File: CollectionMediaClipNode.java
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

package synergyviewcore.collections.model;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcore.Activator;
import synergyviewcore.annotations.model.AnnotationSet;
import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.model.ModelPersistenceException;
import synergyviewcore.navigation.model.AbstractParent;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.navigation.model.IParentNode;
import synergyviewcore.resource.ResourceLoader;

/**
 * @author phyokyaw
 *
 */
public class CollectionMediaClipNode extends AbstractParent<CollectionMediaClip> {
	private EntityManagerFactory _eManagerFactory;
	public static final String COLLECTION_CLIP_ICON = "link.png";

	/**
	 * @param resourceValue
	 * @param parentValue
	 */
	public CollectionMediaClipNode(CollectionMediaClip resourceValue, IParentNode parentValue) {
		super(resourceValue, parentValue);
		this.setLabel(resource.getClipName());
		_eManagerFactory = parentValue.getEMFactoryProvider().getEntityManagerFactory();

		loadAnnotation();
	}

	public void updateResource() {
		EntityManager entityManager = null;
		try {
			entityManager = _eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.merge(this.getResource());
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

	/**
	 * 
	 */
	private void loadAnnotation() {

		for(AnnotationSet annotationSetItem : resource.getAnnotationSetList()) {
			AnnotationSetNode annotationSetNode = new AnnotationSetNode(annotationSetItem, this);
			_children.add(annotationSetNode);
		}
		this.fireChildrenChanged();

	}

	public ImageDescriptor getIcon() {
		return ResourceLoader.getIconDescriptor(COLLECTION_CLIP_ICON);
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
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 */
	public void addAnnotationSet(String annotationSetName) {
		EntityManager entityManager = null;
		try {
			entityManager = _eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			AnnotationSet annotationSet = new AnnotationSet();
			annotationSet.setName(annotationSetName);
			annotationSet.setCollectionMediaClip(resource);
			annotationSet.setId(UUID.randomUUID().toString());
			resource.getAnnotationSetList().add(annotationSet);
			entityManager.persist(annotationSet);
			entityManager.getTransaction().commit();
			AnnotationSetNode node = new AnnotationSetNode(annotationSet, this);
			_children.add(node);
			this.fireChildrenChanged();
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}

	public void removeAnnotationSetNode(AnnotationSetNode nodeValue) throws ModelPersistenceException {
		EntityManager entityManager = null;
		try {
			entityManager = _eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			resource.getAnnotationSetList().remove(nodeValue.getResource());
			AnnotationSet setToRemove = entityManager.merge(nodeValue.getResource());
			entityManager.remove(setToRemove);
			entityManager.getTransaction().commit();
			this.deleteChildren(new INode[]{nodeValue});
			this.fireChildrenChanged();
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			throw new ModelPersistenceException("Unable to delete.", ex);
		}
	}
	
	public void renameClip(String name) throws ModelPersistenceException {
		EntityManager entityManager = null;
		try {
			entityManager = _eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			resource.setClipName(name);
			entityManager.merge(resource);
			entityManager.getTransaction().commit();
			this.getViewerProvider().getTreeViewer().refresh(this);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			throw new ModelPersistenceException("Unable to rename.", ex);
		}
	}

}
