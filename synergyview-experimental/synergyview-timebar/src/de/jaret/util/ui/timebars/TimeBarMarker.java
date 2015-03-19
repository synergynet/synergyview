/*
 *  File: TimeBarMarker.java 
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
package de.jaret.util.ui.timebars;

import de.jaret.util.date.JaretDate;

/**
 * Interface for a vertical marker to be displayed in a TimeBarViewer.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarMarker.java 160 2007-01-02 22:02:40Z olk $
 */
public interface TimeBarMarker {
    /**
     * Add a listener to be informed about changes to the marker.
     * 
     * @param tbml the TimeBarMarkerListener to be added
     */
    void addTimeBarMarkerListener(TimeBarMarkerListener tbml);

    /**
     * Remove a TimeBarMarkerListener for this marker.
     * 
     * @param tbml TimeBarMArkerListener to be removed
     */
    void remTimeBarMarkerListener(TimeBarMarkerListener tbml);

    /**
     * Retrieve the current time marked by the marker.
     * 
     * @return Returns the timestamp currently marked.
     */
    JaretDate getDate();

    /**
     * Set the timestamp to be marked.
     * 
     * @param date The date to be marked by the marker
     */
    void setDate(JaretDate date);

    /**
     * Retrieve the draggable state of the marker.
     * 
     * @return Return true if the marker is draggable
     */
    boolean isDraggable();

    /**
     * Set whether the marker can be dragged by the user.
     * 
     * @param draggable if true the marker can be dragged by the user.
     */
    void setDraggable(boolean draggable);

    /**
     * Retrieve the description of the marker.
     * 
     * @return description to be displayed
     */
    String getDescription();

    /**
     * Retrieve the tooltip text for the marker.
     * 
     * @return the tooltip text
     */
    String getTooltipText();

}
