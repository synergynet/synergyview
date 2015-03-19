/*
 *  File: AppointmentRenderer.java 
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
package de.jaret.examples.timebars.calendar.swt.renderer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.examples.timebars.calendar.model.Appointment;
import de.jaret.examples.timebars.calendar.model.Appointment.Status;
import de.jaret.util.date.Interval;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.swt.TextRenderer;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.swt.renderer.AbstractTimeBarRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2;

/**
 * Renderer for appointments in the calendar example.
 * 
 * @author Peter Kliem
 * @version $Id: AppointmentRenderer.java 579 2007-10-04 13:54:06Z olk $
 */
public class AppointmentRenderer extends AbstractTimeBarRenderer implements TimeBarRenderer, TimeBarRenderer2 {
    /** width or height times this factor = percentage used as the non painted border. */
    protected static final double BORDERFACTOR = 0.1;

    protected static final int HEIGHTFORTIMESPAN = 20;
    protected static final int WIDTHFORSTATUS = 5;
    /** insets for drawing. */
    protected static final int INSETS = 2;
    protected static final int DRAGMARKSIZE = HEIGHTFORTIMESPAN - 8;

    protected static final RGB FREERGB = new RGB(255, 255, 255);
    protected static final RGB BOOKEDRGB = new RGB(0, 0, 255);
    protected static final RGB TENTATIVERGB = new RGB(0, 255, 255);
    protected static final RGB ABSENTRGB = new RGB(253, 11, 222);

    TimeBarViewerDelegate _delegate;

    protected Color _barColor;

    protected Color _freeColor;
    protected Color _bookedColor;
    protected Color _tentativeColor;
    protected Color _absentColor;

    /**
     * Create renderer for printing.
     * 
     * @param printer printer device
     */
    public AppointmentRenderer(Printer printer) {
        super(printer);
        _barColor = printer.getSystemColor(SWT.COLOR_YELLOW);
        _freeColor = new Color(printer, FREERGB);
        _bookedColor = new Color(printer, BOOKEDRGB);
        _tentativeColor = new Color(printer, TENTATIVERGB);
        _absentColor = new Color(printer, ABSENTRGB);
    }

    /**
     * Construct renderer for screen use.
     * 
     */
    public AppointmentRenderer() {
        super(null);
        _barColor = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
        _freeColor = new Color(Display.getCurrent(), FREERGB);
        _bookedColor = new Color(Display.getCurrent(), BOOKEDRGB);
        _tentativeColor = new Color(Display.getCurrent(), TENTATIVERGB);
        _absentColor = new Color(Display.getCurrent(), ABSENTRGB);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected,
            boolean printing, boolean overlap) {
        _delegate = delegate;
        defaultDraw(gc, drawingArea, delegate, interval, selected, printing, overlap);
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        return getToolTipText(_delegate, interval, drawingArea, x, y, overlapping);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        return contains(_delegate, interval, drawingArea, x, y, overlapping);
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getContainingRectangle(Interval interval, Rectangle drawingArea, boolean overlapping) {
        return getContainingRectangle(_delegate, interval, drawingArea, overlapping);
    }

    /**
     * {@inheritDoc}. Will create print renderes for all registered renderers.
     */
    public TimeBarRenderer createPrintrenderer(Printer printer) {
        AppointmentRenderer renderer = new AppointmentRenderer(printer);
        return renderer;
    }

    /**
     * Drawing method for default rendering.
     * 
     * @param gc GC
     * @param drawingArea drawingArea
     * @param delegate delegate
     * @param interval interval to draw
     * @param selected true for selected drawing
     * @param printing true for printing
     * @param overlap true if the interval overlaps with other intervals
     */
    private void defaultDraw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval,
            boolean selected, boolean printing, boolean overlap) {

        Appointment appointment = (Appointment) interval;

        // draw focus
        if (!printing) {
            drawFocus(gc, drawingArea, delegate, interval, selected, printing, overlap);
        }

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle iRect = getIRect(horizontal, drawingArea, overlap);

        Color bg = gc.getBackground();

        if (horizontal) {
            // horizontal is fallback ONLY
            if (!selected) {
                gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
            } else {
                gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
            }
            gc.fillRectangle(iRect);
            gc.drawRectangle(iRect);
            SwtGraphicsHelper.drawStringCentered(gc, interval.toString(), iRect);
        } else {
            // vertical

            Color bgx = gc.getBackground();
            // fill the background
            gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
            gc.fillRectangle(iRect);
            // draw the upper border containing the time span
            int h = HEIGHTFORTIMESPAN;
            int w = WIDTHFORSTATUS;
            gc.setBackground(_barColor);
            gc.fillRectangle(iRect.x + w, iRect.y, iRect.width - w, h);

            int textStartX = iRect.x + w + 2;

            // draw recur image if the appointment is recurrent
            if (appointment.isRecurring()) {
                Image img = getRecurImage(gc.getDevice());
                int imgX = iRect.x + w + 2;
                int imgY = iRect.y + (h - img.getBounds().height) / 2;
                gc.drawImage(img, imgX, imgY);
                textStartX += img.getBounds().width;
            }

            // draw lock image if the appointment is not editable
            if (!appointment.isEditable()) {
                Image img = getLockImage(gc.getDevice());
                int imgX = iRect.x + w;
                int imgY = iRect.y;
                gc.drawImage(img, imgX, imgY);
            }
            

            // draw drag area
            int dragMarkerStartX = iRect.x + iRect.width - DRAGMARKSIZE - INSETS;
            gc.drawRectangle(dragMarkerStartX, iRect.y + INSETS, DRAGMARKSIZE, DRAGMARKSIZE);
            // draw the time heading
            drawTimeString(gc, appointment, textStartX, iRect.y, dragMarkerStartX - textStartX, h);

            gc.setBackground(bgx);
            Rectangle textRect = new Rectangle(iRect.x + w + INSETS, iRect.y + h + INSETS, iRect.width - w - INSETS,
                    iRect.height - INSETS - h);
            TextRenderer.renderText(gc, textRect, true, false, appointment.getText());

            // draw the status
            gc.setBackground(getColorForStatus((Appointment) interval));
            gc.fillRectangle(iRect.x, iRect.y, w, iRect.height);

            // draw the border
            gc.drawRectangle(iRect);

            // draw selection using alpha blending
            if (selected && !printing) {
                bgx = gc.getBackground();
                int alpha = gc.getAlpha();
                gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
                gc.setAlpha(60);
                gc.fillRectangle(iRect);

                gc.setAlpha(alpha);
                gc.setBackground(bgx);
            }

        }
        gc.setBackground(bg);
    }

