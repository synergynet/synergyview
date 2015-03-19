/*
 *  File: Umlauf.java 
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
package de.jaret.examples.timebars.fzd.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;

/**
 * @author Peter Kliem
 * @version $Id: Umlauf.java 259 2007-02-16 13:54:00Z olk $
 */
public class Umlauf extends IntervalImpl {
    protected String _umlaufbezeichnug;
    protected List _fahrten = new ArrayList();
    protected Fahrzeug _fahrzeug;

    /**
     * @param umlaufbezeichnug
     */
    public Umlauf(String umlaufbezeichnug) {
        _umlaufbezeichnug = umlaufbezeichnug;
    }

    /**
     * @return Returns the fahrzeug.
     */
    public Fahrzeug getFahrzeug() {
        return _fahrzeug;
    }

    /**
     * @param fahrzeug The fahrzeug to set.
     */
    public void setFahrzeug(Fahrzeug fahrzeug) {
        Fahrzeug oldVal = _fahrzeug;
        _fahrzeug = fahrzeug;
        firePropertyChange("Fahrzeug", oldVal, fahrzeug);
    }

    /**
     * @return Returns the fahrten.
     */
    public List getFahrten() {
        return _fahrten;
    }

    public void addFahrt(Fahrt fahrt) {
        _fahrten.add(fahrt);
        // not very fast ...
        Collections.sort(_fahrten, new Comparator() {
            public int compare(Object arg0, Object arg1) {
                Interval i1 = (Interval) arg0;
                Interval i2 = (Interval) arg1;

                return i1.getBegin().compareTo(i2.getBegin());
            }
        });
        // TODO könnte man auch direkt aus der sortierten Liste holen
        if (_begin == null) {
            setBegin(fahrt.getBegin().copy());
            setEnd(fahrt.getEnd().copy());
        } else {
            if (_begin.compareTo(fahrt.getBegin()) > 0) {
                setBegin(fahrt.getBegin().copy());
            } else if (_end.compareTo(fahrt.getEnd()) < 0) {
                setEnd(fahrt.getEnd().copy());
            }
        }
    }

    /**
     * @return Returns the umlaufbezeichnug.
     */
    public String getUmlaufbezeichnug() {
        return _umlaufbezeichnug;
    }
}
