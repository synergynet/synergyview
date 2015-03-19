/*
 *  File: ITickProvider.java 
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

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;

/**
 * Interface describing a provider for ticks/times for marking. This can be used to link a grid renderer to a time scale
 * renderer.
 * 
 * @author kliem
 * @version $Id: ITickProvider.java 822 2009-02-04 21:20:32Z kliem $
 */
public interface ITickProvider {
    /**
     * Provide a list of dates used as major ticks.
     * 
     * @param delegate the delegate the ticks are used with
     * @return list of dates
     */
    List<JaretDate> getMajorTicks(TimeBarViewerDelegate delegate);

    /**
     * Provide a list of dates used as minor ticks.
     * 
     * @param delegate the delegate the ticks are used with
     * @return list of dates
     */
    List<JaretDate> getMinorTicks(TimeBarViewerDelegate delegate);

}
