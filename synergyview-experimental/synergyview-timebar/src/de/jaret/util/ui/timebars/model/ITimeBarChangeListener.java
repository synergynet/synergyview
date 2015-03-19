/*
 *  File: ITimeBarChangeListener.java 
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
import de.jaret.util.ui.timebars.TimeBarMarker;

/**
 * Listener interface for listeners that will be notified on changes a user makes interactively using the timebar
 * viewer. If the change is done by keyboard shortcuts those will reported as a series of events (each keystroke).
 * 
 * @author kliem
 * @version $Id: ITimeBarChangeListener.java 825 2009-02-04 21:51:08Z kliem $
 */
public interface ITimeBarChangeListener {
    /**
     * Indicates that an interval is about to be changed interactively by the user.
     * 
     * @param row row of the interval about to be changed
     * @param interval interval that is about to be changed
     */
    void intervalChangeStarted(TimeBarRow row, Interval interval);

    /**
     * Indicates an intermediate state while he user drags interval edegs or the interval.
     * 
     * @param row row of the interval that is changed
     * @param interval interval that is changed
     * @param oldBegin begin date before the change
     * @param oldEnd end date before the change
     */
    void intervalIntermediateChange(TimeBarRow row, Interval interval, JaretDate oldBegin, JaretDate oldEnd);

    /**
     * Indicates the final state after an interactive change performed by the user.
     * 
     * @param row row of the interval that has been changed
     * @param interval interval that has been changed
     * @param oldBegin begin date before the change
     * @param oldEnd end date before the change
     */
    void intervalChanged(TimeBarRow row, Interval interval, JaretDate oldBegin, JaretDate oldEnd);

    /**
     * Indicates that the user cancelled the change (most probably by pressing the ESC key).
     * 
     * @param row row of the interval whose change has been cancelled
     * @param interval interval whose change has been cancelled
     */
    void intervalChangeCancelled(TimeBarRow row, Interval interval);

    /**
     * Indicates that a marker drag started.
     * 
     * @param marker marker that is beeing dragged
     */
    void markerDragStarted(TimeBarMarker marker);

    /**
     * Indicates that a marker drag stopped.
     * 
     * @param marker marker
     */
    void markerDragStopped(TimeBarMarker marker);
}
