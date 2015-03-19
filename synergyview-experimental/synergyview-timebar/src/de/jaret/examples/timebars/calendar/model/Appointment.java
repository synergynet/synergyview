package de.jaret.examples.timebars.calendar.model;

import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;

/**
 * Extension of the interval class, always projecting in the same base date, thus the time is relevant.
 * 
 * @author Peter Kliem
 * @version $Id: Appointment.java 705 2008-01-25 22:51:54Z kliem $
 */
public class Appointment extends IntervalImpl {
    /** enumeration for the possible states. */
    public enum Status {
        FREE, BOOKED, TENTATIVE, ABSENT
    };

    /** the real begin including the date. */
    protected JaretDate _realBegin;
    /** the real end including the date. */
    protected JaretDate _realEnd;

    protected String _text;
    protected String _description;
    protected String _id;
    protected Status _status = Status.BOOKED;
    /** true if the event is part of a series. */
    protected boolean _recurring = false;
    protected boolean _editable = true;

    /** extension object to be used by the synchronizers. */
    protected Object _extension;

    /** true whenever the appointment (realBegin/end) spans a whole day or more than one day. */
    protected boolean _spansMultipleDays = false;

    /** the synchronizer id caring for this appointment. */
    protected String _synchronizerId;

    /** date the appointment has been checked against its source the last time. */
    protected JaretDate _lastChecked;
    /**
     * id of the last changing logic: either a synchronizer or the calendar plugin id. changer always defaults to the
     * plugin, synchronizers have to set it after doing an update.
     */
    protected String _lastChangerId;
    /** timestamp of the lst change. */
    protected JaretDate _lastChangeDate;

    /**
     * Construct a basic appointment. Begin and end have to be set seperately!
     * 
     * @param id id to use
     */
    public Appointment(String id) {
        _id = id;
    }

    public Appointment(JaretDate date, int h, int m, double durH, String text) {
        JaretDate begin = date.copy().setTime(h, m, 0);
        setRealBegin(begin);

        JaretDate end = begin.copy().advanceHours(durH);
        setRealEnd(end);

        _text = text;
        checkSpanMultiple();
        changed();
    }

    public Appointment(JaretDate begin, JaretDate end, String text) {
        setRealBegin(begin.copy());
        setRealEnd(end.copy());
        _text = text;
        checkSpanMultiple();
        changed();
    }

    /**
     * Create a copy of the appointment. This method should be overriden by extending classes.
     * 
     * @return a copy of the interval that does not include the meta information (synchronizer, changer, timestamps)
     */
    public Appointment copy() {
        Appointment a = new Appointment(_realBegin, 0, 0, 0, _text);
        a.setRealBegin(_realBegin.copy());
        a.setRealEnd(_realEnd.copy());
        a.setStatus(_status);
        a.setEditable(_editable);
        a.setRecurring(_recurring);
        a.setDescription(_description);
        a.setSynchronizerId(_synchronizerId);
        return a;
    }

    protected void changed() {
        // set the last changed date to now
        // if the last change date is already in the future (that may happen when synchronizing with
        // external systems) set is to be after that date in the future
        JaretDate d = new JaretDate();
        if (_lastChangeDate == null || d.compareTo(_lastChangeDate) > 0) {
            _lastChangeDate = d;
        } else {
            _lastChangeDate.advanceMinutes(1);
        }

    }

    public JaretDate getRealBegin() {
        return _realBegin;
    }

    public void setRealBegin(JaretDate realBegin) {
        JaretDate oldVal = _realBegin;
        _realBegin = realBegin;
        setBegin(correct(realBegin));
        checkSpanMultiple();
        // TODO: this ensures prop changes for day changes, could be done more sensitive
        firePropertyChange(PROP_BEGIN, oldVal, _realBegin);
        changed();
    }

    public JaretDate getRealEnd() {
        return _realEnd;
    }

