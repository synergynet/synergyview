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

package synergyviewcore.collections.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import synergyviewcore.annotations.model.AnnotationSet;
import synergyviewcore.model.PersistenceModelObject;


/**
 * The Class CollectionMediaClip.
 *
 * @author phyo
 */
@Entity
public class CollectionMediaClip extends PersistenceModelObject {

	/** The collection. */
	@ManyToOne
	private Collection collection;


	/** The annotation set list. */
	@OneToMany(mappedBy="collectionMediaClip", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private List<AnnotationSet> annotationSetList = new ArrayList<AnnotationSet>();
	
	/** The Constant PROP_ANNOTATIONSET_LIST. */
	public static final String PROP_ANNOTATIONSET_LIST = "annotationSetList";

	/** The hidden collection media. */
	@OneToMany(cascade=CascadeType.ALL)
	private List<CollectionMedia> hiddenCollectionMedia = new ArrayList<CollectionMedia>();
	
	/** The Constant PROP_HIDDEN_COLLECTION_MEDIA. */
	public static final String PROP_HIDDEN_COLLECTION_MEDIA = "hiddenCollectionMedia";

	/** The clip name. */
	private String clipName;
	
	/** The Constant PROP_CLIP_NAME. */
	public static final String PROP_CLIP_NAME = "clipName";

	/** The start offset. */
	private int startOffset;
	
	/** The Constant PROP_START_OFFSET. */
	public static final String PROP_START_OFFSET = "startOffset";


	/** The duration. */
	private int duration;
	
	/** The Constant PROP_DURATION. */
	public static final String PROP_DURATION = "duration";

	/**
	 * Sets the hidden collection media.
	 *
	 * @param hiddenCollectionMedia the new hidden collection media
	 */
	public void setHiddenCollectionMedia(List<CollectionMedia> hiddenCollectionMedia) {
		this.firePropertyChange(PROP_HIDDEN_COLLECTION_MEDIA, hiddenCollectionMedia, this.hiddenCollectionMedia = hiddenCollectionMedia);
	}

	/**
	 * Gets the hidden collection media.
	 *
	 * @return the hidden collection media
	 */
	public List<CollectionMedia> getHiddenCollectionMedia() {
		return hiddenCollectionMedia;
	}

	/**
	 * Sets the start offset.
	 *
	 * @param startOffset the new start offset
	 */
	public void setStartOffset(int startOffset) {
		this.firePropertyChange(PROP_START_OFFSET, this.startOffset, this.startOffset = startOffset);
	}

	/**
	 * Gets the start offset.
	 *
	 * @return the start offset
	 */
	public int getStartOffset() {
		return startOffset;
	}

	/**
	 * Sets the duration.
	 *
	 * @param duration the new duration
	 */
	public void setDuration(int duration) {
		this.firePropertyChange(PROP_DURATION, this.duration, this.duration = duration);
	}

	/**
	 * Gets the duration.
	 *
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Sets the clip name.
	 *
	 * @param clipName the new clip name
	 */
	public void setClipName(String clipName) {
		this.firePropertyChange(PROP_CLIP_NAME, this.clipName , this.clipName = clipName);
	}

	/**
	 * Gets the clip name.
	 *
	 * @return the clip name
	 */
	public String getClipName() {
		return clipName;
	}

	/**
	 * Sets the collection.
	 *
	 * @param collection the collection to set
	 */
	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	/**
	 * Gets the collection.
	 *
	 * @return the collection
	 */
	public Collection getCollection() {
		return collection;
	}

	/**
	 * Sets the annotation set list.
	 *
	 * @param analysisTranscriptionList the analysisTranscriptionList to set
	 */
	public void setAnnotationSetList(
			List<AnnotationSet> analysisTranscriptionList) {
		this.firePropertyChange(PROP_ANNOTATIONSET_LIST, this.annotationSetList, this.annotationSetList = analysisTranscriptionList);
	}

	/**
	 * Gets the annotation set list.
	 *
	 * @return the analysisTranscriptionList
	 */
	public List<AnnotationSet> getAnnotationSetList() {
		return annotationSetList;
	}

}
