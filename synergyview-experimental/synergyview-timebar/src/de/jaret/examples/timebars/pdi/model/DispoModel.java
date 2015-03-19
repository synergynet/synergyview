/*
 *  File: DispoModel.java 
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

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.AbstractTimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;

/**
 * @author Peter Kliem
 * @version $Id: DispoModel.java 259 2007-02-16 13:54:00Z olk $
 */
public class DispoModel extends AbstractTimeBarModel {
    protected PdiCalendar _kalender;
    protected List _personenDispos = new ArrayList();

    public DispoModel(PdiCalendar kalender) {
        _kalender = kalender;
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarRow getRow(int row) {
        return (TimeBarRow) _personenDispos.get(row);
    }

    public void addPersonenDispo(PersonenDisposition pd) {
        _personenDispos.add(pd);
        pd.addTimeBarRowListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        return _personenDispos.size();
    }

    public JaretDate getMinDate() {
        return _kalender.getMinDate();
    }

    public JaretDate getMaxDate() {
        return _kalender.getMaxDate();
    }

}
