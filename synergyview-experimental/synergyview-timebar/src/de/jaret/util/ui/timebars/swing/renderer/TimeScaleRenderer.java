/*
 *  File: TimeScaleRenderer.java 
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

import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * Interface for a renderer used to render a time scale in the TimeBarViewer. Care has to be taken not to create a
 * JComponent for every call of <code>getRendererComponent</code>. The renderer should guard one JComponent to configure
 * and return on subsequent calls.
 * 
 * @author Peter Kliem
 * @version $Id: TimeScaleRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeScaleRenderer {
    /**
     * Return a JComponent to be used to render the time scale. The component has to be configured properly before
     * returning it. All needed information about the time scale, major and minor ticks can be obtainend by the
     * TimeBarViewer.
     * <p>
     * The component may implement <code>getToolTipText(MouseEvent evt)</code> to return a proper tooltip for the
     * location
     * </p>
     * 
     * @param tbv the TimeBarViwer the component is used for
     * @param top if true the scale is drawn at the top of the diagram
     * @return a configured JComponent
     */
    JComponent getRendererComponent(TimeBarViewer tbv, boolean top);

    /**
     * Return the height needed for painting the time scale. The value will be fetched only once when the renderer is
     * set for the TimeBarViewer for reasons of stability in painting.
     * 
     * @return the height of the time scale
     */
    int getHeight();
}
