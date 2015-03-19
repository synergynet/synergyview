/*
 *  File: TimeBarRowFilter.java 
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

import de.jaret.util.misc.PropertyObservable;
import de.jaret.util.ui.timebars.model.TimeBarRow;

/**
 * Row filter for the TimeBarViewer. The filter is a property observable so that the time bar viewer can listen on
 * changes on the filter forcing it to a subsequent refresh.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarRowFilter.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeBarRowFilter extends PropertyObservable {
    /**
     * Return true if the given row is in the filtered result.
     * 
     * @param row row to be checked for inclusion in the result
     * @return true if the row is part of the result
     */
    boolean isInResult(TimeBarRow row);
}
