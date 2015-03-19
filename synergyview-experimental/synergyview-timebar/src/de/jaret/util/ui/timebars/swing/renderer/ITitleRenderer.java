/*
 *  File: ITitleRenderer.java 
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
 * Interface describing a title renderer (for drawing the title area).
 * 
 * @author kliem
 * @version $Id: ITitleRenderer.java 797 2008-12-27 14:21:37Z kliem $
 */
public interface ITitleRenderer {
    /**
     * Return a component for rendering in the title area fo the timebarviewer.
     * 
     * @param tbv timebarviewer
     * @return a JComponent setup for rendering the title
     */
    JComponent getTitleRendererComponent(TimeBarViewer tbv);
}
