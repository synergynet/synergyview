/*
 *  File: Tour.java 
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

import java.util.ArrayList;
import java.util.List;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.AbstractTimeBarRowModel;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;

/**
 * @author Peter Kliem
 * @version $Id: Tour.java 234 2007-02-10 00:22:45Z olk $
 */
public class Tour extends AbstractTimeBarRowModel {

    protected ArrayList _elements = new ArrayList();

    String orte[] = { "LZ", "HQ", "LN", "CHI" };

    public Tour(int nr, JaretDate baseDate) {
        setRowHeader(new DefaultRowHeader(Integer.toString(nr)));

        JaretDate begin = baseDate.copy();
        begin.advanceMinutes(Math.random() * (7 * 60));
        _minDate = begin.copy();
        for (int i = 0; i < (int) (Math.random() * 45.0); i++) {
            int length = (int) (Math.random() * 240.0);
            JaretDate end = begin.copy();
            end.advanceMinutes(length);
            String label = Integer.toString((int) (Math.random() * 5000));
            String beginOrt = orte[(int) (Math.random() * orte.length)];
            String endeOrt = orte[(int) (Math.random() * orte.length)];
            int typ = (int) (Math.random() * 3);

            TourElement e = new TourElement(begin.copy(), end, beginOrt, endeOrt, typ, label);
            _elements.add(e);
            begin.advanceMinutes(length);
        }
        _maxDate = begin.copy();
    }

    public List getIntervals() {
        return _elements;
    }

}
