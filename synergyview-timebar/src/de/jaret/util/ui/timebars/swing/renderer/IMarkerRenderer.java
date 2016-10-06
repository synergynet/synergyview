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

import java.awt.Graphics;

import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;

/**
 * Interface describing a marker renderer.
 * 
 * @author kliem
 * @version $Id: IMarkerRenderer.java 839 2009-02-15 19:51:37Z kliem $
 */
public interface IMarkerRenderer {
    /**
     * Draw a marker.
     * 
     * @param delegate the delegate
     * @param graphics the GRaphics to draw with
     * @param marker the marker to draw
     * @param x the x coordinate of the marker
     * @param isDragged true if the markeris currently beeing dragged
     */
    void renderMarker(TimeBarViewerDelegate delegate, Graphics graphics, TimeBarMarker marker, int x, boolean isDragged);

    /**
     * Retrieve the width of a specific marker.
     * 
     * @param marker the marker
     * @return the width in pixel
     */
    int getMarkerWidth(TimeBarMarker marker);

}
