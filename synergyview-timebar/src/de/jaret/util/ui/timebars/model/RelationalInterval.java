/*
 *  File: RelationalInterval.java 
 *  Copyright (c) 2004-2008  Peter Kliem (Peter.Kliem@jaret.de)
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
package de.jaret.util.ui.timebars.model;

import java.util.ArrayList;
import java.util.List;

import de.jaret.util.date.IntervalImpl;

/**
 * Simple implementation of the relational interval based on the standard interval. Handles the relation when a relation
 * is removed (setting the appropriate field in the relation to <code>null</code>. Does handle
 * 
 * @author kliem
 * @version $Id: RelationalInterval.java 800 2008-12-27 22:27:33Z kliem $
 */
public class RelationalInterval extends IntervalImpl implements IRelationalInterval {
    /** list of relations. */
    protected List<IIntervalRelation> _relations = new ArrayList<IIntervalRelation>();

    /**
     * {@inheritDoc}
     */
    public void addRelation(IIntervalRelation relation) {
        if (!_relations.contains(relation)) {
            _relations.add(relation);
            firePropertyChange(RELATIONS, null, relation);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<IIntervalRelation> getRelations() {
        return _relations;
    }

    /**
     * {@inheritDoc}. Removes the reference from the relation.
     */
    public void removeRelation(IIntervalRelation relation) {
        if (_relations.contains(relation)) {
            _relations.remove(relation);
            if (relation.getStartInterval() == this) {
                relation.setStartInterval(null);
            }
            if (relation.getEndInterval() == this) {
                relation.setEndInterval(null);
            }
            firePropertyChange(RELATIONS, relation, null);
        }
    }

}
