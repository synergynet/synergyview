/*
 *  File: DefaultOverlapStrategy.java 
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.model.TimeBarRow;

/**
 * Default implementation of an overlap strategy that does a complete check on overlaps between intervals including
 * transitive overlaps. This can be time consuming when dealing with a lot of intervals.
 * 
 * Thanks go to Mathias Kurt for supplying an optimized algorithm for computing the overlapping quite fast.
 * 
 * @author kliem
 * @version $Id: $
 */
public class DefaultOverlapStrategy implements IOverlapStrategy {
    /** the delegate the strategy works for. */
    protected TimeBarViewerDelegate _delegate;
    /** Cache for overlap infos. */
    protected Map<TimeBarRow, Map<Interval, OverlapInfo>> _oiRowCache = new HashMap<TimeBarRow, Map<Interval, OverlapInfo>>();

    /** if set to false, intervals will be sorted before checking overlapping. */
    protected boolean _assumeSortedIntervals = true;

    /**
     * Construct a default strategy for a specific delegate.
     * 
     * @param delegate the delegate the strategy works for
     */
    public DefaultOverlapStrategy(TimeBarViewerDelegate delegate) {
        _delegate = delegate;
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
     * {@inheritDoc} Assumes sorted intervals unless <code>assumeSortedIntervals</code> is set to false.
     */
    public Map<Interval, OverlapInfo> updateOICache(TimeBarRow row) {
        // all intervals in the row
        List<Interval> intervals = _delegate.filterIntervals(row.getIntervals());
        // result is an overlap information for each interval
        Map<Interval, OverlapInfo> result = new HashMap<Interval, OverlapInfo>();

        // if intervals are not sorted: sort in advance by begin timestamp
        if (!_assumeSortedIntervals) {
            List<Interval> temp = new ArrayList<Interval>(intervals.size());
            temp.addAll(intervals);
            Collections.sort(temp, new Comparator<Interval>() {
                public int compare(Interval o1, Interval o2) {
                    return (int) (o1.getBegin().getDate().getTime() - o2.getBegin().getDate().getTime());
                }
            });
            intervals = temp;
        }

        // build a array of indices, sorted according to the start time
        Integer[] sortedStartTime = new Integer[intervals.size()];
        for (int i = 0; i < intervals.size(); i++) {
            sortedStartTime[i] = i;
        }
        final List<Interval> finalIntervals = intervals;
        Arrays.sort(sortedStartTime, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return finalIntervals.get(o1).getBegin().compareTo(finalIntervals.get(o2).getBegin());
            }
        });

        // map max end time of the list so far on index in sortedStartTime
        TreeMap<Long, Integer> mapEndTime = new TreeMap<Long, Integer>();
        long maxEndTime = Long.MIN_VALUE;
        for (int i = 0; i < sortedStartTime.length; i++) {
            Interval interval1 = intervals.get(sortedStartTime[i]);
            maxEndTime = Math.max(maxEndTime, interval1.getEnd().getDate().getTime());
            mapEndTime.put(maxEndTime, i);
        }

        // list of all overlapinfos
        List<OverlapInfo> oList = new ArrayList<OverlapInfo>();
        // create overlap information for all intervals in the row
        for (int i = 0; i < intervals.size(); i++) {
            Interval interval = intervals.get(i);
            OverlapInfo oi = new OverlapInfo();
            oi.interval = interval;
            result.put(interval, oi);
            oList.add(oi);

            // collect intersecting intervals for each interval and store the intersecting intervals in the
            // overlap information.

            // java 5
            SortedMap<Long, Integer> first = mapEndTime.subMap(0L, interval.getBegin().getDate().getTime());
            int startIdx = (first.size() == 0 ? 0 : first.get(first.lastKey()));
            // Java 6 (mapEndTime should be a navigable map than)
            // Map.Entry<Long, Integer> first = mapEndTime.floorEntry(interval.getBegin().getDate().getTime() - 1);
            // int startIdx = (null == first ? 0 : first.getValue());
            for (int y = startIdx; y < sortedStartTime.length; y++) {
                int x = sortedStartTime[y];
                if (i == x) {
                    continue;
                }
                Interval cmp = intervals.get(x);
                if (0 < cmp.getBegin().compareTo(interval.getEnd())) {
                    break;
                }
                if (IntervalImpl.intersectNonIncluding(interval, cmp)) {
                    oi.overlapping.add(cmp);
                }
            }

            // set the current values
            oi.overlappingCount = oi.overlapping.size();
            oi.maxOverlapping = oi.overlappingCount;

        }

