/**
 *  File: EventObject.java
 *  Copyright (c) 2010
 *  Linxiao Ma
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

package synergyviewmvc.timebar.event;

import java.util.EventObject;
import java.util.List;

import synergyviewmvc.collections.model.CollectionMedia;

/**
 * @author Linxiao Ma
 *
 */
public class MediaListEvent extends EventObject {

	private static final long serialVersionUID = -3726699087845782876L;

	public static enum CollectionChangeType{
		MediaAdded, MediaRemoved
	}
	
	protected CollectionChangeType mediaCollectionChangeType;
	protected List<CollectionMedia> collectionMedias;
	/**
	 * @param arg0
	 */
	public MediaListEvent(Object arg0, CollectionChangeType mediaCollectionChangeType, List<CollectionMedia> collectionMedias) {
		super(arg0);
		this.mediaCollectionChangeType = mediaCollectionChangeType;
		this.collectionMedias = collectionMedias;
	}
	
	public CollectionChangeType getMediaCollectionChangeType(){
		return mediaCollectionChangeType;
	}
	
	public List<CollectionMedia> collectionMedias(){
		return this.collectionMedias;
	}

}
