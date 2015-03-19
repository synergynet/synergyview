/*
 *  File: TimeBarMarkerImpl.java 
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
 * A simple implementation of a marker for the TimeBarViewer.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarMarkerImpl.java 821 2009-02-04 21:12:16Z kliem $
 */
public class TimeBarMarkerImpl extends AbstractTimeBarMarker {
    /** current date of the marker. */
    protected JaretDate _date;

    /** description string. */
    protected String _description;

    /** tooltip. */
    protected String _tooltip;

    /**
     * Construct a time bar marker.
     * 
     * @param draggable true if the marker should be draggable
     * @param date the initial date of the marker
     */
    public TimeBarMarkerImpl(boolean draggable, JaretDate date) {
        super(draggable);
        _date = date;
    }


    /**
     * {@inheritDoc}
     */
    public JaretDate getDate() {
        return _date;
    }

    /**
     * Set the date of the marker.
     * 
     * @param date The date to set.
     */
    public void setDate(JaretDate date) {
        JaretDate oldVal = _date;
        _date = date.copy();
        fireMarkerChanged(oldVal, _date);
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return _description;
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(String description) {
        String oldVal = _description;
        _description = description;
        fireMarkerDescriptionChanged(oldVal, _description);
    }

    /**
     * {@inheritDoc}
     */
    public String getTooltipText() {
        return _tooltip;
    }

    /**
     * {@inheritDoc}
     */
    public void setTooltipText(String tooltip) {
        _tooltip = tooltip;
    }
}
