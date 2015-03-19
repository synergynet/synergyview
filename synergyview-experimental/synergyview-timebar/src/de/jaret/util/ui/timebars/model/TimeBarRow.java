/*
 *  File: TimeBarRow.java 
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
package de.jaret.util.ui.timebars.model;

import java.util.List;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;

/**
 * Model for a single row of intervals. When implementing the interface care should be taken to think about a performant
 * implementation of the by-date-selecting mehtods for interval retrieval.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarRow.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeBarRow {
    /**
     * Provide the full ordered list of intervals.
     * 
     * @return an ordered List of Intervals
     */
    List<Interval> getIntervals();

    /**
     * Provide the intervals in beetween a given interval. An interval should be selected as inside the given bounds if
     * it is in between the bounds or one of the bounds is in the interval in question.
     * 
     * @param beginDate first date
     * @param endDate last date
     * @return ordered List of Intervals between the given dates
     */
    List<Interval> getIntervals(JaretDate beginDate, JaretDate endDate);

    /**
     * Returns all intervals containing the given date.
     * 
     * @param date the date to be included in the intervals
     * @return List of intervals containing the given date
     */
    List<Interval> getIntervals(JaretDate date);

    /**
     * Return the row header.
     * 
     * @return row header
     */
    TimeBarRowHeader getRowHeader();

    /**
     * Return the beginning date of the earliest interval in the row. A row may return <code>null</code> if it contains
     * no intervals. If a row supplies a min date it must always supply a max value.
     * 
     * @return earliest date in the row or <code>null</code> if the row contains no intervals.
     */
    JaretDate getMinDate();

    /**
     * Return the ending date of the latest interval in the row. A row may return <code>null</code> if it contains no
     * intervals. If a row supplies a max date it must always supply a min date.
     * 
     * @return latest date in the row or <code>null</code> if the row contains no intervals.
     */
    JaretDate getMaxDate();

    /**
     * Register a <code>TimeBarRowListener</code> for listening to changes in the row.
     * 
     * @param tbrl TimeBarRowListener to be added
     */
    void addTimeBarRowListener(TimeBarRowListener tbrl);

    /**
     * Remove a previously added TimeBarRowListener.
     * 
     * @param tbrl TimeBarRowListener to be removed
     */
    void remTimeBarRowListener(TimeBarRowListener tbrl);

}
