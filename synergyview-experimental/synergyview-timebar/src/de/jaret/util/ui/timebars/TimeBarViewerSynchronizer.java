/*
 *  File: TimeBarViewerSynchronizer.java 
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import de.jaret.util.date.JaretDate;

/**
 * A synchronizer for two or more TimeBarViewers.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarViewerSynchronizer.java 863 2009-06-22 20:06:19Z kliem $
 */
public class TimeBarViewerSynchronizer implements PropertyChangeListener {
    /** attribute marking that rowheigths should be synced. */
    protected boolean _syncRowHeight = false;

    /** attribute marking that start dates should be synced. */
    protected boolean _syncStartDate = true;

    /** attribute marking that the scales should be synced. */
    protected boolean _syncTimeScale = true;

    /** attribute marking that the y axis width should be synced. */
    protected boolean _syncYAxisWidth = true;

    /** List of viewers to be synchronized. */
    protected List<TimeBarViewerInterface> _viewers;

    /**
     * Constructs a new Synchronizer. It will synchronize the timescale (optional), min/max dates of the scale, optional
     * rowheight, optional startdate, optional y axis width
     * 
     * @param syncRowHeight if true, row heights will be synchronized
     * @param syncStartDate if true, the start dates will be synchronized
     * @param syncTimeScale if true the scaling of the time axis will be synchronized
     * @param syncYAxisWidth if true sync the width of the y axis
     */
    public TimeBarViewerSynchronizer(boolean syncRowHeight, boolean syncStartDate, boolean syncTimeScale, boolean syncYAxisWidth) {
        _syncRowHeight = syncRowHeight;
        _syncStartDate = syncStartDate;
        _syncTimeScale = syncTimeScale;
        _syncYAxisWidth = syncYAxisWidth;
    }


    /**
     * Constructs a new Synchronizer. It will synchronize the timescale (optional), min/max dates of the scale, optional
     * rowheight, optional startdate
     * 
     * @param syncRowHeight if true, row heights will be synchronized
     * @param syncStartDate if true, the start dates will be synchronized
     * @param syncTimeScale if true the scaling of the time axis will be synchronized
     */
    public TimeBarViewerSynchronizer(boolean syncRowHeight, boolean syncStartDate, boolean syncTimeScale) {
    	this(syncRowHeight, syncStartDate, syncTimeScale, true);
    }

    /**
     * Add a viewer to the set of synchronized viewers.
     * 
     * @param viewer the viewer to be added
     */
    public void addViewer(TimeBarViewerInterface viewer) {
        if (_viewers == null) {
            _viewers = new ArrayList<TimeBarViewerInterface>();
        }
        _viewers.add(viewer);
        viewer.addPropertyChangeListener(this);
    }

    /**
     * Remove a viewer from the set of synchronized viewers.
     * 
     * @param viewer the viewer to be removed
     */
    public void remViewer(TimeBarViewerInterface viewer) {
        if (_viewers != null) {
            _viewers.remove(viewer);
            viewer.removePropertyChangeListener(this);
        }
    }

    /**
     * Handle property changes from the different viewers.
     * 
     * @param evt propertyChange event
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // System.out.println("propChange "+evt.getPropertyName());
        TimeBarViewerInterface emitting = (TimeBarViewerInterface) evt.getSource();
        if (evt.getPropertyName().equals(TimeBarViewerInterface.PROPERTYNAME_PIXELPERSECOND) && _syncTimeScale) {
            setPixPerSecond(emitting, emitting.getPixelPerSecond());
        } else if (evt.getPropertyName().equals(TimeBarViewerInterface.PROPERTYNAME_ROWHEIGHT) && _syncRowHeight) {
            setRowHeight(emitting, emitting.getRowHeight());
        } else if (evt.getPropertyName().equals(TimeBarViewerInterface.PROPERTYNAME_STARTDATE) && _syncStartDate) {
            setStartDate(emitting, emitting.getStartDate());
        } else if (evt.getPropertyName().equals(TimeBarViewerInterface.PROPERTYNAME_MINDATE)) {
            setMinDate(emitting, emitting.getMinDate());
        } else if (evt.getPropertyName().equals(TimeBarViewerInterface.PROPERTYNAME_MAXDATE)) {
            setMaxDate(emitting, emitting.getMaxDate());
        } else if (evt.getPropertyName().equals(TimeBarViewerInterface.PROPERTYNAME_YAXISWIDTH) && _syncYAxisWidth) {
            setYAxisWidth(emitting, emitting.getYAxisWidth());
        }
    }

    /**
     * Set the pix per second value for all viewers but the emitting viewer.
     * 
     * @param emitting emitting viewer
     * @param pixPerSecond value
     */
    private void setPixPerSecond(TimeBarViewerInterface emitting, double pixPerSecond) {
        for (TimeBarViewerInterface viewer : _viewers) {
            if (!emitting.equals(viewer)) { // do not set on the emitting viewer
                viewer.removePropertyChangeListener(this);
                viewer.setPixelPerSecond(pixPerSecond);
                viewer.addPropertyChangeListener(this);
            }
        }
    }

