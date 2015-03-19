/*
 *  File: DefaultRowHeader.java 
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

import de.jaret.util.misc.PropertyObservableBase;

/**
 * A very simple object with a bound property that can be used as a RowHeader in the TimeBarRow.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultRowHeader.java 800 2008-12-27 22:27:33Z kliem $
 */
public class DefaultRowHeader extends PropertyObservableBase implements TimeBarRowHeader {
    /** the label. */
    protected String _label;

    /** the row of the header. */
    protected TimeBarRow _row;

    /**
     * Constructor supplying a label and the row. This should most probably be used.
     * 
     * @param label the label
     * @param row the row
     */
    public DefaultRowHeader(String label, TimeBarRow row) {
        _label = label;
        _row = row;
    }

    /**
     * Constructor supplying a label.
     * 
     * @param label label to use
     */
    public DefaultRowHeader(String label) {
        this(label, null);
    }

    /**
     * Set the header label.
     * 
     * @param label new header label
     */
    public void setLabel(String label) {
        String oldVal = _label;
        _label = label;
        firePropertyChange("Label", oldVal, label);
    }

    /**
     * Straight forward toString method.
     * 
     * @return the label
     */
    public String toString() {
        return _label;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return _label;
    }

    /**
     * {@inheritDoc}
     */
    public TimeBarRow getRow() {
        return _row;
    }
}
