/*
 *  File: TimeBarSelectionListener.java 
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

/**
 * Listener for registering selection changes on a TimeBarSelectionModel.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarSelectionListener.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeBarSelectionListener {
    /**
     * The selection did unspecific change. Should be rarely called by the SelectionModel.
     * 
     * @param selectionModel the selection model the change took place in
     */
    void selectionChanged(TimeBarSelectionModel selectionModel);

    /**
     * An element (either a row or an interval) has been added to the selection.
     * 
     * @param selectionModel the selection model that has been changed
     * @param element the interval aded to the selection
     */
    void elementAddedToSelection(TimeBarSelectionModel selectionModel, Object element);

    /**
     * An element (either a row or an interval) has been removed from the selection.
     * 
     * @param selectionModel the selection model that has been changed
     * @param element element that has been removed
     */
    void elementRemovedFromSelection(TimeBarSelectionModel selectionModel, Object element);
}
