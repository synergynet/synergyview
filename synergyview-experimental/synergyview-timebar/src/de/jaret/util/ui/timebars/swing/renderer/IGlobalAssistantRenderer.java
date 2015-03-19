/*
 *  File: IGlobalAssistantRenderer.java 
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

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;

/**
 * This interface describes a general purpose renderer that can be used to render anything befor the intervals will be
 * painted and after the intervals have been painted.
 * 
 * @author kliem
 * @version $Id: IGlobalAssistantRenderer.java 835 2009-02-14 21:03:52Z kliem $
 */
public interface IGlobalAssistantRenderer {
    /**
     * Will be called before the interval rendering starts.
     * 
     * @param delegate delegate that calls
     * @param graphics graphics to draw with
     */
    void doRenderingBeforeIntervals(TimeBarViewerDelegate delegate, Graphics graphics);

    /**
     * Will be called after the intervals have been rendered.
     * 
     * @param delegate delegate that calls
     * @param graphics graphics to draw with
     */
    void doRenderingLast(TimeBarViewerDelegate delegate, Graphics graphics);

}
