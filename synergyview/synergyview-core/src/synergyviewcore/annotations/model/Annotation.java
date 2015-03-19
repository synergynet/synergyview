package synergyviewcore.annotations.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import synergyviewcore.attributes.model.Attribute;
import synergyviewcore.model.PersistenceModelObject;
import synergyviewcore.subjects.model.Subject;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class Annotation extends PersistenceModelObject {
	
	public static final String PROP_STARTTIME = "startTime"; 
	private long startTime;
	private int hr;
	private int mi;
	private int sec;
	private int milliSec;
//	
//	@Lob
//	public static final String PROP_IMAGEDATA = "imageData"; 
//	private byte[] imageData;

	
	@OneToMany(fetch=FetchType.EAGER)
	private List<Attribute> attributes = new ArrayList<Attribute>();
	
	@OneToOne
	private Subject subject;
	public static final String PROP_SUBJECT = "subject";
	
	@ManyToOne
	private AnnotationSet annotationSet;
	
	public static final String PROP_TEXT = "text";
	@Lob
	private String text;
	
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.firePropertyChange(PROP_STARTTIME,this.startTime, this.startTime = startTime);
	}
	
	/**
	 * @return the startTime
	 */
	public long getStartTime() {
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


	public void setSec(int sec) {
		this.sec = sec;
	}

	public int getSec() {
		return sec;
	}

	public void setMilliSec(int milliSec) {
		this.milliSec = milliSec;
	}

	public int getMilliSec() {
		return milliSec;
	}

	public void setMi(int mi) {
		this.mi = mi;
	}

	public int getMi() {
		return mi;
	}

	public void setHr(int hr) {
		this.hr = hr;
	}

	public int getHr() {
		return hr;
	}

	public String getFormattedStartTime() {
		return  String.format("%02d:%02d:%02d", hr, mi, sec);
	}

//	/**
//	 * @param imageData the imageData to set
//	 */
//	public void setImageData(byte[] imageData) {
//		this.firePropertyChange(PROP_IMAGEDATA, this.imageData, this.imageData = imageData);
//	}
//
//	/**
//	 * @return the imageData
//	 */
//	public byte[] getImageData() {
//		return imageData;
//	}
//	

	
	
}
