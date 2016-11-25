/**
 * File: AnnotationSetNode.java Copyright (c) 2010 phyo This program is free
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

package synergyviewcore.annotations.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import synergyviewcommons.collections.CollectionChangeListener;
import synergyviewcommons.collections.IObservableList;
import synergyviewcommons.collections.ObservableList;
import synergyviewcore.Activator;
import synergyviewcore.annotations.ui.editors.CollectionMediaClipAnnotationEditor;
import synergyviewcore.model.ModelPersistenceException;
import synergyviewcore.navigation.model.AbstractParent;
import synergyviewcore.navigation.model.DisposeException;
import synergyviewcore.navigation.model.IParentNode;
import synergyviewcore.projects.ui.NodeEditorInput;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.subjects.model.Subject;

/**
 * The Class AnnotationSetNode.
 * 
 * @author phyo
 */
public class AnnotationSetNode extends AbstractParent<AnnotationSet> {

    /** The Constant ANNOTATION_ICON. */
    public static final String ANNOTATION_ICON = "note.png";

    /** The annotation attribute map. */
    private Map<Annotation, AnnotationAttributeController> annotationAttributeMap = new HashMap<Annotation, AnnotationAttributeController>();

    /** The annotation list. */
    private IObservableList<List<Annotation>, Annotation> annotationList;

    /** The e manager factory. */
    private EntityManagerFactory eManagerFactory;

    /** The subject annotation map. */
    private Map<Subject, IObservableList<List<Annotation>, Annotation>> subjectAnnotationMap = new HashMap<Subject, IObservableList<List<Annotation>, Annotation>>();

    /** The subject list. */
    private IObservableList<List<Subject>, Subject> subjectList;

    /**
     * Instantiates a new annotation set node.
     * 
     * @param annotationSet
     *            the annotation set
     * @param parentValue
     *            the parent value
     */
    public AnnotationSetNode(AnnotationSet annotationSet, IParentNode parentValue) {
	super(annotationSet, parentValue);
	this.setLabel(annotationSet.getName());
	eManagerFactory = this.getEMFactoryProvider().getEntityManagerFactory();
	initialise();
    }

    /**
     * Adds the annotation list change listener.
     * 
     * @param listener
     *            the listener
     */
    public void addAnnotationListChangeListener(CollectionChangeListener listener) {
	annotationList.addChangeListener(listener);
    }

    /**
     * Adds the annotation list change listener.
     * 
     * @param listener
     *            the listener
     * @param subject
     *            the subject
     * @throws Exception
     *             the exception
     */
    public void addAnnotationListChangeListener(CollectionChangeListener listener, Subject subject) throws Exception {
	// Check the parameter
	if (subject == null) {
	    throw new IllegalArgumentException("Subject parameter is null");
	}
	IObservableList<List<Annotation>, Annotation> tempList = subjectAnnotationMap.get(subject);
	if (tempList == null) {
	    throw new IllegalArgumentException("Subject not found");
	}

	tempList.addChangeListener(listener);
    }

    /**
     * Adds the annotations.
     * 
     * @param annotations
     *            the annotations
     * @param subject
     *            the subject
     * @throws ModelPersistenceException
     *             the model persistence exception
     */
    public void addAnnotations(List<Annotation> annotations, Subject subject) throws ModelPersistenceException {
	EntityManager entityManager = null;
	IObservableList<List<Annotation>, Annotation> tempList = subjectAnnotationMap.get(subject);
	if (tempList != null) {
	    try {
		for (Annotation annotation : annotations) {
		    if (!isAnntationWithinClip(annotation)) {
			throw new Exception("Unable to add annotation with the following text as it is out of media clip area: " + annotation.getText());
		    }
		}

		entityManager = eManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		for (Annotation annotation : annotations) {
		    annotation.setAnnotationSet(this.getResource());
		    annotation.setSubject(subject);
		    entityManager.persist(annotation);
		    annotationAttributeMap.put(annotation, new AnnotationAttributeController(annotation, eManagerFactory));
		}
		entityManager.getTransaction().commit();
		tempList.addAll(annotations);
		annotationList.addAll(annotations);

	    } catch (Exception ex) {
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
		logger.log(status);
		throw new ModelPersistenceException("Unable to add annotation", ex);
	    } finally {
		if (entityManager.isOpen()) {
		    entityManager.close();
		}
	    }
	}
    }

