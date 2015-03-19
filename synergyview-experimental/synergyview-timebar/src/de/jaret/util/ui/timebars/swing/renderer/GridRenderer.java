/*
 *  File: GridRenderer.java 
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

import de.jaret.util.ui.timebars.strategy.ITickProvider;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * Interface for supplying a JComponent used to render the grid in a <code>TimeBarViewer</code>. The component should be
 * parametrized using the access methods of the viewer and should be reused for subsequent paint calls.
 * 
 * @author Peter Kliem
 * @version $Id: GridRenderer.java 835 2009-02-14 21:03:52Z kliem $
 */
public interface GridRenderer {
    /**
     * Supply a configured JComponent for rendering the grid.
     * 
     * @param tbv Timebarviewer for retrieving everything to paramatrize the compoennt
     * @return configured component
     */
    JComponent getRendererComponent(TimeBarViewer tbv);

    /**
     * Set a tick provider to use.
     * 
     * @param tickProvider the tick provider to use
     */
    void setTickProvider(ITickProvider tickProvider);
}
