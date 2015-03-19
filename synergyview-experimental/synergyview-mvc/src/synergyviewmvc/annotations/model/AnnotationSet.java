package synergyviewmvc.annotations.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import synergyviewmvc.collections.model.CollectionMediaClip;
import synergyviewmvc.model.PersistenceModelObject;
import synergyviewmvc.subjects.model.Subject;

@Entity
public class AnnotationSet extends PersistenceModelObject
{
	
	@OneToMany
	private List<Subject> subjectList = new ArrayList<Subject>();
	public static final String PROP_SUBJECTLIST = "subjectList";
	
	private String name;
	public static final String PROP_NAME = "name";
	
	public AnnotationSet() {
		//
	}
	
	public static final String PROP_ANNOTATIONLIST = "annotationList";
	@OneToMany(mappedBy="annotationSet", cascade=CascadeType.ALL)
	private List<Annotation> annotationList = new ArrayList<Annotation>();
	
	@ManyToOne
	private CollectionMediaClip collectionMediaClip;
	public static final String PROP_COLLECTIONMEDIACLIP = "collectionMediaClip";

	/**
	 * @param subjects the subjects to set
	 */
	public void setSubjectList(List<Subject> subjectList) {
		this.firePropertyChange(PROP_SUBJECTLIST, this.subjectList, this.subjectList = subjectList);
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
		this.firePropertyChange(PROP_ANNOTATIONLIST, this.annotationList, this.annotationList = annotationList);
	}

	public List<Annotation> getAnnotationList() {
		return annotationList;
	}
	

	
}
