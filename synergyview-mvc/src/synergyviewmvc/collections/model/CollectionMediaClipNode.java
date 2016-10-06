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

package synergyviewmvc.collections.model;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewmvc.annotations.model.AnnotationSet;
import synergyviewmvc.annotations.model.AnnotationSetNode;
import synergyviewmvc.navigation.model.AbstractParent;
import synergyviewmvc.resource.ResourceLoader;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.INode;
import uk.ac.durham.tel.commons.jface.node.IParentNode;
import uk.ac.durham.tel.commons.jface.node.NodeRemoveException;

/**
 * @author phyokyaw
 *
 */
public class CollectionMediaClipNode extends AbstractParent<CollectionMediaClip> {
	private EntityManagerFactory eManagerFactory;
	public static final String COLLECTION_CLIP_ICON = "link.png";

	/**
	 * @param resourceValue
	 * @param parentValue
	 */
	public CollectionMediaClipNode(CollectionMediaClip resourceValue, IParentNode parentValue) {
		super(resourceValue, parentValue);
		this.setLabel(resource.getClipName());
		eManagerFactory = this.getEMFactoryProvider().getEntityManagerFactory();

		loadAnnotation();
	}

	public void updateResource() {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.merge(this.getResource());
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

	/**
	 * 
	 */
	private void loadAnnotation() {

		for(AnnotationSet annotationSetItem : resource.getAnalysisTranscriptionList()) {
			AnnotationSetNode annotationSetNode = new AnnotationSetNode(annotationSetItem, this);
			children.add(annotationSetNode);
		}
		this.fireChildrenChanged();

	}

	public ImageDescriptor getIcon() {
		return ResourceLoader.getIconDescriptor(COLLECTION_CLIP_ICON);
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.INode#dispose()
	 */
	public void dispose() throws DisposeException {
		try {
			this.deleteChildren(children.toArray(new INode[]{}));
			EntityManager entityManager = null;
			try {
				entityManager = eManagerFactory.createEntityManager();
				entityManager.getTransaction().begin();
				//TODO fix this
				entityManager.getTransaction().commit();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (NodeRemoveException e) {
			e.printStackTrace();
			new DisposeException("Unable to remove the Collection Media Clip Node", e);
		}
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 */
	public void addAnnotation(String nameValue) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			AnnotationSet annotationSet = new AnnotationSet();
			annotationSet.setName(nameValue);
			annotationSet.setCollectionMediaClip(resource);
			annotationSet.setId(UUID.randomUUID().toString());
			resource.getAnalysisTranscriptionList().add(annotationSet);
			entityManager.persist(annotationSet);
			entityManager.getTransaction().commit();
			AnnotationSetNode node = new AnnotationSetNode(annotationSet, this);
			children.add(node);
			this.fireChildrenChanged();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void removeAnnotationNode(AnnotationSetNode nodeValue) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			resource.getAnalysisTranscriptionList().remove(nodeValue.getResource());
			AnnotationSet setToRemove = entityManager.merge(nodeValue.getResource());
			entityManager.remove(setToRemove);
			entityManager.getTransaction().commit();
			this.deleteChildren(new INode[]{nodeValue});
			this.fireChildrenChanged();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
