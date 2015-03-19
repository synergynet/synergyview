/*
 *  File: IntervalModificator.java 
 *  Copyright (c) 2004-2007  Peter Kliem (Peter.Kliem@jaret.de)
 *  A commercial license is available, see http://www.jaret.de.
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
package de.jaret.util.ui.timebars.mod;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.TimeBarRow;

/**
 * Interface for deciding about interval modifications through the time bar viewer (visual editing).
 * 
 * @author Peter Kliem
 * @version $Id: IntervalModificator.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface IntervalModificator {

    /**
     * Decides whether the interval modificator is responsible for a given interval in a row.
     * 
     * @param row row of the interval
     * @param interval interval in question
     * @return true if this modificator should be questioned
     */
    boolean isApplicable(TimeBarRow row, Interval interval);

    /**
     * Decide whether a given interval in a row is allowed to be sized.
     * 
     * @param row row of the interval
     * @param interval interval in question
     * @return true if the interval may be sized
     */
    boolean isSizingAllowed(TimeBarRow row, Interval interval);

    /**
     * Decide whether a given interval in a given row can change the begin date.
     * 
     * @param row row of the interval
     * @param interval interval to be modified
     * @param newBegin new begin date
     * @return true if the modification is allowed
     */
    boolean newBeginAllowed(TimeBarRow row, Interval interval, JaretDate newBegin);

    /**
     * Decide whether a given interval in a given row can change the end date.
     * 
     * @param row row of the interval
     * @param interval interval to be modified
     * @param newEnd new end date
     * @return true if the modification is allowed
     */
    boolean newEndAllowed(TimeBarRow row, Interval interval, JaretDate newEnd);

    /**
     * Decide whether a given interval in a row is allowed to be shifted.
     * 
     * @param row row of the interval
     * @param interval interval in question
     * @return true if the interval may be shifted
     */
    boolean isShiftingAllowed(TimeBarRow row, Interval interval);

    /**
     * Decide whether an interval may be shifted in time to a new begin date.
     * 
     * @param row row of the interval
     * @param interval interval to be modified
     * @param newBegin new begin date
     * @return true if the modification is allowed
     */
    boolean shiftAllowed(TimeBarRow row, Interval interval, JaretDate newBegin);

    /**
     * If this method returns a positive value this is used as the modification interval. The value is given in seconds.
     * 
     * @return the positive grid snap or a negative value indicating no grid snap
     */
    double getSecondGridSnap();
}
