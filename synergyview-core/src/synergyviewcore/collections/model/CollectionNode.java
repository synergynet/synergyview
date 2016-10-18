/**
 * File: CollectionNodeFolder.java Copyright (c) 2010 phyo This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import synergyviewcore.Activator;
import synergyviewcore.collections.ui.editors.CollectionEditor;
import synergyviewcore.navigation.model.AbstractParent;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.navigation.model.IParentNode;
import synergyviewcore.projects.ui.NodeEditorInput;
import synergyviewcore.resource.ResourceLoader;

/**
 * The Class CollectionNode.
 * 
 * @author phyo
 */
public class CollectionNode extends AbstractParent<Collection> {

    /** The Constant COLLECTION_ICON. */
    public static final String COLLECTION_ICON = "film.png";

    /** The e manager factory. */
    private EntityManagerFactory eManagerFactory;

    /**
     * Instantiates a new collection node.
     * 
     * @param resourceValue
     *            the resource value
     * @param parentValue
     *            the parent value
     */
    public CollectionNode(Collection resourceValue, IParentNode parentValue) {
	super(resourceValue, parentValue);
	this.setLabel(resourceValue.getName());
	eManagerFactory = parentValue.getEMFactoryProvider().getEntityManagerFactory();
	loadChildNodes();

    }

    /**
     * Adds the clip.
     * 
     * @param cClips
     *            the c clips
     * @throws Exception
     *             the exception
     */
    public void addClip(List<CollectionMediaClip> cClips) throws Exception {
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
		_children.add(node);
	    }
	    this.fireChildrenChanged();
	    this.getViewerProvider().getTreeViewer().refresh(this);
	} catch (Exception ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	    throw new Exception("Unable to add clip!", ex);
	}
    }

    /**
     * Adds the media.
     * 
     * @param cMedias
     *            the c medias
     */
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
	    this.getViewerProvider().getTreeViewer().refresh(this);
	} catch (Exception ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	} finally {
	    if (entityManager.isOpen()) {
		entityManager.close();
	    }
	}
    }

    /**
     * Clear clip collection.
     */
    public void clearClipCollection() {
	EntityManager entityManager = null;
	try {
	    entityManager = eManagerFactory.createEntityManager();
	    entityManager.getTransaction().begin();
	    resource = entityManager.merge(resource);
	    resource.getCollectionMediaClipList().clear();
	    entityManager.persist(resource);
	    entityManager.getTransaction().commit();
	    this.getViewerProvider().getTreeViewer().refresh(this);
	} catch (Exception ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	} finally {
	    if (entityManager.isOpen()) {
		entityManager.close();
	    }
	}
    }

    /**
     * Clear media collection.
     */
    public void clearMediaCollection() {
	EntityManager entityManager = null;
	try {
	    entityManager = eManagerFactory.createEntityManager();
	    entityManager.getTransaction().begin();
	    resource = entityManager.merge(resource);
	    resource.getCollectionMediaList().clear();
	    entityManager.persist(resource);
	    entityManager.getTransaction().commit();
	    this.getViewerProvider().getTreeViewer().refresh(this);
	} catch (Exception ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	} finally {
	    if (entityManager.isOpen()) {
		entityManager.close();
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.navigation.model.INode#dispose()
     */
    public void dispose() {

	// Find the close the editor related to this collection
	Display.getDefault().asyncExec(new Runnable() {
	    public void run() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
		    for (IWorkbenchPage pageRef : window.getPages()) {
			for (IEditorReference editorRef : pageRef.getEditorReferences()) {
			    try {
				if (editorRef.getId().compareTo(CollectionEditor.ID) == 0) {
				    NodeEditorInput cNodeEditorInput = (NodeEditorInput) editorRef.getEditorInput();
				    if (CollectionNode.this == cNodeEditorInput.getNode()) {
					pageRef.closeEditor(editorRef.getEditor(false), false);
				    }
				}
			    } catch (PartInitException ex) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
				logger.log(status);
			    }

			}
		    }
		}
	    }

	});

	this.deleteChildren(_children.toArray(new INode[] {}));
    }

    /**
     * Find collection media clip node.
     * 
     * @param clipValue
     *            the clip value
     * @return the collection media clip node
     */
    public CollectionMediaClipNode findCollectionMediaClipNode(CollectionMediaClip clipValue) {
	for (INode cClipNode : _children) {
	    if (cClipNode.getResource() == clipValue) {
		return (CollectionMediaClipNode) cClipNode;
	    }
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.navigation.model.IParentNode#getChildrenNames()
     */
    public List<String> getChildrenNames() {
	List<String> nameList = new ArrayList<String>();
	for (INode collectionClipNode : _children) {
	    nameList.add(((CollectionMediaClipNode) collectionClipNode).getResource().getClipName());
	}
	return nameList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.navigation.model.AbstractNode#getIcon()
     */
    public ImageDescriptor getIcon() {
	return ResourceLoader.getIconDescriptor(COLLECTION_ICON);
    }

    /**
     * Gets the media names.
     * 
     * @return the media names
     */
    public List<String> getMediaNames() {

	EntityManager entityManager = null;
	try {
	    entityManager = eManagerFactory.createEntityManager();
	    TypedQuery<String> q = entityManager.createQuery("SELECT CM.mediaName FROM CollectionMedia CM Where CM.collection = :collection", String.class);
	    q.setParameter("collection", this.getResource());
	    return q.getResultList();
	} catch (Exception ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	    return null;
	} finally {
	    if (entityManager.isOpen()) {
		entityManager.close();
	    }
	}
    }

    /**
     * Load child nodes.
     */
    private void loadChildNodes() {
	for (CollectionMediaClip collectionItem : resource.getCollectionMediaClipList()) {
	    CollectionMediaClipNode collectionClipNodeFolder = new CollectionMediaClipNode(collectionItem, this);
	    _children.add(collectionClipNodeFolder);
	}
	this.fireChildrenChanged();
    }

    /**
     * Removes the clip.
     * 
     * @param cClips
     *            the c clips
     * @throws Exception
     *             the exception
     */
    public void removeClip(List<CollectionMediaClip> cClips) throws Exception {
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
	    this.deleteChildren(childrenToRemove.toArray(new INode[] {}));
	    this.fireChildrenChanged();
	    this.getViewerProvider().getTreeViewer().refresh(this);
	} catch (Exception ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	    throw new Exception("Unable to remove clip!", ex);
	}
    }

    /**
     * Removes the media.
     * 
     * @param cMedias
     *            the c medias
     */
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
	    this.getViewerProvider().getTreeViewer().refresh(this);
	} catch (Exception ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	} finally {
	    if (entityManager.isOpen()) {
		entityManager.close();
	    }
	}
    }

    /**
     * Update media.
     * 
     * @param cMedia
     *            the c media
     */
    public void updateMedia(CollectionMedia cMedia) {
	EntityManager entityManager = null;
	try {
	    entityManager = eManagerFactory.createEntityManager();
	    entityManager.getTransaction().begin();
	    entityManager.merge(cMedia);
	    entityManager.getTransaction().commit();
	    this.getViewerProvider().getTreeViewer().refresh(this);
	} catch (Exception ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	} finally {
	    if (entityManager.isOpen()) {
		entityManager.close();
	    }
	}
    }

    /**
     * Update resource.
     */
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
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	} finally {
	    if (entityManager.isOpen()) {
		entityManager.close();
	    }
	}
    }
}
