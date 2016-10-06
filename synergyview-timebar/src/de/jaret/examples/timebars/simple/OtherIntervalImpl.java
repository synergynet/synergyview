/*
 *  File: SwtOverlapExample.java 
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
package de.jaret.examples.timebars.simple;

import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;

/**
 * Just an extension of intervalimpl for differentiation.
 * 
 * @author kliem
 * @version $Id: OtherIntervalImpl.java 810 2009-01-08 22:28:57Z kliem $
 */
public class OtherIntervalImpl extends IntervalImpl {
 String _label;
    public OtherIntervalImpl() {
        super();
    }
    public OtherIntervalImpl(JaretDate begin, JaretDate end) {
        super(begin, end);
    }

    public void setLabel(String label) {
        _label = label;
    }
    
    @Override
        public String toString() {
            return _label!=null?_label:super.toString();
        }
}
