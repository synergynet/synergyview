/**
 *  File: CsvCollectionClipAnnotationFormatter.java
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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import synergyviewcore.Activator;
import synergyviewcore.annotations.model.Annotation;
import synergyviewcore.annotations.model.AnnotationSet;
import synergyviewcore.annotations.model.IntervalAnnotation;
import synergyviewcore.attributes.model.Attribute;
import synergyviewcore.collections.model.CollectionMediaClip;
import synergyviewcore.subjects.model.Subject;
import synergyviewcore.util.DateTimeHelper;


/**
 * The Class TsvCollectionClipAnnotationFormatter.
 *
 * @author phyo
 */
public class TsvCollectionClipAnnotationFormatter implements
		ICollectionClipAnnotationFormatter {
	
	/** The logger. */
	private final ILog logger;
	
	/**
	 * Instantiates a new tsv collection clip annotation formatter.
	 */
	public TsvCollectionClipAnnotationFormatter() {
		logger = Activator.getDefault().getLog();
	}
	
	/* (non-Javadoc)
	 * @see synergyviewcore.collections.format.ICollectionClipAnnotationFormatter#export(java.util.List, java.io.OutputStream)
	 */
	public String export(CollectionMediaClip clipsToExport) throws Exception {	
		try {
			StringBuilder str = new StringBuilder();
			for (AnnotationSet set : clipsToExport.getAnnotationSetList()) {
				Map<Subject, SubjectAnnotationCount> subjectMap = new HashMap<Subject, SubjectAnnotationCount>();
				str.append(String.format("%s - %s (%s)\n", set.getName(), set.getCollectionMediaClip().getClipName(), set.getCollectionMediaClip().getCollection().getName()));
				str.append(" \n");
				str.append(String.format("Clip Duration:\t%s\n", DateTimeHelper.getHMSFromMilliFormatted(set.getCollectionMediaClip().getDuration())));
				str.append(" \n");		
				str.append("Start Time\tDuration\tSubject\tAnnotation\tAttributes\n");
				List<Annotation> annotationList = set.getAnnotationList();
				annotationList = sortAnnoatationList(annotationList);
				
				for (Annotation annotation : annotationList) {
					SubjectAnnotationCount subjectAnnotationCount = subjectMap.get(annotation.getSubject());
					if (subjectAnnotationCount == null) {
						subjectAnnotationCount = new SubjectAnnotationCount();
						subjectMap.put(annotation.getSubject(), subjectAnnotationCount);
					}
					String txt = annotation.getText().replaceAll("\t", "");
					txt = txt.replaceAll("\n", "");
					txt = txt.replaceAll("\r", "");
					String formatedLine;
					subjectAnnotationCount.incrementCount();
					if (annotation instanceof IntervalAnnotation) {
						IntervalAnnotation intervalAnnotation = (IntervalAnnotation) annotation;
						subjectAnnotationCount.addTotalMilliSec(intervalAnnotation.getDuration());						
						StringBuilder attributes = new StringBuilder();	
						
						List<Attribute> attributesList = intervalAnnotation.getAttributes();
						attributesList = sortAttributeList(attributesList);
						
						for (int i = 0; i < attributesList.size(); i++) {
							attributes.append(attributesList.get(i).getName());
							if (i != attributesList.size() -1){
								attributes.append(", ");								
							}else{
								attributes.append("\t ");										
							}
							if (!subjectAnnotationCount.getAttributeDuration().containsKey(attributesList.get(i))) {
								AttributeInfo info = new AttributeInfo();
								info.setTotalMilliSec(intervalAnnotation.getDuration());
								info.incrementCount();
								subjectAnnotationCount.getAttributeDuration().put(attributesList.get(i), info);
							} else {
								AttributeInfo info = subjectAnnotationCount.getAttributeDuration().get(attributesList.get(i));
								info.setTotalMilliSec(info.getTotalMilliSec() + intervalAnnotation.getDuration());
								info.incrementCount();
								subjectAnnotationCount.getAttributeDuration().put(attributesList.get(i), info);
							}
						}
						String attributesStr = attributes.toString();
						if (!attributesStr.isEmpty())attributesStr = attributesStr.substring(0,attributesStr.length() - 2);
						formatedLine = String.format("%s\t%s\t%s\t%s\t%s\n", intervalAnnotation.getFormattedStartTime(), 
								intervalAnnotation.getFormattedDuration(), intervalAnnotation.getSubject().getName(), txt, attributesStr) ;
					} else 
						formatedLine = String.format("%s\t-\t%s\t", annotation.getFormattedStartTime(),annotation.getSubject().getName()) + txt + "\n";
					str.append(formatedLine);
				}
				str.append(" \n");
				str.append("--------------------------------------------\n");
				str.append("Summary\n");
				str.append("--------------------------------------------\n");
				for (Map.Entry<Subject, SubjectAnnotationCount> entry : subjectMap.entrySet()) {
					str.append("Subject:\t" + entry.getKey().getName() + "\n");	
					str.append("Annotation count:\t" + entry.getValue().getCount() + "\n");
					str.append(String.format("Total duration:\t%s\n", DateTimeHelper.getHMSFromMilliFormatted(entry.getValue().getTotalMilliSec())));
					str.append(" \n");
					str.append("Attribute\tOccurrences\tTotal Time\n");
					for (Map.Entry<Attribute, AttributeInfo> attributeEntry : entry.getValue().getAttributeDuration().entrySet()) {
						str.append(String.format("%s\t%d\t%s\n",attributeEntry.getKey().getName(), 
								attributeEntry.getValue().getCount(), DateTimeHelper.getHMSFromMilliFormatted(attributeEntry.getValue().getTotalMilliSec())));
					}

					str.append(" \n");
					str.append("--------------------------------------------\n");
				}
				
			}
			return str.toString();
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			throw new Exception("Unable to parse the data", ex);
		}
	}
	
	
	/**
	 * Sort annoatation list.
	 *
	 * @param annotationList the annotation list
	 * @return the list
	 */
	public List<Annotation> sortAnnoatationList(List<Annotation> annotationList) {
		Collections.sort(annotationList, new Comparator<Annotation>() {

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
		return annotationList;
	}
	
	/**
	 * Sort attribute list.
	 *
	 * @param attributesToSort the attributes to sort
	 * @return the list
	 */
	public List<Attribute> sortAttributeList(List<Attribute> attributesToSort) {
		Collections.sort(attributesToSort, new Comparator<Attribute>() {
			public int compare(Attribute arg0, Attribute arg1) {				
				return arg0.getName().compareTo(arg1.getName());  
			}
			
		});
		return attributesToSort;
	}
	

	/**
	 * The Class SubjectAnnotationCount.
	 */
	private static class SubjectAnnotationCount {
		
		/** The count. */
		private int count;
		
		/** The total milli sec. */
		private long totalMilliSec;
		
		/** The attribute duration. */
		private Map<Attribute, AttributeInfo> attributeDuration = new HashMap<Attribute, AttributeInfo>();
		
		/**
		 * Increment count.
		 */
		public void incrementCount() {
			count++;
		}
		
		/**
		 * Gets the count.
		 *
		 * @return the count
		 */
		public int getCount() {
			return count;
		}
		
		/**
		 * Adds the total milli sec.
		 *
		 * @param totalMilliSec the total milli sec
		 */
		public void addTotalMilliSec(long totalMilliSec) {
			this.totalMilliSec = this.totalMilliSec + totalMilliSec;
		}
		
		/**
		 * Gets the total milli sec.
		 *
		 * @return the total milli sec
		 */
		public long getTotalMilliSec() {
			return totalMilliSec;
		}

		/**
		 * Gets the attribute duration.
		 *
		 * @return the attribute duration
		 */
		public Map<Attribute, AttributeInfo> getAttributeDuration() {
			return attributeDuration;
		}
	}
	
	/**
	 * The Class AttributeInfo.
	 */
	private static class AttributeInfo {
		
		/** The count. */
		private int count = 0;
		
		/** The total milli sec. */
		private long totalMilliSec;

		/**
		 * Gets the count.
		 *
		 * @return the count
		 */
		public int getCount() {
			return count;
		}
		
		/**
		 * Sets the total milli sec.
		 *
		 * @param totalMilliSec the new total milli sec
		 */
		public void setTotalMilliSec(long totalMilliSec) {
			this.totalMilliSec = totalMilliSec;
		}
		
		/**
		 * Gets the total milli sec.
		 *
		 * @return the total milli sec
		 */
		public long getTotalMilliSec() {
			return totalMilliSec;
		}
		
		/**
		 * Increment count.
		 */
		public void incrementCount() {
			count++;
		}
	}
}
