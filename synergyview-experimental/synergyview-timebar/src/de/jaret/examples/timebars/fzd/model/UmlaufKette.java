/*
 *  File: UmlaufKette.java 
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

import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;

/**
 * @author Peter Kliem
 * @version $Id: UmlaufKette.java 160 2007-01-02 22:02:40Z olk $
 */
public class UmlaufKette extends DefaultTimeBarRowModel {
    protected List _umlaeufe = new ArrayList();
    protected String _bezeichnung;

    public UmlaufKette(String bez) {
        super(new DefaultRowHeader(bez));
        _bezeichnung = bez;
    }

    public String getBezeichnung() {
        return _bezeichnung;
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
     * @return
     */
    public boolean isCompletelyAssigned() {
        Iterator iter = _umlaeufe.iterator();
        while (iter.hasNext()) {
            Umlauf umlauf = (Umlauf) iter.next();
            if (umlauf.getFahrzeug() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param umlauf
     * @return
     */
    public boolean contains(Umlauf umlauf) {
        return _umlaeufe.contains(umlauf);
    }

}
