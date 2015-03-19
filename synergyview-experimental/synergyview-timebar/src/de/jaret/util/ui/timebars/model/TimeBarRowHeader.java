/*
 *  File: TimeBarRowHeader.java 
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
package de.jaret.util.ui.timebars.model;

import de.jaret.util.misc.PropertyObservable;

/**
 * Interface describing a row header.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarRowHeader.java 160 2007-01-02 22:02:40Z olk $
 */
public interface TimeBarRowHeader extends PropertyObservable {
    /**
     * Retrieve the label to use for the header.
     * 
     * @return label of the row
     */
    String getLabel();

    /**
     * retrieve the row the header belongs to.
     * 
     * @return the row the header belongs to
     */
    TimeBarRow getRow();
}
