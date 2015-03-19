package synergyviewcore.annotations.format;

import java.io.InputStream;
import java.io.OutputStream;

import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.subjects.model.Subject;

public interface IAnnotationFormatter {
	void write(AnnotationSetNode annotationSetNode, Subject subjectToSave, OutputStream outStream) throws Exception;
	void read(AnnotationSetNode annotationSetNode, Subject subjectToLoad, InputStream inStream) throws Exception;
}
