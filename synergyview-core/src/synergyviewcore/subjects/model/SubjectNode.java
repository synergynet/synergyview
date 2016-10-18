/**
 * File: SubjectNode.java Copyright (c) 2010 phyo This program is free software;
 * you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcore.subjects.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcore.Activator;
import synergyviewcore.navigation.model.AbstractParent;
import synergyviewcore.navigation.model.DisposeException;
import synergyviewcore.navigation.model.IParentNode;
import synergyviewcore.resource.ResourceLoader;

/**
 * The Class SubjectNode.
 * 
 * @author phyo
 */
public class SubjectNode extends AbstractParent<Subject> {

    /** The Constant SUBJECT_ICON. */
    public static final String SUBJECT_ICON = "page_key.png";

    /** The e manager factory. */
    private EntityManagerFactory eManagerFactory;

    /**
     * Instantiates a new subject node.
     * 
     * @param subject
     *            the subject
     * @param parent
     *            the parent
     */
    public SubjectNode(Subject subject, IParentNode parent) {
	super(subject, parent);
	this.setLabel(subject.getName());
	this.eManagerFactory = parent.getEMFactoryProvider().getEntityManagerFactory();
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.navigation.model.INode#dispose()
     */
    public void dispose() throws DisposeException {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.navigation.model.IParentNode#getChildrenNames()
     */
    public List<String> getChildrenNames() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.navigation.model.AbstractNode#getIcon()
     */
    public ImageDescriptor getIcon() {
	return ResourceLoader.getIconDescriptor(SUBJECT_ICON);
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