        // now get the transitive overlaps for eachinterval

        for (OverlapInfo oi : oList) {
            Interval interval = oi.interval;

            // verify maxOverlapping
            int max = 0;
            int cur = 0;
            for (Interval check : oi.overlapping) {
                if (check.contains(interval.getBegin())) {
                    cur++;
                }
            }
            if (cur > max) {
                max = cur;
            }
            cur = 0;
            for (Interval check : oi.overlapping) {
                if (check.contains(interval.getEnd())) {
                    cur++;
                }
            }
            if (cur > max) {
                max = cur;
            }

            for (Interval check : oi.overlapping) {
                if (interval.contains(check.getBegin())) {
                    cur = 1;
                    for (Interval check2 : oi.overlapping) {
                        if (!check.equals(check2)) {
                            if (IntervalImpl.containsNonIncluding(check2, check.getBegin())) {
                                cur++;
                            }
                        }
                    }
                    if (cur > max) {
                        max = cur;
                    }
                }
            }
            for (Interval check : oi.overlapping) {
                if (interval.contains(check.getEnd())) {
                    cur = 1;
                    for (Interval check2 : oi.overlapping) {
                        if (!check.equals(check2)) {
                            if (IntervalImpl.containsNonIncluding(check2, check.getEnd())) {
                                cur++;
                            }
                        }
                    }
                    if (cur > max) {
                        max = cur;
                    }
                }
            }

            oi.maxOverlapping = max;
            oi.overlappingCount = max;
        }

        // sort oList by overlap count for further processing
        // intrevals with only some overlaps will be done first
        // there might be scenarios that would require another order.
        Collections.sort(oList, new Comparator<OverlapInfo>() {
            public int compare(OverlapInfo arg0, OverlapInfo arg1) {
                return -arg1.overlappingCount - arg0.overlappingCount;
            }
        });

        // retrieve the maximum number of overlapping intervals for a single interval including transitive overlaps
        // This runs unless no more changes will be encountered
        calcTransitiveOverlaps(result, oList);

        // positions
        // go through all overlap infos and assign positions
        for (int i = 0; i < oList.size(); i++) {
            OverlapInfo oi = oList.get(i);
            assignPosition(oi, result);
            // check whether a position could be assigned
            // in rare conditions the code above will not be concise
            if (oi.pos == -1) {
                // no position assigned --> correct maxoverlapping and do another round
                oi.maxOverlapping = oi.maxOverlapping + 1;
                // recalc transitive overlaps and assign position again
                calcTransitiveOverlaps(result, oList);
                assignPosition(oi, result);
            }
        }

