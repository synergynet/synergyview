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

/**
 * Listener that is informed when the viewstate of the timebar viewer changes.
 * 
 * @author kliem
 * @version $Id: ITimeBarViewStateListener.java 532 2007-08-14 21:36:42Z olk $
 */
public interface ITimeBarViewStateListener {

    /**
     * Called when the height of row changed.
     * 
     * @param row row
     * @param newHeight the new height of the row
     */
    void rowHeightChanged(TimeBarRow row, int newHeight);

    /**
     * Called whenever the viewstate is changed in a way that will make a complete repaint/recalculate of the underlying
     * timebarviewer necessary.
     */
    void viewStateChanged();
}
