/*
 *  File: ITimeBarViewState.java 
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
 * A standard viewstate for the timebar viewer. This mainly holds the row heights/column widths.
 * 
 * @author kliem
 * @version $Id: ITimeBarViewState.java 790 2008-12-10 23:14:13Z kliem $
 */
public interface ITimeBarViewState {
    /**
     * Set the default height for all rows. This height will also be used, if variable heights are not used.
     * 
     * @param height height
     */
    void setDefaultRowHeight(int height);

    /**
     * Retrieve the default row height.
     * 
     * @return the default row height
     */
    int getDefaultRowHeight();

    /**
     * Retrieve the height of the given row.
     * 
     * @param row row to receive the height for
     * @return the height of the row
     */
    int getRowHeight(TimeBarRow row);

    /**
     * Set the height for a specific row.
     * 
     * @param row row to set the height for
     * @param height the height of the row
     */
    void setRowHeight(TimeBarRow row, int height);

    /**
     * Check whether the intervals in a certain row should be drawn overlapping. If no value had been set, the global
     * value from the delegate is returned.
     * 
     * @param row row to look for
     * @return true if the intervals shoukd be drawn overlapping
     */
    boolean getDrawOverlapping(TimeBarRow row);

    /**
     * Set for a single row whether the intervals should be drawn overlapping (overwriting the global setting done in
     * the viewer/delegate).
     * 
     * @param row the row to set the property for
     * @param drawOverlapping <code>true</code> for overlapping drawing
     */
    void setDrawOverlapping(TimeBarRow row, boolean drawOverlapping);

    /**
     * Set whether to use variable row heights/widths.
     * 
     * @param useVariableRowHeights <code>true</code> to use variable row heights/widths. This will have an impact no
     * some performance aspects in the timebar viewer.
     */
    void setUseVariableRowHeights(boolean useVariableRowHeights);

    /**
     * Retrieve whether to use variable row heights/widths.
     * 
     * @return <code>true</code> if variable row heights/width should be used
     */
    boolean getUseVariableRowHeights();

    /**
     * Set a strategy for height calculation.
     * 
     * @param rowHeightStrategy the strategy for calculation or <code>null</code> to remove a strategy
     */
    void setRowHeightStrategy(IRowHeightStrategy rowHeightStrategy);

    /**
     * Retrieve the row height strategy if set.
     * 
     * @return the strategy or <code>null</code>
     */
    IRowHeightStrategy getRowHeightStrategy();

    /**
     * Add a listener to be informed about row height/width changes.
     * 
     * @param listener listener to be informed
     */
    void addTimeBarViewStateListener(ITimeBarViewStateListener listener);

    /**
     * Remove a viewstate listener.
     * 
     * @param listener listener to be removed
     */
    void removeTimeBarViewStateListener(ITimeBarViewStateListener listener);

}
