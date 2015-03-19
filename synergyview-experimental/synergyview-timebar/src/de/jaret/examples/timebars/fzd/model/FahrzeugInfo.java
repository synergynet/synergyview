/*
 *  File: FahrzeugInfo.java 
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

import de.jaret.util.ui.timebars.model.DefaultRowHeader;

public class FahrzeugInfo extends DefaultRowHeader {
    int _kilometer;

    public FahrzeugInfo(String label) {
        super(label);
    }

    /**
     * @return Returns the kilometer.
     */
    public int getKilometer() {
        return _kilometer;
    }

    /**
     * @param kilometer The kilometer to set.
     */
    public void setKilometer(int kilometer) {
        int oldVal = _kilometer;
        _kilometer = kilometer;
        firePropertyChange("Kilometer", new Integer(oldVal), new Integer(kilometer));
    }
}