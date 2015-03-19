/*
 *  File: TimeBarGapRenderer.java 
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
package de.jaret.util.ui.timebars.swing.renderer;

import javax.swing.JComponent;

import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * Interface for supplying a JComponent for rendering the gaps between two intervals. The supplied JComponent should be
 * reused and only be set up for rendering a single gap. Setup is done by the <code>TimeBarViewer</code>. Since in some
 * situations the gap betwwen two intervals is very small (or non-existent) it is possible to enforce a minimum width
 * for the clipping rect and size for the gap rendering component.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarGapRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeBarGapRenderer {
    /**
     * 
     * @param tbv the calling TimeBarViewer
     * @param row the row the intervals are in
     * @param interval1 left interval (may be null if the gap is the starting "gap")
     * @param interval2 right interval (may be <code>null</code> if it is the ending "gap")
     * @return a configured JComponent for rendering
     */
    JComponent getTimeBarGapRendererComponent(TimeBarViewer tbv, TimeBarRow row, Interval interval1, Interval interval2);

    /**
     * By returning a non negative value a minimum width will be given to the renderer. The size and clipping rect will
     * be centered around the middle of the gap between the two intervals.
     * 
     * @return minimum width for the rendering component. A negative value indicates no need for a fixed minimum width
     */
    int getMinimumWidth();
}
