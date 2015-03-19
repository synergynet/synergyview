/*
 *  File: TimeBarModelListener.java 
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
 * Interface for listening to changes in the data of a TimeBarModel.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarModelListener.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeBarModelListener {
    /**
     * Unspecific or multiple changes occured in the model. Should not be used without the need to signal a huge change
     * of data.
     * 
     * @param model the model of which the data changed
     */
    void modelDataChanged(TimeBarModel model);

    /**
     * The model was enlarged by a new row.
     * 
     * @param model the changed model
     * @param row the added row
     */
    void rowAdded(TimeBarModel model, TimeBarRow row);

    /**
     * The model was reduced by a row.
     * 
     * @param model the changed model
     * @param row the removed row
     */
    void rowRemoved(TimeBarModel model, TimeBarRow row);

    /**
     * Unspecific change in the data of a row.
     * 
     * @param model the changed model
     * @param row the row of which the data has changed
     */
    void rowDataChanged(TimeBarModel model, TimeBarRow row);

    /**
     * A new element was added to a row.
     * 
     * @param model the changed model
     * @param row the changed row
     * @param element the added element
     */
    void elementAdded(TimeBarModel model, TimeBarRow row, Interval element);

    /**
     * An element was removed from a row.
     * 
     * @param model the changed model
     * @param row the changed row
     * @param element the removed element
     */
    void elementRemoved(TimeBarModel model, TimeBarRow row, Interval element);

    /**
     * An element in a row has changed.
     * 
     * @param model the changed model
     * @param row the changed row
     * @param element the element that changed
     */
    void elementChanged(TimeBarModel model, TimeBarRow row, Interval element);

    /**
     * The header of a row changed.
     * 
     * @param model the changed model
     * @param row the changed row
     * @param newHeader the changed header
     */
    void headerChanged(TimeBarModel model, TimeBarRow row, Object newHeader);

}
