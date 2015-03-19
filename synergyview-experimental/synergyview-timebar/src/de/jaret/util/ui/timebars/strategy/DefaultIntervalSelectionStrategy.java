/*
 *  File: DefaultIntervalSelectionStrategy.java 
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
package de.jaret.util.ui.timebars.strategy;

import java.util.List;

import de.jaret.util.date.Interval;

/**
 * Default implementation of the interval selection strategy: use the first in the list.
 * 
 * @author kliem
 * @version $Id: DefaultIntervalSelectionStrategy.java 821 2009-02-04 21:12:16Z kliem $
 */
public class DefaultIntervalSelectionStrategy implements IIntervalSelectionStrategy {

    /**
     * {@inheritDoc} Chose the first from the list.
     */
    public Interval selectInterval(List<Interval> intervals) {
        return intervals.get(0);
    }

}