        // put to cache
        _oiRowCache.put(row, result);
        return result;
    }

    /**
     * Calculate the tansitive overlaps for all interval. The algorith will run several times until no more changes will
     * be encountered.
     * 
     * @param result map interval, overlapinfo that holds the information of the complete row
     * @param oList all overlap information objects as a list
     */
    private void calcTransitiveOverlaps(Map<Interval, OverlapInfo> result, List<OverlapInfo> oList) {
        boolean change = true;
        while (change) {
            change = false;
            for (int i = 0; i < oList.size(); i++) {
                OverlapInfo oi = oList.get(i);
                int max = getMaxOverlapping(oi, result);
                if (max != oi.maxOverlapping) {
                    oi.maxOverlapping = max;
                    change = true;
                }
            }
        }
    }

    /**
     * Check and assign a position in the row to the gievn interval in th eoverlap information.
     * 
     * @param oi the overlap information that should be processed
     * @param result map interval, overlapinfo that holds the information of the complete row
     */
    private void assignPosition(OverlapInfo oi, Map<Interval, OverlapInfo> result) {
        // position array for all overlapping intervals initialized with -1
        int[] positions = new int[oi.maxOverlapping + 1];
        for (int p = 0; p < oi.maxOverlapping + 1; p++) {
            positions[p] = -1;
        }
        // fill in positions of overlapping intervals that already have an assigned position
        for (Interval interval : oi.overlapping) {
            OverlapInfo o = result.get(interval);
            if (o.pos != -1) {
                positions[o.pos] = o.pos;
            }
        }
        // If the current overlap information does not have an assigned position yet,
        // find an empty slot and assign it
        if (oi.pos == -1) {
            for (int p = 0; p < oi.maxOverlapping + 1; p++) {
                if (positions[p] == -1) {
                    oi.pos = p;
                    positions[p] = p;
                    break;
                }
            }
        }
    }

    // dump a row for debugging
    // private void dumpRow(TimeBarRow row) {
    // System.err.println("Row dump " + row);
    // System.err.println("DefaultTimeBarRowModel row = new DefaultTimeBarRowModel(new DefaultRowHeader(\"abc\"));");
    // for (Interval interval : row.getIntervals()) {
    // System.err.println("i = new OtherIntervalImpl(new JaretDate(" + interval.getBegin().getDate().getTime()
    // + "L), new JaretDate(" + interval.getEnd().getDate().getTime() + "L));");
    // System.err.println("i.setLabel(\"" + interval.toString() + "\");");
    // System.err.println("row.addInterval(i);");
    // }
    // }

    /**
     * Retrieve the maximum count of overlapping intervals for all overlapping intervals registered in an overlapInfo.
     * 
     * @param oi OverlapInfo
     * @param map map containing the overlapinfos of all intervals
     * @return the maximum overlap count of all overlapping intervals
     */
    private int getMaxOverlapping(OverlapInfo oi, Map<Interval, OverlapInfo> map) {
        int max = oi.overlappingCount;
        for (Interval interval : oi.overlapping) {
            OverlapInfo o = map.get(interval);
            if (o.maxOverlapping > max) {
                max = o.maxOverlapping;
            }
        }
        return max;
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

    /**
     * Debug method for dumping all overlap informtaions contained in a map.
     * 
     * @param map map containing intervls and overlap information
     */
//    private void dump(Map<Interval, OverlapInfo> map) {
//        for (Interval i : map.keySet()) {
//            OverlapInfo oi = map.get(i);
//            dump(oi);
//        }
//        System.out.println();
//    }

    /**
     * Debug helper for dumping the overlap informations.
     * 
     * @param oi overlap information to dump
     */
//    private void dump(OverlapInfo oi) {
//        System.out.println("oi " + oi.interval + " pos " + oi.pos + " max " + oi.maxOverlapping);
//        for (Interval interval : oi.overlapping) {
//            System.out.println(" --> " + interval);
//        }
//    }

    /**
     * Retrieve the status of assumeSortedIntervals. If this is false, intervals will be sorted temporarily for
     * overlapping.
     * 
     * @return the status
     */
    public boolean getAssumeSortedIntervals() {
        return _assumeSortedIntervals;
    }

    /**
     * If set to false intervals will be sorted often ... defaults to true.
     * 
     * @param assumeSortedIntervals the new status
     */
    public void setAssumeSortedIntervals(boolean assumeSortedIntervals) {
        _assumeSortedIntervals = assumeSortedIntervals;
    }

}
