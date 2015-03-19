/*
 *  File: TimeScaleDragSupport.java 
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
package de.jaret.util.ui.timebars.swt.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * Little utility allowing dragging the time scale and optionaly the diagram area in addition to using the scroll bar.
 * 
 * @author kliem
 * @version $Id: TimeScaleDragSupport.java 622 2007-11-01 15:23:19Z kliem $
 */
public class TimeScaleDragSupport implements MouseMoveListener {
    /** last registered x position. */
    private int _lastX = -1;
    /** last registered y position. */
    private int _lastY = -1;
    /** viewer tha has been hooked. */
    private TimeBarViewer _tbv;

    /** true if dragging on the diagram is monitored as well. */
    private boolean _includeDiagram = false;

    /**
     * Construct the support for a timebar viewer.
     * 
     * @param tbv timebar viewer
     * @param includeDiagram <code>true</code> causes dragging on the diagram itself to be translated into scrolling
     */
    public TimeScaleDragSupport(TimeBarViewer tbv, boolean includeDiagram) {
        _tbv = tbv;
        _includeDiagram = includeDiagram;
        _tbv.addMouseMoveListener(this);
    }

    /**
     * Construct the support for a timebar viewer. Timescale monitoring only.
     * 
     * @param tbv timebar viewer
     */
    public TimeScaleDragSupport(TimeBarViewer tbv) {
        this(tbv, false);
    }

    /**
     * {@inheritDoc}
     */
    public void mouseMove(MouseEvent me) {
        TimeBarViewerDelegate delegate = (TimeBarViewerDelegate) _tbv.getData("delegate");
        if ((me.stateMask & SWT.BUTTON1) != 0
                && (delegate.getXAxisRect().contains(me.x, me.y) || (_includeDiagram && delegate.getDiagramRect()
                        .contains(me.x, me.y)))) {
            if (_lastX != -1) {
                int seconds = _tbv.dateForXY(me.x, me.y).diffSeconds(_tbv.dateForXY(_lastX, _lastY));
                _tbv.setStartDate(_tbv.getStartDate().copy().advanceSeconds(-seconds));
            }
            _lastX = me.x;
            _lastY = me.y;
        } else {
            _lastX = -1;
        }
    }
}
