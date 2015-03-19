/*
 *  File: IMarkerRenderer.java 
 *  Copyright (c) 2004-2009  Peter Kliem (Peter.Kliem@jaret.de)
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

import java.awt.Color;
import java.awt.Graphics;

import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;

/**
 * Default marker renderer rendering the marker as a single line.
 * 
 * @author kliem
 * @version $Id: DefaultMarkerRenderer.java 823 2009-02-04 21:20:58Z kliem $
 */
public class DefaultMarkerRenderer implements IMarkerRenderer {
    /** color used when the marker is dragged. */
    protected Color _draggedColor = Color.BLUE;
    /** color for marker rendering. */
    protected Color _markerColor = Color.RED;

    /**
     * {@inheritDoc}
     */
    public int getMarkerWidth(TimeBarMarker marker) {
        return 4;
    }

    /**
     * {@inheritDoc}
     */
    public void renderMarker(TimeBarViewerDelegate delegate, Graphics graphics, TimeBarMarker marker, int x,
            boolean isDragged) {
        Color oldCol = graphics.getColor();
        if (isDragged) {
            graphics.setColor(_draggedColor);
        } else {
            graphics.setColor(_markerColor);
        }
        if (delegate.getOrientation().equals(TimeBarViewerInterface.Orientation.HORIZONTAL)) {
            graphics.drawLine(x, 0, x, delegate.getDiagramRect().height + delegate.getXAxisHeight());
        } else {
            graphics.drawLine(0, x, delegate.getDiagramRect().height + delegate.getXAxisHeight(), x);
        }

        graphics.setColor(oldCol);
    }

}
