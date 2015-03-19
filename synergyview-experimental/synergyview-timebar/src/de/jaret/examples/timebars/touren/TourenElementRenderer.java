/*
 *  File: TourenElementRenderer.java 
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
package de.jaret.examples.timebars.touren;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

import de.jaret.util.date.Interval;
import de.jaret.util.swing.GraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.TimeBarRenderer;

/**
 * @author Peter Kliem
 * @version $Id: TourenElementRenderer.java 869 2009-07-07 19:32:45Z kliem $
 */
public class TourenElementRenderer implements TimeBarRenderer {
    RendererComponent _component;

    public TourenElementRenderer() {
        _component = new RendererComponent();
    }

    public JComponent getTimeBarRendererComponent(TimeBarViewer tbv, Interval value, boolean isSelected, boolean overlapping) {
        _component.setTourElement((TourElement) value);
        _component.setSelected(isSelected);
        return _component;
    }

    class RendererComponent extends JComponent {
        TourElement _te;
        boolean _selected;

        public RendererComponent() {
            setLayout(null);
            setOpaque(false);
        }

        public void setTourElement(TourElement te) {
            _te = te;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.JComponent#getToolTipText()
         */
        public String getToolTipText() {
            return "<html><b>" + _te.getLabel() + "</b><br/" + _te.getBegin().toDisplayString() + " - "
                    + _te.getEnd().toDisplayString() + "<br/ " + _te.getBeginOrt() + " - " + _te.getEndeOrt()
                    + "</html>";
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

            int y = height / 3;
            int bheight = height / 3;
            int yEnd = y + bheight;

            // balken
            if (_selected) {
                g.setColor(Color.BLUE);
            } else {
                if (_te.getTyp() == 0) {
                    g.setColor(Color.YELLOW);
                } else {
                    g.setColor(Color.CYAN);
                }
            }
            g.fillRect(0, y, width - 1, height / 3);
            // Rahmen
            g.setColor(Color.BLACK);
            g.drawRect(0, y, width - 1, height / 3);

            // Balkenbeschriftung
            GraphicsHelper.drawStringCenteredVCenter(g, _te.getLabel(), 0, width, height / 2);

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
