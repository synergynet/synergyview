/*
 *  File: DayHeaderRenderer.java 
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.examples.timebars.calendar.model.Appointment;
import de.jaret.examples.timebars.calendar.model.AppointmentPlaceholder;
import de.jaret.examples.timebars.calendar.model.CalendarIntervalFilter;
import de.jaret.examples.timebars.calendar.model.CalendarModel;
import de.jaret.examples.timebars.calendar.model.Day;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.ResourceImageDescriptor;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.HeaderRenderer;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;

/**
 * Header renderer for the calendar example. Includes rendering of spanning appointments (one part per header) represented by AppointmentPlaceholders.
 * 
 * @author Peter Kliem
 * @version $Id: DayHeaderRenderer.java 705 2008-01-25 22:51:54Z kliem $
 */
public class DayHeaderRenderer extends RendererBase implements HeaderRenderer {
    /** line width when printing. */
    private static final int PRINTING_LINEWIDTH = 3;
    /** alpha for selecton painting. */
    protected static final int SELECTION_ALPHA = 60;

    /** height for appointments that span multiple days. */
    private static final int APPOINTMENT_HEIGHT = 18;
    private static final int STARTAPPOINTMENTS = APPOINTMENT_HEIGHT;

    protected List<DateFormat> _formats;
    int _lastWidth = -1;
    int _lastFormatIdx = -1;

    /**
     * Constructor for printing use.
     * 
     * @param printer printing device
     */
    public DayHeaderRenderer(Printer printer) {
        super(printer);
        initDateFormats();
    }

    /**
     * Constructor for screen use.
     * 
     */
    public DayHeaderRenderer() {
        this(null);
    }

