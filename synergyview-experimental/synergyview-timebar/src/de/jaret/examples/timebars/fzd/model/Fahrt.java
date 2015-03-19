/*
 *  File: Fahrt.java 
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

import de.jaret.util.date.IntervalImpl;

/**
 * @author Peter Kliem
 * @version $Id: Fahrt.java 160 2007-01-02 22:02:40Z olk $
 */
public class Fahrt extends IntervalImpl {
    protected String _beginOrt;
    protected String _endeOrt;
    protected String _fahrtNummer;
    protected Umlauf _umlauf;

    /**
     * @param beginOrt
     * @param endeOrt
     * @param fahrtNummer
     */
    public Fahrt(String beginOrt, String endeOrt, String fahrtNummer) {
        _beginOrt = beginOrt;
        _endeOrt = endeOrt;
        _fahrtNummer = fahrtNummer;
    }

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
        String oldVal = _beginOrt;
        _beginOrt = beginOrt;
        firePropertyChange("BeginOrt", oldVal, beginOrt);
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
        String oldVal = _endeOrt;
        _endeOrt = endeOrt;
        firePropertyChange("EndeOrt", oldVal, endeOrt);
    }

    /**
     * @return Returns the fahrtNummer.
     */
    public String getFahrtNummer() {
        return _fahrtNummer;
    }

    /**
     * @param fahrtNummer The fahrtNummer to set.
     */
    public void setFahrtNummer(String fahrtNummer) {
        String oldVal = _fahrtNummer;
        _fahrtNummer = fahrtNummer;
        firePropertyChange("FahrtNummer", oldVal, fahrtNummer);
    }

    /**
     * @return Returns the umlauf.
     */
    public Umlauf getUmlauf() {
        return _umlauf;
    }

    /**
     * @param umlauf The umlauf to set.
     */
    public void setUmlauf(Umlauf umlauf) {
        _umlauf = umlauf;
    }
}
