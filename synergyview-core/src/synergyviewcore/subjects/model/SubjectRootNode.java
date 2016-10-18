/**
 * File: SubjectFolder.java Copyright (c) 2010 phyo This program is free
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

package synergyviewcore.subjects.model;

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
 * The Class SubjectRootNode.
 * 
 * @author phyo
 */
public class SubjectRootNode extends AbstractParent<Subject> {

    /** The Constant SUBJECTSFOLDER_ICON. */
    public static final String SUBJECTSFOLDER_ICON = "page_gear.png";

    /** The _e manager factory. */
    private EntityManagerFactory _eManagerFactory;

    /** The subject list. */
    private List<Subject> subjectList = null;

    /**
     * Instantiates a new subject root node.
     * 
     * @param parentValue
     *            the parent value
     */
    public SubjectRootNode(IParentNode parentValue) {
	super(null, parentValue);
	this.setLabel("Subjects");
	_eManagerFactory = parentValue.getEMFactoryProvider().getEntityManagerFactory();
	loadChildNodes();
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.attributes.model.IAttributeNode#addChildAttribute( synergyviewcore.attributes.model.Attribute)
     */
    /**
     * Adds the child collection.
     * 
     * @param subjectValue
     *            the subject value
     */
    public void addChildCollection(Subject subjectValue) {
	EntityManager entityManager = null;
	try {
	    entityManager = _eManagerFactory.createEntityManager();
	    entityManager.getTransaction().begin();
	    entityManager.persist(subjectValue);
	    entityManager.getTransaction().commit();
	    SubjectNode node = new SubjectNode(subjectValue, this);
	    _children.add(node);
	    this.fireChildrenChanged();
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
	this.deleteChildren(_children.toArray(new INode[] {}));
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.navigation.model.IParentNode#getChildrenNames()
     */
    public List<String> getChildrenNames() {
	List<String> nameList = new ArrayList<String>();
	for (INode subjectNode : _children) {
	    nameList.add(((SubjectNode) subjectNode).getResource().getName());
	}
	return nameList;

    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.navigation.model.AbstractNode#getIcon()
     */
    public ImageDescriptor getIcon() {
	return ResourceLoader.getIconDescriptor(SUBJECTSFOLDER_ICON);
    }

    /**
     * Gets the subject.
     * 
     * @param id
     *            the id
     * @return the subject
     */
    public Subject getSubject(String id) {
	for (Subject subject : subjectList) {
	    if (subject.getId().compareTo(id) == 0) {
		return subject;
	    }
	}
	return null;
    }

    /**
     * Gets the subject by name.
     * 
     * @param name
     *            the name
     * @return the subject by name
     */
    public Subject getSubjectByName(String name) {
	for (Subject subject : subjectList) {
	    if (subject.getName().compareTo(name) == 0) {
		return subject;
	    }
	}
	return null;
    }

    /**
     * Load child nodes.
     */
    private void loadChildNodes() {
	EntityManager entityManager = null;
	try {
	    entityManager = _eManagerFactory.createEntityManager();
	    TypedQuery<Subject> query = entityManager.createQuery("select S from Subject S", Subject.class);
	    subjectList = query.getResultList();
	    if (subjectList.size() > 0) {
		for (Subject subjectItem : subjectList) {
		    SubjectNode subjectNode = new SubjectNode(subjectItem, this);
		    _children.add(subjectNode);
		}
		this.fireChildrenChanged();
	    }
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
     * Removes the child collection node.
     * 
     * @param subjectNodeValue
     *            the subject node value
     */
    public void removeChildCollectionNode(SubjectNode subjectNodeValue) {
	EntityManager entityManager = null;
	try {
	    entityManager = _eManagerFactory.createEntityManager();
	    entityManager.getTransaction().begin();
	    Subject c = entityManager.merge(subjectNodeValue.getResource());
	    entityManager.remove(c);
	    entityManager.getTransaction().commit();
	    this.deleteChildren(new INode[] { ((INode) subjectNodeValue) });
	    this.fireChildrenChanged();
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
