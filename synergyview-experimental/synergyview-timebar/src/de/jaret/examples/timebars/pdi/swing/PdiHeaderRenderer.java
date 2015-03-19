/*
 *  File: PdiHeaderRenderer.java 
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
package de.jaret.examples.timebars.pdi.swing;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;

import de.jaret.examples.timebars.pdi.model.Person;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.HeaderRenderer;

/**
 * @author Peter Kliem
 * @version $Id: PdiHeaderRenderer.java 237 2007-02-10 21:11:50Z olk $
 */
public class PdiHeaderRenderer implements HeaderRenderer {
    JLabel _component = new JLabel("", JLabel.RIGHT);

    public JComponent getHeaderRendererComponent(TimeBarViewer tbv, TimeBarRowHeader value, boolean isSelected) {
        _component.setText(((Person) value).getName());
        if (isSelected) {
            _component.setOpaque(true);
            _component.setBackground(Color.BLUE);
        } else {
            _component.setBackground(Color.WHITE);
        }
        return _component;
    }

    public int getWidth() {
        return 100;
    }
}
