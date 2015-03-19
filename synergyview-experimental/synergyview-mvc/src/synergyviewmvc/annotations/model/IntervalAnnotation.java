/**
 *  File: IntervalAnnotation.java
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

package synergyviewmvc.annotations.model;

import javax.persistence.Entity;

/**
 * @author phyokyaw
 *
 */
@Entity
public class IntervalAnnotation extends Annotation {
	
	public static final String PROP_DURATION = "duration";
	private int duration;
	
	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.firePropertyChange(PROP_DURATION, this.duration, this.duration = duration);
	}
	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}
	
}
