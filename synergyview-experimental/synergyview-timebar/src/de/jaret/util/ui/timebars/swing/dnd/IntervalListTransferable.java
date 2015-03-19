/*
 *  File: IntervalListTransferable.java 
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
package de.jaret.util.ui.timebars.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Iterator;
import java.util.List;

/**
 * Transferable for a list of intervals and
 * 
 * @author Peter Kliem
 * @version $Id: IntervalListTransferable.java 800 2008-12-27 22:27:33Z kliem $
 */
public class IntervalListTransferable implements Transferable {
    public static DataFlavor intervalListFlavor = new DataFlavor(List.class, "IntervalListFavor");

    List data;

    static DataFlavor supportedFlavors[] = {intervalListFlavor, DataFlavor.stringFlavor};

    public IntervalListTransferable(List rowIntervalTuples) {
        data = rowIntervalTuples;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        if (flavor.equals(DataFlavor.stringFlavor)) {
            return toString();
        }
        return data;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (int i = 0; i < supportedFlavors.length; i++) {
            if (supportedFlavors[i].equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        Iterator it = data.iterator();
        while (it.hasNext()) {
            RowIntervalTuple tuple = (RowIntervalTuple) it.next();
            buf.append(tuple.toString() + " ");
        }
        return buf.toString();
    }
}
