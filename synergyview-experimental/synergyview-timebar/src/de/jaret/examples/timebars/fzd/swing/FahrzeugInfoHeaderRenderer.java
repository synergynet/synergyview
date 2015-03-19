/*
 *  File: FahrzeugInfoHeaderRenderer.java 
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
package de.jaret.examples.timebars.fzd.swing;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import de.jaret.examples.timebars.fzd.model.FahrzeugInfo;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.HeaderRenderer;

/**
 * @author Peter Kliem
 * @version $Id: FahrzeugInfoHeaderRenderer.java 228 2007-02-09 23:23:29Z olk $
 */
public class FahrzeugInfoHeaderRenderer implements HeaderRenderer {
    JTable _component = new JTable();
    FzgInfoTableModel _model = new FzgInfoTableModel();

    public FahrzeugInfoHeaderRenderer() {
    }

    public JComponent getHeaderRendererComponent(TimeBarViewer tbv, TimeBarRowHeader value, boolean isSelected) {
        _model.setFzgInfo((FahrzeugInfo) value);
        _component.setModel(_model);
        _component.setToolTipText("FahrzeugHeaderToolTip");
        if (isSelected) {
            _component.setBackground(Color.BLUE);
        } else {
            _component.setBackground(Color.WHITE);
        }
        return _component;
    }

    public int getWidth() {
        return 150;
    }

    class FzgInfoTableModel extends AbstractTableModel {
        FahrzeugInfo _fzgInfo;

        public void setFzgInfo(FahrzeugInfo fzgInfo) {
            _fzgInfo = fzgInfo;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return 2;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            return 2;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
            case 0:
                switch (rowIndex) {
                case 0:
                    return "FzdNr";
                case 1:
                    return "km";
                default:
                    return "n.a.";
                }
            case 1:
                switch (rowIndex) {
                case 0:
                    return _fzgInfo.getLabel();
                case 1:
                    return new Integer(_fzgInfo.getKilometer());
                default:
                    return "n.a.";
                }

            default:
                return "???";
            }
        }

    }
}
