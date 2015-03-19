package synergyviewcore.annotations.format;

import java.io.InputStream;
import java.io.OutputStream;

import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.model.ModelObject;
import synergyviewcore.subjects.model.Subject;

public class AnalysisSubRipFormatter extends ModelObject implements IAnnotationFormatter {

	/* (non-Javadoc)
	 * @see synergyviewcore.annotations.format.IFormatter#write(synergyviewcore.annotations.model.AnnotationSetNode, synergyviewcore.subjects.model.Subject, java.io.OutputStream)
	 */
	public void write(AnnotationSetNode annotationSetNode,
			Subject subjectToSave, OutputStream outStream) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.annotations.format.IFormatter#read(synergyviewcore.annotations.model.AnnotationSetNode, synergyviewcore.subjects.model.Subject, java.io.InputStream)
	 */
	public void read(AnnotationSetNode annotationSetNode,
			Subject subjectToLoad, InputStream inStream) {
		// TODO Auto-generated method stub
		
	}
}
