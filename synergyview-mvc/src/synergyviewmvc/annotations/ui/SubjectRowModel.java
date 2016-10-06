/**
 *  File: SubtitleUserRowModel.java
 *  Copyright (c) 2010
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

package synergyviewmvc.annotations.ui;

import java.util.List;

import synergyviewmvc.annotations.model.Annotation;
import synergyviewmvc.annotations.model.IntervalAnnotation;
import synergyviewmvc.annotations.ui.IAnnotationListener.ICaptionChangeListener;
import synergyviewmvc.subjects.model.Subject;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;

/**
 * @author phyo
 *
 */
public class SubjectRowModel extends DefaultTimeBarRowModel {

	private Subject _subject;
	private TimeBarMarkerImpl _marker;
	private DefaultTimeBarModel _owner;
	private ICaptionChangeListener _captionChangeListener;
	public SubjectRowModel(TimeBarRowHeader header, DefaultTimeBarModel owner, Subject subject, TimeBarMarkerImpl marker, ICaptionChangeListener captionChangeListener) {
		super(header);
		_subject = subject;
		_owner = owner;
		_marker = marker;
		_captionChangeListener = captionChangeListener;
	}
	

	public void initCaptions(JaretDate startClipDate, List<Annotation> analysisCaptions) {
		for (Annotation analysisCaption : analysisCaptions) {
			long startIntervalMilli = analysisCaption.getStartTime() - startClipDate.getMillisInDay() + startClipDate.getMillis();
			if (analysisCaption instanceof IntervalAnnotation) {
				IntervalAnnotation intervalAnnotation = (IntervalAnnotation) analysisCaption;
				AnnotationIntervalImpl mediaInterval = new AnnotationIntervalImpl(startClipDate.copy().advanceMillis(startIntervalMilli), startClipDate.copy().advanceMillis(startIntervalMilli + intervalAnnotation.getDuration()), intervalAnnotation, _marker, this, _captionChangeListener);
				mediaInterval.setLabel(analysisCaption.getText());
				addInterval(mediaInterval);
			}
		}
	}

	public Subject getSubject() {
		return _subject;
	}

	public DefaultTimeBarModel getOwner() {
		return _owner;
	}

	protected SubjectRowModel() {
		super();

	}
}
