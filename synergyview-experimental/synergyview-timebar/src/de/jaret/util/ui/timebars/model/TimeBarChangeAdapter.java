/*
 *  File: TimeBarChangeAdapter.java 
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
 * Empty stub for an ITimeBarChangeListener.
 * 
 * @author kliem
 * @version $Id: TimeBarChangeAdapter.java 825 2009-02-04 21:51:08Z kliem $
 */
public class TimeBarChangeAdapter implements ITimeBarChangeListener {

    /**
     * {@inheritDoc} Empty stub.
     */
    public void intervalChangeCancelled(TimeBarRow row, Interval interval) {
    }

    /**
     * {@inheritDoc} Empty stub.
     */
    public void intervalChangeStarted(TimeBarRow row, Interval interval) {
    }

    /**
     * {@inheritDoc} Empty stub.
     */
    public void intervalChanged(TimeBarRow row, Interval interval, JaretDate oldBegin, JaretDate oldEnd) {
    }

    /**
     * {@inheritDoc} Empty stub.
     */
    public void intervalIntermediateChange(TimeBarRow row, Interval interval, JaretDate oldBegin, JaretDate oldEnd) {
    }

    /**
     * {@inheritDoc} Empty stub.
     */
    public void markerDragStarted(TimeBarMarker marker) {
    }

    /**
     * {@inheritDoc} Empty stub.
     */
    public void markerDragStopped(TimeBarMarker marker) {
    }

}
