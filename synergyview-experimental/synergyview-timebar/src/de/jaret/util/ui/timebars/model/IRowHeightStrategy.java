/*
 *  File: ITimeBarViewState.java 
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

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;

/**
 * Interface describing a strategy for calculating the height/width of a row.
 * 
 * @author kliem
 * @version $Id: IRowHeightStrategy.java 532 2007-08-14 21:36:42Z olk $
 */
public interface IRowHeightStrategy {
    /**
     * If the strategy should override manually set heights/widths thhis should return <code>true</code>.
     * 
     * @return <code>true</code> if any manual set heights/widths should be overriden by the strategy
     */
    boolean overrideDefault();

    /**
     * Calculate the height/width of a row.
     * 
     * @param delegate the time bar viewers delegate
     * @param timeBarViewState the viewstate
     * @param row the row in question
     * @return the calculated height
     */
    int calculateRowHeight(TimeBarViewerDelegate delegate, ITimeBarViewState timeBarViewState, TimeBarRow row);

}
