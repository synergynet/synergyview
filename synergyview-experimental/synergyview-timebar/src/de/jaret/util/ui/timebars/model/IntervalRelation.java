/*
 *  File: IntervalRelation.java 
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

import de.jaret.util.misc.PropertyObservableBase;

/**
 * Implementation of the IIntervalRelation. Handles the references itself, i.e. setting ine end to <code>null</code>
 * will remove the relation from the interval.
 * 
 * @author kliem
 * @version $Id: IntervalRelation.java 800 2008-12-27 22:27:33Z kliem $
 */
public class IntervalRelation extends PropertyObservableBase implements IIntervalRelation {
    /** the direction of the relation. */
    protected Direction _direction = Direction.FORWARD;
    /** start interval. */
    protected IRelationalInterval _startInterval;
    /** end interval. */
    protected IRelationalInterval _endInterval;

    /** type of the relation. */
    protected Type _type = Type.END_BEGIN;

    /**
     * Construct a releation between two intervals (forward, end-begin).
     * 
     * @param startInterval first interval
     * @param endInterval second interval
     */
    public IntervalRelation(IRelationalInterval startInterval, IRelationalInterval endInterval) {
        setStartInterval(startInterval);
        setEndInterval(endInterval);
    }

    /**
     * {@inheritDoc}
     */
    public Direction getDirection() {
        return _direction;
    }

    /**
     * {@inheritDoc}
     */
    public void setDirection(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("direction might not be null");
        }
        if (!direction.equals(_direction)) {
            Direction oldValue = _direction;
            _direction = direction;
            firePropertyChange(DIRECTION, oldValue, direction);
        }
    }

    /**
     * {@inheritDoc}
     */
    public IRelationalInterval getEndInterval() {
        return _endInterval;
    }

    /**
     * {@inheritDoc}
     */
    public void setEndInterval(IRelationalInterval interval) {
        if (isRealModification(_endInterval, interval)) {
            IRelationalInterval oldVal = _endInterval;
            // remove from old interval
            if (oldVal != null) {
                oldVal.removeRelation(this);
            }
            _endInterval = interval;
            // add to new interval
            if (_endInterval != null) {
                _endInterval.addRelation(this);
            }
            firePropertyChange(ENDINTERVAL, oldVal, interval);
        }
    }

    /**
     * {@inheritDoc}
     */
    public IRelationalInterval getStartInterval() {
        return _startInterval;
    }

    /**
     * {@inheritDoc}
     */
    public void setStartInterval(IRelationalInterval interval) {
        if (isRealModification(_startInterval, interval)) {
            IRelationalInterval oldVal = _startInterval;
            // remove from old interval
            if (oldVal != null) {
                oldVal.removeRelation(this);
            }
            _startInterval = interval;
            // add to new interval
            if (_startInterval != null) {
                _startInterval.addRelation(this);
            }
            firePropertyChange(STARTINTERVAL, oldVal, interval);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Type getType() {
        return _type;
    }

    /**
     * {@inheritDoc}
     */
    public void setType(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("type might not be null");
        }
        if (!type.equals(_type)) {
            Type oldValue = _type;
            _type = type;
            firePropertyChange(TYPE, oldValue, type);
        }
    }

}
