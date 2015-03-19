/*
 *  File: DefaultMiscRenderer.java 
 *  Copyright (c) 2004-2008  Peter Kliem (Peter.Kliem@jaret.de)
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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * Default implementation of th misc renderer for the swing time bar viewer.
 * 
 * @author kliem
 * @version $Id: DefaultMiscRenderer.java 881 2009-09-22 21:25:47Z kliem $
 */
public class DefaultMiscRenderer implements IMiscRenderer {
    /** color used for highlighting. */
    public static final Color HIGHLIGHT_COLOR = Color.RED;

    /**
     * {@inheritDoc}
     */
    public void renderRegionRect(Graphics graphics, TimeBarViewer tbv, TimeBarViewerDelegate delegate) {
        if (delegate.getRegionRect() != null) {
            Color color = graphics.getColor();
            Rectangle rect = delegate.calcRect(delegate.getRegionRect());
            graphics.setColor(Color.GRAY);
            Graphics2D g2 = (Graphics2D) graphics;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            graphics.fillRect(rect.x, rect.y, rect.width, rect.height);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            graphics.drawRect(rect.x, rect.y, rect.width, rect.height);
            graphics.setColor(color);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void renderSelectionRect(Graphics graphics, TimeBarViewer tbv, Rectangle selectionRect) {
        Color old = graphics.getColor();
    	graphics.setColor(Color.GRAY);
        graphics.drawRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
        graphics.setColor(old);
    }

    /**
     * {@inheritDoc}
     */
    public void drawRowGridLine(Graphics graphics, int x1, int y1, int x2, int y2) {
        Color color = graphics.getColor();
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.drawLine(x1, y1, x2, y2);
        graphics.setColor(color);
    }

    /**
     * {@inheritDoc}
     */
    public void drawRowBackground(Graphics graphics, int x, int y, int width, int height, boolean selected,
            boolean highlighted) {

        Color color = graphics.getColor();
        if (selected) {
            graphics.setColor(Color.BLUE);
        } else { // highlighted
            graphics.setColor(HIGHLIGHT_COLOR);
        }
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
        graphics.fillRect(x, y, width, height);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        graphics.setColor(color);

    }

}
