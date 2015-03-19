/*
 *  File: DefaultTimeBarRenderer.java 
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
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;

import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * A simple default renderer for intervals using a JButton. This default renderer provides the possibility to register
 * timebar renderers for special classes.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultTimeBarRenderer.java 881 2009-09-22 21:25:47Z kliem $
 */
public class DefaultTimeBarRenderer implements TimeBarRenderer {
    /** component used for rendering. */
    protected JButton _component = new JButton();


    /**
     * {@inheritDoc}
     */
    public JComponent getTimeBarRendererComponent(TimeBarViewer tbv, Interval value, boolean isSelected,
            boolean overlapping) {
       return defaultGetTimeBarRendererComponent(tbv, value, isSelected, overlapping);
    }

    /**
     * {@inheritDoc}
     */
    public JComponent defaultGetTimeBarRendererComponent(TimeBarViewer tbv, Interval value, boolean isSelected,
            boolean overlapping) {
        _component.setText(value.toString());
        _component.setToolTipText(value.toString());
        if (isSelected) {
            _component.setBackground(Color.BLUE);
          } else {
            _component.setBackground(Color.LIGHT_GRAY);
        }
        return _component;
    }

    /**
     * {@inheritDoc} Simple default implementation.
     */
	public Rectangle getPreferredDrawingBounds(Rectangle intervalDrawingArea,
			TimeBarViewerDelegate delegate, Interval interval,
			boolean selected, boolean overlap) {
		return intervalDrawingArea;
	}
}
