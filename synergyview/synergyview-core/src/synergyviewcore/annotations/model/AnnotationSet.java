package synergyviewcore.annotations.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;

import synergyviewcore.collections.model.CollectionMediaClip;
import synergyviewcore.model.PersistenceModelObject;
import synergyviewcore.subjects.model.Subject;


/**
 * The Class AnnotationSet.
 */
@Entity
public class AnnotationSet extends PersistenceModelObject
{
	
	/** The Constant PROP_SUBJECTLIST. */
	public static final String PROP_SUBJECTLIST = "subjectList";
	
	/** The subject list. */
	@OneToMany
	@OrderColumn(name="SUBJECTSORDER")
	private List<Subject> subjectList = new ArrayList<Subject>();
	
	/** The name. */
	private String name;
	
	/** The Constant PROP_NAME. */
	public static final String PROP_NAME = "name";
	
	/** The lock. */
	@Column(name="COMPLETED")
	private boolean lock = false;
	
	/** The Constant PROP_LOCK. */
	public static final String PROP_LOCK = "lock";
	
	/**
	 * Sets the lock.
	 *
	 * @param lock the new lock
	 */
	public void setLock(boolean lock) {
		this.firePropertyChange(PROP_LOCK, this.lock, this.lock = lock);
	}
	
	/**
	 * Checks if is lock.
	 *
	 * @return true, if is lock
	 */
	public boolean isLock() {
		return lock;
	}
	
	
	/** The hide caption. */
	private boolean hideCaption = false;
	
	/** The Constant PROP_HIDECAPTION. */
	public static final String PROP_HIDECAPTION = "hideCaption";
	
	/**
	 * Sets the hide caption.
	 *
	 * @param hideCaption the new hide caption
	 */
	public void setHideCaption(boolean hideCaption) {
		this.firePropertyChange(PROP_HIDECAPTION, this.hideCaption, this.hideCaption = hideCaption);
	}
	
	/**
	 * Checks if is hide caption.
	 *
	 * @return true, if is hide caption
	 */
	public boolean isHideCaption() {
		return hideCaption;
	}
	
	/**
	 * Instantiates a new annotation set.
	 */
	public AnnotationSet() {
		//
	}
	
	/** The Constant PROP_ANNOTATIONLIST. */
	public static final String PROP_ANNOTATIONLIST = "annotationList";
	
	/** The annotation list. */
	@OneToMany(mappedBy="annotationSet", cascade=CascadeType.ALL)
	@OrderBy("startTime ASC")
	private List<Annotation> annotationList = new ArrayList<Annotation>();
	
	/** The collection media clip. */
	@ManyToOne
	private CollectionMediaClip collectionMediaClip;
	
	/** The Constant PROP_COLLECTIONMEDIACLIP. */
	public static final String PROP_COLLECTIONMEDIACLIP = "collectionMediaClip";

	/**
	 * Sets the subject list.
	 *
	 * @param subjectList the new subject list
	 */
	public void setSubjectList(List<Subject> subjectList) {
		this.firePropertyChange(PROP_SUBJECTLIST, null, this.subjectList = subjectList);
	}

	/**
	 * Gets the subject list.
	 *
	 * @return the subjects
	 */
	public List<Subject> getSubjectList() {
		return subjectList;
	}

	/**
	 * Sets the collection media clip.
	 *
	 * @param collectionMediaClip the collectionMediaClip to set
	 */
	public void setCollectionMediaClip(CollectionMediaClip collectionMediaClip) {
		this.firePropertyChange(PROP_COLLECTIONMEDIACLIP, this.collectionMediaClip, this.collectionMediaClip = collectionMediaClip);
	}

	/**
	 * Gets the collection media clip.
	 *
	 * @return the collectionMediaClip
	 */
	public CollectionMediaClip getCollectionMediaClip() {
		return collectionMediaClip;
	}



	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the annotation list.
	 *
	 * @param annotationList the new annotation list
	 */
	public void setAnnotationList(List<Annotation> annotationList) {
		this.firePropertyChange(PROP_ANNOTATIONLIST, null, this.annotationList = annotationList);
	}

	/**
	 * Gets the annotation list.
	 *
	 * @return the annotation list
	 */
	@OrderBy(value="startTime")
	public List<Annotation> getAnnotationList() {
		return annotationList;
	}
	
	/**
	 * Sort annoatation list.
	 */
	public void sortAnnoatationList() {
		Collections.sort(this.annotationList, new Comparator<Annotation>() {

			public int compare(Annotation arg0, Annotation arg1) {
				long startTime1 = arg0.getMilliSec() + (arg0.getSec() * 1000) + (arg0.getMi() * 60 * 1000) + (arg0.getHr() * 60 * 60 * 1000);
				long startTime2 = arg1.getMilliSec() + (arg1.getSec() * 1000) + (arg1.getMi() * 60 * 1000) + (arg1.getHr() * 60 * 60 * 1000);
				if(startTime1 > startTime2)
		            return 1;
		        else if(startTime1 < startTime2)
		            return -1;
		        else
		            return 0;    
			}
			
		});
	}
	
}
