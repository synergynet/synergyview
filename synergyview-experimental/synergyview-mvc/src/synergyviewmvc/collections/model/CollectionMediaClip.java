/**
 *  File: CollectionMediaClilp.java
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

package synergyviewmvc.collections.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import synergyviewmvc.annotations.model.AnnotationSet;
import synergyviewmvc.model.PersistenceModelObject;

/**
 * @author phyo
 *
 */
@Entity
public class CollectionMediaClip extends PersistenceModelObject {

	@ManyToOne
	private Collection collection;

	@OneToMany(mappedBy="collectionMediaClip", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private List<AnnotationSet> analysisTranscriptionList = new ArrayList<AnnotationSet>();
	public static final String PROP_ANALYSIS_TRANSCRIPTION_LIST = "analysisTranscriptionList";

	@OneToMany(cascade=CascadeType.ALL)
	private List<CollectionMedia> hiddenCollectionMedia = new ArrayList<CollectionMedia>();
	public static final String PROP_HIDDEN_COLLECTION_MEDIA = "hiddenCollectionMedia";

	private String clipName;
	public static final String PROP_CLIP_NAME = "clipName";

	private int startOffset;
	public static final String PROP_START_OFFSET = "startOffset";


	private int duration;
	public static final String PROP_DURATION = "duration";

	public void setMedia(List<CollectionMedia> media) {
		this.firePropertyChange(PROP_HIDDEN_COLLECTION_MEDIA, media, this.hiddenCollectionMedia = media);
	}

	public List<CollectionMedia> getHiddenCollectionMedia() {
		return hiddenCollectionMedia;
	}

	public void setStartOffset(int startOffset) {
		this.firePropertyChange(PROP_START_OFFSET, this.startOffset, this.startOffset = startOffset);
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setDuration(int duration) {
		this.firePropertyChange(PROP_DURATION, this.duration, this.duration = duration);
	}

	public int getDuration() {
		return duration;
	}

	public void setClipName(String clipName) {
		this.firePropertyChange(PROP_CLIP_NAME, this.clipName , this.clipName = clipName);
	}

	public String getClipName() {
		return clipName;
	}

	/**
	 * @param collection the collection to set
	 */
	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	/**
	 * @return the collection
	 */
	public Collection getCollection() {
		return collection;
	}

	/**
	 * @param analysisTranscriptionList the analysisTranscriptionList to set
	 */
	public void setAnalysisTranscriptionList(
			List<AnnotationSet> analysisTranscriptionList) {
		this.firePropertyChange(PROP_ANALYSIS_TRANSCRIPTION_LIST, this.analysisTranscriptionList, this.analysisTranscriptionList = analysisTranscriptionList);
	}

	/**
	 * @return the analysisTranscriptionList
	 */
	public List<AnnotationSet> getAnalysisTranscriptionList() {
		return analysisTranscriptionList;
	}
}
