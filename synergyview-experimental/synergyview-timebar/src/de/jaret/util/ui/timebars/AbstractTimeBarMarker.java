/*
 *  File: AbstractTimeBarMarker.java 
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

import java.util.ArrayList;
import java.util.List;

import de.jaret.util.date.JaretDate;

/**
 * Abstract base implementation of a TimeBarMarker.
 * 
 * @author Peter Kliem
 * @version $Id: AbstractTimeBarMarker.java 251 2007-02-12 21:09:03Z olk $
 */
public abstract class AbstractTimeBarMarker implements TimeBarMarker {
    /** attribute storing the draggable state. */
    protected boolean _draggable = true;

    /** listener list. */
    protected List<TimeBarMarkerListener> _listenerList;

    /**
     * @param draggable true if the marker should be draggable
     */
    public AbstractTimeBarMarker(boolean draggable) {
        _draggable = draggable;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDraggable() {
        return _draggable;
    }

    /**
     * @param draggable The draggable to set.
     */
    public void setDraggable(boolean draggable) {
        _draggable = draggable;
        // fire a description change to trigger redraw
        fireMarkerDescriptionChanged(getDescription(), getDescription());
    }

    /**
     * {@inheritDoc}
     */
    public void addTimeBarMarkerListener(TimeBarMarkerListener tbml) {
        if (_listenerList == null) {
            _listenerList = new ArrayList<TimeBarMarkerListener>();
        }
        _listenerList.add(tbml);
    }

    /**
     * {@inheritDoc}
     */
    public void remTimeBarMarkerListener(TimeBarMarkerListener tbml) {
        if (_listenerList != null) {
            _listenerList.remove(tbml);
        }
    }

    /**
     * Inform registered listeners of a date change.
     * 
     * @param oldDate the last date the amrker was set to
     * @param newDate the new date
     */
    protected void fireMarkerChanged(JaretDate oldDate, JaretDate newDate) {
        if (_listenerList != null) {
            for (TimeBarMarkerListener listener : _listenerList) {
                listener.markerMoved(this, oldDate, newDate);
            }
        }
    }

    /**
     * Inform registered listeners of a description change.
     * 
     * @param oldValue the prevoiuos description
     * @param newValue the new value
     */
    protected void fireMarkerDescriptionChanged(String oldValue, String newValue) {
        if (_listenerList != null) {
            for (TimeBarMarkerListener listener : _listenerList) {
                listener.markerDescriptionChanged(this, oldValue, newValue);
            }
        }
    }
}
