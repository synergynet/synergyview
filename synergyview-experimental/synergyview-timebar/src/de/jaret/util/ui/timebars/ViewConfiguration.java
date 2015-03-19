/*
 *  File: ViewConfiguration.java 
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

import de.jaret.util.date.JaretDate;

/**
 * Configuration information for printing the model of a timebarviewer.
 * 
 * @author Peter Kliem
 * @version $Id: ViewConfiguration.java 800 2008-12-27 22:27:33Z kliem $
 */
public class ViewConfiguration {
    /** seconds rendered per page. */
    private int _secondsPerPage;
    /** start date for rendering. */
    private JaretDate _startDate;
    /** end date for rendering. */
    private JaretDate _endDate;
    /** fooer text. */
    private String _footLine;
    /** name of the print. */
    private String _name;
    /** true for y axis drawn on each page. */
    private boolean _repeatYAxis = false;
    /** true for x axis dran on every page. */
    private boolean _repeatScale = false;

    /**
     * Retrieve the name of the viewconfiguraion. The name will be used as the name of the print job (if set).
     * 
     * @return name of the vc
     */
    public String getName() {
        return _name;
    }

    /**
     * Set the name of the view configuration. The name will be used as the name of the print job (if set).
     * 
     * @param name The name to set.
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Retrieve the number of seconds that shoul dbe printed on the first page.
     * 
     * @return number of seconds to be printed on the first page.
     */
    public int getSecondsPerPage() {
        return _secondsPerPage;
    }

    /**
     * The seconds per page determine the scale of the print. The value denotes the number of seconds on the first page
     * (the page that includes the yaxis). On subsequent pages without y axis (if repeatYAxis is set to false) there
     * will be more seconds on a page.
     * 
     * @param secondsPerPage seconds to print on the first page
     */
    public void setSecondsPerPage(int secondsPerPage) {
        _secondsPerPage = secondsPerPage;
    }

    /**
     * @return Returns the startDate.
     */
    public JaretDate getStartDate() {
        return _startDate;
    }

    /**
     * Set the start date of the region to print.
     * 
     * @param startDate The startDate to set.
     */
    public void setStartDate(JaretDate startDate) {
        _startDate = startDate;
    }

    /**
     * Retrieve the end date for parinting.
     * 
     * @return Returns the endDate or <code>null</code> for printing to the end of available data
     */
    public JaretDate getEndDate() {
        return _endDate;
    }

    /**
     * Set the end date of the region to print.
     * 
     * @param endDate The endDate to set or <code>null</code> for printing to the end of available data
     */
    public void setEndDate(JaretDate endDate) {
        _endDate = endDate;
    }

    /**
     * @return Returns the footLine.
     */
    public String getFootLine() {
        return _footLine;
    }

    /**
     * @param footLine The footLine to set.
     */
    public void setFootLine(String footLine) {
        _footLine = footLine;
    }

    /**
     * @return Returns the repeatScale.
     */
    public boolean getRepeatScale() {
        return _repeatScale;
    }

    /**
     * @param repeatScale The repeatScale to set.
     */
    public void setRepeatScale(boolean repeatScale) {
        _repeatScale = repeatScale;
    }

    /**
     * @return Returns the repeatYAxis.
     */
    public boolean getRepeatYAxis() {
        return _repeatYAxis;
    }

    /**
     * @param repeatYAxis The repeatYAxis to set.
     */
    public void setRepeatYAxis(boolean repeatYAxis) {
        _repeatYAxis = repeatYAxis;
    }

}
