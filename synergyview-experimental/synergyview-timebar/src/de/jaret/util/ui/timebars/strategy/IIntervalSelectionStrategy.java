/*
 *  File: IIntervalSelectionStrategy.java 
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
 * Interface describing strategies when there is a doubt which interval should be considered in various selction
 * actions.
 * 
 * @author kliem
 * @version $Id: IIntervalSelectionStrategy.java 792 2008-12-13 23:04:54Z kliem $
 */
public interface IIntervalSelectionStrategy {

    /**
     * Chose one of the intervals to be used.
     * 
     * @param intervals list of intervals
     * @return the selected interval
     */
    Interval selectInterval(List<Interval> intervals);

}
