/*
 *  File: IIntervalRelation.java 
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

/**
 * Relation between intervals.
 * 
 * @author kliem
 * @version $Id: IIntervalRelation.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface IIntervalRelation {
    /**
     * Direction of the relation.
     */
    enum Direction {
        /**
         * Bidirectional.
         */
        BI,
        /**
         * No specified direction.
         */
        NONE,
        /**
         * Forward.
         */
        FORWARD,
        /**
         * Backwards.
         */
        BACK
    };

    /**
     * Relation type denoting if the relation should refer to the end or begin of the interval.
     */
    enum Type {
        /**
         * Begin of first interval to end of second interval.
         */
        BEGIN_END,
        /**
         * Begin of first interval to begin of second interval.
         */
        BEGIN_BEGIN,
        /**
         * End of first interval to begin of second interval.
         */
        END_BEGIN,
        /**
         * End of first interval to end of second interval.
         */
        END_END
    };

    /** propertyname constant for the direction property. */
    String DIRECTION = "Direction";
    /** propertyname constant for the type property. */
    String TYPE = "Type";
    /** propertyname constant for the start interval property. */
    String STARTINTERVAL = "StartInterval";
    /** propertyname constant for the end interval property. */
    String ENDINTERVAL = "EndInterval";

    /**
     * Retrieve the start interval.
     * 
     * @return the start interval
     */
    IRelationalInterval getStartInterval();

    /**
     * Set the start interval.
     * 
     * @param interval the start interval
     */
    void setStartInterval(IRelationalInterval interval);

    /**
     * Retrieve the end interval.
     * 
     * @return the end interval
     */
    IRelationalInterval getEndInterval();

    /**
     * Set the end interval.
     * 
     * @param interval the end interval
     */
    void setEndInterval(IRelationalInterval interval);

    /**
     * Retrieve the direction of the relation.
     * 
     * @return the direction
     */
    Direction getDirection();

    /**
     * Set the direction of the realtion.
     * 
     * @param direction direction
     */
    void setDirection(Direction direction);

    /**
     * Retrieve the type of the relation.
     * 
     * @return the type
     */
    Type getType();

    /**
     * Set the type of the relation.
     * 
     * @param type the type to use
     */
    void setType(Type type);

}
