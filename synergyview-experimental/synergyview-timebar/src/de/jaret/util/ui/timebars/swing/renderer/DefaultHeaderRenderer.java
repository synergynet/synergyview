/*
 *  File: DefaultHeaderRenderer.java 
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

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;

import de.jaret.util.ui.timebars.model.TimeBarRowHeader;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * Simple default implementation of a HeaderRenderer using a JLabel.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultHeaderRenderer.java 427 2007-05-13 15:58:36Z olk $
 */
public class DefaultHeaderRenderer implements HeaderRenderer {
    /** component used for rendering. */
    private JLabel _component = new JLabel();

    /**
     * {@inheritDoc}
     */
    public JComponent getHeaderRendererComponent(TimeBarViewer tbv, TimeBarRowHeader value, boolean isSelected) {
        _component.setText(value.toString());
        _component.setToolTipText(value.toString());
        if (isSelected) {
            _component.setOpaque(true);
            _component.setBackground(Color.BLUE);
        } else {
            _component.setBackground(Color.WHITE);
        }
        return _component;
    }

    /**
     * {@inheritDoc}
     */
    public int getWidth() {
        return 35;
    }
}
