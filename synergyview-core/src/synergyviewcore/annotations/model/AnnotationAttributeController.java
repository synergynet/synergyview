/**
 * File: AnnotationAttributeController.java Copyright (c) 2010 phyo This program
 * is free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcore.annotations.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import synergyviewcommons.collections.CollectionChangeListener;
import synergyviewcommons.collections.IObservableList;
import synergyviewcommons.collections.ObservableList;
import synergyviewcore.Activator;
import synergyviewcore.attributes.model.Attribute;

/**
 * The Class AnnotationAttributeController.
 * 
 * @author phyo
 */
public class AnnotationAttributeController {
	
	/** The annotation. */
	private Annotation annotation;
	
	/** The attribute list. */
	private IObservableList<List<Attribute>, Attribute> attributeList;
	
	/** The e manager factory. */
	private EntityManagerFactory eManagerFactory;
	
	/** The logger. */
	private final ILog logger;
	
	/**
	 * Instantiates a new annotation attribute controller.
	 * 
	 * @param annotation
	 *            the annotation
	 * @param eManagerFactory
	 *            the e manager factory
	 */
	public AnnotationAttributeController(Annotation annotation,
			EntityManagerFactory eManagerFactory) {
		logger = Activator.getDefault().getLog();
		attributeList = new ObservableList<List<Attribute>, Attribute>(
				annotation.getAttributes());
		this.eManagerFactory = eManagerFactory;
		this.annotation = annotation;
	}
	
	/**
	 * Adds the attribute list.
	 * 
	 * @param attributeListToAdd
	 *            the attribute list to add
	 */
	public void addAttributeList(List<Attribute> attributeListToAdd) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			for (Attribute attribute : attributeListToAdd) {
				if (!attributeList.contains(attribute)) {
					attributeList.add(attribute);
				}
			}
			entityManager.merge(annotation);
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}
	
	/**
	 * Adds the attribute list change listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addAttributeListChangeListener(CollectionChangeListener listener) {
		attributeList.addChangeListener(listener);
	}
	
	/**
	 * Gets the attribute list.
	 * 
	 * @return the attribute list
	 */
	public List<Attribute> getAttributeList() {
		return attributeList.getReadOnlyList();
	}
	
	/**
	 * Removes the attribute list.
	 * 
	 * @param attributeListToRemove
	 *            the attribute list to remove
	 */
	public void removeAttributeList(List<Attribute> attributeListToRemove) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			for (Attribute attribute : attributeListToRemove) {
				if (attributeList.contains(attribute)) {
					attributeList.remove(attribute);
				}
			}
			entityManager.merge(annotation);
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}
	
	/**
	 * Removes the attribute list change listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeAttributeListChangeListener(
			CollectionChangeListener listener) {
		attributeList.removeChangeListener(listener);
	}
	
}
