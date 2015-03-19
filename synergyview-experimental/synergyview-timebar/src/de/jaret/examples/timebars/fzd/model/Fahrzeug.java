/*
 *  File: Fahrzeug.java 
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
import java.util.Iterator;
import java.util.List;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;

/**
 * @author Peter Kliem
 * @version $Id: Fahrzeug.java 160 2007-01-02 22:02:40Z olk $
 */
public class Fahrzeug extends DefaultTimeBarRowModel {
    protected String _fzdNummer;
    protected List _umlaeufe = new ArrayList();
    protected FahrzeugInfo _fzgInfo;

    public Fahrzeug(String fzdNummer) {
        _fzdNummer = fzdNummer;
        _fzgInfo = new FahrzeugInfo(fzdNummer);
        setRowHeader(_fzgInfo);

    }

    public FahrzeugInfo getFahrzeugInfo() {
        return _fzgInfo;
    }

    /**
     * @return Returns the fzdNummer.
     */
    public String getFzdNummer() {
        return _fzdNummer;
    }

    /**
     * @param fzdNummer The fzdNummer to set.
     */
    public void setFzdNummer(String fzdNummer) {
        _fzdNummer = fzdNummer;
    }

    /**
     * @return Returns the umlaeufe.
     */
    public List getUmlaeufe() {
        return _umlaeufe;
    }

    public void addUmlauf(Umlauf umlauf) {
        addInterval(umlauf);
        _umlaeufe.add(umlauf);
        umlauf.setFahrzeug(this);
        Iterator it = umlauf.getFahrten().iterator();
        while (it.hasNext()) {
            Fahrt fahrt = (Fahrt) it.next();
            addInterval(fahrt);
        }
    }

    public void remUmlauf(Umlauf umlauf) {
        if (_umlaeufe.contains(umlauf)) {
            Iterator it = umlauf.getFahrten().iterator();
            while (it.hasNext()) {
                Fahrt fahrt = (Fahrt) it.next();
                remInterval(fahrt);
            }
            remInterval(umlauf);

        }
    }

    /**
     * @param restKette
     */
    public void addUmlaufKette(UmlaufKette kette) {
        Iterator it = kette.getUmlaeufe().iterator();
        while (it.hasNext()) {
            Umlauf u = (Umlauf) it.next();
            addUmlauf(u);
        }
    }

    /**
     * @param date
     * @return
     */
    public int getFahrtRest(JaretDate date) {
        List intervals = getIntervals(date);
        Iterator it = intervals.iterator();
        while (it.hasNext()) {
            Interval interval = (Interval) it.next();
            if (interval instanceof Fahrt) {
                return interval.getEnd().diffSeconds(date);
            }
        }
        return 0;
    }

    /**
     * @param date
     * @return
     */
    public Fahrt getFahrt(JaretDate date) {
        List intervals = getIntervals(date);
        Iterator it = intervals.iterator();
        while (it.hasNext()) {
            Interval interval = (Interval) it.next();
            if (interval instanceof Fahrt) {
                return (Fahrt) interval;
            }
        }
        return null;
    }

}
