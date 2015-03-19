/*
 *  File: DemoTimeBarNode.java 
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
package de.jaret.examples.timebars.hierarchy.model;

import de.jaret.util.ui.timebars.model.DefaultTimeBarNode;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;

/**
 * Extension of the default timebar node fo demonstration purposes.
 * 
 * @author Peter Kliem
 * @version $Id: DemoTimeBarNode.java 722 2008-03-18 19:34:03Z kliem $
 */
public class DemoTimeBarNode extends DefaultTimeBarNode {
    private String _text;
    private boolean _type;

    public DemoTimeBarNode(TimeBarRowHeader header) {
        super(header);
    }

    /**
     * @return the text
     */
    public String getText() {
        return _text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        _text = text;
    }

    /**
     * @return the type
     */
    public boolean isType() {
        return _type;
    }

    /**
     * @param type the type to set
     */
    public void setType(boolean type) {
        _type = type;
    }


}
