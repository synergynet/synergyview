package de.jaret.examples.timebars.calendar.model;

import java.util.ArrayList;
import java.util.List;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;

public class CalendarModel extends DefaultTimeBarModel {
    /** date used as a base date for all intervals. */
    public static final JaretDate BASEDATE = new JaretDate(1, 5, 2000, 0, 0, 0);
    /**
     * Update or create the model for the given month. If the month is not already created, day entries will be created.
     * 
     * @param month month
     * @param year year
     */
    public void createMonth(int month, int year) {
        int beginIndex = getIndexForDate(new JaretDate(1, month, year, 0, 0, 0));
        if (beginIndex == -1) {
            createMonthInternal(month, year);
        }
    }
    /**
     * Get the index for the given date or -1.
     * 
     * @param jaretDate date
     * @return index or -1
     */
    public int getIndexForDate(JaretDate jaretDate) {
        for (int i = 0; i < _rows.size(); i++) {
            Day day = (Day) _rows.get(i);
            if (day.getDayDate().compareDateTo(jaretDate) == 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Create empty days for the given month. The method assumes that the existing days are present without gaps.
     * 
     * @param month month
     * @param year year
     */
    private void createMonthInternal(int month, int year) {
        if (_rows.size() == 0
                || ((Day) _rows.get(_rows.size() - 1)).getDayDate().compareDateTo(
                        new JaretDate(1, month, year, 0, 0, 0)) < 0) {
            // simply attach to the end
            JaretDate date = new JaretDate(1, month, year, 0, 0, 0);
            while (date.getMonth() == month) {
                Day day = new Day(date);
                addRow(day);
                date = date.copy().advanceDays(1);
            }
        } else {
            // add before beginning
            JaretDate date = new JaretDate(1, month, year, 0, 0, 0);
            int index = 0;
            while (date.getMonth() == month) {
                Day day = new Day(date);
                addRow(index++, day);
                date = date.copy().advanceDays(1);
            }
        }
    }
  
    /**
     * Retrieve the day for a given date.
     * 
     * @param date date of the day
     * @return the day or <code>null</code> if none could be found
     */
    public Day getDay(JaretDate date) {
        for (TimeBarRow row : _rows) {
            Day day = (Day) row;
            if (day.getDayDate().compareDateTo(date) == 0) {
                return day;
            }
        }
        return null;
    }

    /**
     * Retrieve the day for an index (simple cast from getRow).
     * 
     * @param idx index
     * @return day
     */
    public Day getDay(int idx) {
        return (Day) getRow(idx);
    }


    /**
     * {@inheritDoc} Adds handling of the placeholder creation.
     */
    protected void fireElementAdded(TimeBarRow row, Interval interval) {
        Appointment app = (Appointment) interval;
        deletePlaceholders(app);
        if (app.isSpansMultipleDays()) {
            Day day = (Day) row;
            int idx = getIndexForRow(day);
            createOrUpdatePlaceholders(idx, app);
        }

        super.fireElementAdded(row, interval);
    }

    /**
     * {@inheritDoc} Adds handling of the placeholder handling and checks whether an appointment is still registered
     * with the right day (row) of the model.
     */
    protected void fireElementChanged(TimeBarRow row, Interval interval) {
        Appointment app = (Appointment) interval;

        Day day = (Day) checkDayOfAppointment((Appointment) interval, (Day) row);
        deletePlaceholders(app);
        if (app.isSpansMultipleDays()) {
            int idx = getIndexForRow(day);
            createOrUpdatePlaceholders(idx, app);
        }

        super.fireElementChanged(row, interval);
    }

    /**
     * Check and possibly correct the day for an appointment.
     * 
     * @param appointment appointment to check
     * @param day the current day
     * @return the new day
     */
    private Day checkDayOfAppointment(Appointment appointment, Day day) {
        if (day.getDayDate().compareDateTo(appointment.getRealBegin()) == 0) {
            return day;
        }
        day.remInterval(appointment);
        Day newDay = getDay(appointment.getRealBegin());
        newDay.addInterval(appointment);
        return newDay;
    }

    /**
     * {@inheritDoc} Adds handling of the placeholder deletion for the removed interval.
     */
    protected void fireElementRemoved(TimeBarRow row, Interval interval) {
        Appointment app = (Appointment) interval;
        deletePlaceholders(app);
        super.fireElementRemoved(row, interval);
    }

    /**
     * Remove all placeholders in the model for a given appointment.
     * 
     * @param app appointment
     */
    private void deletePlaceholders(Appointment app) {
        for (int i = 0; i < getRowCount(); i++) {
            getDay(i).removePlaceholder(app);
        }
    }

    /**
     * Create or update placeholder objects for an appointment on several days.
     * 
     * @param startDayIdx index of
     * @param appointment
     */
    private void createOrUpdatePlaceholders(int startDayIdx, Appointment appointment) {
        int idx = startDayIdx;
        Day day = getDay(idx);
        List<AppointmentPlaceholder> placeholders = new ArrayList<AppointmentPlaceholder>();
        int maxPos = -1;
        while (day.getDayDate().compareDateTo(appointment.getRealEnd()) <= 0) {
            // special check: if the date is the last date and the time is 00:00
            // --> do not add a placeholder!
            if (!(day.getDayDate().compareDateTo(appointment.getRealEnd()) == 0 && appointment.isWholeDayAppointment())) {
                AppointmentPlaceholder ph = day.getPlaceholder(appointment);
                if (ph == null) {
                    ph = new AppointmentPlaceholder(day, appointment);
                }
                placeholders.add(ph);
                day.getPlaceholders().add(ph);

                if (day.getMaxPlaceholderPos() > maxPos) {
                    maxPos = day.getMaxPlaceholderPos();
                }
            }
            idx++;
            day = getDay(idx);
        }

        for (AppointmentPlaceholder appointmentPlaceholder : placeholders) {
            appointmentPlaceholder.setPosition(maxPos + 1);
        }
    }

    
    
}
