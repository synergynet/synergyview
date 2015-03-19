/*
 *  File: SumRenderer.java 
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
package de.jaret.examples.timebars.hierarchy.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.TimeBarRenderer;

/**
 * @author Peter Kliem
 * @version $Id: SumRenderer.java 869 2009-07-07 19:32:45Z kliem $
 */

public class SumRenderer implements TimeBarRenderer {
    SumRendererComponent _sumComponent;

    public SumRenderer() {
        _sumComponent = new SumRendererComponent();
    }

    public JComponent getTimeBarRendererComponent(TimeBarViewer tbv, Interval value, boolean isSelected, boolean overlapping) {
        _sumComponent.setInterval(value);
        _sumComponent.setSelected(isSelected);
        return _sumComponent;
    }

    public class SumRendererComponent extends JComponent {
        Interval _interval;
        boolean _selected;

        public SumRendererComponent() {
            setLayout(null);
            setOpaque(false);
        }

        public void setInterval(Interval interval) {
            _interval = interval;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.JComponent#getToolTipText()
         */
        public String getToolTipText() {
            return _interval.toString();
        }

        public void setSelected(boolean selected) {
            _selected = selected;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int height = getHeight();
            int width = getWidth();

            int y = height / 3 - WIDTH;
            int bheight = WIDTH;
            int yEnd = y + bheight;

            // balken
            if (_selected) {
                g.setColor(Color.BLUE);
            } else {
                g.setColor(Color.BLACK);
            }
            g.fillRect(0, y, width - 1, bheight);

            int leftx[] = { 0, 0, 4 };
            int lefty[] = { y + bheight, y + bheight + 7, y + bheight };

            int rightx[] = { width - 1, width - 1, width - 1 - 4 };
            int righty[] = { y + bheight, y + bheight + 7, y + bheight };

            g.fillPolygon(leftx, lefty, 3);
            g.fillPolygon(rightx, righty, 3);

        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.JComponent#contains(int, int)
         */
        public boolean contains(int x, int y) {
            if (y >= getHeight() / 3 && y <= getHeight() / 3 + getHeight() / 3) {
                return true;
            } else {
                return false;
            }
        }
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
