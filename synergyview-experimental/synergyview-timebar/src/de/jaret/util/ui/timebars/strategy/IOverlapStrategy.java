/*
 *  File: IOverlapStrategy.java 
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
package de.jaret.util.ui.timebars.strategy;

import java.util.Map;

import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.model.TimeBarRow;

/**
 * Interface describing the strategy to calculate overlap information for a time bar viewer.
 * 
 * @author kliem
 * @version $Id: $
 */
public interface IOverlapStrategy {

    /**
     * Retrieve the information about overlapping intervals and drawing position for a given interval.
     * 
     * @param row row of the interval
     * @param interval interval
     * @return the overlap information
     */
    OverlapInfo getOverlapInfo(TimeBarRow row, Interval interval);

    /**
     * Retrieve tha maximal count of overlapping intervals in a row.
     * 
     * @param row row to check
     * @return count of maximum overlapping intervals in the row
     */
    int getMaxOverlapCount(TimeBarRow row);

    /**
     * Calculate the number of overlapping intervals and determine the positions to draw them on. May Assume sorted
     * Intervals - check the strategy implementation!
     * 
     * @param row row to update the cache for
     * @return a map containing overlap infos for every interval in the row
     */
    Map<Interval, OverlapInfo> updateOICache(TimeBarRow row);

    /**
     * Clear all cached data. This method is to be called on model changes that invalidate cleared data.
     */
    void clearCachedData();

    /**
     * Called when a strategy is no loner used. May be useful to help garbage collecting or disconnect listeners.
     */
    void dispose();

}
