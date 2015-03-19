/*
 *  File: CalendarGridRenderer.java 
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
package de.jaret.examples.timebars.calendar.swing.renderer;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import de.jaret.util.date.DateUtils;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TickScaler;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.TickScaler.Range;
import de.jaret.util.ui.timebars.strategy.ITickProvider;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.GridRenderer;

/**
 * Grid renderer for the calendar example.
 * 
 * @author Peter Kliem
 * @version $Id: CalendarGridRenderer.java 836 2009-02-14 21:24:39Z kliem $
 */
public class CalendarGridRenderer implements GridRenderer {
    /** color for major grid lines. */
    private static final Color MAJORGRIDCOLOR = new Color(200, 200, 200);
    /** color for minor grid lines. */
    private static final Color MINORGRIDCOLOR = new Color(230, 230, 230);

    private static final Color WORK_COLOR = new Color(246, 249, 169);
    private static final Color NONWORK_COLOR = new Color(214, 218, 104);
    
    private JaretDate _beginWork = new JaretDate(1, 5, 2007, 8, 0, 0);
    private JaretDate _endWork = new JaretDate(1, 5, 2007, 18, 0, 0);
    
    
    /** component used for painting. */
    protected MyGridRenderer _component = new MyGridRenderer();

    /**
     * {@inheritDoc}
     */
    public JComponent getRendererComponent(TimeBarViewer tbv) {
        _component.setTimeBarViewer(tbv);
        return _component;
    }

    /**
     * JComponent for drawin gthe grid.
     * 
     * @author kliem
     * @version $Id: CalendarGridRenderer.java 836 2009-02-14 21:24:39Z kliem $
     */
    @SuppressWarnings("serial")
    class MyGridRenderer extends JComponent {
        /** the viewer. */
        private TimeBarViewer _tbv;

        /**
         * Set the viewer.
         * 
         * @param tbv the viewer
         */
        public void setTimeBarViewer(TimeBarViewer tbv) {
            _tbv = tbv;
        }

        private int xForDate(JaretDate date) {
            return _tbv.xForDate(date)-_tbv.getHierarchyWidth()-_tbv.getYAxisWidth();
        }

        /**
         * {@inheritDoc}
         */
        public void paintComponent(Graphics g) {

            boolean horizontal = _tbv.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;

            // first date
            JaretDate date = _tbv.getStartDate().copy();
            // TODO
            if (date.getHours() == 23 && date.getMinutes() == 59) {
                date.advanceSeconds(1);
            }

            int idx = TickScaler.getTickIdx(_tbv.getPixelPerSecond());
            int majTick = TickScaler.getMajorTickMinutes(idx);
            int minTick = TickScaler.getMinorTickMinutes(idx);
            Range range = TickScaler.getRange(idx);

            // clean starting date on a major tick minute position (starting
            // with a day)
            date.setMinutes(0);
            date.setHours(0);
            date.setSeconds(0);
            // if range is week take a week starting point
            if (range == Range.WEEK) {
                while (date.getDayOfWeek() != DateUtils.getFirstDayOfWeek()) {
                    date.backDays(1.0);
                }
            } else if (range == Range.MONTH) {
                // month -> month starting point
                date.setDay(1);
            }
            JaretDate save = date.copy();
            
            
            // background painting
            Color fg = g.getColor();
            int end = xForDate(date.setTime(_beginWork.getHours(), _beginWork.getMinutes(), 0));
            int end2 = xForDate(date.setTime(_endWork.getHours(), _endWork.getMinutes(), 0));
            int end3 = xForDate(date.setTime(23,59,59));
            if (horizontal) {
                g.setColor(NONWORK_COLOR);
                g.fillRect(0, 0, end, getHeight());
                g.setColor(WORK_COLOR);
                g.fillRect(end,0,  end2 - end, getHeight());
                g.setColor(NONWORK_COLOR);
                g.fillRect(end2, 0, end3, getHeight());
            } else {
                g.setColor(NONWORK_COLOR);
                g.fillRect(0, 0, getWidth(), end);
                g.setColor(WORK_COLOR);
                g.fillRect(0, end, getWidth(), end2 - end);
                g.setColor(NONWORK_COLOR);
                g.fillRect(0, end2, getWidth(), end3);
            }
            g.setColor(fg);

            
            date.backMinutes(majTick); // minor ticks should start before the
            // major ticks

            int max = horizontal ? getWidth() : getHeight();

            date = save.copy();
            // draw the minor grid
            g.setColor(MINORGRIDCOLOR);
            while (xForDate(date) < max) {
                int coord = xForDate(date);
                if (horizontal) {
                    g.drawLine(coord, 0, coord, getHeight());
                } else {
                    g.drawLine(0, coord, getWidth(), coord);
                }
                if (range == Range.MONTH) {
                    int adv = Math.round(minTick / (24 * 60 * 7 * 4));
                    if (adv == 0) {
                        adv = 1;
                    }
                    date.advanceMonths(adv);
                } else {
                    date.advanceMinutes(minTick);
                }
            }

            date = save.copy();
            // draw the major grid
            g.setColor(MAJORGRIDCOLOR);
            while (xForDate(date) < max) {
                int coord = xForDate(date);
                if (horizontal) {
                    g.drawLine(coord, 0, coord, getHeight());
                } else {
                    g.drawLine(0, coord, getWidth(), coord);
                }
                if (range == Range.MONTH) {
                    int adv = Math.round(majTick / (24 * 60 * 7 * 4));
                    if (adv == 0) {
                        adv = 1;
                    }
                    date.advanceMonths(adv);
                } else {
                    date.advanceMinutes(majTick);
                }
            }
        }
    }

    /**
     * {@inheritDoc} Does nothing.
     */
    public void setTickProvider(ITickProvider tickProvider) {
        // do nothing
    }

}
