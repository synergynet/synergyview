/**
 *  File: AnnotationSetNode.java
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

package synergyviewmvc.annotations.model;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import synergyviewmvc.annotations.ui.editors.CollectionMediaClipAnnotationEditor;
import synergyviewmvc.attributes.model.Attribute;
import synergyviewmvc.navigation.model.AbstractParent;
import synergyviewmvc.projects.model.WorkspaceRoot;
import synergyviewmvc.projects.ui.NodeEditorInput;
import synergyviewmvc.resource.ResourceLoader;
import synergyviewmvc.subjects.model.Subject;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.IParentNode;

/**
 * @author phyo
 *
 */
public class AnnotationSetNode extends AbstractParent<AnnotationSet> {
	private EntityManagerFactory eManagerFactory;
	private IObservableList annotationList;
	private IObservableList subjectList;

	public static final String ANNOTATION_ICON = "note.png";

	public AnnotationSetNode(AnnotationSet annotationSet, IParentNode parentValue) {
		super(annotationSet, parentValue);
		this.setLabel(annotationSet.getName());
		WorkspaceRoot root = (WorkspaceRoot) this.getRoot();
		annotationList = BeanProperties.list(AnnotationSet.PROP_ANNOTATIONLIST).observe(root.getRealm(), this.getResource());
		subjectList = BeanProperties.list(AnnotationSet.PROP_SUBJECTLIST).observe(root.getRealm(), this.getResource());
		eManagerFactory = this.getEMFactoryProvider().getEntityManagerFactory();
	}

	public void addAnnotations(List<Annotation> annotations, Subject subject) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			annotationList.addAll(annotations);
			for (Annotation annotation : annotations) {
				annotation.setAnnotationSet(this.getResource());
				annotation.setSubject(subject);
				entityManager.persist(annotation);
			}
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public IObservableList getObservableAnnotationList() {
		return (IObservableList) Collections.unmodifiableList(annotationList);
	}
	
	@SuppressWarnings("unchecked")
	public IObservableList getObservableSubjectsList() {
		return (IObservableList) Collections.unmodifiableList(subjectList);
	}

	public ImageDescriptor getIcon() {
		return ResourceLoader.getIconDescriptor(ANNOTATION_ICON);
	}

	public void removeAnnoations(List<Annotation> annotations) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			annotationList.removeAll(annotations);
			for (Annotation annotation : annotations) {
				Annotation annoToRemove = entityManager.merge(annotation);
				entityManager.remove(annoToRemove);
			}
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

	public void addSubjects(List<Subject> attributes) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			subjectList.addAll(attributes);
			entityManager.merge(resource);
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

	
	
	public void removeSubjects(List<Subject> attributes) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			subjectList.removeAll(attributes);
			entityManager.merge(resource);
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}



	/**
	 * @param result
	 * @return
	 */
	public List<Annotation> getAnnotationsForSubject(Subject subject) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			TypedQuery<Annotation> q = entityManager.createQuery("SELECT A from Annotation A WHERE A.subject = :subject AND A.annotationSet = :set", Annotation.class);
			q.setParameter("subject", subject);
			q.setParameter("set", this.getResource());
			return Collections.unmodifiableList(q.getResultList());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}

	}

	/**
	 * @param analysisCaption
	 * @param resource
	 */
	public void addAttributeToAnnotation(Annotation analysisCaption,
			List<Attribute> attributesToAdd) {
		EntityManager entityManager = null;
		try {
			for (Attribute a : attributesToAdd) {
				if (!analysisCaption.getAttributes().contains(a))
					analysisCaption.getAttributes().add(a);
			}
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.merge(analysisCaption);
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
	

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void updateAnnotation(Annotation annotation) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			if (annotationList.contains(annotation)) {
				entityManager.getTransaction().begin();
				entityManager.merge(annotation);
				entityManager.getTransaction().commit();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.commons.jface.node.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.commons.jface.node.INode#dispose()
	 */
	public void dispose() throws DisposeException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window!=null) {
			for (IWorkbenchPage ref : window.getPages()) {
				if (ref.getActiveEditor() instanceof CollectionMediaClipAnnotationEditor) {
					CollectionMediaClipAnnotationEditor annoEditor = (CollectionMediaClipAnnotationEditor) ref.getActiveEditor();
					NodeEditorInput cNodeEditorInput = (NodeEditorInput) annoEditor.getEditorInput();
					if (this == cNodeEditorInput.getNode()) {
						ref.closeEditor(ref.getActiveEditor(), false);
					}
				}
			}
		}
	}
	
	

}
