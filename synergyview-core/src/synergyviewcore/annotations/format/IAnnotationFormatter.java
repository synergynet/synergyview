package synergyviewcore.annotations.format;

import java.io.InputStream;
import java.io.OutputStream;

import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.subjects.model.Subject;

/**
 * The Interface IAnnotationFormatter.
 */
public interface IAnnotationFormatter {
	
	/**
	 * Read.
	 * 
	 * @param annotationSetNode
	 *            the annotation set node
	 * @param subjectToLoad
	 *            the subject to load
	 * @param inStream
	 *            the in stream
	 * @throws Exception
	 *             the exception
	 */
	void read(AnnotationSetNode annotationSetNode, Subject subjectToLoad,
			InputStream inStream) throws Exception;
	
	/**
	 * Write.
	 * 
	 * @param annotationSetNode
	 *            the annotation set node
	 * @param subjectToSave
	 *            the subject to save
	 * @param outStream
	 *            the out stream
	 * @throws Exception
	 *             the exception
	 */
	void write(AnnotationSetNode annotationSetNode, Subject subjectToSave,
			OutputStream outStream) throws Exception;
}
