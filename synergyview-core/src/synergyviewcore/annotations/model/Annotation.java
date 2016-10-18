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

/**
 * The Class Annotation.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Annotation extends PersistenceModelObject {

    /** The Constant PROP_STARTTIME. */
    public static final String PROP_STARTTIME = "startTime";

    /** The Constant PROP_SUBJECT. */
    public static final String PROP_SUBJECT = "subject";

    /** The Constant PROP_TEXT. */
    public static final String PROP_TEXT = "text";

    /** The annotation set. */
    @ManyToOne
    private AnnotationSet annotationSet;

    /** The attributes. */
    @OneToMany(fetch = FetchType.EAGER)
    private List<Attribute> attributes = new ArrayList<Attribute>();

    /** The hr. */
    private int hr;

    /** The mi. */
    private int mi;

    /** The milli sec. */
    private int milliSec;
    //
    // @Lob
    // public static final String PROP_IMAGEDATA = "imageData";
    // private byte[] imageData;

    /** The sec. */
    private int sec;

    /** The start time. */
    private long startTime;

    /** The subject. */
    @OneToOne
    private Subject subject;

    /** The text. */
    @Lob
    private String text;

    /**
     * Gets the annotation set.
     * 
     * @return the annotation set
     */
    public AnnotationSet getAnnotationSet() {
	return annotationSet;
    }

    /**
     * Gets the attributes.
     * 
     * @return the attributes
     */
    public List<Attribute> getAttributes() {
	return attributes;
    }

    /**
     * Gets the formatted start time.
     * 
     * @return the formatted start time
     */
    public String getFormattedStartTime() {
	return String.format("%02d:%02d:%02d", hr, mi, sec);
    }

    /**
     * Gets the hr.
     * 
     * @return the hr
     */
    public int getHr() {
	return hr;
    }

    /**
     * Gets the mi.
     * 
     * @return the mi
     */
    public int getMi() {
	return mi;
    }

    /**
     * Gets the milli sec.
     * 
     * @return the milli sec
     */
    public int getMilliSec() {
	return milliSec;
    }

    /**
     * Gets the sec.
     * 
     * @return the sec
     */
    public int getSec() {
	return sec;
    }

    /**
     * Gets the start time.
     * 
     * @return the startTime
     */
    public long getStartTime() {
	return startTime;
    }

    /**
     * Gets the subject.
     * 
     * @return the subject
     */
    public Subject getSubject() {
	return subject;
    }

    /**
     * Gets the text.
     * 
     * @return the text
     */
    public String getText() {
	return text;
    }

    /**
     * Sets the annotation set.
     * 
     * @param annotationSet
     *            the new annotation set
     */
    public void setAnnotationSet(AnnotationSet annotationSet) {
	this.annotationSet = annotationSet;
    }

    /**
     * Sets the attributes.
     * 
     * @param attributes
     *            the new attributes
     */
    public void setAttributes(List<Attribute> attributes) {
	this.attributes = attributes;
    }

    /**
     * Sets the hr.
     * 
     * @param hr
     *            the new hr
     */
    public void setHr(int hr) {
	this.hr = hr;
    }

    /**
     * Sets the mi.
     * 
     * @param mi
     *            the new mi
     */
    public void setMi(int mi) {
	this.mi = mi;
    }

    /**
     * Sets the milli sec.
     * 
     * @param milliSec
     *            the new milli sec
     */
    public void setMilliSec(int milliSec) {
	this.milliSec = milliSec;
    }

    /**
     * Sets the sec.
     * 
     * @param sec
     *            the new sec
     */
    public void setSec(int sec) {
	this.sec = sec;
    }

    /**
     * Sets the start time.
     * 
     * @param startTime
     *            the startTime to set
     */
    public void setStartTime(long startTime) {
	this.firePropertyChange(PROP_STARTTIME, this.startTime, this.startTime = startTime);
    }

    /**
     * Sets the subject.
     * 
     * @param subject
     *            the new subject
     */
    public void setSubject(Subject subject) {
	this.firePropertyChange(PROP_SUBJECT, this.subject, this.subject = subject);
    }

    /**
     * Sets the text.
     * 
     * @param text
     *            the text to set
     */
    public void setText(String text) {
	this.firePropertyChange(PROP_TEXT, this.text, this.text = text);
    }

    // /**
    // * @param imageData the imageData to set
    // */
    // public void setImageData(byte[] imageData) {
    // this.firePropertyChange(PROP_IMAGEDATA, this.imageData, this.imageData =
    // imageData);
    // }
    //
    // /**
    // * @return the imageData
    // */
    // public byte[] getImageData() {
    // return imageData;
    // }
    //

}
