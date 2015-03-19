/*
 *  File: DutyMass.java 
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;

/**
 * @author Peter Kliem
 * @version $Id: DutyMass.java 160 2007-01-02 22:02:40Z olk $
 */
public class DutyMass {
    protected Map _diensteProTag = new HashMap();
    TimeBarModel _model = new DefaultTimeBarModel();

    public void addDienst(PdiDay t, Duty d) {
        List dienste = getDienste(t);
        if (dienste == null) {
            dienste = new ArrayList();
            _diensteProTag.put(t, dienste);
        }
        dienste.add(d);
        addDienstToTimeBarModel(_model, d);
    }

    public List getDienste(PdiDay t) {
        return (List) _diensteProTag.get(t);
    }

    public TimeBarModel getTimeBarModel(PdiCalendar kalender) {
        return _model;

    }

    private void addDienstToTimeBarModel(TimeBarModel model, Duty d) {
        for (int i = 0; i < model.getRowCount(); i++) {
            DefaultTimeBarRowModel row = (DefaultTimeBarRowModel) model.getRow(i);
            if (row.getIntervals(d.getBegin(), d.getEnd()).size() == 0) {
                row.addInterval(d);
                return;
            }
        }
        DefaultTimeBarRowModel row = new DefaultTimeBarRowModel(new DefaultRowHeader(""));
        row.addInterval(d);
        ((DefaultTimeBarModel) model).addRow(row);
    }
}
