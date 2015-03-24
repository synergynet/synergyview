/**
 * File: SubtitleUserRowModel.java Copyright (c) 2010 phyo This program is free
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

package synergyviewcore.annotations.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import synergyviewcommons.collections.CollectionChangeEvent;
import synergyviewcommons.collections.CollectionChangeListener;
import synergyviewcommons.collections.CollectionDiffEntry;
import synergyviewcore.Activator;
import synergyviewcore.annotations.model.Annotation;
import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.annotations.model.IntervalAnnotation;
import synergyviewcore.subjects.model.Subject;
import synergyviewcore.util.DateTimeHelper;
import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;

/**
 * The Class SubjectRowModel.
 * 
 * @author phyo
 */
public class SubjectRowModel extends DefaultTimeBarRowModel implements
		CollectionChangeListener {
	
	/** The annotation set node. */
	private AnnotationSetNode annotationSetNode;
	
	/** The header. */
	private DefaultRowHeader header;
	
	/** The label listener. */
	private PropertyChangeListener labelListener;
	
	/** The logger. */
	private ILog logger;
	
	/** The map. */
	private Map<Annotation, AnnotationIntervalImpl> map = new HashMap<Annotation, AnnotationIntervalImpl>();
	
	/** The marker. */
	private TimeBarMarkerImpl marker;
	
	/** The owner. */
	private DefaultTimeBarModel owner;
	
	/** The start clip date. */
	private JaretDate startClipDate;
	
	/** The subject. */
	private Subject subject;
	
	/**
	 * Instantiates a new subject row model.
	 */
	protected SubjectRowModel() {
		super();
		
	}
	
	/**
	 * Instantiates a new subject row model.
	 * 
	 * @param startClipDate
	 *            the start clip date
	 * @param annotationSetNode
	 *            the annotation set node
	 * @param header
	 *            the header
	 * @param owner
	 *            the owner
	 * @param subject
	 *            the subject
	 * @param marker
	 *            the marker
	 */
	public SubjectRowModel(JaretDate startClipDate,
			AnnotationSetNode annotationSetNode, DefaultRowHeader header,
			DefaultTimeBarModel owner, Subject subject, TimeBarMarkerImpl marker) {
		super(header);
		logger = Activator.getDefault().getLog();
		this.subject = subject;
		this.owner = owner;
		this.marker = marker;
		this.startClipDate = startClipDate;
		this.annotationSetNode = annotationSetNode;
		addInitialAnnotationIntervals();
		this.header = header;
		labelListener = new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent arg0) {
				SubjectRowModel.this.header.setLabel((String) arg0
						.getNewValue());
			}
			
		};
		subject.addPropertyChangeListener(Subject.PROP_NAME, labelListener);
	}
	
	/**
	 * Adds the annotation interval.
	 * 
	 * @param annotation
	 *            the annotation
	 */
	private void addAnnotationInterval(Annotation annotation) {
		
		if (annotation instanceof IntervalAnnotation) {
			IntervalAnnotation intervalAnnotation = (IntervalAnnotation) annotation;
			
			JaretDate startDate = null;
			if (intervalAnnotation.getStartTime() > 0) {
				long startIntervalMilli = intervalAnnotation.getStartTime()
						- (DateTimeHelper.getMilliFromJaretDate(startClipDate));
				startDate = startClipDate.copy().advanceMillis(
						startIntervalMilli);
			} else {
				startDate = new JaretDate(startClipDate.copy());
				startDate.setHours(intervalAnnotation.getHr());
				startDate.setMinutes(intervalAnnotation.getMi());
				startDate.setSeconds(intervalAnnotation.getSec());
				startDate.setMilliseconds(intervalAnnotation.getMilliSec());
			}
			
			AnnotationIntervalImpl mediaInterval = new AnnotationIntervalImpl(
					annotationSetNode, startDate.copy(), startDate.copy()
							.advanceMillis(intervalAnnotation.getDuration()),
					intervalAnnotation, marker, this);
			mediaInterval.setLabel(annotation.getText());
			addInterval(mediaInterval);
			map.put(annotation, mediaInterval);
		}
	}
	
	/**
	 * Adds the initial annotation intervals.
	 */
	private void addInitialAnnotationIntervals() {
		for (Annotation annotation : annotationSetNode
				.getAnnotationList(subject)) {
			addAnnotationInterval(annotation);
		}
		try {
			annotationSetNode.addAnnotationListChangeListener(this,
					this.subject);
			annotationSetNode.addSubjectListChangeListener(this);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
		}
	}
	
	/**
	 * Dispose.
	 */
	public void dispose() {
		for (Interval interval : this.getIntervals()) {
			if (interval instanceof AnnotationIntervalImpl) {
				((AnnotationIntervalImpl) interval).dispose();
			}
		}
		subject.removePropertyChangeListener(Subject.PROP_NAME,
				this.labelListener);
		try {
			annotationSetNode.removeAnnotationListChangeListener(this, subject);
			annotationSetNode.removeSubjectListChangeListener(this);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
		}
	}
	
	/**
	 * Gets the annotation node.
	 * 
	 * @param annotation
	 *            the annotation
	 * @return the annotation node
	 */
	public AnnotationIntervalImpl getAnnotationNode(Annotation annotation) {
		for (Interval interval : this.getIntervals()) {
			AnnotationIntervalImpl annoInterval = (AnnotationIntervalImpl) interval;
			if (annoInterval.getAnnotation().equals(annotation)) {
				return annoInterval;
			}
		}
		return null;
	}
	
	/**
	 * Gets the annotation set node.
	 * 
	 * @return the annotation set node
	 */
	public AnnotationSetNode getAnnotationSetNode() {
		return annotationSetNode;
	}
	
	/**
	 * Gets the owner.
	 * 
	 * @return the owner
	 */
	public DefaultTimeBarModel getOwner() {
		return owner;
	}
	
	/**
	 * Gets the start clip date.
	 * 
	 * @return the start clip date
	 */
	public JaretDate getStartClipDate() {
		return startClipDate.copy();
	}
	
	/**
	 * Gets the subject.
	 * 
	 * @return the subject
	 */
	public Subject getSubject() {
		return subject;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcommons.collections.ListChangeListener#listChanged(
	 * synergyviewcommons.collections.ListChangeEvent)
	 */
	public void listChanged(CollectionChangeEvent event) {
		if (event.getSource() == annotationSetNode.getSubjectList()) {
			for (CollectionDiffEntry<?> diff : event.getListDiff()
					.getDifferences()) {
				if (!diff.isAddition() && (diff.getElement() == this.subject)) {
					dispose();
					this.owner.remRow(this);
				}
			}
		}
		
		if (event.getSource() == annotationSetNode.getAnnotationList(subject)) {
			for (CollectionDiffEntry<?> diff : event.getListDiff()
					.getDifferences()) {
				
				if (diff.getElement() instanceof Annotation) {
					Annotation changedAnnotation = (Annotation) diff
							.getElement();
					if (diff.isAddition()) {
						addAnnotationInterval(changedAnnotation);
					} else {
						AnnotationIntervalImpl interval = map
								.get(changedAnnotation);
						if (interval != null) {
							interval.dispose();
							remInterval(interval);
							map.remove(changedAnnotation);
						}
						
					}
				}
			}
		}
	}
}
