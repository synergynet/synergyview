/*
 *  File: RowIntervalTuple.java 
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
package de.jaret.util.ui.timebars.swing.dnd;

import java.util.List;

import de.jaret.util.ui.timebars.model.TimeBarRow;

/**
 * @author Peter Kliem
 * @version $Id: RowIntervalTuple.java 160 2007-01-02 22:02:40Z olk $
 */
public class RowIntervalTuple {
    protected TimeBarRow _row;

    protected List _intervals;

    /**
     * @param row
     * @param intervals
     */
    public RowIntervalTuple(TimeBarRow row, List intervals) {
        _row = row;
        _intervals = intervals;
    }

    /**
     * @return Returns the intervals.
     */
    public List getIntervals() {
        return _intervals;
    }

    /**
     * @param intervals The intervals to set.
     */
    public void setIntervals(List intervals) {
        _intervals = intervals;
    }

    /**
     * @return Returns the row.
     */
    public TimeBarRow getRow() {
        return _row;
    }

    /**
     * @param row The row to set.
     */
    public void setRow(TimeBarRow row) {
        _row = row;
    }
}
