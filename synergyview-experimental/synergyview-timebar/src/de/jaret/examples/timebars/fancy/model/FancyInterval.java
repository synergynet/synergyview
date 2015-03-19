/*
 *  File: FancyInterval.java 
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
package de.jaret.examples.timebars.fancy.model;

import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;

import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;

/**
 * Extension of the interval implementation holding an additional state and a percentage.
 * 
 * @author kliem
 * @version $Id: FancyInterval.java 801 2008-12-27 22:44:54Z kliem $
 */
public class FancyInterval extends IntervalImpl {
    private boolean _state = false;
    private int _percentage = 0;
    private LinkedList<Interval> _history = new LinkedList<Interval>();

    public boolean getState() {
        return _state;
    }

    public void setState(boolean state) {
        if (_state != state) {
            // if state really changed inform listeners
            _state = state;
            firePropertyChange("State", !state, state);
        }
    }

    public int getPercentage() {
        return _percentage;
    }

    public void setPercentage(int percentage) {
        if (percentage != _percentage) {
            int oldVal = _percentage;
            _percentage = percentage;
            firePropertyChange("Percentage", oldVal, percentage);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // TODO check property
        saveHistory();
        super.propertyChange(evt);
    }

    @Override
    public void setBegin(JaretDate begin) {
        super.setBegin(begin);
        saveHistory();
    }

    @Override
    public void setEnd(JaretDate end) {
        super.setEnd(end);
        saveHistory();
    }

    private void saveHistory() {
        if (_end != null && _begin != null) {
            Interval interval = new IntervalImpl();
            interval.setBegin(_begin.copy());
            interval.setEnd(_end.copy());
            if (_history.size() > 10) {
                _history.poll();
            }
            _history.add(interval);
        }
    }

    public List<Interval> getHistory() {
        return _history;
    }

}
