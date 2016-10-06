/*
 *  File: ISelectionRectListener.java 
 *  Copyright (c) 2004-2008  Peter Kliem (Peter.Kliem@jaret.de)
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

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;

/**
 * Interface allowing listening to the selection rectangle in a time bar viewer.
 * 
 * @author kliem
 * @version $Id: ISelectionRectListener.java 793 2008-12-14 21:02:06Z kliem $
 */
public interface ISelectionRectListener {

    /**
     * Invoked whenever the selection rectangle changes.
     * 
     * @param delegate the delegate for further inquiries
     * @param beginDate the begin date of the selected area
     * @param endDate the end date of the selected area
     * @param rows the list of rows that are covered (even partially) by the selection rectangle
     */
    void selectionRectChanged(TimeBarViewerDelegate delegate, JaretDate beginDate, JaretDate endDate,
            List<TimeBarRow> rows);

    /**
     * Invoked when the selection rect is closed.
     * 
     * @param delegate delegate
     */
    void selectionRectClosed(TimeBarViewerDelegate delegate);

    /**
     * Invoked whenever the region select rectangle changes.
     * 
     * @param delegate the delegate for further inquiries
     * @param tbrect struct containing the selection data
     */
    void regionRectChanged(TimeBarViewerDelegate delegate, TBRect tbrect);

    /**
     * Invoked when the region rect is closed.
     * 
     * @param delegate delegate
     */
    void regionRectClosed(TimeBarViewerDelegate delegate);
}
