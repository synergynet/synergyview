/*
 *  File: UmlaufRenderer.java 
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
package de.jaret.examples.timebars.fzd.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

import de.jaret.examples.timebars.fzd.model.Umlauf;
import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.TimeBarRenderer;

/**
 * @author Peter Kliem
 * @version $Id: UmlaufRenderer.java 869 2009-07-07 19:32:45Z kliem $
 */
public class UmlaufRenderer implements TimeBarRenderer {
    UmlaufRendererComponent _umlaufComponent;

    public UmlaufRenderer() {
        _umlaufComponent = new UmlaufRendererComponent();
    }

    /**
     * {@inheritDoc}
     */
    public JComponent getTimeBarRendererComponent(TimeBarViewer tbv, Interval value, boolean isSelected, boolean overlapping) {
        if (value instanceof Umlauf) {
            _umlaufComponent.setUmlauf((Umlauf) value);
            _umlaufComponent.setSelected(isSelected);
            return _umlaufComponent;
        } else {
            throw new RuntimeException("unsupported "+value.getClass().getName());
        }
    }

    /**
     * The rendering jcomponent for an umlauf.
     * 
     * @author kliem
     * @version $Id: UmlaufRenderer.java 869 2009-07-07 19:32:45Z kliem $
     */
    public class UmlaufRendererComponent extends JComponent {
        Umlauf _umlauf;
        boolean _selected;

        public UmlaufRendererComponent() {
            setLayout(null);
            setOpaque(false);
        }

        public void setUmlauf(Umlauf umlauf) {
            _umlauf = umlauf;
        }

        public String getToolTipText() {
            return "<html><b>" + _umlauf.getUmlaufbezeichnug() + "</b><br/" + _umlauf.getBegin().toDisplayString()
                    + " - " + _umlauf.getEnd().toDisplayString() + "<br/ " + _umlauf.getFahrten().size()
                    + " Fahrten</html>";
        }

        public void setSelected(boolean selected) {
            _selected = selected;
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int height = getHeight();
            int width = getWidth();

            // balken
            int y = height / 5;
            if (_selected) {
                g.setColor(Color.BLUE);
            } else {
                g.setColor(Color.LIGHT_GRAY);
            }
            g.fillRect(0, y, width - 1, height - 2 * height / 5);
            g.setColor(Color.BLACK);

        }

        public boolean contains(int x, int y) {
            if (y >= getHeight() / 5 && y <= getHeight() / 5 + getHeight() - 2 * getHeight() / 5) {
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
