/*
 *  File: AppointmentPlaceholder.java 
 *  Copyright (c) 2007  Peter Kliem (Peter.Kliem@jaret.de)
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
package de.jaret.examples.timebars.calendar.model;

/**
 * A placeholder for appointments spanning a complete day or strech over more than one day.
 * 
 * @author Peter Kliem
 * @version $Id: AppointmentPlaceholder.java 705 2008-01-25 22:51:54Z kliem $
 */
public class AppointmentPlaceholder {
    /** appointment this placeholder stands for. */
    protected Appointment _appointment;
    /** day of this placeholder. */
    protected Day _day;
    /** position of display. -1 is undefined. */
    protected int _position = -1;

    public AppointmentPlaceholder(Day day, Appointment appointment) {
        _day = day;
        _appointment = appointment;
    }

    public Appointment getAppointment() {
        return _appointment;
    }

    public void setAppointment(Appointment appointment) {
        _appointment = appointment;
    }

    public Day getDay() {
        return _day;
    }

    public void setDay(Day day) {
        _day = day;
    }

    public int getPosition() {
        return _position;
    }

    public void setPosition(int position) {
        _position = position;
    }

    public boolean isFirst() {
        return _day.getDayDate().compareDateTo(_appointment.getRealBegin()) == 0;
    }

    public boolean isLast() {
        return _day.getDayDate().compareDateTo(_appointment.getRealEnd()) == 0
                || (_day.getDayDate().compareDateTo(_appointment.getRealEnd().copy().backDays(1)) == 0 && _appointment
                        .isWholeDayAppointment());
    }

}
