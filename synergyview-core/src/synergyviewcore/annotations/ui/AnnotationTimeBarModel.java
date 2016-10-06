/**
 * File: AnnotationTimeBarModel.java Copyright (c) 2011 phyo This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcore.annotations.ui;

import java.util.HashMap;
import java.util.Map;

import synergyviewcommons.collections.CollectionChangeEvent;
import synergyviewcommons.collections.CollectionChangeListener;
import synergyviewcommons.collections.CollectionDiffEntry;
import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.projects.model.ProjectNode;
import synergyviewcore.subjects.model.Subject;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;

/**
 * The Class AnnotationTimeBarModel.
 * 
 * @author phyo
 */
public class AnnotationTimeBarModel extends DefaultTimeBarModel {
	
	/** The annotation set node. */
	private AnnotationSetNode annotationSetNode;
	
	/** The marker. */
	private TimeBarMarkerImpl marker;
	
	/** The subject list change listener. */
	private CollectionChangeListener subjectListChangeListener;
	
	/** The subject map. */
	private Map<Subject, SubjectRowModel> subjectMap = new HashMap<Subject, SubjectRowModel>();
	
	/**
	 * Instantiates a new annotation time bar model.
	 * 
	 * @param annotationSetNode
	 *            the annotation set node
	 * @param marker
	 *            the marker
	 */
	public AnnotationTimeBarModel(AnnotationSetNode annotationSetNode,
			TimeBarMarkerImpl marker) {
		this.annotationSetNode = annotationSetNode;
		this.marker = marker;
		
		init();
	}
	
	/**
	 * Adding a new subject row.
	 * 
	 * @param subject
	 *            Subject row to be added
	 */
	private void addSubjectRow(Subject subject) {
		DefaultRowHeader header = new DefaultRowHeader(subject.getName());
		SubjectRowModel subjectRow = new SubjectRowModel(this.getMinDate()
				.copy(), annotationSetNode, header, this, subject, marker);
		this.addRow(subjectRow);
		subjectMap.put(subject, subjectRow);
	}
	
	/**
	 * Release the resources. This should be called explicitly by the timebar
	 * viewer!!
	 */
	public void dispose() {
		
		annotationSetNode
				.removeSubjectListChangeListener(subjectListChangeListener);
		
		for (int i = 0; i < getRowCount(); i++) {
			TimeBarRow row = getRow(i);
			if (row instanceof SubjectRowModel) {
				removeSubjectRow((SubjectRowModel) row);
			}
		}
	}
	
	/**
	 * Initialise the timebar model.
	 */
	private void init() {
		
		subjectListChangeListener = new CollectionChangeListener() {
			public void listChanged(CollectionChangeEvent event) {
				for (CollectionDiffEntry<?> diff : event.getListDiff()
						.getDifferences()) {
					Object changeObject = diff.getElement();
					if (changeObject instanceof Subject) {
						Subject changedSubject = (Subject) changeObject;
						if (diff.isAddition()) {
							addSubjectRow(changedSubject);
						} else {
							SubjectRowModel subjectRowModel = subjectMap
									.get(changedSubject);
							if (subjectRowModel == null) {
								return;
							}
							removeSubjectRow(subjectRowModel);
							subjectMap.remove(changedSubject);
						}
					}
				}
			}
		};
		
		// Add annotation Subjects change listener
		annotationSetNode
				.addSubjectListChangeListener(subjectListChangeListener);
		
		// Adding subject rows
		for (Subject subject : annotationSetNode.getSubjectList()) {
			Subject projectSubject = ((ProjectNode) this.annotationSetNode
					.getLastParent()).getSubjectRootNode().getSubject(
					subject.getId());
			addSubjectRow(projectSubject);
		}
	}
	
	/**
	 * Disposes and removes subject row .
	 * 
	 * @param subjectRowModel
	 *            Subject row to be removed
	 */
	private void removeSubjectRow(SubjectRowModel subjectRowModel) {
		subjectRowModel.dispose(); // Needs to explicitly dispose the model
		remRow(subjectRowModel);
	}
	
}
