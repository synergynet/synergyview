/*
 *  File: PdiDay.java 
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
package de.jaret.examples.timebars.pdi.model;

import de.jaret.util.date.JaretDate;

/**
 * @author Peter Kliem
 * @version $Id: PdiDay.java 160 2007-01-02 22:02:40Z olk $
 */
public class PdiDay {
    protected String _betriebstag;
    protected JaretDate _date;

    /**
     * @param betriebstag
     * @param date
     */
    public PdiDay(String betriebstag, JaretDate date) {
        _betriebstag = betriebstag;
        _date = date;
    }

    /**
     * @return Returns the betriebstag.
     */
    public String getBetriebstag() {
        return _betriebstag;
    }

    /**
     * @param betriebstag The betriebstag to set.
     */
    public void setBetriebstag(String betriebstag) {
        _betriebstag = betriebstag;
    }

    /**
     * @return Returns the date.
     */
    public JaretDate getDate() {
        return _date;
    }

    /**
     * @param date The date to set.
     */
    public void setDate(JaretDate date) {
        _date = date;
    }
}
