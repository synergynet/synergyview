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

@Entity
public class AnnotationSet extends PersistenceModelObject
{
	
	public static final String PROP_SUBJECTLIST = "subjectList";
	@OneToMany
	@OrderColumn(name="SUBJECTSORDER")
	private List<Subject> subjectList = new ArrayList<Subject>();
	
	private String name;
	public static final String PROP_NAME = "name";
	
	@Column(name="COMPLETED")
	private boolean lock = false;
	
	public static final String PROP_LOCK = "lock";
	
	public void setLock(boolean lock) {
		this.firePropertyChange(PROP_LOCK, this.lock, this.lock = lock);
	}
	public boolean isLock() {
		return lock;
	}
	
	
	private boolean hideCaption = false;
	public static final String PROP_HIDECAPTION = "hideCaption";
	public void setHideCaption(boolean hideCaption) {
		this.firePropertyChange(PROP_HIDECAPTION, this.hideCaption, this.hideCaption = hideCaption);
	}
	
	public boolean isHideCaption() {
		return hideCaption;
	}
	
	public AnnotationSet() {
		//
	}
	
	public static final String PROP_ANNOTATIONLIST = "annotationList";
	@OneToMany(mappedBy="annotationSet", cascade=CascadeType.ALL)
	@OrderBy("startTime ASC")
	private List<Annotation> annotationList = new ArrayList<Annotation>();
	
	@ManyToOne
	private CollectionMediaClip collectionMediaClip;
	public static final String PROP_COLLECTIONMEDIACLIP = "collectionMediaClip";

	/**
	 * @param subjects the subjects to set
	 */
	public void setSubjectList(List<Subject> subjectList) {
		this.firePropertyChange(PROP_SUBJECTLIST, null, this.subjectList = subjectList);
	}

	/**
	 * @return the subjects
	 */
	public List<Subject> getSubjectList() {
		return subjectList;
	}

	/**
	 * @param collectionMediaClip the collectionMediaClip to set
	 */
	public void setCollectionMediaClip(CollectionMediaClip collectionMediaClip) {
		this.firePropertyChange(PROP_COLLECTIONMEDIACLIP, this.collectionMediaClip, this.collectionMediaClip = collectionMediaClip);
	}

	/**
	 * @return the collectionMediaClip
	 */
	public CollectionMediaClip getCollectionMediaClip() {
		return collectionMediaClip;
	}



	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setAnnotationList(List<Annotation> annotationList) {
		this.firePropertyChange(PROP_ANNOTATIONLIST, null, this.annotationList = annotationList);
	}

	@OrderBy(value="startTime")
	public List<Annotation> getAnnotationList() {
		return annotationList;
	}
	
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
