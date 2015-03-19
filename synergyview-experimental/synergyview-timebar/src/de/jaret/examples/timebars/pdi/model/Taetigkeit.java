/*
 *  File: Taetigkeit.java 
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

import java.util.ArrayList;
import java.util.List;

import de.jaret.util.date.IntervalImpl;

/**
 * @author Peter Kliem
 * @version $Id: Taetigkeit.java 160 2007-01-02 22:02:40Z olk $
 */
public class Taetigkeit extends IntervalImpl {
    protected String _beginOrt;
    protected String _endeOrt;
    protected int _bezahlteZeitSeconds;
    protected List _probleme;

    /**
     * @return Returns the beginOrt.
     */
    public String getBeginOrt() {
        return _beginOrt;
    }

    /**
     * @param beginOrt The beginOrt to set.
     */
    public void setBeginOrt(String beginOrt) {
        _beginOrt = beginOrt;
    }

    /**
     * @return Returns the bezahlteZeitSeconds.
     */
    public int getBezahlteZeitSeconds() {
        return _bezahlteZeitSeconds;
    }

    /**
     * @param bezahlteZeitSeconds The bezahlteZeitSeconds to set.
     */
    public void setBezahlteZeitSeconds(int bezahlteZeitSeconds) {
        _bezahlteZeitSeconds = bezahlteZeitSeconds;
    }

    /**
     * @return Returns the endeOrt.
     */
    public String getEndeOrt() {
        return _endeOrt;
    }

    /**
     * @param endeOrt The endeOrt to set.
     */
    public void setEndeOrt(String endeOrt) {
        _endeOrt = endeOrt;
    }

    public List getProbleme() {
        return _probleme;
    }

    public void addProblem(String problem) {
        if (_probleme == null) {
            _probleme = new ArrayList();
        }
        _probleme.add(problem);
        firePropertyChange("Probleme", null, problem);
    }

    public void clearProbleme() {
        if (_probleme != null) {
            _probleme.clear();
            firePropertyChange("Probleme", null, null);
        }
    }

}
