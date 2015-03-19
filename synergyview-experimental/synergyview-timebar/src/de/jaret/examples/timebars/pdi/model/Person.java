/*
 *  File: Person.java 
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

import de.jaret.util.misc.PropertyObservableBase;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;

/**
 * @author Peter Kliem
 * @version $Id: Person.java 160 2007-01-02 22:02:40Z olk $
 */
public class Person extends PropertyObservableBase implements TimeBarRowHeader {
    protected String _name;
    protected String _betriebshof;

    /**
     * @param name
     * @param betriebshof
     */
    public Person(String name, String betriebshof) {
        _name = name;
        _betriebshof = betriebshof;
    }

    /**
     * @return Returns the betriebshof.
     */
    public String getBetriebshof() {
        return _betriebshof;
    }

    /**
     * @param betriebshof The betriebshof to set.
     */
    public void setBetriebshof(String betriebshof) {
        _betriebshof = betriebshof;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return _name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        _name = name;
    }

    public String getLabel() {
        // TODO Auto-generated method stub
        return null;
    }

    public TimeBarRow getRow() {
        // TODO Auto-generated method stub
        return null;
    }
}
