package synergyviewmvc.annotations.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import synergyviewmvc.attributes.model.Attribute;
import synergyviewmvc.model.PersistenceModelObject;
import synergyviewmvc.subjects.model.Subject;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class Annotation extends PersistenceModelObject {
	
	public static final String PROP_STARTTIME = "startTime"; 
	private int startTime;
	
	@OneToMany(fetch=FetchType.EAGER)
	private List<Attribute> attributes = new ArrayList<Attribute>();
	
	@OneToOne
	private Subject subject;
	public static final String PROP_SUBJECT = "subject";
	
	@ManyToOne
	private AnnotationSet annotationSet;
	
	public static final String PROP_TEXT = "text";
	private String text;
	
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(int startTime) {
		this.firePropertyChange(PROP_STARTTIME,this.startTime, this.startTime = startTime);
	}
	
	/**
	 * @return the startTime
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.firePropertyChange(PROP_TEXT, this.text, this.text = text);
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	public void setAnnotationSet(AnnotationSet annotationSet) {
		this.annotationSet = annotationSet;
	}
	
	public AnnotationSet getAnnotationSet() {
		return annotationSet;
	}
	
	public void setSubject(Subject subject) {
		this.firePropertyChange(PROP_SUBJECT, this.subject, this.subject = subject);
	}
	
	public Subject getSubject() {
		return subject;
	}

	
	
}
