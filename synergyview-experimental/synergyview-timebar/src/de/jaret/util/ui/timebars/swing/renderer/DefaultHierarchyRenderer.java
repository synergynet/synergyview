/*
 *  File: DefaultHierarchyRenderer.java 
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

import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * Default implementation of a swing hierarchy renderer.
 * 
 * @author kliem
 * @version $Id: DefaultHierarchyRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public class DefaultHierarchyRenderer implements HierarchyRenderer {
    /**
     * Size for the hierarchy symbols.
     */
    private static final int SIZE = 12;

    /** component used to render the hierarchy. */
    private MyHierarchyRenderer _renderer = new MyHierarchyRenderer();

    /**
     * Default constructor.
     */
    public DefaultHierarchyRenderer() {
        _renderer = new MyHierarchyRenderer();
    }

    /**
     * {@inheritDoc}
     */
    public JComponent getHierarchyRendererComponent(TimeBarViewer tbv, TimeBarRow row, boolean selected,
            boolean expanded, boolean leaf, int level, int depth) {
        _renderer.configure(tbv, row, selected, expanded, leaf, level, depth);
        _renderer.setToolTipText("Hierarchy");
        return _renderer;
    }

    /**
     * {@inheritDoc}
     */
    public int getWidth() {
        return SIZE + 4;
    }

    class MyHierarchyRenderer extends JComponent {
        TimeBarViewer _tbv;

        TimeBarRow _row;

        boolean _selected;

        boolean _expanded;

        boolean _leaf;

        int _level;

        int _depth;

        public void configure(TimeBarViewer tbv, TimeBarRow row, boolean selected, boolean expanded, boolean leaf,
                int level, int depth) {
            _tbv = tbv;
            _row = row;
            _expanded = expanded;
            _leaf = leaf;
            _level = level;
            _depth = depth;
        }

        /**
         * {@inheritDoc}
         */
        public void paintComponent(Graphics gc) {
            int offx = (this.getWidth() - SIZE) / (_depth + 1);

            int x = offx * _level + SIZE / 2;

            int y = (this.getHeight() - SIZE) / 2;
            if (_leaf) {
                drawLeaf(gc, SIZE, x, y);
            } else if (_expanded) {
                drawMinus(gc, SIZE, x, y);
            } else {
                drawPlus(gc, SIZE, x, y);
            }

            // draw tree connections
            boolean drawConnections = false;
            if (drawConnections) {
                for (int i = 0; i <= _level - 1; i++) {
                    x = offx * i + SIZE;
                    gc.drawLine(x, 0, x, this.getHeight());

                }
                x = offx * _level + SIZE;
                gc.drawLine(x, 0, x, (this.getHeight() - SIZE) / 2);
                gc.drawLine(x, ((this.getHeight() - SIZE) / 2) + SIZE, x, y + this.getHeight());

                if (_level > 0) {
                    x = offx * (_level - 1) + SIZE;
                    int xx = offx * (_level) + SIZE;
                    y = ((this.getHeight() - SIZE) / 2) + SIZE / 2;
                    gc.drawLine(x, y, xx - SIZE / 2, y);
                }
            }

        }

        protected void drawPlus(Graphics gc, int size, int x, int y) {
            gc.drawLine(x + 3, y + size / 2, x + size - 3, y + size / 2);
            gc.drawLine(x + size / 2, y + 3, x + size / 2, y + size - 3);
            gc.drawRect(x, y, size, size);
        }

        protected void drawMinus(Graphics gc, int size, int x, int y) {
            gc.drawLine(x + 3, y + size / 2, x + size - 3, y + size / 2);
            gc.drawRect(x, y, size, size);
        }

        protected void drawLeaf(Graphics gc, int size, int x, int y) {
            Color bg = gc.getColor();
            gc.setColor(Color.BLACK);
            gc.fillOval(x + size / 2, y + size / 2, size / 2, size / 2);
            gc.setColor(bg);
        }
    }
}
