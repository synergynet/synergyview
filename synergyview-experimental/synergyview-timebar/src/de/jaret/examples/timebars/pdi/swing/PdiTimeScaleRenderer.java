/*
 *  File: PdiTimeScaleRenderer.java 
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

import javax.swing.JButton;
import javax.swing.JComponent;

import de.jaret.examples.timebars.pdi.model.PdiCalendar;
import de.jaret.examples.timebars.pdi.model.PdiDay;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.TimeScaleRenderer;

/**
 * @author Peter Kliem
 * @version $Id: PdiTimeScaleRenderer.java 237 2007-02-10 21:11:50Z olk $
 */
public class PdiTimeScaleRenderer implements TimeScaleRenderer {
    protected static int RHEIGHT = 50;
    Renderer _component = new Renderer();
    PdiCalendar _kalender;

    public PdiTimeScaleRenderer(PdiCalendar kalender) {
        _kalender = kalender;
    }

    public JComponent getRendererComponent(TimeBarViewer tbv, boolean top) {
        _component.setTimeBarViewer(tbv);
        return _component;
    }

    public int getHeight() {
        return RHEIGHT;
    }

    class Renderer extends JComponent {
        JButton _button = new JButton();
        TimeBarViewer _tbv;

        public void setTimeBarViewer(TimeBarViewer tbv) {
            _tbv = tbv;
        }

        private int xForDate(JaretDate date) {
            long seconds = date.diffSeconds(_tbv.getStartDate());
            int x = (int) ((double) seconds * _tbv.getPixelPerSecond());
            return x;
        }

        private JaretDate dateForX(int x) {
            int diffSec = (int) ((double) x / _tbv.getPixelPerSecond());
            JaretDate date = new JaretDate(_tbv.getStartDate());
            date.advanceSeconds(diffSec);
            return date;
        }

        protected void paintComponent(Graphics g) {
            JaretDate date = _tbv.getStartDate().copy();
            date.setHours(0);
            while (_tbv.getEndDate().compareTo(date) > 0) {
                PdiDay tag = _kalender.getTag(date);
                if (tag != null) {
                    int x = xForDate(tag.getDate());
                    _button.setText("<html>" + tag.getDate().toDisplayStringDate() + "<br/" + tag.getBetriebstag()
                            + "</html>");
                    int width = (int) (_tbv.getPixelPerSecond() * 24.0 * 60.0 * 60.0);
                    g.setColor(Color.BLACK);
                    _button.setBounds(x, 0, width, RHEIGHT);
                    Graphics gg = g.create(x, 0, width, RHEIGHT);
                    _button.paint(gg);
                    gg.dispose();
                }
                date.advanceDays(1);
            }
        }
    }

}