    /**
     * Adds the subject list change listener.
     * 
     * @param listener
     *            the listener
     */
    public void addSubjectListChangeListener(CollectionChangeListener listener) {
	subjectList.addChangeListener(listener);
    }

    /**
     * Adds the subjects.
     * 
     * @param subjects
     *            the subjects
     * @throws ModelPersistenceException
     *             the model persistence exception
     */
    public void addSubjects(List<Subject> subjects) throws ModelPersistenceException {
	EntityManager entityManager = null;
	if (!subjectList.containsAll(subjects)) {
	    try {
		entityManager = eManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		for (Subject subject : subjects) {
		    List<Annotation> annotationList = new ArrayList<Annotation>();
		    subjectAnnotationMap.put(subject, new ObservableList<List<Annotation>, Annotation>(annotationList));
		}
		subjectList.addAll(subjects);
		entityManager.merge(resource);
		entityManager.getTransaction().commit();
	    } catch (Exception ex) {
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
		logger.log(status);
		throw new ModelPersistenceException("Unable to add subjects.", ex);
	    } finally {
		if (entityManager.isOpen()) {
		    entityManager.close();
		}
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcommons.jface.node.INode#dispose()
     */
    public void dispose() throws DisposeException {

	// Find the close the editor related to this annotation set
	Display.getDefault().asyncExec(new Runnable() {
	    public void run() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
		    for (IWorkbenchPage pageRef : window.getPages()) {
			for (IEditorReference editorRef : pageRef.getEditorReferences()) {
			    try {
				if (editorRef.getId().compareTo(CollectionMediaClipAnnotationEditor.ID) == 0) {
				    NodeEditorInput cNodeEditorInput = (NodeEditorInput) editorRef.getEditorInput();
				    if (AnnotationSetNode.this == cNodeEditorInput.getNode()) {
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
    }

    /**
     * Gets the annotation attribute controller.
     * 
     * @param annotation
     *            the annotation
     * @return the annotation attribute controller
     */
    public AnnotationAttributeController getAnnotationAttributeController(Annotation annotation) {
	return annotationAttributeMap.get(annotation);
    }

    /**
     * Gets the annotation list.
     * 
     * @return the annotation list
     */
    public List<Annotation> getAnnotationList() {
	return annotationList.getReadOnlyList();
    }

    /**
     * Gets the annotation list.
     * 
     * @param subject
     *            the subject
     * @return the annotation list
     */
    public List<Annotation> getAnnotationList(Subject subject) {
	// TODO throws exception
	if (subjectList.contains(subject) && subjectAnnotationMap.containsKey(subject)) {
	    return subjectAnnotationMap.get(subject).getReadOnlyList();
	} else {
	    return null;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcommons.jface.node.IParentNode#getChildrenNames()
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
	return ResourceLoader.getIconDescriptor(ANNOTATION_ICON);
    }

    /**
     * Gets the subject list.
     * 
     * @return the subject list
     */
    public List<Subject> getSubjectList() {
	return subjectList.getReadOnlyList();
    }

    /**
     * Hide caption.
     * 
     * @param value
     *            the value
     */
    public void hideCaption(boolean value) {
	EntityManager entityManager = null;
	this.resource.setHideCaption(value);
	try {
	    entityManager = eManagerFactory.createEntityManager();
	    entityManager.getTransaction().begin();
	    entityManager.merge(this.resource);
	    entityManager.getTransaction().commit();

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
     * Initialise.
     */
    private void initialise() {
	EntityManager entityManager = null;
	try {
	    entityManager = eManagerFactory.createEntityManager();
	    entityManager.merge(resource);
	    subjectList = new ObservableList<List<Subject>, Subject>(resource.getSubjectList());
	    List<Annotation> tempAnnotationList = new ArrayList<Annotation>();
	    for (Subject subject : subjectList.getReadOnlyList()) {
		TypedQuery<Annotation> annotationQuery = entityManager.createQuery("SELECT A from Annotation A WHERE A.subject = :subject AND A.annotationSet = :set", Annotation.class);
		annotationQuery.setParameter("subject", subject);
		annotationQuery.setParameter("set", resource);
		IObservableList<List<Annotation>, Annotation> tempList = new ObservableList<List<Annotation>, Annotation>(annotationQuery.getResultList());
		for (Annotation annotation : tempList.getReadOnlyList()) {
		    tempAnnotationList.add(annotation);
		    annotation.setAnnotationSet(resource);
		    annotationAttributeMap.put(annotation, new AnnotationAttributeController(annotation, eManagerFactory));
		}
		subjectAnnotationMap.put(subject, tempList);
	    }
	    resource.setAnnotationList(tempAnnotationList);
	    resource.sortAnnoatationList();
	    annotationList = new ObservableList<List<Annotation>, Annotation>(resource.getAnnotationList());
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
     * Checks if is anntation within clip.
     * 
     * @param annotation
     *            the annotation
     * @return true, if is anntation within clip
     */
    private boolean isAnntationWithinClip(Annotation annotation) {
	long clipStartMilli = this.getResource().getCollectionMediaClip().getStartOffset();
	long clipEndMilli = clipStartMilli + this.getResource().getCollectionMediaClip().getDuration();
	if (annotation instanceof IntervalAnnotation) {
	    IntervalAnnotation iAnnotation = (IntervalAnnotation) annotation;
	    if ((iAnnotation.getStartTime() >= clipStartMilli) && ((iAnnotation.getDuration() + iAnnotation.getStartTime()) <= clipEndMilli)) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    if ((annotation.getStartTime() >= clipStartMilli) && (annotation.getStartTime() <= clipEndMilli)) {
		return true;
	    } else {
		return false;
	    }
	}
    }

    /**
     * Move annotation.
     * 
     * @param annotation
     *            the annotation
     * @param fromSubject
     *            the from subject
     * @param toSubject
     *            the to subject
     * @throws ModelPersistenceException
     *             the model persistence exception
     */
    public void moveAnnotation(Annotation annotation, Subject fromSubject, Subject toSubject) throws ModelPersistenceException {
	EntityManager entityManager = null;
	try {
	    entityManager = eManagerFactory.createEntityManager();
	    entityManager.getTransaction().begin();
	    annotation.setSubject(toSubject);
	    entityManager.merge(annotation);
	    entityManager.getTransaction().commit();
	    subjectAnnotationMap.get(fromSubject).remove(annotation);
	    subjectAnnotationMap.get(toSubject).add(annotation);
	} catch (Exception ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	    throw new ModelPersistenceException("Unable to update annotation", ex);
	} finally {
	    if (entityManager.isOpen()) {
		entityManager.close();
	    }
	}
    }

    /**
     * Removes the annotation list change listener.
     * 
     * @param listener
     *            the listener
     */
    public void removeAnnotationListChangeListener(CollectionChangeListener listener) {
	annotationList.removeChangeListener(listener);
    }

    /**
     * Removes the annotation list change listener.
     * 
     * @param listener
     *            the listener
     * @param subject
     *            the subject
     * @throws Exception
     *             the exception
     */
    public void removeAnnotationListChangeListener(CollectionChangeListener listener, Subject subject) throws Exception {
	// Check the parameter
	if (subject == null) {
	    throw new IllegalArgumentException("Subject parameter is null");
	}
	IObservableList<List<Annotation>, Annotation> tempList = subjectAnnotationMap.get(subject);
	if (tempList != null) {
	    tempList.removeChangeListener(listener);
	}

    }

    /**
     * Removes the annotations.
     * 
     * @param annotations
     *            the annotations
     * @param subject
     *            the subject
     * @throws ModelPersistenceException
     *             the model persistence exception
     */
    public void removeAnnotations(List<Annotation> annotations, Subject subject) throws ModelPersistenceException {
	EntityManager entityManager = null;
	IObservableList<List<Annotation>, Annotation> tempList = subjectAnnotationMap.get(subject);
	if (tempList != null) {
	    try {
		entityManager = eManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		for (Annotation annotation : annotations) {
		    Annotation annoToRemove = entityManager.merge(annotation);
		    entityManager.remove(annoToRemove);
		}
		entityManager.getTransaction().commit();
		for (Annotation annotation : annotations) {
		    annotationAttributeMap.remove(annotation);
		}
		tempList.removeAll(annotations);
		annotationList.removeAll(annotations);
	    } catch (Exception ex) {
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
		logger.log(status);
		throw new ModelPersistenceException("Unable to remove annotation.", ex);
	    } finally {
		if (entityManager.isOpen()) {
		    entityManager.close();
		}
	    }
	}
    }

    /**
     * Removes the subject list change listener.
     * 
     * @param listener
     *            the listener
     */
    public void removeSubjectListChangeListener(CollectionChangeListener listener) {
	subjectList.removeChangeListener(listener);
    }

    /**
     * Removes the subjects.
     * 
     * @param subjects
     *            the subjects
     * @throws ModelPersistenceException
     *             the model persistence exception
     */
    public void removeSubjects(List<Subject> subjects) throws ModelPersistenceException {
	// TODO this needs to be in one transaction
	EntityManager entityManager = null;
	if (subjectList.containsAll(subjects)) {
	    for (Subject subject : subjects) {
		if (!subjectAnnotationMap.containsKey(subject)) {
		    return;
		}
	    }
	    try {
		for (Subject subject : subjects) {
		    this.removeAnnotations(subjectAnnotationMap.get(subject), subject);
		    subjectAnnotationMap.remove(subject);
		}

		entityManager = eManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		subjectList.removeAll(subjects);
		entityManager.merge(resource);
		entityManager.getTransaction().commit();
	    } catch (Exception ex) {
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
		logger.log(status);
		throw new ModelPersistenceException("Unable to remove subjects.", ex);
	    } finally {
		if (entityManager.isOpen()) {
		    entityManager.close();
		}
	    }
	}
    }

    /**
     * Rename annotation set.
     * 
     * @param newName
     *            the new name
     * @throws ModelPersistenceException
     *             the model persistence exception
     */
    public void renameAnnotationSet(String newName) throws ModelPersistenceException {
	EntityManager entityManager = null;
	try {
	    entityManager = eManagerFactory.createEntityManager();
	    entityManager.getTransaction().begin();
	    this.resource.setName(newName);
	    entityManager.merge(this.resource);
	    entityManager.getTransaction().commit();
	    this.setLabel(resource.getName());
	    this.getViewerProvider().getTreeViewer().refresh(this);
	} catch (Exception ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	    throw new ModelPersistenceException("Unable to update annotation set", ex);
	} finally {
	    if (entityManager.isOpen()) {
		entityManager.close();
	    }
	}
    }

    /**
     * Sets the lock.
     * 
     * @param isLock
     *            the new lock
     */
    public void setLock(boolean isLock) {
	this.resource.setLock(isLock);
	EntityManager entityManager = null;
	try {
	    entityManager = eManagerFactory.createEntityManager();
	    entityManager.getTransaction().begin();
	    entityManager.merge(this.resource);
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
     * Update annotation.
     * 
     * @param annotation
     *            the annotation
     * @throws ModelPersistenceException
     *             the model persistence exception
     */
    // public void addAttributeToAnnotation(Annotation annotation,
    // List<Attribute> attributesToAdd) {
    // EntityManager entityManager = null;
    // try {
    // for (Attribute a : attributesToAdd) {
    // if (!annotation.getAttributes().contains(a))
    // annotation.getAttributes().add(a);
    // }
    // entityManager = eManagerFactory.createEntityManager();
    // entityManager.getTransaction().begin();
    // entityManager.merge(annotation);
    // entityManager.getTransaction().commit();
    // } catch (Exception ex) {
    // IStatus status = new
    // Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
    // logger.log(status);
    // } finally {
    // if (entityManager.isOpen())
    // entityManager.close();
    // }
    // }

    public void updateAnnotation(Annotation annotation) throws ModelPersistenceException {
	EntityManager entityManager = null;
	try {
	    entityManager = eManagerFactory.createEntityManager();
	    entityManager.getTransaction().begin();
	    entityManager.merge(annotation);
	    entityManager.getTransaction().commit();
	} catch (Exception ex) {
	    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
	    logger.log(status);
	    throw new ModelPersistenceException("Unable to update annotation", ex);
	} finally {
	    if (entityManager.isOpen()) {
		entityManager.close();
	    }
	}
    }

}
