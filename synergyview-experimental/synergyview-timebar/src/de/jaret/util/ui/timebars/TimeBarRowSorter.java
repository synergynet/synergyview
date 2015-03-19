/*
 *  File: TimeBarRowSorter.java 
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
package de.jaret.util.ui.timebars;

import java.io.Serializable;
import java.util.Comparator;

import de.jaret.util.misc.PropertyObservable;
import de.jaret.util.ui.timebars.model.TimeBarRow;

/**
 * Comparator for comparing rows. The interface acts as a tag interface, ensuring the comparator used for sorting the
 * rows was specially designed for this purpose. It is an extension of PropertyObservable allowing the time bar viewer
 * to react with a refresh on property changes.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarRowSorter.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeBarRowSorter extends Comparator<TimeBarRow>, PropertyObservable, Serializable {

}
