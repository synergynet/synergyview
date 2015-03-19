/*
 *  File: FahrtRenderer.java 
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

import de.jaret.examples.timebars.fzd.model.Fahrt;
import de.jaret.util.date.Interval;
import de.jaret.util.swing.GraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.TimeBarRenderer;

/**
 * @author Peter Kliem
 * @version $Id: FahrtRenderer.java 869 2009-07-07 19:32:45Z kliem $
 */
public class FahrtRenderer implements TimeBarRenderer {
    FahrtRendererComponent _fahrtComponent;

    public FahrtRenderer() {
        _fahrtComponent = new FahrtRendererComponent();
    }

    public JComponent getTimeBarRendererComponent(TimeBarViewer tbv, Interval value, boolean isSelected, boolean overlapping) {
        if (value instanceof Fahrt) {
            _fahrtComponent.setFahrt((Fahrt) value);
            _fahrtComponent.setSelected(isSelected);
            return _fahrtComponent;
        } else {
            throw new RuntimeException("unsupported "+value.getClass().getName());
        }
    }

    /**
     * Rendering jcompoentn for the fahrt.
     * 
     * @author kliem
     * @version $Id: FahrtRenderer.java 869 2009-07-07 19:32:45Z kliem $
     */
    public class FahrtRendererComponent extends JComponent {
        Fahrt _fahrt;
        boolean _selected;

        public FahrtRendererComponent() {
            setLayout(null);
            setOpaque(false);
        }

        public void setFahrt(Fahrt fahrt) {
            _fahrt = fahrt;
        }

        public String getToolTipText() {
            return "<html><b>" + _fahrt.getFahrtNummer() + "</b><br/" + _fahrt.getBegin().toDisplayString() + " - "
                    + _fahrt.getEnd().toDisplayString() + "<br/ " + _fahrt.getBeginOrt() + " - " + _fahrt.getEndeOrt()
                    + "</html>";
        }

        public void setSelected(boolean selected) {
            _selected = selected;
        }

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
                g.setColor(Color.YELLOW);
            }
            g.fillRect(0, y, width - 1, height / 3);
            // Rahmen
            g.setColor(Color.BLACK);
            g.drawRect(0, y, width - 1, height / 3);

            // Balkenbeschriftung
            GraphicsHelper.drawStringCenteredVCenter(g, _fahrt.getFahrtNummer(), 0, width, height / 2);

            // beginn- und endeort
            g.drawString(_fahrt.getBeginOrt(), 0, y);
            GraphicsHelper.drawStringRightAlignedVTop(g, _fahrt.getEndeOrt(), width - 1, yEnd);

        }

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
