/*
 *  File: HeaderRenderer.java 
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

import de.jaret.util.ui.timebars.model.TimeBarRowHeader;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * Renderer for row headers.
 * 
 * @author Peter Kliem
 * @version $Id: HeaderRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface HeaderRenderer {
    /**
     * Provide a JComponent configured to render the header object supplied as value.
     * 
     * @param tbv the calling TimeBarViewer
     * @param value the header object to render
     * @param isSelected if true draw the selectd state
     * @return a configured JComponent ready to be painted
     */
    JComponent getHeaderRendererComponent(TimeBarViewer tbv, TimeBarRowHeader value, boolean isSelected);

    /**
     * Return the width required by the header renderer. The value will only be read once by the TimeBarViewer when the
     * rendderer ist set. There is no support for dynamic change of the header width initiated by the renderer.
     * 
     * @return the width of the header
     */
    int getWidth();
}