    /**
     * Set the rowheight value for all viewers but the emitting viewer.
     * 
     * @param emitting emitting viewer
     * @param rowHeight value
     */
    private void setRowHeight(TimeBarViewerInterface emitting, int rowHeight) {
        for (TimeBarViewerInterface viewer : _viewers) {
            if (!emitting.equals(viewer)) { // do not set on the emitting viewer
                viewer.removePropertyChangeListener(this);
                viewer.setRowHeight(rowHeight);
                viewer.addPropertyChangeListener(this);
            }
        }
    }

    /**
     * Set the minimium date value for all viewers but the emitting viewer.
     * 
     * @param emitting emitting viewer
     * @param minDate value
     */
    private void setMinDate(TimeBarViewerInterface emitting, JaretDate minDate) {
        for (TimeBarViewerInterface viewer : _viewers) {
            if (!emitting.equals(viewer)) { // do not set on the emitting viewer
                viewer.removePropertyChangeListener(this);
                viewer.setMinDate(minDate.copy());
                viewer.addPropertyChangeListener(this);
            }
        }
    }

    /**
     * Set the maximum date value for all viewers but the emitting viewer.
     * 
     * @param emitting emitting viewer
     * @param maxDate value
     */
    private void setMaxDate(TimeBarViewerInterface emitting, JaretDate maxDate) {
        for (TimeBarViewerInterface viewer : _viewers) {
            if (!emitting.equals(viewer)) { // do not set on the emitting viewer
                viewer.removePropertyChangeListener(this);
                viewer.setMaxDate(maxDate.copy());
                viewer.addPropertyChangeListener(this);
            }
        }
    }

    /**
     * Set the start date value for all viewers but the emitting viewer.
     * 
     * @param emitting emitting viewer
     * @param startDate value
     */
    private void setStartDate(TimeBarViewerInterface emitting, JaretDate startDate) {
        for (TimeBarViewerInterface viewer : _viewers) {
            if (!emitting.equals(viewer)) { // do not set on the emitting viewer
                viewer.removePropertyChangeListener(this);
                viewer.setStartDate(startDate.copy());
                viewer.addPropertyChangeListener(this);
            }
        }
    }

    /**
     * Set the y axis width value for all viewers but the emitting viewer.
     * 
     * @param emitting emitting viewer
     * @param yAxisWidth the width of the y axis
     */
    private void setYAxisWidth(TimeBarViewerInterface emitting, int yAxisWIdth) {
        for (TimeBarViewerInterface viewer : _viewers) {
            if (!emitting.equals(viewer)) { // do not set on the emitting viewer
                viewer.removePropertyChangeListener(this);
                viewer.setYAxisWidth(yAxisWIdth);
                viewer.addPropertyChangeListener(this);
            }
        }
    }

    /**
     * Produce a string representation.
     * 
     * @return simple string representation
     */
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("TimeBarViewerSynchronizer:\n");
        for (TimeBarViewerInterface viewer : _viewers) {
            buf.append(viewer.toString() + "\n");
        }
        return buf.toString();
    }

}
