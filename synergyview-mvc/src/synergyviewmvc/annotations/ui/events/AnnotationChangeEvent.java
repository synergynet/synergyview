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

package synergyviewmvc.annotations.ui.events;

import java.util.EventObject;

import synergyviewmvc.annotations.ui.IAnnotationListener.CaptionPublishState;

/**
 * @author phyokyaw
 *
 */
public class AnnotationChangeEvent extends EventObject {

	private String _captionText;
	private CaptionPublishState _captionState;
	/**
	 * @param source
	 */
	public AnnotationChangeEvent(Object source, String captionText, CaptionPublishState captionState) {
		super(source);
		_captionText = captionText;
		_captionState = captionState;
	}

	/**
	 * @return the captionText
	 */
	public String getCaptionText() {
		return _captionText;
	}


	/**
	 * @return the _captionState
	 */
	public CaptionPublishState getCaptionState() {
		return _captionState;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
