/**
 *  File: ICaptionListener.java
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

package synergyviewmvc.annotations.ui;

import synergyviewmvc.annotations.ui.events.AnnotationChangeEvent;

/**
 * @author phyokyaw
 *
 */
public interface IAnnotationListener {
	
	public void addCaptionListener(ICaptionChangeListener captionChangeListener);
	public void removeCaptionListener(ICaptionChangeListener captionChangeListener);
	public enum CaptionPublishState {SET, UNSET};
	public interface ICaptionChangeListener {
		public void captionChange(Object source, AnnotationChangeEvent eventArg);
	}
}
