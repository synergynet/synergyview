/**
 *  File: XmlAnnotationSetFormatter.java
 *  Copyright (c) 2011
 *  phyo
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package synergyviewcore.collections.format;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import synergyviewcore.annotations.model.Annotation;
import synergyviewcore.annotations.model.AnnotationSet;
import synergyviewcore.attributes.model.Attribute;
import synergyviewcore.collections.model.Collection;
import synergyviewcore.collections.model.CollectionMediaClip;
import synergyviewcore.collections.model.CollectionNode;
import synergyviewcore.projects.model.ProjectNode;
import synergyviewcore.subjects.model.Subject;

/**
 * @author phyo
 * 
 */
public class XmlCollectionFormatter implements
		ICollectionFormatter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * synergyviewcore.annotations.format.IAnnotationSetFormatter
	 * #
	 * write(synergyviewcore.collections.model.CollectionMediaClip
	 * , java.io.OutputStream)
	 */
	public void write(Collection collection, OutputStream outStream)
			throws Exception {
		XMLEncoder e = new XMLEncoder(outStream);
		try {
			e.writeObject(collection.getCollectionMediaClipList());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Unable to save the collection clips!", ex);
		} finally {
			e.close();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * synergyviewcore.annotations.format.IAnnotationSetFormatter
	 * #read(synergyviewcore.collections.model.
	 * CollectionMediaClipNode, java.io.InputStream)
	 */
	public void read(CollectionNode collectionNode, InputStream inStream)
			throws Exception {
		XMLDecoder d = new XMLDecoder(inStream);
		try {
			if (collectionNode.getResource().getCollectionMediaList().isEmpty())
				throw new Exception("No media found in the collection!");
			@SuppressWarnings("unchecked")
			List<CollectionMediaClip> collectionMediaClipList = (List<CollectionMediaClip>) d
					.readObject();
			List<CollectionMediaClip> newCollectionMediaClipList = new ArrayList<CollectionMediaClip>();
			for (CollectionMediaClip clip : collectionMediaClipList) {
				CollectionMediaClip newClip = new CollectionMediaClip();
				newClip.setClipName(clip.getClipName());
				newClip.setId(UUID.randomUUID().toString());
				newClip.setDuration(clip.getDuration());
				newClip.setStartOffset(clip.getStartOffset());
				newClip.setCollection(collectionNode.getResource());
				newCollectionMediaClipList.add(newClip);
				
				for (AnnotationSet annotationSet : clip.getAnnotationSetList()) {
					annotationSet.setCollectionMediaClip(newClip);
					annotationSet.setId(UUID.randomUUID().toString());

					List<Subject> newSubjectList = new ArrayList<Subject>();
					List<Annotation> newAnnotationtList = new ArrayList<Annotation>();
					for (Subject subject : annotationSet.getSubjectList()) {
						Subject currentSubject = ((ProjectNode) collectionNode
								.getLastParent()).getSubjectRootNode()
								.getSubject(subject.getId());
						if (currentSubject==null) {
							currentSubject = ((ProjectNode) collectionNode
									.getLastParent()).getSubjectRootNode()
									.getSubjectByName(subject.getName());
							if (currentSubject==null)
							throw new Exception("Unable to find the subject used in the collection clip for annotation list.");
						}
						newSubjectList.add(currentSubject);
					}
					for (Annotation annotation : annotationSet
							.getAnnotationList()) {
						
						
						//Adding existing subjects
						Subject currentSubject = ((ProjectNode) collectionNode
								.getLastParent()).getSubjectRootNode()
								.getSubject(annotation.getSubject().getId());
						
						if (currentSubject==null) {
							currentSubject = ((ProjectNode) collectionNode
									.getLastParent()).getSubjectRootNode()
									.getSubjectByName(annotation.getSubject().getName());
							if (currentSubject==null)
							throw new Exception("Unable to find the subject used in the collection clip for annotation.");
						}
						
						annotation.setSubject(currentSubject);
						
						//Adding existing attributes
						List<Attribute> currentAttributeList = new ArrayList<Attribute>();
						for (Attribute attribute : annotation.getAttributes()) {
							Attribute currentAttribute = ((ProjectNode) collectionNode
							.getLastParent()).getProjectAttributeRootNode().getAttribute(attribute);
							if (currentAttribute==null) {
								currentAttribute = ((ProjectNode) collectionNode
										.getLastParent()).getProjectAttributeRootNode().getAttributeByName(attribute.getName());
							}
							currentAttributeList.add(currentAttribute);
						}
						annotation.setAttributes(currentAttributeList);
						
						
						annotation.setId(UUID.randomUUID().toString());
						newAnnotationtList.add(annotation);
					}
					annotationSet.setSubjectList(newSubjectList);
					annotationSet.setAnnotationList(newAnnotationtList);
					newClip.getAnnotationSetList().add(annotationSet);
				}
			}
			collectionNode.addClip(newCollectionMediaClipList);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Unable to load the collection clips!", ex);
		} finally {
			d.close();
		}
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.collections.format.ICollectionMediaClipFormatter#export(java.util.List, java.io.OutputStream)
	 */
	public void export(List<CollectionMediaClip> clipsToExport,
			OutputStream outStream) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
