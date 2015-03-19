package de.jaret.examples.timebars.calendar.swing.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import de.jaret.examples.timebars.calendar.model.Appointment;
import de.jaret.util.date.Interval;
import de.jaret.util.swing.GraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.TimeBarRenderer;

public class AppointmentRenderer implements TimeBarRenderer {
    /** insets for drawing. */
    protected static final int INSETS = 2;
    /** width or height times this factor = perentage used as the non painted border. */
    protected static final double BORDERFACTOR = 0.1;

    /** component used for rendering. */
    private AppRendererComponent _component = new AppRendererComponent();

    public JComponent getTimeBarRendererComponent(TimeBarViewer tbv, Interval value, boolean isSelected,
            boolean overlapping) {
        _component.setUp(tbv, value, isSelected, overlapping);
        return _component;
    }

    /**
     * Component for the renderer.
     * 
     * @author kliem
     * @version $Id: AppointmentRenderer.java 869 2009-07-07 19:32:45Z kliem $
     */
    @SuppressWarnings("serial")
    class AppRendererComponent extends JComponent {
        private TimeBarViewer _tbv;
        private Interval _interval;
        private boolean _selected;
        private boolean _overlapping;

        /** textarea used for rendering the appointment text. */
        private JTextArea _area = new JTextArea();

        /**
         * Set up the compoentn for rendering.
         * 
         * @param tbv viewer
         * @param value interval
         * @param isSelected true if selected
         * @param overlapping true if overlapping
         */
        public void setUp(TimeBarViewer tbv, Interval value, boolean isSelected, boolean overlapping) {
            _tbv = tbv;
            _interval = value;
            _selected = isSelected;
            _overlapping = overlapping;
        }

        /**
         * {@inheritDoc}
         */
        protected void paintComponent(Graphics g) {
            // draw focus
            // drawFocus(gc, drawingArea, delegate, interval, selected, printing, overlap);

            boolean horizontal = _tbv.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
            Rectangle iRect = getIRect(horizontal, _overlapping);

            Color fg = g.getColor();
            String str = _interval.toString();

            if (horizontal) {
                // horizontal is fallback ONLY
                if (!_selected) {
                    g.setColor(Color.LIGHT_GRAY);
                } else {
                    g.setColor(Color.BLUE);
                }
                g.fillRect(iRect.x, iRect.y, iRect.width, iRect.height);
                g.setColor(Color.BLACK);
                g.drawRect(iRect.x, iRect.y, iRect.width, iRect.height);
                GraphicsHelper.drawStringCentered(g, str, iRect);
            } else {
                // vertical
                String timeString = getTimeString((Appointment) _interval);

                if (!_selected) {
                    g.setColor(Color.LIGHT_GRAY);
                } else {
                    g.setColor(Color.BLUE);
                }
                Color bgx = g.getColor();
                // fill the background
                g.fillRect(iRect.x, iRect.y, iRect.width, iRect.height);
                // draw the upper border containing the time span
                int h = 20;
                g.setColor(Color.YELLOW);
                g.fillRect(iRect.x, iRect.y, iRect.width, h);
                g.setColor(Color.BLACK);
                GraphicsHelper.drawStringCentered(g, timeString, new Rectangle(iRect.x, iRect.y, iRect.width, h));
             
                Rectangle textRect = new Rectangle(iRect.x + INSETS, iRect.y + h + INSETS, iRect.width - INSETS,
                        iRect.height - INSETS - h);
                _area.setBounds(textRect);
                _area.setBackground(bgx);
                _area.setText(((Appointment) _interval).getText());
                Graphics gg = g.create(textRect.x, textRect.y, textRect.width, textRect.height);
                _area.paint(gg);
                gg.dispose();

                // draw the border
                g.setColor(Color.BLACK);
                g.drawRect(iRect.x, iRect.y, iRect.width, iRect.height);
            }
            g.setColor(fg);

        }

        private String getTimeString(Appointment interval) {
            return interval.getBegin().toDisplayStringTime(false) + " - "
                    + interval.getEnd().toDisplayStringTime(false);
        }

        public Rectangle getContainingRectangle(TimeBarViewerDelegate delegate, Interval interval,
                Rectangle drawingArea, boolean overlapping) {
            boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
            Rectangle iRect = getIRect(horizontal, overlapping);
            return iRect;
        }

        protected Rectangle getIRect(boolean horizontal, boolean overlap) {
            int cwidth = getWidth();
            int cheight = getHeight();
            if (horizontal) {
                int borderHeight = (int) (cheight * BORDERFACTOR / 2);
                int height = cheight - (overlap ? 0 : 2 * borderHeight);
                int y = overlap ? 0 : borderHeight;
                return new Rectangle(0, y, cwidth - 1, height - 1);
            } else {
                int borderWidth = (int) (cwidth * BORDERFACTOR / 2);
                int width = cwidth - (overlap ? 0 : 2 * borderWidth);
                int x = overlap ? 0 : borderWidth;
                return new Rectangle(x, 0, width - 1, cheight - 1);
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
