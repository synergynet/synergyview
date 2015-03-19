/*
 *  File: DefaultGridRenderer.java 
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
import java.awt.Graphics;

import javax.swing.JComponent;

import de.jaret.util.date.DateUtils;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TickScaler;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.TickScaler.Range;
import de.jaret.util.ui.timebars.strategy.ITickProvider;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * Simple default implementation of a GridRenderer rendering the major and minor ticks using two different colors.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultGridRenderer.java 881 2009-09-22 21:25:47Z kliem $
 */
public class DefaultGridRenderer implements GridRenderer {
    /** default color for major grid lines. */
    private static final Color MAJORGRIDCOLOR = new Color(200, 200, 200);
    /** default color for minor grid lines. */
    private static final Color MINORGRIDCOLOR = new Color(230, 230, 230);
    /** approximate number of minutes in a month. */
    private static final double APPROX_MONTH_MINUTES = (24.0 * 60.0 * 7.0 * 4.0);

    /** color used to paint the major grid. */
    protected Color _majorGridColor = MAJORGRIDCOLOR;

	/** color used to paint the minor grid. */
    protected Color _minorGridColor = MINORGRIDCOLOR;
    
    
    /** component used for painting. */
    protected MyGridRenderer _component = new MyGridRenderer();

    /** external tick provider. */
    protected ITickProvider _tickProvider = null;

    /**
     * {@inheritDoc}
     */
    public JComponent getRendererComponent(TimeBarViewer tbv) {
        _component.setTimeBarViewer(tbv);
        return _component;
    }

    /**
     * Set a tick provider to determine the ticks to be drawn. If no tick provider is set the defaulst by the tick
     * scaler will be used.
     * 
     * @param tickProvider tick provider to be used or <code>null</code>.
     */
    public void setTickProvider(ITickProvider tickProvider) {
        _tickProvider = tickProvider;
    }

    /**
     * Retrieve the major grid color.
     * @return the major grid color
     */
    public Color getMajorGridColor() {
		return _majorGridColor;
	}

    /**
     * Set the major grid color.
     * @param majorGridColor the Color to use for the major grid
     */
	public void setMajorGridColor(Color majorGridColor) {
		_majorGridColor = majorGridColor;
	}

	/**
	 * Retrieve the minor grid color.
	 * @return the minor grid color
	 */
	public Color getMinorGridColor() {
		return _minorGridColor;
	}

	/**
	 * Set the minor grid color.
	 * @param minorGridColor the color to use for the minor grid
	 */
	public void setMinorGridColor(Color minorGridColor) {
		_minorGridColor = minorGridColor;
	}

    
    /**
     * JComponent for drawing the grid.
     * 
     * @author kliem
     * @version $Id: DefaultGridRenderer.java 881 2009-09-22 21:25:47Z kliem $
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

        /**
         * Calculate the x coordinate for a given date.
         * 
         * @param date date in question
         * @return x coord
         */
        private int xForDate(JaretDate date) {
            int x = _tbv.xForDate(date);
            x -= _tbv.getHierarchyWidth() + _tbv.getYAxisWidth();
            return x;
        }

        /**
         * {@inheritDoc}
         */
        public void paintComponent(Graphics g) {

            boolean horizontal = _tbv.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;

            if (_tickProvider == null) {
                paint(g, horizontal);
            } else {
                paint(g, horizontal, _tickProvider);
            }
        }

        protected void paint(Graphics g, boolean horizontal, ITickProvider tickProvider) {
            // draw minor ticks
            Color color = g.getColor();
            g.setColor(_minorGridColor);

            for (JaretDate tickDate : tickProvider.getMinorTicks(_tbv.getDelegate())) {

                int coord = xForDate(tickDate);
                if (horizontal) {
                    g.drawLine(coord, 0, coord, getHeight());
                } else {
                    g.drawLine(0, coord, getWidth(), coord);
                }
            }
            // draw major ticks
            g.setColor(_majorGridColor);

            for (JaretDate tickDate : tickProvider.getMajorTicks(_tbv.getDelegate())) {
                int coord = xForDate(tickDate);
                if (horizontal) {
                    g.drawLine(coord, 0, coord, getHeight());
                } else {
                    g.drawLine(0, coord, getWidth(), coord);
                }
            }
            g.setColor(color);
        }

        protected void paint(Graphics g, boolean horizontal) {
            Color color = g.getColor();
            // first date
            JaretDate date = _tbv.getStartDate().copy();

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

            date.backMinutes(majTick); // minor ticks should start before the
            // major ticks

            int max = horizontal ? getWidth() : getHeight();

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
                    int adv = (int) Math.round((double) minTick / APPROX_MONTH_MINUTES);
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
                    int adv = (int) Math.round((double) majTick / APPROX_MONTH_MINUTES);
                    if (adv == 0) {
                        adv = 1;
                    }
                    date.advanceMonths(adv);
                } else {
                    date.advanceMinutes(majTick);
                }
            }
            g.setColor(color);
        }
    }

}
