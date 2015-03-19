/*
 *  File: DefaultTimeScaleRenderer.java 
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

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import de.jaret.util.date.DateUtils;
import de.jaret.util.date.JaretDate;
import de.jaret.util.swing.GraphicsHelper;
import de.jaret.util.ui.timebars.TickScaler;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.TickScaler.Range;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * A default renderer for a time scale to be used in a TimeBarViewer.
 * 
 * @author Peter Kliem
 * @version $Id: OldDefaultTimeScaleRenderer.java 835 2009-02-14 21:03:52Z kliem $
 */
public class OldDefaultTimeScaleRenderer implements TimeScaleRenderer {
    /** rendering component. */
    protected MyTimeScaleRenderer _renderer = new MyTimeScaleRenderer();

    /**
     * {@inheritDoc}
     */
    public JComponent getRendererComponent(TimeBarViewer tbv, boolean top) {
        _renderer.setTimeBarViewer(tbv);
        _renderer.setTop(top);
        return _renderer;
    }

    /**
     * {@inheritDoc}
     */
    public int getHeight() {
        return 50;
    }

    /**
     * JComponent for renderuing the timescale.
     * 
     * @author kliem
     * @version $Id: OldDefaultTimeScaleRenderer.java 835 2009-02-14 21:03:52Z kliem $
     */
    @SuppressWarnings("serial")
    class MyTimeScaleRenderer extends JComponent {
        /** the viewer the renderer is used for. */
        private TimeBarViewer _tbv;
        /** true if the position is top. */
        boolean _top;

        /**
         * Set the viewer.
         * 
         * @param tbv viewer
         */
        public void setTimeBarViewer(TimeBarViewer tbv) {
            _tbv = tbv;
        }

        /**
         * Set the time scale position.
         * 
         * @param top true for top
         */
        public void setTop(boolean top) {
            _top = top;
        }

        private int xForDate(JaretDate date) {
            int x = _tbv.xForDate(date);
            x -= _tbv.getHierarchyWidth() + _tbv.getYAxisWidth();
            return x;
        }

        private JaretDate dateForX(int x) {
            return _tbv.dateForX(x + _tbv.getHierarchyWidth() + _tbv.getYAxisWidth());
        }

        /**
         * {@inheritDoc}
         */
        public void paintComponent(Graphics g) {
            if (_tbv.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL) {
                paintHorizontal(g);
            } else {
                paintVertical(g);
            }
        }

        /**
         * paint the horizontal scale.
         */
        public void paintHorizontal(Graphics g) {

            int idx = TickScaler.getTickIdx(_tbv.getPixelPerSecond());
            int majTick = TickScaler.getMajorTickMinutes(idx);
            int minTick = TickScaler.getMinorTickMinutes(idx);
            TickScaler.Range range = TickScaler.getRange(idx);

            // first date
            JaretDate date = _tbv.getStartDate().copy().backMinutes(2 * majTick);

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

            int basey;
            int minorOff;
            int majorOff;
            int majorLabelOff;
            int dayOff;
            if (!_top) {
                basey = 0;
                minorOff = 5;
                majorOff = 10;
                majorLabelOff = 22;
                dayOff = 34;
            } else {
                basey = getHeight() - 1;
                minorOff = -5;
                majorOff = -10;
                majorLabelOff = -10;
                dayOff = -22;
            }
            int oy = basey;

            // draw top line
            g.drawLine(0, oy, getWidth(), oy);

            // draw the minor ticks
            while (xForDate(date) < getWidth()) {
                int x = xForDate(date);
                g.drawLine(x, oy, x, oy + minorOff);
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
            // draw the major ticks
            while (xForDate(date) < getWidth()) {
                int x = xForDate(date);
                g.drawLine(x, oy, x, oy + majorOff);
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

            // labels: draw every two major ticks
            date = save.copy();
            int lastDay = date.getDay();
            int width = getWidth();
            while (xForDate(date) < width + 100) {
                int x = xForDate(date);
                if (date.getMinutes() % (majTick * 2) == 0) {
                    // Second line
                    String str = null;
                    if (range == Range.HOUR) {
                        // time
                        str = date.toDisplayStringTime();
                    } else if (range == Range.DAY) {
                        // day
                        str = date.getShortDayOfWeekString();
                    } else if (range == Range.WEEK) {
                        // week
                        str = "KW" + date.getWeekOfYear();
                    } else if (range == Range.MONTH) {
                        // month
                        str = Integer.toString(date.getYear());
                    }
                    // draw
                    GraphicsHelper.drawStringCentered(g, str, x, oy + majorLabelOff);
                    // first line
                    if (range == Range.HOUR) {
                        if (date.getDay() != lastDay) {
                            str = date.getDay() + ". (" + date.getDayOfWeekString() + ")";
                        } else {
                            str = "";
                        }
                        lastDay = date.getDay();
                    } else if (range == Range.DAY || range == Range.WEEK) {
                        str = date.getDay() + ".";
                    } else if (range == Range.MONTH) {
                        str = date.getMonthString();
                    }
                    GraphicsHelper.drawStringCentered(g, str, x, oy + dayOff);
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

        /**
         * paint the vertical scale.
         */
        public void paintVertical(Graphics g) {

            int idx = TickScaler.getTickIdx(_tbv.getPixelPerSecond());
            int majTick = TickScaler.getMajorTickMinutes(idx);
            int minTick = TickScaler.getMinorTickMinutes(idx);
            TickScaler.Range range = TickScaler.getRange(idx);

            // first date
            JaretDate date = _tbv.getStartDate().copy().backMinutes(majTick * 3);

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

            int basex;
            int minorOff;
            int majorOff;
            if (!_top) {
                basex = 0;
                minorOff = 5;
                majorOff = 10;
            } else {
                basex = getWidth() - 1;
                minorOff = -5;
                majorOff = -10;
            }
            int ox = basex;

            // draw left/right line
            if (_top) {
                g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
            } else {
                g.drawLine(0, 0, 0, getHeight());
            }

            // draw the minor ticks
            while (xForDate(date) < getHeight()) {
                int y = xForDate(date);
                g.drawLine(ox, y, ox + minorOff, y);
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
            // draw the major ticks
            while (xForDate(date) < getHeight()) {
                int y = xForDate(date);
                g.drawLine(ox, y, ox + majorOff, y);
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

            // labels: draw every two major ticks
            date = save.copy();
            int height = getHeight();
            while (xForDate(date) < height + 50) {
                int y = xForDate(date);
                if (date.getMinutes() % (majTick * 2) == 0) {
                    // Second line
                    String str = null;
                    if (range == Range.HOUR) {
                        // time
                        str = date.toDisplayStringTime();
                    } else if (range == Range.DAY) {
                        // day
                        str = date.getShortDayOfWeekString();
                    } else if (range == Range.WEEK) {
                        // week
                        str = "KW" + date.getWeekOfYear();
                    } else if (range == Range.MONTH) {
                        // month
                        str = Integer.toString(date.getYear());
                    }
                    // draw
                    if (_top) {
                        GraphicsHelper.drawStringRightAlignedVCenter(g, str, ox + majorOff - 1, y);
                    } else {
                        GraphicsHelper.drawStringLeftAlignedVCenter(g, str, ox + majorOff + 1, y);
                    }
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

        /**
         * {@inheritDoc}
         */
        public String getToolTipText(MouseEvent event) {
            if (_tbv.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL) {
                JaretDate date = dateForX(event.getX());
                return date.toDisplayString();
            } else {
                JaretDate date = dateForX(event.getY());
                return date.toDisplayString();
            }
        }
    }

}
