/*
 *  File: TimeBarModel.java 
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

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;

/**
 * Model for a number of rows containing intervals.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarModel.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeBarModel {
    /**
     * Return the row for the given index.
     * 
     * @param row index of the row
     * @return the row for the given index
     */
    TimeBarRow getRow(int row);

    /**
     * Get the number of rows in the model.
     * 
     * @return number of rows
     */
    int getRowCount();

    /**
     * Retrieve the earliest date in the model.
     * 
     * @return earliest date in the model
     */
    JaretDate getMinDate();

    /**
     * Retrieve tha latesr dat ein the model.
     * 
     * @return the latest date in the model
     */
    JaretDate getMaxDate();

    /**
     * Retrieve the TimeBarRow for a given interval.
     * 
     * @param interval interval to gt the row for.
     * @return TimeBarRow that contains the interval or <code>null</code> if no row could be determined.
     */
    TimeBarRow getRowForInterval(Interval interval);

    /**
     * Add a listener to listen for changes in the model.
     * 
     * @param tbml TimeBarModelListener for watching the model
     */
    void addTimeBarModelListener(TimeBarModelListener tbml);

    /**
     * Removes a previously added listener.
     * 
     * @param tbml TimeBarModelListener to be removed
     */
    void remTimeBarModelListener(TimeBarModelListener tbml);

}
