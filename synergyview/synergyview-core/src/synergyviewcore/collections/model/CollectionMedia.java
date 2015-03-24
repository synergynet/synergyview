/**
 * File: CollectionMedia.java Copyright (c) 2010 phyo This program is free
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

package synergyviewcore.collections.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import synergyviewcore.model.PersistenceModelObject;

/**
 * The Class CollectionMedia.
 * 
 * @author phyo
 */
@Entity
public class CollectionMedia extends PersistenceModelObject {
	
	/** The Constant PROP_MEDIA_NAME. */
	public static final String PROP_MEDIA_NAME = "mediaName";
	
	/** The Constant PROP_OFFSET. */
	public static final String PROP_OFFSET = "offSet";
	
	/** The collection. */
	@ManyToOne
	private Collection collection;
	
	/** The media name. */
	private String mediaName;
	
	/** The mute. */
	private boolean mute;
	
	/** The off set. */
	private long offSet;
	
	/**
	 * Gets the collection.
	 * 
	 * @return the collection
	 */
	public Collection getCollection() {
		return collection;
	}
	
	/**
	 * Gets the media name.
	 * 
	 * @return the media name
	 */
	public String getMediaName() {
		return mediaName;
	}
	
	/**
	 * Gets the off set.
	 * 
	 * @return the off set
	 */
	public long getOffSet() {
		return offSet;
	}
	
	/**
	 * Checks if is mute.
	 * 
	 * @return true, if is mute
	 */
	public boolean isMute() {
		return mute;
	}
	
	/**
	 * Sets the collection.
	 * 
	 * @param collection
	 *            the collection to set
	 */
	public void setCollection(Collection collection) {
		this.collection = collection;
	}
	
	/**
	 * Sets the media name.
	 * 
	 * @param mediaName
	 *            the new media name
	 */
	public void setMediaName(String mediaName) {
		this.firePropertyChange(PROP_MEDIA_NAME, this.mediaName,
				this.mediaName = mediaName);
	}
	
	/**
	 * Sets the mute.
	 * 
	 * @param mute
	 *            the new mute
	 */
	public void setMute(boolean mute) {
		this.mute = mute;
	}
	
	/**
	 * Sets the off set.
	 * 
	 * @param offSet
	 *            the new off set
	 */
	public void setOffSet(long offSet) {
		this.firePropertyChange(PROP_OFFSET, this.offSet, this.offSet = offSet);
	}
	
}
