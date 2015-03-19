/*
 *  File: BreakIntervalFilter.java 
 *  Copyright (c) 2004-2009  Peter Kliem (Peter.Kliem@jaret.de)
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
package de.jaret.examples.timebars.millis.model;

import de.jaret.util.date.Interval;
import de.jaret.util.misc.PropertyObservableBase;
import de.jaret.util.ui.timebars.TimeBarIntervalFilter;
import de.jaret.util.ui.timebars.model.PPSInterval;
import de.jaret.util.ui.timebars.model.TimeBarRow;

/**
 * Special interval filter removing all intervals that are completely inside a break in the timescale.
 * 
 * @author kliem
 * @version $Id: BreakIntervalFilter.java 836 2009-02-14 21:24:39Z kliem $
 */
public class BreakIntervalFilter extends PropertyObservableBase implements TimeBarIntervalFilter {
    /** the row containing the ps intervals. */
    private TimeBarRow _ppsScaleRow;

    /**
     * Construct the filter with a reference to the row that contains the pss intervals.
     * 
     * @param ppsScaleRow the time bar row containing the pps intervals
     */
    public BreakIntervalFilter(TimeBarRow ppsScaleRow) {
        _ppsScaleRow = ppsScaleRow;
    }

    /**
     * {@inheritDoc} Intervals are in the result if they do not fall into a break.
     */
    public boolean isInResult(Interval interval) {
        for (Interval i : _ppsScaleRow.getIntervals()) {
            PPSInterval ppsInterval = (PPSInterval) i;
            if (ppsInterval.isBreak() && ppsInterval.contains(interval)) {
                return false;
            }
        }
        return true;
    }

}
