/*
 *  File: FzdOperations.java 
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
package de.jaret.examples.timebars.fzd.ctrl;

import java.util.Iterator;

import de.jaret.examples.timebars.fzd.model.Fahrzeug;
import de.jaret.examples.timebars.fzd.model.Umlauf;
import de.jaret.examples.timebars.fzd.model.UmlaufKette;
import de.jaret.examples.timebars.fzd.model.UmlaufKettenModel;

/**
 * @author Peter Kliem
 * @version $Id: FzdOperations.java 160 2007-01-02 22:02:40Z olk $
 */
public class FzdOperations {
    public static void assign(UmlaufKettenModel kettenModel, UmlaufKette kette, Umlauf umlauf, Fahrzeug fahrzeug) {
        // kette zerfällt in zwei
        UmlaufKette restKette = new UmlaufKette(kette.getBezeichnung() + "Rest");
        // Sortierung vorausgesetzt
        // erst alle rauskopieren (getrennt wg. ConcurrentUpdate)
        Iterator it = kette.getUmlaeufe().iterator();
        while (it.hasNext()) {
            Umlauf u = (Umlauf) it.next();
            if (u.equals(umlauf)) {
                restKette.addUmlauf(u);
                while (it.hasNext()) {
                    Umlauf uml = (Umlauf) it.next();
                    restKette.addUmlauf(uml);
                }
            }
        }
        // jetzt aus der alten entfernen
        it = restKette.getUmlaeufe().iterator();
        while (it.hasNext()) {
            Umlauf u = (Umlauf) it.next();
            kette.remUmlauf(u);
        }
        // kette ins model (Masse)
        kettenModel.addUmlaufKette(restKette);

        // dem fahrzeug zuweisen
        fahrzeug.addUmlaufKette(restKette);
    }
}
