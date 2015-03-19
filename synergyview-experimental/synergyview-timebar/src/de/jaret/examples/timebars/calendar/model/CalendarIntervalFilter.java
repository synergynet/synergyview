/*
 *  File: CalendarIntervalFilter.java 
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

import de.jaret.util.date.Interval;
import de.jaret.util.misc.PropertyObservableBase;
import de.jaret.util.ui.timebars.TimeBarIntervalFilter;

/**
 * Interval filter for the calendar time bar viewer to exclude appointments by certain synchronizers and appointmens
 * that are spanning multiple days.
 * 
 * @author Peter Kliem
 * @version $Id: CalendarIntervalFilter.java 705 2008-01-25 22:51:54Z kliem $
 */
public class CalendarIntervalFilter extends PropertyObservableBase implements TimeBarIntervalFilter {

    /**
     * Special for the header, since the normal filtering excludes whole day appointments.
     * 
     * @param interval
     * @return
     */
    public boolean isInResultHeader(Interval interval) {
        return true;
    }

    public boolean isInResult(Interval interval) {
        Appointment appointment = (Appointment) interval;
        if (isInResultHeader(interval) && !appointment.isSpansMultipleDays()) {
            return true;
        }
        return false;
    }


}
