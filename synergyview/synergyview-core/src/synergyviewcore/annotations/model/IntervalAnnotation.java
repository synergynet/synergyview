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

package synergyviewcore.annotations.model;

import javax.persistence.Entity;

import synergyviewcore.util.DateTimeHelper;


/**
 * The Class IntervalAnnotation.
 *
 * @author phyokyaw
 */
@Entity
public class IntervalAnnotation extends Annotation {
	
	/** The Constant PROP_DURATION. */
	public static final String PROP_DURATION = "duration";
	
	/** The duration. */
	private long duration;
	
	/**
	 * Sets the duration.
	 *
	 * @param duration the duration to set
	 */
	public void setDuration(long duration) {
		this.firePropertyChange(PROP_DURATION, this.duration, this.duration = duration);
	}
	
	/**
	 * Gets the duration.
	 *
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}
	
	/**
	 * Gets the formatted duration.
	 *
	 * @return the formatted duration
	 */
	public String getFormattedDuration() {
		return DateTimeHelper.getHMSFromMilliFormatted(duration);
	}
	
}
