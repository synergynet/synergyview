/*
 *  File: IdentificableIdService.java 
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

/**
 * Extenison of the tImeBarNode interface supplying an additional id.
 * 
 * @author kliem
 * @version $Id: IIdentifiableTimeBarNode.java 685 2007-12-26 15:30:21Z kliem $
 */
public interface IIdentifiableTimeBarNode extends TimeBarNode {

    /**
     * Supply an id.
     * 
     * @return id
     */
    String getId();
}
