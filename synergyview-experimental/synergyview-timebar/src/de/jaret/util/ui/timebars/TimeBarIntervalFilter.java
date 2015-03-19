/*
 *  File: TimeBarIntervalFilter.java 
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

import de.jaret.util.date.Interval;
import de.jaret.util.misc.PropertyObservable;

/**
 * Filter for intervals in a <code>TimeBarViewer</code>. If set on a timebar viewer only those intervals will be
 * displayed that are selected by the filter to be in the result.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarIntervalFilter.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeBarIntervalFilter extends PropertyObservable {
    /**
     * Check whether an interval is in the resulting set.
     * 
     * @param interval interval to be checked
     * @return true if the interval is in the resultingset of intervals
     */
    boolean isInResult(Interval interval);
}