    /**
     * Draw the heading time in the upper box whenever possible.
     * 
     * @param gc
     * @param appointment
     * @param x
     * @param y
     * @param width
     * @param height
     */
    private void drawTimeString(GC gc, Appointment appointment, int x, int y, int width, int height) {
        if (width < 1) {
            return;
        }
        String timeString = appointment.getBegin().toDisplayStringTime(false) + " - "
                + appointment.getEnd().toDisplayStringTime(false);
        if (gc.textExtent(timeString).x > width) {
            timeString = appointment.getBegin().toDisplayStringTime(false) + "-"
                    + appointment.getEnd().toDisplayStringTime(false);

            if (gc.textExtent(timeString).x > width) {
                timeString = appointment.getBegin().toDisplayStringTime(false) + "-";
                if (gc.textExtent(timeString).x > width) {
                    // draw nothing
                    return;
                }
            }

        }

        SwtGraphicsHelper.drawStringCentered(gc, timeString, x, y, width, height);
    }

    private Color getColorForStatus(Appointment app) {
        if (app.getStatus() == Status.BOOKED) {
            return _bookedColor;
        } else if (app.getStatus() == Status.ABSENT) {
            return _absentColor;
        } else if (app.getStatus() == Status.TENTATIVE) {
            return _tentativeColor;
        } else if (app.getStatus() == Status.FREE) {
            return _freeColor;
        }
        return _freeColor;
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
        Rectangle iRect = getIRect(horizontal, drawingArea, overlapping);
        return iRect.contains(drawingArea.x + x, drawingArea.y + y);
    }

    public Rectangle getContainingRectangle(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea,
            boolean overlapping) {
        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle iRect = getIRect(horizontal, drawingArea, overlapping);
        return iRect;
    }

    protected static Rectangle getIRect(boolean horizontal, Rectangle drawingArea, boolean overlap) {
        if (horizontal) {
            int borderHeight = (int) (drawingArea.height * BORDERFACTOR / 2);
            int height = drawingArea.height - (overlap ? 0 : 2 * borderHeight);
            int y = drawingArea.y + (overlap ? 0 : borderHeight);
            return new Rectangle(drawingArea.x, y, drawingArea.width - 1, height - 1);
        } else {
            int borderWidth = (int) (drawingArea.width * BORDERFACTOR / 2);
            int width = drawingArea.width - (overlap ? 0 : 2 * borderWidth);
            int x = drawingArea.x + (overlap ? 0 : borderWidth);
            return new Rectangle(x, drawingArea.y, width - 1, drawingArea.height - 1);
        }
    }

    public static boolean isInDragMark(Rectangle bounds, int x, int y, boolean horizontal, boolean overlap) {
        Rectangle iRect = getIRect(horizontal, bounds, overlap);
        Rectangle dragMark = new Rectangle(iRect.x + iRect.width - DRAGMARKSIZE - INSETS, iRect.y + INSETS,
                DRAGMARKSIZE, DRAGMARKSIZE);
        return dragMark.contains(x, y);
    }

    private Image _recurImage;
    private Image _lockImage;

    private Image getRecurImage(Device device) {
        if (_recurImage != null) {
            return _recurImage;
        }
        _recurImage = new Image(device, this.getClass().getResourceAsStream(
                "/de/jaret/examples/timebars/calendar/swt/renderer/recurrent.gif"));
        return _recurImage;
    }

    private Image getLockImage(Device device) {
        if (_lockImage != null) {
            return _lockImage;
        }
        _lockImage = new Image(device, this.getClass().getResourceAsStream(
                "/de/jaret/examples/timebars/calendar/swt/renderer/lock.gif"));
        return _lockImage;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        if (_freeColor != null) {
            _freeColor.dispose();
        }
        if (_bookedColor != null) {
            _bookedColor.dispose();
        }
        if (_tentativeColor != null) {
            _tentativeColor.dispose();
        }
        if (_freeColor != null) {
            _freeColor.dispose();
        }
        if (_recurImage != null) {
            _recurImage.dispose();
        }
        if (_lockImage != null) {
            _lockImage.dispose();
        }
    }

}