    protected void initDateFormats() {
        _formats = new ArrayList<DateFormat>();
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        _formats.add(df);
        df = DateFormat.getDateInstance(DateFormat.LONG);
        _formats.add(df);
        df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        _formats.add(df);
        df = DateFormat.getDateInstance(DateFormat.SHORT);
        _formats.add(df);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, TimeBarRowHeader header,
            boolean selected, boolean printing) {
        Day day = (Day) header.getRow();
        Date date = day.getDayDate().getDate();

        String label = getLabel(gc, drawingArea, date);

        Color bg = gc.getBackground();
        Color fg = gc.getForeground();
        if (selected) {
            gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
            gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
        }
        gc.fillRectangle(drawingArea);
        SwtGraphicsHelper.drawStringCentered(gc, label, drawingArea.x, drawingArea.x + drawingArea.width, drawingArea.y
                + scaleY(INSETS));

        // int appStartY = drawingArea.y + scaleY(INSETS) + gc.textExtent(label).y;

        gc.setBackground(bg);
        gc.setForeground(fg);
        if (delegate.getDrawRowGrid()) {
            if (delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL) {
                gc.drawLine(drawingArea.x, drawingArea.y + drawingArea.height - 1, drawingArea.x + drawingArea.width,
                        drawingArea.y + drawingArea.height - 1);
            } else {
                gc.drawLine(drawingArea.x + drawingArea.width - 1, drawingArea.y,
                        drawingArea.x + drawingArea.width - 1, drawingArea.y + drawingArea.height - 1);
            }
        }

        // draw the spanning appointments
        bg = gc.getBackground();
        for (AppointmentPlaceholder ph : day.getPlaceholders()) {
            // check filtering
            if (delegate.getIntervalFilter() == null
                    || ((CalendarIntervalFilter) delegate.getIntervalFilter()).isInResultHeader(ph.getAppointment())) {
                int y = drawingArea.y + STARTAPPOINTMENTS + ph.getPosition() * (scaleY(APPOINTMENT_HEIGHT)+scaleY(1));
                gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_YELLOW));

                gc.drawRectangle(drawingArea.x, y, drawingArea.width, scaleY(APPOINTMENT_HEIGHT)-1);
                if (ph.isFirst()) {
                    gc.fillRectangle(drawingArea.x + 1, y + 1, drawingArea.width, scaleY(APPOINTMENT_HEIGHT) - 2);
//                    int sx = drawingArea.x + scaleX(INSETS);
//                    if (!ph.getAppointment().isWholeDayAppointment()) {
//                        gc.drawImage(getClockImage(gc.getDevice()), sx, y);
//                        sx += getClockImage(gc.getDevice()).getBounds().width + scaleY(INSETS);
//                    }
                    if (ph.isLast()) {
                        gc.drawLine(drawingArea.x + drawingArea.width - 1, y + 1,
                                drawingArea.x + drawingArea.width - 1, y + scaleY(APPOINTMENT_HEIGHT) - 2);
                    }
//                    SwtGraphicsHelper.drawStringVCentered(gc, ph.getAppointment().getText(), sx, y, y
//                            + scaleY(APPOINTMENT_HEIGHT));
                } else if (ph.isLast()) {
                    gc.fillRectangle(drawingArea.x, y + 1, drawingArea.width - 1, scaleY(APPOINTMENT_HEIGHT) - 2);
                } else {
                    gc.fillRectangle(drawingArea.x, y + 1, drawingArea.width, scaleY(APPOINTMENT_HEIGHT) - 2);
                }

                // for every part of the appointment in the differnet rows paint the icon and and thet text
                // relying on the clipping on the gc
                TimeBarRow firstRow = ((CalendarModel)delegate.getModel()).getDay(ph.getAppointment().getRealBegin());
                int sx = delegate.yForRow(firstRow) + scaleX(INSETS);
                if (!ph.getAppointment().isWholeDayAppointment()) {
                    gc.drawImage(getClockImage(gc.getDevice()), sx, y);
                    sx += getClockImage(gc.getDevice()).getBounds().width + scaleY(INSETS);
                }
                SwtGraphicsHelper.drawStringVCentered(gc, ph.getAppointment().getText(), sx, y, y
                        + scaleY(APPOINTMENT_HEIGHT));

                
                
                // draw selection using alpha blending
                if (delegate.getSelectionModel().isSelected(ph.getAppointment())) {
                    Color bgx = gc.getBackground();
                    int alpha = gc.getAlpha();
                    gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
                    gc.setAlpha(SELECTION_ALPHA);
                    gc.fillRectangle(drawingArea.x, y + 1, drawingArea.width, scaleY(APPOINTMENT_HEIGHT) - 2);

                    gc.setAlpha(alpha);
                    gc.setBackground(bgx);
                }

            }
        }
        gc.setBackground(bg);

    }

    protected static final int INSETS = 2;

    public static Appointment getAppointment(TimeBarViewer tbv, Day day, int by, int height, int x, int y) {
        if (y < by + STARTAPPOINTMENTS || y > by + height) {
            return null;
        }
        if (day.getPlaceholders().size() == 0) {
            return null;
        }
        int pos = (y - by) / APPOINTMENT_HEIGHT;
        Appointment app = day.getPlaceholderForPosition(pos - 1);
        if (app == null) {
            return null;
        }
        // check filtering
        if (tbv.getIntervalFilter() == null || ((CalendarIntervalFilter) tbv.getIntervalFilter()).isInResultHeader(app)) {
            return app;
        }
        return null;
    }

    private String getLabel(GC gc, Rectangle drawingArea, Date date) {
        if (_lastWidth == drawingArea.width && _lastFormatIdx != -1) {
            return _formats.get(_lastFormatIdx).format(date);
        }
        _lastWidth = drawingArea.width;

        // check from long to short
        for (int i = 0; i < _formats.size(); i++) {
            DateFormat format = _formats.get(i);
            String str = format.format(date);
            Point extent = gc.stringExtent(str);
            if (extent.x < drawingArea.width - 2 * INSETS) {
                _lastFormatIdx = i;
                return str;
            }
        }

        // no format is suitable, go with the shortest
        return _formats.get(_formats.size() - 1).format(date);
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(TimeBarRow row, Rectangle drawingArea, int x, int y) {
        if (row == null) {
            return null;
        }
        return row.getRowHeader().getLabel();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Rectangle drawingArea, int x, int y) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        if (_clockImage != null) {
            _clockImage.dispose();
        }
        if (_imageRegistry != null) {
            _imageRegistry.dispose();
        }
    }

    /**
     * {@inheritDoc}
     */
    public DayHeaderRenderer createPrintRenderer(Printer printer) {
        return new DayHeaderRenderer(printer);
    }

    private Image _clockImage;
    /** image registry for holding the icon. */
    protected ImageRegistry _imageRegistry;

    private Image getClockImage(Device device) {
        if (_clockImage != null) {
            return _clockImage;
        }
        _clockImage = getImageRegistry().getDescriptor("clock").createImage();
        return _clockImage;
    }
    /**
     * Retrieve the image registry (lazy creation).
     * 
     * @return the initialized registry
     */
    protected ImageRegistry getImageRegistry() {
        if (_imageRegistry == null) {
            _imageRegistry = new ImageRegistry();
            ImageDescriptor imgDesc = new ResourceImageDescriptor(
                    "/de/jaret/examples/timebars/calendar/swt/renderer/clock.gif");
            _imageRegistry.put("clock", imgDesc);
        }
        return _imageRegistry;
    }

}
