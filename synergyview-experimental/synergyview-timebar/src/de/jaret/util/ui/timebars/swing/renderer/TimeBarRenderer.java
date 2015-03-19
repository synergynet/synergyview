/*
 *  File: TimeBarRenderer.java 
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

import java.awt.Rectangle;

import javax.swing.JComponent;

import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * Interface for supplying a JComponent to render an interval in the <code>TimeBarViewer</code>. The JComponent should
 * be reused, i.e. there only has to be one JComponent to be set up for drawing.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarRenderer.java 869 2009-07-07 19:32:45Z kliem $
 */
public interface TimeBarRenderer {
    /** key for identifying the content rectangle as a client property. */
    String CONTAINING_RECTANGLE = "contRect";

    /**
     * Supply a component to render an interval in the TimeBarViewer. The component may
     * <ul>
     * <li>implement contains(x,y) for exact selection and tooltip firing</li>
     * <li>put the containing rectangle in the client property <code>TimeBarRenderer.CONTAINING_RECTANGLE</code> for
     * exact selection using the selection rectangle</li>
     * </ul>
     * 
     * @param tbv the asking TimeBarViewer
     * @param value the interval to render
     * @param isSelected render marked as selected
     * @param overlapping true if the interval is one of an overlapping set of intervals
     * @return a configured JComponent, ready to be painted by <code>paint(Graphics g)</code>
     */
    JComponent getTimeBarRendererComponent(TimeBarViewer tbv, Interval value, boolean isSelected, boolean overlapping);

    /**
     * Retrieve the preferred drawing bounds for a specific interval. As a default implementation simply return the interval drawing area.
     * 
     * @param intervalDrawingArea the rectangle to render the interval in.
     * @param delegate the viewer delegate
     * @param interval the interval to be rendered
     * @param selected true if the interval is selected
     * @param overlap true if the interval is drawn as one of several intervals that overlap while beeing drawn.
     * @return the bounding rectangle that the renderer will paint in when rendering
     */
    Rectangle getPreferredDrawingBounds(Rectangle intervalDrawingArea, TimeBarViewerDelegate delegate,
            Interval interval, boolean selected, boolean overlap);



}
