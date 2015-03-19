/*
 *  File: Assignment.java 
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

import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;

/**
 * @author Peter Kliem
 * @version $Id: Assignment.java 259 2007-02-16 13:54:00Z olk $
 */
public class Assignment extends IntervalImpl {
    protected String _type;
    protected String _reason;
    protected PdiDay _tag;
    protected int _posAmTag;
    protected Taetigkeit _taetigkeit;
    protected PersonenDisposition _personenDispo;

    public Assignment(PdiDay tag, Taetigkeit taetigkeit, PersonenDisposition pd) {
        _tag = tag;
        _taetigkeit = taetigkeit;
        _personenDispo = pd;
    }

    /**
     * @return Returns the posAmTag.
     */
    public int getPosAmTag() {
        return _posAmTag;
    }

    /**
     * @param posAmTag The posAmTag to set.
     */
    public void setPosAmTag(int posAmTag) {
        _posAmTag = posAmTag;
    }

    /**
     * {@inheritDoc}
     */
    public JaretDate getBegin() {
        return _taetigkeit.getBegin();
    }

    public JaretDate getEnd() {
        return _taetigkeit.getEnd();
    }

    /**
     * @return Returns the taetigkeit.
     */
    public Taetigkeit getTaetigkeit() {
        return _taetigkeit;
    }

    /**
     * @return Returns the tag.
     */
    public PdiDay getTag() {
        return _tag;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ((Duty) _taetigkeit).getDienstNr();
    }

    /**
     * @return Returns the personenDispo.
     */
    public PersonenDisposition getPersonenDispo() {
        return _personenDispo;
    }
}
