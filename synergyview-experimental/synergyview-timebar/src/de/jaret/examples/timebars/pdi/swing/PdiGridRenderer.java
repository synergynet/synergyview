/*
 *  File: PdiGridRenderer.java 
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
import java.util.Calendar;

import javax.swing.JComponent;

import de.jaret.examples.timebars.pdi.model.PdiCalendar;
import de.jaret.examples.timebars.pdi.model.PdiDay;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.strategy.ITickProvider;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.GridRenderer;

/**
 * @author Peter Kliem
 * @version $Id: PdiGridRenderer.java 836 2009-02-14 21:24:39Z kliem $
 */
public class PdiGridRenderer implements GridRenderer {
    private Color MAJORGRIDCOLOR = new Color(200, 200, 200);
    private Color SATURDAY_COLOR = new Color(255, 230, 230);
    private Color SUNDAY_COLOR = new Color(255, 200, 200);

    protected PdiCalendar _kalender;
    protected GridRenderer _component = new GridRenderer();

    public PdiGridRenderer(PdiCalendar kalender) {
        _kalender = kalender;
    }

    public JComponent getRendererComponent(TimeBarViewer tbv) {
        _component.setTimeBarViewer(tbv);
        return _component;
    }

    class GridRenderer extends JComponent {
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

        public void paintComponent(Graphics g) {
            JaretDate date = _tbv.getStartDate().copy();
            date.setHours(0);
            while (_tbv.getEndDate().compareTo(date) > 0) {
                PdiDay tag = _kalender.getTag(date);
                if (tag != null) {
                    int x = xForDate(tag.getDate());
                    int width = (int) (_tbv.getPixelPerSecond() * 24.0 * 60.0 * 60.0);
                    g.setColor(MAJORGRIDCOLOR);
                    g.drawLine(x, 0, x, getHeight());
                    int dayOfWeek = date.getDayOfWeek();
                    if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                        g.setColor(dayOfWeek == Calendar.SATURDAY ? SATURDAY_COLOR : SUNDAY_COLOR);
                        g.fillRect(x, 0, width, getHeight());
                    }
                }
                date.advanceDays(1);
            }
        }
    }

    /**
     * {@inheritDoc} does nothing.
     */
    public void setTickProvider(ITickProvider tickProvider) {
        // do nothing
    }
}
