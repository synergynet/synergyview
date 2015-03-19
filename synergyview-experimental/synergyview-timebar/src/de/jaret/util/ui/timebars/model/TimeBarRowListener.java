/*
 *  File: TimeBarRowListener.java 
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

/**
 * Interface for listening to changes on the data of a TimeBarRow.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarRowListener.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeBarRowListener {
    /**
     * Unspecific or multiple change of row data. This method should not be used without the need for signalling an
     * unspecific data change.
     * 
     * @param row the changed row
     */
    void rowDataChanged(TimeBarRow row);

    /**
     * A new element was added to the row.
     * 
     * @param row the changed row
     * @param element the new interval in the row
     */
    void elementAdded(TimeBarRow row, Interval element);

    /**
     * An element was removed from the row.
     * 
     * @param row the changed row
     * @param element the removed element
     */
    void elementRemoved(TimeBarRow row, Interval element);

    /**
     * An element in the row has changed.
     * 
     * @param row the row of the changed element
     * @param element the changed interval
     */
    void elementChanged(TimeBarRow row, Interval element);

    /**
     * The header of the row has changed.
     * 
     * @param row the row containing the header.
     * @param newHeader the new or updated header object
     */
    void headerChanged(TimeBarRow row, TimeBarRowHeader newHeader);
}
