/**
 *  File: CollectionNodeFolder.java
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import synergyviewmvc.collections.ui.editors.CollectionEditor;
import synergyviewmvc.navigation.model.AbstractParent;
import synergyviewmvc.projects.ui.NodeEditorInput;
import synergyviewmvc.resource.ResourceLoader;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.INode;
import uk.ac.durham.tel.commons.jface.node.IParentNode;
import uk.ac.durham.tel.commons.jface.node.NodeRemoveException;

/**
 * @author phyo
 *
 */
public class CollectionNode extends AbstractParent<Collection> {
	private EntityManagerFactory eManagerFactory;
	public static final String COLLECTION_ICON = "film.png";
	//private IObservableList _collectionMediaClipList;
	//private IObservableList _collectionMediaList;

	/**
	 * @param resourceValue
	 * @param parentValue
	 */
	public CollectionNode(Collection resourceValue,
			IParentNode parentValue) {
		super(resourceValue, parentValue);
		this.setLabel(resourceValue.getName());
		eManagerFactory = this.getEMFactoryProvider().getEntityManagerFactory();
		loadChildNodes();
	}

	public CollectionMediaClipNode findCollectionMediaClipNode(CollectionMediaClip clipValue) {
		for (INode cClipNode : children) {
			if (cClipNode.getResource() == clipValue) {
				return (CollectionMediaClipNode) cClipNode;
			}
		}
		return null;
	}

	private void loadChildNodes() {
		for(CollectionMediaClip collectionItem : resource.getCollectionMediaClipList()) {
			CollectionMediaClipNode collectionClipNode = new CollectionMediaClipNode(collectionItem, this);
			children.add(collectionClipNode);
		}
		this.fireChildrenChanged();
	}

	public ImageDescriptor getIcon() {
		return ResourceLoader.getIconDescriptor(COLLECTION_ICON);
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.INode#dispose()
	 */
	public void dispose() throws DisposeException {
		try {
			this.deleteChildren(children.toArray(new INode[]{}));
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window!=null) {
				for (IWorkbenchPage ref : window.getPages()) {
					if (ref.getActiveEditor() instanceof CollectionEditor) {
						CollectionEditor colEditor = (CollectionEditor) ref.getActiveEditor();
						NodeEditorInput cNodeEditorInput = (NodeEditorInput) colEditor.getEditorInput();
						if (this == cNodeEditorInput.getNode()) {
							ref.closeEditor(ref.getActiveEditor(), false);
						}
					}
				}
			}
		} catch (NodeRemoveException e) {
			e.printStackTrace();
			throw new DisposeException("Unable to remove the Collection Node.", e);
		}
	}

	public void updateResource() {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.merge(this.getResource());
			entityManager.getTransaction().commit();
			this.setLabel(resource.getName());
			this.getViewerProvider().getTreeViewer().refresh(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

	public void addMedia(List<CollectionMedia> cMedias) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			resource.getCollectionMediaList().addAll(cMedias);
			for (CollectionMedia mediaItem : cMedias) {
				mediaItem.setCollection(resource);
				entityManager.persist(mediaItem);
			}
			entityManager.getTransaction().commit();
			this.fireChildrenChanged();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

	public void updateMedia(CollectionMedia cMedia) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.merge(cMedia);
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

	public void removeMedia(List<CollectionMedia> cMedias) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			resource.getCollectionMediaList().removeAll(cMedias);
			entityManager.getTransaction().begin();
			for (CollectionMedia cMedia : cMedias) {
				CollectionMedia collectionToRemove = entityManager.merge(cMedia);
				entityManager.remove(collectionToRemove);
			}
			entityManager.getTransaction().commit();
			this.fireChildrenChanged();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

	public void clearMediaCollection() {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			resource = entityManager.merge(resource);
			resource.getCollectionMediaList().clear();
			entityManager.persist(resource);
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

	public void addClip(List<CollectionMediaClip> cClips) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			resource.getCollectionMediaClipList().addAll(cClips);
			entityManager.getTransaction().begin();
			for (CollectionMediaClip mediaClipItem : cClips) {
				mediaClipItem.setCollection(resource);
				entityManager.persist(mediaClipItem);
			}
			entityManager.getTransaction().commit();
			for (CollectionMediaClip cClip : cClips) {
				CollectionMediaClipNode node = new CollectionMediaClipNode(cClip, CollectionNode.this);
				children.add(node);
			}
			this.fireChildrenChanged();
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	public void removeClip(List<CollectionMediaClip> cClips) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			resource.getCollectionMediaClipList().removeAll(cClips);
			entityManager.getTransaction().begin();
			List<INode> childrenToRemove = new ArrayList<INode>();
			for (CollectionMediaClip cClip : cClips) {
				CollectionMediaClip sessionClip = entityManager.merge(cClip);
				entityManager.remove(sessionClip);
				childrenToRemove.add(findCollectionMediaClipNode(cClip));
			}
			entityManager.getTransaction().commit();
			this.deleteChildren(childrenToRemove.toArray(new INode[]{}));
			this.fireChildrenChanged();
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	public void clearClipCollection() {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			resource = entityManager.merge(resource);
			resource.getCollectionMediaClipList().clear();
			entityManager.persist(resource);
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		List<String> nameList = new ArrayList<String>();
		for (INode collectionClipNode : children) {
			nameList.add(((CollectionMediaClipNode) collectionClipNode).getResource().getClipName());
		}
		return nameList;
	}

	public List<String> getMediaNames() {

		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			TypedQuery<String> q = entityManager.createQuery("SELECT CM.mediaName FROM CollectionMedia CM Where CM.collection = :collection", String.class);
			q.setParameter("collection", this.getResource());
			return q.getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
}
