/*
 *  File: PdiCalendar.java 
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
import java.util.Iterator;
import java.util.List;

import de.jaret.util.date.JaretDate;

/**
 * @author Peter Kliem
 * @version $Id: PdiCalendar.java 160 2007-01-02 22:02:40Z olk $
 */
public class PdiCalendar {
    protected List _tage = new ArrayList();

    public void addTag(PdiDay tag) {
        _tage.add(tag);
    }

    public JaretDate getMinDate() {
        return ((PdiDay) _tage.get(0)).getDate();
    }

    public JaretDate getMaxDate() {
        return ((PdiDay) _tage.get(_tage.size() - 1)).getDate();
    }

    public PdiDay getTag(JaretDate date) {
        Iterator it = _tage.iterator();
        while (it.hasNext()) {
            PdiDay tag = (PdiDay) it.next();
            if (tag.getDate().getDayOfYear() == date.getDayOfYear()) {
                return tag;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public List getTage() {
        return _tage;
    }

}
