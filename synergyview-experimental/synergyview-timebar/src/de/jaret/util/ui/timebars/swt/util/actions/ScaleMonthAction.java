/*
 *  File: ScaleMonthAction.java 
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
package de.jaret.util.ui.timebars.swt.util.actions;

import org.eclipse.jface.action.Action;

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * Scale the time scale to approx one month.
 * 
 * @author Peter Kliem
 * @version $Id: ScaleMonthAction.java 160 2007-01-02 22:02:40Z olk $
 */
public class ScaleMonthAction extends Action {
    /** the timebarviewer th action has been constructed for. */
    private final TimeBarViewer _viewer;
    /** the delegate of the timebarviewer. */
    private final TimeBarViewerDelegate _delegate;

    /**
     * Constructor.
     * 
     * @param viewer timebar viewer
     * @param delegate delegate of the viewer.
     */
    public ScaleMonthAction(TimeBarViewer viewer, TimeBarViewerDelegate delegate) {
        _viewer = viewer;
        _delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        int width = _delegate.getDiagramRect().width;
        double pps = (double) width / (30.0 * 24.0 * 60.0 * 60.0);
        _viewer.setPixelPerSecond(pps);
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return "Scale to month";
    }
}
