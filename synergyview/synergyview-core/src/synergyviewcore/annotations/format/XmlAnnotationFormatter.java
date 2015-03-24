/**
 * File: XmlFormatter.java Copyright (c) 2010 phyo This program is free
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

package synergyviewcore.annotations.format;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import synergyviewcore.Activator;
import synergyviewcore.annotations.model.Annotation;
import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.subjects.model.Subject;

/**
 * The Class XmlAnnotationFormatter.
 * 
 * @author phyo
 */
public class XmlAnnotationFormatter implements IAnnotationFormatter {
	
	/** The logger. */
	private final ILog logger;
	
	/**
	 * Instantiates a new xml annotation formatter.
	 */
	public XmlAnnotationFormatter() {
		logger = Activator.getDefault().getLog();
	}
	
	@SuppressWarnings("unchecked")
	public void read(AnnotationSetNode annotationSetNode,
			Subject subjectToLoad, InputStream inStream) throws Exception {
		XMLDecoder d = new XMLDecoder(inStream);
		try {
			List<Annotation> annotationList = (List<Annotation>) d.readObject();
			for (Annotation annotation : annotationList) {
				annotation.setId(UUID.randomUUID().toString());
			}
			annotationSetNode.addAnnotations(annotationList, subjectToLoad);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
			throw new Exception("Unable to load the annotations!", ex);
		} finally {
			d.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.annotations.format.IFormatter#read(synergyviewcore.
	 * annotations.model.AnnotationSetNode,
	 * synergyviewcore.subjects.model.Subject, java.io.InputStream)
	 */
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.annotations.format.IFormatter#write(synergyviewcore.
	 * annotations.model.AnnotationSetNode,
	 * synergyviewcore.subjects.model.Subject, java.io.OutputStream)
	 */
	public void write(AnnotationSetNode annotationSetNode,
			Subject subjectToSave, OutputStream outStream) throws Exception {
		XMLEncoder e = new XMLEncoder(outStream);
		try {
			List<Annotation> annotationList = annotationSetNode
					.getAnnotationList(subjectToSave);
			e.writeObject(annotationList);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
			throw new Exception("Unable to save the annotations!", ex);
		} finally {
			e.close();
		}
		
	}
	
}
