/**
 *  File: TimeChangeEvent.java
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

package synergyviewcore.annotations.ui.events;

import java.util.EventObject;

import de.jaret.util.date.JaretDate;

/**
 * @author phyo
 *
 */
public class TimeChangeEvent extends EventObject {
	private JaretDate _currentTime;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JaretDate getCurrentTime() {
		return _currentTime;
	}

	/**
	 * @param source
	 */
	public TimeChangeEvent(Object source,  JaretDate currentTime) {
		super(source);
		_currentTime = currentTime;
	}
}
