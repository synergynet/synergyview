/**
 * File: MarkerInterval.java Copyright (c) 2010 Linxiao Ma This program is free
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

package synergyviewcore.timebar.model;

import de.jaret.util.date.JaretDate;

/**
 * The Class MarkerInterval.
 */
public class MarkerInterval extends de.jaret.util.date.Event {
	
	/** The _label. */
	String _label;
	
	/**
	 * Instantiates a new marker interval.
	 * 
	 * @param date
	 *            the date
	 */
	public MarkerInterval(JaretDate date) {
		super(date);
	}
	
	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return _label;
	}
	
	/**
	 * Sets the label.
	 * 
	 * @param label
	 *            the new label
	 */
	public void setLabel(String label) {
		String oldVal = _label;
		_label = label;
		if (isRealModification(oldVal, label)) {
			firePropertyChange("Label", null, label);
		}
	}
}
