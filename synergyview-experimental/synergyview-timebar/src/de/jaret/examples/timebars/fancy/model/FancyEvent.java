/*
 *  File: FancyEvent.java 
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
package de.jaret.examples.timebars.fancy.model;

import de.jaret.util.date.Event;
import de.jaret.util.date.JaretDate;

/**
 * An extension of the general event holding a label.
 * 
 * @author kliem
 * @version $Id: FancyEvent.java 869 2009-07-07 19:32:45Z kliem $
 */
public class FancyEvent extends Event {
    String _label;

    
    public FancyEvent(JaretDate date) {
        super(date);
    }

    
    public String getLabel() {
        return _label;
    }

    public void setLabel(String label) {
        String oldVal = _label;
        _label = label;
        if (isRealModification(oldVal, label)) {
        	firePropertyChange("Label", null, label);
        }
    }
}
