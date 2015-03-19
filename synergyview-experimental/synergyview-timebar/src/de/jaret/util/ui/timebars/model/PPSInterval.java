/*
 *  File: PPSInterval.java 
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

import de.jaret.util.date.IntervalImpl;

/**
 * Special interval for holding a pps value in the xScalePPSInterval row.
 * 
 * @author kliem
 * @version $Id: PPSInterval.java 836 2009-02-14 21:24:39Z kliem $
 */
public class PPSInterval extends IntervalImpl {
    /** default value. */
    private static final double DEFAULT_PIXEL_PER_SECOND = 0.05;
    /** the carried pps value. */
    private double _pps = DEFAULT_PIXEL_PER_SECOND;

    /** true if this pps interval marks a break in the time scale. */
    private boolean _isBreak = false;

    /** number of pixels that should be used to display a break if it is break. */
    private int _breakDisplayWidth = 10;

    /**
     * Construct.
     * 
     * @param pps pps value
     */
    public PPSInterval(double pps) {
        _pps = pps;
    }

    /**
     * Retrieve the pps value.
     * 
     * @return pps value
     */
    public double getPps() {
        return _pps;
    }

    /**
     * Set the pps value.
     * 
     * @param pps pps value
     */
    public void setPps(double pps) {
        if (_pps != pps) {
            double oldval = _pps;
            _pps = pps;
            firePropertyChange("Pps", oldval, pps);
        }
    }

    /**
     * {@inheritDoc} Simply output the pps value for displaying purposes.
     */
    public String toString() {
        return (_isBreak ? "break" : "") + "pps: " + _pps;
    }

    /**
     * Check whether the pps interval should cause a break in the timeline.
     * 
     * @return <code>true</code> if this pps interval denotes a break in the timeline
     */
    public boolean isBreak() {
        return _isBreak;
    }

    /**
     * Set whether the pps interval will be rendered as a break in the timeline.
     * 
     * @param isBreak <code>true</code> for rendering as a break in the timeline
     */
    public void setBreak(boolean isBreak) {
        if (isBreak != _isBreak) {
            boolean oldVal = _isBreak;
            _isBreak = isBreak;
            firePropertyChange("break", oldVal, isBreak);
        }
    }

    /**
     * Retrieve the width that should be used if this pps interval denotes a break in the timeline.
     * 
     * @return the width in pixel
     */
    public int getBreakDisplayWidth() {
        return _breakDisplayWidth;
    }

    /**
     * Set the width that should be used if the pps interval denotes a break in the timescale.
     * 
     * @param breakDisplayWidth width in pixel
     */
    public void setBreakDisplayWidth(int breakDisplayWidth) {
        if (_breakDisplayWidth != breakDisplayWidth) {
            int oldVal = _breakDisplayWidth;
            _breakDisplayWidth = breakDisplayWidth;
            firePropertyChange("breakWidth", oldVal, breakDisplayWidth);
        }
    }

}