    public void setRealEnd(JaretDate realEnd) {
        JaretDate oldVal = _realEnd;
        _realEnd = realEnd;
        setEnd(correct(realEnd));
        checkSpanMultiple();
        changed();
        // TODO: this ensures prop changes for day changes, could be done more sensitive
        firePropertyChange(PROP_END, oldVal, _realEnd);
    }

    public void setBegin(JaretDate begin) {
        _realBegin = setTime(_realBegin, begin);
        super.setBegin(begin);
        changed();
    }

    public void setEnd(JaretDate end) {
        _realEnd = setTime(_realEnd, end);
        super.setEnd(end);
        changed();
    }

    private JaretDate setTime(JaretDate real, JaretDate date) {
        JaretDate d = real.copy();
        d.setTime(date.getHours(), date.getMinutes(), date.getSeconds());
        return d;
    }

    private void checkSpanMultiple() {
        if (_realBegin != null && _realEnd != null) {
            if (_realEnd.diffMinutes(_realBegin) >= 60 * 24) {
                setSpansMultipleDays(true);
            } else {
                setSpansMultipleDays(false);
            }
        }

    }

    private JaretDate correct(JaretDate realBegin) {
        return CalendarModel.BASEDATE.copy().setTime(realBegin.getHours(), realBegin.getMinutes(),
                realBegin.getSeconds());
    }

    public String getText() {
        return _text;
    }

    public void setText(String text) {
        String oldVal = _text;
        _text = text;
        changed();
        firePropertyChange("Text", oldVal, text);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.calendar.model.IAppointment#getDescription()
     */
    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        String oldVal = _description;
        _description = description;
        changed();
        firePropertyChange("Description", oldVal, description);
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
        changed();
    }

    public String getSynchronizerId() {
        return _synchronizerId;
    }

    public void setSynchronizerId(String synchronizerId) {
        _synchronizerId = synchronizerId;
    }

    public JaretDate getLastChecked() {
        return _lastChecked;
    }

    public void setLastChecked(JaretDate lastChecked) {
        _lastChecked = lastChecked;
    }

    public String getLastChangerId() {
        return _lastChangerId;
    }

    public void setLastChangerId(String lastChangerId) {
        _lastChangerId = lastChangerId;
    }

    public JaretDate getLastChangeDate() {
        return _lastChangeDate;
    }

    public void setLastChangeDate(JaretDate lastChangeDate) {
        _lastChangeDate = lastChangeDate;
    }

    public Status getStatus() {
        return _status;
    }

    public void setStatus(Status status) {
        _status = status;
    }

    public boolean isRecurring() {
        return _recurring;
    }

    public void setRecurring(boolean recurring) {
        _recurring = recurring;
    }

    public boolean isEditable() {
        return _editable;
    }

    public void setEditable(boolean editable) {
        _editable = editable;
    }

    public boolean isSpansMultipleDays() {
        return _spansMultipleDays;
    }

    protected void setSpansMultipleDays(boolean spansMultipleDays) {
        if (_spansMultipleDays != spansMultipleDays) {
            _spansMultipleDays = spansMultipleDays;
            firePropertyChange("SpansMultipleDays", !spansMultipleDays, spansMultipleDays);
        }
    }

    /**
     * Check whether the appointment is a whole day event.
     * 
     * @return true if begin and end time are 00:00
     */
    public boolean isWholeDayAppointment() {
        JaretDate d = new JaretDate();
        d.setTime(0, 0, 0);
        return getRealBegin().compareTimeTo(d) == 0 && getRealEnd().compareTimeTo(d) == 0;
    }

    public Object getExtension() {
        return _extension;
    }

    public void setExtension(Object extension) {
        _extension = extension;
    }

    /**
     * Create a string representation suitable for display.
     * 
     * @return string representation
     */
    public String toDisplayString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getText());
        buf.append("|");
        buf.append(getRealBegin().toDisplayString());
        buf.append("--");
        buf.append(getRealEnd().toDisplayString());

        return buf.toString();
    }

}
