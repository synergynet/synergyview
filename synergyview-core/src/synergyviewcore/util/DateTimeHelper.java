/**
 * File: DateTimeHelper.java Copyright (c) 2011 phyo This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcore.util;

import de.jaret.util.date.JaretDate;

/**
 * The Class DateTimeHelper.
 * 
 * @author phyo
 */
public class DateTimeHelper {

    /**
     * Gets the HMS from milli.
     * 
     * @param milli
     *            the milli
     * @return the HMS from milli
     */
    public static String getHMSFromMilli(long milli) {
	return String.format("%02dh:%02dm:%02ds:%03d", (milli / (1000 * 60 * 60)) % 24, (milli / (1000 * 60)) % 60, (milli / 1000) % 60, milli % 1000);
    }

    /**
     * Gets the HMS from milli formatted.
     * 
     * @param milli
     *            the milli
     * @return the HMS from milli formatted
     */
    public static String getHMSFromMilliFormatted(long milli) {
	return String.format("%02dh %02dm %02ds %03dms", (milli / (1000 * 60 * 60)) % 24, (milli / (1000 * 60)) % 60, (milli / 1000) % 60, milli % 1000);
    }

    /**
     * Gets the milli from jaret date.
     * 
     * @param date
     *            the date
     * @return the milli from jaret date
     */
    public static long getMilliFromJaretDate(JaretDate date) {
	return date.getMillis() + (date.getSeconds() * 1000) + (date.getMinutes() * 60 * 1000) + (date.getHours() * 60 * 60 * 1000);
    }
}
