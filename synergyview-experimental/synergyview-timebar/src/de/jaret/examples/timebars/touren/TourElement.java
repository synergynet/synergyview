/*
 *  File: TourElement.java 
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
package de.jaret.examples.timebars.touren;

import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;

public class TourElement extends IntervalImpl {
    String _beginOrt;
    String _endeOrt;
    int _typ;
    String _label;

    public TourElement(JaretDate begin, JaretDate end, String beginOrt, String endeOrt, int typ, String label) {
        setBegin(begin);
        setEnd(end);
        _beginOrt = beginOrt;
        _endeOrt = endeOrt;
        _typ = typ;
        _label = label;
    }

    /**
     * @return Returns the beginOrt.
     */
    public String getBeginOrt() {
        return _beginOrt;
    }

    /**
     * @return Returns the endeOrt.
     */
    public String getEndeOrt() {
        return _endeOrt;
    }

    /**
     * @return Returns the label.
     */
    public String getLabel() {
        return _label;
    }

    /**
     * @return Returns the typ.
     */
    public int getTyp() {
        return _typ;
    }
}