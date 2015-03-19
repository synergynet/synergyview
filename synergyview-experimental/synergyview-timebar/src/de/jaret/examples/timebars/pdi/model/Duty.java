/*
 *  File: Duty.java 
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

import de.jaret.util.date.JaretDate;

/**
 * @author Peter Kliem
 * @version $Id: Duty.java 160 2007-01-02 22:02:40Z olk $
 */
public class Duty extends Taetigkeit {
    protected String _dienstNr;
    protected PdiDay _tag;
    protected Person _assignedTo;

    /**
     * @param dienstNr
     * @param tag
     */
    public Duty(String dienstNr, PdiDay tag, JaretDate begin, JaretDate end) {
        _dienstNr = dienstNr;
        _tag = tag;
        _begin = begin;
        _end = end;
    }

    /**
     * @return Returns the dienstNr.
     */
    public String getDienstNr() {
        return _dienstNr;
    }

    /**
     * @param dienstNr The dienstNr to set.
     */
    public void setDienstNr(String dienstNr) {
        _dienstNr = dienstNr;
    }

    /**
     * @return Returns the tag.
     */
    public PdiDay getTag() {
        return _tag;
    }

    /**
     * @param tag The tag to set.
     */
    public void setTag(PdiDay tag) {
        _tag = tag;
    }

    /**
     * @return Returns the assignedTo.
     */
    public Person getAssignedTo() {
        return _assignedTo;
    }

    /**
     * @param assignedTo The assignedTo to set.
     */
    public void setAssignedTo(Person assignedTo) {
        _assignedTo = assignedTo;
    }
}
