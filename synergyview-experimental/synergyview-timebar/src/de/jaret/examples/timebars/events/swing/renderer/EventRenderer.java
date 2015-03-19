/*
 *  File: EventRenderer.java 
 *  Copyright (c) 2004-2009  Peter Kliem (Peter.Kliem@jaret.de)
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
package de.jaret.examples.timebars.events.swing.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

import de.jaret.examples.timebars.events.model.SampleEvent;
import de.jaret.util.date.Interval;
import de.jaret.util.swing.GraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.TimeBarRenderer;

/**
 * Renderer for events in the event example.
 * 
 * @author Peter Kliem
 * @version $Id: EventRenderer.java 803 2008-12-28 19:30:23Z kliem $
 */
public class EventRenderer implements TimeBarRenderer {
    /** size of the drawn element. */
    private static final int SIZE = 10;
    /** extend for the label. */
    private static final int MAXLABELWIDTH = 50;
    /** pixeloffset for the label drawing. */
    private static final int LABELOFFSET = 3;

    protected Color _color = Color.GRAY;
	protected Color _selectedColor = Color.BLUE;
    
    
    EventRendererComponent _eventComponent;

    TimeBarViewerDelegate _delegate;
    
    public EventRenderer() {
        _eventComponent = new EventRendererComponent();
    }

    public JComponent getTimeBarRendererComponent(TimeBarViewer tbv, Interval value, boolean isSelected,
            boolean overlapping) {
        _delegate = tbv.getDelegate();
    	if (value instanceof SampleEvent) {
            _eventComponent.setEvent((SampleEvent) value);
            _eventComponent.setSelected(isSelected);
            return _eventComponent;
        } else {
            throw new RuntimeException("unsupported " + value.getClass().getName());
        }
    }


    /**
     * {@inheritDoc}
     */
    public Rectangle getPreferredDrawingBounds(Rectangle intervalDrawingArea, TimeBarViewerDelegate delegate,
            Interval interval, boolean selected, boolean overlap) {

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        if (horizontal) {
            return new Rectangle(intervalDrawingArea.x - SIZE, intervalDrawingArea.y, intervalDrawingArea.width + 2
                    * SIZE + MAXLABELWIDTH, intervalDrawingArea.height);
        } else {
            return new Rectangle(intervalDrawingArea.x, intervalDrawingArea.y - SIZE, intervalDrawingArea.width,
                    intervalDrawingArea.height + 2 * SIZE + MAXLABELWIDTH);
        }
    }



//    /**
//     * {@inheritDoc}
//     */
//    public boolean contains(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
//        return contains(_delegate, interval, drawingArea, x, y, overlapping);
//    }

//    /**
//     * {@inheritDoc}
//     */
//    public Rectangle getContainingRectangle(Interval interval, Rectangle drawingArea, boolean overlapping) {
//        return getContainingRectangle(_delegate, interval, drawingArea, overlapping);
//    }




    
    
    
    /**
     * Rendering jcomponent for an event.
     * 
     * @author kliem
     * @version $Id: EventRenderer.java 803 2008-12-28 19:30:23Z kliem $
     */
    public class EventRendererComponent extends JComponent {
        SampleEvent _interval;
        boolean _selected;
        
        
        public EventRendererComponent() {
            setLayout(null);
            setOpaque(false);
        }

        public void setEvent(SampleEvent interval) {
            _interval = interval;
        }

        public String getToolTipText() {
            return "<html><b>" + _interval.getLabel() + "</b></html>";
        }

        public void setSelected(boolean selected) {
            _selected = selected;
        }

        protected void paintComponent(Graphics g) {
            Color bg = g.getColor();
            int height = getHeight();
            int width = getWidth();
        	Rectangle drawingArea = new Rectangle(0,0,width,height);
            boolean horizontal = _delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
            Rectangle da = getDrawingRect(drawingArea, horizontal);

            // draw focus
//            drawFocus(g, da, delegate, interval, selected, printing, overlap);


            // draw the diamond

            int[] pointsx = new int[] {da.x, da.x + da.width / 2, da.x + da.width,
                     da.x + da.width / 2};
            int[] pointsy = new int[] {da.y + da.height / 2,  da.y, 
                    da.y + da.height / 2,  da.y + da.height};
            
            if (_selected) {
            	g.setColor(_selectedColor);
            } else {
            	g.setColor(_color);
            }
            g.fillPolygon(pointsx, pointsy, pointsx.length);

            
            
            g.setColor(Color.BLACK);
            g.drawPolygon(pointsx, pointsy, pointsx.length);
        

            SampleEvent fe = (SampleEvent) _interval;

            // draw the label
            if (horizontal) {
                GraphicsHelper.drawStringVCentered(g, fe.getLabel(), da.x + da.width + LABELOFFSET, da.y, da.y
                        + da.height);
            } else {
// TODO
            	//                SwtGraphicsHelper.drawStringCentered(gc, fe.getLabel(), da.x + da.width / 2, da.y + da.height
//                        + scaleY(LABELOFFSET) + gc.textExtent(fe.getLabel()).y);
            }

            g.setColor(bg);
            
        }

        /**
         * Calculate the drawing area for the marking symbol.
         * 
         * @param drawingArea drawing area as given for the time
         * @return Rectangle for drawing the main symbol
         */
        private Rectangle getDrawingRect(Rectangle drawingArea, boolean horizontal) {
            if (horizontal) {
                int y = drawingArea.y + (drawingArea.height - 2 * SIZE) / 2;
                return new Rectangle(drawingArea.x , y, 2 * SIZE, 2 * SIZE);
            } else {
                int x = drawingArea.x + (drawingArea.width - 2 * SIZE) / 2;
                return new Rectangle(x, drawingArea.y - SIZE, 2 * SIZE, 2 * SIZE);
            }
        }

        public String getToolTipText(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x,
                int y, boolean overlapping) {
            if (contains(delegate, interval, drawingArea, x, y, overlapping)) {
                return interval.toString();
            }
            return null;
        }

        public boolean contains(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x, int y,
                boolean overlapping) {
            boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
            Rectangle da = getDrawingRect(drawingArea, horizontal);
            return da.contains(drawingArea.x + x, drawingArea.y + y);
        }

        public Rectangle getContainingRectangle(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea,
                boolean overlapping) {
            boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
            Rectangle da = getDrawingRect(drawingArea, horizontal);
            return da;
        }
        
        
        
        public boolean contains(int x, int y) {
            if (y >= getHeight() / 3 && y <= getHeight() / 3 + getHeight() / 3) {
                return true;
            } else {
                return false;
            }
        }
    }
    
    public Color getColor() {
		return _color;
	}

	public void setColor(Color color) {
		_color = color;
	}

	public Color getSelectedColor() {
		return _selectedColor;
	}

	public void setSelectedColor(Color selectedColor) {
		_selectedColor = selectedColor;
	}




    
    
}
