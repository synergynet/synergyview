/*
 *  File: PdiRenderer.java 
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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.JComponent;

import de.jaret.examples.timebars.pdi.model.Assignment;
import de.jaret.examples.timebars.pdi.model.Duty;
import de.jaret.examples.timebars.pdi.model.Taetigkeit;
import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDateFormatter;
import de.jaret.util.swing.GraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.TimeBarRenderer;

public class PdiRenderer implements TimeBarRenderer {
    DienstRenderer _component = new DienstRenderer();

    /**
     * {@inheritDoc}
     */
    public JComponent getTimeBarRendererComponent(TimeBarViewer tbv, Interval value, boolean isSelected, boolean overlapping) {
        if (value instanceof Assignment) {
            _component.setDienst((Taetigkeit) ((Assignment) value).getTaetigkeit());
        } else {
            _component.setDienst((Duty) value);
        }
        _component.setSelected(isSelected);
        return _component;
    }

    class DienstRenderer extends JComponent {
        Taetigkeit _taetigkeit;
        boolean _selected;

        public DienstRenderer() {
            setLayout(null);
            setOpaque(false);
        }

        public void setDienst(Taetigkeit t) {
            _taetigkeit = t;
        }

        /**
         * {@inheritDoc}
         */
        public String getToolTipText() {
            if (_taetigkeit instanceof Duty) {
                Duty dienst = (Duty) _taetigkeit;
                return "<html><b>"
                        + dienst.getDienstNr()
                        + "</b><br/"
                        + _taetigkeit.getBegin().toDisplayString()
                        + " - "
                        + _taetigkeit.getEnd().toDisplayString()
                        + "<br/ Az.:"
                        + JaretDateFormatter.secondsToDisplayString(_taetigkeit.getEnd().diffSeconds(
                                _taetigkeit.getBegin())) + "</html>";
            } else {
                return "<html><b>" + "Ttigkeit" + "</b><br/" + _taetigkeit.getBegin().toDisplayString() + " - "
                        + _taetigkeit.getEnd().toDisplayString() + "</html>";
            }
        }

        public void setSelected(boolean selected) {
            _selected = selected;
        }

        /**
         * {@inheritDoc}
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
                g.setColor(Color.LIGHT_GRAY);
            }
            // body
            g.fillRect(0, y, width - 1, bheight);
            // Ecke falls Probleme vorhanden sind
            if (_taetigkeit.getProbleme() != null && _taetigkeit.getProbleme().size() != 0) {
                g.setColor(Color.RED);
                g.fillRect(width - 1 - bheight / 2, y, bheight / 2, bheight / 2);
            }
            // Rahmen
            g.setColor(Color.BLACK);
            g.drawRect(0, y, width - 1, bheight);

            // store the containing rectangle in a client property
            Rectangle containingRect = new Rectangle(0 + getX(), y + getY(), width - 1, bheight);
            putClientProperty(CONTAINING_RECTANGLE, containingRect);

            String name;
            if (_taetigkeit instanceof Duty) {
                name = ((Duty) _taetigkeit).getDienstNr();
            } else {
                name = "Ttigkeit";
            }

            int twidth = GraphicsHelper.getStringDrawingWidth(g, name);
            // Balkenbeschriftung nur wenn sie passt
            if (getWidth() > twidth + 2) {
                GraphicsHelper.drawStringCenteredVCenter(g, name, 0, width, height / 2);
            }

            // beginn- und endeort
            // g.drawString(_fahrt.getBeginOrt(), 0,y);
            // GraphicsHelper.drawStringRightAlignedVTop(g, _fahrt.getEndeOrt(),
            // width-1, yEnd);

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.Component#setBounds(int, int, int, int)
         */
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, width, height);
            // store the containing rectangle in a client property
            int bheight = height / 3;
            int ys = height / 3;
            Rectangle containingRect = new Rectangle(x, ys + y, width - 1, bheight);
            putClientProperty(CONTAINING_RECTANGLE, containingRect);
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
         */
        public String getToolTipText(MouseEvent event) {
            if (_taetigkeit.getProbleme() == null || _taetigkeit.getProbleme().size() == 0) {
                return super.getToolTipText(event);
            } else {
                int bheight = getHeight() / 3;
                int y = getHeight() / 3;
                if (event.getX() >= getWidth() - 1 - bheight / 2 && event.getY() >= y
                        && event.getY() <= y + bheight / 2) {
                    return problemTT();
                } else {
                    return getToolTipText();
                }
            }
        }

        /**
         * @return
         */
        private String problemTT() {
            StringBuffer buf = new StringBuffer();
            buf.append("<html>");
            Iterator it = _taetigkeit.getProbleme().iterator();
            while (it.hasNext()) {
                String problem = (String) it.next();
                buf.append(problem);
                if (it.hasNext()) {
                    buf.append("</br");
                }
            }

            buf.append("</html>");
            return buf.toString();
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
