/**
 *  File: CollectionMedia.java
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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import synergyviewcore.model.PersistenceModelObject;

/**
 * @author phyo
 *
 */
@Entity
public class CollectionMedia extends PersistenceModelObject {

	private String mediaName;
	
	@ManyToOne
	private Collection collection;
	
	
	private long offSet;
	public static final String PROP_OFFSET = "offSet";
	
	public static final String PROP_MEDIA_NAME = "mediaName";
	public String getMediaName() {
		return mediaName;
	}
	
	private boolean mute;
	
	public void setMediaName(String mediaName) {
		this.firePropertyChange(PROP_MEDIA_NAME, this.mediaName, this.mediaName = mediaName);
	}

	public void setOffSet(long offSet) {
		this.firePropertyChange(PROP_OFFSET, this.offSet, this.offSet = offSet);
	}

	public long getOffSet() {
		return offSet;
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

	public void setMute(boolean mute) {
		this.mute = mute;
	}

	public boolean isMute() {
		return mute;
	}
	
	
	
}
