/**
 *  File: AnnotationChangeEvent.java
 *  Copyright (c) 2010
 *  phyokyaw
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

package synergyviewcore.annotations.ui.events;

import java.util.EventObject;

import synergyviewcore.annotations.ui.events.ICaptionChangeListener.CaptionPublishState;


/**
 * The Class CaptionChangeEvent.
 *
 * @author phyokyaw
 */
public class CaptionChangeEvent extends EventObject {

	/** The _caption text. */
	private String _captionText;
	
	/** The _caption state. */
	private CaptionPublishState _captionState;
	
	/**
	 * Instantiates a new caption change event.
	 *
	 * @param source the source
	 * @param captionText the caption text
	 * @param captionState the caption state
	 */
	public CaptionChangeEvent(Object source, String captionText, CaptionPublishState captionState) {
		super(source);
		_captionText = captionText;
		_captionState = captionState;
	}

	/**
	 * Gets the caption text.
	 *
	 * @return the captionText
	 */
	public String getCaptionText() {
		return _captionText;
	}


	/**
	 * Gets the caption state.
	 *
	 * @return the _captionState
	 */
	public CaptionPublishState getCaptionState() {
		return _captionState;
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

}
