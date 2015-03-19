package de.jaret.examples.timebars.calendar.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;

/**
 * A day representation as an extenion of the timebar row model. A list of placeholders are used for indicting
 * appointments that span more than one day (rendereing is done by the header renderer).
 * 
 * @author Peter Kliem
 * @version $Id: Day.java 801 2008-12-27 22:44:54Z kliem $
 */
public class Day extends DefaultTimeBarRowModel {
    /** the date of the da. */
    private JaretDate _dayDate;

    /** List of placeholders. */
    private List<AppointmentPlaceholder> _placeholders = new ArrayList<AppointmentPlaceholder>();

    /**
     * Construct the day for a specific date.
     * @param date the day date
     */
    public Day(JaretDate date) {
        super();
        // use a copy on write list to allow smooth multi threading
        _intervals = new CopyOnWriteArrayList<Interval>();
        setRowHeader(new DefaultRowHeader(date.toDisplayStringDate(), this));
        _dayDate = date;
    }

    /**
     * Add an interval.
     * 
     * @param interval interval to add
     */
    public synchronized void addInterval(Interval interval) {
        insertSorted(interval);
        // Check min/max modifications by the added interval
        if (_minDate == null || _intervals.size() == 1) {
            _minDate = interval.getBegin().copy();
            _maxDate = interval.getEnd().copy();
        } else {
            if (_minDate.compareTo(interval.getBegin()) > 0) {
                _minDate = interval.getBegin().copy();
            } else if (_maxDate.compareTo(interval.getEnd()) < 0) {
                _maxDate = interval.getEnd().copy();
            }
        }
        interval.addPropertyChangeListener(this);
        fireElementAdded(interval);
    }

    Comparator<Interval> intervalComparator = new Comparator<Interval>() {
        public int compare(Interval i1, Interval i2) {
            return i1.getBegin().compareTo(i2.getBegin());
        }
    };

    private void insertSorted(Interval interval) {
        if (_intervals.size() == 0) {
            _intervals.add(interval);
            return;
        }
        if (intervalComparator.compare(interval, _intervals.get(0)) < 0) {
            _intervals.add(0, interval);
            return;
        }
        for (int i = 0; i < _intervals.size() - 1; i++) {
            if (intervalComparator.compare(_intervals.get(i), interval) <= 0
                    && intervalComparator.compare(interval, _intervals.get(i + 1)) >= 0) {
                _intervals.add(i + 1, interval);
                return;
            }
        }
        _intervals.add(interval);
    }

    public JaretDate getDayDate() {
        return _dayDate;
    }

    /**
     * Look up an appointment on that day by comparing the ids.
     * 
     * @param id id to check
     * @return the appointment or <code>null</code>
     */
    public Appointment getAppointmentById(String id) {
        for (Interval interval : _intervals) {
            Appointment app = (Appointment) interval;
            if (id.equals(app.getId())) {
                return app;
            }
        }
        return null;
    }

    /**
     * Get all appointments for a day.
     * 
     * @return list of appointments
     */
    public List<Appointment> getAppointments() {
        List<Appointment> result = new ArrayList<Appointment>(_intervals.size());
        for (Interval interval : _intervals) {
            result.add((Appointment) interval);
        }
        return result;
    }

    /**
     * Retrieve allplaceholders present.
     * @return the list of placeholders
     */
    public List<AppointmentPlaceholder> getPlaceholders() {
        return _placeholders;
    }

    /**
     * Retrive the placeholder for an appointment.
     * @param appointment appointment
     * @return the placeholder for the appointment or <code>null</code>
     */
    public AppointmentPlaceholder getPlaceholder(Appointment appointment) {
        for (AppointmentPlaceholder ph : _placeholders) {
            if (ph.getAppointment().equals(appointment)) {
                return ph;
            }
        }
        return null;
    }

    /**
     * Removes all placeholdes for the given appointment.
     * 
     * @param appointment appointment
     */
    public void removePlaceholder(Appointment appointment) {
        AppointmentPlaceholder ph = getPlaceholder(appointment);
        if (ph != null) {
            _placeholders.remove(ph);
        }
    }

    public int getMaxPlaceholderPos() {
        int max = -1;
        for (AppointmentPlaceholder ph : _placeholders) {
            if (ph.getPosition() > max) {
                max = ph.getPosition();
            }
        }
        return max;
    }

    public Appointment getPlaceholderForPosition(int pos) {
        for (AppointmentPlaceholder ph : _placeholders) {
            if (ph.getPosition() == pos) {
                return ph.getAppointment();
            }
        }
        return null;
    }

    public Collection<Appointment> getAppointmentBySynchronizerId(String synchronizerId) {
        List<Appointment> result = new ArrayList<Appointment>(_intervals.size());
        for (Interval interval : _intervals) {
            Appointment app = (Appointment) interval;
            if (app.getSynchronizerId().equals(synchronizerId)) {
                result.add(app);
            }
        }
        return result;
    }

}
