package de.jaret.examples.timebars.calendar.model;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.mod.IntervalModificator;
import de.jaret.util.ui.timebars.model.TimeBarRow;

public class AppointmentModificator implements IntervalModificator {

    public double getSecondGridSnap() {
        return 300;
    }

    public boolean isShiftingAllowed(TimeBarRow row, Interval interval) {
        return ((Appointment) interval).isEditable();
    }

    public boolean isSizingAllowed(TimeBarRow row, Interval interval) {
        return ((Appointment) interval).isEditable();
    }

    public boolean newBeginAllowed(TimeBarRow row, Interval interval, JaretDate newBegin) {
        return CalendarModel.BASEDATE.compareDateTo(newBegin) == 0;
    }

    public boolean newEndAllowed(TimeBarRow row, Interval interval, JaretDate newEnd) {
        return CalendarModel.BASEDATE.compareDateTo(newEnd) == 0;
    }

    public boolean shiftAllowed(TimeBarRow row, Interval interval, JaretDate newBegin) {
        return CalendarModel.BASEDATE.compareDateTo(newBegin) == 0
                && CalendarModel.BASEDATE.compareDateTo(newBegin.copy().advanceSeconds(interval.getSeconds())) == 0;
    }

    public boolean isApplicable(TimeBarRow row, Interval interval) {
        return true;
    }

}
