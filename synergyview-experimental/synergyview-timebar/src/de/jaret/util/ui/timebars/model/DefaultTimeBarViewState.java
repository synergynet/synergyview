/*
 *  File: ITimeBarViewStateListener.java 
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;

/**
 * The default implementation of a ITimeBarViewState.
 * 
 * @author kliem
 * @version $Id: DefaultTimeBarViewState.java 800 2008-12-27 22:27:33Z kliem $
 */
public class DefaultTimeBarViewState implements ITimeBarViewState {
    /** vies state listeners. */
    protected List<ITimeBarViewStateListener> _tbvsListeners = new Vector<ITimeBarViewStateListener>();

    /** map holding the heights. */
    protected Map<TimeBarRow, Integer> _heights = new HashMap<TimeBarRow, Integer>();

    /** map holding the individual overlapping properties for rows. */
    protected Map<TimeBarRow, Boolean> _drawOverlapping = new HashMap<TimeBarRow, Boolean>();

    /** true if variable row heights should be used, false if fixed heights should be used. */
    protected boolean _useVariableRowHeights = false;

    /** the default height for rows. */
    protected int _defaultHeight;

    /** strategy for calculatng row heights. */
    protected IRowHeightStrategy _rowHeightStrategy;

    /** delegate this viewstate is assigned to. */
    protected TimeBarViewerDelegate _delegate;

    /**
     * Consruct the default view state for a specific delegate.
     * 
     * @param delegate the delegate
     */
    public DefaultTimeBarViewState(TimeBarViewerDelegate delegate) {
        _delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    public int getRowHeight(TimeBarRow row) {
        if (!_useVariableRowHeights) {
            return _defaultHeight;
        }
        Integer h = _heights.get(row);
        if (h == null || (_rowHeightStrategy != null) && _rowHeightStrategy.overrideDefault()) {
            if (_rowHeightStrategy == null) {
                // use the default
                return _defaultHeight;
            } else {
                return _rowHeightStrategy.calculateRowHeight(_delegate, this, row);
            }
        }
        return h;
    }

    /**
     * {@inheritDoc}
     */
    public void setRowHeight(TimeBarRow row, int height) {
        int curHeight = getRowHeight(row);
        if (curHeight != height) {
            _heights.put(row, height);
            fireRowHeightChanged(row, height);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setRowHeightStrategy(IRowHeightStrategy rowHeightStrategy) {
        _rowHeightStrategy = rowHeightStrategy;
        fireViewStateChanged();
    }

    /**
     * {@inheritDoc}
     */
    public IRowHeightStrategy getRowHeightStrategy() {
        return _rowHeightStrategy;
    }

    /**
     * Inform the listeners about the new height of a row.
     * 
     * @param row row
     * @param height new height
     */
    public void fireRowHeightChanged(TimeBarRow row, int height) {
        for (ITimeBarViewStateListener listener : _tbvsListeners) {
            listener.rowHeightChanged(row, height);
        }
    }

    /**
     * Inform listeners about a massive change in the view state.
     */
    public void fireViewStateChanged() {
        for (ITimeBarViewStateListener listener : _tbvsListeners) {
            listener.viewStateChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeTimeBarViewStateListener(ITimeBarViewStateListener listener) {
        _tbvsListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void addTimeBarViewStateListener(ITimeBarViewStateListener listener) {
        _tbvsListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultRowHeight(int height) {
        _defaultHeight = height;
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultRowHeight() {
        return _defaultHeight;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUseVariableRowHeights() {
        return _useVariableRowHeights;
    }

    /**
     * {@inheritDoc}
     */
    public void setUseVariableRowHeights(boolean useVariableRowHeights) {
        if (useVariableRowHeights != _useVariableRowHeights) {
            _useVariableRowHeights = useVariableRowHeights;
            fireViewStateChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean getDrawOverlapping(TimeBarRow row) {
        Boolean result = _drawOverlapping.get(row);
        if (result == null) {
            return _delegate.isDrawOverlapping();
        }
        return result.booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    public void setDrawOverlapping(TimeBarRow row, boolean drawOverlapping) {
        _drawOverlapping.put(row, drawOverlapping);
    }

}
