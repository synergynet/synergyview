/*
 *  File: DistributeOverlapStrategy.java 
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
package de.jaret.examples.timebars.timeline;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.strategy.IOverlapStrategy;
import de.jaret.util.ui.timebars.strategy.OverlapInfo;

/**
 * Implementation of an overlap strategy simply distributing the intervals over a number of rows.
 * 
 * @author kliem
 * @version $Id: $
 */
public class DistributeOverlapStrategy implements IOverlapStrategy{
    /** the delegate the stragey works for. */
    protected TimeBarViewerDelegate _delegate;
    /** Cache for overlap infos. */
    protected Map<TimeBarRow, Map<Interval, OverlapInfo>> _oiRowCache = new HashMap<TimeBarRow, Map<Interval, OverlapInfo>>();
    
    protected int _numRows; 
    
    
    /**
     * Construct the strategy.
     * 
     * @param delegate the delegate the strategy works for
     * @param numRows number of rows to distribute the intervals on
     */
    public DistributeOverlapStrategy(TimeBarViewerDelegate delegate, int numRows) {
        _delegate = delegate;
        _numRows = numRows;
    }

    /**
     * {@inheritDoc}
     */
    public OverlapInfo getOverlapInfo(TimeBarRow row, Interval interval) {
        Map<Interval, OverlapInfo> oiIntervalMap = _oiRowCache.get(row);
        if (oiIntervalMap == null) {
            oiIntervalMap = updateOICache(row);
        }
        OverlapInfo oi = oiIntervalMap.get(interval);
        return oi;
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxOverlapCount(TimeBarRow row) {
        // TODO caching
        Map<Interval, OverlapInfo> oiIntervalMap = _oiRowCache.get(row);
        if (oiIntervalMap == null) {
            oiIntervalMap = updateOICache(row);
        }
        int max = 0;
        Collection<OverlapInfo> infos = oiIntervalMap.values();
        for (OverlapInfo overlapInfo : infos) {
            if (overlapInfo.maxOverlapping > max) {
                max = overlapInfo.maxOverlapping;
            }
        }
        return max + 1;
    }

    /**
     * {@inheritDoc} Assumes sorted Intervals.
     */
    public Map<Interval, OverlapInfo> updateOICache(TimeBarRow row) {
        List<Interval> intervals = _delegate.filterIntervals(row.getIntervals());
        Map<Interval, OverlapInfo> result = new HashMap<Interval, OverlapInfo>();
        // todo: assumes sorted intervals!
        for (int i = 0; i < intervals.size(); i++) {
            Interval interval = intervals.get(i);
            OverlapInfo oi = new OverlapInfo();
            oi.interval = interval;
            result.put(interval, oi);
            oi.maxOverlapping = _numRows;
            oi.overlappingCount=_numRows;
            oi.pos = i % _numRows;
        }
            
            // put to cache
        _oiRowCache.put(row, result);
        // dump(result);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void clearCachedData() {
        _oiRowCache.clear(); // clear oi cache
    }

    /**
     * {@inheritDoc} Simply helps the garbage collector.
     */
    public void dispose() {
        _delegate = null;
        _oiRowCache = null;
    }


}
