/*
 *  File: DefaultIntervalModificator.java 
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
 * Default implementation of the IntervalModificator simply allowing everything.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultIntervalModificator.java 881 2009-09-22 21:25:47Z kliem $
 */
public class DefaultIntervalModificator implements IIntervalModificator {

    /**
     * {@inheritDoc}
     */
    public boolean isApplicable(TimeBarRow row, Interval interval) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSizingAllowed(TimeBarRow row, Interval interval) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean newBeginAllowed(TimeBarRow row, Interval interval, JaretDate newBegin) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean newEndAllowed(TimeBarRow row, Interval interval, JaretDate newEnd) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isShiftingAllowed(TimeBarRow row, Interval interval) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean shiftAllowed(TimeBarRow row, Interval interval, JaretDate newBegin) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public double getSecondGridSnap() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
	public double getSecondGridSnap(TimeBarRow row, Interval interval) {
        return -1;
	}

}
