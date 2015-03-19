/*
 *  File: TimeBarSelectionModel.java 
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

import java.util.List;

import de.jaret.util.date.Interval;

/**
 * Selection model for the TimeBarModel.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarSelectionModel.java 800 2008-12-27 22:27:33Z kliem $
 */
public interface TimeBarSelectionModel {
    /**
     * Check whether the selection is completely empty.
     * 
     * @return true id the selection is empty
     */
    boolean isEmpty();

    /**
     * Check whether there are selected rows.
     * 
     * @return true if the selection contains rows
     */
    boolean hasRowSelection();

    /**
     * Check whether there are selected intervals.
     * 
     * @return true if the selection contains intervals
     */
    boolean hasIntervalSelection();

    /**
     * Check whether there are selected relations.
     * 
     * @return true if the selection contains relations
     */
    boolean hasRelationSelection();

    /**
     * Retrieves the list of currently selected rows.
     * 
     * @return List containing the selected <code>TimeBarRows</code>
     */
    List<TimeBarRow> getSelectedRows();

    /**
     * Retrieves the list of currently selected intervals.
     * 
     * @return List containing the selected <code>Intervals</code>
     */
    List<Interval> getSelectedIntervals();

    /**
     * Retrieves the list of currently selected relations.
     * 
     * @return List containing the selected <code>IIntervalRelations</code>
     */
    List<IIntervalRelation> getSelectedRelations();

    /**
     * Enable/Disable row selection.
     * 
     * @param allowed row selection allowed when set to true
     */
    void setRowSelectionAllowed(boolean allowed);

    /**
     * Check allowance of row selections.
     * 
     * @return <code>true</code> if row selections are allowed
     */
    boolean isRowSelectionAllowed();

    /**
     * Enable/dible row selection toggle mode (click toggles selection without modifier keys).
     * 
     * @param activated true for activated toggle mode
     */
    void setRowSelectionToggleMode(boolean activated);

    /**
     * Retrieve the row selection toggle mode.
     * 
     * @return <code>true</code> for activated toggle mode
     */
    boolean getRowSelectionToggleMode();

    /**
     * Enable/disable interval selection.
     * 
     * @param allowed interval selection allowed when set to true
     */
    void setIntervalSelectionAllowed(boolean allowed);

    /**
     * Check allowance of interval selections.
     * 
     * @return <code>true</code> if interval selections are allowed
     */
    boolean isIntervalSelectionAllowed();

    /**
     * Enable/Disable relation selection.
     * 
     * @param allowed relation selection allowed when set to true
     */
    void setRelationSelectionAllowed(boolean allowed);

    /**
     * Check allowance of relation selections.
     * 
     * @return <code>true</code> if relation selections are allowed
     */
    boolean isRelationSelectionAllowed();

    /**
     * If multiple selction is not allowed only one row and one interval may be selected at one time.
     * 
     * @param allowed true to signal multiple selections are allowed
     */
    void setMultipleSelectionAllowed(boolean allowed);

    /**
     * Retrieves the multiple selection allowance.
     * 
     * @return true if multiple selections are allowed
     */
    boolean getMultipleSelectionAllowed();

    /**
     * Clears all selections.
     * 
     */
    void clearSelection();

    /**
     * Clears the selected intervals.
     * 
     */
    void clearIntervalSelection();

    /**
     * Clears the selected rows.
     * 
     */
    void clearRowSelection();

    /**
     * Clears the selected relations.
     * 
     */
    void clearRelationSelection();

    /**
     * Set the row selection to a given row.
     * 
     * @param row row to be selected
     */
    void setSelectedRow(TimeBarRow row);

    /**
     * Add a row to the collection of selected rows.
     * 
     * @param row row to be selected
     */
    void addSelectedRow(TimeBarRow row);

    /**
     * Remove a row from the collection of selected rows.
     * 
     * @param row row to be removed from the selection
     */
    void remSelectedRow(TimeBarRow row);

    /**
     * Check whether a given row is selected.
     * 
     * @param row row to check
     * @return true if the given row is selected
     */
    boolean isSelected(TimeBarRow row);

    /**
     * Set the interval as the oly selected interval.
     * 
     * @param interval interval that will be the only selected interval
     */
    void setSelectedInterval(Interval interval);

    /**
     * Add an interval to the selection.
     * 
     * @param interval interval to be added to the selection
     */
    void addSelectedInterval(Interval interval);

    /**
     * Remove an interval from the selection.
     * 
     * @param interval interval to remove from the selection
     */
    void remSelectedInterval(Interval interval);

    /**
     * Remove a list of intervals from the seletion.
     * 
     * @param intervals list of intervals to remove from the selection
     */
    void remSelectedIntervals(List<Interval> intervals);

    /**
     * Check whether an interval is in the selection.
     * 
     * @param interval interval to check
     * @return true if the interval in qustion is in the selection
     */
    boolean isSelected(Interval interval);

    /**
     * Set the relation as the oly selected relation.
     * 
     * @param relation relation that will be the only selected relation
     */
    void setSelectedRelation(IIntervalRelation relation);

    /**
     * Add an relation to the selection.
     * 
     * @param relation relation to be added to the selection
     */
    void addSelectedRelation(IIntervalRelation relation);

    /**
     * Remove an relation from the selection.
     * 
     * @param relation relation to remove from the selection
     */
    void remSelectedRelation(IIntervalRelation relation);

    /**
     * Remove a list of relations from the seletion.
     * 
     * @param relations list of relations to remove from the selection
     */
    void remSelectedRelations(List<IIntervalRelation> relations);

    /**
     * Check whether an inetrval is in the selection.
     * 
     * @param relation relation to check
     * @return true if the relation in question is in the selection
     */
    boolean isSelected(IIntervalRelation relation);

    /**
     * Add a <code>TimeBarSelectionListener</code> to be informed if the selection is altered.
     * 
     * @param tbsl the Listener to be added
     */
    void addTimeBarSelectionListener(TimeBarSelectionListener tbsl);

    /**
     * Remove a registered Listener.
     * 
     * @param tbsl the listener to be deregistered
     */
    void remTimeBarSelectionListener(TimeBarSelectionListener tbsl);
}
