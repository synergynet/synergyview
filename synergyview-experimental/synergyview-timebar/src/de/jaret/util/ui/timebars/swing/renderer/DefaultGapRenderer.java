/*
 *  File: DefaultGapRenderer.java 
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

import de.jaret.util.date.Interval;
import de.jaret.util.misc.FormatHelper;
import de.jaret.util.swing.GraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * Default gap renderer showing the time between intervals and an double headed arrow.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultGapRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public class DefaultGapRenderer implements TimeBarGapRenderer {
    /** the rendering component. */
    protected GapRenderer _component = new GapRenderer();

    /**
     * {@inheritDoc}
     */
    public JComponent getTimeBarGapRendererComponent(TimeBarViewer tbv, TimeBarRow row, Interval interval1,
            Interval interval2) {
        boolean horizontal = tbv.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        _component.setUp(horizontal, interval1, interval2);

        return _component;
    }

    /**
     * {@inheritDoc}
     */
    public int getMinimumWidth() {
        return -1; // no minimum width required
    }

    /**
     * Gap renderer component.
     * 
     * @author kliem
     * @version $Id: DefaultGapRenderer.java 800 2008-12-27 22:27:33Z kliem $
     * @SuppressWarnings("serial")
     */
     
    class GapRenderer extends JComponent {
        /** earlier interval. */
        private Interval _i1;
        /** later interval. */
        private Interval _i2;
        /** true for horizontal orientation. */
        private boolean _horizontal;

        /**
         * Set up the component for rendering.
         * 
         * @param horizontal true for horizontal orientation
         * @param interval1 earlier interval
         * @param interval2 later interval
         */
        public void setUp(boolean horizontal, Interval interval1, Interval interval2) {
            _horizontal = horizontal;
            _i1 = interval1;
            _i2 = interval2;
        }

        /**
         * {@inheritDoc}
         */
        protected void paintComponent(Graphics g) {
            // on the edge one of the intervals may be null
            // for this renderer this means there is no need to paint anything
            if (_i1 != null && _i2 != null) {
                int diffminutes = (int) _i2.getBegin().diffMinutes(_i1.getEnd());
                String timeString = FormatHelper.NFInt2Digits().format(diffminutes / 60) + ":"
                        + FormatHelper.NFInt2Digits().format(diffminutes % 60);
                int twidth = GraphicsHelper.getStringDrawingWidth(g, timeString);
                int theight = GraphicsHelper.getStringDrawingHeight(g, timeString);

                g.setColor(Color.DARK_GRAY);
                if (_horizontal) {
                    // center the timeString
                    if (getWidth() > twidth + 3 && getHeight() / 2 >= theight) {
                        GraphicsHelper.drawStringCentered(g, timeString, getWidth() / 2, getHeight() / 2 - 2);
                    }
                    // draw a line with arrow endings
                    if (getWidth() > 15) {
                        GraphicsHelper.drawArrowLine(g, 2, getHeight() / 2, getWidth() - 3, getHeight() / 2, 4, true,
                                true);
                    }
                } else {
                    // center the timeString
                    if (getHeight() > theight + 3 && getWidth() >= twidth) {
                        GraphicsHelper.drawStringCenteredPoint(g, timeString, getWidth() / 2, getHeight() / 2);
                    }
                    // draw a line with arrow endings
                    if (getWidth() > 15) {
                        GraphicsHelper.drawArrowLineVertical(g, getWidth() / 2, 2, getWidth() / 2, getHeight() - 3, 3,
                                4, true, true);
                    }
                }

            }
        }

    }

}
